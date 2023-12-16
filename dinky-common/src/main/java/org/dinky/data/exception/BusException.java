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

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * BusException
 *
 * @since 2021/5/28 14:21
 */
@Slf4j
@Getter
@Setter
public class BusException extends RuntimeException {

    private static final long serialVersionUID = -2955156471454043812L;

    /** 异常信息的code码 */
    private String code;

    /** 如果有值得话，将不会取i18n里面的错误信息 */
    private String msg;

    /** 异常信息的参数 */
    private Object[] errorArgs;

    public BusException(String message) {
        super(message);
        setMsg(message);
    }

    public BusException(Status status) {
        super(status.getMessage());
        setCode(String.valueOf(status.getCode()));
        setMsg(status.getMessage());
    }

    public BusException(Status status, Object... errorArgs) {
        super(status.getMessage());
        setCode(String.valueOf(status.getCode()));
        setMsg(StrUtil.format(status.getMessage(), errorArgs));
    }

    public BusException(String message, Object... args) {
        super();
        setCode(message);
        setErrorArgs(args);
    }

    /**
     * An exception that gets the error message through i 18n
     *
     * @param code code
     * @param e e
     * @return {@link BusException}
     */
    public static BusException valueOf(String code, Throwable e) {
        String errMsg = ExceptionUtil.stacktraceToString(e);
        log.error(errMsg);
        return new BusException(code, errMsg);
    }

    /**
     * An exception that gets the error message through i 18n
     *
     * @param code code
     * @param errorArgs errorArgs
     * @return {@link BusException}
     */
    public static BusException valueOf(String code, Object... errorArgs) {
        return new BusException(code, errorArgs);
    }

    /**
     * Without passing the exception to i 18n, it is directly returned to the msg past
     *
     * @param msg msg
     * @return {@link BusException}
     */
    public static BusException valueOfMsg(String msg) {
        return new BusException(msg);
    }
}
