package org.dinky.zdpx.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.flink.api.common.RuntimeExecutionMode;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public class RuntimeExecutionModeDeserializer extends StdDeserializer<RuntimeExecutionMode> {
    public RuntimeExecutionModeDeserializer() {
        super(RuntimeExecutionMode.class);
    }

    @Override
    public RuntimeExecutionMode deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String modeStr = node.asText().toUpperCase();
        return Arrays.stream(RuntimeExecutionMode.values())
            .filter(mode -> mode.name().equals(modeStr))
            .findFirst()
            .orElse(null);
    }
}
