
SELECT add_column_if_not_exists('public','dinky_alert_history', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_history', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_job_instance', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_job_history', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_job_instance', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_sys_menu', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_sys_role_menu', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_row_permissions', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_user_role', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_user_tenant', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_udf_manage', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_udf_template', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_catalogue', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','dinky_cluster_configuration', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_cluster', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_dashboard', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_database', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_flink_document', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_fragment', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_git_project', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_metrics', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_sys_operate_log', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_resources', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_savepoints', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_sys_token', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_task', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_task_version', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_alert_group', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_alert_instance', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_alert_rules', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

select add_column_if_not_exists('public','dinky_alert_template', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

ALTER TABLE public.dinky_sys_login_log drop column  is_deleted;
select add_column_if_not_exists('public','dinky_sys_login_log', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');


SELECT add_column_if_not_exists('public','metadata_column', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','metadata_database', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','metadata_database_property', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','metadata_function', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','metadata_table', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');

SELECT add_column_if_not_exists('public','metadata_table_property', 'is_delete', 'boolean', 'false', 'is delete 0: false, 1: true');


CREATE UNIQUE INDEX unx1_metadata_column
    ON metadata_column USING BTREE (table_id, column_name, is_delete);

CREATE UNIQUE INDEX unx1_metadata_database
    ON metadata_database USING BTREE (database_name, is_delete);

CREATE UNIQUE INDEX unx1_metadata_database_property
    ON metadata_database_property USING BTREE (database_id, "key", is_delete);

CREATE UNIQUE INDEX unx1_metadata_function
    ON metadata_function USING BTREE ("function_name", database_id, is_delete);

CREATE UNIQUE INDEX unx1_metadata_table
    ON metadata_table USING BTREE ("table_name", database_id, is_delete);

CREATE UNIQUE INDEX unx1_metadata_table_property
    ON metadata_table_property USING BTREE (table_id, "key", is_delete);

