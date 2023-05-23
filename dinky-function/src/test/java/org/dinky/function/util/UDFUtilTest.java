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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dinky.process.exception.DinkyException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;

class UDFUtilTest {

    @Test
    void isUdfStatement() {
        Pattern pattern = Pattern.compile(UDFUtil.FUNCTION_SQL_REGEX, Pattern.CASE_INSENSITIVE);

        Function<String, Boolean> c = (s) -> UDFUtil.isUdfStatement(pattern, s);

        assertTrue(c.apply("create function a as 'abc'"));
        assertTrue(c.apply("create  function  a  as  'abc'  language python"));
        assertTrue(c.apply("create  function  a  as  'abc'  using jar 'path'"));
        assertTrue(c.apply("create  function  a  as  'abc'  using jar 'path', jar 'path/2'"));
        assertTrue(
                c.apply("create tempOrary function  a  as  'abc'  using jar 'path', jar 'path/2'"));
        assertTrue(
                c.apply(
                        "create tempOrary system function  a  as  'abc'  using jar 'path', jar 'path/2'"));
        assertTrue(
                c.apply(
                        "create tempOrary system function  a  as  'abc'  using jar 'path',jar 'path/2'"));
        assertFalse(
                c.apply(
                        " create tempOrary system function  a  as  'abc'  using jar 'path', jar 'path/2'"));
        assertFalse(
                c.apply(
                        "create tempOrary system function  a  as  abc  using jar 'path', jar 'path/2'"));
    }

    @Test
    @Ignore
    @Disabled("this is local test!")
    void pythonTest() throws ExecutionException {

        String pythonPath = "python";
        String udfFile = "C:\\project\\companyProjects\\dinky-quickstart-python\\udtf.py";
        List<String> pythonUdfList = UDFUtil.getPythonUdfList(pythonPath, udfFile);
    }

    private List<String> execPyAndGetUdfNameList(String pyPath, String pyFile, String checkPyFile) {
        Process process;
        try {
            // 运行Python3脚本的命令，换成自己的即可
            String shell =
                    StrUtil.join(
                            " ",
                            Arrays.asList(
                                    Opt.ofBlankAble(pyPath).orElse("python3"),
                                    pyFile,
                                    checkPyFile));
            process = Runtime.getRuntime().exec(shell);

            if (process.waitFor() != 0) {
                LineNumberReader lineNumberReader =
                        new LineNumberReader(new InputStreamReader(process.getErrorStream()));
                String errMsg = lineNumberReader.lines().collect(Collectors.joining("\n"));
                throw new DinkyException(errMsg);
            }

            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String result = input.readLine();
            input.close();
            ir.close();
            return StrUtil.split(result, ",");
        } catch (IOException | InterruptedException e) {
            throw new DinkyException(e);
        }
    }
}
