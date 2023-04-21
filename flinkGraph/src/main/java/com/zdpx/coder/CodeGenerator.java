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

package com.zdpx.coder;

import javax.lang.model.element.Modifier;

import com.google.common.base.Strings;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import lombok.extern.slf4j.Slf4j;

/** generate java code file that define the scene structure and execution logic. */
@Slf4j
public class CodeGenerator {

    public static void main(String[] args) {
        CodeGenerator cg = new CodeGenerator();
        String result = cg.generate();
        log.debug(result);
    }

    public String generate() {
        return generate("Test");
    }

    public String generate(String sceneName) {
        if (!validSceneName(sceneName)) {
            String err = "scene name illegal.";
            log.error(err);
            throw new IllegalArgumentException(err);
        }

        MethodSpec main =
                MethodSpec.methodBuilder("main")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(void.class)
                        .addParameter(String[].class, "args")
                        .build();

        TypeSpec classBody =
                TypeSpec.classBuilder(sceneName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(main)
                        .build();

        JavaFile javaFile = JavaFile.builder(Specifications.COM_ZDPX_CJPG, classBody).build();

        return javaFile.toString();
    }

    public boolean validSceneName(String sceneName) {
        log.debug("Scene name {}", sceneName);
        return !Strings.isNullOrEmpty(sceneName);
    }
}
