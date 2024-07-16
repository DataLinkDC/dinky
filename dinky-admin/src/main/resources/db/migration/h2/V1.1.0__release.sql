

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

INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (164, -1, '看板', '/dashboard', './Dashboard', 'dashboard', 'DashboardOutlined', 'C', 0, 162, '2024-06-18 22:04:34', '2024-06-18 22:04:34', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (165, 164, '创建仪表盘', '/dashboard/add', NULL, 'dashboard:add', 'AppstoreAddOutlined', 'F', 0, 163, '2024-06-18 22:05:50', '2024-06-18 22:05:50', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (166, 164, '修改仪表盘', '/dashboard/edit', NULL, 'dashboard:edit', 'EditFilled', 'F', 0, 164, '2024-06-18 22:06:44', '2024-06-18 22:06:44', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (167, 164, '删除仪表盘', '/dashboard/delete', NULL, 'dashboard:delete', 'DeleteOutlined', 'F', 0, 165, '2024-06-18 22:07:04', '2024-06-18 22:07:04', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (168, 164, '进入仪表盘', '/dashboard/view', '', 'dashboard:view', 'FundViewOutlined', 'F', 0, 166, '2024-06-21 10:36:00', '2024-06-21 10:36:00', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (169, 164, '添加看板', '/dashboard/chart/add', NULL, 'dashboard:chart:add', 'AreaChartOutlined', 'F', 0, 167, '2024-06-21 10:53:33', '2024-06-21 10:53:33', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (170, 164, '修改看板', '/dashboard/chart/edit', NULL, 'dashboard:chart:edit', 'BarChartOutlined', 'F', 0, 168, '2024-06-21 10:54:26', '2024-06-21 10:54:26', NULL);
