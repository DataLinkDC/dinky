-- note: Rolling back SQL statements is only necessary to perform a rollback operation in the event of an automatic upgrade failure. The following SQL statements need to be manually executed

update dinky_sys_menu set `path`='/settings/alertrule',
                          `component`='./SettingCenter/AlertRule',
                          `perms`='settings:alertrule',
                          `parent_id`=6
where `id` = 116;

update dinky_sys_menu set `path`='/settings/alertrule/add',
                          `perms`='settings:alertrule:add'
where `id` = 117;
update dinky_sys_menu set `path`='/settings/alertrule/delete',
                          `perms`='settings:alertrule:delete'
where `id` = 118;
update dinky_sys_menu set `path`='/settings/alertrule/edit',
                          `perms`='settings:alertrule:edit'
where `id` = 119;

ALTER TABLE dinky_task DROP COLUMN `first_level_owner`;
ALTER TABLE dinky_task DROP COLUMN `second_level_owners`;


ALTER TABLE dinky_udf_manage ALTER COLUMN class_name SET DATA TYPE VARCHAR(50);

ALTER TABLE dinky_history ALTER COLUMN statement SET DATA TYPE text ;

ALTER TABLE dinky_task ALTER COLUMN statement SET DATA TYPE text ;

ALTER TABLE dinky_task_version ALTER COLUMN statement SET DATA TYPE text ;

-- Delete the 1.1.0 record in the _dinky_flyway_schema_history table
DELETE FROM `_dinky_flyway_schema_history` WHERE version = '1.1.0';

ALTER TABLE dinky_resources ALTER COLUMN `file_name` SET DATA TYPE VARCHAR(64);