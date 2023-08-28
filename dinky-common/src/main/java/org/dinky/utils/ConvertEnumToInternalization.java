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

import org.dinky.data.enums.Status;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConvertEnumToInternalization {
    public static void main(String[] args) {
        generateFiles(Status.values());
    }

    public static void generateFiles(Status[] statuses) {
        Map<String, String> codeMap = new HashMap<>();
        Map<String, String> enMsgMap = new HashMap<>();
        Map<String, String> zhMsgMap = new HashMap<>();

        for (Status status : statuses) {
            String key = status.name().toLowerCase().replace('_', '.');
            codeMap.put(key, String.valueOf(status.getCode()));
            enMsgMap.put(key, getFieldValue(status, "enMsg"));
            zhMsgMap.put(key, getFieldValue(status, "zhMsg"));
        }

        try {
            String path = "D:\\project\\dinky\\dinky-common\\src\\main\\resources\\";

            BufferedWriter codeFileWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path + "message_code.properties"), StandardCharsets.UTF_8));
            BufferedWriter enMsgFileWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path + "i18n/messages_en_US.properties"), StandardCharsets.UTF_8));
            BufferedWriter zhMsgFileWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path + "i18n/messages_zh_CN.properties"), StandardCharsets.UTF_8));

            for (Map.Entry<String, String> entry : codeMap.entrySet()) {
                codeFileWriter.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
            for (Map.Entry<String, String> entry : enMsgMap.entrySet()) {
                enMsgFileWriter.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
            for (Map.Entry<String, String> entry : zhMsgMap.entrySet()) {
                zhMsgFileWriter.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }

            codeFileWriter.close();
            enMsgFileWriter.close();
            zhMsgFileWriter.close();

            codeFileWriter.close();
            enMsgFileWriter.close();
            zhMsgFileWriter.close();

            System.out.println("Files generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFieldValue(Status status, String fieldName) {
        try {
            Field field = status.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (String) field.get(status);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
