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

package com.zdpx.coder.operator.operators;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdpx.coder.Specifications;
import com.zdpx.coder.graph.InputPortObject;
import com.zdpx.coder.graph.OutputPortObject;
import com.zdpx.coder.operator.Column;
import com.zdpx.coder.operator.FieldFunction;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.OperatorUtil;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.NameHelper;
import com.zdpx.coder.utils.TemplateUtils;

/**
 * <b>Complex Event Processing</b> operator, which allows for pattern detection in event streams.
 * the sql statement follow <I>Row Pattern Recognition</I>(ISO/IEC TR 19075-5:2016), using the
 * <b>MATCH_RECOGNIZE</b> clause for complex event processing in SQL.
 *
 * <p>A <b>MATCH_RECOGNIZE</b> clause enables the following tasks:
 *
 * <ul>
 *   <li>Logically partition and order the data that is used with the <i>PARTITION BY</i> and
 *       <i>ORDER BY</i> clauses.
 *   <li>Define patterns of rows to seek using the <i>PATTERN</i> clause. These patterns use a
 *       syntax similar to that of regular expressions.
 *   <li>The logical components of the row pattern variables are specified in the <i>DEFINE</i>
 *       clause.
 *   <li>Define measures, which are expressions usable in other parts of the SQL query, in the
 *       MEASURES clause.
 * </ul>
 *
 * <p>Every <b>MATCH_RECOGNIZE</b> query consists of the following clauses:
 *
 * <ul>
 *   <li><b>PARTITION BY</b> - defines the logical partitioning of the table; similar to a GROUP BY
 *       operation.
 *   <li><b>ORDER BY</b> - specifies how the incoming rows should be ordered; this is essential as
 *       patterns depend on an order.
 *   <li><b>MEASURES</b> - defines output of the clause; similar to a SELECT clause.
 *   <li><b>ONE ROW PER MATCH</b> - output mode which defines how many rows per match should be
 *       produced.
 *   <li><b>AFTER MATCH SKIP</b> - specifies where the next match should start; this is also a way
 *       to control how many distinct matches a single event can belong to.
 *   <li><b>PATTERN</b> - allows constructing patterns that will be searched for using a regular
 *       expression-like syntax .
 *   <li><b>DEFINE</b> - this section defines the conditions that the pattern variables must
 *       satisfy.
 * </ul>
 *
 * <p>The following example illustrates the syntax for basic pattern recognition:
 *
 * <pre>{@code
 * SELECT T.aid, T.bid, T.cid
 * FROM MyTable
 *     MATCH_RECOGNIZE (
 *       PARTITION BY userid
 *       ORDER BY proctime
 *       MEASURES
 *         A.id AS aid,
 *         B.id AS bid,
 *         C.id AS cid
 *       PATTERN (A B C)
 *       DEFINE
 *         A AS name = 'a',
 *         B AS name = 'b',
 *         C AS name = 'c'
 *     ) AS T
 * }</pre>
 *
 * <note> Currently, the MATCH_RECOGNIZE clause can only be applied to an <b>append table</b>
 * </note>
 *
 * @author Licho Sun
 */
public class CepOperator extends Operator {
    public static final String PARTITION = "partition";
    public static final String ORDER_BY = "orderBy";
    public static final String INPUT_TABLE_NAME = "inputTableName";
    public static final String OUTPUT_TABLE_NAME = "outputTableName";
    public static final String MEASURES = "measures";
    public static final String DEFINES = "defines";
    public static final String PATTERNS = "patterns";
    private static final String SKIP_STRATEGY = "skipStrategy";
    private static final String CEP = "CEP";

    private static final String TEMPLATE =
            MessageFormat.format(
                    "<#import \"{0}\" as e>\nCREATE VIEW $'{'{1}.{2}'}' \nAS \n<@e.cepFunction {1}/>",
                    Specifications.TEMPLATE_FILE, CEP, OUTPUT_TABLE_NAME);

    private final ObjectMapper mapper = new ObjectMapper();
    private InputPortObject<TableInfo> inputPortObject;
    private OutputPortObject<TableInfo> outputPortObject;

    @Override
    protected void initialize() {
        inputPortObject = registerInputObjectPort("input_0");
        outputPortObject = registerOutputObjectPort("output_0");
    }

    @Override
    protected Map<String, String> declareUdfFunction() {
        return new HashMap<>();
    }

    @Override
    protected boolean applies() {
        return true;
    }

