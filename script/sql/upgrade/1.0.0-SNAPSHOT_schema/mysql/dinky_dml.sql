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


begin ;
delete from `dinky_task_statement` where id in (select id from `dinky_task` where `name` = 'dlink_default_catalog');

delete from `dinky_task` where `name` = 'dlink_default_catalog';

update dinky_udf_template set template_code= 'from pyflink.table import DataTypes\nfrom pyflink.table.udf import udf\n\n\n@udf(result_type=DataTypes.STRING())\ndef ${className}(variable1:str):\n    return ''\''  where id = 5;

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

INSERT INTO `dinky_resources` (`id`, `file_name`, `description`, `user_id`, `type`, `size`, `pid`, `full_name`, `is_directory`) VALUES (0, 'Root', 'main folder', 1, 0, 0, -1, '', 1);


-- ----------------------------
-- Records of dinky_sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `dinky_sys_menu` VALUES (1, -1, '首页', '/home', './Home', 'home', 'HomeOutlined', 'C', 0, 1, '2023-08-11 14:06:52', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (2, -1, '运维中心', '/devops', null, 'devops', 'ControlOutlined', 'M', 0, 20, '2023-08-11 14:06:52', '2023-09-26 14:53:34', null);
INSERT INTO `dinky_sys_menu` VALUES (3, -1, '注册中心', '/registration', null, 'registration', 'AppstoreOutlined', 'M', 0, 23, '2023-08-11 14:06:52', '2023-09-26 14:54:03', null);
INSERT INTO `dinky_sys_menu` VALUES (4, -1, '认证中心', '/auth', null, 'auth', 'SafetyCertificateOutlined', 'M', 0, 79, '2023-08-11 14:06:52', '2023-09-26 15:08:42', null);
INSERT INTO `dinky_sys_menu` VALUES (5, -1, '数据开发', '/datastudio', './DataStudio', 'datastudio', 'CodeOutlined', 'C', 0, 4, '2023-08-11 14:06:52', '2023-09-26 14:49:12', null);
INSERT INTO `dinky_sys_menu` VALUES (6, -1, '配置中心', '/settings', null, 'settings', 'SettingOutlined', 'M', 0, 115, '2023-08-11 14:06:53', '2023-09-26 15:16:03', null);
INSERT INTO `dinky_sys_menu` VALUES (7, -1, '关于', '/about', './Other/About', 'about', 'SmileOutlined', 'C', 0, 143, '2023-08-11 14:06:53', '2023-09-26 15:21:21', null);
INSERT INTO `dinky_sys_menu` VALUES (8, -1, '监控', '/metrics', './Metrics', 'metrics', 'DashboardOutlined', 'C', 0, 140, '2023-08-11 14:06:53', '2023-09-26 15:20:49', null);
INSERT INTO `dinky_sys_menu` VALUES (9, 3, '集群', '/registration/cluster', null, 'registration:cluster', 'GoldOutlined', 'M', 0, 24, '2023-08-11 14:06:54', '2023-09-26 14:54:19', null);
INSERT INTO `dinky_sys_menu` VALUES (10, 3, '数据源', '/registration/datasource', './RegCenter/DataSource', 'registration:datasource', 'DatabaseOutlined', 'M', 0, 37, '2023-08-11 14:06:54', '2023-09-26 14:59:31', null);
INSERT INTO `dinky_sys_menu` VALUES (11, -1, '个人中心', '/account/center', './Other/PersonCenter', 'account:center', 'UserOutlined', 'C', 0, 144, '2023-08-11 14:06:54', '2023-09-26 15:21:29', null);
INSERT INTO `dinky_sys_menu` VALUES (12, 3, '告警', '/registration/alert', null, 'registration:alert', 'AlertOutlined', 'M', 0, 43, '2023-08-11 14:06:54', '2023-09-26 15:01:32', null);
INSERT INTO `dinky_sys_menu` VALUES (13, 3, '文档', '/registration/document', './RegCenter/Document', 'registration:document', 'BookOutlined', 'C', 0, 55, '2023-08-11 14:06:54', '2023-09-26 15:03:59', null);
INSERT INTO `dinky_sys_menu` VALUES (14, 3, '全局变量', '/registration/fragment', './RegCenter/GlobalVar', 'registration:fragment', 'RocketOutlined', 'C', 0, 59, '2023-08-11 14:06:54', '2023-09-26 15:04:55', null);
INSERT INTO `dinky_sys_menu` VALUES (15, 3, 'Git 项目', '/registration/gitproject', './RegCenter/GitProject', 'registration:gitproject', 'GithubOutlined', 'C', 0, 63, '2023-08-11 14:06:54', '2023-09-26 15:05:37', null);
INSERT INTO `dinky_sys_menu` VALUES (16, 3, 'UDF 模版', '/registration/udf', './RegCenter/UDF', 'registration:udf', 'ToolOutlined', 'C', 0, 69, '2023-08-11 14:06:54', '2023-09-26 15:06:40', null);
INSERT INTO `dinky_sys_menu` VALUES (17, 2, 'job-detail', '/devops/job-detail', './DevOps/JobDetail', 'devops:job-detail', 'InfoCircleOutlined', 'C', 0, 22, '2023-08-11 14:06:54', '2023-09-26 14:53:53', null);
INSERT INTO `dinky_sys_menu` VALUES (18, 2, 'job', '/devops/joblist', './DevOps', 'devops:joblist', 'AppstoreFilled', 'C', 0, 21, '2023-08-11 14:06:54', '2023-09-26 14:53:43', null);
INSERT INTO `dinky_sys_menu` VALUES (19, 3, '资源中心', '/registration/resource', './RegCenter/Resource', 'registration:resource', 'FileZipOutlined', 'C', 0, 73, '2023-08-11 14:06:54', '2023-09-26 15:07:25', null);
INSERT INTO `dinky_sys_menu` VALUES (20, 4, '角色', '/auth/role', './AuthCenter/Role', 'auth:role', 'TeamOutlined', 'C', 0, 88, '2023-08-11 14:06:54', '2023-09-26 15:10:19', null);
INSERT INTO `dinky_sys_menu` VALUES (21, 4, '用户', '/auth/user', './AuthCenter/User', 'auth:user', 'UserOutlined', 'C', 0, 80, '2023-08-11 14:06:54', '2023-09-26 15:08:51', null);
INSERT INTO `dinky_sys_menu` VALUES (22, 4, '菜单', '/auth/menu', './AuthCenter/Menu', 'auth:menu', 'MenuOutlined', 'C', 0, 94, '2023-08-11 14:06:54', '2023-09-26 15:11:34', null);
INSERT INTO `dinky_sys_menu` VALUES (23, 4, '租户', '/auth/tenant', './AuthCenter/Tenant', 'auth:tenant', 'SecurityScanOutlined', 'C', 0, 104, '2023-08-11 14:06:54', '2023-09-26 15:13:35', null);
INSERT INTO `dinky_sys_menu` VALUES (24, 6, '全局设置', '/settings/globalsetting', './SettingCenter/GlobalSetting', 'settings:globalsetting', 'SettingOutlined', 'C', 0, 116, '2023-08-11 14:06:54', '2023-09-26 15:16:12', null);
INSERT INTO `dinky_sys_menu` VALUES (25, 6, '系统日志', '/settings/systemlog', './SettingCenter/SystemLogs', 'settings:systemlog', 'InfoCircleOutlined', 'C', 0, 131, '2023-08-11 14:06:55', '2023-09-26 15:18:53', null);
INSERT INTO `dinky_sys_menu` VALUES (26, 6, '进程', '/settings/process', './SettingCenter/Process', 'settings:process', 'ReconciliationOutlined', 'C', 0, 135, '2023-08-11 14:06:55', '2023-09-26 15:19:35', null);
INSERT INTO `dinky_sys_menu` VALUES (27, 4, '行权限', '/auth/rowpermissions', './AuthCenter/RowPermissions', 'auth:rowpermissions', 'SafetyCertificateOutlined', 'C', 0, 100, '2023-08-11 14:06:55', '2023-09-26 15:12:46', null);
INSERT INTO `dinky_sys_menu` VALUES (28, 9, 'Flink 实例', '/registration/cluster/instance', './RegCenter/Cluster/Instance', 'registration:cluster:instance', 'ReconciliationOutlined', 'C', 0, 25, '2023-08-11 14:06:55', '2023-09-26 14:54:29', null);
INSERT INTO `dinky_sys_menu` VALUES (29, 12, '告警组', '/registration/alert/group', './RegCenter/Alert/AlertGroup', 'registration:alert:group', 'AlertOutlined', 'C', 0, 48, '2023-08-11 14:06:55', '2023-09-26 15:02:23', null);
INSERT INTO `dinky_sys_menu` VALUES (30, 9, '集群配置', '/registration/cluster/config', './RegCenter/Cluster/Configuration', 'registration:cluster:config', 'SettingOutlined', 'C', 0, 31, '2023-08-11 14:06:55', '2023-09-26 14:57:57', null);
INSERT INTO `dinky_sys_menu` VALUES (31, 12, '告警实例', '/registration/alert/instance', './RegCenter/Alert/AlertInstance', 'registration:alert:instance', 'AlertFilled', 'C', 0, 44, '2023-08-11 14:06:55', '2023-09-26 15:01:42', null);
INSERT INTO `dinky_sys_menu` VALUES (32, 1, '作业监控', '/home/jobOverView', 'JobOverView', 'home:jobOverView', 'AntCloudOutlined', 'F', 0, 2, '2023-08-15 16:52:59', '2023-09-26 14:48:50', null);
INSERT INTO `dinky_sys_menu` VALUES (33, 1, '数据开发', '/home/devOverView', 'DevOverView', 'home:devOverView', 'AimOutlined', 'F', 0, 3, '2023-08-15 16:54:47', '2023-09-26 14:49:00', null);
INSERT INTO `dinky_sys_menu` VALUES (34, 5, '项目列表', '/datastudio/left/project', null, 'datastudio:left:project', 'ConsoleSqlOutlined', 'F', 0, 5, '2023-09-01 18:00:39', '2023-09-26 14:49:31', null);
INSERT INTO `dinky_sys_menu` VALUES (35, 5, '数据源', '/datastudio/left/datasource', null, 'datastudio:left:datasource', 'TableOutlined', 'F', 0, 7, '2023-09-01 18:01:09', '2023-09-26 14:49:42', null);
INSERT INTO `dinky_sys_menu` VALUES (36, 5, 'catalog', '/datastudio/left/catalog', null, 'datastudio:left:structure', 'DatabaseOutlined', 'F', 0, 6, '2023-09-01 18:01:30', '2023-09-26 14:49:54', null);
INSERT INTO `dinky_sys_menu` VALUES (37, 5, '作业配置', '/datastudio/right/jobConfig', null, 'datastudio:right:jobConfig', 'SettingOutlined', 'F', 0, 8, '2023-09-01 18:02:15', '2023-09-26 14:50:24', null);
INSERT INTO `dinky_sys_menu` VALUES (38, 5, '预览配置', '/datastudio/right/previewConfig', null, 'datastudio:right:previewConfig', 'InsertRowRightOutlined', 'F', 0, 9, '2023-09-01 18:03:08', '2023-09-26 14:50:54', null);
INSERT INTO `dinky_sys_menu` VALUES (39, 5, '版本历史', '/datastudio/right/historyVision', null, 'datastudio:right:historyVision', 'HistoryOutlined', 'F', 0, 10, '2023-09-01 18:03:29', '2023-09-26 14:51:03', null);
INSERT INTO `dinky_sys_menu` VALUES (40, 5, '保存点', '/datastudio/right/savePoint', null, 'datastudio:right:savePoint', 'FolderOutlined', 'F', 0, 11, '2023-09-01 18:03:58', '2023-09-26 14:51:13', null);
INSERT INTO `dinky_sys_menu` VALUES (41, 5, '作业信息', '/datastudio/right/jobInfo', null, 'datastudio:right:jobInfo', 'InfoCircleOutlined', 'F', 0, 8, '2023-09-01 18:04:31', '2023-09-25 18:26:45', null);
INSERT INTO `dinky_sys_menu` VALUES (42, 5, '控制台', '/datastudio/bottom/console', null, 'datastudio:bottom:console', 'ConsoleSqlOutlined', 'F', 0, 12, '2023-09-01 18:04:56', '2023-09-26 14:51:24', null);
INSERT INTO `dinky_sys_menu` VALUES (43, 5, '结果', '/datastudio/bottom/result', null, 'datastudio:bottom:result', 'SearchOutlined', 'F', 0, 13, '2023-09-01 18:05:16', '2023-09-26 14:51:36', null);
INSERT INTO `dinky_sys_menu` VALUES (45, 5, '血缘', '/datastudio/bottom/lineage', null, 'datastudio:bottom:lineage', 'PushpinOutlined', 'F', 0, 15, '2023-09-01 18:07:15', '2023-09-26 14:52:00', null);
INSERT INTO `dinky_sys_menu` VALUES (46, 5, '表数据监控', '/datastudio/bottom/process', null, 'datastudio:bottom:process', 'TableOutlined', 'F', 0, 16, '2023-09-01 18:07:55', '2023-09-26 14:52:38', null);
INSERT INTO `dinky_sys_menu` VALUES (47, 5, '小工具', '/datastudio/bottom/tool', null, 'datastudio:bottom:tool', 'ToolOutlined', 'F', 0, 17, '2023-09-01 18:08:18', '2023-09-26 14:53:04', null);
INSERT INTO `dinky_sys_menu` VALUES (48, 28, '新建', '/registration/cluster/instance/add', null, 'registration:cluster:instance:add', 'PlusOutlined', 'F', 0, 26, '2023-09-06 08:56:45', '2023-09-26 14:56:54', null);
INSERT INTO `dinky_sys_menu` VALUES (49, 28, '回收', '/registration/cluster/instance/recovery', null, 'registration:cluster:instance:recovery', 'DeleteFilled', 'F', 0, 29, '2023-09-06 08:57:30', '2023-09-26 14:56:54', null);
INSERT INTO `dinky_sys_menu` VALUES (50, 28, '编辑', '/registration/cluster/instance/edit', null, 'registration:cluster:instance:edit', 'EditOutlined', 'F', 0, 27, '2023-09-06 08:56:45', '2023-09-26 14:56:54', null);
INSERT INTO `dinky_sys_menu` VALUES (51, 28, '删除', '/registration/cluster/instance/delete', null, 'registration:cluster:instance:delete', 'DeleteOutlined', 'F', 0, 28, '2023-09-06 08:57:30', '2023-09-26 14:56:54', null);
INSERT INTO `dinky_sys_menu` VALUES (52, 30, '新建', '/registration/cluster/config/add', null, 'registration:cluster:config:add', 'PlusOutlined', 'F', 0, 32, '2023-09-06 09:00:31', '2023-09-26 14:58:50', null);
INSERT INTO `dinky_sys_menu` VALUES (53, 30, '编辑', '/registration/cluster/config/edit', null, 'registration:cluster:config:edit', 'EditOutlined', 'F', 0, 33, '2023-09-06 08:56:45', '2023-09-26 14:58:50', null);
INSERT INTO `dinky_sys_menu` VALUES (54, 30, '删除', '/registration/cluster/config/delete', null, 'registration:cluster:config:delete', 'DeleteOutlined', 'F', 0, 34, '2023-09-06 08:57:30', '2023-09-26 14:58:50', null);
INSERT INTO `dinky_sys_menu` VALUES (55, 10, '新建', '/registration/datasource/add', null, 'registration:datasource:add', 'PlusOutlined', 'F', 0, 38, '2023-09-06 09:01:05', '2023-09-26 15:00:42', null);
INSERT INTO `dinky_sys_menu` VALUES (56, 10, '编辑', '/registration/datasource/edit', null, 'registration:datasource:edit', 'EditOutlined', 'F', 0, 39, '2023-09-06 08:56:45', '2023-09-26 15:00:41', null);
INSERT INTO `dinky_sys_menu` VALUES (57, 10, '删除', '/registration/datasource/delete', null, 'registration:datasource:delete', 'DeleteOutlined', 'F', 0, 40, '2023-09-06 08:57:30', '2023-09-26 15:00:42', null);
INSERT INTO `dinky_sys_menu` VALUES (58, 31, '新建', '/registration/alert/instance/add', null, 'registration:alert:instance:add', 'PlusOutlined', 'F', 0, 46, '2023-09-06 09:01:05', '2023-09-26 15:02:04', null);
INSERT INTO `dinky_sys_menu` VALUES (59, 31, '编辑', '/registration/alert/instance/edit', null, 'registration:alert:instance:edit', 'EditOutlined', 'F', 0, 45, '2023-09-06 08:56:45', '2023-09-26 15:01:54', null);
INSERT INTO `dinky_sys_menu` VALUES (60, 31, '删除', '/registration/alert/instance/delete', null, 'registration:alert:instance:delete', 'DeleteOutlined', 'F', 0, 47, '2023-09-06 08:57:30', '2023-09-26 15:02:13', null);
INSERT INTO `dinky_sys_menu` VALUES (61, 29, '新建', '/registration/alert/group/add', null, 'registration:alert:group:add', 'PlusOutlined', 'F', 0, 49, '2023-09-06 09:01:05', '2023-09-26 15:02:48', null);
INSERT INTO `dinky_sys_menu` VALUES (62, 29, '编辑', '/registration/alert/group/edit', null, 'registration:alert:group:edit', 'EditOutlined', 'F', 0, 49, '2023-09-06 08:56:45', '2023-09-26 15:02:36', null);
INSERT INTO `dinky_sys_menu` VALUES (63, 29, '删除', '/registration/alert/group/delete', null, 'registration:alert:group:delete', 'DeleteOutlined', 'F', 0, 50, '2023-09-06 08:57:30', '2023-09-26 15:03:01', null);
INSERT INTO `dinky_sys_menu` VALUES (64, 13, '新建', '/registration/document/add', null, 'registration:document:add', 'PlusOutlined', 'F', 0, 57, '2023-09-06 09:01:05', '2023-09-26 15:04:22', null);
INSERT INTO `dinky_sys_menu` VALUES (65, 13, '编辑', '/registration/document/edit', null, 'registration:document:edit', 'EditOutlined', 'F', 0, 56, '2023-09-06 08:56:45', '2023-09-26 15:04:13', null);
INSERT INTO `dinky_sys_menu` VALUES (66, 13, '删除', '/registration/document/delete', null, 'registration:document:delete', 'DeleteOutlined', 'F', 0, 58, '2023-09-06 08:57:30', '2023-09-26 15:04:32', null);
INSERT INTO `dinky_sys_menu` VALUES (68, 14, '新建', '/registration/fragment/add', null, 'registration:fragment:add', 'PlusOutlined', 'F', 0, 61, '2023-09-06 09:01:05', '2023-09-26 15:05:13', null);
INSERT INTO `dinky_sys_menu` VALUES (69, 14, '编辑', '/registration/fragment/edit', null, 'registration:fragment:edit', 'EditOutlined', 'F', 0, 60, '2023-09-06 08:56:45', '2023-09-26 15:05:04', null);
INSERT INTO `dinky_sys_menu` VALUES (70, 14, '删除', '/registration/fragment/delete', null, 'registration:fragment:delete', 'DeleteOutlined', 'F', 0, 62, '2023-09-06 08:57:30', '2023-09-26 15:05:21', null);
INSERT INTO `dinky_sys_menu` VALUES (72, 15, '新建', '/registration/gitproject/add', null, 'registration:gitproject:add', 'PlusOutlined', 'F', 0, 65, '2023-09-06 09:01:05', '2023-09-26 15:06:01', null);
INSERT INTO `dinky_sys_menu` VALUES (73, 15, '编辑', '/registration/gitproject/edit', null, 'registration:gitproject:edit', 'EditOutlined', 'F', 0, 64, '2023-09-06 08:56:45', '2023-09-26 15:05:52', null);
INSERT INTO `dinky_sys_menu` VALUES (74, 15, '删除', '/registration/gitproject/delete', null, 'registration:gitproject:delete', 'DeleteOutlined', 'F', 0, 66, '2023-09-06 08:57:30', '2023-09-26 15:06:09', null);
INSERT INTO `dinky_sys_menu` VALUES (76, 15, '构建', '/registration/gitproject/build', null, 'registration:gitproject:build', 'PlaySquareOutlined', 'F', 0, 67, '2023-09-06 08:57:30', '2023-09-26 15:06:17', null);
INSERT INTO `dinky_sys_menu` VALUES (77, 15, '查看日志', '/registration/gitproject/showLog', null, 'registration:gitproject:showLog', 'SearchOutlined', 'F', 0, 68, '2023-09-06 08:57:30', '2023-09-26 15:06:26', null);
INSERT INTO `dinky_sys_menu` VALUES (78, 16, '新建', '/registration/udf/template/add', null, 'registration:udf:template:add', 'PlusOutlined', 'F', 0, 71, '2023-09-06 09:01:05', '2023-09-26 15:07:04', null);
INSERT INTO `dinky_sys_menu` VALUES (79, 16, '编辑', '/registration/udf/template/edit', null, 'registration:udf:template:edit', 'EditOutlined', 'F', 0, 70, '2023-09-06 08:56:45', '2023-09-26 15:06:48', null);
INSERT INTO `dinky_sys_menu` VALUES (80, 16, '删除', '/registration/udf/template/delete', null, 'registration:udf:template:delete', 'DeleteOutlined', 'F', 0, 72, '2023-09-06 08:57:30', '2023-09-26 15:07:12', null);
INSERT INTO `dinky_sys_menu` VALUES (82, 19, '上传', '/registration/resource/upload', null, 'registration:resource:upload', 'PlusOutlined', 'F', 0, 77, '2023-09-06 09:01:05', '2023-09-26 15:08:02', null);
INSERT INTO `dinky_sys_menu` VALUES (83, 19, '重命名', '/registration/resource/rename', null, 'registration:resource:rename', 'EditOutlined', 'F', 0, 75, '2023-09-06 08:56:45', '2023-09-26 15:07:45', null);
INSERT INTO `dinky_sys_menu` VALUES (84, 19, '删除', '/registration/resource/delete', null, 'registration:resource:delete', 'DeleteOutlined', 'F', 0, 76, '2023-09-06 08:57:30', '2023-09-26 15:07:54', null);
INSERT INTO `dinky_sys_menu` VALUES (85, 19, '创建文件夹', '/registration/resource/addFolder', null, 'registration:resource:addFolder', 'PlusOutlined', 'F', 0, 74, '2023-09-06 08:57:30', '2023-09-26 15:07:37', null);
INSERT INTO `dinky_sys_menu` VALUES (86, 4, 'Token 令牌', '/auth/token', './AuthCenter/Token', 'auth:token', 'SecurityScanFilled', 'C', 0, 111, '2023-09-05 23:14:23', '2023-09-26 15:15:22', null);
INSERT INTO `dinky_sys_menu` VALUES (87, 21, '添加', '/auth/user/add', null, 'auth:user:add', 'PlusOutlined', 'F', 0, 81, '2023-09-22 22:06:52', '2023-09-26 15:09:49', null);
INSERT INTO `dinky_sys_menu` VALUES (88, 21, '重置密码', '/auth/user/reset', null, 'auth:user:reset', 'RollbackOutlined', 'F', 0, 84, '2023-09-22 22:08:17', '2023-09-26 15:09:49', null);
INSERT INTO `dinky_sys_menu` VALUES (89, 21, '恢复用户', '/auth/user/recovery', null, 'auth:user:recovery', 'RadiusSettingOutlined', 'F', 0, 85, '2023-09-22 22:08:53', '2023-09-26 15:09:49', null);
INSERT INTO `dinky_sys_menu` VALUES (90, 21, '删除', '/auth/user/delete', null, 'auth:user:delete', 'DeleteOutlined', 'F', 0, 83, '2023-09-22 22:09:29', '2023-09-26 15:09:49', null);
INSERT INTO `dinky_sys_menu` VALUES (91, 21, '修改密码', '/auth/user/changePassword', null, 'auth:user:changePassword', 'EditOutlined', 'F', 0, 86, '2023-09-22 22:10:01', '2023-09-26 15:09:49', null);
INSERT INTO `dinky_sys_menu` VALUES (92, 21, '分配角色', '/auth/user/assignRole', null, 'auth:user:assignRole', 'ForwardOutlined', 'F', 0, 87, '2023-09-22 22:10:31', '2023-09-26 15:09:49', null);
INSERT INTO `dinky_sys_menu` VALUES (93, 21, '编辑', '/auth/user/edit', null, 'auth:user:edit', 'EditOutlined', 'F', 0, 82, '2023-09-22 22:11:41', '2023-09-26 15:09:49', null);
INSERT INTO `dinky_sys_menu` VALUES (94, 20, '添加', '/auth/role/add', null, 'auth:role:add', 'PlusOutlined', 'F', 0, 89, '2023-09-22 22:06:52', '2023-09-26 15:11:10', null);
INSERT INTO `dinky_sys_menu` VALUES (95, 20, '删除', '/auth/role/delete', null, 'auth:role:delete', 'DeleteOutlined', 'F', 0, 91, '2023-09-22 22:09:29', '2023-09-26 15:11:10', null);
INSERT INTO `dinky_sys_menu` VALUES (96, 20, '分配菜单', '/auth/role/assignMenu', null, 'auth:role:assignMenu', 'AntDesignOutlined', 'F', 0, 92, '2023-09-22 22:10:31', '2023-09-26 15:11:10', null);
INSERT INTO `dinky_sys_menu` VALUES (97, 20, '编辑', '/auth/role/edit', null, 'auth:role:edit', 'EditOutlined', 'F', 0, 90, '2023-09-22 22:11:41', '2023-09-26 15:11:10', null);
INSERT INTO `dinky_sys_menu` VALUES (98, 20, '查看用户列表', '/auth/role/viewUser', null, 'auth:role:viewUser', 'FundViewOutlined', 'F', 0, 93, '2023-09-22 22:11:41', '2023-09-26 15:11:10', null);
INSERT INTO `dinky_sys_menu` VALUES (99, 86, '添加 Token', '/auth/token/add', null, 'auth:token:add', 'PlusOutlined', 'F', 0, 112, '2023-09-22 22:11:41', '2023-09-26 15:15:46', null);
INSERT INTO `dinky_sys_menu` VALUES (100, 86, '删除 Token', '/auth/token/delete', null, 'auth:token:delete', 'DeleteOutlined', 'F', 0, 114, '2023-09-22 22:11:41', '2023-09-26 15:15:46', null);
INSERT INTO `dinky_sys_menu` VALUES (101, 86, '修改 Token', '/auth/token/edit', null, 'auth:token:edit', 'EditOutlined', 'F', 0, 113, '2023-09-22 22:11:41', '2023-09-26 15:15:46', null);
INSERT INTO `dinky_sys_menu` VALUES (102, 27, '添加', '/auth/rowPermissions/add', null, 'auth:rowPermissions:add', 'PlusOutlined', 'F', 0, 101, '2023-09-22 22:11:41', '2023-09-26 15:13:12', null);
INSERT INTO `dinky_sys_menu` VALUES (103, 27, '编辑', '/auth/rowPermissions/edit', null, 'auth:rowPermissions:edit', 'EditOutlined', 'F', 0, 102, '2023-09-22 22:11:41', '2023-09-26 15:13:12', null);
INSERT INTO `dinky_sys_menu` VALUES (104, 27, '删除', '/auth/rowPermissions/delete', null, 'auth:rowPermissions:delete', 'DeleteOutlined', 'F', 0, 103, '2023-09-22 22:11:41', '2023-09-26 15:13:12', null);
INSERT INTO `dinky_sys_menu` VALUES (105, 23, '添加', '/auth/tenant/add', null, 'auth:tenant:add', 'PlusOutlined', 'F', 0, 105, '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
INSERT INTO `dinky_sys_menu` VALUES (106, 23, '编辑', '/auth/tenant/edit', null, 'auth:tenant:edit', 'EditOutlined', 'F', 0, 106, '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
INSERT INTO `dinky_sys_menu` VALUES (107, 23, '删除', '/auth/tenant/delete', null, 'auth:tenant:delete', 'DeleteOutlined', 'F', 0, 107, '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
INSERT INTO `dinky_sys_menu` VALUES (108, 23, '分配用户', '/auth/tenant/assignUser', null, 'auth:tenant:assignUser', 'EuroOutlined', 'F', 0, 108, '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
INSERT INTO `dinky_sys_menu` VALUES (109, 23, '查看用户', '/auth/tenant/viewUser', null, 'auth:tenant:viewUser', 'FundViewOutlined', 'F', 0, 109, '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
INSERT INTO `dinky_sys_menu` VALUES (110, 23, '设置/取消租户管理员', '/auth/tenant/modifyTenantManager', null, 'auth:tenant:modifyTenantManager', 'ExclamationCircleOutlined', 'F', 0, 110, '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
INSERT INTO `dinky_sys_menu` VALUES (111, 22, '创建根菜单', '/auth/menu/createRoot', null, 'auth:menu:createRoot', 'FolderAddOutlined', 'F', 0, 95, '2023-09-22 22:11:41', '2023-09-26 15:12:26', null);
INSERT INTO `dinky_sys_menu` VALUES (112, 22, '刷新', '/auth/menu/refresh', null, 'auth:menu:refresh', 'ReloadOutlined', 'F', 0, 97, '2023-09-22 22:11:41', '2023-09-26 15:12:26', null);
INSERT INTO `dinky_sys_menu` VALUES (113, 22, '编辑', '/auth/menu/edit', null, 'auth:menu:edit', 'EditOutlined', 'F', 0, 98, '2023-09-22 22:11:41', '2023-09-26 15:12:26', null);
INSERT INTO `dinky_sys_menu` VALUES (114, 22, '添加子项', '/auth/menu/addSub', null, 'auth:menu:addSub', 'PlusOutlined', 'F', 0, 96, '2023-09-22 22:11:41', '2023-09-26 15:12:26', null);
INSERT INTO `dinky_sys_menu` VALUES (115, 22, '删除', '/auth/menu/delete', null, 'auth:menu:delete', 'DeleteOutlined', 'F', 0, 99, '2023-09-22 22:11:41', '2023-09-26 15:12:26', null);
INSERT INTO `dinky_sys_menu` VALUES (116, 6, '告警策略', '/settings/alertrule', './SettingCenter/AlertRule', 'settings:alertrule', 'AndroidOutlined', 'C', 0, 136, '2023-09-22 23:31:10', '2023-09-26 15:19:52', null);
INSERT INTO `dinky_sys_menu` VALUES (117, 116, '添加', '/settings/alertrule/add', null, 'settings:alertrule:add', 'PlusOutlined', 'F', 0, 137, '2023-09-22 23:34:51', '2023-09-26 15:20:03', null);
INSERT INTO `dinky_sys_menu` VALUES (118, 116, '删除', '/settings/alertrule/delete', null, 'settings:alertrule:delete', 'DeleteOutlined', 'F', 0, 139, '2023-09-22 23:35:20', '2023-09-26 15:20:21', null);
INSERT INTO `dinky_sys_menu` VALUES (119, 116, '编辑', '/settings/alertrule/edit', null, 'settings:alertrule:edit', 'EditOutlined', 'F', 0, 138, '2023-09-22 23:36:32', '2023-09-26 15:20:13', null);
INSERT INTO `dinky_sys_menu` VALUES (120, 8, 'Dinky 服务监控', '/metrics/server', './Metrics/Server', 'metrics:server', 'DashboardOutlined', 'F', 0, 141, '2023-09-22 23:37:43', '2023-09-26 15:21:00', null);
INSERT INTO `dinky_sys_menu` VALUES (121, 8, 'Flink 任务监控', '/metrics/job', './Metrics/Job', 'metrics:job', 'DashboardTwoTone', 'C', 0, 142, '2023-09-22 23:38:34', '2023-09-26 15:21:08', null);
INSERT INTO `dinky_sys_menu` VALUES (122, 24, 'Dinky 环境配置', '/settings/globalsetting/dinky', null, 'settings:globalsetting:dinky', 'SettingOutlined', 'C', 0, 117, '2023-09-22 23:40:30', '2023-09-26 15:16:20', null);
INSERT INTO `dinky_sys_menu` VALUES (123, 24, 'Flink 环境配置', '/settings/globalsetting/flink', null, 'settings:globalsetting:flink', 'SettingOutlined', 'C', 0, 119, '2023-09-22 23:40:30', '2023-09-26 15:16:40', null);
INSERT INTO `dinky_sys_menu` VALUES (124, 24, 'Maven 配置', '/settings/globalsetting/maven', null, 'settings:globalsetting:maven', 'SettingOutlined', 'C', 0, 121, '2023-09-22 23:40:30', '2023-09-26 15:17:04', null);
INSERT INTO `dinky_sys_menu` VALUES (125, 24, 'DolphinScheduler 配置', '/settings/globalsetting/ds', null, 'settings:globalsetting:ds', 'SettingOutlined', 'C', 0, 123, '2023-09-22 23:40:30', '2023-09-26 15:17:23', null);
INSERT INTO `dinky_sys_menu` VALUES (126, 24, 'LDAP 配置', '/settings/globalsetting/ldap', null, 'settings:globalsetting:ldap', 'SettingOutlined', 'C', 0, 125, '2023-09-22 23:40:30', '2023-09-26 15:17:41', null);
INSERT INTO `dinky_sys_menu` VALUES (127, 24, 'Metrics 配置', '/settings/globalsetting/metrics', null, 'settings:globalsetting:metrics', 'SettingOutlined', 'C', 0, 127, '2023-09-22 23:40:30', '2023-09-26 15:18:06', null);
INSERT INTO `dinky_sys_menu` VALUES (128, 24, 'Resource 配置', '/settings/globalsetting/resource', null, 'settings:globalsetting:resource', 'SettingOutlined', 'C', 0, 129, '2023-09-22 23:40:30', '2023-09-26 15:18:27', null);
INSERT INTO `dinky_sys_menu` VALUES (129, 122, '编辑', '/settings/globalsetting/dinky/edit', null, 'settings:globalsetting:dinky:edit', 'EditOutlined', 'F', 0, 118, '2023-09-22 23:44:18', '2023-09-26 15:16:29', null);
INSERT INTO `dinky_sys_menu` VALUES (130, 123, '编辑', '/settings/globalsetting/flink/edit', null, 'settings:globalsetting:flink:edit', 'EditOutlined', 'F', 0, 120, '2023-09-22 23:44:18', '2023-09-26 15:16:50', null);
INSERT INTO `dinky_sys_menu` VALUES (131, 124, '编辑', '/settings/globalsetting/maven/edit', null, 'settings:globalsetting:maven:edit', 'EditOutlined', 'F', 0, 122, '2023-09-22 23:44:18', '2023-09-26 15:17:13', null);
INSERT INTO `dinky_sys_menu` VALUES (132, 125, '编辑', '/settings/globalsetting/ds/edit', null, 'settings:globalsetting:ds:edit', 'EditOutlined', 'F', 0, 124, '2023-09-22 23:44:18', '2023-09-26 15:17:32', null);
INSERT INTO `dinky_sys_menu` VALUES (133, 126, '编辑', '/settings/globalsetting/ldap/edit', null, 'settings:globalsetting:ldap:edit', 'EditOutlined', 'F', 0, 126, '2023-09-22 23:44:18', '2023-09-26 15:17:51', null);
INSERT INTO `dinky_sys_menu` VALUES (134, 127, '编辑', '/settings/globalsetting/metrics/edit', null, 'settings:globalsetting:metrics:edit', 'EditOutlined', 'F', 0, 128, '2023-09-22 23:44:18', '2023-09-26 15:18:16', null);
INSERT INTO `dinky_sys_menu` VALUES (135, 128, '编辑', '/settings/globalsetting/resource/edit', null, 'settings:globalsetting:resource:edit', 'EditOutlined', 'F', 0, 130, '2023-09-22 23:44:18', '2023-09-26 15:18:39', null);
INSERT INTO `dinky_sys_menu` VALUES (136, 12, '告警模版', '/registration/alert/template', './RegCenter/Alert/AlertTemplate', 'registration:alert:template', 'AlertOutlined', 'C', 0, 51, '2023-09-23 21:34:43', '2023-09-26 15:03:14', null);
INSERT INTO `dinky_sys_menu` VALUES (137, 136, '添加', '/registration/alert/template/add', null, 'registration:alert:template:add', 'PlusOutlined', 'F', 0, 52, '2023-09-23 21:36:37', '2023-09-26 15:03:22', null);
INSERT INTO `dinky_sys_menu` VALUES (138, 136, '编辑', '/registration/alert/template/edit', null, 'registration:alert:template:edit', 'EditOutlined', 'F', 0, 53, '2023-09-23 21:37:00', '2023-09-26 15:03:30', null);
INSERT INTO `dinky_sys_menu` VALUES (139, 136, '删除', '/registration/alert/template/delete', null, 'registration:alert:template:delete', 'DeleteOutlined', 'F', 0, 54, '2023-09-23 21:37:43', '2023-09-26 15:03:37', null);
INSERT INTO `dinky_sys_menu` VALUES (140, 25, '系统日志', '/settings/systemlog/rootlog', null, 'settings:systemlog:rootlog', 'BankOutlined', 'F', 0, 133, '2023-09-23 21:43:57', '2023-09-26 15:19:14', null);
INSERT INTO `dinky_sys_menu` VALUES (141, 25, '日志列表', '/settings/systemlog/loglist', null, 'settings:systemlog:loglist', 'BankOutlined', 'F', 0, 134, '2023-09-23 21:45:05', '2023-09-26 15:19:23', null);
INSERT INTO `dinky_sys_menu` VALUES (142, 30, '部署 Session 集群', '/registration/cluster/config/deploy', null, 'registration:cluster:config:deploy', 'PlayCircleOutlined', 'F', 0, 35, '2023-09-26 13:42:55', '2023-09-26 14:58:50', null);
INSERT INTO `dinky_sys_menu` VALUES (143, 30, ' 心跳检测', '/registration/cluster/config/heartbeat', null, 'registration:cluster:config:heartbeat', 'HeartOutlined', 'F', 0, 36, '2023-09-26 13:44:23', '2023-09-26 14:58:50', null);
INSERT INTO `dinky_sys_menu` VALUES (144, 28, '心跳检测', '/registration/cluster/instance/heartbeat', null, 'registration:cluster:instance:heartbeat', 'HeartOutlined', 'F', 0, 30, '2023-09-26 13:51:04', '2023-09-26 14:57:42', null);
INSERT INTO `dinky_sys_menu` VALUES (145, 10, '心跳检测', '/registration/datasource/heartbeat', null, 'registration:datasource:heartbeat', 'HeartOutlined', 'F', 0, 41, '2023-09-26 14:00:06', '2023-09-26 15:00:42', null);
INSERT INTO `dinky_sys_menu` VALUES (146, 10, ' 拷贝', '/registration/datasource/copy', null, 'registration:datasource:copy', 'CopyOutlined', 'F', 0, 42, '2023-09-26 14:02:28', '2023-09-26 15:00:41', null);


-- ----------------------------
-- Records of dinky_alert_rule
-- ----------------------------

INSERT INTO dinky_alert_rules (id, name, rule, template_id, rule_type, trigger_conditions, description, enabled, create_time, update_time, creator, updater) VALUES (3, 'alert.rule.jobFail', '[{"ruleKey":"jobStatus","ruleOperator":"EQ","ruleValue":"''FAILED''","rulePriority":"1"}]', 1, 'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-11-22 17:03:44', null, null);
INSERT INTO dinky_alert_rules (id, name, rule, template_id, rule_type, trigger_conditions, description, enabled, create_time, update_time, creator, updater) VALUES (4, 'alert.rule.getJobInfoFail', '[{"ruleKey":"jobStatus","ruleOperator":"EQ","ruleValue":"''UNKNOWN''","rulePriority":"1"}]', 1, 'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-11-22 17:03:44', null, null);
INSERT INTO dinky_alert_rules (id, name, rule, template_id, rule_type, trigger_conditions, description, enabled, create_time, update_time, creator, updater) VALUES (5, 'alert.rule.jobRestart', '[{"ruleKey":"jobStatus","ruleOperator":"EQ","ruleValue":"''RESTARTING''","rulePriority":"1"}]', 1, 'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-11-22 17:03:44', null, null);
INSERT INTO dinky_alert_rules (id, name, rule, template_id, rule_type, trigger_conditions, description, enabled, create_time, update_time, creator, updater) VALUES (6, 'alert.rule.checkpointFail', '[{"ruleKey":"isCheckpointFailed","ruleOperator":"EQ","ruleValue":"true"}]', 1, 'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-11-22 17:03:44', null, null);
INSERT INTO dinky_alert_rules (id, name, rule, template_id, rule_type, trigger_conditions, description, enabled, create_time, update_time, creator, updater) VALUES (7, 'alert.rule.jobRunException', '[{"ruleKey":"isException","ruleOperator":"EQ","ruleValue":"true"}]', 1, 'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-11-22 17:03:44', null, null);


INSERT INTO dinky_alert_template (id, name, template_content, enabled, create_time, update_time, creator, updater) VALUES (1, 'Default', '
- **Job Name :** <font color=''gray''>${jobName}</font>
- **Job Status :** <font color=''red''>${jobStatus}</font>
- **Alert Time :** ${alertTime}
- **Start Time :** ${jobStartTime}
- **End Time :** ${jobEndTime}
- **<font color=''red''>${errorMsg}</font>**
[Go toTask Web](http://${taskUrl})
', 1, '2023-11-20 17:45:48', '2023-11-23 00:14:22', null, 1);

COMMIT;




update dinky_user set super_admin_flag =1  where id =1;

alter table dinky_task alter column `step` set default 1;
-- todo: 需要修改历史作业的默认值 , 过滤条件待定



replace  INTO dinky_task
(id, name, tenant_id, dialect, type, check_point, save_point_strategy, save_point_path, parallelism, fragment, statement_set, batch_model, cluster_id, cluster_configuration_id, database_id,jar_id ,env_id, alert_group_id, config_json, note, step, job_instance_id, enabled, create_time, update_time, version_id, statement)
SELECT
                             t.id,
                             t.`name`,
                             t.tenant_id,
                             t.dialect,
                             t.type,
                             t.check_point,
                             t.save_point_strategy,
                             t.save_point_path,
                             t.parallelism,
                             t.fragment,
                             t.statement_set,
                             t.batch_model,
                             t.cluster_id,
                             t.cluster_configuration_id,
                             t.database_id,
                             t.jar_id,
                             t.env_id,
                             t.alert_group_id,
                             t.config_json,
                             t.note,
                             t.step,
                             t.job_instance_id,
                             t.enabled,
                             t.create_time,
                             t.update_time,
                             t.version_id,
                             s.statement
FROM
    dinky_task AS t
LEFT JOIN
    dinky_task_statement AS s ON t.id = s.id;


-- 删除dinky_job_history 的 jar_json 字段
alter table dinky_job_history drop column jar_json;
alter table dinky_task drop column jar_id;
UPDATE dinky_task_version SET task_configure=JSON_REMOVE(task_configure, '$.jarId');
UPDATE dinky_history SET config_json=JSON_REMOVE(config_json, '$.jarId');
UPDATE dinky_history SET config_json=JSON_REMOVE(config_json, '$.jarTask');
UPDATE dinky_history SET config_json=JSON_REMOVE(config_json, '$.session');
alter table dinky_history add batch_model boolean default false null after job_manager_address;

INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (218, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE print', 'Whole library synchronization print', 'EXECUTE CDCSOURCE demo_print WITH (
  \'connector\' = \'mysql-cdc\',
  \'hostname\' = \'127.0.0.1\',
  \'port\' = \'3306\',
  \'username\' = \'root\',
  \'password\' = \'123456\',
  \'checkpoint\' = \'10000\',
  \'scan.startup.mode\' = \'initial\',
  \'parallelism\' = \'1\',
  \'table-name\' = \'test\\.student,test\\.score\',
  \'sink.connector\' = \'print\'
);', 'All Versions', 0, 1, '2023-10-31 16:01:45', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (219, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE doris', 'Whole library synchronization doris', 'EXECUTE CDCSOURCE demo_print WITH (
  \'connector\' = \'mysql-cdc\',
  \'hostname\' = \'127.0.0.1\',
  \'port\' = \'3306\',
  \'username\' = \'root\',
  \'password\' = \'123456\',
  \'checkpoint\' = \'10000\',
  \'scan.startup.mode\' = \'initial\',
  \'parallelism\' = \'1\',
  \'table-name\' = \'test\\.student,test\\.score\',
  \'sink.connector\' = \'print\'
);', 'All Versions', 0, 1, '2023-10-31 16:02:21', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (220, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE demo_doris_schema_evolution', 'The entire library is synchronized to doris tape mode evolution', 'EXECUTE CDCSOURCE demo_doris_schema_evolution WITH (
  \'connector\' = \'mysql-cdc\',
  \'hostname\' = \'127.0.0.1\',
  \'port\' = \'3306\',
  \'username\' = \'root\',
  \'password\' = \'123456\',
  \'checkpoint\' = \'10000\',
  \'scan.startup.mode\' = \'initial\',
  \'parallelism\' = \'1\',
  \'table-name\' = \'test\\.student,test\\.score\',
  \'sink.connector\' = \'datastream-doris-schema-evolution\',
  \'sink.fenodes\' = \'127.0.0.1:8030\',
  \'sink.username\' = \'root\',
  \'sink.password\' = \'123456\',
  \'sink.doris.batch.size\' = \'1000\',
  \'sink.sink.max-retries\' = \'1\',
  \'sink.sink.batch.interval\' = \'60000\',
  \'sink.sink.db\' = \'test\',
  \'sink.table.identifier\' = \'${schemaName}.${tableName}\'
);', 'All Versions', 0, 1, '2023-10-31 16:04:53', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (221, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE StarRocks ', 'The entire library is synchronized to StarRocks
', 'EXECUTE CDCSOURCE demo_hudi WITH (
 \'connector\' = \'mysql-cdc\',
 \'hostname\' = \'127.0.0.1\',
 \'port\' = \'3306\',
 \'username\' = \'root\',
 \'password\' = \'123456\',
 \'checkpoint\' = \'10000\',
 \'scan.startup.mode\' = \'initial\',
 \'parallelism\' = \'1\',
 \'database-name\'=\'bigdata\',
 \'table-name\'=\'bigdata\\.products,bigdata\\.orders\',
 \'sink.connector\'=\'hudi\',
 \'sink.path\'=\'hdfs://nameservice1/data/hudi/${tableName}\',
 \'sink.hoodie.datasource.write.recordkey.field\'=\'${pkList}\',
 \'sink.hoodie.parquet.max.file.size\'=\'268435456\',
 \'sink.write.tasks\'=\'1\',
 \'sink.write.bucket_assign.tasks\'=\'2\',
 \'sink.write.precombine\'=\'true\',
 \'sink.compaction.async.enabled\'=\'true\',
 \'sink.write.task.max.size\'=\'1024\',
 \'sink.write.rate.limit\'=\'3000\',
 \'sink.write.operation\'=\'upsert\',
 \'sink.table.type\'=\'COPY_ON_WRITE\',
 \'sink.compaction.tasks\'=\'1\',
 \'sink.compaction.delta_seconds\'=\'20\',
 \'sink.compaction.async.enabled\'=\'true\',
 \'sink.read.streaming.skip_compaction\'=\'true\',
 \'sink.compaction.delta_commits\'=\'20\',
 \'sink.compaction.trigger.strategy\'=\'num_or_time\',
 \'sink.compaction.max_memory\'=\'500\',
 \'sink.changelog.enabled\'=\'true\',
 \'sink.read.streaming.enabled\'=\'true\',
 \'sink.read.streaming.check.interval\'=\'3\',
 \'sink.hive_sync.skip_ro_suffix\' = \'true\',
 \'sink.hive_sync.enable\'=\'true\',
 \'sink.hive_sync.mode\'=\'hms\',
 \'sink.hive_sync.metastore.uris\'=\'thrift://bigdata1:9083\',
 \'sink.hive_sync.db\'=\'qhc_hudi_ods\',
 \'sink.hive_sync.table\'=\'${tableName}\',
 \'sink.table.prefix.schema\'=\'true\'
);', 'All Versions', 0, 1, '2023-10-31 16:05:50', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (222, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_mysql', 'The entire library is synchronized to mysql', 'EXECUTE CDCSOURCE demo_startrocks WITH (
  \'connector\' = \'mysql-cdc\',
  \'hostname\' = \'127.0.0.1\',
  \'port\' = \'3306\',
  \'username\' = \'root\',
  \'password\' = \'123456\',
  \'checkpoint\' = \'3000\',
  \'scan.startup.mode\' = \'initial\',
  \'parallelism\' = \'1\',
  \'table-name\' = \'bigdata\\.products,bigdata\\.orders\',
  \'sink.connector\' = \'starrocks\',
  \'sink.jdbc-url\' = \'jdbc:mysql://127.0.0.1:19035\',
  \'sink.load-url\' = \'127.0.0.1:18035\',
  \'sink.username\' = \'root\',
  \'sink.password\' = \'123456\',
  \'sink.sink.db\' = \'ods\',
  \'sink.table.prefix\' = \'ods_\',
  \'sink.table.lower\' = \'true\',
  \'sink.database-name\' = \'ods\',
  \'sink.table-name\' = \'${tableName}\',
  \'sink.sink.properties.format\' = \'json\',
  \'sink.sink.properties.strip_outer_array\' = \'true\',
  \'sink.sink.max-retries\' = \'10\',
  \'sink.sink.buffer-flush.interval-ms\' = \'15000\',
  \'sink.sink.parallelism\' = \'1\'
);', 'All Versions', 0, 1, '2023-10-31 16:07:08', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (223, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE demo_doris', 'The entire library is synchronized to mysql', 'EXECUTE CDCSOURCE cdc_mysql WITH (
 \'connector\' = \'mysql-cdc\',
 \'hostname\' = \'127.0.0.1\',
 \'port\' = \'3306\',
 \'username\' = \'root\',
 \'password\' = \'123456\',
 \'checkpoint\' = \'3000\',
 \'scan.startup.mode\' = \'initial\',
 \'parallelism\' = \'1\',
 \'table-name\' = \'bigdata\\.products,bigdata\\.orders\',
 \'sink.connector\' = \'jdbc\',
 \'sink.url\' = \'jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8&useSSL=false\',
 \'sink.username\' = \'root\',
 \'sink.password\' = \'123456\',
 \'sink.sink.db\' = \'test\',
 \'sink.table.prefix\' = \'test_\',
 \'sink.table.lower\' = \'true\',
 \'sink.table-name\' = \'${tableName}\',
 \'sink.driver\' = \'com.mysql.jdbc.Driver\',
 \'sink.sink.buffer-flush.interval\' = \'2s\',
 \'sink.sink.buffer-flush.max-rows\' = \'100\',
 \'sink.sink.max-retries\' = \'5\',
 \'sink.auto.create\' = \'true\'
);', 'All Versions', 0, 1, '2023-10-31 16:07:47', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (224, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_oracle', 'The entire library is synchronized to cdc_oracle', 'EXECUTE CDCSOURCE cdc_oracle WITH (
 \'connector\' = \'oracle-cdc\',
 \'hostname\' = \'127.0.0.1\',
 \'port\' = \'1521\',
 \'username\'=\'root\',
 \'password\'=\'123456\',
 \'database-name\'=\'ORCL\',
 \'checkpoint\' = \'3000\',
 \'scan.startup.mode\' = \'initial\',
 \'parallelism\' = \'1\',
 \'table-name\' = \'TEST\\..*\',
 \'connector\' = \'jdbc\',
 \'url\' = \'jdbc:oracle:thin:@127.0.0.1:1521:orcl\',
 \'username\' = \'root\',
 \'password\' = \'123456\',
 \'table-name\' = \'TEST2.${tableName}\'
);', 'All Versions', 0, 1, '2023-10-31 16:08:30', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (225, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_kafka_one', 'The entire library is synchronized to a topic in kafka', 'EXECUTE CDCSOURCE cdc_kafka_one WITH (
 \'connector\' = \'mysql-cdc\',
 \'hostname\' = \'127.0.0.1\',
 \'port\' = \'3306\',
 \'username\' = \'root\',
 \'password\' = \'123456\',
 \'checkpoint\' = \'3000\',
 \'scan.startup.mode\' = \'initial\',
 \'parallelism\' = \'1\',
 \'table-name\' = \'bigdata\\.products,bigdata\\.orders\',
 \'sink.connector\'=\'datastream-kafka\',
 \'sink.topic\'=\'cdctest\',
 \'sink.brokers\'=\'bigdata2:9092,bigdata3:9092,bigdata4:9092\'
);', 'All Versions', 0, 1, '2023-10-31 16:10:13', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (226, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_kafka_mul', 'The entire library is synchronized to a single topic in kafka', 'EXECUTE CDCSOURCE cdc_kafka_mul WITH (
 \'connector\' = \'mysql-cdc\',
 \'hostname\' = \'127.0.0.1\',
 \'port\' = \'3306\',
 \'username\' = \'root\',
 \'password\' = \'123456\',
 \'checkpoint\' = \'3000\',
 \'scan.startup.mode\' = \'initial\',
 \'parallelism\' = \'1\',
 \'table-name\' = \'bigdata\\.products,bigdata\\.orders\',
 \'sink.connector\'=\'datastream-kafka\',
 \'sink.brokers\'=\'bigdata2:9092,bigdata3:9092,bigdata4:9092\'
)', 'All Versions', 0, 1, '2023-10-31 16:10:59', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (227, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_upsert_kafka', 'The entire library is synchronized to kafka primary key mode', 'EXECUTE CDCSOURCE cdc_upsert_kafka WITH (
 \'connector\' = \'mysql-cdc\',
 \'hostname\' = \'127.0.0.1\',
 \'port\' = \'3306\',
 \'username\' = \'root\',
 \'password\' = \'123456\',
 \'checkpoint\' = \'3000\',
 \'scan.startup.mode\' = \'initial\',
 \'parallelism\' = \'1\',
 \'table-name\' = \'bigdata\\.products,bigdata\\.orders\',
 \'sink.connector\' = \'upsert-kafka\',
 \'sink.topic\' = \'${tableName}\',
 \'sink.properties.bootstrap.servers\' = \'bigdata2:9092,bigdata3:9092,bigdata4:9092\',
 \'sink.key.format\' = \'json\',
 \'sink.value.format\' = \'json\'
);', 'All Versions', 0, 1, '2023-10-31 16:12:14', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (228, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_postgresql ', 'The entire library is synchronized to postgresql', 'EXECUTE CDCSOURCE cdc_postgresql WITH (
 \'connector\' = \'mysql-cdc\',
 \'hostname\' = \'127.0.0.1\',
 \'port\' = \'3306\',
 \'username\' = \'root\',
 \'password\' = \'123456\',
 \'checkpoint\' = \'3000\',
 \'scan.startup.mode\' = \'initial\',
 \'parallelism\' = \'1\',
 \'table-name\' = \'bigdata\\.products,bigdata\\.orders\',
 \'sink.connector\' = \'jdbc\',
 \'sink.url\' = \'jdbc:postgresql://127.0.0.1:5432/test\',
 \'sink.username\' = \'test\',
 \'sink.password\' = \'123456\',
 \'sink.sink.db\' = \'test\',
 \'sink.table.prefix\' = \'test_\',
 \'sink.table.lower\' = \'true\',
 \'sink.table-name\' = \'${tableName}\',
 \'sink.driver\' = \'org.postgresql.Driver\',
 \'sink.sink.buffer-flush.interval\' = \'2s\',
 \'sink.sink.buffer-flush.max-rows\' = \'100\',
 \'sink.sink.max-retries\' = \'5\'
)', 'All Versions', 0, 1, '2023-10-31 16:12:54', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (229, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_clickhouse', 'Sync the entire library to clickhouse', 'EXECUTE CDCSOURCE cdc_clickhouse WITH (
 \'connector\' = \'mysql-cdc\',
 \'hostname\' = \'127.0.0.1\',
 \'port\' = \'3306\',
 \'username\' = \'root\',
 \'password\' = \'123456\',
 \'checkpoint\' = \'3000\',
 \'scan.startup.mode\' = \'initial\',
 \'parallelism\' = \'1\',
 \'table-name\' = \'bigdata\\.products,bigdata\\.orders\',
  \'sink.connector\' = \'clickhouse\',
  \'sink.url\' = \'clickhouse://127.0.0.1:8123\',
  \'sink.username\' = \'default\',
  \'sink.password\' = \'123456\',
  \'sink.sink.db\' = \'test\',
  \'sink.table.prefix\' = \'test_\',
  \'sink.table.lower\' = \'true\',
  \'sink.database-name\' = \'test\',
  \'sink.table-name\' = \'${tableName}\',
  \'sink.sink.batch-size\' = \'500\',
  \'sink.sink.flush-interval\' = \'1000\',
  \'sink.sink.max-retries\' = \'3\'
);', 'All Versions', 0, 1, '2023-10-31 16:13:33', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (230, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE mysql2hive', 'The entire library is synchronized to the sql-catalog of hive', 'EXECUTE CDCSOURCE mysql2hive WITH (
  \'connector\' = \'mysql-cdc\',
  \'hostname\' = \'127.0.0.1\',
  \'port\' = \'3306\',
  \'username\' = \'root\',
  \'password\' = \'123456\',
  \'checkpoint\' = \'10000\',
  \'scan.startup.mode\' = \'initial\',
  \'parallelism\' = \'1\',
  \'table-name\' = \'test\\..*\',
  \'sink.connector\' = \'sql-catalog\',
  \'sink.catalog.name\' = \'hive\',
  \'sink.catalog.type\' = \'hive\',
  \'sink.default-database\' = \'hdb\',
  \'sink.hive-conf-dir\' = \'/usr/local/dlink/hive-conf\'
);', 'All Versions', 0, 1, '2023-10-31 16:14:31', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (231, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE  mysql2paimon', 'The entire library is synchronized to paimon', 'EXECUTE CDCSOURCE mysql2paimon WITH (
  \'connector\' = \'mysql-cdc\',
  \'hostname\' = \'127.0.0.1\',
  \'port\' = \'3306\',
  \'username\' = \'root\',
  \'password\' = \'123456\',
  \'checkpoint\' = \'10000\',
  \'scan.startup.mode\' = \'initial\',
  \'parallelism\' = \'1\',
  \'table-name\' = \'test\\..*\',
  \'sink.connector\' = \'sql-catalog\',
  \'sink.catalog.name\' = \'fts\',
  \'sink.catalog.type\' = \'table-store\',
  \'sink.catalog.warehouse\'=\'file:/tmp/table_store\'
);', 'All Versions', 0, 1, '2023-10-31 16:15:22', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (232, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE mysql2dinky_catalog', 'The entire library is synchronized to dinky\'s built-in catalog', 'EXECUTE CDCSOURCE mysql2dinky_catalog WITH (
  \'connector\' = \'mysql-cdc\',
  \'hostname\' = \'127.0.0.1\',
  \'port\' = \'3306\',
  \'username\' = \'root\',
  \'password\' = \'123456\',
  \'checkpoint\' = \'10000\',
  \'scan.startup.mode\' = \'initial\',
  \'parallelism\' = \'1\',
  \'table-name\' = \'test\\..*\',
  \'sink.connector\' = \'sql-catalog\',
  \'sink.catalog.name\' = \'dlinkmysql\',
  \'sink.catalog.type\' = \'dlink_mysql\',
  \'sink.catalog.username\' = \'dlink\',
  \'sink.catalog.password\' = \'dlink\',
  \'sink.catalog.url\' = \'jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC\',
  \'sink.sink.db\' = \'default_database\'
);', 'All Versions', 0, 1, '2023-10-31 16:16:22', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (233, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE mysql2multiple_sink', 'Synchronization of the entire library to multiple data sources (sink)', 'EXECUTE CDCSOURCE mysql2multiple_sink WITH (
  \'connector\' = \'mysql-cdc\',
  \'hostname\' = \'127.0.0.1\',
  \'port\' = \'3306\',
  \'username\' = \'root\',
  \'password\' = \'123456\',
  \'checkpoint\' = \'3000\',
  \'scan.startup.mode\' = \'initial\',
  \'parallelism\' = \'1\',
  \'table-name\' = \'test\\.student,test\\.score\',
  \'sink[0].connector\' = \'doris\',
  \'sink[0].fenodes\' = \'127.0.0.1:8030\',
  \'sink[0].username\' = \'root\',
  \'sink[0].password\' = \'dw123456\',
  \'sink[0].sink.batch.size\' = \'1\',
  \'sink[0].sink.max-retries\' = \'1\',
  \'sink[0].sink.batch.interval\' = \'60000\',
  \'sink[0].sink.db\' = \'test\',
  \'sink[0].table.prefix\' = \'ODS_\',
  \'sink[0].table.upper\' = \'true\',
  \'sink[0].table.identifier\' = \'${schemaName}.${tableName}\',
  \'sink[0].sink.label-prefix\' = \'${schemaName}_${tableName}_1\',
  \'sink[0].sink.enable-delete\' = \'true\',
  \'sink[1].connector\'=\'datastream-kafka\',
  \'sink[1].topic\'=\'cdc\',
  \'sink[1].brokers\'=\'127.0.0.1:9092\'
)', 'All Versions', 0, 1, '2023-10-31 16:17:27', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (234, 'Reference', 'FUN_UDF', 'OTHER_FUNCTION', 'ADD JAR', 'ADD JAR', 'ADD JAR ${1:}; -- str path ', 'All Versions', 0, 1, '2023-10-31 16:19:52', '2023-12-28 00:02:02', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (235, 'Function', 'Other', 'Other', 'SHOW FRAGMENTS', 'SHOW FRAGMENTS', 'SHOW FRAGMENTS;', 'All Versions', 0, 1, '2023-10-31 16:20:30', '2023-12-28 09:57:55', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (236, 'Function', 'Other', 'Other', 'SHOW FRAGMENT var1', 'SHOW FRAGMENT var1', 'SHOW FRAGMENT ${1:};', 'All Versions', 0, 1, '2023-10-31 16:21:23', '2023-12-28 09:57:54', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (237, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE demo_hudi', 'The entire library is synchronized to hudi', 'EXECUTE CDCSOURCE demo_hudi WITH (
 \'connector\' = \'mysql-cdc\',
 \'hostname\' = \'127.0.0.1\',
 \'port\' = \'3306\',
 \'username\' = \'root\',
 \'password\' = \'123456\',
 \'checkpoint\' = \'10000\',
 \'scan.startup.mode\' = \'initial\',
 \'parallelism\' = \'1\',
 \'database-name\'=\'bigdata\',
 \'table-name\'=\'bigdata\\.products,bigdata\\.orders\',
 \'sink.connector\'=\'hudi\',
 \'sink.path\'=\'hdfs://nameservice1/data/hudi/${tableName}\',
 \'sink.hoodie.datasource.write.recordkey.field\'=\'${pkList}\',
 \'sink.hoodie.parquet.max.file.size\'=\'268435456\',
 \'sink.write.tasks\'=\'1\',
 \'sink.write.bucket_assign.tasks\'=\'2\',
 \'sink.write.precombine\'=\'true\',
 \'sink.compaction.async.enabled\'=\'true\',
 \'sink.write.task.max.size\'=\'1024\',
 \'sink.write.rate.limit\'=\'3000\',
 \'sink.write.operation\'=\'upsert\',
 \'sink.table.type\'=\'COPY_ON_WRITE\',
 \'sink.compaction.tasks\'=\'1\',
 \'sink.compaction.delta_seconds\'=\'20\',
 \'sink.compaction.async.enabled\'=\'true\',
 \'sink.read.streaming.skip_compaction\'=\'true\',
 \'sink.compaction.delta_commits\'=\'20\',
 \'sink.compaction.trigger.strategy\'=\'num_or_time\',
 \'sink.compaction.max_memory\'=\'500\',
 \'sink.changelog.enabled\'=\'true\',
 \'sink.read.streaming.enabled\'=\'true\',
 \'sink.read.streaming.check.interval\'=\'3\',
 \'sink.hive_sync.skip_ro_suffix\' = \'true\',
 \'sink.hive_sync.enable\'=\'true\',
 \'sink.hive_sync.mode\'=\'hms\',
 \'sink.hive_sync.metastore.uris\'=\'thrift://bigdata1:9083\',
 \'sink.hive_sync.db\'=\'qhc_hudi_ods\',
 \'sink.hive_sync.table\'=\'${tableName}\',
 \'sink.table.prefix.schema\'=\'true\'
);', 'All Versions', 0, 1, '2023-10-31 16:24:47', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (238, 'Reference', 'SQL_TEMPLATE', 'FlinkJar', 'EXECUTE JAR ', 'EXECUTE JAR use sql', 'EXECUTE JAR WITH (
\'uri\'=\'rs:///jar/flink/demo/SocketWindowWordCount.jar\',
\'main-class\'=\'org.apache.flink.streaming.examples.socket\',
\'args\'=\' --hostname localhost \',
\'parallelism\'=\'\',
\'savepoint-path\'=\'\'
);', 'All Versions', 0, 1, '2023-10-31 16:27:53', '2023-12-28 09:57:54', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (239, 'Reference', 'FUN_UDF', 'OTHER_FUNCTION', 'PRINT tablename', 'PRINT table data', 'PRINT ${1:}', 'All Versions', 0, 1, '2023-10-31 16:30:22', '2023-12-28 00:09:39', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (240, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'CREATE TABLE Like', 'CREATE TABLE Like source table', 'DROP TABLE IF EXISTS sink_table;
CREATE TABLE IF not EXISTS sink_table
WITH (
    \'topic\' = \'motor_vehicle_error\'
)
LIKE source_table;', 'All Versions', 0, 1, '2023-10-31 16:33:38', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (241, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'CREATE TABLE like source_table EXCLUDING', 'CREATE TABLE like source_table EXCLUDING', 'DROP TABLE IF EXISTS sink_table;
CREATE TABLE IF not EXISTS sink_table(
     -- Add watermark definition
    WATERMARK FOR order_time AS order_time - INTERVAL \'5\' SECOND
)
WITH (
    \'topic\' = \'motor_vehicle_error\'
)
LIKE source_table (
     -- Exclude everything besides the computed columns which we need to generate the watermark for.
    -- We do not want to have the partitions or filesystem options as those do not apply to kafka.
    EXCLUDING ALL
    INCLUDING GENERATED
);', 'All Versions', 0, 1, '2023-10-31 16:36:13', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (242, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'CREATE TABLE ctas_kafka', 'CREATE TABLE ctas_kafka', 'CREATE TABLE my_ctas_table
WITH (
    \'connector\' = \'kafka\'
)
AS SELECT id, name, age FROM source_table WHERE mod(id, 10) = 0;', 'All Versions', 0, 1, '2023-10-31 16:37:33', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (243, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'CREATE TABLE rtas_kafka', 'CREATE TABLE rtas_kafka', 'CREATE OR REPLACE TABLE my_ctas_table
WITH (
    \'connector\' = \'kafka\'
)
AS SELECT id, name, age FROM source_table WHERE mod(id, 10) = 0;', 'All Versions', 0, 1, '2023-10-31 16:41:46', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (244, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'datagen job demo', 'datagen job demo', 'DROP TABLE IF EXISTS source_table3;
CREATE TABLE IF NOT EXISTS source_table3(
--订单id
`order_id` BIGINT,
--产品

`product` BIGINT,
--金额
`amount` BIGINT,

--支付时间
`order_time` as CAST(CURRENT_TIMESTAMP AS TIMESTAMP(3)), -- `在这里插入代码片`
--WATERMARK
WATERMARK FOR order_time AS order_time - INTERVAL \'2\' SECOND
) WITH(
\'connector\' = \'datagen\',
 \'rows-per-second\' = \'1\',
 \'fields.order_id.min\' = \'1\',
 \'fields.order_id.max\' = \'2\',
 \'fields.amount.min\' = \'1\',
 \'fields.amount.max\' = \'10\',
 \'fields.product.min\' = \'1\',
 \'fields.product.max\' = \'2\'
);

