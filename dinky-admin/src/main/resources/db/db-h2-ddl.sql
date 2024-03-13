
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `dinky_alert_group` (
                                   `id` int(11) NOT null AUTO_INCREMENT COMMENT 'id',
                                   `name` varchar(50) NOT null COMMENT 'alert group name',
                                   `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                                   `alert_instance_ids` text null COMMENT 'Alert instance IDS',
                                   `note` varchar(255) null DEFAULT null COMMENT 'note',
                                   `enabled` tinyint(4) null DEFAULT 1 COMMENT 'is enable',
                                   `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                   `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                     `creator` int(11) null DEFAULT null COMMENT 'creator',
                                   `updater` int(11)  null DEFAULT null COMMENT 'updater'

) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `dinky_alert_history` (
                                     `id` int(11) NOT null AUTO_INCREMENT COMMENT 'id',
                                     `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                                     `alert_group_id` int(11) null DEFAULT null COMMENT 'Alert group ID',
                                     `job_instance_id` int(11) null DEFAULT null COMMENT 'job instance ID',
                                     `title` varchar(255) null DEFAULT null COMMENT 'alert title',
                                     `content` text null COMMENT 'content description',
                                     `status` int(11) null DEFAULT null COMMENT 'alert status',
                                     `log` text null COMMENT 'log',
                                     `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                     `update_time` datetime(0) null DEFAULT null COMMENT 'update time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `dinky_alert_instance` (
                                      `id` int(11) NOT null AUTO_INCREMENT COMMENT 'id',
                                      `name` varchar(50) NOT null COMMENT 'alert instance name',
                                      `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                                      `type` varchar(50) null DEFAULT null COMMENT 'alert instance type such as: DingTalk,Wechat(Webhook,app) Feishu ,email',
                                      `params` longtext null  COMMENT 'configuration',
                                      `enabled` tinyint(4) null DEFAULT 1 COMMENT 'is enable',
                                      `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                      `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                         `creator` int(11) null DEFAULT 1 COMMENT 'creator',
                                   `updater` int(11)  null DEFAULT 1 COMMENT 'updater'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `dinky_catalogue` (
                                 `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                                 `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                                 `task_id` int(11) null DEFAULT null COMMENT 'Job ID',
                                 `name` varchar(100) NOT null COMMENT 'Job Name',
                                 `type` varchar(50) null DEFAULT null COMMENT 'Job Type',
                                 `parent_id` int(11) NOT null DEFAULT 0 COMMENT 'parent ID',
                                 `enabled` tinyint(1) NOT null DEFAULT 1 COMMENT 'is enable',
                                 `is_leaf` tinyint(1) NOT null COMMENT 'is leaf node',
                                 `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                 `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                   `creator` int(11) null DEFAULT null COMMENT 'creator',
                                   `updater` int(11)  null DEFAULT null COMMENT 'updater'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `dinky_cluster` (
                               `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                               `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                               `name` varchar(255) NOT null COMMENT 'cluster instance name',
                               `alias` varchar(255) null DEFAULT null COMMENT 'cluster instance alias',
                               `type` varchar(50) null DEFAULT null COMMENT 'cluster types',
                               `hosts` text null COMMENT 'cluster hosts',
                               `job_manager_host` varchar(255) null DEFAULT null COMMENT 'Job Manager Host',
                               `version` varchar(20) null DEFAULT null COMMENT 'version',
                               `status` int(11) null DEFAULT null COMMENT 'cluster status',
                               `note` varchar(255) null DEFAULT null COMMENT 'note',
                               `auto_registers` tinyint(1) null DEFAULT 0 COMMENT 'is auto registration',
                               `cluster_configuration_id` int(11) null DEFAULT null COMMENT 'cluster configuration id',
                               `task_id` int(11) null DEFAULT null COMMENT 'task ID',
                               `enabled` tinyint(1) NOT null DEFAULT 1 COMMENT 'is enable',
                               `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                               `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                               `creator` int(11) null DEFAULT null COMMENT 'creator',
                               `updater` int(11)  null DEFAULT null COMMENT 'updater'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `dinky_cluster_configuration` (
                                             `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                                             `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                                             `name` varchar(255) NOT null COMMENT 'cluster configuration name',
                                             `type` varchar(50) null DEFAULT null COMMENT 'cluster type',
                                             `config_json` text null COMMENT 'json of configuration',
                                             `is_available` tinyint(1) NOT null DEFAULT 0 COMMENT 'is available',
                                             `note` varchar(255) null DEFAULT null COMMENT 'note',
                                             `enabled` tinyint(1) NOT null DEFAULT 1 COMMENT 'is enable',
                                             `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                             `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                             `creator` int(11) null DEFAULT null COMMENT 'creator',
                                           `updater` int(11)  null DEFAULT null COMMENT 'updater'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `dinky_database` (
                                `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                                `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                                `name` varchar(30) NOT null COMMENT 'database name',
                                `group_name` varchar(255) null DEFAULT 'Default' COMMENT 'database belong group name',
                                `type` varchar(50) NOT null COMMENT 'database type',
                                `connect_config` text NOT null COMMENT 'database type',
                                `note` varchar(255) null DEFAULT null COMMENT 'note',
                                `flink_config` text null COMMENT 'Flink configuration',
                                `flink_template` text null COMMENT 'Flink template',
                                `db_version` varchar(255) null DEFAULT null COMMENT 'version，such as: 11g of oracle ，2.2.3 of hbase',
                                `status` tinyint(1) null DEFAULT null COMMENT 'heartbeat status',
                                `health_time` datetime(0) null DEFAULT null COMMENT 'last heartbeat time of trigger',
                                `heartbeat_time` datetime(0) null DEFAULT null COMMENT 'last heartbeat time',
                                `enabled` tinyint(1) NOT null DEFAULT 1 COMMENT 'is enable',
                                `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                `creator` int(11) null DEFAULT null COMMENT 'creator',
                               `updater` int(11)  null DEFAULT null COMMENT 'updater'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `dinky_flink_document` (
                                      `id` int(11) NOT null AUTO_INCREMENT COMMENT 'id',
                                      `category` varchar(255) null DEFAULT null COMMENT 'document category',
                                      `type` varchar(255) null DEFAULT null COMMENT 'document type',
                                      `subtype` varchar(255) null DEFAULT null COMMENT 'document subtype',
                                      `name` varchar(255) null DEFAULT null COMMENT 'document name',
                                      `description` longtext null,
                                      `fill_value` longtext null COMMENT 'fill value',
                                      `version` varchar(255) null DEFAULT null COMMENT 'document version such as:(flink1.12,flink1.13,flink1.14,flink1.15)',
                                      `like_num` int(11) null DEFAULT 0 COMMENT 'like number',
                                      `enabled` tinyint(1) NOT null DEFAULT 0 COMMENT 'is enable',
                                      `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                      `update_time` datetime(0) null DEFAULT null COMMENT 'update_time',
                                      `creator` int(11) null DEFAULT null COMMENT 'creator',
                                   `updater` int(11)  null DEFAULT null COMMENT 'updater'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;


CREATE TABLE `dinky_fragment` (
                                `id` int(11) NOT null AUTO_INCREMENT COMMENT 'id',
                                `name` varchar(50) NOT null COMMENT 'fragment name',
                                `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                                `fragment_value` text NOT null COMMENT 'fragment value',
                                `note` text null COMMENT 'note',
                                `enabled` tinyint(4) null DEFAULT 1 COMMENT 'is enable',
                                `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                `creator` int(11) null DEFAULT null COMMENT 'creator',
                               `updater` int(11)  null DEFAULT null COMMENT 'updater'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `dinky_history` (
                               `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                               `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                               `cluster_id` int(11) NOT null DEFAULT 0 COMMENT 'cluster ID',
                               `cluster_configuration_id` int(11) null DEFAULT null COMMENT 'cluster configuration id',
                               `session` varchar(255) null DEFAULT null COMMENT 'session',
                               `job_id` varchar(255) null DEFAULT null COMMENT 'Job ID',
                               `job_name` varchar(255) null DEFAULT null COMMENT 'Job Name',
                               `job_manager_address` varchar(255) null DEFAULT null COMMENT 'JJobManager Address',
                               `batch_model` boolean null DEFAULT false COMMENT 'is batch model',
                               `status` int(11) NOT null DEFAULT 0 COMMENT 'status',
                               `type` varchar(50) null DEFAULT null COMMENT 'job type',
                               `statement` text null COMMENT 'statement set',
                               `error` text null COMMENT 'error message',
                               `result` text null COMMENT 'result set',
                               `config_json` text null COMMENT 'config json',
                               `start_time` datetime(0) null DEFAULT null COMMENT 'job start time',
                               `end_time` datetime(0) null DEFAULT null COMMENT 'job end time',
                               `task_id` int(11) null DEFAULT null COMMENT 'task ID'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;


CREATE TABLE `dinky_job_history` (
                                   `id` int(11) NOT null COMMENT 'id',
                                   `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                                   `job_json` text null COMMENT 'Job information json',
                                   `exceptions_json` text null COMMENT 'error message json',
                                   `checkpoints_json` text null COMMENT 'checkpoints json',
                                   `checkpoints_config_json` text null COMMENT 'checkpoints configuration json',
                                   `config_json` text null COMMENT 'configuration',
                                   `cluster_json` text null COMMENT 'cluster instance configuration',
                                   `cluster_configuration_json` text null COMMENT 'cluster config',
                                   `update_time` datetime(0) null DEFAULT null COMMENT 'update time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `dinky_job_instance` (
                                    `id` int(11) NOT null AUTO_INCREMENT COMMENT 'id',
                                    `name` varchar(255) null DEFAULT null COMMENT 'job instance name',
                                    `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                                    `task_id` int(11) null DEFAULT null COMMENT 'task ID',
                                    `step` int(11) null DEFAULT null COMMENT 'job lifecycle',
                                    `cluster_id` int(11) null DEFAULT null COMMENT 'cluster ID',
                                    `jid` varchar(50) null DEFAULT null COMMENT 'Flink JobId',
                                    `status` varchar(50) null DEFAULT null COMMENT 'job instance status',
                                    `history_id` int(11) null DEFAULT null COMMENT 'execution history ID',
                                    `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                    `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                    `finish_time` datetime(0) null DEFAULT null COMMENT 'finish time',
                                    `duration` bigint(20) null DEFAULT null COMMENT 'job duration',
                                    `error` text null COMMENT 'error logs',
                                    `failed_restart_count` int(11) null DEFAULT null COMMENT 'failed restart count',
                                   `creator` int(11) null DEFAULT null COMMENT 'creator',
                                   `updater` int(11)  null DEFAULT null COMMENT 'updater',
                                   `operator` int(11)  null DEFAULT null COMMENT 'operator'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;


CREATE TABLE `dinky_role` (
                            `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                            `tenant_id` int(11) NOT null COMMENT 'tenant id',
                            `role_code` varchar(64) NOT null COMMENT 'role code',
                            `role_name` varchar(64) NOT null COMMENT 'role name',
                            `is_delete` tinyint(1) NOT null DEFAULT 0 COMMENT 'is delete',
                            `note` varchar(255) null DEFAULT null COMMENT 'note',
                            `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                            `update_time` datetime(0) null DEFAULT null COMMENT 'update time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;



CREATE TABLE `dinky_savepoints` (
                                  `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                                  `task_id` int(11) NOT null COMMENT 'task ID',
                                  `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                                  `name` varchar(255) NOT null COMMENT 'task name',
                                  `type` varchar(255) NOT null COMMENT 'savepoint type',
                                  `path` varchar(255) NOT null COMMENT 'savepoint path',
                                  `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                  `creator` int(11) null DEFAULT null COMMENT 'creator'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;


CREATE TABLE `dinky_sys_config` (
                                  `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                                  `name` varchar(255) NOT null COMMENT 'configuration name',
                                  `value` text null COMMENT 'configuration value',
                                  `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                  `update_time` datetime(0) null DEFAULT null COMMENT 'update time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `dinky_task` (
                            `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                            `name` varchar(255) NOT null COMMENT 'Job name',
                            `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                            `dialect` varchar(50) null DEFAULT null COMMENT 'dialect',
                            `type` varchar(50) null DEFAULT null COMMENT 'Job type',
                            `check_point` int(11) null DEFAULT null COMMENT 'CheckPoint trigger seconds',
                            `save_point_strategy` int(11) null DEFAULT null COMMENT 'SavePoint strategy',
                            `save_point_path` varchar(255) null DEFAULT null COMMENT 'SavePointPath',
                            `parallelism` int(11) null DEFAULT null COMMENT 'parallelism',
                            `fragment` tinyint(1) null DEFAULT 0 COMMENT 'fragment',
                            `statement_set` tinyint(1) null DEFAULT 0 COMMENT 'enable statement set',
                            `batch_model` tinyint(1) null DEFAULT 0 COMMENT 'use batch model',
                            `cluster_id` int(11) null DEFAULT null COMMENT 'Flink cluster ID',
                            `cluster_configuration_id` int(11) null DEFAULT null COMMENT 'cluster configuration ID',
                            `database_id` int(11) null DEFAULT null COMMENT 'database ID',
                            `env_id` int(11) null DEFAULT null COMMENT 'env id',
                            `alert_group_id` bigint(20) null DEFAULT null COMMENT 'alert group id',
                            `config_json` text null COMMENT 'configuration json',
                            `note` varchar(255) null DEFAULT null COMMENT 'Job Note',
                            `step` int(11) null DEFAULT 1 COMMENT 'Job lifecycle',
                            `job_instance_id` bigint(20) null DEFAULT null COMMENT 'job instance id',
                            `enabled` tinyint(1) NOT null DEFAULT 1 COMMENT 'is enable',
                            `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                            `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                            `version_id` int(11) null DEFAULT null COMMENT 'version id',
                            `statement` text null DEFAULT null COMMENT 'statement',
                            `creator` int(11) null DEFAULT null COMMENT 'creator',
                           `updater` int(11)  null DEFAULT null COMMENT 'updater',
                           `operator` int(11)  null DEFAULT null COMMENT 'operator'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;


CREATE TABLE `dinky_task_version` (
                                    `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                                    `task_id` int(11) NOT null COMMENT 'task ID ',
                                    `tenant_id` int(11) NOT null DEFAULT 1 COMMENT 'tenant id',
                                    `version_id` int(11) NOT null COMMENT 'version ID ',
                                    `statement` text null COMMENT 'flink sql statement',
                                    `name` varchar(255) NOT null COMMENT 'version name',
                                    `dialect` varchar(50) null DEFAULT null COMMENT 'dialect',
                                    `type` varchar(50) null DEFAULT null COMMENT 'type',
                                    `task_configure` text NOT null COMMENT 'task configuration',
                                    `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                     `creator` int(11) null DEFAULT null COMMENT 'creator'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `dinky_tenant` (
                              `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                              `tenant_code` varchar(64) NOT null COMMENT 'tenant code',
                              `is_delete` tinyint(1) NOT null DEFAULT 0 COMMENT 'is delete',
                              `note` varchar(255) null DEFAULT null COMMENT 'note',
                              `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                              `update_time` datetime(0) null DEFAULT null COMMENT 'update time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;


CREATE TABLE `dinky_udf_template` (
                                    `id` int(11) NOT null AUTO_INCREMENT,
                                    `name` varchar(100) null DEFAULT null COMMENT 'template name',
                                    `code_type` varchar(10) null DEFAULT null COMMENT 'code type',
                                    `function_type` varchar(10) null DEFAULT null COMMENT 'function type',
                                    `template_code` longtext null COMMENT 'code',
                                    `enabled` tinyint(1) not null DEFAULT 1 COMMENT 'is enable',
                                    `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                    `update_time` datetime DEFAULT null ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                     `creator` int(11) null DEFAULT null COMMENT 'creator',
                                   `updater` int(11)  null DEFAULT null COMMENT 'updater'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;



CREATE TABLE `dinky_user` (
                            `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                            `username` varchar(50) NOT null COMMENT 'username',
                             `user_type`   int    DEFAULT 0 NOT null COMMENT 'login type (0:LOCAL,1:LDAP)',
                            `password` varchar(50) null DEFAULT null COMMENT 'password',
                            `nickname` varchar(50) null DEFAULT null COMMENT 'nickname',
                            `worknum` varchar(50) null DEFAULT null COMMENT 'worknum',
                            `avatar` blob null COMMENT 'avatar',
                            `mobile` varchar(20) null DEFAULT null COMMENT 'mobile phone',
                            `enabled` tinyint(1) NOT null DEFAULT 1 COMMENT 'is enable',
                            `super_admin_flag` tinyint(1) DEFAULT '0' COMMENT 'is super admin(0:false,1true)',
                            `is_delete` tinyint(1) NOT null DEFAULT 0 COMMENT 'is delete',
                            `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                            `update_time` datetime(0) null DEFAULT null COMMENT 'update time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;


CREATE TABLE `dinky_user_role` (
                                 `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                                 `user_id` int(11) NOT null COMMENT 'user id',
                                 `role_id` int(11) NOT null COMMENT 'role id',
                                 `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                 `update_time` datetime(0) null DEFAULT null COMMENT 'update time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;


CREATE TABLE `dinky_user_tenant` (
                                   `id` int(11) NOT null AUTO_INCREMENT COMMENT 'ID',
                                   `user_id` int(11) NOT null COMMENT 'user id',
                                   `tenant_id` int(11) NOT null COMMENT 'tenant id',
                                    `tenant_admin_flag` tinyint DEFAULT '0' COMMENT 'tenant admin flag(0:false,1:true)',
                                   `create_time` datetime(0) null DEFAULT null COMMENT 'create time',
                                   `update_time` datetime(0) null DEFAULT null COMMENT 'update time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;


CREATE TABLE `metadata_column` (
                                 `column_name` varchar(255) NOT null COMMENT 'column name',
                                 `column_type` varchar(255) NOT null COMMENT 'column type, such as : Physical , Metadata , Computed , WATERMARK',
                                 `data_type` varchar(255) NOT null COMMENT 'data type',
                                 `expr` varchar(255) null DEFAULT null COMMENT 'expression',
                                 `description` varchar(255) NOT null COMMENT 'column description',
                                 `table_id` int(11) NOT null COMMENT 'table id',
                                 `primary` bit(1) null DEFAULT null COMMENT 'table primary key',
                                 `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                 `create_time` datetime(0) NOT null DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'create time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `metadata_database` (
                                   `id` int(11) NOT null AUTO_INCREMENT COMMENT 'id',
                                   `database_name` varchar(255) NOT null COMMENT 'database name',
                                   `description` varchar(255) null DEFAULT null COMMENT 'database description',
                                   `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                   `create_time` datetime(0) null DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'create time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `metadata_database_property` (
                                            `key` varchar(255) NOT null COMMENT 'key',
                                            `value` varchar(255) null DEFAULT null COMMENT 'value',
                                            `database_id` int(11) NOT null COMMENT 'database id',
                                            `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                            `create_time` datetime(0) NOT null DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'create time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `metadata_function` (
                                   `id` int(11) NOT null AUTO_INCREMENT COMMENT '主键',
                                   `function_name` varchar(255) NOT null COMMENT 'function name',
                                   `class_name` varchar(255) NOT null COMMENT 'class name',
                                   `database_id` int(11) NOT null COMMENT 'database id',
                                   `function_language` varchar(255) null DEFAULT null COMMENT 'function language',
                                   `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                   `create_time` datetime(0) null DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'create time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `metadata_table` (
                                `id` int(11) NOT null AUTO_INCREMENT COMMENT '主键',
                                `table_name` varchar(255) NOT null COMMENT 'table name',
                                `table_type` varchar(255) NOT null COMMENT 'type，such as：database,table,view',
                                `database_id` int(11) NOT null COMMENT 'database id',
                                `description` varchar(255) null DEFAULT null COMMENT 'table description',
                                `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                `create_time` datetime(0) null DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'create time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

CREATE TABLE `metadata_table_property` (
                                         `key` varchar(255) NOT null COMMENT 'key',
                                         `value` mediumtext null COMMENT 'value',
                                         `table_id` int(11) NOT null COMMENT 'table id',
                                         `update_time` datetime(0) null DEFAULT null COMMENT 'update time',
                                         `create_time` datetime(0) NOT null DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'create tiime'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;
-- ----------------------------
-- Records of metadata_table_property
-- ----------------------------
-- ----------------------------
-- Table structure for dinky_row_permissions
-- ----------------------------

CREATE TABLE dinky_row_permissions (
                                             id int PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
                                             role_id int NOT null COMMENT '角色ID',
                                             table_name varchar(255) null COMMENT '表名',
                                             expression varchar(255) null COMMENT '表达式',
                                             create_time datetime null COMMENT '创建时间',
                                             update_time datetime null COMMENT '更新时间',
                                            `creator` int(11) null DEFAULT null COMMENT 'creator',
                                           `updater` int(11)  null DEFAULT null COMMENT 'updater'
);
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `dinky_git_project` (
                                   `id` bigint(20) NOT null AUTO_INCREMENT,
                                   `tenant_id` bigint(20) NOT null,
                                   `name` varchar(255) NOT null,
                                   `url` varchar(1000) NOT null,
                                   `branch` varchar(1000) NOT null,
                                   `username` varchar(255) DEFAULT null,
                                   `password` varchar(255) DEFAULT null,
                                   `private_key` varchar(255) DEFAULT null COMMENT 'keypath',
                                   `pom` varchar(255) DEFAULT null,
                                   `build_args` varchar(255) DEFAULT null,
                                   `code_type` tinyint(4) DEFAULT null COMMENT 'code type(1-java,2-python)',
                                   `type` tinyint(4) NOT null COMMENT '1-http ,2-ssh',
                                   `last_build` datetime DEFAULT null,
                                   `description` varchar(255) DEFAULT null,
                                   `build_state` tinyint(2) NOT null DEFAULT '0' COMMENT '0-notStart 1-process 2-failed 3-success',
                                   `build_step` tinyint(2) NOT null DEFAULT '0',
                                   `enabled` tinyint(1) NOT null DEFAULT '1' COMMENT '0-disable 1-enable',
                                   `udf_class_map_list` text COMMENT 'scan udf class',
                                   `order_line` int(11) NOT null DEFAULT '1' COMMENT 'order',
                                   `create_time` datetime NOT null DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                   `update_time` datetime NOT null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                    `creator` int(11) null DEFAULT null COMMENT 'creator',
                                   `updater` int(11)  null DEFAULT null COMMENT 'updater',
                                   `operator` int(11)  null DEFAULT null COMMENT 'operator'

) ENGINE = InnoDB;


CREATE TABLE `dinky_metrics` (
                                 `id` int(11) NOT null AUTO_INCREMENT,
                                 `task_id` int(255) DEFAULT null,
                                 `vertices` varchar(255) DEFAULT null,
                                 `metrics` varchar(255) DEFAULT null,
                                 `position` int(11) DEFAULT null,
                                 `show_type` varchar(255) DEFAULT null,
                                 `show_size` varchar(255) DEFAULT null,
                                 `title` CLOB DEFAULT null,
                                 `layout_name` varchar(255) DEFAULT null,
                                 `create_time` datetime DEFAULT null,
                                 `update_time` datetime DEFAULT null
) ENGINE = InnoDB;


CREATE TABLE `dinky_resources` (
                                   `id` int(11) NOT null AUTO_INCREMENT COMMENT 'key',
                                   `file_name` varchar(64) DEFAULT null COMMENT 'file name',
                                   `description` varchar(255) DEFAULT null,
                                   `user_id` int(11) DEFAULT null COMMENT 'user id',
                                   `type` tinyint(4) DEFAULT null COMMENT 'resource type,0:FILE，1:UDF',
                                   `size` bigint(20) DEFAULT null COMMENT 'resource size',
                                   `pid` int(11) DEFAULT null,
                                   `full_name` text DEFAULT null,
                                   `is_directory` tinyint(4) DEFAULT null,
                                   `create_time` datetime NOT null DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                   `update_time` datetime NOT null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                   `creator` int(11) null DEFAULT null COMMENT 'creator',
                                   `updater` int(11)  null DEFAULT null COMMENT 'updater'
) ENGINE = InnoDB;


-- ----------------------------
-- Table structure for dinky_sys_login_log
-- ----------------------------

CREATE TABLE `dinky_sys_login_log` (
  `id` int(11) NOT null AUTO_INCREMENT COMMENT 'key',
  `user_id` int(11) NOT null COMMENT 'user id',
  `username` varchar(60)  NOT null COMMENT 'username',
  `login_type` int NOT null COMMENT 'login type（0:LOCAL,1:LDAP）',
  `ip` varchar(40)  NOT null COMMENT 'ip addr',
  `status` int NOT null COMMENT 'login status',
  `msg` text  NOT null COMMENT 'status msg',
  `create_time` datetime NOT null COMMENT 'create time',
  `access_time` datetime DEFAULT null COMMENT 'access time',
  `update_time` datetime NOT null,
  `is_deleted` tinyint(1) NOT null DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;


-- ----------------------------
-- Table structure for dinky_sys_operate_log
-- ----------------------------

CREATE TABLE `dinky_sys_operate_log`  (
  `id` bigint NOT null AUTO_INCREMENT COMMENT 'id',
  `module_name` varchar(50) DEFAULT '' COMMENT 'module name',
  `business_type` int null DEFAULT 0 COMMENT 'business type',
  `method` varchar(100) null DEFAULT '' COMMENT 'method name',
  `request_method` varchar(10) null DEFAULT '' COMMENT 'request method',
  `operate_name` varchar(50) DEFAULT '' COMMENT 'operate name',
  `operate_user_id` int NOT null COMMENT 'operate user id',
  `operate_url` varchar(255) DEFAULT '' COMMENT 'operate url',
  `operate_ip` varchar(50) DEFAULT '' COMMENT 'ip',
  `operate_location` varchar(255) DEFAULT '' COMMENT 'operate location',
  `operate_param` longtext DEFAULT '' COMMENT 'request param',
  `json_result` longtext DEFAULT null COMMENT 'return json result',
  `status` int null DEFAULT null COMMENT 'operate status',
  `error_msg` longtext DEFAULT null COMMENT 'error msg',
  `operate_time` datetime(0) DEFAULT null COMMENT 'operate time',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB;



-- ----------------------------
-- Table structure for dinky_sys_menu
-- ----------------------------

create table `dinky_sys_menu` (
                                  `id` bigint not null auto_increment comment ' id',
                                  `parent_id` bigint not null comment 'parent menu id',
                                  `name` varchar(64) not null comment 'menu button name',
                                  `path` varchar(64) default null comment 'routing path',
                                  `component` varchar(64) default null comment 'routing component component',
                                  `perms` varchar(64) default null comment 'authority id',
                                  `icon` varchar(64) default null comment 'icon',
                                  `type` char(1) default null comment 'type(M:directory C:menu F:button)',
                                  `display` tinyint default 1 comment 'whether the menu is displayed',
                                  `order_num` int default null comment 'sort',
                                  `create_time` datetime not null default current_timestamp comment 'create time',
                                  `update_time` datetime not null default current_timestamp on update current_timestamp comment 'modify time',
                                  `note` varchar(255) default null comment 'note',
                                  primary key (`id`)
) engine=innodb ;

-- ----------------------------
-- Table structure dinky_sys_role_menu
-- ----------------------------

CREATE TABLE `dinky_sys_role_menu` (
                                       `id` bigint NOT null AUTO_INCREMENT COMMENT 'id',
                                       `role_id` bigint NOT null COMMENT 'role id',
                                       `menu_id` bigint NOT null COMMENT 'menu id',
                                       `create_time` datetime not null default current_timestamp comment 'create time',
                                       `update_time` datetime not null default current_timestamp on update current_timestamp comment 'modify time',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `un_role_menu_inx` (`role_id`,`menu_id`)
) ENGINE=InnoDB ;



-- ----------------------------
-- Table structure updater
-- ----------------------------

CREATE TABLE `dinky_sys_token` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                   `token_value` varchar(255) NOT NULL COMMENT 'token value',
                                   `user_id` bigint NOT NULL COMMENT 'user id',
                                   `role_id` bigint NOT NULL COMMENT 'role id',
                                   `tenant_id` bigint NOT NULL COMMENT 'tenant id',
                                   `expire_type` tinyint NOT NULL COMMENT '1: never expire, 2: expire after a period of time, 3: expire at a certain time',
                                   `expire_start_time` datetime DEFAULT NULL COMMENT 'expire start time ,when expire_type = 3 , it is the start time of the period',
                                   `expire_end_time` datetime DEFAULT NULL COMMENT 'expire end time ,when expire_type = 2,3 , it is the end time of the period',
                                   `create_time` datetime NOT NULL COMMENT 'create time',
                                   `update_time` datetime NOT NULL COMMENT 'modify time',
                                   `source` tinyint(2) DEFAULT NULL COMMENT '1:login 2:custom',
                                  `creator` bigint DEFAULT NULL COMMENT 'creator',
                                   `updater` bigint DEFAULT NULL COMMENT 'updater',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='token management';



-- ----------------------------
-- Table structure dinky_sys_alert
-- ----------------------------

create table if not exists dinky_alert_template
(
    id               int auto_increment
        primary key  COMMENT 'id',
    name             varchar(20)            COMMENT 'template name',
    template_content text              null COMMENT 'template content',
    enabled          tinyint default 1 null COMMENT 'is enable',
    create_time      datetime          null COMMENT 'create time',
    update_time      datetime          null COMMENT 'update time',
    `creator` bigint DEFAULT NULL COMMENT 'creator',
                                   `updater` bigint DEFAULT NULL COMMENT 'updater'
);


create table if not exists dinky_alert_rules
(
    id                 int auto_increment
        primary key comment 'id',
    name               varchar(40)  unique     not null comment 'rule name',
    rule               text              null comment 'specify rule',
    template_id        int               null comment 'template id',
    rule_type          varchar(10)       null comment 'alert rule type',
    trigger_conditions varchar(20)       null comment 'trigger conditions',
    description        text              null comment 'description',
    enabled            tinyint default 1 null comment 'is enable',
    create_time        datetime          null comment 'create time',
    update_time        datetime          null comment 'update time',
    `creator` bigint DEFAULT NULL COMMENT 'creator',
`updater` bigint DEFAULT NULL COMMENT 'updater'
);




CREATE TABLE IF NOT EXISTS `dinky_udf_manage` (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `name` varchar(50) DEFAULT NULL COMMENT 'udf name',
                                    `class_name` varchar(50) DEFAULT NULL COMMENT 'Complete class name',
                                    `task_id` int(11) DEFAULT NULL COMMENT 'task id',
                                    `resources_id` int(11) DEFAULT NULL COMMENT 'resources id',
                                    `enabled` tinyint(1) DEFAULT 1 COMMENT 'is enable',
                                    `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                    `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                    `creator` bigint DEFAULT NULL COMMENT 'creator',
                                   `updater` bigint DEFAULT NULL COMMENT 'updater'
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;
