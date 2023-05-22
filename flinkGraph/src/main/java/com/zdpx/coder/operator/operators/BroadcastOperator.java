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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.zdpx.coder.Specifications;
import com.zdpx.coder.code.CodeJavaBuilder;
import com.zdpx.coder.graph.DataType;
import com.zdpx.coder.graph.InputPortObject;
import com.zdpx.coder.graph.OutputPortObject;
import com.zdpx.coder.operator.Column;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.OperatorParameterUtils;
import com.zdpx.coder.operator.OperatorUtil;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.NameHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * 广播算子, 输入有两个数据流, 一般为一个为状态稳定数据流, 一个为数据变化流, 将稳定的数据嫁接到后者的每一帧数据上, 由于目前sql方式需要有关联主键 才可以联表,
 * 所以该算子是为源码方式实现的, 之后会有一个sql方式实现的宏算子.
 *
 * @author Licho Sun
 */
@Slf4j
public class BroadcastOperator extends Operator {

    public static final String VALUE = "value";
    public static final String CTX = "ctx";
    public static final String OUT = "out";
    public static final String STATE = "state";

    private static final TypeName MAP_STATE_DESCRIPTOR_VOID_ROW =
            ParameterizedTypeName.get(
                    Specifications.MAP_STATE_DESCRIPTOR, Specifications.VOID, Specifications.ROW);

    private static final TypeName BROADCAST_CONNECTED_STREAM_ROW_ROW =
            ParameterizedTypeName.get(
                    Specifications.BROADCAST_CONNECTED_STREAM,
                    Specifications.ROW,
                    Specifications.ROW);

    private static final ClassName READ_ONLY_BROADCAST_STATE =
            ClassName.get("org.apache.flink.api.common.state", "ReadOnlyBroadcastState");

    private static final TypeName READ_ONLY_BROADCAST_STATE_VOID_ROW =
            ParameterizedTypeName.get(
                    READ_ONLY_BROADCAST_STATE, Specifications.VOID, Specifications.ROW);

    private static final ClassName BROADCAST_STATE =
            ClassName.get("org.apache.flink.api.common.state", "BroadcastState");

    private static final TypeName BROADCAST_STATE_VOID_ROW =
            ParameterizedTypeName.get(BROADCAST_STATE, Specifications.VOID, Specifications.ROW);

    private static final String DESCRIPTOR_NAME = "descriptor";

    private InputPortObject<TableInfo> primaryInput;
    private InputPortObject<TableInfo> broadcastInput;
    private OutputPortObject<TableInfo> outputPort;

    private TypeName outputTupleNType;
    private ClassName typeNClass;
    private String broadcastResultTableName;

    @Override
    protected void initialize() {
        primaryInput = registerInputObjectPort("primaryInput");
        broadcastInput = registerInputObjectPort("broadcastInput");
        outputPort = registerOutputObjectPort("output_0");
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

        if (getOutputPorts().isEmpty()
                || this.nodeWrapper == null
                || !(this.getSchemaUtil().getGenerateResult() instanceof CodeJavaBuilder)) {
            log.error("BroadcastOperator information err.");
            return;
        }

        Map<String, Object> parameters = getFirstParameterMap();
        Map<String, String> primaryParams =
                OperatorParameterUtils.getColumns("primary", parameters);
        Map<String, String> broadcastParams =
                OperatorParameterUtils.getColumns("broadcast", parameters);
        Map<String, String> outputParams = OperatorParameterUtils.getColumns("output", parameters);

        CodeBlock cb = getCodeBlock(primaryParams, broadcastParams, outputParams);
        CodeJavaBuilder gjr = (CodeJavaBuilder) this.getSchemaUtil().getGenerateResult();
        gjr.generateJavaFunction(cb);

        List<Column> cls = new ArrayList<>();
        BiConsumer<Map<String, String>, InputPortObject<TableInfo>> func =
                (Map<String, String> params, InputPortObject<TableInfo> input) ->
                        params.forEach(
                                (k, v) ->
                                        input.getOutputPseudoData().getColumns().stream()
                                                .filter(t -> t.getName().equals(k))
                                                .findAny()
                                                .ifPresent(cls::add));
        func.accept(primaryParams, primaryInput);
        func.accept(broadcastParams, broadcastInput);

        OperatorUtil.postTableOutput(outputPort, broadcastResultTableName, cls);
    }

