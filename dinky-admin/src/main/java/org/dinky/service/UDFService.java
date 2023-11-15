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

import org.dinky.data.model.Resources;
import org.dinky.data.model.udf.UDFManage;
import org.dinky.data.vo.UDFManageVO;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.IService;

public interface UDFService extends IService<UDFManage> {
    /**
     * update udf name by id
     * @param entity udf
     * @return boolean
     */
    boolean update(UDFManage entity);

    /**
     * get all udf
     * @return List
     */
    List<UDFManageVO> selectAll();

    /**
     * get udf by id
     * @return UDFManage
     */
    List<Resources> udfResourcesList();

    /**
     * add or update udf by resourceIds
     * @param resourceIds resourceIds
     */
    @Transactional(rollbackFor = Exception.class)
    void addOrUpdateByResourceId(List<Integer> resourceIds);
}