-- SELECT * FROM source_table3 LIMIT 10;

DROP TABLE IF EXISTS sink_table5;
CREATE TABLE IF NOT EXISTS sink_table5(
--产品
`product` BIGINT,
--金额
`amount` BIGINT,
--支付时间
`order_time` TIMESTAMP(3),
--1分钟时间聚合总数
`one_minute_sum` BIGINT
) WITH(
\'connector\'=\'print\'
);

INSERT INTO sink_table5
SELECT
product,
amount,
order_time,
SUM(amount) OVER(
PARTITION BY product
ORDER BY order_time
-- 标识统计范围是1个 product 的最近 1 分钟的数据
RANGE BETWEEN INTERVAL \'1\' MINUTE PRECEDING AND CURRENT ROW
) as one_minute_sum
FROM source_table3;', 'All Versions', 0, 1, '2023-11-15 15:42:16', '2023-12-28 00:02:57', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (245, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'checkpoint config', 'checkpoint config', '-- 声明一些调优参数 (checkpoint 等相关配置)
set \'execution.checkpointing.checkpoints-after-tasks-finish.enabled\' =\'true\';
SET \'pipeline.operator-chaining\' = \'false\';
set \'state.savepoints.dir\'=\'file:///opt/data/flink_cluster/savepoints\'; -- 目录自行修改
set \'state.checkpoints.dir\'= \'file:///opt/data/flink_cluster/checkpoints\'; -- 目录自行修改
-- set state.checkpoint-storage=\'filesystem\';
set \'state.backend.type\'=\'rocksdb\';
set \'execution.checkpointing.interval\'=\'60 s\';
set \'state.checkpoints.num-retained\'=\'100\';
-- 使 solt 均匀分布在 各个 TM 上
set \'cluster.evenly-spread-out-slots\'=\'true\';', 'All Versions', 0, 1, '2023-11-15 15:57:42', '2023-12-28 15:49:20', null, null);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (246, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'note template', 'note template', '-- -----------------------------------------------------------------
-- @Description(作业描述): ${1:}
-- @Creator(创建人): ${2:}
-- @Create DateTime(创建时间): ${3:}
-- -----------------------------------------------------------------

${4:}', 'All Versions', 0, 1, '2023-11-17 17:03:24', '2023-12-28 12:05:20', 1, 1);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (247, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'dinky_paimon_auto_create_table', 'dinky paimon auto create table', '-- -----------------------------------------------------------------
-- 该 demo 用于创建 mysql-cdc 到 paimon 的整库同步案例 并使用自动建表,注意 #{schemaName} 和 #{tableName} 为固定写法,不要修改,用于动态获取库名和表名
-- -----------------------------------------------------------------


EXECUTE CDCSOURCE dinky_paimon_auto_create_table
WITH
  (
    \'connector\' = \'mysql-cdc\',
    \'hostname\' = \'\',
    \'port\' = \'\',
    \'username\' = \'\',
    \'password\' = \'\',
    \'checkpoint\' = \'10000\',
    \'parallelism\' = \'1\',
    \'scan.startup.mode\' = \'initial\',
    \'database-name\' = \'dinky\',
    \'sink.connector\' = \'paimon\',
    \'sink.path\' = \'hdfs:/tmp/paimon/#{schemaName}.db/#{tableName}\',
    \'sink.auto-create\' = \'true\',
  );', 'All Versions', 0, 1, '2023-12-27 16:53:37', '2023-12-28 12:05:20', 1, 1);
INSERT INTO dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num, enabled, create_time, update_time, creator, updater) VALUES (248, 'Reference', 'FUN_UDF', 'OTHER_FUNCTION', 'add-customjar', 'add CUSTOMJAR 为 Dinky 扩展语法 功能实现和 add jar 类似 , 推荐使用此方式', '-- add CUSTOMJAR 为 Dinky 扩展语法 功能实现和 add jar 类似 , 推荐使用此方式
add CUSTOMJAR \'${1:}\';', 'All Versions', 0, 1, '2023-12-28 10:50:17', '2023-12-28 15:49:40', 1, 1);


