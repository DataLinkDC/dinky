/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.utils;

import org.apache.flink.runtime.rest.messages.MessageHeaders;
import org.apache.flink.runtime.rest.messages.ResponseBody;
import org.apache.flink.runtime.rest.util.RestMapperUtils;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JavaType;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;

public class FlinkJsonUtil {
    private static final ObjectMapper objectMapper = RestMapperUtils.getStrictObjectMapper();

    public static <T extends ResponseBody> T toBean(String json, MessageHeaders<?, T, ?> messageHeaders) {
        final JavaType responseType;

        final Collection<Class<?>> typeParameters = messageHeaders.getResponseTypeParameters();

        if (typeParameters.isEmpty()) {
            responseType = objectMapper.constructType(messageHeaders.getResponseClass());
        } else {
            responseType = objectMapper
                    .getTypeFactory()
                    .constructParametricType(
                            messageHeaders.getResponseClass(),
                            typeParameters.toArray(new Class<?>[typeParameters.size()]));
        }
        try {
            JsonParser jsonParser = objectMapper.treeAsTokens(objectMapper.readTree(json));
            JavaType javaType = objectMapper.constructType(responseType);
            return objectMapper.readValue(jsonParser, javaType);
        } catch (Exception e) {
            throw new RuntimeException("json to flink bean exception.", e);
        }
    }
}
