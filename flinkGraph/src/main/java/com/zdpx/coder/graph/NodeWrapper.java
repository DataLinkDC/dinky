package com.zdpx.coder.graph;

import com.zdpx.coder.operator.Operator;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class NodeWrapper {
    private String parameters;
    private Node parent;
    private List<Node> children = new ArrayList<>();
}
