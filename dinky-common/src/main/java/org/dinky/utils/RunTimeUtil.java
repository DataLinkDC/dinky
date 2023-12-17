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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;

/**
 * RunTimeUtil
 *
 * @since 2021/12/11
 */
public class RunTimeUtil {

    public static void recovery(Object obj) {
        obj = null;
        System.gc();
    }

    /**
     * 处理命令，多行命令原样返回，单行命令拆分处理
     *
     * @param cmds 命令
     * @return 处理后的命令
     */
    public static String[] handleCmds(String... cmds) {
        if (ArrayUtil.isEmpty(cmds)) {
            return new String[] {};
        }

        // 单条命令的情况
        if (1 == cmds.length) {
            final String cmd = cmds[0];
            if (StrUtil.isBlank(cmd)) {
                return new String[] {};
            }
            cmds = cmdSplit(cmd);
        }
        return cmds;
    }

    /**
     * 命令分割，使用空格分割，考虑双引号和单引号的情况
     *
     * @param cmd 命令，如 git commit -m 'test commit'
     * @return 分割后的命令
     */
    public static String[] cmdSplit(String cmd) {
        final List<String> cmds = new ArrayList<>();

        final int length = cmd.length();
        final Stack<Character> stack = new Stack<>();
        boolean inWrap = false;
        final StrBuilder cache = StrUtil.strBuilder();

        char c;
        for (int i = 0; i < length; i++) {
            c = cmd.charAt(i);
            switch (c) {
                case CharUtil.SINGLE_QUOTE:
                case CharUtil.DOUBLE_QUOTES:
                    if (inWrap) {
                        if (c == stack.peek()) {
                            // 结束包装
                            stack.pop();
                            inWrap = false;
                        }
                        cache.append(c);
                    } else {
                        stack.push(c);
                        cache.append(c);
                        inWrap = true;
                    }
                    break;
                case CharUtil.SPACE:
                    if (inWrap) {
                        // 处于包装内
                        cache.append(c);
                    } else {
                        cmds.add(cache.toString());
                        cache.reset();
                    }
                    break;
                default:
                    cache.append(c);
                    break;
            }
        }

        if (cache.hasContent()) {
            cmds.add(cache.toString());
        }

        return cmds.toArray(new String[0]);
    }
}
