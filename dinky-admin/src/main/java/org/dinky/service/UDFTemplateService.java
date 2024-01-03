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

import org.dinky.data.model.udf.UDFTemplate;
import org.dinky.mybatis.service.ISuperService;

public interface UDFTemplateService extends ISuperService<UDFTemplate> {

    /**
     * Save or update a UDF template.
     *
     * @param udfTemplate A {@link UDFTemplate} object to save or update.
     * @return A boolean value indicating whether the save or update operation was successful.
     */
    boolean saveOrUpdate(UDFTemplate udfTemplate);

    /**
     * Modify the status of a UDF template.
     *
     * @param id The ID of the UDF template to modify.
     * @return A {@link Boolean} value indicating whether the modification was successful.
     */
    Boolean modifyUDFTemplateStatus(Integer id);

    Boolean deleteUDFTemplateById(Integer id);

    Boolean hasRelationShip(Integer id);
}
