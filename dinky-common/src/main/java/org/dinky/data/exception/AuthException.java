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

import java.text.MessageFormat;

import lombok.Data;

/**
 * AuthException About login error
 *
 * @since 2021/5/28 14:21
 */
@Data
public class AuthException extends Exception {

    private Status status;

    public AuthException(Status status) {
        super(status.getMessage());
        this.status = status;
    }

    public AuthException(Throwable cause, Status status) {
        super(status.getMessage(), cause);
        this.status = status;
    }

    public AuthException(Status status, Object... args) {
        super(MessageFormat.format(status.getMessage(), args));
        this.status = status;
    }
}
