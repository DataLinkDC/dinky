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

import java.util.Optional;

import org.dinky.utils.I18n;
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
    SUCCESS(200),
    FAILED(400),

    /** request && response message */
    INTERNAL_SERVER_ERROR_ARGS(7001),
    REQUEST_PARAMS_NOT_VALID_ERROR(7002),
    REQUEST_PARAMS_ERROR(7003),
    UNKNOWN_ERROR(7004),

    /** interface CRUD operations */
    ADDED_SUCCESS(9001),
    ADDED_FAILED(9002),
    MODIFY_SUCCESS(9003),
    MODIFY_FAILED(9004),
    SAVE_FAILED(9005),
    SAVE_SUCCESS(9006),
    DELETE_FAILED(9007),
    DELETE_SUCCESS(9008),
    REFRESH_SUCCESS(9009),
    REFRESH_FAILED(9010),
    QUERY_SUCCESS(9011),
    QUERY_FAILED(9012),
    COPY_SUCCESS(9013),
    COPY_FAILED(9014),
    CLEAR_SUCCESS(9015),
    CLEAR_FAILED(9016),
    OPERATE_SUCCESS(9017),
    OPERATE_FAILED(9018),
    EXECUTE_SUCCESS(9019),
    EXECUTE_FAILED(9020),
    RESTART_SUCCESS(9021),
    RESTART_FAILED(9022),
    // 已成功停止
    STOP_SUCCESS(9023),
    STOP_FAILED(9024),
    RENAME_SUCCESS(9025),
    RENAME_FAILED(9026),
    MOVE_SUCCESS(9027),
    MOVE_FAILED(9028),
    TEST_CONNECTION_SUCCESS(9029),
    TEST_CONNECTION_FAILED(9030),

    /** user,tenant,role */
    // user
    USER_ALREADY_EXISTS(10001),
    USER_NOT_EXIST(10002),
    USER_NAME_PASSWD_ERROR(10003),
    LOGIN_SUCCESS(10004),
    LOGIN_FAILURE(10005),
    USER_NOT_LOGIN(10006),
    SIGN_OUT_SUCCESS(10007),
    USER_DISABLED_BY_ADMIN(10008),
    LOGIN_PASSWORD_NOT_NULL(10009),
    USER_NOT_BINDING_TENANT(10010),
    USER_OLD_PASSWORD_INCORRECT(10011),
    CHANGE_PASSWORD_SUCCESS(10012),
    CHANGE_PASSWORD_FAILED(10013),
    USER_ASSIGN_ROLE_SUCCESS(10014),
    USER_BINDING_ROLE_DELETE_ALL(10015),
    USER_ASSIGN_ROLE_FAILED(10016),
    GET_TENANT_FAILED(10017),
    SWITCHING_TENANT_SUCCESS(10018),
    USER_SUPERADMIN_CANNOT_DISABLE(10019),
    NOT_TOKEN(10020),
    INVALID_TOKEN(10021),
    EXPIRED_TOKEN(10022),
    BE_REPLACED(10023),
    KICK_OUT(10024),
    TOKEN_FREEZED(10025),
    NO_PREFIX(10026),

    // role
    ROLE_ALREADY_EXISTS(10101),
    ROLE_BINDING_USER(10112),
    // 该角色已绑定行权限，无法删除
    ROLE_BINDING_ROW_PERMISSION(10113),

    // tenant
    TENANT_ALREADY_EXISTS(10201),
    TENANT_NOT_EXIST(10202),
    TENANT_BINDING_USER(10203),
    TENANT_ASSIGN_USER_SUCCESS(10204),
    TENANT_ASSIGN_USER_FAILED(10205),
    TENANT_BINDING_USER_DELETE_ALL(10206),
    TENANT_ADMIN_ALREADY_EXISTS(10207),

    // tenant
    TENANT_NAME_EXIST(10101),
    TENANT_NAME_NOT_EXIST(10102),
    // role
    ROLE_NAME_EXIST(10201),
    ROLE_NOT_EXIST(10202),

    // menu
    MENU_NAME_EXIST(10301),
    MENU_NOT_EXIST(10302),
    MENU_HAS_CHILD(10303),
    MENU_HAS_ASSIGN(10304),
    SELECT_MENU(10305),
    ASSIGN_MENU_SUCCESS(10306),
    ASSIGN_MENU_FAILED(10307),

    /** database */
    DATASOURCE_CONNECT_SUCCESS(11001),
    // 状态刷新完成
    DATASOURCE_STATUS_REFRESH_SUCCESS(11002),

    // 该数据源不存在
    DATASOURCE_NOT_EXIST(11003),
    // 数据源连接正常
    DATASOURCE_CONNECT_NORMAL(11004),
    // 清除库表缓存
    DATASOURCE_CLEAR_CACHE_SUCCESS(11005),

    /** job or task about */
    JOB_RELEASE_DISABLED_UPDATE(12001),
    SCHEDULE_STATUS_UNKNOWN(12002),
    TASK_NOT_EXIST(12003),
    JOB_INSTANCE_NOT_EXIST(12004),
    SAVEPOINT_IS_NULL(12005),

    /** alert instance */
    SEND_TEST_SUCCESS(13001),
    SEND_TEST_FAILED(13002),
    TEST_MSG_TITLE(13003),
    TEST_MSG_JOB_NAME(13004),
    TEST_MSG_JOB_URL(13005),
    TEST_MSG_JOB_LOG_URL(13006),
    TEST_MSG_JOB_NAME_TITLE(13007),

    /** alert group */
    ALERT_GROUP_EXIST(14001),

    /** cluster instance */
    CLUSTER_INSTANCE_HEARTBEAT_SUCCESS(15001),
    CLUSTER_INSTANCE_RECYCLE_SUCCESS(15002),
    CLUSTER_INSTANCE_KILL(15003),
    CLUSTER_INSTANCE_DEPLOY(15004),

    /** git */
    GIT_PROJECT_NOT_FOUND(16001),
    GIT_SORT_FAILED(16002),
    GIT_SORT_SUCCESS(16003),
    GIT_BRANCH_NOT_FOUND(16003),
    GIT_BUILDING(16004),
    GIT_BUILD_SUCCESS(16005),

    /** dolphin scheduler */
    // 节点获取失败
    DS_GET_NODE_LIST_ERROR(17001),
    // 请先工作流保存
    DS_WORK_FLOW_NOT_SAVE(17002),
    // 添加工作流定义成功
    DS_ADD_WORK_FLOW_DEFINITION_SUCCESS(17003),
    DS_WORK_FLOW_DEFINITION_ONLINE(17004),
    DS_WORK_FLOW_DEFINITION_TASK_NAME_EXIST(
            17005
    ),
    DS_ADD_TASK_DEFINITION_SUCCESS(17006),
    DS_TASK_NOT_EXIST(17007),
    DS_TASK_TYPE_NOT_SUPPORT(
            17008),
    DS_WORK_FLOW_DEFINITION_NOT_EXIST(17009),

    /** LDAP About * */
    LDAP_USER_DUPLICAT(18001),
    LDAP_USER_AUTOLOAD_FORBAID(
            18002
    ),
    LDAP_DEFAULT_TENANT_NOFOUND(18003),
    LDAP_USER_INCORRECT(18004),
    LDAP_NO_USER_FOUND(
            18005),
    LDAP_FILTER_INCORRECT(
            18006),

    LDAP_LOGIN_FORBID(
            18007
    ),

    /**
     * datastudio about
     */
    // 该目录下存在子目录/作业，无法删除
    FOLDER_NOT_EMPTY(19001),

    /** global exception */
    GLOBAL_PARAMS_CHECK_ERROR(90001),
    GLOBAL_PARAMS_CHECK_ERROR_VALUE(90002),
    ;

    private final int code;
    Status(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        I18n.setLocale(LocaleContextHolder.getLocale());
        String id = this.name().toLowerCase().replace('_', '.');
        return I18n.getMessage(id);
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
