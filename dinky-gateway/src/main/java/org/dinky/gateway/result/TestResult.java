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

package org.dinky.gateway.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * TestResult
 *
 * @since 2021/11/27 16:12
 */
@ApiModel(value = "TestResult", description = "Result of a test operation")
public class TestResult {

    @ApiModelProperty(
            value = "Availability status",
            dataType = "boolean",
            example = "true",
            notes = "Indicates whether the test is available")
    private boolean isAvailable;

    @ApiModelProperty(
            value = "Error message",
            dataType = "String",
            example = "An error occurred",
            notes = "Error message if the test encountered an issue")
    private String error;

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getError() {
        return error;
    }

    public TestResult(boolean isAvailable, String error) {
        this.isAvailable = isAvailable;
        this.error = error;
    }

    public static TestResult success() {
        return new TestResult(true, null);
    }

    public static TestResult fail(String error) {
        return new TestResult(false, error);
    }
}
