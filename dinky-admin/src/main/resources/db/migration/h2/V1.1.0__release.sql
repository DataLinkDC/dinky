

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
ALTER TABLE dinky_udf_manage ALTER COLUMN class_name SET DATA TYPE VARCHAR(100);

ALTER TABLE dinky_task
    add  COLUMN `first_level_owner` int DEFAULT NULL comment 'primary responsible person id';

ALTER TABLE dinky_task
    add  COLUMN `second_level_owners` varchar(128) DEFAULT NULL comment 'list of secondary responsible persons ids';


update dinky_task set first_level_owner = creator;


ALTER TABLE dinky_history ALTER COLUMN statement SET DATA TYPE LONGVARCHAR ;

ALTER TABLE dinky_history ALTER COLUMN result SET DATA TYPE LONGVARCHAR ;

ALTER TABLE dinky_task ALTER COLUMN statement SET DATA TYPE LONGVARCHAR ;

ALTER TABLE dinky_task_version ALTER COLUMN statement SET DATA TYPE LONGVARCHAR ;

ALTER TABLE dinky_resources ALTER COLUMN `file_name` SET DATA TYPE TEXT;
alter table dinky_udf_manage add column `language` VARCHAR(10) DEFAULT null comment 'udf language' ;


-- update dashboard
ALTER TABLE `dinky_metrics` ADD COLUMN `vertices_title` varchar(255) NULL COMMENT 'vertices title' AFTER `vertices`;

CREATE TABLE `dinky_dashboard` (
                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                   `name` varchar(255) DEFAULT NULL,
                                   `remark` text,
                                   `chart_theme` varchar(255) DEFAULT NULL,
                                   `layouts` longtext,
                                   `create_time` datetime DEFAULT NULL,
                                   `update_time` datetime DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE = InnoDB ROW_FORMAT = Dynamic;
