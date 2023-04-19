package org.dinky.zdpx.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.dinky.zdpx.coder.json.ResultType;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public class ResultTypeDeserializer extends StdDeserializer<ResultType> {
    public ResultTypeDeserializer() {
        super(ResultType.class);
    }

    @Override
    public ResultType deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String modeStr = node.asText().toUpperCase();
        return Arrays.stream(ResultType.values())
            .filter(mode -> mode.name().equals(modeStr))
            .findFirst()
            .orElse(null);
    }
}
