package com.zdpx.coder.graph;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public abstract class NodeWrapper {
    private String parameters;
    private Node parent;
    private List<Node> children = new ArrayList<>();
}
