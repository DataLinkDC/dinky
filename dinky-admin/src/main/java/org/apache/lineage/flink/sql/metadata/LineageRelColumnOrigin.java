/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lineage.flink.sql.metadata;

import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.plan.RelOptSchema;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelDistribution;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelReferentialConstraint;
import org.apache.calcite.rel.metadata.RelColumnOrigin;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.schema.ColumnStrategy;
import org.apache.calcite.util.ImmutableBitSet;

import java.util.Collections;
import java.util.List;

public class LineageRelColumnOrigin extends RelColumnOrigin {
    /**
     * Stores the expression for data conversion,
     * which source table fields are transformed by which expression the target field
     */
    private String transform;

    // ~ Constructors -----------------------------------------------------------

    public LineageRelColumnOrigin(
            RelOptTable originTable,
            int iOriginColumn,
            boolean isDerived) {
        super(originTable, iOriginColumn, isDerived);
    }

    public LineageRelColumnOrigin(
            RelOptTable originTable,
            int iOriginColumn,
            boolean isDerived,
            String transform) {
        super(originTable, iOriginColumn, isDerived);
        this.transform = transform;
    }

    public LineageRelColumnOrigin(RelColumnOrigin relColumnOrigin) {
        super(relColumnOrigin.getOriginTable(), relColumnOrigin.getOriginColumnOrdinal(), relColumnOrigin.isDerived());
    }

    public LineageRelColumnOrigin(
            RelColumnOrigin relColumnOrigin,
            String transform) {
        this(relColumnOrigin);
        this.transform = transform;
    }

    public String getTransform() {
        return transform;
    }

    public static String getTransform(RelColumnOrigin relColumnOrigin) {
        return relColumnOrigin instanceof LineageRelColumnOrigin ? ((LineageRelColumnOrigin) relColumnOrigin).getTransform() : null;
    }

    public static final NullRelOptTable NULL_REL_OPT_TABLE = new NullRelOptTable();
    public static class NullRelOptTable implements RelOptTable {
        @Override
        public List<String> getQualifiedName() {
            return Collections.emptyList();
        }

        @Override
        public double getRowCount() {
            return 0;
        }

        @Override
        public RelDataType getRowType() {
            return null;
        }

        @Override
        public RelOptSchema getRelOptSchema() {
            return null;
        }

        @Override
        public RelNode toRel(ToRelContext toRelContext) {
            return null;
        }

        @Override
        public List<RelCollation> getCollationList() {
            return null;
        }

        @Override
        public RelDistribution getDistribution() {
            return null;
        }

        @Override
        public boolean isKey(ImmutableBitSet immutableBitSet) {
            return false;
        }

        @Override
        public List<ImmutableBitSet> getKeys() {
            return null;
        }

        @Override
        public List<RelReferentialConstraint> getReferentialConstraints() {
            return null;
        }

        @Override
        public Expression getExpression(Class aClass) {
            return null;
        }

        @Override
        public RelOptTable extend(List<RelDataTypeField> list) {
            return null;
        }

        @Override
        public List<ColumnStrategy> getColumnStrategies() {
            return null;
        }

        @Override
        public <C> C unwrap(Class<C> aClass) {
            return null;
        }
    }
}