    private CodeBlock getCodeBlock(
            Map<String, String> primaryParams,
            Map<String, String> broadcastParams,
            Map<String, String> outputParams) {
        String descriptorNameVariable = NameHelper.generateVariableName(DESCRIPTOR_NAME);
        String connectDS = NameHelper.generateVariableName("connectDs");
        String resultDS = NameHelper.generateVariableName("resultDS");
        String primaryTableName = getTableNameByInputPort(primaryInput);
        String primaryTableDs = NameHelper.generateVariableName(primaryTableName, "Ds");
        String broadTableName = getTableNameByInputPort(broadcastInput);
        String broadTableNameDs = NameHelper.generateVariableName(broadTableName, "Ds");

        broadcastResultTableName = NameHelper.generateVariableName("BroadcastResult");
        outputTupleNType = getStreamOutputTypeName(primaryParams, broadcastParams);

        ParameterizedTypeName singleOutputStreamOperatorTupleN =
                ParameterizedTypeName.get(
                        Specifications.SINGLE_OUTPUT_STREAM_OPERATOR, outputTupleNType);

        ParameterizedTypeName broadcastProcessFunctionRow2TupleN =
                ParameterizedTypeName.get(
                        Specifications.BROADCAST_PROCESS_FUNCTION,
                        Specifications.ROW,
                        Specifications.ROW,
                        outputTupleNType);

        TypeName readOnlyContext =
                broadcastProcessFunctionRow2TupleN.nestedClass("ReadOnlyContext");
        ParameterizedTypeName context = broadcastProcessFunctionRow2TupleN.nestedClass("Context");

        CodeBlock outputCodeBlock = createOutput(primaryParams, broadcastParams, typeNClass);
        final TypeName collectorTupleN =
                ParameterizedTypeName.get(Specifications.COLLECTOR, outputTupleNType);
        TypeSpec broadcastProcessFunction =
                getBroadcastProcessFunction(
                        descriptorNameVariable,
                        readOnlyContext,
                        context,
                        collectorTupleN,
                        outputCodeBlock);

        String columnsNames = OperatorParameterUtils.generateColumnNames(outputParams);
        return CodeBlock.builder()
                .addStatement(
                        "$1T $4N = $3N.toDataStream($3N.sqlQuery(\"select * from $2N\"))",
                        Specifications.DATA_STREAM_ROW,
                        primaryTableName,
                        Specifications.TABLE_ENV,
                        primaryTableDs)
                .addStatement(
                        "$1T $4N = $3N.toDataStream($3N.sqlQuery(\"select * from $2N\"))",
                        Specifications.DATA_STREAM_ROW,
                        broadTableName,
                        Specifications.TABLE_ENV,
                        broadTableNameDs)
                .addStatement(
                        "$2T $1L = new $2T($1S, $3T.VOID, $3T.ROW())",
                        descriptorNameVariable,
                        MAP_STATE_DESCRIPTOR_VOID_ROW,
                        Specifications.TYPES)
                .addStatement(
                        "$5T $1L = $2N.connect($3N.broadcast($4N))",
                        connectDS,
                        primaryTableDs,
                        broadTableNameDs,
                        descriptorNameVariable,
                        BROADCAST_CONNECTED_STREAM_ROW_ROW)
                .addStatement(
                        "$T $N = $N.process($L)",
                        singleOutputStreamOperatorTupleN,
                        resultDS,
                        connectDS,
                        broadcastProcessFunction)
                .addStatement(
                        "$1T $2N = $3N.fromDataStream($4N).as($5L)",
                        Specifications.TABLE,
                        broadcastResultTableName,
                        Specifications.TABLE_ENV,
                        resultDS,
                        columnsNames)
                .addStatement(
                        "$N.createTemporaryView($S, $N)",
                        Specifications.TABLE_ENV,
                        broadcastResultTableName,
                        broadcastResultTableName)
                .build();
    }

