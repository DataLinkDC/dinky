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


delete from `dinky_task_statement` where id in (select id from `dinky_task` where `name` = 'dlink_default_catalog');

delete from `dinky_task` where `name` = 'dlink_default_catalog';

update dinky_udf_template set template_code= 'from pyflink.table import DataTypes\nfrom pyflink.table.udf import udf\n\n\n@udf(result_type=DataTypes.STRING())\ndef ${className}(variable1:str):\n    return \'\''  where id = 5;

--  update flinkClusterConfiguration
SET @userJarPath = ( SELECT VALUE FROM dinky_sys_config WHERE `name` = 'sqlSubmitJarPath' LIMIT 1 );
UPDATE dinky_cluster_configuration SET config_json =( SELECT JSON_SET( config_json, '$.userJarPath', @userJarPath));


-- Fix spelling error
update dinky_task set dialect = 'KubernetesApplication' where dialect = 'KubernetesApplaction';

-- change dinky_udf_template table structure
alter table dinky_udf_template alter column `enabled` set default 1;
alter table dinky_udf_template modify column `name` varchar(100);
alter table dinky_udf_template modify column `template_code` longtext;


-- change dinky_udf table structure
alter table dinky_udf modify column `name` varchar(200);
alter table dinky_udf modify column `class_name` varchar(200);
alter table dinky_udf modify column `class_name` varchar(200);
alter table dinky_udf alter column `enable` set default 1;
alter table dinky_udf modify column `source_code` longtext;

-- change data source of type
update dinky_database set `type` = 'MySQL' where `type` = 'Mysql';
update dinky_database set `type` = 'PostgreSQL' where `type` = 'PostgreSql';
update dinky_database set `type` = 'SQLServer' where `type` = 'SqlServer';

INSERT INTO `dinky_git_project` (`id`, `tenant_id`, `name`, `url`, `branch`, `username`, `password`, `private_key`, `pom`, `build_args`, `code_type`, `type`, `last_build`, `description`, `build_state`, `build_step`, `enabled`, `udf_class_map_list`, `order_line`) VALUES (1, 1, 'java-udf', 'https://github.com/zackyoungh/dinky-quickstart-java.git', 'master', NULL, NULL, NULL, NULL, '-P flink-1.14', 1, 1, NULL, NULL, 0, 0, 1, '[]', 1);
INSERT INTO `dinky_git_project` (`id`, `tenant_id`, `name`, `url`, `branch`, `username`, `password`, `private_key`, `pom`, `build_args`, `code_type`, `type`, `last_build`, `description`, `build_state`, `build_step`, `enabled`, `udf_class_map_list`, `order_line`) VALUES (2, 1, 'python-udf', 'https://github.com/zackyoungh/dinky-quickstart-python.git', 'master', NULL, NULL, NULL, NULL, '', 2, 1, NULL, NULL, 0, 0, 1, '[]',2);


UPDATE `dinky_sys_config` SET  `name` = 'flink.settings.useRestAPI' where `name` = 'useRestAPI';
UPDATE `dinky_sys_config` SET  `name` = 'flink.settings.sqlSeparator' where `name` = 'sqlSeparator';
UPDATE `dinky_sys_config` SET  `name` = 'flink.settings.jobIdWait' where `name` = 'jobIdWait';

INSERT INTO `dinky_resources` (`id`, `file_name`, `description`, `user_id`, `type`, `size`, `pid`, `full_name`, `is_directory`) VALUES (0, 'Root', 'main folder', 1, 0, 0, -1, '/Root', 1);


