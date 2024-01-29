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
    STOP_SUCCESS(9023, "stop.success"),
    STOP_FAILED(9024, "stop.failed"),
    RENAME_SUCCESS(9025, "rename.success"),
    RENAME_FAILED(9026, "rename.failed"),
    MOVE_SUCCESS(9027, "move.success"),
    MOVE_FAILED(9028, "move.failed"),
    TEST_CONNECTION_SUCCESS(9029, "test.connection.success"),
    TEST_CONNECTION_FAILED(9030, "test.connection.failed"),
    DEBUG_SUCCESS(9031, "debug.success"),
    DEBUG_FAILED(9032, "debug.failed"),
    PUBLISH_SUCCESS(9033, "publish.success"),
    PUBLISH_FAILED(9034, "publish.failed"),
    OFFLINE_SUCCESS(9035, "offline.success"),
    OFFLINE_FAILED(9036, "offline.failed"),
    VERSION_ROLLBACK_SUCCESS(9037, "version.rollback.success"),
    VERSION_ROLLBACK_FAILED(9038, "version.rollback.failed"),

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
    USER_SUPERADMIN_CANNOT_DELETE(10027, "user.superadmin.cannot.delete"),

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

    DATASOURCE_EXIST_RELATIONSHIP(11006, "datasource.exist.relationship"),

    /**
     * job or task about
     */
    JOB_RELEASE_DISABLED_UPDATE(12001, "job.release.disabled.update"),
    SCHEDULE_STATUS_UNKNOWN(12002, "schedule.status.unknown"),
    TASK_NOT_EXIST(12003, "task.not.exist"),
    JOB_INSTANCE_NOT_EXIST(12004, "job.instance.not.exist"),
    SAVEPOINT_IS_NULL(12005, "savepoint.is.null"),
    TASK_STATUS_IS_NOT_DONE(12006, "task.status.is.not.done"),
    TASK_SQL_EXPLAN_FAILED(12007, "task.sql.explain.failed"),
    TASK_UPDATE_FAILED(12008, "task.update.failed"),
    TASK_IS_ONLINE(12009, "task.is.online"),
    TASK_IS_EXIST(12010, "task.is.existed"),
    TASK_IS_PUBLISH_CANNOT_DELETE(12011, "task.is.publish.cannot.delete"),
    TASK_IS_RUNNING_CANNOT_DELETE(12012, "task.is.running.cannot.delete"),
    JOB_ALERT_MAX_SEND_COUNT(12013, "job.alert.max.send.count"),

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
    //    "Alert group has relationship with other table, please delete the relationship first"
    ALERT_GROUP_EXIST_RELATIONSHIP(14002, "alert.group.exist.relationship"),

    /**
     * cluster instance
     */
    CLUSTER_INSTANCE_HEARTBEAT_SUCCESS(15001, "cluster.instance.heartbeat.success"),
    CLUSTER_INSTANCE_RECYCLE_SUCCESS(15002, "cluster.instance.recycle.success"),
    CLUSTER_INSTANCE_KILL(15003, "cluster.instance.kill"),
    CLUSTER_INSTANCE_DEPLOY(15004, "cluster.instance.deploy"),
    CLUSTER_NOT_EXIST(15004, "cluster.not.exist"),
    CLUSTER_INSTANCE_EXIST_RELATIONSHIP(15005, "cluster.instance.exist.relationship"),
    CLUSTER_INSTANCE_LOCAL_NOT_SUPPORT_KILL(15006, "cluster.instance.local.not.support.kill"),
    CLUSTER_INSTANCE_NOT_HEALTH(15007, "cluster.instance.not.health"),
    CLUSTER_INSTANCE_HEALTH_NOT_DELETE(15008, "cluster.instance.health.not.delete"),

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
    DS_PROCESS_DEFINITION_UPDATE(17010, "ds.work.flow.definition.process.update"),

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
     * alert template
     */
    ALERT_TEMPLATE_EXIST_RELATIONSHIP(21001, "alert.template.exist.relationship"),

    /**
     * cluster config
     */
    CLUSTER_CONFIG_EXIST_RELATIONSHIP(22001, "cluster.config.exist.relationship"),

    /**
     * udf template
     */
    UDF_TEMPLATE_EXIST_RELATIONSHIP(23001, "udf.template.exist.relationship"),

    /**
     * Resource
     */
    ROOT_DIR_NOT_ALLOW_DELETE(9031, "resource.root.dir.not.allow.delete"),
    RESOURCE_DIR_OR_FILE_NOT_EXIST(9032, "resource.dir.or.file.not.exist"),

    /**
     * global exception
     */
    GLOBAL_PARAMS_CHECK_ERROR(90001, "global.params.check.error"),
    GLOBAL_PARAMS_CHECK_ERROR_VALUE(90002, "global.params.check.error.value"),

    /**
     *
     * Daemon About
     * */
    DAEMON_TASK_CONFIG_NOT_EXIST(100001, "daemon.task.config.not.exist"),
    DAEMON_TASK_NOT_SUPPORT(100002, "daemon.task.not.support"),

    /**
     * system config
     */
    SYS_FLINK_SETTINGS_USERESTAPI(100, "sys.flink.settings.useRestAPI"),
    SYS_FLINK_SETTINGS_USERESTAPI_NOTE(101, "sys.flink.settings.useRestAPI.note"),
    SYS_FLINK_SETTINGS_SQLSEPARATOR(102, "sys.flink.settings.sqlSeparator"),
    SYS_FLINK_SETTINGS_SQLSEPARATOR_NOTE(103, "sys.flink.settings.sqlSeparator.note"),
    SYS_FLINK_SETTINGS_JOBIDWAIT(104, "sys.flink.settings.jobIdWait"),
    SYS_FLINK_SETTINGS_JOBIDWAIT_NOTE(105, "sys.flink.settings.jobIdWait.note"),
    SYS_MAVEN_SETTINGS_SETTINGSFILEPATH(106, "sys.maven.settings.settingsFilePath"),
    SYS_MAVEN_SETTINGS_SETTINGSFILEPATH_NOTE(107, "sys.maven.settings.settingsFilePath.note"),
    SYS_MAVEN_SETTINGS_REPOSITORY(108, "sys.maven.settings.repository"),
    SYS_MAVEN_SETTINGS_REPOSITORY_NOTE(109, "sys.maven.settings.repository.note"),
    SYS_MAVEN_SETTINGS_REPOSITORYUSER(110, "sys.maven.settings.repositoryUser"),
    SYS_MAVEN_SETTINGS_REPOSITORYUSER_NOTE(111, "sys.maven.settings.repositoryUser.note"),
    SYS_MAVEN_SETTINGS_REPOSITORYPASSWORD(112, "sys.maven.settings.repositoryPassword"),
    SYS_MAVEN_SETTINGS_REPOSITORYPASSWORD_NOTE(113, "sys.maven.settings.repositoryPassword.note"),

    SYS_ENV_SETTINGS_PYTHONHOME(114, "sys.env.settings.pythonHome"),
    SYS_ENV_SETTINGS_PYTHONHOME_NOTE(115, "sys.env.settings.pythonHome.note"),
    SYS_ENV_SETTINGS_DINKYADDR(116, "sys.env.settings.dinkyAddr"),
    SYS_ENV_SETTINGS_DINKYADDR_NOTE(117, "sys.env.settings.dinkyAddr.note"),

    SYS_ENV_SETTINGS_JOB_RESEND_DIFF_SECOND(118, "sys.env.settings.jobResendDiffSecond"),
    SYS_ENV_SETTINGS_JOB_RESEND_DIFF_SECOND_NOTE(119, "sys.env.settings.jobResendDiffSecond.note"),

    SYS_ENV_SETTINGS_DIFF_MINUTE_MAX_SEND_COUNT(120, "sys.env.settings.diffMinuteMaxSendCount"),
    SYS_ENV_SETTINGS_DIFF_MINUTE_MAX_SEND_COUNT_NOTE(121, "sys.env.settings.diffMinuteMaxSendCount.note"),

    SYS_ENV_SETTINGS_MAX_RETAIN_DAYS(1171, "sys.env.settings.maxRetainDays"),
    SYS_ENV_SETTINGS_MAX_RETAIN_DAYS_NOTE(1172, "sys.env.settings.maxRetainDays.note"),
    SYS_ENV_SETTINGS_MAX_RETAIN_COUNT(1173, "sys.env.settings.maxRetainCount"),
    SYS_ENV_SETTINGS_MAX_RETAIN_COUNT_NOTE(1174, "sys.env.settings.maxRetainCount.note"),

    SYS_DOLPHINSCHEDULER_SETTINGS_ENABLE(118, "sys.dolphinscheduler.settings.enable"),
    SYS_DOLPHINSCHEDULER_SETTINGS_ENABLE_NOTE(119, "sys.dolphinscheduler.settings.enable.note"),
    SYS_DOLPHINSCHEDULER_SETTINGS_URL(120, "sys.dolphinscheduler.settings.url"),
    SYS_DOLPHINSCHEDULER_SETTINGS_URL_NOTE(121, "sys.dolphinscheduler.settings.url.note"),
    SYS_DOLPHINSCHEDULER_SETTINGS_TOKEN(122, "sys.dolphinscheduler.settings.token"),
    SYS_DOLPHINSCHEDULER_SETTINGS_TOKEN_NOTE(123, "sys.dolphinscheduler.settings.token.note"),
    SYS_DOLPHINSCHEDULER_SETTINGS_PROJECTNAME(124, "sys.dolphinscheduler.settings.projectName"),
    SYS_DOLPHINSCHEDULER_SETTINGS_PROJECTNAME_NOTE(125, "sys.dolphinscheduler.settings.projectName.note"),
    SYS_LDAP_SETTINGS_URL(126, "sys.ldap.settings.url"),
    SYS_LDAP_SETTINGS_URL_NOTE(127, "sys.ldap.settings.url.note"),
    SYS_LDAP_SETTINGS_USERDN(128, "sys.ldap.settings.userDn"),
    SYS_LDAP_SETTINGS_USERDN_NOTE(129, "sys.ldap.settings.userDn.note"),
    SYS_LDAP_SETTINGS_USERPASSWORD(130, "sys.ldap.settings.userPassword"),
    SYS_LDAP_SETTINGS_USERPASSWORD_NOTE(131, "sys.ldap.settings.userPassword.note"),
    SYS_LDAP_SETTINGS_TIMELIMIT(132, "sys.ldap.settings.timeLimit"),
    SYS_LDAP_SETTINGS_TIMELIMIT_NOTE(133, "sys.ldap.settings.timeLimit.note"),
    SYS_LDAP_SETTINGS_BASEDN(134, "sys.ldap.settings.baseDn"),
    SYS_LDAP_SETTINGS_BASEDN_NOTE(135, "sys.ldap.settings.baseDn.note"),
    SYS_LDAP_SETTINGS_FILTER(136, "sys.ldap.settings.filter"),
    SYS_LDAP_SETTINGS_FILTER_NOTE(137, "sys.ldap.settings.filter.note"),
    SYS_LDAP_SETTINGS_AUTOLOAD(138, "sys.ldap.settings.autoload"),
    SYS_LDAP_SETTINGS_AUTOLOAD_NOTE(139, "sys.ldap.settings.autoload.note"),
    SYS_LDAP_SETTINGS_DEFAULTTEANT(140, "sys.ldap.settings.defaultTeant"),
    SYS_LDAP_SETTINGS_DEFAULTTEANT_NOTE(141, "sys.ldap.settings.defaultTeant.note"),
    SYS_LDAP_SETTINGS_CASTUSERNAME(142, "sys.ldap.settings.castUsername"),
    SYS_LDAP_SETTINGS_CASTUSERNAME_NOTE(143, "sys.ldap.settings.castUsername.note"),
    SYS_LDAP_SETTINGS_CASTNICKNAME(144, "sys.ldap.settings.castNickname"),
    SYS_LDAP_SETTINGS_CASTNICKNAME_NOTE(145, "sys.ldap.settings.castNickname.note"),
    SYS_LDAP_SETTINGS_ENABLE(146, "sys.ldap.settings.enable"),
    SYS_LDAP_SETTINGS_ENABLE_NOTE(147, "sys.ldap.settings.enable.note"),
    SYS_METRICS_SETTINGS_SYS_ENABLE(148, "sys.metrics.settings.sys.enable"),
    SYS_METRICS_SETTINGS_SYS_ENABLE_NOTE(149, "sys.metrics.settings.sys.enable.note"),
    SYS_METRICS_SETTINGS_SYS_GATHERTIMING(150, "sys.metrics.settings.sys.gatherTiming"),
    SYS_METRICS_SETTINGS_SYS_GATHERTIMING_NOTE(151, "sys.metrics.settings.sys.gatherTiming.note"),
    SYS_METRICS_SETTINGS_FLINK_GATHERTIMING(152, "sys.metrics.settings.flink.gatherTiming"),
    SYS_METRICS_SETTINGS_FLINK_GATHERTIMING_NOTE(153, "sys.metrics.settings.flink.gatherTiming.note"),
    SYS_METRICS_SETTINGS_FLINK_GATHERTIMEOUT(154, "sys.metrics.settings.flink.gatherTimeout"),
    SYS_METRICS_SETTINGS_FLINK_GATHERTIMEOUT_NOTE(155, "sys.metrics.settings.flink.gatherTimeout.note"),
    SYS_RESOURCE_SETTINGS_ENABLE(156, "sys.resource.settings.base.enable"),
    SYS_RESOURCE_SETTINGS_ENABLE_NOTE(157, "sys.resource.settings.base.enable.note"),
    SYS_RESOURCE_SETTINGS_UPLOAD_BASE_PATH(158, "sys.resource.settings.base.upload.base.path"),
    SYS_RESOURCE_SETTINGS_UPLOAD_BASE_PATH_NOTE(159, "sys.resource.settings.base.upload.base.path.note"),
    SYS_RESOURCE_SETTINGS_MODEL(160, "sys.resource.settings.base.model"),
    SYS_RESOURCE_SETTINGS_MODEL_NOTE(161, "sys.resource.settings.base.model.note"),
    SYS_RESOURCE_SETTINGS_OSS_ENDPOINT(162, "sys.resource.settings.oss.endpoint"),
    SYS_RESOURCE_SETTINGS_OSS_ENDPOINT_NOTE(163, "sys.resource.settings.oss.endpoint.note"),
    SYS_RESOURCE_SETTINGS_OSS_ACCESSKEY(164, "sys.resource.settings.oss.accessKey"),
    SYS_RESOURCE_SETTINGS_OSS_ACCESSKEY_NOTE(165, "sys.resource.settings.oss.accessKey.note"),
    SYS_RESOURCE_SETTINGS_OSS_SECRETKEY(166, "sys.resource.settings.oss.secretKey"),
    SYS_RESOURCE_SETTINGS_OSS_SECRETKEY_NOTE(167, "sys.resource.settings.oss.secretKey.note"),
    SYS_RESOURCE_SETTINGS_OSS_BUCKETNAME(168, "sys.resource.settings.oss.bucketName"),
    SYS_RESOURCE_SETTINGS_OSS_BUCKETNAME_NOTE(169, "sys.resource.settings.oss.bucketName.note"),
    SYS_RESOURCE_SETTINGS_OSS_REGION(170, "sys.resource.settings.oss.region"),
    SYS_RESOURCE_SETTINGS_OSS_REGION_NOTE(171, "sys.resource.settings.oss.region.note"),
    SYS_RESOURCE_SETTINGS_HDFS_ROOT_USER(172, "sys.resource.settings.hdfs.root.user"),
    SYS_RESOURCE_SETTINGS_HDFS_ROOT_USER_NOTE(173, "sys.resource.settings.hdfs.root.user.note"),
    SYS_RESOURCE_SETTINGS_HDFS_FS_DEFAULTFS(174, "sys.resource.settings.hdfs.fs.defaultFS"),
    SYS_RESOURCE_SETTINGS_HDFS_FS_DEFAULTFS_NOTE(175, "sys.resource.settings.hdfs.fs.defaultFS.note"),

    SYS_RESOURCE_SETTINGS_HDFS_CORE_SITE(178, "sys.resource.settings.hdfs.core.site"),
    SYS_RESOURCE_SETTINGS_HDFS_CORE_SITE_NOTE(179, "sys.resource.settings.hdfs.core.site.note"),
    SYS_RESOURCE_SETTINGS_HDFS_HDFS_SITE(380, "sys.resource.settings.hdfs.hdfs.site"),
    SYS_RESOURCE_SETTINGS_HDFS_HDFS_SITE_NOTE(381, "sys.resource.settings.hdfs.hdfs.site.note"),

    SYS_RESOURCE_SETTINGS_PATH_STYLE_ACCESS(176, "sys.resource.settings.oss.path.style.access"),
    SYS_RESOURCE_SETTINGS_PATH_STYLE_ACCESS_NOTE(177, "sys.resource.settings.oss.path.style.access.note"),

    /**
     * gateway config
     */
    GAETWAY_KUBERNETS_TEST_FAILED(180, "gateway.kubernetes.test.failed"),
    GAETWAY_KUBERNETS_TEST_SUCCESS(181, "gateway.kubernetes.test.success"),

    /**
     * process
     * */
    PROCESS_SUBMIT_SUBMITTASK(190, "process.submit.submitTask"),
    PROCESS_SUBMIT_CHECKSQL(191, "process.submit.checkSql"),
    PROCESS_SUBMIT_EXECUTE(192, "process.submit.execute"),
    PROCESS_SUBMIT_BUILDCONFIG(193, "process.submit.buildConfig"),
    PROCESS_SUBMIT_EXECUTECOMMSQL(194, "process.submit.execute.commSql"),
    PROCESS_SUBMIT_EXECUTEFLINKSQL(195, "process.submit.execute.flinkSql"),
    PROCESS_REGISTER_EXITS(196, "process.register.exits"),
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
