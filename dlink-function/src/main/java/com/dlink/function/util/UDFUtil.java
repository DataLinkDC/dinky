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

package com.dlink.function.util;

import com.dlink.assertion.Asserts;
import com.dlink.config.Dialect;
import com.dlink.function.FunctionFactory;
import com.dlink.function.compiler.CustomStringJavaCompiler;
import com.dlink.function.compiler.CustomStringScalaCompiler;
import com.dlink.function.constant.PathConstant;
import com.dlink.function.data.model.UDF;
import com.dlink.gateway.GatewayType;
import com.dlink.pool.ClassEntity;
import com.dlink.pool.ClassPool;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.catalog.FunctionLanguage;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.engine.freemarker.FreemarkerEngine;
import groovy.lang.GroovyClassLoader;

/**
 * UDFUtil
 *
 * @author wenmo
 * @since 2021/12/27 23:25
 */
public class UDFUtil {

    public static final String SESSION = "SESSION";
    public static final String YARN = "YARN";
    public static final String APPLICATION = "APPLICATION";

    /**
     * 网关类型 map
     * 快速获取 session 与 application 等类型，为了减少判断
     */
    public static final Map<String, List<GatewayType>> GATEWAY_TYPE_MAP = MapUtil
            .builder(SESSION,
                    Arrays.asList(GatewayType.YARN_SESSION, GatewayType.KUBERNETES_SESSION, GatewayType.STANDALONE))
            .put(YARN,
                    Arrays.asList(GatewayType.YARN_APPLICATION, GatewayType.YARN_PER_JOB))
            .put(APPLICATION,
                    Arrays.asList(GatewayType.YARN_APPLICATION, GatewayType.KUBERNETES_APPLICATION))
            .build();

    protected static final Logger log = LoggerFactory.getLogger(UDFUtil.class);
    /**
     * 存放 udf md5与版本对应的k,v值
     */
    protected static final Map<String, Integer> UDF_MD5_MAP = new HashMap<>();
    private static final String FUNCTION_REGEX = "function (.*?)'(.*?)'";
    private static final String LANGUAGE_REGEX = "language (.*);";
    public static final String PYTHON_UDF_ATTR = "(\\S)\\s+=\\s+ud(?:f|tf|af|taf)";
    public static final String PYTHON_UDF_DEF = "@ud(?:f|tf|af|taf).*\\n+def\\s+(.*)\\(.*\\):";
    public static final String SCALA_UDF_CLASS = "class\\s+(\\w+)(\\s*\\(.*\\)){0,1}\\s+extends";
    public static final String SCALA_UDF_PACKAGE = "package\\s+(.*);";
    private static final TemplateEngine ENGINE = new FreemarkerEngine(new TemplateConfig());

    /**
     * 模板解析
     *
     * @param dialect   方言
     * @param template  模板
     * @param className 类名
     * @return {@link String}
     */
    public static String templateParse(String dialect, String template, String className) {

        List<String> split = StrUtil.split(className, ".");
        switch (Dialect.get(dialect)) {
            case JAVA:
            case SCALA:
                String clazz = CollUtil.getLast(split);
                String packageName = StrUtil.strip(className, clazz);
                Dict data = Dict.create()
                        .set("className", clazz)
                        .set("package", Asserts.isNullString(packageName) ? "" : StrUtil.strip(packageName, "."));
                return ENGINE.getTemplate(template).render(data);
            case PYTHON:
            default:
                String clazzName = split.get(0);
                Dict data2 = Dict.create()
                        .set("className", clazzName)
                        .set("attr", split.size() > 1 ? split.get(1) : null);
                return ENGINE.getTemplate(template).render(data2);
        }
    }

    public static String[] initJavaUDF(List<UDF> udf, GatewayType gatewayType, Integer missionId) {
        return FunctionFactory.initUDF(
                CollUtil.newArrayList(CollUtil.filterNew(udf, x -> x.getFunctionLanguage() != FunctionLanguage.PYTHON)),
                missionId, null).getJarPaths();
    }

    public static String[] initPythonUDF(List<UDF> udf, GatewayType gatewayType, Integer missionId,
            Configuration configuration) {
        return FunctionFactory.initUDF(
                CollUtil.newArrayList(CollUtil.filterNew(udf, x -> x.getFunctionLanguage() == FunctionLanguage.PYTHON)),
                missionId, configuration).getPyPaths();
    }

    public static String getPyFileName(String className) {
        Asserts.checkNullString(className, "类名不能为空");
        return StrUtil.split(className, ".").get(0);
    }

    public static String getPyUDFAttr(String code) {
        return Opt.ofBlankAble(ReUtil.getGroup1(UDFUtil.PYTHON_UDF_ATTR, code))
                .orElse(ReUtil.getGroup1(UDFUtil.PYTHON_UDF_DEF, code));
    }

    public static String getScalaFullClassName(String code) {
        String packageName = ReUtil.getGroup1(UDFUtil.SCALA_UDF_PACKAGE, code);
        String clazz = ReUtil.getGroup1(UDFUtil.SCALA_UDF_CLASS, code);
        return String.join(".", Arrays.asList(packageName, clazz));
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

    @Deprecated
    public static Map<String, List<String>> buildJar(List<UDF> codeList) {
        List<String> successList = new ArrayList<>();
        List<String> failedList = new ArrayList<>();
        String tmpPath = PathConstant.UDF_PATH;
        String udfJarPath = PathConstant.UDF_JAR_TMP_PATH;
        // 删除jar缓存
        FileUtil.del(udfJarPath);
        codeList.forEach(udf -> {
            if (udf.getFunctionLanguage() == FunctionLanguage.JAVA) {
                CustomStringJavaCompiler compiler = new CustomStringJavaCompiler(udf.getCode());
                boolean res = compiler.compilerToTmpPath(tmpPath);
                String className = compiler.getFullClassName();
                if (res) {
                    log.info("class编译成功:{}" + className);
                    log.info("compilerTakeTime：" + compiler.getCompilerTakeTime());
                    ClassPool.push(ClassEntity.build(className, udf.getCode()));
                    successList.add(className);
                } else {
                    log.warn("class编译失败:{}" + className);
                    log.warn(compiler.getCompilerMessage());
                    failedList.add(className);
                }
            } else if (udf.getFunctionLanguage() == FunctionLanguage.SCALA) {
                String className = udf.getClassName();
                if (CustomStringScalaCompiler.getInterpreter(null).compileString(udf.getCode())) {
                    log.info("scala class编译成功:{}" + className);
                    ClassPool.push(ClassEntity.build(className, udf.getCode()));
                    successList.add(className);
                } else {
                    log.warn("scala class编译失败:{}" + className);
                    failedList.add(className);
                }
            }

        });
        String[] clazzs = successList.stream().map(className -> StrUtil.replace(className, ".", "/") + ".class")
                .toArray(String[]::new);
        InputStream[] fileInputStreams = successList.stream()
                .map(className -> tmpPath + StrUtil.replace(className, ".", "/") + ".class")
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
    @Deprecated
    public static String getUdfFileAndBuildJar(List<UDF> codeList) {
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
    @Deprecated
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
