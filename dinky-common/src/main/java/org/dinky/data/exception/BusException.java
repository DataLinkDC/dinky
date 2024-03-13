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

package org.dinky.data.exception;

import org.dinky.data.enums.Status;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Prompt exception, after the exception is caught,
 * it will look for internationalization prompts according to the code,
 * the web will pop up the corresponding prompt
 * Please do not use this exception for meaningless throwing
 *
 * @since 2021/5/28 14:21
 */
@Slf4j
@Getter
@Setter
public class BusException extends RuntimeException {

    private static final long serialVersionUID = -2955156471454043812L;

    /** 异常信息的code码 */
    private Status code;

    /** 异常信息的参数 */
    private Object[] errorArgs;

    /** 如果有值得话，将不会取i18n里面的错误信息 */
    private String msg;

    public BusException(String message) {
        super(message);
        setMsg(message);
    }

    public BusException(Status status) {
        super(status.getMessage());
        setCode(status);
        setMsg(status.getMessage());
    }

    public BusException(Status status, Object... errorArgs) {
        super(status.getMessage());
        setCode(status);
        setMsg(StrUtil.format(status.getMessage(), errorArgs));
    }

    /**
     * An exception that gets the error message through i 18n
     *
     * @param message code
     * @param e e
     * @return {@link BusException}
     */
    public static BusException valueOf(String message, Throwable e) {
        log.error(message, e);
        return new BusException(message + e.getMessage());
    }

    public static BusException valueOf(Status code, Throwable e) {
        log.error(code.getMessage(), e);
        return new BusException(code, e.getMessage());
    }

    /**
     * An exception that gets the error message through i 18n
     *
     * @param code code
     * @param errorArgs errorArgs
     * @return {@link BusException}
     */
    public static BusException valueOf(Status code, Object... errorArgs) {
        return new BusException(code, errorArgs);
    }

    /**
     * Without passing the exception to i 18n, it is directly returned to the msg past
     *
     * @param msg msg
     * @return {@link BusException}
     */
    public static BusException valueOf(String msg) {
        return new BusException(msg);
    }
}
