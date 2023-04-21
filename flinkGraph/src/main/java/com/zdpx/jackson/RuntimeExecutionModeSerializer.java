package com.zdpx.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.flink.api.common.RuntimeExecutionMode;

import java.io.IOException;

/**
 *
 */
public class RuntimeExecutionModeSerializer extends StdSerializer<RuntimeExecutionMode> {
    protected RuntimeExecutionModeSerializer(Class t) {
        super(t);
    }

    protected RuntimeExecutionModeSerializer(JavaType type) {
        super(type);
    }

    @Override
    public void serialize(RuntimeExecutionMode value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name());
    }
}
