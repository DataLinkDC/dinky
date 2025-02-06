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

package org.apache.calcite.rel.metadata;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.SingleRel;
import org.apache.calcite.rel.core.*;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.*;
import org.apache.calcite.util.BuiltInMethod;
import org.apache.lineage.flink.sql.metadata.LineageRelColumnOrigin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.lineage.flink.sql.metadata.Constant.DELIMITER;
import static org.apache.lineage.flink.sql.metadata.Constant.INITIAL_CAPACITY;
import static org.apache.lineage.flink.sql.metadata.LineageRelColumnOrigin.NULL_REL_OPT_TABLE;

public class RelMdColumnOrigins implements MetadataHandler<BuiltInMetadata.ColumnOrigin> {

    private static final Logger LOG = LoggerFactory.getLogger(RelMdColumnOrigins.class);

    private final Pattern pattern = Pattern.compile("\\$[\\w.]+");

    public static final RelMetadataProvider SOURCE = ReflectiveRelMetadataProvider.reflectiveSource(BuiltInMethod.COLUMN_ORIGIN.method, new RelMdColumnOrigins());

    // ~ Constructors -----------------------------------------------------------

    private RelMdColumnOrigins() {
    }

    // ~ Methods ----------------------------------------------------------------

    public MetadataDef<BuiltInMetadata.ColumnOrigin> getDef() {
        return BuiltInMetadata.ColumnOrigin.DEF;
    }

    public Set<RelColumnOrigin> getColumnOrigins(Aggregate rel, RelMetadataQuery mq, int iOutputColumn) {
        if (iOutputColumn < rel.getGroupCount()) {
            // get actual index of Group columns.
            return mq.getColumnOrigins(rel.getInput(), rel.getGroupSet().asList().get(iOutputColumn));
        }

        // Aggregate columns are derived from input columns
        AggregateCall call = rel.getAggCallList().get(iOutputColumn - rel.getGroupCount());
        final Set<RelColumnOrigin> set = new LinkedHashSet<>();
        for (Integer iInput : call.getArgList()) {
            set.addAll(mq.getColumnOrigins(rel.getInput(), iInput));
        }
        return createDerivedColumnOrigins(set, call);
/*
        if (iOutputColumn < rel.getGroupCount()) {
            // get actual index of Group columns.
            return mq.getColumnOrigins(rel.getInput(), rel.getGroupSet().asList().get(iOutputColumn));
        }

        // Aggregate columns are derived from input columns
        AggregateCall call = rel.getAggCallList().get(iOutputColumn - rel.getGroupCount());
        Set<RelColumnOrigin> set = new LinkedHashSet<>();
        List<Integer> args = call.getArgList();
        if( args == null || args.size()<1 ) {
            set.add(new LineageRelColumnOrigin(NULL_REL_OPT_TABLE, 0, true, call.toString()));
            return set;
        }else{
            for (Integer iInput : args) {
                set.addAll(mq.getColumnOrigins(rel.getInput(), iInput));
            }
        }

        return createDerivedColumnOrigins(set, call);*/
    }

    public Set<RelColumnOrigin> getColumnOrigins(Join rel, RelMetadataQuery mq, int iOutputColumn) {
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
        List<String> fieldNameList = rel.getLeft().getRowType().getFieldNames();
        int nLeftColumns = fieldNameList.size();
        if (iOutputColumn < nLeftColumns) {
            return mq.getColumnOrigins(rel.getLeft(), iOutputColumn);
        } else {
            Uncollect uncollect = null;
            if (rel.getRight() instanceof TableFunctionScan) {
                final Set<RelColumnOrigin> set = new LinkedHashSet<>();
                for (Integer iInput : rel.getRequiredColumns().asList()) {
                    set.addAll(mq.getColumnOrigins(rel.getLeft(), iInput));
                }
                // get the field name of the left table configured in the Table Function on the right
                TableFunctionScan tableFunctionScan = (TableFunctionScan) rel.getRight();
                String transform = computeTransform(set, tableFunctionScan.getCall()) + DELIMITER + tableFunctionScan.getRowType().getFieldNames().get(iOutputColumn - nLeftColumns);
                return createDerivedColumnOrigins(set, transform);
            } else if (rel.getRight() instanceof Uncollect) {
                uncollect = (Uncollect)rel.getRight();
            } else if(rel.getRight() instanceof Project) {
                Project project = ((Project) rel.getRight());
                final RelNode input = project.getInput();
                if(input instanceof Uncollect) {
                    uncollect = (Uncollect)input;
                }
            }
            if(uncollect != null) {
                final Set<RelColumnOrigin> set = new LinkedHashSet<>();
                for (Integer iInput : rel.getRequiredColumns().asList()) {
                    set.addAll(mq.getColumnOrigins(rel.getLeft(), iInput));
                }
//                List<String> nameList = uncollect.getInput().getRowType().getFieldNames(); // String.join(",", nameList) + DELIMITER +
                String fieldName = uncollect.getRowType().getFieldNames().get(iOutputColumn - nLeftColumns);
                String transform = computeTransform(set, "UNNEST("+fieldName+")");
                return createDerivedColumnOrigins(set, transform);
            }

            return mq.getColumnOrigins(rel.getRight(), iOutputColumn - nLeftColumns);
        }
    }