    private TypeSpec getBroadcastProcessFunction(
            String descriptorNameVariable,
            TypeName readOnlyContext,
            ParameterizedTypeName context,
            TypeName collectorTupleN,
            CodeBlock codeBlock) {
        final String broadcastState = "broadcastState";
        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(
                        ParameterizedTypeName.get(
                                Specifications.BROADCAST_PROCESS_FUNCTION,
                                Specifications.ROW,
                                Specifications.ROW,
                                outputTupleNType))
                .addMethod(
                        MethodSpec.methodBuilder("processElement")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(Specifications.ROW, VALUE)
                                .addParameter(readOnlyContext, CTX)
                                .addParameter(collectorTupleN, OUT)
                                .addException(Exception.class)
                                .addStatement(
                                        "$T $N = $N.getBroadcastState($N)",
                                        READ_ONLY_BROADCAST_STATE_VOID_ROW,
                                        broadcastState,
                                        CTX,
                                        descriptorNameVariable)
                                .addStatement(
                                        "$T $N = $L.get(null)",
                                        Specifications.ROW,
                                        STATE,
                                        broadcastState)
                                .beginControlFlow("if ($N != null)", STATE)
                                .addCode(codeBlock)
                                .endControlFlow()
                                .build())
                .addMethod(
                        MethodSpec.methodBuilder("processBroadcastElement")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(Specifications.ROW, VALUE)
                                .addParameter(context, CTX)
                                .addParameter(collectorTupleN, OUT)
                                .addException(Exception.class)
                                .addStatement(
                                        "$T $L = $N.getBroadcastState($N)",
                                        BROADCAST_STATE_VOID_ROW,
                                        broadcastState,
                                        CTX,
                                        descriptorNameVariable)
                                .addStatement("$N.clear()", broadcastState)
                                .addStatement("$N.put(null, value)", broadcastState)
                                .build())
                .build();
    }

    @SafeVarargs
    private final TypeName getStreamOutputTypeName(Map<String, String>... columns) {
        typeNClass = getOutputClass(columns);

        List<ClassName> typeList = new ArrayList<>();
        Arrays.stream(columns)
                .flatMap(t -> t.values().stream())
                .forEach(
                        t -> {
                            ClassName type = OperatorParameterUtils.getClassNameByName(t);
                            typeList.add(type);
                        });

        outputTupleNType =
                ParameterizedTypeName.get(typeNClass, typeList.toArray(new ClassName[0]));
        return outputTupleNType;
    }

    private ClassName getOutputClass(Map<String, String>[] columns) {
        int count = Arrays.stream(columns).map(Map::size).mapToInt(t -> t).sum();
        return createOutputGenericTypeN(count);
    }

    private CodeBlock createOutput(
            Map<String, String> primaryColumns, Map<String, String> broadcastColumns, TypeName tn) {
        CodeBlock.Builder cb = CodeBlock.builder();
        cb.add("$N.collect($T.of(", OUT, tn);

        boolean sentinel = false;
        final String GET_FIELD = "($T)$N.getField($S)";
        for (Map.Entry<String, String> pc : primaryColumns.entrySet()) {
            if (sentinel) {
                cb.add(", ");
            }

            sentinel = true;
            ClassName cl = OperatorParameterUtils.getClassNameByName(pc.getValue());
            cb.add(GET_FIELD, cl, VALUE, pc.getKey());
        }

        for (Map.Entry<String, String> pc : broadcastColumns.entrySet()) {
            ClassName cl = OperatorParameterUtils.getClassNameByName(pc.getValue());
            cb.add(", ");
            cb.add(GET_FIELD, cl, STATE, pc.getKey());
        }

        cb.addStatement("))");
        return cb.build();
    }

    private ClassName createOutputGenericTypeN(int count) {
        return ClassName.get("org.apache.flink.api.java.tuple", String.format("Tuple%s", count));
    }

    private String getTableNameByInputPort(InputPortObject<TableInfo> inputPortObject) {
        String pseudoName = "";
        TableInfo primaryPseudoData = inputPortObject.getOutputPseudoData();
        if (primaryPseudoData == null) {
            throw new NullPointerException(
                    String.format(
                            "can not get Output PseudoData from %s",
                            inputPortObject.getConnection().getFromPort().getParent().getName()));
        }
        if (primaryPseudoData.getType() == DataType.TABLE) {
            pseudoName = primaryPseudoData.getName();
        }
        return pseudoName;
    }
}
