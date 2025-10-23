ALTER TABLE `dinky_alert_history`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_history`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_job_history`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_job_instance`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_sys_menu`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_sys_role_menu`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_row_permissions`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_user_role`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_user_tenant`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_udf_manage`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_udf_template`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_catalogue`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_cluster_configuration`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_cluster`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_dashboard`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_database`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';


ALTER TABLE `dinky_flink_document`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_fragment`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_git_project`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_metrics`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_sys_operate_log`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_resources`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_savepoints`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_sys_token`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_task`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_task_version`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_alert_group`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_alert_instance`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_alert_rules`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_alert_template`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `dinky_sys_login_log`
    CHANGE COLUMN `is_deleted` `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `metadata_column`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `metadata_database`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `metadata_database_property`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `metadata_function`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `metadata_table`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE `metadata_table_property`
    ADD COLUMN `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

ALTER TABLE metadata_column
    ADD UNIQUE INDEX unx_metadata_column (table_id, column_name, is_delete) USING BTREE;
ALTER TABLE metadata_database
    ADD UNIQUE INDEX unx_metadata_database (database_name, is_delete) USING BTREE;
ALTER TABLE metadata_database_property
    ADD UNIQUE INDEX unx_metadata_database_property (database_id, `key`, is_delete) USING BTREE;
ALTER TABLE metadata_function
    ADD UNIQUE INDEX unx_metadata_function (`function_name`, `database_id`, `is_delete`) USING BTREE;
ALTER TABLE metadata_table
    ADD UNIQUE INDEX unx_metadata_table (`table_name`, `database_id`, `is_delete`) USING BTREE;
ALTER TABLE metadata_table_property
    ADD UNIQUE INDEX unx_metadata_table_property (table_id, `key`, is_delete) USING BTREE;