    @Override
    protected void execute() {

        Map<String, Object> parameters = getFirstParameterMap();
        final String partition = (String) parameters.get(PARTITION);
        String orderBy = (String) parameters.get(ORDER_BY);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> defineList = (List<Map<String, Object>>) parameters.get(DEFINES);
        List<Define> defines =
                mapper.convertValue(defineList, new TypeReference<List<Define>>() {});

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> patternList =
                (List<Map<String, Object>>) parameters.get(PATTERNS);
        List<Pattern> patterns =
                mapper.convertValue(patternList, new TypeReference<List<Pattern>>() {});

        SkipStrategy skipStrategy =
                mapper.convertValue(parameters.get(SKIP_STRATEGY), SkipStrategy.class);

        TableInfo tableInfo = inputPortObject.getOutputPseudoData();
        @SuppressWarnings("unchecked")
        List<FieldFunction> ffs =
                FieldFunction.analyzeParameters(
                        tableInfo.getName(), (List<Map<String, Object>>) parameters.get(MEASURES));
        String outputTableName = NameHelper.generateVariableName("CepOperator");

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(OUTPUT_TABLE_NAME, outputTableName);
        parameterMap.put(INPUT_TABLE_NAME, tableInfo.getName());
        parameterMap.put(PARTITION, partition);
        parameterMap.put(ORDER_BY, orderBy);
        parameterMap.put(SKIP_STRATEGY, skipStrategy);
        parameterMap.put(MEASURES, ffs);
        parameterMap.put(
                DEFINES, defines.stream().map(Define::toString).collect(Collectors.toList()));
        parameterMap.put(
                PATTERNS, patterns.stream().map(Pattern::toString).collect(Collectors.toList()));

        Map<String, Object> result = new HashMap<>();
        result.put(CEP, parameterMap);
        String sqlStr = TemplateUtils.format(this.getClass().getName(), result, TEMPLATE);
        this.getSchemaUtil().getGenerateResult().generate(sqlStr);

        List<Column> columns = Specifications.convertFieldFunctionToColumns(ffs);
        tableInfo.getColumns().stream()
                .filter(t -> t.getName().equals(partition))
                .findFirst()
                .ifPresent(columns::add);
        OperatorUtil.postTableOutput(outputPortObject, outputTableName, columns);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getSpecialTypeList(
            Map<String, Object> parameters, String key, Class<T> type) {
        List<Map<String, Object>> measureList = (List<Map<String, Object>>) parameters.get(key);
        return mapper.convertValue(measureList, new TypeReference<List<T>>() {});
    }

    /**
     * The {@link Define DEFINE} and MEASURES keywords have similar meanings to the WHERE and SELECT
     * clauses in a simple SQL query.
     *
     * <p>The MEASURES clause defines what will be included in the output of a matching pattern. It
     * can project columns and define expressions for evaluation. The number of produced rows
     * depends on the output mode setting.
     *
     * <p>The {@link Define DEFINE} clause specifies conditions that rows have to fulfill in order
     * to be classified to a corresponding pattern variable. If a condition is not defined for a
     * pattern variable, a default condition will be used which evaluates to true for every row.
     */
    static class Define {
        private static final String AS = "AS";
        /** Pattern Variable */
        private String variable;
        /** condition like where sql statement. */
        private String condition;

        public Define() {
            // for jackson
        }

        public String generateStatement() {
            return variable + " " + AS + " " + condition;
        }

        @Override
        public String toString() {
            return generateStatement();
        }

        // region g/s
        public String getVariable() {
            return variable;
        }

        public void setVariable(String variable) {
            this.variable = variable;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }
        // endregion
    }

    static class Pattern {
        /** pattern variable name */
        private String variable;
        /** pattern variable quantifier */
        private String quantifier;

        public Pattern() {
            //            for jackson
        }

        @Override
        public String toString() {
            return generateStatement();
        }

        public String generateStatement() {
            return variable + quantifier;
        }

        // region g/s
        public String getVariable() {
            return variable;
        }

        public void setVariable(String variable) {
            this.variable = variable;
        }

        public String getQuantifier() {
            return quantifier;
        }

        public void setQuantifier(String quantifier) {
            this.quantifier = quantifier;
        }
        // endregion

    }

    /**
     * The AFTER MATCH SKIP clause specifies where to start a new matching procedure after a
     * complete match was found.
     */
    static class SkipStrategy {
        /**
         * SKIP PAST LAST ROW - resumes the pattern matching at the next row after the last row of
         * the current match.
         */
        public static final String LAST_ROW = "LAST_ROW";
        /**
         * <b>SKIP TO NEXT ROW</b> - continues searching for a new match starting at the next row
         * after the starting row of the match.
         */
        public static final String NEXT_ROW = "NEXT_ROW";
        /**
         * <b>SKIP TO LAST variable</b> - resumes the pattern matching at the last row that is
         * mapped to the specified pattern variable.
         */
        public static final String LAST = "LAST";
        /**
         * <b>SKIP TO FIRST variable</b> - resumes the pattern matching at the first row that is
         * mapped to the specified pattern variable.
         */
        public static final String FIRST = "FIRST";

        /** strategy */
        private String strategy;
        /** pattern variable */
        private String variable;

        public String generateStatement() {
            switch (strategy) {
                case LAST_ROW:
                    {
                        return "PAST LAST ROW";
                    }
                case NEXT_ROW:
                    {
                        return "TO NEXT ROW";
                    }
                case LAST:
                    {
                        return "TO LAST " + variable;
                    }
                case FIRST:
                    {
                        return "TO FIRST " + variable;
                    }
                default:
                    return "";
            }
        }

        @Override
        public String toString() {
            return generateStatement();
        }

        // region g/s
        public String getStrategy() {
            return strategy;
        }

        public void setStrategy(String strategy) {
            this.strategy = strategy;
        }

        public String getVariable() {
            return variable;
        }

        public void setVariable(String variable) {
            this.variable = variable;
        }
        // endregion
    }
}
