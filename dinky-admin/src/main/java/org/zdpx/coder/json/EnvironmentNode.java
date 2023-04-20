package org.zdpx.coder.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.zdpx.jackson.ResultTypeDeserializer;
import org.zdpx.jackson.RuntimeExecutionModeDeserializer;

/**
 *
 */
public class EnvironmentNode {
    /**
     * flink 运行模式,(流式/批处理)
     */
    @JsonDeserialize(using = RuntimeExecutionModeDeserializer.class)
    private RuntimeExecutionMode mode;
    /**
     * 并发数量
     */
    private int parallelism;
    /**
     * 环境名称
     */
    private String name;
    /**
     * 生成结果类型sql/java
     */
    @JsonDeserialize(using = ResultTypeDeserializer.class)
    private ResultType resultType;

    public RuntimeExecutionMode getMode() {
        return mode;
    }

    //region getter/setter
    public void setMode(RuntimeExecutionMode mode) {
        this.mode = mode;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }
    //endregion
}
