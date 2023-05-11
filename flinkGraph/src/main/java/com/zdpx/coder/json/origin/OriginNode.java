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

package com.zdpx.coder.json.origin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.zdpx.coder.graph.NodeWrapper;
import com.zdpx.coder.graph.ProcessPackage;
import com.zdpx.coder.operator.Operator;

/**
 * 节点包裹类, 主要对应于{@link OperatorNode} 相关信息. 包含数据流逻辑无关的节点信息. 作为内部计算图(graph)与外部场景配置(json)的连接点.
 *
 * @author Licho Sun
 */
public class OriginNode extends NodeWrapper {
    /** 节点并发度 */
    private int parallelism;
    /** 节点ID */
    private String id;
    /** 节点代码, 目前使用类路径作为值 */
    private String code;
    /** 节点名称 */
    private String name;
    /** 所在过程信息 */
    private ProcessPackage parentProcessPackage;

    private List<ProcessPackage> processPackages = new ArrayList<>();

    private String parameters;

    private Operator operator;

    // region g/s

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessPackage getParentProcess() {
        return parentProcessPackage;
    }

    public void setParentProcess(ProcessPackage parentProcessPackage) {
        this.parentProcessPackage = parentProcessPackage;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public List<ProcessPackage> getProcesses() {
        return processPackages;
    }

    public void setProcesses(List<ProcessPackage> processPackages) {
        this.processPackages = processPackages;
    }

    // endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OriginNode that = (OriginNode) o;
        return id.equals(that.id) && code.equals(that.code) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name);
    }
}
