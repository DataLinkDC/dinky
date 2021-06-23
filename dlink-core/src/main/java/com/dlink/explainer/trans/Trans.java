package com.dlink.explainer.trans;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Trans
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public interface Trans {

    String getHandle();

    boolean canHandle(String pact);

    void build(JsonNode node);

    void translate();

    String asSummaryString();

    Integer getId();

    List<Predecessor> getPredecessors();

    void setParentId(Integer parentId);

    Integer getParentId();

    String getPact();
}
