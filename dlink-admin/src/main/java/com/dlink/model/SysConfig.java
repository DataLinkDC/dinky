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


package com.dlink.model;

import com.baomidou.mybatisplus.annotation.*;
import com.dlink.db.annotation.Save;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SysConfig
 *
 * @author wenmo
 * @since 2021/11/18
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_sys_config")
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 3769276772487490408L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "配置名不能为空", groups = {Save.class})
    private String name;

    private String value;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    protected Serializable pkVal() {
        return this.id;
    }


}
