
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


update dinky_sys_menu
set `path`='/registration/alert/rule',
    `component`='./RegCenter/Alert/AlertRule',
    `perms`='registration:alert:rule',
    `parent_id`=12
where `id` = 116;

update dinky_sys_menu
set `path`='/registration/alert/rule/add',
    `perms`='registration:alert:rule:add'
where `id` = 117;

update dinky_sys_menu
set `path`='/registration/alert/rule/delete',
    `perms`='registration:alert:rule:delete'
where `id` = 118;

update dinky_sys_menu
set `path`='/registration/alert/rule/edit',
    `perms`='registration:alert:rule:edit'
where `id` = 119;



-- Increase class_name column's length from 50 to 100.
ALTER TABLE dinky_udf_manage CHANGE COLUMN class_name class_name VARCHAR(100) null DEFAULT null COMMENT 'Complete class name';

ALTER TABLE dinky_task
    add  COLUMN `first_level_owner` int DEFAULT NULL comment 'primary responsible person id';

ALTER TABLE dinky_task
    add  COLUMN `second_level_owners` varchar(128) DEFAULT NULL comment 'list of secondary responsible persons ids';


update dinky_task set first_level_owner = creator;



SET FOREIGN_KEY_CHECKS = 1;
