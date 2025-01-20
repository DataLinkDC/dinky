SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

--- ----------------------------
-- Table structure for dinky_approval
-- ----------------------------
CREATE TABLE IF NOT EXISTS dinky_approval
(
    id                    int(11) AUTO_INCREMENT COMMENT 'id',
    task_id               int(11)     NOT NULL COMMENT 'task id',
    tenant_id             int(11)     NOT NULL default 1 COMMENT 'tenant id',
    previous_task_version int(11)                                  DEFAULT NULL COMMENT 'previous version of task',
    current_task_version  int(11)     NOT NULL COMMENT 'current version to be reviewed of task',
    status                VARCHAR(50) NOT NULL COMMENT 'approval status',
    submitter             int(11)     NOT NULL COMMENT 'submitter user id',
    submitter_comment     varchar(255)                             DEFAULT NULL COMMENT 'submitter comment',
    reviewer              int(11)                                  DEFAULT NULL COMMENT 'reviewer user id',
    reviewer_comment      varchar(255)                             DEFAULT NULL COMMENT 'reviewer comment',
    create_time           datetime(0) null                         DEFAULT null COMMENT 'create time',
    update_time           datetime(0) null                         DEFAULT null COMMENT 'update time'
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

-- ----------------------------
-- approval menu
-- ----------------------------
INSERT INTO `dinky_sys_menu` (id, parent_id, name, path, component, perms, icon, type, display, order_num, create_time,
                              update_time, note)
VALUES (176, 4, '审批发布', '/auth/approval', './AuthCenter/Approval', 'auth:approval:operate', 'AuditOutlined', 'C', 0, 169,
        '2024-12-10 12:13:00', '2024-12-10 12:13:00', NULL);

insert into `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`,
                              `order_num`, `create_time`, `update_time`, `note`)
values (177, 24, '审批配置', '/settings/globalsetting/approval', null, 'settings:globalsetting:approval',
        'SettingOutlined', 'F', 0, 170, '2024-12-30 23:45:30', '2024-12-30 23:45:30', null);

insert into `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`,
                              `order_num`, `create_time`, `update_time`, `note`)
values (178, 177, '编辑', '/settings/globalsetting/approval/edit', null, 'settings:globalsetting:approval:edit',
        'EditOutlined', 'F', 0, 171, '2024-12-30 23:45:30', '2024-12-30 23:45:30', null);

SET FOREIGN_KEY_CHECKS = 1;