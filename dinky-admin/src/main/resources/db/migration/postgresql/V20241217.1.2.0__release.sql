
-- Table structure for public.dinky_user_tenant
CREATE TABLE IF NOT EXISTS public.dinky_user_tenant_backup
(
    id                serial PRIMARY KEY,
    user_id           int       NOT NULL,
    tenant_id         int       NOT NULL,
    tenant_admin_flag boolean           DEFAULT false,
    create_time       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS  user_tenant_un_idx1 ON public.dinky_user_tenant_backup (user_id, tenant_id);

COMMENT ON TABLE public.dinky_user_tenant_backup IS 'Relationship between users and tenants';
COMMENT ON COLUMN public.dinky_user_tenant_backup.id IS 'id';
COMMENT ON COLUMN public.dinky_user_tenant_backup.user_id IS 'user id';
COMMENT ON COLUMN public.dinky_user_tenant_backup.tenant_id IS 'tenant id';
COMMENT ON COLUMN public.dinky_user_tenant_backup.tenant_admin_flag IS 'tenant admin flag(0:false,1:true)';

CREATE OR REPLACE TRIGGER set_update_time_dinky_user_tenant
    BEFORE UPDATE
    ON public.dinky_user_tenant_backup
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

insert into public.dinky_user_tenant_backup(id, user_id, tenant_id, tenant_admin_flag, create_time, update_time)
select id, user_id, tenant_id,
       case when tenant_admin_flag = 0 then false else true end as tenant_admin_flag,
       create_time, update_time
from public.dinky_user_tenant;

drop table if exists public.dinky_user_tenant;
alter table public.dinky_user_tenant_backup rename to dinky_user_tenant;





DO $$
    DECLARE
        table_seq_info text[][] := ARRAY[
            -- Enter the table name and the corresponding sequence name in order
            ['dinky_alert_group', 'dinky_alert_group_id_seq'],
            ['dinky_alert_history', 'dinky_alert_history_id_seq'],
            ['dinky_alert_instance', 'dinky_alert_instance_id_seq'],
            ['dinky_alert_rules', 'dinky_alert_rules_id_seq'],
            ['dinky_alert_template', 'dinky_alert_template_id_seq'],
            ['dinky_catalogue', 'dinky_catalogue_id_seq'],
            ['dinky_cluster', 'dinky_cluster_id_seq'],
            ['dinky_cluster_configuration', 'dinky_cluster_configuration_id_seq'],
            ['dinky_dashboard', 'dinky_dashboard_id_seq'],
            ['dinky_database', 'dinky_database_id_seq'],
            ['dinky_flink_document', 'dinky_flink_document_id_seq'],
            ['dinky_fragment', 'dinky_fragment_id_seq'],
            ['dinky_git_project', 'dinky_git_project_id_seq'],
            ['dinky_history', 'dinky_history_id_seq'],
            ['dinky_job_history', 'dinky_job_history_id_seq'],
            ['dinky_job_instance', 'dinky_job_instance_id_seq'],
            ['dinky_metrics', 'dinky_metrics_id_seq'],
            ['dinky_resources', 'dinky_resources_id_seq'],
            ['dinky_role', 'dinky_role_id_seq'],
            ['dinky_row_permissions', 'dinky_row_permissions_id_seq'],
            ['dinky_savepoints', 'dinky_savepoints_id_seq'],
            ['dinky_sys_config', 'dinky_sys_config_id_seq'],
            ['dinky_sys_login_log', 'dinky_sys_login_log_id_seq'],
            ['dinky_sys_menu', 'dinky_sys_menu_id_seq'],
            ['dinky_sys_operate_log', 'dinky_sys_operate_log_id_seq'],
            ['dinky_sys_role_menu', 'dinky_sys_role_menu_id_seq'],
            ['dinky_sys_token', 'dinky_sys_token_id_seq'],
            ['dinky_task', 'dinky_task_id_seq'],
            ['dinky_task_version', 'dinky_task_version_id_seq'],
            ['dinky_tenant', 'dinky_tenant_id_seq'],
            ['dinky_udf_manage', 'dinky_udf_manage_id_seq'],
            ['dinky_udf_template', 'dinky_udf_template_id_seq'],
            ['dinky_user', 'dinky_user_id_seq'],
            ['dinky_user_role', 'dinky_user_role_id_seq'],
            ['dinky_user_tenant', 'dinky_user_tenant_id_seq'],
            ['metadata_database', 'metadata_database_id_seq'],
            ['metadata_function', 'metadata_function_id_seq'],
            ['metadata_table', 'metadata_table_id_seq']
            ];
        i integer := 1;
    BEGIN
        -- Loop through each table-name and series-name combination
        WHILE i <= array_length(table_seq_info, 1) LOOP
                -- Obtain the current table name and sequence name
                DECLARE
                    table_name text := table_seq_info[i][1];
                    seq_name text := table_seq_info[i][2];
                    max_id integer;
                BEGIN
                    -- Step 1: Query the maximum value of ID in the current table (for each table)
                    EXECUTE format('SELECT MAX(id) FROM %I', table_name) INTO max_id;
                    -- Step 2: Delete existing sequences (for each table)
                    EXECUTE format('DROP SEQUENCE IF EXISTS %I CASCADE', seq_name);
                    -- Step 3: Recreate the sequence and set the maximum value (for each table)
                    EXECUTE format('CREATE SEQUENCE IF NOT EXISTS %I START WITH %s INCREMENT BY 1 ',
                                   seq_name,
                                   COALESCE(max_id, 0) + 1  -- The starting value is set to the maximum id value in the table plus 1, or starting with 1 if the table is empty
                                   );
                    -- Step 4: Set the default value of the id column in the table to the next value of the new series (for each table)
                    EXECUTE format('ALTER TABLE %I ALTER COLUMN id SET DEFAULT nextval(''%I'')', table_name, seq_name);
                END;
                i := i + 1;
            END LOOP;
    END $$;


