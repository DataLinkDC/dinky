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

import org.apache.maven.shared.invoker.*;
import org.dinky.function.constant.PathConstant;
import org.dinky.model.SystemConfiguration;
import org.dinky.process.exception.DinkyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.engine.freemarker.FreemarkerEngine;
import cn.hutool.system.SystemUtil;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
public class MavenUtil {

    private static final List<String> COMMON_BIN_PATH = CollUtil.newArrayList("/usr", "/usr/local");
    private static final TemplateEngine ENGINE =
            new FreemarkerEngine(
                    new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));

    public static boolean build(String setting, String pom, String logFile, List<String> args) {
        return build(
                setting, pom, null, null, logFile, CollUtil.newArrayList("package"), args, null);
    }

    public static boolean build(
            String setting,
            String pom,
            String mavenHome,
            String repositoryDir,
            String logFile,
            List<String> goals,
            List<String> args,
            Consumer<String> consumer) {
        Assert.notBlank(pom, "the project pom file cannot be empty");

        // maven安装路径
        mavenHome = Opt.ofBlankAble(mavenHome).orElse(getMavenHome());
        Opt.ofBlankAble(mavenHome)
                .orElseThrow(
                        () -> new DinkyException("Please set the environment variable:MAVEN_HOME"));

        InvocationRequest request = new DefaultInvocationRequest();

        request.addArg("-DskipTests=true");
        Opt.ofEmptyAble(args).ifPresent(c -> CollUtil.removeBlank(args).forEach(request::addArg));
        if (StrUtil.isBlank(setting)) {
            repositoryDir = Opt.ofBlankAble(repositoryDir).orElse("/usr/local/resp");
            // 设置仓库地址
            request.setLocalRepositoryDirectory(new File(repositoryDir));
        } else {
            request.setGlobalSettingsFile(new File(setting));
            Opt.ofBlankAble(repositoryDir)
                    .ifPresent(x -> request.setLocalRepositoryDirectory(new File(x)));
        }

        // 设置pom文件路径
        request.setPomFile(new File(pom));
        // 执行的maven命令
        request.setGoals(goals);
        request.setMavenHome(new File(mavenHome));

        try {
            FileUtil.touch(logFile);
            // 日志处理
            PrintStream out = new PrintStream(logFile);
            PrintStreamHandler printStreamHandler =
                    new PrintStreamHandler(out, true) {
                        @Override
                        public void consumeLine(String line) {
                            // 2023-04-21 18:24:37.613 INFO  -
                            line = DateUtil.date().toMsStr() + " - " + line;
                            if (consumer != null) {
                                consumer.accept(line);
                            }
                            super.consumeLine(line);
                        }
                    };
            request.setOutputHandler(printStreamHandler);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        Invoker invoker = new DefaultInvoker();
        try {
            // 判断是否执行成功
            return invoker.execute(request).getExitCode() == 0;
        } catch (MavenInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getMavenVersion() {
        String mavenHome = getMavenHome();
        InvocationRequest request = new DefaultInvocationRequest();
        request.setMavenHome(new File(mavenHome));
        request.addArg("-v");

        List<String> msg = new LinkedList<>();
        List<String> errMsg = new LinkedList<>();
        request.setOutputHandler(msg::add);
        request.setErrorHandler(errMsg::add);

        Invoker invoker = new DefaultInvoker();

        try {
            int exitCode = invoker.execute(request).getExitCode();
            if (exitCode != 0) {
                throw new RuntimeException(String.join("\n", errMsg));
            }
        } catch (MavenInvocationException e) {
            throw new RuntimeException(e);
        }
        return StrUtil.join("\n", msg);
    }

    public static String getMavenHome() {
        String mavenHome = SystemUtil.get("MAVEN_HOME");
        if (StrUtil.isNotBlank(mavenHome)) {
            return mavenHome;
        }
        if (SystemUtil.getOsInfo().isLinux() || SystemUtil.getOsInfo().isMac()) {
            for (String path : COMMON_BIN_PATH) {
                if (FileUtil.exist(path + "/bin/mvn")) {
                    return path;
                }
            }
        }
        return null;
    }

    public static List<File> getJars(File pom) {
        List<File> jarFileList = new ArrayList<>();
        getJarList(pom, jarFileList);
        return jarFileList;
    }

    private static void getJarList(File pom, List<File> jarFileList) {
        File pomParentDir = FileUtil.getParent(pom, 1);
        File targetDir = new File(pomParentDir, "target");
        if (FileUtil.exist(targetDir) && FileUtil.isDirectory(targetDir)) {
            File[] buildFile = targetDir.listFiles();
            for (File build : buildFile) {
                if ("jar".equals(FileUtil.extName(build))) {
                    if (build.getName().length() > 13
                            && "original-".equals(build.getName().substring(0, 9))) {
                        continue;
                    }
                    jarFileList.add(build);
                }
            }
        }

        File[] files = FileUtil.file(pomParentDir).listFiles();
        for (File file : files) {
            File pomFile = new File(file, "pom.xml");
            if (file.isDirectory() && FileUtil.exist(pomFile)) {
                getJarList(pomFile, jarFileList);
            }
        }
    }

    public static String getMavenSettingsPath() {
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
        String mavenSettings = systemConfiguration.getMavenSettings();
        if (StrUtil.isNotBlank(mavenSettings) && !FileUtil.isFile(mavenSettings)) {
            throw new DinkyException("settings file is not exists,path: " + mavenSettings);
        } else if (StrUtil.isBlank(mavenSettings)) {
            Dict render =
                    Dict.create()
                            .set("repositoryUrl", systemConfiguration.getMavenRepository())
                            .set("repositoryUser", systemConfiguration.getMavenRepositoryUser())
                            .set(
                                    "repositoryPassword",
                                    systemConfiguration.getMavenRepositoryPassword());
            String content = ENGINE.getTemplate("settings.xml").render(render);
            File file =
                    FileUtil.writeUtf8String(
                            content,
                            FileUtil.file(PathConstant.TMP_PATH, "maven", "conf", "settings.xml"));
            return file.getAbsolutePath();
        }
        return mavenSettings;
    }
}
