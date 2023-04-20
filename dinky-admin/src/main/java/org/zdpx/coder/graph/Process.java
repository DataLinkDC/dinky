package org.zdpx.coder.graph;

import org.zdpx.coder.json.Description;
import org.zdpx.coder.operator.TableInfo;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 过程类,相当于一个节点集合, 不参与计算图, 主要用于分组.
 */
public class Process {
    /**
     * 该过程在界面上是否可以展开
     */
    private boolean expanded;
    /**
     * 版本
     */
    private String version;

    /**
     * 包含的节点包裹类
     */
    private Set<OperatorWrapper> operatorWrappers = new LinkedHashSet<>();
    /**
     * 包含的节点间连接信息
     */
    private Set<Connection<TableInfo>> connects = new LinkedHashSet<>();
    /**
     * 仓储的描述信息, 图上的注释信息
     */
    private Set<Description> descriptions = new LinkedHashSet<>();

    //region getter/setter

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<OperatorWrapper> getOperators() {
        return operatorWrappers;
    }

    public void setOperators(Set<OperatorWrapper> operatorWrappers) {
        this.operatorWrappers = operatorWrappers;
    }

    public Set<Connection<TableInfo>> getConnects() {
        return connects;
    }

    public void setConnects(Set<Connection<TableInfo>> connects) {
        this.connects = connects;
    }

    public Set<Description> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Set<Description> descriptions) {
        this.descriptions = descriptions;
    }

    //endregion
}
