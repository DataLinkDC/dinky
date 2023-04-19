package org.dinky.zdpx.coder.graph;


import org.dinky.zdpx.coder.json.OperatorNode;
import org.dinky.zdpx.coder.operator.Operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 节点包裹类, 主要对应于{@link OperatorNode} 相关信息. 包含数据流逻辑无关的节点信息. 作为内部计算图(graph)与外部场景配置(json)的连接点.
 *
 * @author Licho Sun
 */
public class OperatorWrapper {
    /**
     * 节点并发度
     */
    private int parallelism;
    /**
     * 节点ID
     */
    private String id;
    /**
     * 节点代码, 目前使用类路径作为值
     */
    private String code;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 节点是否牌活动状态
     */
    private boolean activated;
    /**
     * 节点是否可展开
     */
    private boolean expanded;
    /**
     * 版本兼容性信息
     */
    private String compatibility;
    /**
     * 所在过程信息
     */
    private Process parentProcess;
    /**
     * 节点来源
     */
    private String origin;
    /**
     * 节点高度
     */
    private int height;
    /**
     * 节点宽度
     */
    private int width;
    /**
     * x坐标
     */
    private int x;
    /**
     * y坐标
     */
    private int y;
    private List<Process> processes = new ArrayList<>();
    private String parameters;

    private Operator operator;

    //region g/s

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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
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

    public Process getParentProcess() {
        return parentProcess;
    }

    public void setParentProcess(Process parentProcess) {
        this.parentProcess = parentProcess;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getCompatibility() {
        return compatibility;
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

//endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OperatorWrapper that = (OperatorWrapper) o;
        return id.equals(that.id) && code.equals(that.code) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name);
    }
}
