package com.zdpx.coder.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 */
public class ProcessNode {
    private boolean expanded;
    private String version;
    private Set<OperatorNode> operators = new LinkedHashSet<>();
    private Set<ConnectionNode> connects = new LinkedHashSet<>();
    private Set<DescriptionNode> descriptions = new LinkedHashSet<>();
    @JsonIgnore
    private OperatorNode parent;
    //region getter/setter

    public OperatorNode getParent() {
        return parent;
    }

    public void setParent(OperatorNode parent) {
        this.parent = parent;
    }

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

    public Set<OperatorNode> getOperators() {
        return operators;
    }

    public void setOperators(Set<OperatorNode> operators) {
        this.operators = operators;
    }

    public Set<ConnectionNode> getConnects() {
        return connects;
    }

    public void setConnects(Set<ConnectionNode> connects) {
        this.connects = connects;
    }

    public Set<DescriptionNode> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Set<DescriptionNode> descriptions) {
        this.descriptions = descriptions;
    }

    //endregion
}
