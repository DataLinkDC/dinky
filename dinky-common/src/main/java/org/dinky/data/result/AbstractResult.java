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

package org.dinky.data.result;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * AbstractResult
 *
 * @since 2021/6/29 22:49
 */
public class AbstractResult {

    protected boolean success;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected long time;
    protected String error;

    public void success() {
        this.setEndTime(LocalDateTime.now());
        this.setSuccess(true);
    }

    public void error(String error) {
        this.setEndTime(LocalDateTime.now());
        this.setSuccess(false);
        this.setError(error);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if (startTime != null && endTime != null) {
            Duration duration = java.time.Duration.between(startTime, endTime);
            time = duration.toMillis();
        }
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
