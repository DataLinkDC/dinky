package com.dlink.utils;

import com.dlink.assertion.Asserts;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.fasterxml.jackson.databind.DeserializationFeature.*;
import static com.fasterxml.jackson.databind.MapperFeature.REQUIRE_SETTERS_FOR_GETTERS;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * JSONUtil
 *
 * @author wenmo
 * @since 2022/2/23 19:57
 **/
public class JSONUtil {
    private static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
            .configure(READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
            .configure(REQUIRE_SETTERS_FOR_GETTERS, true)
            .setTimeZone(TimeZone.getDefault());

    public static ArrayNode createArrayNode() {
        return objectMapper.createArrayNode();
    }

    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    public static JsonNode toJsonNode(Object obj) {
        return objectMapper.valueToTree(obj);
    }

    public static String toJsonString(Object object, SerializationFeature feature) {
        try {
            ObjectWriter writer = objectMapper.writer(feature);
            return writer.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("object to json exception!", e);
        }

        return null;
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        if (Asserts.isNullString(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.error("parse object exception!", e);
        }
        return null;
    }

    public static <T> T parseObject(byte[] src, Class<T> clazz) {
        if (src == null) {
            return null;
        }
        String json = new String(src, UTF_8);
        return parseObject(json, clazz);
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (Asserts.isNullString(json)) {
            return Collections.emptyList();
        }
        try {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
            return objectMapper.readValue(json, listType);
        } catch (Exception e) {
            logger.error("parse list exception!", e);
        }
        return Collections.emptyList();
    }

    public static Map<String, String> toMap(String json) {
        return parseObject(json, new TypeReference<Map<String, String>>() {
        });
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> classK, Class<V> classV) {
        return parseObject(json, new TypeReference<Map<K, V>>() {
        });
    }

    public static <T> T parseObject(String json, TypeReference<T> type) {
        if (Asserts.isNullString(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            logger.error("json to map exception!", e);
        }
        return null;
    }

    public static String toJsonString(Object object) {
        if (Asserts.isNull(object)) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Object json deserialization exception.", e);
        }
    }

    public static <T> byte[] toJsonByteArray(T obj) {
        if (obj == null) {
            return null;
        }
        String json = "";
        try {
            json = toJsonString(obj);
        } catch (Exception e) {
            logger.error("json serialize exception.", e);
        }
        return json.getBytes(UTF_8);
    }

    public static ObjectNode parseObject(String text) {
        try {
            if (text.isEmpty()) {
                return parseObject(text, ObjectNode.class);
            } else {
                return (ObjectNode) objectMapper.readTree(text);
            }
        } catch (Exception e) {
            throw new RuntimeException("String json deserialization exception.", e);
        }
    }

    public static ArrayNode parseArray(String text) {
        try {
            return (ArrayNode) objectMapper.readTree(text);
        } catch (Exception e) {
            throw new RuntimeException("Json deserialization exception.", e);
        }
    }

    public static class JsonDataDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            if (node instanceof TextNode) {
                return node.asText();
            } else {
                return node.toString();
            }
        }
    }
}
