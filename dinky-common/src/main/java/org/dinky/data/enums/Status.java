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

import org.dinky.utils.I18n;

import java.util.Objects;
import java.util.Optional;

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

    /**
     * response data msg
     */
    SUCCESS(200, "success"),
    FAILED(400, "failed"),

    /**
     * request && response message
     */
    INTERNAL_SERVER_ERROR_ARGS(7001, "internal.server.error.args"),
    REQUEST_PARAMS_NOT_VALID_ERROR(7002, "request.params.not.valid.error"),
    REQUEST_PARAMS_ERROR(7003, "request.params.error"),
    UNKNOWN_ERROR(7004, "unknown.error"),

    /**
     * interface CRUD operations
     */
    ADDED_SUCCESS(9001, "added.success"),
    ADDED_FAILED(9002, "added.failed"),
    MODIFY_SUCCESS(9003, "modify.success"),
    MODIFY_FAILED(9004, "modify.failed"),
    SAVE_FAILED(9005, "save.failed"),
    SAVE_SUCCESS(9006, "save.success"),
    DELETE_FAILED(9007, "delete.failed"),
    DELETE_SUCCESS(9008, "delete.success"),
    REFRESH_SUCCESS(9009, "refresh.success"),
    REFRESH_FAILED(9010, "refresh.failed"),
    QUERY_SUCCESS(9011, "query.success"),
    QUERY_FAILED(9012, "query.failed"),
    COPY_SUCCESS(9013, "copy.success"),
    COPY_FAILED(9014, "copy.failed"),
    CLEAR_SUCCESS(9015, "clear.success"),
    CLEAR_FAILED(9016, "clear.failed"),
    OPERATE_SUCCESS(9017, "operate.success"),
    OPERATE_FAILED(9018, "operate.failed"),
    EXECUTE_SUCCESS(9019, "execute.success"),
    EXECUTE_FAILED(9020, "execute.failed"),
    RESTART_SUCCESS(9021, "restart.success"),
    RESTART_FAILED(9022, "restart.failed"),
    // 已成功停止
    STOP_SUCCESS(9023, "stop.success"),
    STOP_FAILED(9024, "stop.failed"),
    RENAME_SUCCESS(9025, "rename.success"),
    RENAME_FAILED(9026, "rename.failed"),
    MOVE_SUCCESS(9027, "move.success"),
    MOVE_FAILED(9028, "move.failed"),
    TEST_CONNECTION_SUCCESS(9029, "test.connection.success"),
    TEST_CONNECTION_FAILED(9030, "test.connection.failed"),

    /**
     * user,tenant,role
     */
    // user
    USER_ALREADY_EXISTS(10001, "user.already.exists"),
    USER_NOT_EXIST(10002, "user.not.exist"),
    USER_NAME_PASSWD_ERROR(10003, "user.name.passwd.error"),
    LOGIN_SUCCESS(10004, "login.success"),
    LOGIN_FAILURE(10005, "login.failure"),
    USER_NOT_LOGIN(10006, "user.not.login"),
    SIGN_OUT_SUCCESS(10007, "sign.out.success"),
    USER_DISABLED_BY_ADMIN(10008, "user.disabled.by.admin"),
    LOGIN_PASSWORD_NOT_NULL(10009, "login.password.not.null"),
    USER_NOT_BINDING_TENANT(10010, "user.not.binding.tenant"),
    USER_OLD_PASSWORD_INCORRECT(10011, "user.old.password.incorrect"),
    CHANGE_PASSWORD_SUCCESS(10012, "change.password.success"),
    CHANGE_PASSWORD_FAILED(10013, "change.password.failed"),
    USER_ASSIGN_ROLE_SUCCESS(10014, "user.assign.role.success"),
    USER_BINDING_ROLE_DELETE_ALL(10015, "user.binding.role.delete.all"),
    USER_ASSIGN_ROLE_FAILED(10016, "user.assign.role.failed"),
    GET_TENANT_FAILED(10017, "get.tenant.failed"),
    SWITCHING_TENANT_SUCCESS(10018, "switching.tenant.success"),
    USER_SUPERADMIN_CANNOT_DISABLE(10019, "user.superadmin.cannot.disable"),
    NOT_TOKEN(10020, "not.token"),
    INVALID_TOKEN(10021, "invalid.token"),
    EXPIRED_TOKEN(10022, "expired.token"),
    BE_REPLACED(10023, "be.replaced"),
    KICK_OUT(10024, "kick.out"),
    TOKEN_FREEZED(10025, "token.freezed"),
    NO_PREFIX(10026, "no.prefix"),

    // role
    ROLE_ALREADY_EXISTS(10101, "role.already.exists"),
    ROLE_BINDING_USER(10112, "role.binding.user"),
    // 该角色已绑定行权限，无法删除
    ROLE_BINDING_ROW_PERMISSION(10113, "role.binding.row.permission"),

    // tenant
    TENANT_ALREADY_EXISTS(10201, "tenant.already.exists"),
    TENANT_NOT_EXIST(10202, "tenant.not.exist"),
    TENANT_BINDING_USER(10203, "tenant.binding.user"),
    TENANT_ASSIGN_USER_SUCCESS(10204, "tenant.assign.user.success"),
    TENANT_ASSIGN_USER_FAILED(10205, "tenant.assign.user.failed"),
    TENANT_BINDING_USER_DELETE_ALL(10206, "tenant.binding.user.delete.all"),
    TENANT_ADMIN_ALREADY_EXISTS(10207, "tenant.admin.already.exists"),

    // tenant
    TENANT_NAME_EXIST(10101, "tenant.name.exist"),
    TENANT_NAME_NOT_EXIST(10102, "tenant.name.not.exist"),
    // role
    ROLE_NAME_EXIST(10201, "role.name.exist"),
    ROLE_NOT_EXIST(10202, "role.not.exist"),

    // menu
    MENU_NAME_EXIST(10301, "menu.name.exist"),
    MENU_NOT_EXIST(10302, "menu.not.exist"),
    MENU_HAS_CHILD(10303, "menu.has.child"),
    MENU_HAS_ASSIGN(10304, "menu.has.assign"),
    SELECT_MENU(10305, "select.menu"),
    ASSIGN_MENU_SUCCESS(10306, "assign.menu.success"),
    ASSIGN_MENU_FAILED(10307, "assign.menu.failed"),

    /**
     * database
     */
    DATASOURCE_CONNECT_SUCCESS(11001, "datasource.connect.success"),
    // 状态刷新完成
    DATASOURCE_STATUS_REFRESH_SUCCESS(11002, "datasource.status.refresh.success"),

    // 该数据源不存在
    DATASOURCE_NOT_EXIST(11003, "datasource.not.exist"),
    // 数据源连接正常
    DATASOURCE_CONNECT_NORMAL(11004, "datasource.connect.normal"),
    // 清除库表缓存
    DATASOURCE_CLEAR_CACHE_SUCCESS(11005, "datasource.clear.cache.success"),

    /**
     * job or task about
     */
    JOB_RELEASE_DISABLED_UPDATE(12001, "job.release.disabled.update"),
    SCHEDULE_STATUS_UNKNOWN(12002, "schedule.status.unknown"),
    TASK_NOT_EXIST(12003, "task.not.exist"),
    JOB_INSTANCE_NOT_EXIST(12004, "job.instance.not.exist"),
    SAVEPOINT_IS_NULL(12005, "savepoint.is.null"),

    /**
     * alert instance
     */
    SEND_TEST_SUCCESS(13001, "send.test.success"),
    SEND_TEST_FAILED(13002, "send.test.failed"),
    TEST_MSG_TITLE(13003, "test.msg.title"),
    TEST_MSG_JOB_NAME(13004, "test.msg.job.name"),
    TEST_MSG_JOB_URL(13005, "test.msg.job.url"),
    TEST_MSG_JOB_LOG_URL(13006, "test.msg.job.log.url"),
    TEST_MSG_JOB_NAME_TITLE(13007, "test.msg.job.name.title"),

    /**
     * alert group
     */
    ALERT_GROUP_EXIST(14001, "alert.group.exist"),

    /**
     * cluster instance
     */
    CLUSTER_INSTANCE_HEARTBEAT_SUCCESS(15001, "cluster.instance.heartbeat.success"),
    CLUSTER_INSTANCE_RECYCLE_SUCCESS(15002, "cluster.instance.recycle.success"),
    CLUSTER_INSTANCE_KILL(15003, "cluster.instance.kill"),
    CLUSTER_INSTANCE_DEPLOY(15004, "cluster.instance.deploy"),

    /**
     * git
     */
    GIT_PROJECT_NOT_FOUND(16001, "git.project.not.found"),
    GIT_SORT_FAILED(16002, "git.sort.failed"),
    GIT_SORT_SUCCESS(16003, "git.sort.success"),
    GIT_BRANCH_NOT_FOUND(16003, "git.branch.not.found"),
    GIT_BUILDING(16004, "git.building"),
    GIT_BUILD_SUCCESS(16005, "git.build.success"),

    /**
     * dolphin scheduler
     */
    // 节点获取失败
    DS_GET_NODE_LIST_ERROR(17001, "ds.get.node.list.error"),
    // 请先工作流保存
    DS_WORK_FLOW_NOT_SAVE(17002, "ds.work.flow.not.save"),
    // 添加工作流定义成功
    DS_ADD_WORK_FLOW_DEFINITION_SUCCESS(17003, "ds.add.work.flow.definition.success"),
    DS_WORK_FLOW_DEFINITION_ONLINE(17004, "ds.work.flow.definition.online"),
    DS_WORK_FLOW_DEFINITION_TASK_NAME_EXIST(17005, "ds.work.flow.definition.task.name.exist"),
    DS_ADD_TASK_DEFINITION_SUCCESS(17006, "ds.add.task.definition.success"),
    DS_TASK_NOT_EXIST(17007, "ds.task.not.exist"),
    DS_TASK_TYPE_NOT_SUPPORT(17008, "ds.task.type.not.support"),
    DS_WORK_FLOW_DEFINITION_NOT_EXIST(17009, "ds.work.flow.definition.not.exist"),

    /**
     * LDAP About *
     */
    LDAP_USER_DUPLICAT(18001, "ldap.user.duplicat"),
    LDAP_USER_AUTOLOAD_FORBAID(18002, "ldap.user.autoload.forbaid"),
    LDAP_DEFAULT_TENANT_NOFOUND(18003, "ldap.default.tenant.nofound"),
    LDAP_USER_INCORRECT(18004, "ldap.user.incorrect"),
    LDAP_NO_USER_FOUND(18005, "ldap.no.user.found"),
    LDAP_FILTER_INCORRECT(18006, "ldap.filter.incorrect"),

    LDAP_LOGIN_FORBID(18007, "ldap.login.forbid"),

    /**
     * datastudio about
     */
    // 该目录下存在子目录/作业，无法删除
    FOLDER_NOT_EMPTY(19001, "folder.not.empty"),

    /**
     * Alert About
     * */
    ALERT_RULE_JOB_FAIL(20001, "alert.rule.jobFail"),
    ALERT_RULE_GET_JOB_INFO_FAIL(20002, "alert.rule.getJobInfoFail"),
    ALERT_RULE_JOB_RESTART(20003, "alert.rule.jobRestart"),
    ALERT_RULE_CHECKPOINT_FAIL(20004, "alert.rule.checkpointFail"),
    ALERT_RULE_JOB_RUN_EXCEPTION(20005, "alert.rule.jobRunException"),
    ALERT_RULE_CHECKPOINT_TIMEOUT(20006, "alert.rule.checkpointTimeout"),

    /**
     * global exception
     */
    GLOBAL_PARAMS_CHECK_ERROR(90001, "global.params.check.error"),
    GLOBAL_PARAMS_CHECK_ERROR_VALUE(90002, "global.params.check.error.value"),
    ;

    private final int code;
    private final String key;

    Status(int code, String key) {
        this.code = code;
        this.key = key;
    }

    public int getCode() {
        return this.code;
    }

    public String getKey() {
        return this.key;
    }

    public String getMessage() {
        return I18n.getMessage(this.getKey());
    }

    /**
     * Retrieve Status enum entity by status code.
     */
    public static Optional<Status> findStatusByCode(int code) {
        for (Status status : Status.values()) {
            if (code == status.getCode()) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }

    public static String findMessageByKey(String key) {
        for (Status status : Status.values()) {
            if (Objects.equals(key, status.getKey())) {
                return status.getMessage();
            }
        }
        return key;
    }
}
