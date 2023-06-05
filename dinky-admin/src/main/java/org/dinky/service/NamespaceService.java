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

import org.dinky.data.model.Namespace;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.mybatis.service.ISuperService;

import com.fasterxml.jackson.databind.JsonNode;

@Deprecated
public interface NamespaceService extends ISuperService<Namespace> {

    /**
     * delete namespace by id
     *
     * @param para namespace id
     * @return delete result code
     */
    Result<Void> deleteNamespaceById(JsonNode para);

    @Override
    ProTableResult<Namespace> selectForProTable(JsonNode para);
}
