package com.zdpx.coder.json;

import com.zdpx.coder.graph.Scene;
import com.zdpx.coder.json.x6.X6ToInternalConvert;
import org.junit.jupiter.api.Test;

class X6ToInternalConvertTest {

    private final String x6_json = "" +
            "{\n" +
            "  \"cells\": [\n" +
            "    {\n" +
            "      \"position\": { \"x\": 40, \"y\": 40 },\n" +
            "      \"size\": { \"width\": 360, \"height\": 160 },\n" +
            "      \"attrs\": { \"text\": { \"text\": \"Parent\\n(try to move me)\" } },\n" +
            "      \"visible\": true,\n" +
            "      \"shape\": \"custom-group-node\",\n" +
            "      \"id\": \"da936e39-1c1f-4360-835d-ac1b76bf92a4\",\n" +
            "      \"zIndex\": 1,\n" +
            "      \"children\": [\n" +
            "        \"d582a458-ecb5-4099-848c-b806945860f5\",\n" +
            "        \"dfba19c2-3b46-4255-b524-93e26de9739d\",\n" +
            "        \"72359a19-9226-42b2-9c71-7809d090bf84\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"position\": { \"x\": 60, \"y\": 100 },\n" +
            "      \"size\": { \"width\": 100, \"height\": 40 },\n" +
            "      \"attrs\": { \"text\": { \"text\": \"Child\\n(inner)\" } },\n" +
            "      \"visible\": true,\n" +
            "      \"shape\": \"custom-group-node\",\n" +
            "      \"id\": \"d582a458-ecb5-4099-848c-b806945860f5\",\n" +
            "      \"zIndex\": 2,\n" +
            "      \"parent\": \"da936e39-1c1f-4360-835d-ac1b76bf92a4\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"position\": { \"x\": 420, \"y\": 80 },\n" +
            "      \"size\": { \"width\": 100, \"height\": 40 },\n" +
            "      \"attrs\": { \"text\": { \"text\": \"Child\\n(outer)\" } },\n" +
            "      \"visible\": true,\n" +
            "      \"shape\": \"custom-group-node\",\n" +
            "      \"id\": \"dfba19c2-3b46-4255-b524-93e26de9739d\",\n" +
            "      \"zIndex\": 2,\n" +
            "      \"parent\": \"da936e39-1c1f-4360-835d-ac1b76bf92a4\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"shape\": \"edge\",\n" +
            "      \"attrs\": { \"line\": { \"stroke\": \"#8f8f8f\", \"strokeWidth\": 1 } },\n" +
            "      \"id\": \"72359a19-9226-42b2-9c71-7809d090bf84\",\n" +
            "      \"source\": { \"cell\": \"d582a458-ecb5-4099-848c-b806945860f5\" },\n" +
            "      \"target\": { \"cell\": \"dfba19c2-3b46-4255-b524-93e26de9739d\" },\n" +
            "      \"vertices\": [\n" +
            "        { \"x\": 120, \"y\": 60 },\n" +
            "        { \"x\": 200, \"y\": 100 }\n" +
            "      ],\n" +
            "      \"zIndex\": 3,\n" +
            "      \"parent\": \"da936e39-1c1f-4360-835d-ac1b76bf92a4\"\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

    @Test
    void convert() {
        X6ToInternalConvert x6 = new X6ToInternalConvert();
        Scene result = x6.convert(x6_json);
    }
}
