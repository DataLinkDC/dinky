package com.zdpx.coder.json.x6;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdpx.coder.graph.Scene;
import com.zdpx.coder.json.ToInternalConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class X6ToInternalConvert implements ToInternalConvert {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    @Override
    public Scene convert(String origin) {
        try {
            JsonNode x6 = objectMapper.readTree(origin);
            JsonNode cells = x6.path("cells");
            for (JsonNode cell: cells) {
                System.out.println(cell.toString());
            }

        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
