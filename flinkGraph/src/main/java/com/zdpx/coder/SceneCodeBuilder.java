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

package com.zdpx.coder;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.zdpx.coder.code.CodeBuilder;
import com.zdpx.coder.code.CodeJavaBuilderImpl;
import com.zdpx.coder.code.CodeSqlBuilderImpl;
import com.zdpx.coder.graph.Scene;
import com.zdpx.coder.json.ResultType;
import com.zdpx.coder.operator.Identifier;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.utils.InstantiationUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置场景生成操作类
 *
 * @author Licho Sun
 */
@Slf4j
public class SceneCodeBuilder {
    // 自定义函数操作算子集
    private Map<String, String> udfFunctionMap = new HashMap<>();

    private CodeBuilder codeBuilder;
    private final Scene scene;

    public SceneCodeBuilder(Scene scene) {
        this.scene = scene;
        if (scene.getEnvironment().getResultType() == ResultType.JAVA) {
            CodeContext codeContext = createCodeContext(scene);
            codeBuilder = new CodeJavaBuilderImpl(codeContext);
        } else {
            codeBuilder = new CodeSqlBuilderImpl();
        }
    }

    public CodeBuilder getGenerateResult() {
        return codeBuilder;
    }

    public void setGenerateResult(CodeBuilder codeBuilder) {
        this.codeBuilder = codeBuilder;
    }

    public Scene getScene() {
        return scene;
    }

    public Map<String, String> getUdfFunctionMap() {
        return udfFunctionMap;
    }

    public void setUdfFunctionMap(Map<String, String> udfFunctionMap) {
        this.udfFunctionMap = udfFunctionMap;
    }

    /**
     * 根据场景配置生成可执行的java源文件
     *
     * @throws IOException ignore
     */
    public String build() {
        if (codeBuilder == null) {
            throw new IllegalStateException(String.format("Code Builder %s empty!", codeBuilder));
        }

        codeBuilder.firstBuild();
        createOperatorsCode();
        return codeBuilder.lastBuild();
    }

    /** 广度优先遍历计算节点, 生成相对应的源码 */
    private void createOperatorsCode() {
        List<Operator> sinkOperatorNodes =
                Scene.getSinkOperatorNodes(this.scene.getProcessPackage());
        List<Operator> sinks = new ArrayList<>(sinkOperatorNodes);
        Deque<Operator> ops = new ArrayDeque<>();

        bft(new HashSet<>(sinks), ops::push);
        ops.stream().distinct().forEach(this::operate);
    }

    /**
     * 广度优先遍历计算节点, 执行call 函数
     *
     * @param operators 起始节点集
     * @param call 待执行函数
     */
    private void bft(Set<Operator> operators, Consumer<Operator> call) {
        if (operators.isEmpty()) {
            return;
        }

        List<Operator> ops =
                operators.stream()
                        .sorted(Comparator.comparing(Operator::getId, Comparator.naturalOrder()))
                        .collect(Collectors.toList());
        final Set<Operator> preOperators = new HashSet<>();
        for (Operator op : ops) {
            call.accept(op);
            op.getInputPorts().values().stream()
                    .filter(t -> !Objects.isNull(t.getConnection()))
                    .map(t -> t.getConnection().getFromPort())
                    .forEach(fromPort -> preOperators.add(fromPort.getParent()));
        }
        bft(preOperators, call);
    }

    /**
     * 执行每个宏算子的代码生成逻辑
     *
     * @param op 宏算子
     */
    private void operate(Operator op) {
        op.setSchemaUtil(this);
        op.run();
    }

    /**
     * 获取operator的定义, key为{@link Identifier#getCode()} 返回值, 目前为Operator类的全限定名 value为类型定义.
     *
     * @return 返回operator集
     */
    public static Map<String, Class<? extends Operator>> getCodeClassMap(
            Set<Class<? extends Operator>> operators) {
        return operators.stream()
                .filter(c -> !java.lang.reflect.Modifier.isAbstract(c.getModifiers()))
                .collect(
                        Collectors.toMap(
                                t -> {
                                    Operator bcg = InstantiationUtil.instantiate(t);
                                    String code = bcg.getCode();
                                    bcg = null;
                                    return code;
                                },
                                t -> t));
    }

    /**
     * 创建场景代码上下文
     *
     * @param scene 场景类
     * @return 代码上下文
     */
    public CodeContext createCodeContext(Scene scene) {
        return CodeContext.newBuilder(scene.getEnvironment().getName()).scene(scene).build();
    }
}
