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

import org.dinky.data.model.SystemConfiguration;
import org.dinky.function.constant.PathConstant;
import org.dinky.process.exception.DinkyException;

import java.io.File;
import java.io.IOException;
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
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.engine.freemarker.FreemarkerEngine;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@Slf4j
public class MavenUtil {
    static final String javaExecutor =
            FileUtil.file(
                            FileUtil.file(SystemUtil.getJavaRuntimeInfo().getHomeDir())
                                    .getParentFile(),
                            "/bin/java")
                    .getAbsolutePath();

    //    /home/zackyoung/.jdks/corretto-1.8.0_372/bin/java
    // -Dmaven.multiModuleProjectDirectory=/home/zackyoung/IdeaProjects/dinky-quickstart-java
    // -Djansi.passthrough=true -Dmaven.home=/usr/share/maven
    // -Dclassworlds.conf=/usr/share/maven/bin/m2.conf   -Dfile.encoding=UTF-8 -classpath
    // /usr/share/maven/boot/plexus-classworlds-2.x.jar org.codehaus.classworlds.Launcher
    // --update-snapshots -s /home/zackyoung/settings2.xml
    // -Dmaven.repo.local=/home/zackyoung/.m2/repository -DskipTests=true package
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

        String localRepositoryDirectory;
        if (StrUtil.isBlank(setting)) {
            localRepositoryDirectory = Opt.ofBlankAble(repositoryDir).orElse("/usr/local/resp");
            // 设置仓库地址
        } else {
            localRepositoryDirectory = repositoryDir;
        }
        String mavenCommandLine =
                getMavenCommandLine(pom, mavenHome, localRepositoryDirectory, setting, goals, args);
        Opt.ofNullable(consumer)
                .ifPresent(c -> c.accept("Executing command: " + mavenCommandLine + "\n"));

        int waitValue =
                RuntimeUtils.run(
                        mavenCommandLine,
                        s -> {
                            s = DateUtil.date().toMsStr() + " - " + s + "\n";
                            consumer.accept(s);
                        },
                        log::error);
        return waitValue == 0;
    }

    /**
     * /usr/bin/java
     * -Dmaven.multiModuleProjectDirectory=/home/zackyoung/IdeaProjects/dinky-quickstart-java
     * -Djansi.passthrough=true -Dmaven.home=/usr/share/maven
     * -Dclassworlds.conf=/usr/share/maven/bin/m2.conf -Dfile.encoding=UTF-8 -classpath
     * /usr/share/maven/boot/plexus-classworlds-2.x.jar org.codehaus.classworlds.Launcher
     * --update-snapshots -s /home/zackyoung/settings2.xml
     * -Dmaven.repo.local=/home/zackyoung/.m2/repository -DskipTests=true package
     *
     * @return
     */
    public static String getMavenCommandLine(
            String projectDir,
            String mavenHome,
            String repositoryDir,
            String settingsPath,
            List<String> goals,
            List<String> args) {
        List<String> commandLine = new LinkedList<>();
        commandLine.add(javaExecutor);
        commandLine.add("-Dfile.encoding=UTF-8");
        commandLine.add("-Dmaven.multiModuleProjectDirectory=" + projectDir);
        commandLine.add("-Dmaven.home=" + mavenHome);
        Opt.ofBlankAble(repositoryDir)
                .ifPresent(x -> commandLine.add("-Dmaven.repo.local=" + repositoryDir));
        commandLine.add("-Dclassworlds.conf=" + mavenHome + "/bin/m2.conf");
        commandLine.add(
                "-classpath "
                        + mavenHome
                        + "/boot/plexus-classworlds-2.x.jar org.codehaus.classworlds.Launcher");
        commandLine.add("-s " + settingsPath);
        commandLine.add("-f " + projectDir);
        commandLine.add(StrUtil.join(" ", args));
        commandLine.add(StrUtil.join(" ", goals));
        return StrUtil.join(" ", commandLine);
    }

    public static String getMavenVersion() {
        return RuntimeUtil.execForStr(getMavenHome() + "/bin/mvn -v");
    }

    public static String getMavenHome() {
        String mavenHome = SystemUtil.get("MAVEN_HOME");
        if (StrUtil.isNotBlank(mavenHome)) {
            return mavenHome;
        }
        String searchCmd = SystemUtil.getOsInfo().isWindows() ? "where" : "which";
        mavenHome = RuntimeUtil.execForStr(searchCmd + " mvn").trim();
        if (StrUtil.isNotBlank(mavenHome)) {
            try {
                return new File(mavenHome).toPath().toRealPath().getParent().getParent().toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
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
                            .set("tmpDir", PathConstant.TMP_PATH)
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
