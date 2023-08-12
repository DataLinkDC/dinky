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
INSERT INTO `dinky_sys_menu` VALUES (1, -1, '首页', '/home', './Home', NULL, 'HomeOutlined', 'C', 0, NULL, '2023-08-11 14:06:52', '2023-08-11 14:06:52', NULL);
INSERT INTO `dinky_sys_menu` VALUES (2, -1, '运维中心', '/devops', NULL, NULL, 'ControlOutlined', 'M', 0, NULL, '2023-08-11 14:06:52', '2023-08-11 14:06:52', NULL);
INSERT INTO `dinky_sys_menu` VALUES (3, -1, '注册中心', '/registration', NULL, NULL, 'AppstoreOutlined', 'M', 0, NULL, '2023-08-11 14:06:52', '2023-08-11 14:06:52', NULL);
INSERT INTO `dinky_sys_menu` VALUES (4, -1, '认证中心', '/auth', NULL, NULL, 'SafetyCertificateOutlined', 'M', 0, NULL, '2023-08-11 14:06:52', '2023-08-11 14:06:52', NULL);
INSERT INTO `dinky_sys_menu` VALUES (5, -1, '数据开发', '/datastudio', './DataStudio', NULL, 'CodeOutlined', 'C', 0, NULL, '2023-08-11 14:06:52', '2023-08-11 14:06:52', NULL);
INSERT INTO `dinky_sys_menu` VALUES (6, -1, '配置中心', '/settings', NULL, NULL, 'SettingOutlined', 'M', 0, NULL, '2023-08-11 14:06:53', '2023-08-11 14:06:53', NULL);
INSERT INTO `dinky_sys_menu` VALUES (7, -1, '关于', '/about', './Other/About', NULL, 'SmileOutlined', 'C', 0, NULL, '2023-08-11 14:06:53', '2023-08-11 14:06:53', NULL);
INSERT INTO `dinky_sys_menu` VALUES (8, -1, '监控', '/metrics', './Metrics', NULL, 'DashboardOutlined', 'C', 0, NULL, '2023-08-11 14:06:53', '2023-08-11 14:06:53', NULL);
INSERT INTO `dinky_sys_menu` VALUES (9, 3, 'cluster', '/registration/cluster', NULL, NULL, 'GoldOutlined', 'M', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (10, 3, 'database', '/registration/database', './RegCenter/DataSource', NULL, 'DatabaseOutlined', 'M', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (11, -1, 'center', '/account/center', './Other/PersonCenter', NULL, NULL, 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (12, 3, 'alert', '/registration/alert', NULL, NULL, 'AlertOutlined', 'M', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (13, 3, 'document', '/registration/document', './RegCenter/Document', NULL, 'BookOutlined', 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (14, 3, 'fragment', '/registration/fragment', './RegCenter/GlobalVar', NULL, 'RocketOutlined', 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (15, 3, 'gitprojects', '/registration/gitprojects', './RegCenter/GitProject', NULL, 'GithubOutlined', 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (16, 3, 'udf', '/registration/udf', './RegCenter/UDF', NULL, 'ToolOutlined', 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (17, 2, 'job-detail', '/devops/job-detail', './DevOps/JobDetail', NULL, NULL, 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (18, 2, 'job', '/devops/joblist', './DevOps', NULL, NULL, 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (19, 3, 'resource', '/registration/resource', './RegCenter/Resource', NULL, 'FileZipOutlined', 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (20, 4, 'role', '/auth/role', './AuthCenter/Role', NULL, 'TeamOutlined', 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (21, 4, 'user', '/auth/user', './AuthCenter/User', NULL, 'UserOutlined', 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (22, 4, '菜单', '/auth/menu', './AuthCenter/Menu', NULL, 'MenuOutlined', 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (23, 4, 'tenant', '/auth/tenant', './AuthCenter/Tenant', NULL, 'SecurityScanOutlined', 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (24, 6, 'globalsetting', '/settings/globalsetting', './SettingCenter/GlobalSetting', NULL, 'SettingOutlined', 'C', 0, NULL, '2023-08-11 14:06:54', '2023-08-11 14:06:54', NULL);
INSERT INTO `dinky_sys_menu` VALUES (25, 6, 'systemlog', '/settings/systemlog', './SettingCenter/SystemLogs', NULL, 'InfoCircleOutlined', 'C', 0, NULL, '2023-08-11 14:06:55', '2023-08-11 14:06:55', NULL);
INSERT INTO `dinky_sys_menu` VALUES (26, 6, 'process', '/settings/process', './SettingCenter/Process', NULL, 'ReconciliationOutlined', 'C', 0, NULL, '2023-08-11 14:06:55', '2023-08-11 14:06:55', NULL);
INSERT INTO `dinky_sys_menu` VALUES (27, 4, 'rowpermissions', '/auth/rowpermissions', './AuthCenter/RowPermissions', NULL, 'SafetyCertificateOutlined', 'C', 0, NULL, '2023-08-11 14:06:55', '2023-08-11 14:06:55', NULL);
INSERT INTO `dinky_sys_menu` VALUES (28, 9, 'cluster-instance', '/registration/cluster/instance', './RegCenter/Cluster/Instance', NULL, NULL, 'C', 0, NULL, '2023-08-11 14:06:55', '2023-08-11 14:06:55', NULL);
INSERT INTO `dinky_sys_menu` VALUES (29, 12, 'group', '/registration/alert/group', './RegCenter/Alert/AlertGroup', NULL, NULL, 'C', 0, NULL, '2023-08-11 14:06:55', '2023-08-11 14:06:55', NULL);
INSERT INTO `dinky_sys_menu` VALUES (30, 9, 'cluster-config', '/registration/cluster/config', './RegCenter/Cluster/Configuration', NULL, NULL, 'C', 0, NULL, '2023-08-11 14:06:55', '2023-08-11 14:06:55', NULL);
INSERT INTO `dinky_sys_menu` VALUES (31, 12, 'instance', '/registration/alert/instance', './RegCenter/Alert/AlertInstance', NULL, NULL, 'C', 0, NULL, '2023-08-11 14:06:55', '2023-08-11 14:06:55', NULL);
COMMIT;




update dinky_user set super_admin_flag =1  where id =1;