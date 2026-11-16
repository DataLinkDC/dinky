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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UDFUtilTemplateRenderingTest {

    @Test
    void rendersProvidedTemplateData() {
        String javaTemplate = "${(package=='')?string('', 'package '+package+';')}\n"
                + "\n"
                + "import org.apache.flink.table.functions.ScalarFunction;\n"
                + "\n"
                + "public class ${className} extends ScalarFunction {\n"
                + "    public String eval(String s) {\n"
                + "        return null;\n"
                + "    }\n"
                + "}";

        String rendered = UDFUtil.templateParse("Java", javaTemplate, "com.example.MyUdf");

        assertTrue(rendered.contains("package com.example;"));
        assertTrue(rendered.contains("public class MyUdf extends ScalarFunction"));
    }

    @Test
    void rendersPythonTemplatePlaceholders() {
        String pythonTemplate = "class ${className}(ScalarFunction):\n"
                + "    def eval(self, variable):\n"
                + "        return str(variable)\n"
                + "\n"
                + "${attr!'f'} = udf(${className}(), result_type=DataTypes.STRING())";

        String rendered = UDFUtil.templateParse("Python", pythonTemplate, "MyPyUdf.my_attr");

        assertTrue(rendered.contains("class MyPyUdf(ScalarFunction):"));
        assertTrue(rendered.contains("my_attr = udf(MyPyUdf()"));
    }

    @Test
    void rejectsArbitraryTypeConstruction() {
        String template = "${'java.util.ArrayList'?new()}";
        assertThrows(Exception.class, () -> UDFUtil.templateParse("Java", template, "com.example.MyUdf"));
    }
}
