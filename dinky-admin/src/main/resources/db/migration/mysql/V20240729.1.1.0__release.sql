SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `dinky_sys_menu` (`parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`,
                              `create_time`, `update_time`, `note`)
VALUES (-1, '工作台', '/home', './Home', 'home', 'HomeOutlined', 'C', 0, 1, '2024-07-29 11:53:38',
        '2024-07-29 11:53:38', NULL);

INSERT INTO `dinky_sys_menu` (`parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`,
                              `create_time`, `update_time`, `note`)
VALUES (164, '仪表盘列表', '/dashboard/list', './Dashboard', 'dashboard:list', 'UnorderedListOutlined', 'F', 0, 167,
        '2024-07-29 15:37:46', '2024-07-29 15:37:46', NULL);

UPDATE `dinky_sys_menu`
SET `path` = '/dashboard/list/add',
    perms='dashboard:list:add'
WHERE `path` = '/dashboard/add';
UPDATE `dinky_sys_menu`
SET `path` = '/dashboard/list/edit',
    perms='dashboard:list:edit'
WHERE `path` = '/dashboard/edit';
UPDATE `dinky_sys_menu`
SET `path` = '/dashboard/list/delete',
    perms='dashboard:list:delete'
WHERE `path` = '/dashboard/delete';
UPDATE `dinky_sys_menu`
SET `path` = '/dashboard/list/view',
    perms='dashboard:list:view'
WHERE `path` = '/dashboard/view';


INSERT INTO `dinky_sys_menu` (`parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`,
                              `create_time`, `update_time`, `note`)
VALUES (164, '看板布局', '/dashboard/dashboard-layout', './Dashboard/DashboardLayout', 'dashboard:dashboard-layout',
        'DashboardOutlined', 'F', 0, 168, '2024-07-29 16:16:45', '2024-07-29 16:17:28', NULL);
INSERT INTO `dinky_sys_menu` (`parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`,
                              `create_time`, `update_time`, `note`)
VALUES (164, '添加看板', '/dashboard/chart/add', NULL, 'dashboard:chart:add', 'AreaChartOutlined', 'F', 0, 167,
        '2024-06-21 10:53:33', '2024-06-21 10:53:33', NULL);
INSERT INTO `dinky_sys_menu` (`parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`,
                              `create_time`, `update_time`, `note`)
VALUES (164, '修改看板', '/dashboard/chart/edit', NULL, 'dashboard:chart:edit', 'BarChartOutlined', 'F', 0, 168,
        '2024-06-21 10:54:26', '2024-06-21 10:54:26', NULL);


SET FOREIGN_KEY_CHECKS = 1;
