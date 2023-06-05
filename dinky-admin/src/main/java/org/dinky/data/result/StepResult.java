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

import java.io.Serializable;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
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
public class StepResult implements Serializable {

    private Integer currentStep;
    // 0.stepInfo 1.log 2.data 3.finish_info
    private Integer type;
    private Object data;
    /** 2-success 1-process 0-failed */
    private Integer status;

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

    public static StepResult genLog(
            Integer currentStep, Integer status, String log, Boolean history) {
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
                .data(JSONUtil.toJsonStr(data))
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
        return JSONUtil.toJsonStr(this);
    }
}