-- 修改 dinky_udf_template 表的 enable 字段 不允许为空 默认为 1
alter table dinky_udf_template modify column `enabled` tinyint(1) not null default 1 comment 'is enable, 0:no 1:yes';

commit ;


-- 设置默认值 | set default value
begin ;
-- 将以上表 增加的字段的默认值设置为 1 即 admin 用户 | set creator/updater/operator field default value to 1
update dinky_alert_group set creator = 1 , updater = 1;
update dinky_alert_instance set creator = 1, updater = 1;
update dinky_alert_template set creator = 1, updater = 1;
update dinky_alert_rules set creator = 1, updater = 1;
update dinky_catalogue set creator = 1, updater = 1;
update dinky_task set creator = 1, updater = 1, operator = 1;
update dinky_task_version set creator = 1;
update dinky_cluster_configuration set creator = 1, updater = 1;
update dinky_cluster set creator = 1, updater = 1;
update dinky_database set creator = 1, updater = 1;
update dinky_flink_document set creator = 1, updater = 1;
update dinky_fragment set creator = 1, updater = 1;
update dinky_git_project set creator = 1, updater = 1, operator = 1;
update dinky_udf_manage set creator = 1, updater = 1;
update dinky_udf_template set creator = 1, updater = 1;
update dinky_savepoints set creator = 1;
update dinky_resources set creator = 1, updater = 1;
update dinky_row_permissions set creator = 1, updater = 1;
update dinky_job_instance set creator = 1, updater = 1, operator = 1;
update dinky_sys_token set creator = 1, updater = 1;
commit ;


