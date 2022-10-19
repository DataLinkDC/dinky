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

package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.exception.BusException;
import com.dlink.mapper.UDFTemplateMapper;
import com.dlink.model.UDFTemplate;
import com.dlink.service.UDFTemplateService;

import org.springframework.stereotype.Service;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
@Service
public class UDFTemplateServiceImpl extends SuperServiceImpl<UDFTemplateMapper, UDFTemplate> implements UDFTemplateService {

    @Override
    public boolean save(UDFTemplate udfTemplate) {
        UDFTemplate selectOne = query().eq("name", udfTemplate.getName()).one();
        if (udfTemplate.getId() == 0) {
            // 添加
            udfTemplate.setId(null);
            if ((selectOne != null)) {
                throw new BusException("模板名已经存在");
            }
        } else {
            // 修改
            if (selectOne == null) {
                return saveOrUpdate(udfTemplate);
            }
            if (!udfTemplate.getId().equals(selectOne.getId())) {
                throw new BusException("模板名已经存在");
            }
        }

        return saveOrUpdate(udfTemplate);
    }
}
