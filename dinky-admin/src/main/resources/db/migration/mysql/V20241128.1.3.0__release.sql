SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dinky_task_test_case
-- ----------------------------
CREATE TABLE IF NOT EXISTS  `dinky_task_test_case` (
    `id`          INT(11)                                                       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `task_id`     INT(11)                                                       NOT NULL COMMENT 'taskId',
    `table_name`  VARCHAR(255) CHARACTER SET Utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'table name',
    `columns`     MEDIUMTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL COMMENT 'columns',
    `row_data`    MEDIUMTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL COMMENT 'row data',
    `update_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_table_name_un_idx1` (`task_id`, `table_name`) USING BTREE,
    FOREIGN KEY (`task_id`) REFERENCES `dinky_task` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB  AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Test Case' ROW_FORMAT = Dynamic;


SET FOREIGN_KEY_CHECKS = 1;