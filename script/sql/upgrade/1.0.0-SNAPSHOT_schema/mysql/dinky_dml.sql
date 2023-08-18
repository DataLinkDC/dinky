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
INSERT INTO `dinky_sys_menu` VALUES (1, -1, '首页', '/home', './Home', null, 'HomeOutlined', 'C', 0, 1, '2023-08-11 14:06:52', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (2, -1, '运维中心', '/devops', null, null, 'ControlOutlined', 'M', 0, 5, '2023-08-11 14:06:52', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (3, -1, '注册中心', '/registration', null, null, 'AppstoreOutlined', 'M', 0, 9, '2023-08-11 14:06:52', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (4, -1, '认证中心', '/auth', null, null, 'SafetyCertificateOutlined', 'M', 0, 22, '2023-08-11 14:06:52', '2023-08-18 17:09:40', null);
INSERT INTO `dinky_sys_menu` VALUES (5, -1, '数据开发', '/datastudio', './DataStudio', null, 'CodeOutlined', 'C', 0, 4, '2023-08-11 14:06:52', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (6, -1, '配置中心', '/settings', null, null, 'SettingOutlined', 'M', 0, 28, '2023-08-11 14:06:53', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (7, -1, '关于', '/about', './Other/About', null, 'SmileOutlined', 'C', 0, 33, '2023-08-11 14:06:53', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (8, -1, '监控', '/metrics', './Metrics', null, 'DashboardOutlined', 'C', 0, 32, '2023-08-11 14:06:53', '2023-08-18 17:09:38', null);
INSERT INTO `dinky_sys_menu` VALUES (9, 3, 'cluster', '/registration/cluster', null, null, 'GoldOutlined', 'M', 0, 10, '2023-08-11 14:06:54', '2023-08-18 17:09:40', null);
INSERT INTO `dinky_sys_menu` VALUES (10, 3, 'database', '/registration/database', './RegCenter/DataSource', null, 'DatabaseOutlined', 'M', 0, 13, '2023-08-11 14:06:54', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (11, -1, 'center', '/account/center', './Other/PersonCenter', null, 'UserOutlined', 'C', 0, 34, '2023-08-11 14:06:54', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (12, 3, 'alert', '/registration/alert', null, null, 'AlertOutlined', 'M', 0, 14, '2023-08-11 14:06:54', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (13, 3, 'document', '/registration/document', './RegCenter/Document', null, 'BookOutlined', 'C', 0, 17, '2023-08-11 14:06:54', '2023-08-18 17:09:38', null);
INSERT INTO `dinky_sys_menu` VALUES (14, 3, 'fragment', '/registration/fragment', './RegCenter/GlobalVar', null, 'RocketOutlined', 'C', 0, 18, '2023-08-11 14:06:54', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (15, 3, 'gitprojects', '/registration/gitprojects', './RegCenter/GitProject', null, 'GithubOutlined', 'C', 0, 19, '2023-08-11 14:06:54', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (16, 3, 'udf', '/registration/udf', './RegCenter/UDF', null, 'ToolOutlined', 'C', 0, 20, '2023-08-11 14:06:54', '2023-08-18 17:09:40', null);
INSERT INTO `dinky_sys_menu` VALUES (17, 2, 'job-detail', '/devops/job-detail', './DevOps/JobDetail', null, null, 'C', 0, 8, '2023-08-11 14:06:54', '2023-08-18 17:09:40', null);
INSERT INTO `dinky_sys_menu` VALUES (18, 2, 'job', '/devops/joblist', './DevOps', null, null, 'C', 0, 6, '2023-08-11 14:06:54', '2023-08-18 17:09:40', null);
INSERT INTO `dinky_sys_menu` VALUES (19, 3, 'resource', '/registration/resource', './RegCenter/Resource', null, 'FileZipOutlined', 'C', 0, 21, '2023-08-11 14:06:54', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (20, 4, 'role', '/auth/role', './AuthCenter/Role', null, 'TeamOutlined', 'C', 0, 24, '2023-08-11 14:06:54', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (21, 4, 'user', '/auth/user', './AuthCenter/User', null, 'UserOutlined', 'C', 0, 23, '2023-08-11 14:06:54', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (22, 4, '菜单', '/auth/menu', './AuthCenter/Menu', null, 'MenuOutlined', 'C', 0, 25, '2023-08-11 14:06:54', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (23, 4, 'tenant', '/auth/tenant', './AuthCenter/Tenant', null, 'SecurityScanOutlined', 'C', 0, 26, '2023-08-11 14:06:54', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (24, 6, 'globalsetting', '/settings/globalsetting', './SettingCenter/GlobalSetting', null, 'SettingOutlined', 'C', 0, 29, '2023-08-11 14:06:54', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (25, 6, 'systemlog', '/settings/systemlog', './SettingCenter/SystemLogs', null, 'InfoCircleOutlined', 'C', 0, 30, '2023-08-11 14:06:55', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (26, 6, 'process', '/settings/process', './SettingCenter/Process', null, 'ReconciliationOutlined', 'C', 0, 31, '2023-08-11 14:06:55', '2023-08-18 17:09:38', null);
INSERT INTO `dinky_sys_menu` VALUES (27, 4, 'rowpermissions', '/auth/rowpermissions', './AuthCenter/RowPermissions', null, 'SafetyCertificateOutlined', 'C', 0, 27, '2023-08-11 14:06:55', '2023-08-18 17:09:40', null);
INSERT INTO `dinky_sys_menu` VALUES (28, 9, 'cluster-instance', '/registration/cluster/instance', './RegCenter/Cluster/Instance', null, null, 'C', 0, 11, '2023-08-11 14:06:55', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (29, 12, 'group', '/registration/alert/group', './RegCenter/Alert/AlertGroup', null, null, 'C', 0, 16, '2023-08-11 14:06:55', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (30, 9, 'cluster-config', '/registration/cluster/config', './RegCenter/Cluster/Configuration', null, null, 'C', 0, 12, '2023-08-11 14:06:55', '2023-08-18 17:09:40', null);
INSERT INTO `dinky_sys_menu` VALUES (31, 12, 'instance', '/registration/alert/instance', './RegCenter/Alert/AlertInstance', null, null, 'C', 0, 15, '2023-08-11 14:06:55', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (32, 1, '作业监控', '/home/jobOverView', 'JobOverView', 'show', 'AntCloudOutlined', 'F', 0, 2, '2023-08-15 16:52:59', '2023-08-18 17:09:39', null);
INSERT INTO `dinky_sys_menu` VALUES (33, 1, '数据开发', '/home/devOverView', 'DevOverView', 'show', 'AimOutlined', 'F', 0, 3, '2023-08-15 16:54:47', '2023-08-18 17:09:39', null);
COMMIT;




update dinky_user set super_admin_flag =1  where id =1;