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

package com.dlink.process.exception;

import com.dlink.process.context.ProcessContextHolder;
import com.dlink.utils.LogUtil;

/**
 * @author ZackYoung
 * @since 0.7.0
 */
public class DinkyException extends RuntimeException {

    public DinkyException() {
    }

    public DinkyException(String message) {
        super(message);
        ProcessContextHolder.getProcess().error(message);
    }

    public DinkyException(String message, Throwable cause) {
        super(message, cause);
        ProcessContextHolder.getProcess().error(LogUtil.getError(cause));
    }

    public DinkyException(Throwable cause) {
        super(cause);
        ProcessContextHolder.getProcess().error(cause.toString());
    }

    public DinkyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        ProcessContextHolder.getProcess().error(LogUtil.getError(cause));
    }
}
