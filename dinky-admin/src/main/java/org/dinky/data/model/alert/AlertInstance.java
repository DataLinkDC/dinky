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

package org.dinky.data.model.alert;

import org.dinky.data.typehandler.JSONObjectHandler;
import org.dinky.mybatis.model.SuperEntity;

import org.apache.ibatis.type.JdbcType;

import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AlertInstance
 *
 * @since 2022/2/24 19:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_alert_instance")
public class AlertInstance extends SuperEntity<AlertInstance> {

    private static final long serialVersionUID = -3435401513220527001L;

    @ApiModelProperty(value = "Tenant ID", required = true, dataType = "Integer", example = "1")
    private Integer tenantId;

    @ApiModelProperty(
            value = "Alert Instance Type",
            required = true,
            dataType = "String",
            example = "DingTalk",
            extensions = {
                @Extension(
                        name = "alertType-enum",
                        properties = {@ExtensionProperty(name = "values", value = "DingTalk,WeChat,Email,FeiShu,Sms")})
            })
    private String type;

    @ApiModelProperty(
            value = "Alert Instance Params",
            required = true,
            dataType = "String",
            example = "{\"webhook\":\"https://oapi.dingtalk.com/robot/send?access_token=xxxxxx\"}")
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = JSONObjectHandler.class)
    private Map<String, Object> params;
}
