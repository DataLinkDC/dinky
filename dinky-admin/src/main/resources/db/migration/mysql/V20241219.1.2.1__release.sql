/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


SET NAMES Utf8mb4;
SET
    FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for dinky_approval
-- ----------------------------

CREATE TABLE IF NOT EXISTS `dinky_approval`
(
    `id`                    int(11)                                                      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `task_id`               int(11)                                                      NOT NULL COMMENT 'task id',
    `tenant_id`             int(11)                                                      NOT NULL default 1 COMMENT 'tenant id',
    `previous_task_version` int(11)                                                               DEFAULT NULL COMMENT 'previous version of task',
    `current_task_version`  int(11)                                                      NOT NULL COMMENT 'current version to be reviewed of task',
    `status`                varchar(50) CHARACTER SET Utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'approval status',
    `submitter`             int(11)                                                      NOT NULL COMMENT 'submitter user id',
    `submitter_comment`     text                                                                  DEFAULT NULL COMMENT 'submitter comment',
    `reviewer`              int(11)                                                               DEFAULT NULL COMMENT 'reviewer user id',
    `reviewer_comment`      text                                                                  DEFAULT NULL COMMENT 'reviewer comment',
    `create_time`           datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_tIme`           datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `task_id_current_version_union_idx` (`task_id`, `current_task_version`) USING BTREE,
    INDEX `submitter_tenant_id_union_idx` (`submitter`, `tenant_id`) USING BTREE,
    INDEX `reviewer_tenant_id_union_idx` (`reviewer`, `tenant_id`) USING BTREE
) ENGINE = INNODB
  AUTO_INCREMENT = 2
  CHARACTER SET = Utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'approval'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- approval menu
-- ----------------------------

INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`,
                              `order_num`, `create_time`, `update_time`, `note`)
VALUES (176, 4, '审批发布', '/auth/approval', './AuthCenter/Approval', 'auth:approval:operate', 'AuditOutlined', 'C', 0, 169,
        '2024-12-10 12:13:00', '2024-12-10 12:13:00', null);

insert into `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`,
                              `order_num`, `create_time`, `update_time`, `note`)
values (177, 24, '审批配置', '/settings/globalsetting/approval', null, 'settings:globalsetting:approval',
        'SettingOutlined', 'F', 0, 170, '2024-12-30 23:45:30', '2024-12-30 23:45:30', null);

insert into `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`,
                              `order_num`, `create_time`, `update_time`, `note`)
values (178, 177, '编辑', '/settings/globalsetting/approval/edit', null, 'settings:globalsetting:approval:edit',
        'EditOutlined', 'F', 0, 171, '2024-12-30 23:45:30', '2024-12-30 23:45:30', null);

SET
    FOREIGN_KEY_CHECKS = 1;