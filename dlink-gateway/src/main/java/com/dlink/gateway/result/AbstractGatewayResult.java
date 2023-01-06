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

package com.dlink.gateway.result;

import com.dlink.gateway.GatewayType;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * AbstractGatewayResult
 *
 * @author wenmo
 * @since 2021/10/29 15:44
 **/
@Setter
@Getter
public abstract class AbstractGatewayResult implements GatewayResult {

    protected GatewayType type;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected boolean isSuccess;
    protected String exceptionMsg;

    public AbstractGatewayResult(GatewayType type, LocalDateTime startTime) {
        this.type = type;
        this.startTime = startTime;
    }

    public AbstractGatewayResult(LocalDateTime startTime, LocalDateTime endTime, boolean isSuccess, String exceptionMsg) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isSuccess = isSuccess;
        this.exceptionMsg = exceptionMsg;
    }

    public void success() {
        this.isSuccess = true;
        this.endTime = LocalDateTime.now();
    }

    public void fail(String error) {
        this.isSuccess = false;
        this.endTime = LocalDateTime.now();
        this.exceptionMsg = error;
    }

    @Override
    public boolean isSucess() {
        return isSuccess;
    }

    @Override
    public String getError() {
        return exceptionMsg;
    }
}
