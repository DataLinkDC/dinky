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

import org.dinky.data.model.alert.AlertTemplate;
import org.dinky.mybatis.service.ISuperService;

public interface AlertTemplateService extends ISuperService<AlertTemplate> {

    /**
     * check alert template has relationship
     * @param id {@link Integer} alert template id
     * @return {@link Boolean} true: has relationship, false: no relationship
     */
    boolean hasRelationShip(Integer id);

    /**
     * remove alert template by id
     * @param id {@link Integer} alert template id
     * @return {@link Boolean} true: success, false: fail
     */
    boolean removeAlertTemplateById(Integer id);
}
