package com.dlink.explainer.trans;

import com.dlink.assertion.Asserts;
import com.dlink.exception.SqlException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TransGenerator
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class TransGenerator {

    private ObjectNode plan;

    public TransGenerator(ObjectNode plan) {
        this.plan = plan;
    }

    public static Trans getTrans(String pact) {
        switch (pact) {
            case OperatorTrans.TRANS_TYPE:
                return new OperatorTrans();
            case SourceTrans.TRANS_TYPE:
                return new SourceTrans();
            case SinkTrans.TRANS_TYPE:
                return new SinkTrans();
            default:
                return null;
        }
    }

    public List<Trans> translateTrans() {
        JsonNode nodes = plan.get("nodes");
        List<Trans> nodeList = new ArrayList<>();
        Map<Integer, Trans> nodemap = new HashMap<>();
        for (JsonNode node : nodes) {
            String pact = node.get("pact").asText();
            Trans trans = getTrans(pact);
            Asserts.checkNotNull(trans,"该转换无法被解析，原文如下：" + pact);
            trans.build(node);
            nodemap.put(trans.getId(), trans);
        }
        setParentId(nodemap);
        for (Map.Entry<Integer, Trans> entry : nodemap.entrySet()) {
            nodeList.add(entry.getValue());
        }
        return nodeList;
    }

    private void setParentId(Map<Integer, Trans> nodemap) {
        for (Map.Entry<Integer, Trans> entry : nodemap.entrySet()) {
            Trans trans = entry.getValue();
            List<Predecessor> predecessors = trans.getPredecessors();
            if (Asserts.isNull(predecessors)) {
                continue;
            }
            for (int i = 0; i < predecessors.size(); i++) {
                nodemap.get(predecessors.get(i).getId()).setParentId(trans.getId());
            }
        }
    }
}

