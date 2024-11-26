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

import javax.annotation.Nullable;

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

    private static final long serialVersionUID = 1L;

    /** Exception status code */
    private Status code;

    /** Exception parameters */
    private Object[] errorArgs;

    /**
     * Constructs a BusException with the specified message.
     *
     * @param message the detail message
     */
    public BusException(String message) {
        super(message);
    }

    /**
     * Constructs a BusException with the specified status and error arguments.
     *
     * @param status the status code representing the exception
     * @param errorArgs the arguments used for error message formatting
     */
    public BusException(Status status, Object... errorArgs) {
        super(formatMessage(null, status, errorArgs));
        this.code = status;
        this.errorArgs = errorArgs;
    }

    /**
     * Constructs a BusException with the specified cause, status, and error arguments.
     *
     * @param cause the cause of the exception
     * @param status the status code representing the exception
     * @param errorArgs the arguments used for error message formatting
     */
    public BusException(Throwable cause, Status status, Object... errorArgs) {
        super(formatMessage(cause.getMessage(), status, errorArgs), cause);
        this.code = status;
        this.errorArgs = errorArgs;
    }

    /**
     * Creates a BusException instance with the specified message.
     *
     * @param message the detail message
     * @return a new BusException instance
     */
    public static BusException of(String message) {
        return new BusException(message);
    }

    /**
     * Creates a BusException instance with the specified status, and error arguments.
     *
     * @param status the status code representing the exception
     * @param errorArgs the arguments used for error message formatting
     * @return a new BusException instance
     */
    public static BusException of(Status status, Object... errorArgs) {
        return new BusException(status, errorArgs);
    }

    /**
     * Creates a BusException instance with the specified cause, status, and error arguments.
     *
     * @param cause the cause of the exception
     * @param status the status code representing the exception
     * @param errorArgs the arguments used for error message formatting
     * @return a new BusException instance
     */
    public static BusException of(Throwable cause, Status status, Object... errorArgs) {
        return new BusException(cause, status, errorArgs);
    }

    /** Formats the exception message with optional cause message and error arguments. */
    private static String formatMessage(@Nullable String causeMessage, Status status, Object... errorArgs) {
        Object[] args = errorArgs == null ? new Object[0] : errorArgs;

        if (causeMessage != null) {
            Object[] extendedArgs = new Object[args.length + 1];
            System.arraycopy(args, 0, extendedArgs, 0, args.length);
            extendedArgs[args.length] = causeMessage;
            args = extendedArgs;
        }

        return StrUtil.format(status.getMessage(), args);
    }
}
