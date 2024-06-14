# note: Rolling back SQL statements is only necessary to perform a rollback operation in the event of an automatic upgrade failure. The following SQL statements need to be manually executed

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;



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

ALTER TABLE dinky_history CHANGE COLUMN `statement` `statement` longtext DEFAULT NULL COMMENT 'statement set';
ALTER TABLE dinky_history CHANGE COLUMN `result` `result` text DEFAULT NULL COMMENT 'result set';
ALTER TABLE dinky_task CHANGE COLUMN `statement` `statement` longtext DEFAULT NULL COMMENT 'sql statement';
ALTER TABLE dinky_task_version CHANGE COLUMN `statement` `statement` longtext DEFAULT NULL COMMENT 'flink sql statement';


# Delete the 1.1.0 record in the _dinky_flyway_schema_history table
DELETE FROM `_dinky_flyway_schema_history` WHERE version = '1.1.0';
ALTER TABLE dinky_udf_manage DROP COLUMN `language`;


ALTER TABLE dinky_resources CHANGE COLUMN `file_name` `file_name` varchar(64) DEFAULT NULL COMMENT 'file name';


ALTER TABLE dinky_resources CHANGE COLUMN `file_name` `file_name` varchar(64) DEFAULT NULL COMMENT 'file name';


SET FOREIGN_KEY_CHECKS = 1;
