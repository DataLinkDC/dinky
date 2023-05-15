package com.zdpx.coder.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdpx.coder.operator.Identifier;
import com.zdpx.coder.operator.Parameters;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class Node implements Identifier {
    protected String id;
    protected String name;
    protected Parameters parameters = new Parameters();
    protected NodeWrapper nodeWrapper;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeWrapper getNodeWrapper() {
        return nodeWrapper;
    }

    public void setNodeWrapper(NodeWrapper nodeWrapper) {
        this.nodeWrapper = nodeWrapper;
    }

    @Override
    public String getSpecification() {
        Map<String, Parameters> specification = new HashMap<>();
        specification.put(getName(),parameters);
        try {
            return mapper.writeValueAsString(specification);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
