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
    REQUEST_PARAMS_ERROR(7003, "Request Parameter Error", "请求参数错误"),
    UNKNOWN_ERROR(7004, "Unknown Error: {0}", "未知异常: {0}"),

    /** interface CRUD operations */
    ADDED_SUCCESS(9001, "Added Successfully", "新增成功"),
    ADDED_FAILED(9002, "Added Failed", "新增失败"),
    MODIFY_SUCCESS(9003, "Update Successfully", "修改成功"),
    MODIFY_FAILED(9004, "Update Failed", "修改失败"),
    SAVE_FAILED(9005, "Save Failed", "保存失败"),
    SAVE_SUCCESS(9006, "Save Successfully", "保存成功"),
    DELETE_FAILED(9007, "Delete Failed", "删除失败"),
    DELETE_SUCCESS(9008, "Delete Successfully", "删除成功"),
    REFRESH_SUCCESS(9009, "Refresh Successfully", "刷新成功"),
    REFRESH_FAILED(9010, "Refresh Failed", "刷新失败"),
    QUERY_SUCCESS(9011, "Query Successfully", "查询成功"),
    QUERY_FAILED(9012, "Query Failed", "查询失败"),
    COPY_SUCCESS(9013, "Copy Successfully", "复制成功"),
    COPY_FAILED(9014, "Copy Failed", "复制失败"),
    CLEAR_SUCCESS(9015, "Clear Successfully", "清除成功"),
    CLEAR_FAILED(9016, "Clear Failed", "清除失败"),
    OPERATE_SUCCESS(9017, "Operate Successfully", "操作成功"),
    OPERATE_FAILED(9018, "Operate Failed", "操作失败"),
    EXECUTE_SUCCESS(9019, "Execute Successfully", "执行成功"),
    EXECUTE_FAILED(9020, "Execute Failed", "执行失败"),
    RESTART_SUCCESS(9021, "Restart Successfully", "重启成功"),
    RESTART_FAILED(9022, "Restart Failed", "重启失败"),
    // 已成功停止
    STOP_SUCCESS(9023, "Stop Successfully", "已成功停止"),
    STOP_FAILED(9024, "Stop Failed", "停止失败"),
    RENAME_SUCCESS(9025, "Rename Successfully", "重命名成功"),
    RENAME_FAILED(9026, "Rename Failed", "重命名失败"),
    MOVE_SUCCESS(9027, "Move Successfully", "移动成功"),
    MOVE_FAILED(9028, "Move Failed", "移动失败"),
    TEST_CONNECTION_SUCCESS(9029, "Test Connection Successfully", "测试连接成功"),
    TEST_CONNECTION_FAILED(9030, "Test Connection Failed", "测试连接失败"),

    /** user,tenant,role */
    // user
    USER_ALREADY_EXISTS(10001, "User Already Exists", "用户名已存在"),
    USER_NOT_EXIST(10002, "User Not Exist", "用户不存在"),
    USER_NAME_PASSWD_ERROR(10003, "UserName Or Password Not Correct", "用户名或密码不正确"),
    LOGIN_SUCCESS(10004, "Login Successfully", "登录成功"),
    LOGIN_FAILURE(10005, "User Login Failure", "用户登录失败"),
    USER_NOT_LOGIN(10006, "User is Not Login", "用户未登录"),
    SIGN_OUT_SUCCESS(10007, "Sign Out Successfully", "退出成功"),
    USER_DISABLED_BY_ADMIN(10008, "The Current User is Disabled By Admin", "当前用户已被管理员停用"),
    LOGIN_PASSWORD_NOT_NULL(10009, "Login Password Not Null", "登录密码不能为空"),
    USER_NOT_BINDING_TENANT(10010, "User Not Binding Tenant", "用户未绑定租户"),
    USER_OLD_PASSWORD_INCORRECT(10011, "User Old Password Incorrect", "用户旧密码不正确"),
    CHANGE_PASSWORD_SUCCESS(10012, "Change Password Success", "修改密码成功"),
    CHANGE_PASSWORD_FAILED(10013, "Change Password Failed", "修改密码失败"),
    USER_ASSIGN_ROLE_SUCCESS(10014, "User Assign Role Success", "用户分配角色成功"),
    USER_BINDING_ROLE_DELETE_ALL(10015, "User Binding Role Delete All", "用户绑定角色删除所有"),
    USER_ASSIGN_ROLE_FAILED(10016, "User Assign Role Failed", "用户分配角色失败"),
    GET_TENANT_FAILED(10017, "Get Tenant Info Failed", "获取租户信息失败"),
    SWITCHING_TENANT_SUCCESS(10018, "Select Tenant Success", "选择租户成功"),
    // user.superadmin.cannot.disable
    USER_SUPERADMIN_CANNOT_DISABLE(10019, "User SuperAdmin Cannot Disable", "超级管理员用户不能停用"),

    // role
    ROLE_ALREADY_EXISTS(10101, "Role Already Exists", "角色已存在"),
    ROLE_BINDING_USER(10112, "Role Already Binding User , Can Not Delete", "该角色已绑定用户，无法删除"),
    // 该角色已绑定行权限，无法删除
    ROLE_BINDING_ROW_PERMISSION(
            10113, "Role Already Binding Row Permission , Can Not Delete", "该角色已绑定行权限，无法删除"),

    // tenant
    TENANT_ALREADY_EXISTS(10201, "Tenant Already Exists", "租户已存在"),
    TENANT_NOT_EXIST(10202, "Tenant Not Exist", "租户不存在"),
    TENANT_BINDING_USER(10203, "Tenant Binding User , Can Not Delete", "删除租户失败，该租户已绑定用户"),
    TENANT_ASSIGN_USER_SUCCESS(10204, "Tenant Assign User Success", "分配用户成功"),
    TENANT_ASSIGN_USER_FAILED(10205, "Tenant Assign User Failed", "分配用户失败"),
    TENANT_BINDING_USER_DELETE_ALL(10206, "Tenant Binding User Delete All", "该租户绑定的用户已被全部删除"),

    // tenant
    TENANT_NAME_EXIST(10101, "Tenant Already Exists", "租户已存在"),
    TENANT_NAME_NOT_EXIST(10102, "Tenant Not Exists", "租户不存在"),
    // role
    ROLE_NAME_EXIST(10201, "Role Already Exists", "角色已存在"),
    ROLE_NOT_EXIST(10202, "Role Not Exists", "角色不存在"),

    /** database */
    DATASOURCE_CONNECT_SUCCESS(11001, "DataSource Connect Success", "数据源连接测试成功"),
    // 状态刷新完成
    DATASOURCE_STATUS_REFRESH_SUCCESS(11002, "DataSource Status Refresh Success", "数据源状态刷新成功"),

    // 该数据源不存在
    DATASOURCE_NOT_EXIST(11003, "DataSource Not Exist", "数据源不存在"),
    // 数据源连接正常
    DATASOURCE_CONNECT_NORMAL(11004, "DataSource Connect Normal", "数据源连接正常"),
    // 清除库表缓存
    DATASOURCE_CLEAR_CACHE_SUCCESS(11005, "DataSource Clear Cache Success", "清除库表缓存成功"),

    /** job or task about */
    JOB_RELEASE_DISABLED_UPDATE(
            12001, "Assignment Has Been Published, Modification is Prohibited", "作业已发布，禁止修改"),
    SCHEDULE_STATUS_UNKNOWN(12002, "Unknown Status: {0}", "未知状态: {0}"),
    TASK_NOT_EXIST(12003, "Task Not Exist", "任务不存在"),
    JOB_INSTANCE_NOT_EXIST(12004, "Job Instance Not Exist", "作业实例不存在"),
    SAVEPOINT_IS_NULL(12005, "Savepoint Is Null", "保存点为空"),

    /** alert instance */
    SEND_TEST_SUCCESS(13001, "Test Msg Send Success", "测试信息发送成功"),
    SEND_TEST_FAILED(13002, "Test Msg Send Fail", "测试信息发送失败"),
    TEST_MSG_TITLE(13003, "Real Time alarm mertics", "实时告警监控"),
    TEST_MSG_JOB_NAME(13004, "Job of Test", "测试任务"),
    TEST_MSG_JOB_URL(13005, "Jump to the task ", "跳转至该任务"),
    TEST_MSG_JOB_LOG_URL(13006, "Click to view the exception log for this task", "点击查看该任务的异常日志"),
    TEST_MSG_JOB_NAME_TITLE(13007, "Task", "任务"),

    /** alert group */
    ALERT_GROUP_EXIST(14001, "Alert Group Already Exists", "告警组已存在"),

    /** cluster instance */
    CLUSTER_INSTANCE_HEARTBEAT_SUCCESS(15001, "Cluster Instance Heartbeat Success", "集群实例心跳成功"),
    CLUSTER_INSTANCE_RECYCLE_SUCCESS(15002, "Recycle Success", "回收成功"),
    CLUSTER_INSTANCE_KILL(15003, "Kill Success", "已杀死该进程/集群"),
    CLUSTER_INSTANCE_DEPLOY(15004, "Deploy Success", "部署完成"),

    /** git */
    GIT_PROJECT_NOT_FOUND(16001, "Git Project Not Found", "获取不到项目信息"),
    GIT_SORT_FAILED(16002, "Git Sort Failed", "排序失败"),
    GIT_SORT_SUCCESS(16003, "Git Sort Success", "排序成功"),
    GIT_BRANCH_NOT_FOUND(16003, "Git Branch Not Found", "获取不到分支信息"),
    GIT_BUILDING(16004, "Git Building", "此任务正在构建"),
    GIT_BUILD_SUCCESS(16005, "Git Build Success", "构建成功"),

    /** dolphin scheduler */
    // 节点获取失败
    DS_GET_NODE_LIST_ERROR(17001, "Get Node List Error", "节点获取失败"),
    // 请先工作流保存
    DS_WORK_FLOW_NOT_SAVE(17002, "Please Save Workflow First", "请先保存工作流"),
    // 添加工作流定义成功
    DS_ADD_WORK_FLOW_DEFINITION_SUCCESS(17003, "Add Workflow Definition Success", "添加工作流定义成功"),
    DS_WORK_FLOW_DEFINITION_ONLINE(
            17004, "Workflow Definition [{}] Has Been Online", "工作流定义 [{}] 已经上线"),
    DS_WORK_FLOW_DEFINITION_TASK_NAME_EXIST(
            17005,
            "Add Failed, Workflow Definition [{}] Already Exists Task Definition [{}] Please Refresh",
            "添加失败,工作流定义 [{}] 已存在任务定义 [{}] 请刷新"),
    DS_ADD_TASK_DEFINITION_SUCCESS(17006, "Add Task Definition Success", "添加任务定义成功"),
    DS_TASK_NOT_EXIST(17007, "Task Not Exist", "任务不存在"),
    DS_TASK_TYPE_NOT_SUPPORT(
            17008,
            "DolphinScheduler Type Is [{}] Not Support, Not DINKY Type",
            "海豚调度类型为 [{}] 不支持,非DINKY类型"),
    DS_WORK_FLOW_DEFINITION_NOT_EXIST(17009, "Workflow Definition Not Exist", "工作流定义不存在"),

    /** LDAP About * */
    LDAP_USER_DUPLICAT(18001, "The ldap matches to multiple user data", "ldap匹配到多个用户数据"),
    LDAP_USER_AUTOLOAD_FORBAID(
            18002,
            "Auto-mapping LDAP users are not enabled, please contact your administrator to import",
            "未开启自动映射LDAP用户，请联系管理员导入"),
    LDAP_DEFAULT_TENANT_NOFOUND(18003, "The LDAP default tenant does not exist", "LDAP默认租户不存在"),
    LDAP_USER_INCORRECT(18004, "The LDAP user name (DN) Incorrect", "LDAP用户名（DN）不正确"),
    LDAP_NO_USER_FOUND(
            18005,
            "The LDAP connection was successful, but it did not match to any users",
            "LDAP连接成功，但未匹配到任何用户"),
    LDAP_FILTER_INCORRECT(
            18006,
            "If the user filter rule cannot be empty, enter the relevant configuration",
            "用户过滤规则不能为空，请填写相关配置"),

    LDAP_LOGIN_FORBID(
            18007,
            "If the current user login mode is not LDAP, contact the administrator to modify it, or do not use LDAP to log in",
            "当前用户登录模式不是LDAP，请联系管理员修改,或不使用LDAP登录"),

    /** global exception */
    GLOBAL_PARAMS_CHECK_ERROR(90001, "Field: {0}, {1}", "字段: {0}, {1}"),
    GLOBAL_PARAMS_CHECK_ERROR_VALUE(90002, "Field: {0}, Illegal Value: {1}", "字段: {0}, 不合法的值: {1}"),
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
