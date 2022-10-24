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

import scala.tools.nsc.GenericRunnerSettings;
import scala.tools.nsc.interpreter.IMain;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public class CustomStringScalaCompiler {
    private static IMain interpreter;

    public static IMain getInterpreter() {
        if (interpreter != null) {
            return interpreter;
        }
        GenericRunnerSettings settings = new GenericRunnerSettings((err) -> null);

        settings.usejavacp().tryToSetFromPropertyValue("true");
        settings.Yreploutdir().tryToSetFromPropertyValue(PathConstant.UDF_PATH);
        interpreter = new IMain(settings);
        return interpreter;
    }
}
