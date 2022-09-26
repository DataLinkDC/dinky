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

import com.dlink.pool.ClassEntity;
import com.dlink.pool.ClassPool;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.control.CompilerConfiguration;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import groovy.lang.GroovyClassLoader;

/**
 * UDFUtil
 *
 * @author wenmo
 * @since 2021/12/27 23:25
 */
public class UDFUtil {

    public static void buildClass(String code) {
        CustomStringJavaCompiler compiler = new CustomStringJavaCompiler(code);
        boolean res = compiler.compiler();
        if (res) {
            String className = compiler.getFullClassName();
            byte[] compiledBytes = compiler.getJavaFileObjectMap(className).getCompiledBytes();
            ClassPool.push(new ClassEntity(className, code, compiledBytes));
            System.out.println("编译成功");
            System.out.println("compilerTakeTime：" + compiler.getCompilerTakeTime());
            initClassLoader(className);
        } else {
            System.out.println("编译失败");
            System.out.println(compiler.getCompilerMessage());
        }
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
        //Class<?> clazz = groovyClassLoader.parseClass(codeSource,"com.dlink.ud.udf.SubstringFunction");
    }

    public static Map<String, List<String>> buildJar(List<String> codeList) {
        List<String> successList = new ArrayList<>();
        List<String> failedList = new ArrayList<>();
        String tmpPath = System.getProperty("user.dir") + File.separator + "tmp/udf/";
        String udfJarPath = tmpPath + "udf.jar";
        //删除jar缓存
        FileUtil.del(udfJarPath);
        codeList.forEach(code -> {
            CustomStringJavaCompiler compiler = new CustomStringJavaCompiler(code);
            boolean res = compiler.compilerToTmpPath(tmpPath);
            String className = compiler.getFullClassName();
            if (res) {
                System.out.println("编译成功");
                System.out.println("compilerTakeTime：" + compiler.getCompilerTakeTime());
                successList.add(className);
            } else {
                System.out.println("编译失败");
                System.out.println(compiler.getCompilerMessage());
                failedList.add(className);
            }
        });
        String[] clazzs = successList.stream().map(className -> StrUtil.replace(className, ".", "/") + ".class").toArray(String[]::new);
        InputStream[] fileInputStreams = successList.stream().map(className -> tmpPath + StrUtil.replace(className, ".", "/") + ".class")
            .map(FileUtil::getInputStream).toArray(InputStream[]::new);
        // 编译好的文件打包jar
        ZipUtil.zip(FileUtil.file(udfJarPath), clazzs, fileInputStreams);
        return MapUtil.builder("success", successList).put("failed", failedList).build();
    }
}
