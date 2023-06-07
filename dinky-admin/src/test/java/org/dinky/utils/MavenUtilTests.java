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

import org.dinky.function.util.UDFUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@Disabled("This is local test!")
public class MavenUtilTests {

    @Test
    @Ignore
    public void build() {
        MavenUtil.build(
                null,
                "/Users/zackyoung/companyProjects/api-monitor/pom.xml",
                "/usr/local/",
                "/Users/zackyoung/.m2/repository",
                "/Users/zackyoung/projects/ideaProjects/dlink/dinky-admin/tmp/git/build.log",
                Arrays.asList("package"),
                null,
                null);
    }

    @Test
    @Ignore
    public void cloneAndBuild() {
        String branch = "master";

        File gitFile = new File("/Users/zackyoung/Desktop/test/git/api-monitor/" + branch);
        String logFile = "/Users/zackyoung/Desktop/test/git/api-monitor/" + branch + "_log/3.log";
        File pom = new File(gitFile, "pom.xml");

        MavenUtil.build(
                "/Volumes/文档及虚拟机/company/数宜信/OpenApi/settings2.xml",
                pom.getAbsolutePath(),
                logFile,
                null);
    }

    @Test
    @Ignore
    public void getJars() {
        String branch = "master";

        File gitFile = new File("/Users/zackyoung/Desktop/test/git/api-monitor/" + branch);
        String logFile = "/Users/zackyoung/Desktop/test/git/api-monitor/" + branch + "_log/3.log";
        File pom = new File(gitFile, "pom.xml");

        List<File> jars = MavenUtil.getJars(pom);
        System.out.println(jars);
        jars.parallelStream()
                .forEach(
                        jar -> {
                            List<Class<?>> udfClassByJar = UDFUtil.getUdfClassByJar(jar);
                            System.out.println(udfClassByJar);
                        });
    }

    @Test
    @Ignore
    public void getVersion() {
        System.out.println(MavenUtil.getMavenVersion());
    }

    @Test
    @Ignore
    public void getSettings() {
        System.out.println(MavenUtil.getMavenSettingsPath());
    }
}
