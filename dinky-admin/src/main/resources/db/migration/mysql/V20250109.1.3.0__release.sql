SET FOREIGN_KEY_CHECKS = 0;

CALL add_column_if_not_exists('dinky_alert_history', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_history', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_job_history', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_job_instance', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_sys_menu', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_sys_role_menu', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_row_permissions', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_user_role', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_user_tenant', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_udf_manage', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_udf_template', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_catalogue', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_cluster_configuration', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_cluster', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_dashboard', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_database', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_flink_document', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_fragment', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_git_project', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_metrics', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_sys_operate_log', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_resources', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_savepoints', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_sys_token', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_task', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_task_version', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_alert_group', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_alert_instance', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_alert_rules', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

CALL add_column_if_not_exists('dinky_alert_template', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

ALTER TABLE `dinky_sys_login_log`
    CHANGE COLUMN `is_deleted` `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'is delete 0: false, 1: true';

CALL add_column_if_not_exists('metadata_column', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);
CALL add_column_if_not_exists('metadata_database', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);
CALL add_column_if_not_exists('metadata_database_property', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);
CALL add_column_if_not_exists('metadata_function', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);
CALL add_column_if_not_exists('metadata_table', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);
CALL add_column_if_not_exists('metadata_table_property', 'is_delete', 'tinyint(1)', '0', 'is delete 0: false, 1: true', null);

ALTER TABLE metadata_column ADD UNIQUE INDEX unx_metadata_column(table_id, column_name, is_delete) USING BTREE;
ALTER TABLE metadata_database ADD UNIQUE INDEX unx_metadata_database(database_name, is_delete) USING BTREE;
ALTER TABLE metadata_database_property ADD UNIQUE INDEX unx_metadata_database_property(database_id, `key`, is_delete) USING BTREE;
ALTER TABLE metadata_function ADD UNIQUE INDEX unx_metadata_function(`function_name`, `database_id`, `is_delete`) USING BTREE;
ALTER TABLE metadata_table ADD UNIQUE INDEX unx_metadata_table(`table_name`, `database_id`, `is_delete`) USING BTREE;
ALTER TABLE metadata_table_property ADD UNIQUE INDEX unx_metadata_table_property(table_id, `key`, is_delete) USING BTREE;



SET FOREIGN_KEY_CHECKS = 1;
