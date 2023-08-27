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
                    new FileOutputStream(path + "message_en_US.properties"), StandardCharsets.UTF_8));
            BufferedWriter zhMsgFileWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path + "message_zh_CN.properties"), StandardCharsets.UTF_8));

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
