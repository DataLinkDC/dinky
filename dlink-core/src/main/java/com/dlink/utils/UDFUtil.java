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

package com.dlink.utils;

import com.dlink.constant.PathConstant;
import com.dlink.pool.ClassEntity;
import com.dlink.pool.ClassPool;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;
import com.dlink.udf.UDF;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.catalog.FunctionLanguage;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import groovy.lang.GroovyClassLoader;

/**
 * UDFUtil
 *
 * @author wenmo
 * @since 2021/12/27 23:25
 */
public class UDFUtil {

    protected static final Logger log = LoggerFactory.getLogger(UDFUtil.class);
    /**
     * 存放 udf md5与版本对应的k,v值
     */
    protected static final Map<String, Integer> UDF_MD5_MAP = new HashMap<>();
    private static final String FUNCTION_REGEX = "function (.*?)'(.*?)'";
    private static final String LANGUAGE_REGEX = "language (.*);";
    public static final String PYTHON_UDF_ATTR = "(\\S)\\s+=\\s+ud(?:f|tf|af|taf)";

    public static List<UDF> getUDF(String statement) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("Parse UDF class name:");
        Pattern pattern = Pattern.compile(FUNCTION_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statement);
        List<UDF> udfList = new ArrayList<>();
        while (matcher.find()) {
            UDF udf = UDF.builder().className(matcher.group(2))
                .functionLanguage(FunctionLanguage.valueOf(Opt.ofNullable(ReUtil.getGroup1(PatternPool.get(LANGUAGE_REGEX, Pattern.CASE_INSENSITIVE), statement)).orElse("JAVA").toUpperCase()))
                .build();
            udfList.add(udf);
        }
        List<String> classNameList = udfList.stream().map(UDF::getClassName).collect(Collectors.toList());
        process.info(StringUtils.join(",", classNameList));
        process.info(StrUtil.format("A total of {} UDF have been Parsed.", classNameList.size()));
        return udfList;
    }

    public static String buildPy(List<UDF> udfList) {
        InputStream[] inputStreams = udfList.stream().map(x -> {
            String s = buildPy(x);
            return FileUtil.getInputStream(s);
        }).toArray(InputStream[]::new);

        String[] paths = udfList.stream().map(x -> StrUtil.split(x.getClassName(), ".").get(0) + ".py").toArray(String[]::new);
        File file = FileUtil.file(PathConstant.UDF_PYTHON_PATH + "python_udf.zip");
        FileUtil.del(file);
        try (ZipUtils zipWriter = new ZipUtils(file, Charset.defaultCharset())) {
            zipWriter.add(paths, inputStreams);
        }
        return file.getAbsolutePath();
    }

    public static String buildPy(UDF udf) {
        File file = FileUtil.writeUtf8String(udf.getCode(), PathConstant.UDF_PYTHON_PATH + StrUtil.split(udf.getClassName(), ".").get(0) + ".py");
        return file.getAbsolutePath();
    }

    public static Boolean buildClass(String code) {
        CustomStringJavaCompiler compiler = new CustomStringJavaCompiler(code);
        boolean res = compiler.compiler();
        String className = compiler.getFullClassName();
        if (res) {
            byte[] compiledBytes = compiler.getJavaFileObjectMap(className).getCompiledBytes();
            ClassPool.push(new ClassEntity(className, code, compiledBytes));
            log.info("class:{} 编译成功", className);
            log.info("compilerTakeTime：{}", compiler.getCompilerTakeTime());
            initClassLoader(className);
        } else {
            log.warn("class:{} 编译失败", className);
            log.warn(compiler.getCompilerMessage());
        }
        return res;
    }

    public static void initClassLoader(String name) {
        ClassEntity classEntity = ClassPool.get(name);
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(contextClassLoader, config);
        groovyClassLoader.setShouldRecompile(true);
        groovyClassLoader.defineClass(classEntity.getName(), classEntity.getClassByte());
        Thread.currentThread().setContextClassLoader(groovyClassLoader);
    }

    public static Map<String, List<String>> buildJar(List<String> codeList) {
        List<String> successList = new ArrayList<>();
        List<String> failedList = new ArrayList<>();
        String tmpPath = PathConstant.UDF_PATH;
        String udfJarPath = PathConstant.UDF_JAR_TMP_PATH;
        // 删除jar缓存
        FileUtil.del(udfJarPath);
        codeList.forEach(code -> {
            CustomStringJavaCompiler compiler = new CustomStringJavaCompiler(code);
            boolean res = compiler.compilerToTmpPath(tmpPath);
            String className = compiler.getFullClassName();
            if (res) {
                log.info("class编译成功:{}" + className);
                log.info("compilerTakeTime：" + compiler.getCompilerTakeTime());
                successList.add(className);
            } else {
                log.warn("class编译失败:{}" + className);
                log.warn(compiler.getCompilerMessage());
                failedList.add(className);
            }
        });
        String[] clazzs = successList.stream().map(className -> StrUtil.replace(className, ".", "/") + ".class")
            .toArray(String[]::new);
        InputStream[] fileInputStreams =
            successList.stream().map(className -> tmpPath + StrUtil.replace(className, ".", "/") + ".class")
                .map(FileUtil::getInputStream).toArray(InputStream[]::new);
        // 编译好的文件打包jar
        try (ZipUtils zipWriter = new ZipUtils(FileUtil.file(udfJarPath), Charset.defaultCharset())) {
            zipWriter.add(clazzs, fileInputStreams);
        }
        String md5 = md5sum(udfJarPath);
        return MapUtil.builder("success", successList).put("failed", failedList)
            .put("md5", Collections.singletonList(md5)).build();
    }

    /**
     * 得到udf版本和构建jar
     *
     * @param codeList 代码列表
     * @return {@link java.lang.String}
     */
    public static String getUdfFileAndBuildJar(List<String> codeList) {
        // 1. 检查所有jar的版本，通常名字为 udf-${version}.jar;如 udf-1.jar,没有这个目录则跳过
        String md5 = buildJar(codeList).get("md5").get(0);
        if (!FileUtil.exist(PathConstant.UDF_PATH)) {
            FileUtil.mkdir(PathConstant.UDF_PATH);
        }

        try {
            // 获取所有的udf jar的 md5 值，放入 map 里面
            if (UDF_MD5_MAP.isEmpty()) {
                scanUDFMD5();
            }
            // 2. 如果有匹配的，返回对应udf 版本，没有则构建jar，对应信息写入 jar
            if (UDF_MD5_MAP.containsKey(md5)) {
                FileUtil.del(PathConstant.UDF_JAR_TMP_PATH);
                return StrUtil.format("udf-{}.jar", UDF_MD5_MAP.get(md5));
            }
            // 3. 生成新版本jar
            Integer newVersion = UDF_MD5_MAP.values().size() > 0 ? CollUtil.max(UDF_MD5_MAP.values()) + 1 : 1;
            String jarName = StrUtil.format("udf-{}.jar", newVersion);
            String newName = PathConstant.UDF_PATH + jarName;
            FileUtil.rename(FileUtil.file(PathConstant.UDF_JAR_TMP_PATH), newName, true);
            UDF_MD5_MAP.put(md5, newVersion);
            return jarName;
        } catch (Exception e) {
            log.warn("builder jar failed! please check env. msg:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 扫描udf包文件，写入md5到 UDF_MD5_MAP
     */
    private static void scanUDFMD5() {
        List<String> fileList = FileUtil.listFileNames(PathConstant.UDF_PATH);
        fileList.stream().filter(fileName -> ReUtil.isMatch(PathConstant.UDF_JAR_RULE, fileName)).distinct()
            .forEach(fileName -> {
                Integer version = Convert.toInt(ReUtil.getGroup0(PathConstant.UDF_VERSION_RULE, fileName));
                UDF_MD5_MAP.put(md5sum(PathConstant.UDF_PATH + fileName), version);
            });
    }

    private static String md5sum(String filePath) {
        return MD5.create().digestHex(FileUtil.file(filePath));
    }

}
