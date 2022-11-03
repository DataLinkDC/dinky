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

package com.dlink.function.exception;

import cn.hutool.core.util.StrUtil;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public class UDFCompilerException extends RuntimeException {

    public UDFCompilerException() {
    }

    public UDFCompilerException(String message) {
        super(message);
    }

    public static UDFCompilerException notSupportedException(String codeType) {
        return new UDFCompilerException(StrUtil.format("未知的代码类型：{}", codeType));
    }
}
