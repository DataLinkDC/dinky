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

-- rename table
ALTER TABLE dlink_alert_group RENAME dinky_alert_group;
ALTER TABLE dlink_alert_history RENAME dinky_alert_history;
ALTER TABLE dlink_alert_instance RENAME dinky_alert_instance;
ALTER TABLE dlink_catalogue RENAME dinky_catalogue;
ALTER TABLE dlink_cluster RENAME dinky_cluster;
ALTER TABLE dlink_cluster_configuration RENAME dinky_cluster_configuration;
ALTER TABLE dlink_database RENAME dinky_database;
ALTER TABLE dlink_flink_document RENAME dinky_flink_document;
ALTER TABLE dlink_fragment RENAME dinky_fragment;
ALTER TABLE dlink_history RENAME dinky_history;
ALTER TABLE dlink_jar RENAME dinky_jar;
ALTER TABLE dlink_job_history RENAME dinky_job_history;
ALTER TABLE dlink_job_instance RENAME dinky_job_instance;
ALTER TABLE dlink_namespace RENAME dinky_namespace;
ALTER TABLE dlink_role RENAME dinky_role;
ALTER TABLE dlink_role_namespace RENAME dinky_role_namespace;
ALTER TABLE dlink_savepoints RENAME dinky_savepoints;
ALTER TABLE dlink_schema_history RENAME dinky_schema_history;
ALTER TABLE dlink_sys_config RENAME dinky_sys_config;
ALTER TABLE dlink_task RENAME dinky_task;
ALTER TABLE dlink_task_statement RENAME dinky_task_statement;
ALTER TABLE dlink_task_version RENAME dinky_task_version;
ALTER TABLE dlink_tenant RENAME dinky_tenant;
ALTER TABLE dlink_udf RENAME dinky_udf;
ALTER TABLE dlink_udf_template RENAME dinky_udf_template;
ALTER TABLE dlink_upload_file_record RENAME dinky_upload_file_record;
ALTER TABLE dlink_user RENAME dinky_user;
ALTER TABLE dlink_user_role RENAME dinky_user_role;
ALTER TABLE dlink_user_tenant RENAME dinky_user_tenant;


-- rename index
ALTER TABLE dinky_alert_group RENAME INDEX `dlink_alert_instance_un` TO `alert_group_un_idx1`;
ALTER TABLE dinky_alert_instance RENAME INDEX `dlink_alert_instance_un` TO `alert_instance_un_idx1`;
ALTER TABLE dinky_cluster RENAME INDEX `dlink_cluster_un` TO `cluster_un_idx1`;
ALTER TABLE dinky_cluster_configuration RENAME INDEX `dlink_cluster_configuration_un` TO `cluster_configuration_un_idx1`;
ALTER TABLE dinky_database RENAME INDEX `dlink_database_un` TO `database_un_idx1`;
ALTER TABLE dinky_jar RENAME INDEX `dlink_jar_un` TO `jar_un_idx1`;
ALTER TABLE dinky_job_instance RENAME INDEX `dlink_job_instance_un` TO `job_instance_un_idx1`;
ALTER TABLE dinky_job_instance RENAME INDEX `dlink_job_instance_task_id_IDX` TO `job_instance_task_id_idx1`;
ALTER TABLE dinky_task RENAME INDEX `dlink_task_un` TO `task_un_idx1`;
ALTER TABLE dinky_task_statement RENAME INDEX `dlink_task_statement_un` TO `task_statement_un_idx1`;
ALTER TABLE dinky_task_version RENAME INDEX `dlink_task_version_un` TO `task_version_un_idx1`;
ALTER TABLE dinky_fragment RENAME INDEX `un_idx1` TO `fragment_un_idx1`;
ALTER TABLE dinky_namespace RENAME INDEX `dlink_namespace_un` TO `namespace_un_idx1`;
ALTER TABLE dinky_role RENAME INDEX `dlink_role_un` TO `role_un_idx1`;
ALTER TABLE dinky_role_namespace RENAME INDEX `dlink_role_namespace_un` TO `role_namespace_un_idx1`;
ALTER TABLE dinky_user_role RENAME INDEX `dlink_user_role_un` TO `user_role_un_idx1`;
ALTER TABLE dinky_user_tenant RENAME INDEX `dlink_user_role_un` TO `user_tenant_un_idx1`;
ALTER TABLE dinky_catalogue RENAME INDEX `dlink_catalogue_un` TO `catalogue_un_idx1`;
ALTER TABLE dinky_schema_history RENAME INDEX `dlink_schema_history_s_idx` TO `schema_history_idx`;

ALTER TABLE dinky_fragment DROP COLUMN `alias`;
ALTER TABLE dinky_database DROP COLUMN `alias`;