update dinky_task set save_point_strategy = 0 where save_point_strategy is not null and save_point_strategy = -1;




UPDATE dinky_flink_document t SET t.type = 'FLINK_OPTIONS',t.subtype = '' WHERE t.type = '优化参数';

UPDATE dinky_flink_document t SET t.type = 'SQL_TEMPLATE',t.subtype = 'FlinkSql' WHERE t.type = '建表语句';

UPDATE dinky_flink_document t SET t.type = 'SQL_TEMPLATE',t.subtype = 'FlinkSql' WHERE t.type = 'CataLog';

UPDATE dinky_flink_document t SET t.type = 'FLINK_OPTIONS',t.subtype = '' WHERE t.type = '设置参数';

UPDATE dinky_flink_document t SET t.type = 'FUN_UDF' WHERE t.type = '内置函数';
UPDATE dinky_flink_document t SET t.type = 'FUN_UDF' WHERE t.type = 'UDF';

UPDATE dinky_flink_document t SET t.subtype = 'COMPARE_FUNCTION' WHERE t.subtype = '比较函数';
UPDATE dinky_flink_document t SET t.subtype = 'LOGICAL_FUNCTION' WHERE t.subtype = '逻辑函数';
UPDATE dinky_flink_document t SET t.subtype = 'ARITHMETIC_FUNCTIONS' WHERE t.subtype = '算术函数';
UPDATE dinky_flink_document t SET t.subtype = 'STRING_FUNCTIONS' WHERE t.subtype = '字符串函数';
UPDATE dinky_flink_document t SET t.subtype = 'TIME_FUNCTION' WHERE t.subtype = '时间函数';
UPDATE dinky_flink_document t SET t.subtype = 'CONDITIONAL_FUNCTION' WHERE t.subtype = '条件函数';
UPDATE dinky_flink_document t SET t.subtype = 'TYPE_CONVER_FUNCTION' WHERE t.subtype = '类型转换函数功能';
UPDATE dinky_flink_document t SET t.subtype = 'COLLECTION_FUNCTION' WHERE t.subtype = 'Collection 函数';
UPDATE dinky_flink_document t SET t.subtype = 'VALUE_CONSTRUCTION_FUNCTION' WHERE t.subtype = 'Value Construction函数';
UPDATE dinky_flink_document t SET t.subtype = 'VALUE_ACCESS_FUNCTION' WHERE t.subtype = 'Value Access函数';
UPDATE dinky_flink_document t SET t.subtype = 'GROUP_FUNCTION' WHERE t.subtype = '分组函数';
UPDATE dinky_flink_document t SET t.subtype = 'HASH_FUNCTION' WHERE t.subtype = 'hash函数';
UPDATE dinky_flink_document t SET t.subtype = 'AGGREGATE_FUNCTION' WHERE t.subtype = '聚合函数';
UPDATE dinky_flink_document t SET t.subtype = 'COLUMN_FUNCTION' WHERE t.subtype = '列函数';
UPDATE dinky_flink_document t SET t.subtype = 'TABLE_AGGREGATE_FUNCTION' WHERE t.subtype = '表值聚合函数';
UPDATE dinky_flink_document t SET t.subtype = 'OTHER_FUNCTION' WHERE t.subtype = '其他函数';
























