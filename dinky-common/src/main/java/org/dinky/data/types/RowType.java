/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.data.types;

import org.dinky.assertion.Asserts;
import org.dinky.data.exception.ValidationException;
import org.dinky.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class RowType extends LogicalType {
    private static final long serialVersionUID = 1L;

    public static final String FORMAT = "ROW<%s>";

    public static final class RowField implements Serializable {
        private static final long serialVersionUID = 1L;

        public static final String FIELD_FORMAT_WITH_DESCRIPTION = "%s %s '%s'";

        public static final String FIELD_FORMAT_NO_DESCRIPTION = "%s %s";

        private final String name;

        private final LogicalType type;

        private final @Nullable String description;

        public RowField(String name, LogicalType type, @Nullable String description) {
            this.name = Asserts.checkNotNull(name, "Field name must not be null.");
            this.type = Asserts.checkNotNull(type, "Field type must not be null.");
            this.description = description;
        }

        public RowField(String name, LogicalType type) {
            this(name, type, null);
        }

        public String getName() {
            return name;
        }

        public LogicalType getType() {
            return type;
        }

        public Optional<String> getDescription() {
            return Optional.ofNullable(description);
        }

        public RowField copy() {
            return new RowField(name, type.copy(), description);
        }

        public String asSummaryString() {
            return formatString(type.asSummaryString(), true);
        }

        public String asSerializableString() {
            return formatString(type.asSerializableString(), false);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RowField rowField = (RowField) o;
            return name.equals(rowField.name)
                    && type.equals(rowField.type)
                    && Objects.equals(description, rowField.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type, description);
        }

        private String formatString(String typeString, boolean excludeDescription) {
            if (description == null) {
                return String.format(FIELD_FORMAT_NO_DESCRIPTION, escapeIdentifier(name), typeString);
            } else if (excludeDescription) {
                return String.format(FIELD_FORMAT_WITH_DESCRIPTION, escapeIdentifier(name), typeString, "...");
            } else {
                return String.format(
                        FIELD_FORMAT_WITH_DESCRIPTION,
                        escapeIdentifier(name),
                        typeString,
                        escapeSingleQuotes(description));
            }
        }
    }

    private static String escapeIdentifier(String s) {
        return "`" + escapeBackticks(s) + "`";
    }

    private static String escapeBackticks(String s) {
        return s.replace("`", "``");
    }

    private static String escapeSingleQuotes(String s) {
        return s.replace("'", "''");
    }

    private final List<RowField> fields;

    public RowType(boolean isNullable, List<RowField> fields) {
        super(isNullable, LogicalTypeRoot.ROW);
        this.fields =
                Collections.unmodifiableList(new ArrayList<>(Asserts.checkNotNull(fields, "Fields must not be null.")));

        validateFields(fields);
    }

    public RowType(List<RowField> fields) {
        this(true, fields);
    }

    public List<RowField> getFields() {
        return fields;
    }

    public List<String> getFieldNames() {
        return fields.stream().map(RowField::getName).collect(Collectors.toList());
    }

    public LogicalType getTypeAt(int i) {
        return fields.get(i).getType();
    }

    public int getFieldCount() {
        return fields.size();
    }

    public int getFieldIndex(String fieldName) {
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getName().equals(fieldName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public LogicalType copy(boolean isNullable) {
        return new RowType(isNullable, fields.stream().map(RowField::copy).collect(Collectors.toList()));
    }

    @Override
    public LogicalType copy(LogicalTypeParam param) {
        if (param == null || param.getNullable() == null) {
            return copy();
        }
        return copy(param.getNullable());
    }

    @Override
    public String asSummaryString() {
        return withNullability(
                FORMAT, fields.stream().map(RowField::asSummaryString).collect(Collectors.joining(", ")));
    }

    @Override
    public String asSerializableString() {
        return withNullability(
                FORMAT, fields.stream().map(RowField::asSerializableString).collect(Collectors.joining(", ")));
    }

    @Override
    public List<LogicalType> getChildren() {
        return Collections.unmodifiableList(
                fields.stream().map(RowField::getType).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RowType rowType = (RowType) o;
        return fields.equals(rowType.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fields);
    }

    // --------------------------------------------------------------------------------------------

    private static void validateFields(List<RowField> fields) {
        final List<String> fieldNames = fields.stream().map(f -> f.name).collect(Collectors.toList());
        if (fieldNames.stream().anyMatch(StringUtil::isNullOrWhitespaceOnly)) {
            throw new ValidationException("Field names must contain at least one non-whitespace character.");
        }
        final Set<String> duplicates = fieldNames.stream()
                .filter(n -> Collections.frequency(fieldNames, n) > 1)
                .collect(Collectors.toSet());
        if (!duplicates.isEmpty()) {
            throw new ValidationException(
                    String.format("Field names must be unique. Found duplicates: %s", duplicates));
        }
    }

    public static RowType of(LogicalType... types) {
        return of(true, types);
    }

    public static RowType of(boolean isNullable, LogicalType... types) {
        final List<RowField> fields = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            fields.add(new RowField("f" + i, types[i]));
        }
        return new RowType(isNullable, fields);
    }

    public static RowType of(LogicalType[] types, String[] names) {
        return of(true, types, names);
    }

    public static RowType of(boolean nullable, LogicalType[] types, String[] names) {
        List<RowField> fields = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            fields.add(new RowField(names[i], types[i]));
        }
        return new RowType(nullable, fields);
    }
}
