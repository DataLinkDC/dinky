package com.dlink.explainer.ca;

import java.util.Objects;

/**
 * NodeRel
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class NodeRel {
    private Integer preId;
    private Integer sufId;

    public NodeRel(Integer preId, Integer sufId) {
        this.preId = preId;
        this.sufId = sufId;
    }

    public Integer getPreId() {
        return preId;
    }

    public void setPreId(Integer preId) {
        this.preId = preId;
    }

    public Integer getSufId() {
        return sufId;
    }

    public void setSufId(Integer sufId) {
        this.sufId = sufId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeRel nodeRel = (NodeRel) o;
        return Objects.equals(preId, nodeRel.preId) &&
                Objects.equals(sufId, nodeRel.sufId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(preId, sufId);
    }
}
