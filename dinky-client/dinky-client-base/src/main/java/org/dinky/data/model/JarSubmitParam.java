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

package org.dinky.data.model;

import org.dinky.parser.SqlSegment;

import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JarSubmitParam {
    protected JarSubmitParam() {}

    private String uri;
    private String mainClass;
    private String args;
    private String parallelism;
    private String savepointPath;
    private Boolean allowNonRestoredState = SavepointConfigOptions.SAVEPOINT_IGNORE_UNCLAIMED_STATE.defaultValue();

    public static JarSubmitParam build(String statement) {
        JarSubmitParam submitParam = getInfo(statement);
        Assert.notBlank(submitParam.getUri());
        return submitParam;
    }

    public static JarSubmitParam getInfo(String statement) {
        statement = statement.replace("\r\n", " ").replace("\n", " ") + " ENDOFSQL";
        SqlSegment sqlSegment = new SqlSegment("with", "(with\\s+\\()(.+)(\\))", "',");
        sqlSegment.parse(statement);
        List<String> bodyPieces = sqlSegment.getBodyPieces();
        Map<String, String> keyValue = getKeyValue(bodyPieces);
        return BeanUtil.toBean(
                keyValue,
                JarSubmitParam.class,
                CopyOptions.create().setFieldNameEditor(s -> StrUtil.toCamelCase(s, '-')));
    }

    private static Map<String, String> getKeyValue(List<String> list) {
        Map<String, String> map = new HashMap<>();
        Pattern p = Pattern.compile("'(.*?)'\\s*=\\s*'(.*?)'");
        for (String s : list) {
            Matcher m = p.matcher(s + "'");
            if (m.find()) {
                map.put(m.group(1), m.group(2));
            }
        }
        return map;
    }

    public static JarSubmitParam empty() {
        JarSubmitParam jarSubmitParam = new JarSubmitParam();
        jarSubmitParam.setArgs("");
        jarSubmitParam.setMainClass("");
        jarSubmitParam.setUri("");
        return jarSubmitParam;
    }

    public String getArgs() {
        if (StrUtil.subPre(args, 7).equals("base64@")) {
            return Base64.decodeStr(StrUtil.removePrefix(args, "base64@"));
        }
        return args;
    }
}