    public Set<RelColumnOrigin> getColumnOrigins(SetOp rel, RelMetadataQuery mq, int iOutputColumn) {
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
    public Set<RelColumnOrigin> getColumnOrigins(Snapshot rel, RelMetadataQuery mq, int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    /**
     * Support the field blood relationship of watermark
     */
    public Set<RelColumnOrigin> getColumnOrigins(SingleRel rel, RelMetadataQuery mq, int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    /*public Set<RelColumnOrigin> getColumnOrigins(Uncollect rel, RelMetadataQuery mq, int iOutputColumn) {
        final RelNode input = rel.getInput();
        List<String> fieldNameList = input.getRowType().getFieldNames();
        String fieldName = rel.getRowType().getFieldNames().get(iOutputColumn);

        final Set<RelColumnOrigin> set;
        if (input instanceof Project) {
            Project project = (Project) input;
            final RexNode rexNode = project.getProjects().get(iOutputColumn);
            if (rexNode instanceof RexInputRef) {
                RexInputRef inputRef = (RexInputRef) rexNode;
                return mq.getColumnOrigins(input, inputRef.getIndex());
            } // RexFieldAccess
            set = getMultipleColumns(rexNode, input, mq);
        } else {
            set = mq.getColumnOrigins(input, iOutputColumn);
        }

        return createDerivedColumnOrigins(set);
    }*/

    /**
     * Support for new fields in the source table similar to those created with the LOCALTIMESTAMP function
     */
    public Set<RelColumnOrigin> getColumnOrigins(Project rel, final RelMetadataQuery mq, int iOutputColumn) {
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
        } else if (input instanceof TableScan && rexNode.getClass().equals(RexCall.class) && ((RexCall) rexNode).getOperands().isEmpty()) {
            return mq.getColumnOrigins(input, iOutputColumn);
        }
        // Anything else is a derivation, possibly from multiple columns.
        final Set<RelColumnOrigin> set = getMultipleColumns(rexNode, input, mq);
        return createDerivedColumnOrigins(set, rexNode);
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
        final RelNode input = rel.getInput();
        List<String> fieldNameList = input.getRowType().getFieldNames();
        String fieldName = rel.getRowType().getFieldNames().get(iOutputColumn);

        // 1. get the column names of the partitioned keys.
        Set<String> partitionKeySet = rel.getPartitionKeys().toList().stream().map(fieldNameList::get).collect(Collectors.toSet());

        // 2. get the lineage of these partitioned columns.
        if (partitionKeySet.contains(fieldName)) {
            return mq.getColumnOrigins(input, fieldNameList.indexOf(fieldName));
        }

        // 3. the rest of the iOutputColumn must be derived by `MEASURES`.
        RexNode rexNode = rel.getMeasures().get(fieldName);
        RexPatternFieldRef rexPatternFieldRef = searchRexPatternFieldRef(rexNode);
        if (rexPatternFieldRef != null) {
            final Set<RelColumnOrigin> set = mq.getColumnOrigins(input, rexPatternFieldRef.getIndex());
            if (rexNode instanceof RexCall) {
                return createDerivedColumnOrigins(set, ((RexCall) rexNode).getOperands().get(0));
            } else {
                return createDerivedColumnOrigins(set);
            }
        }
        // 4. something unsupported yet.
        LOG.warn("Parse column lineage failed, rel:[{}], iOutputColumn:[{}]", rel, iOutputColumn);
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

    public Set<RelColumnOrigin> getColumnOrigins(Calc rel, final RelMetadataQuery mq, int iOutputColumn) {
        final RelNode input = rel.getInput();
        final RexShuttle rexShuttle = new RexShuttle() {

            @Override
            public RexNode visitLocalRef(RexLocalRef localRef) {
                return rel.getProgram().expandLocalRef(localRef);
            }
        };

        List<RexNode> projects = new ArrayList<>(rexShuttle.apply(rel.getProgram().getProjectList()));

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

    public Set<RelColumnOrigin> getColumnOrigins(Filter rel, RelMetadataQuery mq, int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    public Set<RelColumnOrigin> getColumnOrigins(Sort rel, RelMetadataQuery mq, int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    public Set<RelColumnOrigin> getColumnOrigins(TableModify rel, RelMetadataQuery mq, int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    public Set<RelColumnOrigin> getColumnOrigins(Exchange rel, RelMetadataQuery mq, int iOutputColumn) {
        return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
    }

    public Set<RelColumnOrigin> getColumnOrigins(TableFunctionScan rel, RelMetadataQuery mq, int iOutputColumn) {
        Set<RelColumnOrigin> set = new LinkedHashSet<>();
        Set<RelColumnMapping> mappings = rel.getColumnMappings();
        if (mappings == null) {
            if (!rel.getInputs().isEmpty()) {
                RelNode input = rel.getInput(0);
                List<RelDataTypeField> inputFieldList = input.getRowType().getFieldList();
                int nInputColumns = inputFieldList.size();
                if (iOutputColumn < nInputColumns) {
                    return mq.getColumnOrigins(input, iOutputColumn);
                } else {
                    RexCall rexCall = (RexCall) rel.getCall();
                    List<RexNode> operands = rexCall.getOperands();
                    RexInputRef rexInputRef = (RexInputRef) ((RexCall) operands.get(1)).getOperands().get(0);
                    set = mq.getColumnOrigins(input, rexInputRef.getIndex());

                    String transform = rexCall.op.getName() + DELIMITER + rexCall.getType().getFieldNames().get(iOutputColumn);
                    return createDerivedColumnOrigins(set, transform);
                }
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

    /**
     * Catch-all rule when none of the others apply.
     */
    @SuppressWarnings("squid:S1172")
    public Set<RelColumnOrigin> getColumnOrigins(RelNode rel, RelMetadataQuery mq, int iOutputColumn) {
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

    private Set<RelColumnOrigin> createDerivedColumnOrigins(Set<RelColumnOrigin> inputSet) {
        if (inputSet == null) {
            return Collections.emptySet();
        }
        final Set<RelColumnOrigin> set = new LinkedHashSet<>();
        for (RelColumnOrigin rco : inputSet) {
            RelColumnOrigin derived = new RelColumnOrigin(rco.getOriginTable(), rco.getOriginColumnOrdinal(), true);
            set.add(derived);
        }
        return set;
    }

    private Set<RelColumnOrigin> createDerivedColumnOrigins(Set<RelColumnOrigin> inputSet, Object transform) {
        String finalTransform = computeTransform(inputSet, transform);
        if (inputSet == null || inputSet.isEmpty()) {
            return finalTransform == null ? Collections.emptySet()
                    : Collections.singleton(new LineageRelColumnOrigin(NULL_REL_OPT_TABLE, 0, true, finalTransform));
        }

        final Set<RelColumnOrigin> set = new LinkedHashSet<>();
        for (RelColumnOrigin rco : inputSet) {
            RelColumnOrigin derived = finalTransform == null ? new RelColumnOrigin(rco.getOriginTable(), rco.getOriginColumnOrdinal(), true)
                    : new LineageRelColumnOrigin(rco.getOriginTable(), rco.getOriginColumnOrdinal(), true, finalTransform);
            set.add(derived);
        }
        return set;
    }

    /**
     * Replace the variable at the beginning of $ in input with the real field information
     */
    private String computeTransform(Set<RelColumnOrigin> inputSet, Object transform) {
        LOG.debug("origin transform: {}, class: {}", transform, transform.getClass());
        String finalTransform = transform.toString();

        Matcher matcher = pattern.matcher(finalTransform);

        Set<String> operandSet = new LinkedHashSet<>();
        while (matcher.find()) {
            operandSet.add(matcher.group());
        }

        if (operandSet.isEmpty()) {
            return finalTransform;
        }
        if (inputSet.size() != operandSet.size()) {
            LOG.warn("The number [{}] of fields in the source tables are not equal to operands [{}]", inputSet.size(), operandSet.size());
            return null;
        }

        Map<String, String> sourceColumnMap = buildSourceColumnMap(inputSet, transform);

        matcher = pattern.matcher(finalTransform);
        String temp;
        while (matcher.find()) {
            temp = matcher.group();
            finalTransform = finalTransform.replace(temp, sourceColumnMap.get(temp));
        }
        // temporary special treatment
        finalTransform = finalTransform.replace("_UTF-16LE", "");
        LOG.debug("final transform: {}", finalTransform);
        return finalTransform;
    }

    /**
     * According to the order of generating inputSet, generate the corresponding index number.
     *
     * <p>
     * for example: ROW_NUMBER() OVER (PARTITION BY $0 ORDER BY $3 DESC NULLS LAST),
     * The order of inputSet is $3, $0, instead of $0, $3 obtained by traversing the above string normally
     */
    private Map<String, String> buildSourceColumnMap(Set<RelColumnOrigin> inputSet, Object transform) {
        Set<Object> traversalSet = new LinkedHashSet<>();
        if (transform instanceof AggregateCall) {
            AggregateCall call = ((AggregateCall) transform);
            traversalSet.addAll(call.getArgList());
        } else if (transform instanceof RexNode) {
            RexNode rexNode = (RexNode) transform;
            RexVisitor<Void> visitor = new RexVisitorImpl<Void>(true) {

                @Override
                public Void visitInputRef(RexInputRef inputRef) {
                    traversalSet.add(inputRef.getIndex());
                    return null;
                }

                @Override
                public Void visitPatternFieldRef(RexPatternFieldRef fieldRef) {
                    traversalSet.add(fieldRef.getIndex());
                    return null;
                }

                @Override
                public Void visitFieldAccess(RexFieldAccess fieldAccess) {
                    traversalSet.add(fieldAccess.toString().replace("$", ""));
                    return null;
                }
            };
            rexNode.accept(visitor);
        }
        Map<String, String> sourceColumnMap = new HashMap<>(INITIAL_CAPACITY);
        Iterator<String> iterator = optimizeSourceColumnSet(inputSet).iterator();
        traversalSet.forEach(index -> sourceColumnMap.put("$" + index, iterator.next()));
        LOG.debug("sourceColumnMap: {}", sourceColumnMap);
        return sourceColumnMap;
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
        Set<Object> qualifiedSet = new LinkedHashSet<>();
        for (RelColumnOrigin rco : inputSet) {
            RelOptTable originTable = rco.getOriginTable();
            if(originTable == NULL_REL_OPT_TABLE){
                qualifiedSet.add(rco);
                continue;
            }
            List<String> qualifiedName = originTable.getQualifiedName();
            // catalog,database,table,field
            List<String> qualifiedList = new ArrayList<>(qualifiedName);
            catalogSet.add(qualifiedName.get(0));
            databaseSet.add(qualifiedName.get(1));
            tableSet.add(qualifiedName.get(2));

            String field = rco instanceof LineageRelColumnOrigin ? ((LineageRelColumnOrigin) rco).getTransform() : originTable.getRowType().getFieldNames().get(rco.getOriginColumnOrdinal());
            qualifiedList.add(field);
            qualifiedSet.add(qualifiedList);
        }

        if (catalogSet.size() == 1 && databaseSet.size() == 1 && tableSet.size() == 1) {
            return optimizeName(qualifiedSet, e -> e.get(3));
        } else if (catalogSet.size() == 1 && databaseSet.size() == 1) {
            return optimizeName(qualifiedSet, e->String.join(DELIMITER, e.subList(2, 4)));
        } else if (catalogSet.size() == 1) {
            return optimizeName(qualifiedSet, e -> String.join(DELIMITER, e.subList(1, 4)));
        } else {
            return optimizeName(qualifiedSet, e -> String.join(DELIMITER, e));
        }
    }

    private Set<String> optimizeName(Set<Object> qualifiedSet, Function<List<String>, String> mapper) {
        return qualifiedSet.stream().map(o -> o instanceof LineageRelColumnOrigin
                ? ((LineageRelColumnOrigin) o).getTransform()
                : mapper.apply((List<String>) o)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<RelColumnOrigin> getMultipleColumns(RexNode rexNode, RelNode input, final RelMetadataQuery mq) {
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
