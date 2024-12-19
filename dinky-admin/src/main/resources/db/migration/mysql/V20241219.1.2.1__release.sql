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
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for dinky_approval
-- ----------------------------

CREATE TABLE IF NOT EXISTS `dinky_approval`
(
    `id`                    int(11)                                                      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `task_id`               int(11)                                                      NOT NULL COMMENT 'task id',
    `previous_task_version` int(11)                                                               DEFAULT NULL COMMENT 'previous version of task',
    `current_task_version`  int(11)                                                      NOT NULL COMMENT 'current version to be reviewed of task',
    `status`                varchar(50) CHARACTER SET Utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'approval status',
    `submitter`             int(11)                                                      NOT NULL COMMENT 'submitter user id',
    `submitter_comment`     text                                                                  DEFAULT NULL COMMENT 'submitter comment',
    `reviewer`              int(11)                                                      NOT NULL COMMENT 'reviewer user id',
    `reviewer_comment`      text                                                                  DEFAULT NULL COMMENT 'reviewer comment',
    `create_time`           datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_tIme`           datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `task_id_current_version_idx` (`task_id`, `current_task_version`) USING BTREE,
    INDEX `submitter_idx` (`submitter`) USING BTREE,
    INDEX `reviewer_idx` (`reviewer`) USING BTREE
) ENGINE = INNODB
  AUTO_INCREMENT = 2
  CHARACTER SET = Utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'approval'
  ROW_FORMAT = Dynamic;

INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`)
VALUES (176, -1, '审批中心', '/approval', null, 'approval', 'LockOutlined', 'M', 0, 169, '2024-12-10 12:13:00', '2024-12-10 12:13:00', null);

INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`)
VALUES (177, 176, '任务中心', '/approval/taskApproval', './ApprovalCenter/TaskApproval', 'approval:taskApproval', 'CarryOutlined', 'C', 0, 170, '2024-12-10 12:13:00', '2024-12-10 12:13:00', null);

SET FOREIGN_KEY_CHECKS = 1;