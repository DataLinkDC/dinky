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

package org.apache.calcite.rel.metadata;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.SingleRel;
import org.apache.calcite.rel.core.Aggregate;
import org.apache.calcite.rel.core.AggregateCall;
import org.apache.calcite.rel.core.Calc;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Exchange;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.Match;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.SetOp;
import org.apache.calcite.rel.core.Snapshot;
import org.apache.calcite.rel.core.Sort;
import org.apache.calcite.rel.core.TableFunctionScan;
import org.apache.calcite.rel.core.TableModify;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLocalRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexPatternFieldRef;
import org.apache.calcite.rex.RexShuttle;
import org.apache.calcite.rex.RexVisitor;
import org.apache.calcite.rex.RexVisitorImpl;
import org.apache.calcite.util.BuiltInMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modified based on calcite's source code org.apache.calcite.rel.metadata.RelMdColumnOrigins
 *
 * <p>Modification point:
 * <ol>
 *  <li>Support lookup join, add method getColumnOrigins(Snapshot rel,RelMetadataQuery mq, int iOutputColumn)
 *  <li>Support watermark, add method getColumnOrigins(SingleRel rel,RelMetadataQuery mq, int iOutputColumn)
 *  <li>Support table function, add method getColumnOrigins(Correlate rel, RelMetadataQuery mq, int iOutputColumn)
 *  <li>Support CEP, add method getColumnOrigins(Match rel, RelMetadataQuery mq, int iOutputColumn)
 *  <li>Support transform, add method createDerivedColumnOrigins(Set<RelColumnOrigin> inputSet, String transform, boolean originTransform), and related code
 *  <li>Support field AS LOCALTIMESTAMP, modify method getColumnOrigins(Project rel, RelMetadataQuery mq, int iOutputColumn)
 *  <li>Support PROCTIME() is the first filed, add method computeIndexWithOffset, used by getColumnOrigins(Project rel, RelMetadataQuery mq, int iOutputColumn)
 * <ol/>
 *
 * @description: RelMdColumnOrigins supplies a default implementation of {@link RelMetadataQuery#getColumnOrigins} for the standard logical algebra.
 * @author: HamaWhite
 */
public class RelMdColumnOrigins implements MetadataHandler<BuiltInMetadata.ColumnOrigin> {

    private static final Logger LOG = LoggerFactory.getLogger(RelMdColumnOrigins.class);

    public static final String DELIMITER = ".";

    public static final RelMetadataProvider SOURCE = ReflectiveRelMetadataProvider.reflectiveSource(
            BuiltInMethod.COLUMN_ORIGIN.method, new RelMdColumnOrigins());

    // ~ Constructors -----------------------------------------------------------

    private RelMdColumnOrigins() {
    }

    // ~ Methods ----------------------------------------------------------------

    public MetadataDef<BuiltInMetadata.ColumnOrigin> getDef() {
        return BuiltInMetadata.ColumnOrigin.DEF;
    }

    public Set<RelColumnOrigin> getColumnOrigins(Aggregate rel,
            RelMetadataQuery mq, int iOutputColumn) {
        if (iOutputColumn < rel.getGroupCount()) {
            // get actual index of Group columns.
            return mq.getColumnOrigins(rel.getInput(), rel.getGroupSet().asList().get(iOutputColumn));
        }

        // Aggregate columns are derived from input columns
        AggregateCall call = rel.getAggCallList().get(iOutputColumn
                - rel.getGroupCount());

        final Set<RelColumnOrigin> set = new LinkedHashSet<>();
        for (Integer iInput : call.getArgList()) {
            Set<RelColumnOrigin> inputSet = mq.getColumnOrigins(rel.getInput(), iInput);
            inputSet = createDerivedColumnOrigins(inputSet, call.toString(), true);
            if (inputSet != null) {
                set.addAll(inputSet);
            }
        }
        return set;
    }

    public Set<RelColumnOrigin> getColumnOrigins(Join rel, RelMetadataQuery mq,
            int iOutputColumn) {
        int nLeftColumns = rel.getLeft().getRowType().getFieldList().size();
        Set<RelColumnOrigin> set;
        boolean derived = false;
        if (iOutputColumn < nLeftColumns) {
            set = mq.getColumnOrigins(rel.getLeft(), iOutputColumn);
            if (rel.getJoinType().generatesNullsOnLeft()) {
                derived = true;
            }
        } else {
            set = mq.getColumnOrigins(rel.getRight(), iOutputColumn - nLeftColumns);
            if (rel.getJoinType().generatesNullsOnRight()) {
                derived = true;
            }
        }
        if (derived) {
            // nulls are generated due to outer join; that counts
            // as derivation
            set = createDerivedColumnOrigins(set);
        }
        return set;
    }

    /**
     * Support the field blood relationship of table function
     */
    public Set<RelColumnOrigin> getColumnOrigins(Correlate rel, RelMetadataQuery mq, int iOutputColumn) {

        List<RelDataTypeField> leftFieldList = rel.getLeft().getRowType().getFieldList();

        int nLeftColumns = leftFieldList.size();
        Set<RelColumnOrigin> set;
        if (iOutputColumn < nLeftColumns) {
            set = mq.getColumnOrigins(rel.getLeft(), iOutputColumn);
        } else {
            if (rel.getRight() instanceof TableFunctionScan) {
                // get the field name of the left table configured in the Table Function on the right
                TableFunctionScan tableFunctionScan = (TableFunctionScan) rel.getRight();
                RexCall rexCall = (RexCall) tableFunctionScan.getCall();
                // support only one field in table function
                RexFieldAccess rexFieldAccess = (RexFieldAccess) rexCall.getOperands().get(0);
                String fieldName = rexFieldAccess.getField().getName();

                int leftFieldIndex = 0;
                for (int i = 0; i < nLeftColumns; i++) {
                    if (leftFieldList.get(i).getName().equalsIgnoreCase(fieldName)) {
                        leftFieldIndex = i;
                        break;
                    }
                }
                /**
                 * Get the fields from the left table, don't go to
                 * getColumnOrigins(TableFunctionScan rel,RelMetadataQuery mq, int iOutputColumn),
                 * otherwise the return is null, and the UDTF field origin cannot be parsed
                 */
                set = mq.getColumnOrigins(rel.getLeft(), leftFieldIndex);

                // process transform for udtf
                String transform = rexCall.toString().replace(rexFieldAccess.toString(), fieldName)
                        + DELIMITER
                        + tableFunctionScan.getRowType().getFieldNames().get(iOutputColumn - nLeftColumns);
                set = createDerivedColumnOrigins(set, transform, false);
            } else {
                set = mq.getColumnOrigins(rel.getRight(), iOutputColumn - nLeftColumns);
            }
        }
        return set;
    }

    public Set<RelColumnOrigin> getColumnOrigins(SetOp rel,
            RelMetadataQuery mq, int iOutputColumn) {
        final Set<RelColumnOrigin> set = new LinkedHashSet<>();
        for (RelNode input : rel.getInputs()) {
            Set<RelColumnOrigin> inputSet = mq.getColumnOrigins(input, iOutputColumn);
            if (inputSet == null) {
                return Collections.emptySet();
            }
            set.addAll(inputSet);
        }
        return set;
    }

    /**
     * Support the field blood relationship of lookup join
     */
    public Set<RelColumnOrigin> getColumnOrigins(Snapshot rel,
            RelMetadataQuery mq, int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    /**
     * Support the field blood relationship of watermark
     */
    public Set<RelColumnOrigin> getColumnOrigins(SingleRel rel,
            RelMetadataQuery mq, int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    /**
     * Support for new fields in the source table similar to those created with the LOCALTIMESTAMP function
     */
    public Set<RelColumnOrigin> getColumnOrigins(Project rel,
            final RelMetadataQuery mq, int iOutputColumn) {
        final RelNode input = rel.getInput();
        RexNode rexNode = rel.getProjects().get(iOutputColumn);

        if (rexNode instanceof RexInputRef) {
            // Direct reference: no derivation added.
            RexInputRef inputRef = (RexInputRef) rexNode;
            int index = inputRef.getIndex();
            if (input instanceof TableScan) {
                index = computeIndexWithOffset(rel.getProjects(), inputRef.getIndex(), iOutputColumn);
            }
            return mq.getColumnOrigins(input, index);
        } else if (input instanceof TableScan && rexNode.getClass().equals(RexCall.class)
                && ((RexCall) rexNode).getOperands().isEmpty()) {
            return mq.getColumnOrigins(input, iOutputColumn);
        }
        // Anything else is a derivation, possibly from multiple columns.
        final Set<RelColumnOrigin> set = getMultipleColumns(rexNode, input, mq);
        return createDerivedColumnOrigins(set, rexNode.toString(), true);
    }

    private int computeIndexWithOffset(List<RexNode> projects, int baseIndex, int iOutputColumn) {
        int offset = 0;
        for (int index = 0; index < iOutputColumn; index++) {
            RexNode rexNode = projects.get(index);
            if ((rexNode.getClass().equals(RexCall.class) && ((RexCall) rexNode).getOperands().isEmpty())) {
                offset += 1;
            }
        }
        return baseIndex + offset;
    }

    /**
     * Support field blood relationship of CEP.
     * The first column is the field after PARTITION BY, and the other columns come from the measures in Match
     */
    public Set<RelColumnOrigin> getColumnOrigins(Match rel, RelMetadataQuery mq, int iOutputColumn) {
        int orderCount = rel.getOrderKeys().getKeys().size();

        if (iOutputColumn < orderCount) {
            return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
        }
        final RelNode input = rel.getInput();
        RexNode rexNode = rel.getMeasures().values().asList().get(iOutputColumn - orderCount);

        RexPatternFieldRef rexPatternFieldRef = searchRexPatternFieldRef(rexNode);
        if (rexPatternFieldRef != null) {
            final Set<RelColumnOrigin> set = mq.getColumnOrigins(input, rexPatternFieldRef.getIndex());
            String originTransform = rexNode instanceof RexCall
                    ? ((RexCall) rexNode).getOperands().get(0).toString()
                    : null;
            return createDerivedColumnOrigins(set, originTransform, true);
        }
        return Collections.emptySet();
    }

    private RexPatternFieldRef searchRexPatternFieldRef(RexNode rexNode) {
        if (rexNode instanceof RexCall) {
            RexNode operand = ((RexCall) rexNode).getOperands().get(0);
            if (operand instanceof RexPatternFieldRef) {
                return (RexPatternFieldRef) operand;
            } else {
                // recursive search
                return searchRexPatternFieldRef(operand);
            }
        }
        return null;
    }

    public Set<RelColumnOrigin> getColumnOrigins(Calc rel,
            final RelMetadataQuery mq, int iOutputColumn) {
        final RelNode input = rel.getInput();
        final RexShuttle rexShuttle = new RexShuttle() {

            @Override
            public RexNode visitLocalRef(RexLocalRef localRef) {
                return rel.getProgram().expandLocalRef(localRef);
            }
        };
        final List<RexNode> projects = new ArrayList<>();
        for (RexNode rex : rexShuttle.apply(rel.getProgram().getProjectList())) {
            projects.add(rex);
        }
        final RexNode rexNode = projects.get(iOutputColumn);
        if (rexNode instanceof RexInputRef) {
            // Direct reference: no derivation added.
            RexInputRef inputRef = (RexInputRef) rexNode;
            return mq.getColumnOrigins(input, inputRef.getIndex());
        }
        // Anything else is a derivation, possibly from multiple columns.
        final Set<RelColumnOrigin> set = getMultipleColumns(rexNode, input, mq);
        return createDerivedColumnOrigins(set);
    }

    public Set<RelColumnOrigin> getColumnOrigins(Filter rel,
            RelMetadataQuery mq, int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    public Set<RelColumnOrigin> getColumnOrigins(Sort rel, RelMetadataQuery mq,
            int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    public Set<RelColumnOrigin> getColumnOrigins(TableModify rel, RelMetadataQuery mq,
            int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    public Set<RelColumnOrigin> getColumnOrigins(Exchange rel,
            RelMetadataQuery mq, int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    public Set<RelColumnOrigin> getColumnOrigins(TableFunctionScan rel,
            RelMetadataQuery mq, int iOutputColumn) {
        final Set<RelColumnOrigin> set = new LinkedHashSet<>();
        Set<RelColumnMapping> mappings = rel.getColumnMappings();
        if (mappings == null) {
            if (!rel.getInputs().isEmpty()) {
                // This is a non-leaf transformation: say we don't
                // know about origins, because there are probably
                // columns below.
                return Collections.emptySet();
            } else {
                // This is a leaf transformation: say there are fer sure no
                // column origins.
                return set;
            }
        }
        for (RelColumnMapping mapping : mappings) {
            if (mapping.iOutputColumn != iOutputColumn) {
                continue;
            }
            final RelNode input = rel.getInputs().get(mapping.iInputRel);
            final int column = mapping.iInputColumn;
            Set<RelColumnOrigin> origins = mq.getColumnOrigins(input, column);
            if (origins == null) {
                return Collections.emptySet();
            }
            if (mapping.derived) {
                origins = createDerivedColumnOrigins(origins);
            }
            set.addAll(origins);
        }
        return set;
    }

    // Catch-all rule when none of the others apply.
    @SuppressWarnings("squid:S1172")
    public Set<RelColumnOrigin> getColumnOrigins(RelNode rel,
            RelMetadataQuery mq, int iOutputColumn) {
        // NOTE jvs 28-Mar-2006: We may get this wrong for a physical table
        // expression which supports projections. In that case,
        // it's up to the plugin writer to override with the
        // correct information.

        if (!rel.getInputs().isEmpty()) {
            // No generic logic available for non-leaf rels.
            return Collections.emptySet();
        }

        final Set<RelColumnOrigin> set = new LinkedHashSet<>();

        RelOptTable table = rel.getTable();
        if (table == null) {
            // Somebody is making column values up out of thin air, like a
            // VALUES clause, so we return an empty set.
            return set;
        }

        // Detect the case where a physical table expression is performing
        // projection, and say we don't know instead of making any assumptions.
        // (Theoretically we could try to map the projection using column
        // names.) This detection assumes the table expression doesn't handle
        // rename as well.
        if (table.getRowType() != rel.getRowType()) {
            return Collections.emptySet();
        }

        set.add(new RelColumnOrigin(table, iOutputColumn, false));
        return set;
    }

    private Set<RelColumnOrigin> createDerivedColumnOrigins(
            Set<RelColumnOrigin> inputSet) {
        if (inputSet == null) {
            return Collections.emptySet();
        }
        final Set<RelColumnOrigin> set = new LinkedHashSet<>();
        for (RelColumnOrigin rco : inputSet) {
            RelColumnOrigin derived = new RelColumnOrigin(
                    rco.getOriginTable(),
                    rco.getOriginColumnOrdinal(),
                    true);
            set.add(derived);
        }
        return set;
    }

    private Set<RelColumnOrigin> createDerivedColumnOrigins(
            Set<RelColumnOrigin> inputSet, String transform, boolean originTransform) {
        if (inputSet == null || inputSet.isEmpty()) {
            return Collections.emptySet();
        }
        final Set<RelColumnOrigin> set = new LinkedHashSet<>();

        String finalTransform = originTransform ? computeTransform(inputSet, transform) : transform;
        for (RelColumnOrigin rco : inputSet) {
            RelColumnOrigin derived = new RelColumnOrigin(
                    rco.getOriginTable(),
                    rco.getOriginColumnOrdinal(),
                    true,
                    finalTransform);
            set.add(derived);
        }
        return set;
    }

    /**
     * Replace the variable at the beginning of $ in input with the real field information
     */
    private String computeTransform(Set<RelColumnOrigin> inputSet, String transform) {
        LOG.debug("origin transform: {}", transform);
        Pattern pattern = Pattern.compile("\\$\\d+");
        Matcher matcher = pattern.matcher(transform);

        Set<String> operandSet = new LinkedHashSet<>();
        while (matcher.find()) {
            operandSet.add(matcher.group());
        }

        if (operandSet.isEmpty()) {
            LOG.info("operandSet is empty");
            return null;
        }
        if (inputSet.size() != operandSet.size()) {
            LOG.warn("The number [{}] of fields in the source tables are not equal to operands [{}]", inputSet.size(),
                    operandSet.size());
            return null;
        }

        Map<String, String> sourceColumnMap = new HashMap<>();
        Iterator<String> iterator = optimizeSourceColumnSet(inputSet).iterator();
        operandSet.forEach(e -> sourceColumnMap.put(e, iterator.next()));
        LOG.debug("sourceColumnMap: {}", sourceColumnMap);

        matcher = pattern.matcher(transform);
        String temp;
        while (matcher.find()) {
            temp = matcher.group();
            transform = transform.replace(temp, sourceColumnMap.get(temp));
        }

        // temporary special treatment
        transform = transform.replace("_UTF-16LE", "");
        LOG.debug("transform: {}", transform);
        return transform;
    }

    /**
     * Increase the readability of transform.
     * if catalog, database and table are the same, return field.
     * If the catalog and database are the same, return the table and field.
     * If the catalog is the same, return the database, table, field.
     * Otherwise, return all
     */
    private Set<String> optimizeSourceColumnSet(Set<RelColumnOrigin> inputSet) {
        Set<String> catalogSet = new HashSet<>();
        Set<String> databaseSet = new HashSet<>();
        Set<String> tableSet = new HashSet<>();
        Set<List<String>> qualifiedSet = new LinkedHashSet<>();
        for (RelColumnOrigin rco : inputSet) {
            RelOptTable originTable = rco.getOriginTable();
            List<String> qualifiedName = originTable.getQualifiedName();

            // catalog,database,table,field
            List<String> qualifiedList = new ArrayList<>(qualifiedName);
            catalogSet.add(qualifiedName.get(0));
            databaseSet.add(qualifiedName.get(1));
            tableSet.add(qualifiedName.get(2));

            String field = rco.getTransform() != null
                    ? rco.getTransform()
                    : originTable.getRowType().getFieldNames().get(rco.getOriginColumnOrdinal());
            qualifiedList.add(field);
            qualifiedSet.add(qualifiedList);
        }
        if (catalogSet.size() == 1 && databaseSet.size() == 1 && tableSet.size() == 1) {
            return optimizeName(qualifiedSet, e -> e.get(3));
        } else if (catalogSet.size() == 1 && databaseSet.size() == 1) {
            return optimizeName(qualifiedSet, e -> String.join(DELIMITER, e.subList(2, 4)));
        } else if (catalogSet.size() == 1) {
            return optimizeName(qualifiedSet, e -> String.join(DELIMITER, e.subList(1, 4)));
        } else {
            return optimizeName(qualifiedSet, e -> String.join(DELIMITER, e));
        }
    }

    private Set<String> optimizeName(Set<List<String>> qualifiedSet, Function<List<String>, String> mapper) {
        return qualifiedSet.stream().map(mapper).collect(Collectors.toSet());
    }

    private Set<RelColumnOrigin> getMultipleColumns(RexNode rexNode, RelNode input,
            final RelMetadataQuery mq) {
        final Set<RelColumnOrigin> set = new LinkedHashSet<>();
        final RexVisitor<Void> visitor = new RexVisitorImpl<Void>(true) {

            @Override
            public Void visitInputRef(RexInputRef inputRef) {
                Set<RelColumnOrigin> inputSet = mq.getColumnOrigins(input, inputRef.getIndex());
                if (inputSet != null) {
                    set.addAll(inputSet);
                }
                return null;
            }
        };
        rexNode.accept(visitor);
        return set;
    }
}
