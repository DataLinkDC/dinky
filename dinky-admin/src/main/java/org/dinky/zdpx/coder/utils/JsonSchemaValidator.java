package org.dinky.zdpx.coder.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.SpecVersionDetector;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

/**
 * Json schema 校验类
 *
 * @author Licho Sun
 */
@Slf4j
public class JsonSchemaValidator {

    private final ObjectMapper mapper = new ObjectMapper();
    public static final SpecVersion.VersionFlag VERSION = SpecVersion.VersionFlag.V202012;
    private JsonSchema schema;

    public JsonNode getJsonNodeFromClasspath(String name) throws IOException {
        InputStream is1 = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(name);
        return mapper.readTree(is1);
    }

    public JsonNode getJsonNodeFromStringContent(String content) {
        try {
            return mapper.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("read json schema configure literal error.");
        }
        return null;
    }

    public JsonNode getJsonNodeFromUrl(String url) throws IOException {
        return mapper.readTree(new URL(url));
    }

    public JsonSchema getJsonSchemaFromClasspath(String name) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VERSION);
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(name);
        JsonSchema schemaLocal = factory.getSchema(is);
        setSchema(schemaLocal);
        return schema;
    }

    public JsonSchema getJsonSchemaFromStringContent(String schemaContent) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VERSION);
        JsonSchema schemaLocal = factory.getSchema(schemaContent);
        setSchema(schemaLocal);
        return schema;
    }

    public JsonSchema getJsonSchemaFromUrl(String uri) throws URISyntaxException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VERSION);
        JsonSchema schemaLocal = factory.getSchema(new URI(uri));
        setSchema(schemaLocal);
        return schema;
    }

    public JsonSchema getJsonSchemaFromJsonNode(JsonNode jsonNode) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VERSION);
        JsonSchema schemaLocal = factory.getSchema(jsonNode);
        setSchema(schemaLocal);
        return schema;
    }

    // Automatically detect version for given JsonNode
    public JsonSchema getJsonSchemaFromJsonNodeAutomaticVersion(JsonNode jsonNode) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(jsonNode));
        JsonSchema schemaLocal = factory.getSchema(jsonNode);
        setSchema(schemaLocal);
        return schema;
    }

    public Set<ValidationMessage> validate(String json) {
        JsonNode node = getJsonNodeFromStringContent(json);
        return validate(node);
    }

    public Set<ValidationMessage> validate(JsonNode node) {
        return schema.validate(node);
    }

    public void setSchema(JsonSchema schema) {
        this.schema = schema;
        schema.initializeValidators();
    }

    //region g/s
    public JsonSchema getSchema() {
        return schema;
    }
    //endregion
}
