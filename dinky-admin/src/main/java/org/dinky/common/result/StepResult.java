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

package org.dinky.common.result;

import java.io.Serializable;

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
    /** message type (1-stepInfo 2-log) */
    private Integer type;

    private Integer currentStep;
    private Object data;
    /** 0-fail , 1-process , 2-success */
    private Integer status;

    public static StepResult getStepInfo(Integer currentStep, Object data) {
        return StepResult.builder()
                .type(1)
                .currentStep(currentStep)
                .data(JSONUtil.toJsonStr(data))
                .build();
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
