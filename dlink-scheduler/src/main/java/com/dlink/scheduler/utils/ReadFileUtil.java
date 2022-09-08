package com.dlink.scheduler.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 郑文豪
 */
@Slf4j
public class ReadFileUtil {

    public static String taskDefinition(Map<String, Object> maps) {
        InputStream in = ReadFileUtil.class.getResourceAsStream("/json/taskDefinition.json");
        String readFile = readFile(in);
        if (maps == null || maps.isEmpty()) {
            return readFile;
        }
        return StrUtil.format(readFile, maps);
    }

    public static String taskRelation(Map<String, Object> maps) {
        InputStream in = ReadFileUtil.class.getResourceAsStream("/json/taskRelation.json");
        String readFile = readFile(in);
        if (maps == null || maps.isEmpty()) {
            return readFile;
        }
        return StrUtil.format(readFile, maps);
    }

    public static String createTaskDefinition(Map<String, Object> maps) {
        InputStream in = ReadFileUtil.class.getResourceAsStream("/json/createTaskDefinition.json");
        String readFile = readFile(in);
        if (maps == null || maps.isEmpty()) {
            return readFile;
        }
        return StrUtil.format(readFile, maps);
    }

    /**
     * 读取文件
     */
    public static String readFile(InputStream inputStream) {
        try {
            BufferedReader reader = IoUtil.getUtf8Reader(inputStream);
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            IoUtil.close(reader);
            return sb.toString();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}

