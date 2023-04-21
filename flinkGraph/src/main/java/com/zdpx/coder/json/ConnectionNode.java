package com.zdpx.coder.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 */
public class ConnectionNode {
    private String fromOp;
    private String fromPort;
    private String toOp;
    private String toPort;

    @JsonIgnore
    private OperatorNode from;
    @JsonIgnore
    private OperatorNode to;

    //region getter/setter

    public OperatorNode getFrom() {
        return from;
    }

    public void setFrom(OperatorNode from) {
        this.from = from;
    }

    public OperatorNode getTo() {
        return to;
    }

    public void setTo(OperatorNode to) {
        this.to = to;
    }

    public String getFromOp() {
        return fromOp;
    }

    public void setFromOp(String fromOp) {
        this.fromOp = fromOp;
    }

    public String getFromPort() {
        return fromPort;
    }

    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }

    public String getToOp() {
        return toOp;
    }

    public void setToOp(String toOp) {
        this.toOp = toOp;
    }

    public String getToPort() {
        return toPort;
    }

    public void setToPort(String toPort) {
        this.toPort = toPort;
    }

    //endregion
}
