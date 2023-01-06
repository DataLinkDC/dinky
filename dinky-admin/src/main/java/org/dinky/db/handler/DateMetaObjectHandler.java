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

package com.dlink.db.handler;

import com.dlink.db.properties.MybatisPlusFillProperties;

import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

/**
 * DateMetaObjectHandler
 *
 * @author wenmo
 * @since 2021/5/25
 **/
public class DateMetaObjectHandler implements MetaObjectHandler {
    private MybatisPlusFillProperties mybatisPlusFillProperties;

    public DateMetaObjectHandler(MybatisPlusFillProperties mybatisPlusFillProperties) {
        this.mybatisPlusFillProperties = mybatisPlusFillProperties;
    }

    @Override
    public boolean openInsertFill() {
        return mybatisPlusFillProperties.getEnableInsertFill();
    }

    @Override
    public boolean openUpdateFill() {
        return mybatisPlusFillProperties.getEnableUpdateFill();
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        Object createTime = getFieldValByName(mybatisPlusFillProperties.getCreateTimeField(), metaObject);
        Object updateTime = getFieldValByName(mybatisPlusFillProperties.getUpdateTimeField(), metaObject);
        Object alias = getFieldValByName(mybatisPlusFillProperties.getAlias(), metaObject);
        Object name = getFieldValByName(mybatisPlusFillProperties.getName(), metaObject);
        if (createTime == null) {
            setFieldValByName(mybatisPlusFillProperties.getCreateTimeField(), LocalDateTime.now(), metaObject);
        }
        if (updateTime == null) {
            setFieldValByName(mybatisPlusFillProperties.getUpdateTimeField(), LocalDateTime.now(), metaObject);
        }
        if (alias == null) {
            setFieldValByName(mybatisPlusFillProperties.getAlias(), name, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName(mybatisPlusFillProperties.getUpdateTimeField(), LocalDateTime.now(), metaObject);
    }
}
