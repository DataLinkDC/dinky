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

package org.dinky.function.util;

import org.dinky.assertion.Asserts;
import org.dinky.classloader.DinkyClassLoader;
import org.dinky.config.Dialect;
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.model.FlinkUdfManifest;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.function.FunctionFactory;
import org.dinky.function.compiler.CustomStringJavaCompiler;
import org.dinky.function.compiler.CustomStringScalaCompiler;
import org.dinky.function.constant.PathConstant;
import org.dinky.function.data.model.UDF;
import org.dinky.function.pool.UdfCodePool;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.pool.ClassEntity;
import org.dinky.pool.ClassPool;

import org.apache.flink.client.python.PythonFunctionFactory;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.python.PythonOptions;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.catalog.FunctionLanguage;
import org.apache.flink.table.functions.UserDefinedFunction;
import org.apache.flink.table.functions.UserDefinedFunctionHelper;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.JarClassLoader;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.engine.freemarker.FreemarkerEngine;
import cn.hutool.json.JSONUtil;

/**
 * UDFUtil
 *
 * @since 2021/12/27 23:25
 */
public class UDFUtil {

    public static final String FUNCTION_SQL_REGEX =
            "^CREATE\\s+(?:(?:TEMPORARY|TEMPORARY\\s+SYSTEM)\\s+)?FUNCTION\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?(\\S+)\\s+AS\\s+'(\\S+)'\\s*(?:LANGUAGE\\s+(?:JAVA|SCALA|PYTHON)\\s*)?(?:USING\\s+JAR\\s+'(\\S+)'\\s*(?:,\\s*JAR\\s+'(\\S+)'\\s*)*)?";
    public static final Pattern PATTERN = Pattern.compile(FUNCTION_SQL_REGEX, Pattern.CASE_INSENSITIVE);

    public static final String SESSION = "SESSION";
    public static final String YARN = "YARN";
    public static final String APPLICATION = "APPLICATION";

    /**
     * 网关类型 map 快速获取 session 与 application 等类型，为了减少判断
     */
    public static final Map<String, List<GatewayType>> GATEWAY_TYPE_MAP = MapUtil.builder(
                    SESSION,
                    Arrays.asList(GatewayType.YARN_SESSION, GatewayType.KUBERNETES_SESSION, GatewayType.STANDALONE))
            .put(YARN, Arrays.asList(GatewayType.YARN_APPLICATION, GatewayType.YARN_PER_JOB))
            .put(APPLICATION, Arrays.asList(GatewayType.YARN_APPLICATION, GatewayType.KUBERNETES_APPLICATION))
            .build();

    protected static final Logger log = LoggerFactory.getLogger(UDFUtil.class);
    /**
     * 存放 udf md5与版本对应的k,v值
     */
    protected static final Map<String, Integer> UDF_MD5_MAP = new HashMap<>();

