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

package org.dinky.mybatis.handler;

import org.dinky.mybatis.properties.MybatisPlusFillProperties;

import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * DateMeta Object Handler
 *
 * @since 2021/5/25
 */
@Slf4j
public class DateMetaObjectHandler implements MetaObjectHandler {

    private final MybatisPlusFillProperties mybatisPlusFillProperties;

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
        if (createTime == null) {
            setFieldValByName(mybatisPlusFillProperties.getCreateTimeField(), LocalDateTime.now(), metaObject);
        }
        if (updateTime == null) {
            setFieldValByName(mybatisPlusFillProperties.getUpdateTimeField(), LocalDateTime.now(), metaObject);
        }

        try {
            if (SpringMVCUtil.isWeb() && StpUtil.isLogin()) {
                int loginIdAsInt = StpUtil.getLoginIdAsInt();
                setFillFieldValue(metaObject, loginIdAsInt);
            }
        } catch (Exception e) {
            log.warn(
                    "Ignore set creater filed, because userId cant't get, Please check if your account is logged in normally or if it has been taken offline",
                    e);
        }
    }

    private void setFillFieldValue(MetaObject metaObject, int userId) {
        Object creator = getFieldValByName(mybatisPlusFillProperties.getCreatorField(), metaObject);
        Object updater = getFieldValByName(mybatisPlusFillProperties.getUpdaterField(), metaObject);
        Object operator = getFieldValByName(mybatisPlusFillProperties.getOperatorField(), metaObject);

        if (creator == null) {
            setFieldValByName(mybatisPlusFillProperties.getCreatorField(), userId, metaObject);
        }
        if (updater == null) {
            setFieldValByName(mybatisPlusFillProperties.getUpdaterField(), userId, metaObject);
        }
        if (operator == null) {
            setFieldValByName(mybatisPlusFillProperties.getOperatorField(), userId, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName(mybatisPlusFillProperties.getUpdateTimeField(), LocalDateTime.now(), metaObject);
        try {
            if (SpringMVCUtil.isWeb() && StpUtil.isLogin()) {
                int loginIdAsInt = StpUtil.getLoginIdAsInt();
                setFieldValByName(mybatisPlusFillProperties.getUpdaterField(), loginIdAsInt, metaObject);
                setFieldValByName(mybatisPlusFillProperties.getOperatorField(), loginIdAsInt, metaObject);
            }
        } catch (Exception e) {
            log.warn(
                    "Ignore set update,operator filed, because userId cant't get, Please check if your account is logged in normally or if it has been taken offline",
                    e);
        }
    }
}
