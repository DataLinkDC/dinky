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

package org.dinky.enums;

import java.util.Locale;
import java.util.Optional;

import org.springframework.context.i18n.LocaleContextHolder;

public enum Status {

    /** response data msg */
    SUCCESS(200, "success", "获取成功"),
    FAILED(400, "success", "获取失败"),

    /** request && response message */
    INTERNAL_SERVER_ERROR_ARGS(7001, "Internal Server Error: {0}", "服务端异常: {0}"),
    REQUEST_PARAMS_NOT_VALID_ERROR(7002, "request parameter {0} is not valid", "请求参数[{0}]无效"),

    /** base */
    NAME_NULL(8001, "name must be not null", "名称不能为空"),
    NAME_EXIST(8002, "name {0} already exists", "名称[{0}]已存在"),

    /** interface CRUD operations */
    ADDED_SUCCESS(9001, "added successfully", "新增成功"),
    ADDED_FAILED(9002, "added failed", "新增失败"),
    UPDATE_SUCCESS(9003, "update successfully", "修改成功"),
    UPDATE_FAILED(9004, "update failed", "修改失败"),
    SAVE_ERROR(9005, "save error", "保存失败"),
    SAVE_SUCCESS(9006, "save successfully", "保存成功"),
    DELETE_ERROR(9007, "delete error", "删除失败"),
    DELETE_SUCCESS(9008, "delete successfully", "删除成功"),

    /** user,tenant,role,namespace */
    USER_NAME_EXIST(10001, "user name already exists", "用户名已存在"),
    USER_NAME_NULL(10002, "user name is null", "用户名不能为空"),
    USER_NOT_EXIST(10003, "user {0} not exists", "用户[{0}]不存在"),
    ALERT_GROUP_NOT_EXIST(10004, "alarm group not found", "告警组不存在"),
    ALERT_GROUP_EXIST(10005, "alarm group already exists", "告警组名称已存在"),
    USER_NAME_PASSWD_ERROR(10006, "user name or password error", "用户名或密码不正确"),
    LOGIN_SESSION_FAILED(10007, "create session failed!", "创建session失败"),
    TENANT_NOT_EXIST(10008, "tenant not exists", "租户不存在"),
    LOGIN_SUCCESS(10009, "login success", "登录成功"),
    USER_LOGIN_FAILURE(10010, "user login failure", "用户登录失败"),

    USER_NOT_LOGIN(10011, "user is not login", "用户未登录"),

    CREATE_TENANT_ERROR(10012, "create tenant error", "创建租户失败"),
    UPDATE_TENANT_ERROR(10013, "update tenant error", "更新租户失败"),
    DELETE_TENANT_BY_ID_ERROR(10014, "delete tenant by id error", "删除租户失败"),
    CREATE_USER_ERROR(10015, "create user error", "创建用户失败"),
    UPDATE_USER_ERROR(10016, "update user error", "更新用户失败"),
    DELETE_USER_BY_ID_ERROR(10017, "delete user by id error", "删除用户失败"),
    VERIFY_USERNAME_ERROR(10018, "verify username error", "用户名验证失败"),
    SIGN_OUT_ERROR(10019, "sign out error", "退出失败"),
    SIGN_OUT_SUCCESS(10020, "sign out success", "退出成功"),
    USER_DISABLED(10021, "The current user is disabled", "当前用户已停用"),
    CURRENT_LOGIN_USER_TENANT_NOT_EXIST(
            10022, "the tenant of the currently login user is not specified", "未指定当前登录用户的租户"),
    NO_CURRENT_OPERATING_PERMISSION(
            10023, "The current user does not have this permission.", "当前用户无此权限"),

    /** database */
    DATASOURCE_EXIST(11001, "data source name already exists", "数据源名称已存在"),
    DATASOURCE_CONNECT_FAILED(11002, "data source connection failed", "建立数据源连接失败"),
    CREATE_DATASOURCE_ERROR(11003, "create datasource error", "创建数据源失败"),
    UPDATE_DATASOURCE_ERROR(11004, "update datasource error", "更新数据源失败"),
    QUERY_DATASOURCE_ERROR(11005, "query datasource error", "查询数据源失败"),
    CONNECT_DATASOURCE_FAILURE(11006, "connect datasource failure", "建立数据源连接失败"),
    CONNECTION_TEST_FAILURE(11007, "connection test failure", "测试数据源连接失败"),
    DELETE_DATA_SOURCE_FAILURE(11008, "delete data source failure", "删除数据源失败"),
    VERIFY_DATASOURCE_NAME_FAILURE(11009, "verify datasource name failure", "验证数据源名称失败"),
    UNAUTHORIZED_DATASOURCE(11010, "unauthorized datasource", "未经授权的数据源"),
    AUTHORIZED_DATA_SOURCE(11011, "authorized data source", "授权数据源失败"),

    /** job or task about */
    JOB_RELEASE_DISABLED_UPDATE(
            12001, "Assignment has been published, modification is prohibited", "作业已发布，禁止修改"),
    SCHEDULE_STATUS_UNKNOWN(12002, "unknown status: {0}", "未知状态: {0}"),
    TASK_NOT_EXIST(12003, "task not exist", "任务不存在"),
    JOB_INSTANCE_NOT_EXIST(12004, "job instance not exist", "作业实例不存在"),
    SAVEPOINT_IS_NULL(12005, "savepoint is null", "保存点为空"),

    /** alert group , alert instance */
    CREATE_ALERT_GROUP_ERROR(13001, "create alert group error", "创建告警组失败"),
    QUERY_ALL_ALERT_GROUP_ERROR(13002, "query all alert group error", "查询告警组失败"),
    UPDATE_ALERT_GROUP_ERROR(13003, "update alert group error", "更新告警组失败"),
    DELETE_ALERT_GROUP_ERROR(13004, "delete alert group error", "删除告警组失败"),
    ALERT_GROUP_GRANT_USER_ERROR(13005, "alert group grant user error", "告警组授权用户失败"),

    /** udf */
    CREATE_UDF_FUNCTION_ERROR(14001, "create udf function error", "创建UDF函数失败"),
    UPDATE_UDF_FUNCTION_ERROR(14002, "update udf function error", "更新UDF函数失败"),
    DELETE_UDF_FUNCTION_ERROR(14003, "delete udf function error", "删除UDF函数失败"),
    UDF_FUNCTION_NOT_EXIST(14004, "UDF function not found", "UDF函数不存在"),
    UDF_FUNCTION_EXISTS(14005, "UDF function already exists", "UDF函数已存在"),

    /** for monitor */
    QUERY_DATABASE_STATE_ERROR(15001, "query database state error", "查询数据库状态失败"),

    /** cluster */
    CREATE_CLUSTER_ERROR(16001, "create cluster error", "创建集群失败"),
    CLUSTER_NAME_EXISTS(16002, "this cluster name [{0}] already exists", "集群名称[{0}]已经存在"),
    CLUSTER_NAME_IS_NULL(16003, "this cluster name shouldn't be empty.", "集群名称不能为空"),
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
