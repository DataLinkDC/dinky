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

package com.zdpx.jackson;

import org.apache.flink.api.common.RuntimeExecutionMode;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/** */
public class RuntimeExecutionModeSerializer extends StdSerializer<RuntimeExecutionMode> {
    protected RuntimeExecutionModeSerializer(Class t) {
        super(t);
    }

    protected RuntimeExecutionModeSerializer(JavaType type) {
        super(type);
    }

    @Override
    public void serialize(
            RuntimeExecutionMode value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeString(value.name());
    }
}
