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

package org.dinky.data.enums;

import java.util.Locale;
import java.util.Optional;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Status enum.
 *
 * <p><b>NOTE:</b> This enumeration is used to define status codes and internationalization messages
 * for response data. <br>
 * This is mainly responsible for the internationalization information returned by the interface
 */
public enum Status {
    // TODO:
    //  1. add more status codes and messages
    //  2. Move the internationalization information in the messages.properties file to here
    //  3. messages.properties is mainly responsible for writing the internationalization
    // information in the swagger document

    /** response data msg */
    SUCCESS(200, "Successfully", "获取成功"),
    FAILED(400, "Failed", "获取失败"),

    /** request && response message */
    INTERNAL_SERVER_ERROR_ARGS(7001, "Internal Server Error: {0}", "服务端异常: {0}"),
    REQUEST_PARAMS_NOT_VALID_ERROR(7002, "Request Parameter {0} Is Not Valid", "请求参数[{0}]无效"),

    /** interface CRUD operations */
    ADDED_SUCCESS(9001, "Added Successfully", "新增成功"),
    ADDED_FAILED(9002, "Added Failed", "新增失败"),
    UPDATE_SUCCESS(9003, "Update Successfully", "修改成功"),
    UPDATE_FAILED(9004, "Update Failed", "修改失败"),
    SAVE_ERROR(9005, "Save Failed", "保存失败"),
    SAVE_SUCCESS(9006, "Save Successfully", "保存成功"),
    DELETE_ERROR(9007, "Delete Failed", "删除失败"),
    DELETE_SUCCESS(9008, "Delete Successfully", "删除成功"),
    REFRESH_SUCCESS(9009, "Refresh Successfully", "刷新成功"),
    REFRESH_FAILED(9010, "Refresh Failed", "刷新失败"),
    QUERY_SUCCESS(9011, "Query Successfully", "查询成功"),
    QUERY_FAILED(9012, "Query Failed", "查询失败"),

    /** user,tenant,role */
    // user
    USER_NAME_EXIST(10001, "UserName Already Exists", "用户名已存在"),
    USER_NOT_EXIST(10002, "User Not Exist", "用户不存在"),
    USER_NAME_PASSWD_ERROR(10003, "UserName Or Password Not Correct", "用户名或密码不正确"),
    LOGIN_SUCCESS(10004, "Login Successfully", "登录成功"),
    USER_LOGIN_FAILURE(10005, "User Login Failure", "用户登录失败"),
    USER_NOT_LOGIN(10006, "User is Not Login", "用户未登录"),
    SIGN_OUT_SUCCESS(10007, "Sign Out Successfully", "退出成功"),
    USER_DISABLED_BY_ADMIN(10008, "The Current User is Disabled By Admin", "当前用户已被管理员停用"),
    // tenant
    TENANT_NAME_EXIST(10101, "Tenant Already Exists", "租户已存在"),
    TENANT_NAME_NOT_EXIST(10102, "Tenant Not Exists", "租户不存在"),
    // role
    ROLE_NAME_EXIST(10201, "Role Already Exists", "角色已存在"),
    ROLE_NOT_EXIST(10202, "Role Not Exists", "角色不存在"),

    /** database */
    DATASOURCE_EXIST(11001, "DataSource Name Already Exists", "数据源名称已存在"),
    DATASOURCE_CONNECT_FAILED(11002, "DataSource Connection Failed", "建立数据源连接失败"),
    DATASOURCE_HEARTBEAT_FAILED(11003, "DataSource HeartBeat Failed", "数据源心跳检测失败"),

    /** job or task about */
    JOB_RELEASE_DISABLED_UPDATE(
            12001, "Assignment Has Been Published, Modification is Prohibited", "作业已发布，禁止修改"),
    SCHEDULE_STATUS_UNKNOWN(12002, "Unknown Status: {0}", "未知状态: {0}"),
    TASK_NOT_EXIST(12003, "Task Not Exist", "任务不存在"),
    JOB_INSTANCE_NOT_EXIST(12004, "Job Instance Not Exist", "作业实例不存在"),
    SAVEPOINT_IS_NULL(12005, "Savepoint Is Null", "保存点为空"),
    ;

    private final int code;
    private final String enMsg;
    private final String zhMsg;

    Status(int code, String enMsg, String zhMsg) {
        this.code = code;
        this.enMsg = enMsg;
        this.zhMsg = zhMsg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        if (Locale.SIMPLIFIED_CHINESE
                .getLanguage()
                .equals(LocaleContextHolder.getLocale().getLanguage())) {
            return this.zhMsg;
        } else {
            return this.enMsg;
        }
    }

    /** Retrieve Status enum entity by status code. */
    public static Optional<Status> findStatusByCode(int code) {
        for (Status status : Status.values()) {
            if (code == status.getCode()) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
