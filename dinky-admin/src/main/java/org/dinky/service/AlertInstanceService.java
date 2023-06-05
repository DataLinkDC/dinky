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

package org.dinky.service;

import org.dinky.alert.AlertResult;
import org.dinky.data.model.AlertInstance;
import org.dinky.data.result.Result;
import org.dinky.mybatis.service.ISuperService;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/** AlertInstanceService */
public interface AlertInstanceService extends ISuperService<AlertInstance> {

    /**
     * list all enabled alert instance
     *
     * @return {@link List<AlertInstance>}
     */
    List<AlertInstance> listEnabledAll();

    /**
     * test one alert instance
     *
     * @param alertInstance {@link AlertInstance}
     * @return {@link AlertResult}
     */
    AlertResult testAlert(AlertInstance alertInstance);

    /**
     * batch delete alert instance , this method is{@link Deprecated} , will be removed in the
     * future, please use {@link
     * com.baomidou.mybatisplus.core.mapper.BaseMapper#deleteById(Serializable id)} instead
     *
     * @param para {@link JsonNode}
     * @return {@link Result<Void>}
     */
    @Deprecated
    Result<Void> deleteAlertInstance(JsonNode para);

    /**
     * delete alert instance
     *
     * @param id {@link Integer}
     * @return {@link Result<Void>}
     */
    Boolean deleteAlertInstance(Integer id);

    /**
     * enable or disable alert instance
     *
     * @param id {@link Integer}
     * @return {@link Boolean}
     */
    Boolean enable(Integer id);
}
