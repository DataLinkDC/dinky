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

package com.zdpx.coder.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 计算图参数集
 *
 * @author Licho Sun
 */
public class Parameters {
    private List<Parameter> parameterList = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <T> T getParameterByName(String key) {
        return getParameterList().stream()
                .filter(t -> Objects.equals(t.getKey(), key))
                .findAny()
                .map(value -> (T) value.getValue())
                .orElse(null);
    }

    public void addParameter(String name) {
        addParameter(new Parameter(name));
    }

    public void addParameter(Parameter parameter) {
        parameterList.add(parameter);
    }

    // region g/s
    public List<Parameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<Parameter> parameterList) {
        this.parameterList = parameterList;
    }
    // endregion
}