    public static final String PYTHON_UDF_ATTR = "(\\S+)\\s*=\\s*ud(?:f|tf|af|taf)";
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
                Dict data2 =
                        Dict.create().set("className", clazzName).set("attr", split.size() > 1 ? split.get(1) : null);
                return ENGINE.getTemplate(template).render(data2);
        }
    }

    public static String[] initJavaUDF(List<UDF> udf, GatewayType gatewayType, Integer missionId) {
        return FunctionFactory.initUDF(
                        CollUtil.newArrayList(
                                CollUtil.filterNew(udf, x -> x.getFunctionLanguage() != FunctionLanguage.PYTHON)),
                        missionId,
                        null)
                .getJarPaths();
    }

    public static String[] initPythonUDF(
            List<UDF> udf, GatewayType gatewayType, Integer missionId, Configuration configuration) {
        return FunctionFactory.initUDF(
                        CollUtil.newArrayList(
                                CollUtil.filterNew(udf, x -> x.getFunctionLanguage() == FunctionLanguage.PYTHON)),
                        missionId,
                        configuration)
                .getPyPaths();
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
                    log.info("class compile successful:{}", className);
                    log.info("compilerTakeTime：" + compiler.getCompilerTakeTime());
                    ClassPool.push(ClassEntity.build(className, udf.getCode()));
                    successList.add(className);
                } else {
                    log.warn("class compile failed:{}", className);
                    log.warn(compiler.getCompilerMessage());
                    failedList.add(className);
                }
            } else if (udf.getFunctionLanguage() == FunctionLanguage.SCALA) {
                String className = udf.getClassName();
                if (CustomStringScalaCompiler.getInterpreter(null).compileString(udf.getCode())) {
                    log.info("scala class compile successful:{}", className);
                    ClassPool.push(ClassEntity.build(className, udf.getCode()));
                    successList.add(className);
                } else {
                    log.warn("scala class compile failed:{}", className);
                    failedList.add(className);
                }
            }
        });

        String[] clazzs = successList.stream()
                .map(className -> StrUtil.replace(className, ".", "/") + ".class")
                .toArray(String[]::new);
        InputStream[] fileInputStreams = successList.stream()
                .map(className -> tmpPath + StrUtil.replace(className, ".", "/") + ".class")
                .map(FileUtil::getInputStream)
                .toArray(InputStream[]::new);

        // 编译好的文件打包jar
        try (ZipWriter zipWriter = new ZipWriter(FileUtil.file(udfJarPath), Charset.defaultCharset())) {
            zipWriter.add(clazzs, fileInputStreams);
        }
        String md5 = md5sum(udfJarPath);
        return MapUtil.builder("success", successList)
                .put("failed", failedList)
                .put("md5", Collections.singletonList(md5))
                .build();
    }

    /**
     * 得到udf版本和构建jar
     *
     * @param codeList 代码列表
     * @return {@link String}
     */
    @Deprecated
    public static String getUdfFileAndBuildJar(List<UDF> codeList) {
        // 1. 检查所有jar的版本，通常名字为 udf-${version}.jar;如 udf-1.jar,没有这个目录则跳过
        FileUtil.mkdir(PathConstant.UDF_PATH);

        try {
            // 获取所有的udf jar的 md5 值，放入 map 里面
            if (UDF_MD5_MAP.isEmpty()) {
                scanUDFMD5();
            }

            // 2. 如果有匹配的，返回对应udf 版本，没有则构建jar，对应信息写入 jar
            String md5 = buildJar(codeList).get("md5").get(0);
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
        fileList.stream()
                .filter(fileName -> ReUtil.isMatch(PathConstant.UDF_JAR_RULE, fileName))
                .distinct()
                .forEach(fileName -> {
                    Integer version = Convert.toInt(ReUtil.getGroup0(PathConstant.UDF_VERSION_RULE, fileName));
                    UDF_MD5_MAP.put(md5sum(PathConstant.UDF_PATH + fileName), version);
                });
    }

    private static String md5sum(String filePath) {
        return MD5.create().digestHex(FileUtil.file(filePath));
    }

    public static boolean isUdfStatement(Pattern pattern, String statement) {
        return !StrUtil.isBlank(statement) && CollUtil.isNotEmpty(ReUtil.findAll(pattern, statement, 0));
    }

    public static UDF toUDF(String statement, DinkyClassLoader classLoader) {
        if (isUdfStatement(PATTERN, statement)) {
            List<String> groups = CollUtil.removeEmpty(ReUtil.getAllGroups(PATTERN, statement));
            String udfName = groups.get(1);
            String className = groups.get(2);

            if (groups.size() > 3) {
                // if statement contains using jar, using these jars, not to lookup ClassLoaderUtil
                // pool
                return null;
            }

            FlinkUdfPathContextHolder udfPathContextHolder = classLoader.getUdfPathContextHolder();
            if (ClassLoaderUtil.isPresent(className)) {
                // 获取已经加载在java的类，对应的包路径
                try {
                    udfPathContextHolder.addUdfPath(FileUtil.file(classLoader
                            .loadClass(className)
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .getPath()));
                } catch (ClassNotFoundException e) {
                    throw new DinkyException(e);
                }
                return null;
            }

            UDF udf = UdfCodePool.getUDF(className);
            if (udf != null) {
                return UDF.builder()
                        .name(udfName)
                        .className(className)
                        .code(udf.getCode())
                        .functionLanguage(udf.getFunctionLanguage())
                        .build();
            }
            String gitPackage = UdfCodePool.getGitPackage(className);

            if (StrUtil.isNotBlank(gitPackage) && FileUtil.exist(gitPackage)) {
                if ("jar".equals(FileUtil.getSuffix(gitPackage))) {
                    udfPathContextHolder.addUdfPath(new File(gitPackage));
                } else {
                    udfPathContextHolder.addPyUdfPath(new File(gitPackage));
                }
            }
        }
        return null;
    }

    // create FlinkUdfPathContextHolder from UdfCodePool
    public static FlinkUdfPathContextHolder createFlinkUdfPathContextHolder() {
        FlinkUdfPathContextHolder udfPathContextHolder = new FlinkUdfPathContextHolder();
        UdfCodePool.getUdfCodePool().values().forEach(udf -> {
            if (udf.getFunctionLanguage() == FunctionLanguage.PYTHON) {
                udfPathContextHolder.addPyUdfPath(new File(udf.getCode()));
            } else {
                udfPathContextHolder.addUdfPath(new File(udf.getCode()));
            }
        });

        UdfCodePool.getGitPool().values().forEach(gitPackage -> {
            if (FileUtil.getSuffix(gitPackage).equals("jar")) {
                udfPathContextHolder.addUdfPath(new File(gitPackage));
            } else {
                udfPathContextHolder.addPyUdfPath(new File(gitPackage));
            }
        });
        return udfPathContextHolder;
    }

    public static List<Class<?>> getUdfClassByJar(File jarPath) {
        Assert.notNull(jarPath);

        List<Class<?>> classList = new ArrayList<>();
        try (JarClassLoader loader = new JarClassLoader()) {
            loader.addJar(jarPath);

            ClassScanner classScanner =
                    new ClassScanner("", aClass -> ClassUtil.isAssignable(UserDefinedFunction.class, aClass));
            classScanner.setClassLoader(loader);
            ReflectUtil.invoke(classScanner, "scanJar", new JarFile(jarPath));
            Set<Class<? extends UserDefinedFunction>> classes =
                    (Set<Class<? extends UserDefinedFunction>>) ReflectUtil.getFieldValue(classScanner, "classes");
            for (Class<? extends UserDefinedFunction> aClass : classes) {
                try {
                    UserDefinedFunctionHelper.validateClass(aClass);
                    classList.add(aClass);
                } catch (Exception ex) {
                    throw new DinkyException();
                }
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }

    public static List<String> getPythonUdfList(String udfFile) {
        return getPythonUdfList(SystemConfiguration.getInstances().getPythonHome(), udfFile);
    }

    private static final String PYTHON_FUNC_FILE_MD5 =
            MD5.create().digestHex(ResourceUtil.readUtf8Str("getPyFuncList.py"));

    public static List<String> getPythonUdfList(String pythonPath, String udfFile) {
        File checkFile = new File(PathConstant.TMP_PATH, "getPyFuncList.py");
        if (!checkFile.exists() || !MD5.create().digestHex(checkFile).equals(PYTHON_FUNC_FILE_MD5)) {
            FileUtil.writeUtf8String(ResourceUtil.readUtf8Str("getPyFuncList.py"), checkFile);
        }
        List<String> udfNameList = execPyAndGetUdfNameList(pythonPath, checkFile.getAbsolutePath(), udfFile);

        List<String> successUdfList = new ArrayList<>();
        if (CollUtil.isEmpty(udfNameList)) {
            return new ArrayList<>();
        }
        for (String udfName : udfNameList) {
            if (StrUtil.isBlank(udfName)) {
                continue;
            }
            Configuration configuration = new Configuration();
            configuration.set(PythonOptions.PYTHON_FILES, udfFile);
            configuration.set(PythonOptions.PYTHON_CLIENT_EXECUTABLE, pythonPath);
            configuration.set(PythonOptions.PYTHON_EXECUTABLE, pythonPath);
            try {
                PythonFunctionFactory.getPythonFunction(udfName, configuration, null);
                successUdfList.add(udfName);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return successUdfList;
    }

    private static List<String> execPyAndGetUdfNameList(String pyPath, String pyFile, String checkPyFile) {
        try {
            // 运行Python3脚本的命令，换成自己的即可
            String shell =
                    StrUtil.join(" ", Arrays.asList(Opt.ofBlankAble(pyPath).orElse("python3"), pyFile, checkPyFile));

            return StrUtil.split(StrUtil.trim(RuntimeUtil.execForStr(shell)), ",");
        } catch (Exception e) {
            throw new DinkyException(e);
        }
    }

    public static void addConfigurationClsAndJars(
            CustomTableEnvironment customTableEnvironment, List<URL> jarList, List<URL> classpaths) {
        customTableEnvironment.addConfiguration(
                PipelineOptions.CLASSPATHS,
                classpaths.stream().map(URL::toString).collect(Collectors.toList()));
        customTableEnvironment.addConfiguration(
                PipelineOptions.JARS, jarList.stream().map(URL::toString).collect(Collectors.toList()));
    }

    public static void writeManifest(
            Integer taskId, List<URL> jarPaths, FlinkUdfPathContextHolder udfPathContextHolder) {
        FlinkUdfManifest flinkUdfManifest = new FlinkUdfManifest();
        flinkUdfManifest.setJars(jarPaths);
        flinkUdfManifest.setPythonFiles(udfPathContextHolder.getPyUdfFile().stream()
                .map(URLUtil::getURL)
                .collect(Collectors.toList()));

        FileUtil.writeUtf8String(
                JSONUtil.toJsonStr(flinkUdfManifest),
                PathConstant.getUdfPackagePath(taskId) + PathConstant.DEP_MANIFEST);
    }
}