-- ----------------------------
-- Records of dinky_sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `dinky_sys_menu` VALUES (1, -1, '首页', '/home', './Home', 'home', 'HomeOutlined', 'C', 0, 1, '2023-08-11 14:06:52', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (2, -1, '运维中心', '/devops', null, 'devops', 'ControlOutlined', 'M', 0, 3, '2023-08-11 14:06:52', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (3, -1, '注册中心', '/registration', null, 'registration', 'AppstoreOutlined', 'M', 0, 4, '2023-08-11 14:06:52', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (4, -1, '认证中心', '/auth', null, 'auth', 'SafetyCertificateOutlined', 'M', 0, 5, '2023-08-11 14:06:52', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (5, -1, '数据开发', '/datastudio', './DataStudio', 'datastudio', 'CodeOutlined', 'C', 0, 2, '2023-08-11 14:06:52', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (6, -1, '配置中心', '/settings', null, 'settings', 'SettingOutlined', 'M', 0, 6, '2023-08-11 14:06:53', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (7, -1, '关于', '/about', './Other/About', 'about', 'SmileOutlined', 'C', 0, 8, '2023-08-11 14:06:53', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (8, -1, '监控', '/metrics', './Metrics', 'metrics', 'DashboardOutlined', 'C', 0, 7, '2023-08-11 14:06:53', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (9, 3, '集群', '/registration/cluster', null, 'registration:cluster', 'GoldOutlined', 'M', 0, 14, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (10, 3, '数据源', '/registration/database', './RegCenter/DataSource', 'registration:database', 'DatabaseOutlined', 'M', 0, 13, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (11, -1, '个人中心', '/account/center', './Other/PersonCenter', 'account:center', 'UserOutlined', 'C', 0, 9, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (12, 3, '告警', '/registration/alert', null, 'registration:alert', 'AlertOutlined', 'M', 0, 14, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (13, 3, '文档', '/registration/document', './RegCenter/Document', 'registration:document', 'BookOutlined', 'C', 0, 17, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (14, 3, '全局变量', '/registration/fragment', './RegCenter/GlobalVar', 'registration:fragment', 'RocketOutlined', 'C', 0, 18, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (15, 3, 'Git 项目', '/registration/gitprojects', './RegCenter/GitProject', 'registration:gitprojects', 'GithubOutlined', 'C', 0, 19, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (16, 3, 'UDF 模版', '/registration/udf', './RegCenter/UDF', 'registration:udf', 'ToolOutlined', 'C', 0, 20, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (17, 2, 'job-detail', '/devops/job-detail', './DevOps/JobDetail', 'devops:job-detail', null, 'C', 0, 13, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (18, 2, 'job', '/devops/joblist', './DevOps', 'devops:joblist', null, 'C', 0, 12, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (19, 3, '资源中心', '/registration/resource', './RegCenter/Resource', 'registration:resource', 'FileZipOutlined', 'C', 0, 21, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (20, 4, '角色', '/auth/role', './AuthCenter/Role', 'auth:role', 'TeamOutlined', 'C', 0, 24, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (21, 4, '用户', '/auth/user', './AuthCenter/User', 'auth:user', 'UserOutlined', 'C', 0, 23, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (22, 4, '菜单', '/auth/menu', './AuthCenter/Menu', 'auth:menu', 'MenuOutlined', 'C', 0, 25, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (23, 4, '租户', '/auth/tenant', './AuthCenter/Tenant', 'auth:tenant', 'SecurityScanOutlined', 'C', 0, 26, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (24, 6, '全局设置', '/settings/globalsetting', './SettingCenter/GlobalSetting', 'settings:globalsetting', 'SettingOutlined', 'C', 0, 29, '2023-08-11 14:06:54', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (25, 6, '系统日志', '/settings/systemlog', './SettingCenter/SystemLogs', 'settings:systemlog', 'InfoCircleOutlined', 'C', 0, 30, '2023-08-11 14:06:55', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (26, 6, '进程', '/settings/process', './SettingCenter/Process', 'settings:process', 'ReconciliationOutlined', 'C', 0, 31, '2023-08-11 14:06:55', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (27, 4, '行权限', '/auth/rowpermissions', './AuthCenter/RowPermissions', 'auth:rowpermissions', 'SafetyCertificateOutlined', 'C', 0, 27, '2023-08-11 14:06:55', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (28, 9, 'Flink 实例', '/registration/cluster/instance', './RegCenter/Cluster/Instance', 'registration:cluster:instance', 'ReconciliationOutlined', 'C', 0, 11, '2023-08-11 14:06:55', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (29, 12, '告警组', '/registration/alert/group', './RegCenter/Alert/AlertGroup', 'registration:alert:group', 'AlertOutlined', 'C', 0, 16, '2023-08-11 14:06:55', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (30, 9, '集群配置', '/registration/cluster/config', './RegCenter/Cluster/Configuration', 'registration:cluster:config', 'SettingOutlined', 'C', 0, 12, '2023-08-11 14:06:55', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (31, 12, '告警实例', '/registration/alert/instance', './RegCenter/Alert/AlertInstance', 'registration:alert:instance', 'AlertFilled', 'C', 0, 15, '2023-08-11 14:06:55', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (32, 1, '作业监控', '/home/jobOverView', 'JobOverView', 'home:jobOverView', 'AntCloudOutlined', 'F', 0, 10, '2023-08-15 16:52:59', '2023-09-25 18:19:46', null);
INSERT INTO `dinky_sys_menu` VALUES (33, 1, '数据开发', '/home/devOverView', 'DevOverView', 'home:devOverView', 'AimOutlined', 'F', 0, 11, '2023-08-15 16:54:47', '2023-09-25 18:20:45', null);
INSERT INTO `dinky_sys_menu` VALUES (34, 5, '项目列表', '/datastudio/left/project', null, 'datastudio:left:project', 'ConsoleSqlOutlined', 'F', 0, 1, '2023-09-01 18:00:39', '2023-09-25 18:22:16', null);
INSERT INTO `dinky_sys_menu` VALUES (35, 5, '元数据', '/datastudio/left/metadata', null, 'datastudio:left:metadata', 'TableOutlined', 'F', 0, 2, '2023-09-01 18:01:09', '2023-09-25 18:22:41', null);
INSERT INTO `dinky_sys_menu` VALUES (36, 5, '结构', '/datastudio/left/structure', null, 'datastudio:left:structure', 'DatabaseOutlined', 'F', 0, 3, '2023-09-01 18:01:30', '2023-09-25 18:28:12', null);
INSERT INTO `dinky_sys_menu` VALUES (37, 5, '作业配置', '/datastudio/right/jobConfig', null, 'datastudio:right:jobConfig', 'SettingOutlined', 'F', 0, 4, '2023-09-01 18:02:15', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (38, 5, '执行配置', '/datastudio/right/executeConfig', null, 'datastudio:right:executeConfig', 'ExperimentOutlined', 'F', 0, 5, '2023-09-01 18:03:08', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (39, 5, '版本历史', '/datastudio/right/historyVision', null, 'datastudio:right:historyVision', 'HistoryOutlined', 'F', 0, 6, '2023-09-01 18:03:29', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (40, 5, '保存点', '/datastudio/right/savePoint', null, 'datastudio:right:savePoint', 'FolderOutlined', 'F', 0, 7, '2023-09-01 18:03:58', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (41, 5, '作业信息', '/datastudio/right/jobInfo', null, 'datastudio:right:jobInfo', 'InfoCircleOutlined', 'F', 0, 8, '2023-09-01 18:04:31', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (42, 5, '控制台', '/datastudio/bottom/console', null, 'datastudio:bottom:console', 'ConsoleSqlOutlined', 'F', 0, 9, '2023-09-01 18:04:56', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (43, 5, '结果', '/datastudio/bottom/result', null, 'datastudio:bottom:result', 'SearchOutlined', 'F', 0, 10, '2023-09-01 18:05:16', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (44, 5, 'BI', '/datastudio/bottom/bi', null, 'datastudio:bottom:bi', 'DashboardOutlined', 'F', 0, 11, '2023-09-01 18:05:43', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (45, 5, '血缘', '/datastudio/bottom/lineage', null, 'datastudio:bottom:lineage', 'PushpinOutlined', 'F', 0, 12, '2023-09-01 18:07:15', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (46, 5, '表数据监控', '/datastudio/bottom/process', null, 'datastudio:bottom:process', 'TableOutlined', 'F', 0, 13, '2023-09-01 18:07:55', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (47, 5, '小工具', '/datastudio/bottom/tool', null, 'datastudio:bottom:tool', 'ToolOutlined', 'F', 0, 14, '2023-09-01 18:08:18', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (48, 28, '新建', '/registration/cluster/instance/new', null, 'registration:cluster:instance:new', 'PlusOutlined', 'F', 0, 15, '2023-09-06 08:56:45', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (49, 28, '回收', '/registration/cluster/instance/recovery', null, 'registration:cluster:instance:recovery', 'DeleteFilled', 'F', 0, 16, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (50, 28, '编辑', '/registration/cluster/instance/edit', null, 'registration:cluster:instance:edit', 'EditOutlined', 'F', 0, 17, '2023-09-06 08:56:45', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (51, 28, '删除', '/registration/cluster/instance/delete', null, 'registration:cluster:instance:delete', 'DeleteOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (52, 30, '新建', '/registration/cluster/config/new', null, 'registration:cluster:config:new', 'PlusOutlined', 'F', 0, 17, '2023-09-06 09:00:31', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (53, 30, '编辑', '/registration/cluster/config/edit', null, 'registration:cluster:config:edit', 'EditOutlined', 'F', 0, 17, '2023-09-06 08:56:45', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (54, 30, '删除', '/registration/cluster/config/delete', null, 'registration:cluster:config:delete', 'DeleteOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (55, 10, '新建', '/registration/database/new', null, 'registration:database:new', 'PlusOutlined', 'F', 0, 18, '2023-09-06 09:01:05', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (56, 10, '编辑', '/registration/database/edit', null, 'registration:database:edit', 'EditOutlined', 'F', 0, 17, '2023-09-06 08:56:45', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (57, 10, '删除', '/registration/database/delete', null, 'registration:database:delete', 'DeleteOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (58, 31, '新建', '/registration/alert/instance/new', null, 'registration:alert:instance:new', 'PlusOutlined', 'F', 0, 18, '2023-09-06 09:01:05', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (59, 31, '编辑', '/registration/alert/instance/edit', null, 'registration:alert:instance:edit', 'EditOutlined', 'F', 0, 17, '2023-09-06 08:56:45', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (60, 31, '删除', '/registration/alert/instance/delete', null, 'registration:alert:instance:delete', 'DeleteOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (61, 29, '新建', '/registration/alert/group/new', null, 'registration:alert:group:new', 'PlusOutlined', 'F', 0, 18, '2023-09-06 09:01:05', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (62, 29, '编辑', '/registration/alert/group/edit', null, 'registration:alert:group:edit', 'EditOutlined', 'F', 0, 17, '2023-09-06 08:56:45', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (63, 29, '删除', '/registration/alert/group/delete', null, 'registration:alert:group:delete', 'DeleteOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (64, 13, '新建', '/registration/document/new', null, 'registration:document:new', 'PlusOutlined', 'F', 0, 18, '2023-09-06 09:01:05', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (65, 13, '编辑', '/registration/document/edit', null, 'registration:document:edit', 'EditOutlined', 'F', 0, 17, '2023-09-06 08:56:45', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (66, 13, '删除', '/registration/document/delete', null, 'registration:document:delete', 'DeleteOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (67, 13, '启用', '/registration/document/enable', null, 'registration:document:enable', 'EyeOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (68, 14, '新建', '/registration/fragment/new', null, 'registration:fragment:new', 'PlusOutlined', 'F', 0, 18, '2023-09-06 09:01:05', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (69, 14, '编辑', '/registration/fragment/edit', null, 'registration:fragment:edit', 'EditOutlined', 'F', 0, 17, '2023-09-06 08:56:45', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (70, 14, '删除', '/registration/fragment/delete', null, 'registration:fragment:delete', 'DeleteOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (71, 14, '启用', '/registration/fragment/enable', null, 'registration:fragment:enable', 'EditOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (72, 15, '新建', '/registration/gitprojects/new', null, 'registration:gitprojects:new', 'PlusOutlined', 'F', 0, 18, '2023-09-06 09:01:05', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (73, 15, '编辑', '/registration/gitprojects/edit', null, 'registration:gitprojects:edit', 'EditOutlined', 'F', 0, 17, '2023-09-06 08:56:45', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (74, 15, '删除', '/registration/gitprojects/delete', null, 'registration:gitprojects:delete', 'DeleteOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (75, 15, '启用', '/registration/gitprojects/enable', null, 'registration:gitprojects:enable', 'EyeOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (76, 15, '构建', '/registration/gitprojects/build', null, 'registration:gitprojects:build', 'PlaySquareOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (77, 15, '查询', '/registration/gitprojects/search', null, 'registration:gitprojects:search', 'SearchOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (78, 16, '新建', '/registration/udf/new', null, 'registration:udf:new', 'PlusOutlined', 'F', 0, 18, '2023-09-06 09:01:05', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (79, 16, '编辑', '/registration/udf/edit', null, 'registration:udf:edit', 'EditOutlined', 'F', 0, 17, '2023-09-06 08:56:45', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (80, 16, '删除', '/registration/udf/delete', null, 'registration:udf:delete', 'DeleteOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (81, 16, '启用', '/registration/udf/enable', null, 'registration:udf:enable', 'EyeOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (82, 19, '上传', '/registration/resource/upload', null, 'registration:resource:upload', 'PlusOutlined', 'F', 0, 18, '2023-09-06 09:01:05', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (83, 19, '重命名', '/registration/resource/rename', null, 'registration:resource:rename', 'EditOutlined', 'F', 0, 17, '2023-09-06 08:56:45', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (84, 19, '删除', '/registration/resource/delete', null, 'registration:resource:delete', 'DeleteOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (85, 19, '创建文件夹', '/registration/resource/folder', null, 'registration:resource:folder', 'PlusOutlined', 'F', 0, 18, '2023-09-06 08:57:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (86, 4, 'Token 令牌', '/auth/token', './AuthCenter/Token', 'auth:token', 'SecurityScanFilled', 'C', 0, 35, '2023-09-05 23:14:23', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (87, 21, '添加', '/auth/user/add', null, 'auth:user:add', 'PlusOutlined', 'F', 0, 36, '2023-09-22 22:06:52', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (88, 21, '重置密码', '/auth/user/reset', null, 'auth:user:reset', 'RollbackOutlined', 'F', 0, 37, '2023-09-22 22:08:17', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (89, 21, '恢复用户', '/auth/user/recovery', null, 'auth:user:recovery', 'RadiusSettingOutlined', 'F', 0, 38, '2023-09-22 22:08:53', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (90, 21, '删除', '/auth/user/delete', null, 'auth:user:delete', 'DeleteOutlined', 'F', 0, 39, '2023-09-22 22:09:29', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (91, 21, '修改密码', '/auth/user/changePassword', null, 'auth:user:changePassword', 'EditOutlined', 'F', 0, 40, '2023-09-22 22:10:01', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (92, 21, '分配角色', '/auth/user/assignRole', null, 'auth:user:assignRole', 'ForwardOutlined', 'F', 0, 41, '2023-09-22 22:10:31', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (93, 21, '编辑', '/auth/user/edit', null, 'auth:user:edit', 'EditOutlined', 'F', 0, 42, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (94, 20, '添加', '/auth/role/add', null, 'auth:role:add', 'PlusOutlined', 'F', 0, 44, '2023-09-22 22:06:52', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (95, 20, '删除', '/auth/role/delete', null, 'auth:role:delete', 'DeleteOutlined', 'F', 0, 45, '2023-09-22 22:09:29', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (96, 20, '分配菜单', '/auth/role/assignMenu', null, 'auth:role:assignMenu', 'AntDesignOutlined', 'F', 0, 46, '2023-09-22 22:10:31', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (97, 20, '编辑', '/auth/role/edit', null, 'auth:role:edit', 'EditOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (98, 20, '查看用户列表', '/auth/role/viewUser', null, 'auth:role:viewUser', 'FundViewOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (99, 86, '添加 Token', '/auth/token/add', null, 'auth:token:add', 'PlusOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (100, 86, '删除 Token', '/auth/token/delete', null, 'auth:token:delete', 'DeleteOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (101, 86, '修改 Token', '/auth/token/edit', null, 'auth:token:edit', 'EditOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (102, 27, '添加', '/auth/rowPermissions/add', null, 'auth:rowPermissions:add', 'PlusOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (103, 27, '编辑', '/auth/rowPermissions/edit', null, 'auth:rowPermissions:edit', 'EditOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (104, 27, '删除', '/auth/rowPermissions/delete', null, 'auth:rowPermissions:delete', 'DeleteOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (105, 23, '添加', '/auth/tenant/add', null, 'auth:tenant:add', 'PlusOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (106, 23, '编辑', '/auth/tenant/edit', null, 'auth:tenant:edit', 'EditOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (107, 23, '删除', '/auth/tenant/delete', null, 'auth:tenant:delete', 'DeleteOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (108, 23, '分配用户', '/auth/tenant/assignUser', null, 'auth:tenant:assignUser', 'EuroOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (109, 23, '查看用户', '/auth/tenant/viewUser', null, 'auth:tenant:viewUser', 'FundViewOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (110, 23, '设置/取消租户管理员', '/auth/tenant/modifyTenantManager', null, 'auth:tenant:modifyTenantManager', 'ExclamationCircleOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (111, 22, '创建根菜单', '/auth/menu/createRoot', null, 'auth:menu:createRoot', 'FolderAddOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (112, 22, '刷新', '/auth/menu/refresh', null, 'auth:menu:refresh', 'ReloadOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (113, 22, '编辑', '/auth/menu/edit', null, 'auth:menu:edit', 'EditOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (114, 22, '添加子项', '/auth/menu/addSub', null, 'auth:menu:addSub', 'PlusOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (115, 22, '删除', '/auth/menu/delete', null, 'auth:menu:delete', 'DeleteOutlined', 'F', 0, 47, '2023-09-22 22:11:41', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (116, 6, '告警策略', '/settings/alertrule', './SettingCenter/AlertRule', 'settings:alertrule', 'AndroidOutlined', 'C', 0, 48, '2023-09-22 23:31:10', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (117, 116, '添加', '/settings/alertrule/add', null, 'settings:alertrule:add', 'PlusOutlined', 'F', 0, 49, '2023-09-22 23:34:51', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (118, 116, '删除', '/settings/alertrule/delete', null, 'settings:alertrule:delete', 'DeleteOutlined', 'F', 0, 50, '2023-09-22 23:35:20', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (119, 116, '编辑', '/settings/alertrule/edit', null, 'settings:alertrule:edit', 'EditOutlined', 'F', 0, 51, '2023-09-22 23:36:32', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (120, 8, 'Dinky 服务监控', '/metrics/server', './Metrics/Server', 'metrics:server', 'DashboardOutlined', 'F', 0, 52, '2023-09-22 23:37:43', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (121, 8, 'Flink 任务监控', '/metrics/job', './Metrics/Job', 'metrics:job', 'DashboardTwoTone', 'C', 0, 53, '2023-09-22 23:38:34', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (122, 24, 'Dinky 环境配置', '/settings/globalsetting/dinky', null, 'settings:globalsetting:dinky', 'SettingOutlined', 'C', 0, 54, '2023-09-22 23:40:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (123, 24, 'Flink 环境配置', '/settings/globalsetting/flink', null, 'settings:globalsetting:flink', 'SettingOutlined', 'C', 0, 54, '2023-09-22 23:40:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (124, 24, 'Maven 配置', '/settings/globalsetting/maven', null, 'settings:globalsetting:maven', 'SettingOutlined', 'C', 0, 54, '2023-09-22 23:40:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (125, 24, 'DolphinScheduler 配置', '/settings/globalsetting/ds', null, 'settings:globalsetting:ds', 'SettingOutlined', 'C', 0, 54, '2023-09-22 23:40:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (126, 24, 'LDAP 配置', '/settings/globalsetting/ldap', null, 'settings:globalsetting:ldap', 'SettingOutlined', 'C', 0, 54, '2023-09-22 23:40:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (127, 24, 'Metrics 配置', '/settings/globalsetting/metrics', null, 'settings:globalsetting:metrics', 'SettingOutlined', 'C', 0, 54, '2023-09-22 23:40:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (128, 24, 'Resource 配置', '/settings/globalsetting/resource', null, 'settings:globalsetting:resource', 'SettingOutlined', 'C', 0, 54, '2023-09-22 23:40:30', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (129, 122, '编辑', '/settings/globalsetting/dinky/edit', null, 'settings:globalsetting:dinky:edit', 'EditOutlined', 'F', 0, 55, '2023-09-22 23:44:18', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (130, 123, '编辑', '/settings/globalsetting/flink/edit', null, 'settings:globalsetting:flink:edit', 'EditOutlined', 'F', 0, 55, '2023-09-22 23:44:18', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (131, 124, '编辑', '/settings/globalsetting/maven/edit', null, 'settings:globalsetting:maven:edit', 'EditOutlined', 'F', 0, 55, '2023-09-22 23:44:18', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (132, 125, '编辑', '/settings/globalsetting/ds/edit', null, 'settings:globalsetting:ds:edit', 'EditOutlined', 'F', 0, 55, '2023-09-22 23:44:18', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (133, 126, '编辑', '/settings/globalsetting/ldap/edit', null, 'settings:globalsetting:ldap:edit', 'EditOutlined', 'F', 0, 55, '2023-09-22 23:44:18', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (134, 127, '编辑', '/settings/globalsetting/metrics/edit', null, 'settings:globalsetting:metrics:edit', 'EditOutlined', 'F', 0, 55, '2023-09-22 23:44:18', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (135, 128, '编辑', '/settings/globalsetting/resource/edit', null, 'settings:globalsetting:resource:edit', 'EditOutlined', 'F', 0, 55, '2023-09-22 23:44:18', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (136, 12, '告警模版', '/registration/alert/template', './RegCenter/Alert/AlertTemplate', 'registration:alert:template', 'AlertOutlined', 'C', 0, 56, '2023-09-23 21:34:43', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (137, 136, '添加', '/registration/alert/template/add', null, 'registration:alert:template:add', 'PlusOutlined', 'F', 0, 57, '2023-09-23 21:36:37', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (138, 136, '编辑', '/registration/alert/template/edit', null, 'registration:alert:template:edit', 'EditOutlined', 'F', 0, 58, '2023-09-23 21:37:00', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (139, 136, '删除', '/registration/alert/template/delete', null, 'registration:alert:template:delete', 'DeleteOutlined', 'F', 0, 59, '2023-09-23 21:37:43', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (140, 25, '系统日志', '/settings/systemlog/rootlog', null, 'settings:systemlog:rootlog', 'BankOutlined', 'F', 0, 60, '2023-09-23 21:43:57', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (141, 25, '日志列表', '/settings/systemlog/loglist', null, 'settings:systemlog:loglist', 'BankOutlined', 'F', 0, 61, '2023-09-23 21:45:05', '2023-09-25 18:26:45', null);


-- ----------------------------
-- Records of dinky_alert_rule
-- ----------------------------
INSERT INTO dinky_alert_rules VALUES (3, 'alert.rule.jobFail', '[{"ruleKey":"jobInstance.status","ruleOperator":"EQ","ruleValue":"\'FAILED\'","rulePriority":"1"}]', 1, 'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-09-04 23:03:02');
INSERT INTO dinky_alert_rules VALUES (4, 'alert.rule.getJobInfoFail', '[{"ruleKey":"jobInstance.status","ruleOperator":"EQ","ruleValue":"\'UNKNOWN\'","rulePriority":"1"}]', 1, 'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-09-05 18:03:43');
INSERT INTO dinky_alert_rules VALUES (5, 'alert.rule.jobRestart', '[{"ruleKey":"jobInstance.status","ruleOperator":"EQ","ruleValue":"\'RESTARTING\'","rulePriority":"1"}]', 1, 'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-09-06 21:35:12');
INSERT INTO dinky_alert_rules VALUES (6, 'alert.rule.checkpointFail', '[{"ruleKey":"checkPoints.checkFailed(#key,#checkPoints)","ruleOperator":"EQ","ruleValue":"true"}]', 1, 'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-09-06 21:49:03');
INSERT INTO dinky_alert_rules VALUES (7, 'alert.rule.jobRunException', '[{"ruleKey":"exceptionRule.isException(#key,#exceptions)","ruleOperator":"EQ","ruleValue":"true"}]', 1, 'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-09-06 21:50:12');
INSERT INTO dinky_alert_rules VALUES (8, 'alert.rule.checkpointTimeout', '[{"ruleKey":"checkPoints.checkpointTime(#key,#checkPoints)","ruleOperator":"GE","ruleValue":"1000"}]', 1, 'CUSTOM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-09-06 22:23:35');

INSERT INTO dinky_alert_template VALUES (1, 'Default', '
- **Job Name :** <font color=''gray''>${task.name}</font>
- **Job Status :** <font color=''red''>${jobInstance.status}</font>
- **Alert Time :** ${time}
- **Start Time :** ${startTime}
- **End Time :** ${endTime}
- **<font color=''red''><#if exceptions_msg?length gt 100>${exceptions_msg?substring(0,100)}<#else>${exceptions_msg}</#if></font>**
[Go toTask Web](http://${taskUrl})
', 1, null, null);

COMMIT;




update dinky_user set super_admin_flag =1  where id =1;
