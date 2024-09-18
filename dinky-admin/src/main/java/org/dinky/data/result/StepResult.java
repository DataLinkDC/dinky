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

import org.dinky.utils.JsonUtils;

import java.io.Serializable;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "StepResult", description = "Result of a Step in a Process")
public class StepResult implements Serializable {

    @ApiModelProperty(value = "Current Step", dataType = "Integer", example = "1", notes = "The current step number")
    private Integer currentStep;

    @ApiModelProperty(
            value = "Type",
            dataType = "Integer",
            example = "0",
            notes = "0 for stepInfo, 1 for log, 2 for data, 3 for finish_info")
    private Integer type;

    @ApiModelProperty(
            value = "Data",
            dataType = "Object",
            example = "Step data goes here",
            notes = "The data associated with the step")
    private Object data;

    @ApiModelProperty(
            value = "Status",
            dataType = "Integer",
            example = "2",
            notes = "2 for success, 1 for process, 0 for failed")
    private Integer status;

    @ApiModelProperty(
            value = "History Flag",
            dataType = "Boolean",
            example = "false",
            notes = "Indicates whether this is a historical step result")
    private Boolean history;

    public static StepResult genHistoryLog(Integer currentStep, Integer status, String log) {
        return genLog(currentStep, status, log, true);
    }

    public static StepResult genFinishInfo(Integer currentStep, Integer status, Date date) {
        return StepResult.builder()
                .type(3)
                .currentStep(currentStep)
                .data(DateUtil.formatDateTime(date))
                .status(status)
                .history(false)
                .build();
    }

    public static StepResult genLog(Integer currentStep, Integer status, String log, Boolean history) {
        StepResult stepResult = new StepResult();
        stepResult.setHistory(history);
        stepResult.setData(log);
        stepResult.setType(1);
        stepResult.setCurrentStep(currentStep);
        stepResult.setStatus(status);
        return stepResult;
    }

    public static StepResult getStepInfo(Integer currentStep, Integer status, Object data) {
        return StepResult.builder()
                .type(0)
                .currentStep(currentStep)
                .data(JsonUtils.toJsonString(data))
                .status(status)
                .build();
    }

    public static StepResult getLog(Integer currentStep, Integer status, String data) {
        return StepResult.builder()
                .type(1)
                .currentStep(currentStep)
                .data(data)
                .status(status)
                .history(false)
                .build();
    }

    public static StepResult getData(Integer currentStep, Integer status, String data) {
        return StepResult.builder()
                .type(2)
                .currentStep(currentStep)
                .data(data)
                .status(status)
                .history(true)
                .build();
    }

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this);
    }
}
