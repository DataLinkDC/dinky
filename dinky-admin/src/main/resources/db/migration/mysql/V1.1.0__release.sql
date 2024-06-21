
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- 创建存储过程 用于添加表字段时判断字段是否存在, 如果字段不存在则添加字段, 如果字段存在则不执行任何操作,避免添加重复字段时抛出异常,从而终止Flyway执行, 在 Flyway 执行时, 如果你需要增加字段,必须使用该存储过程
-- Create a stored procedure to determine whether a field exists when adding table fields. If the field does not exist, add it. If the field exists, do not perform any operations to avoid throwing exceptions when adding duplicate fields. When executing in Flyway, if you need to add a field, you must use this stored procedure
-- Parameter Description:
-- tableName: Table name
-- columnName: Field name
-- columnDefinitionType: Field type
-- columnDefinitionDefaultValue Value: Field default value
-- columnDefinitionComment: Field comment
-- afterColumnName: Field position, default value is empty. If it is not empty, it means adding a field after the afterColumnName field

DELIMITER $$
DROP PROCEDURE IF EXISTS add_column_if_not_exists$$
CREATE PROCEDURE add_column_if_not_exists(IN tableName VARCHAR(64), IN columnName VARCHAR(64), IN columnDefinitionType VARCHAR(64), IN columnDefinitionDefaultValue VARCHAR(128), IN columnDefinitionComment VARCHAR(255), in afterColumnName VARCHAR(64))
BEGIN
    IF NOT EXISTS (
        SELECT *
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = tableName
          AND column_name = columnName
    ) THEN
        -- 判断 afterColumnName 是否存在该表内,并赋值给afterColumnNameExists , 不存在结束存储过程并抛出异常 | Determine whether afterColumnName exists in the table, if not end the stored procedure and throw an exception
        IF (afterColumnName IS NOT NULL) THEN
            SET @afterColumnNameExists = (
                SELECT column_name
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = tableName
                  AND column_name = afterColumnName
            );
            IF (@afterColumnNameExists IS NULL) THEN
                SIGNAL SQLSTATE '02000'
                    SET MESSAGE_TEXT = 'The afterColumnName does not exist in the table';
            END IF;
        END IF;


        -- 拼接sql语句,初始 sql | Splice SQL statement initialization sql
        SET @sql = CONCAT('ALTER TABLE ', tableName, ' ADD COLUMN ', columnName, ' ', columnDefinitionType, ' DEFAULT ', columnDefinitionDefaultValue, ' COMMENT ' , '\'', columnDefinitionComment ,'\'' );

        -- 判断 afterColumnName 入参是否 有值, 如果有值则拼接 afterColumnName 和 columnName 之间的关系 | Determine whether afterColumnName parameter has a value. If there is a value, the relationship between afterColumnName and columnName is spliced
        IF (afterColumnName IS NOT NULL and @afterColumnNameExists is not null) THEN
            SET @sql = CONCAT(@sql, ' AFTER '  , afterColumnName);
        END IF;
        -- 查看拼接的sql语句 | View the spliced SQL statement
        SELECT @sql AS executeSqlStatement;
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
    END IF;
END$$

DELIMITER ;


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


CALL add_column_if_not_exists('dinky_task', 'first_level_owner', 'int', 'NULL', 'primary responsible person id' ,null);
CALL add_column_if_not_exists('dinky_task', 'second_level_owners', 'varchar(128)', 'NULL', 'list of secondary responsible persons ids' , null);


update dinky_task set first_level_owner = creator;

ALTER TABLE dinky_alert_template MODIFY COLUMN `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'template name';


ALTER TABLE dinky_history CHANGE COLUMN `statement` `statement` mediumtext DEFAULT NULL COMMENT 'statement set';

ALTER TABLE dinky_history CHANGE COLUMN `result` `result` mediumtext DEFAULT NULL COMMENT 'result set';

ALTER TABLE dinky_task CHANGE COLUMN `statement` `statement` mediumtext DEFAULT NULL COMMENT 'sql statement';

ALTER TABLE dinky_task_version CHANGE COLUMN `statement` `statement` mediumtext DEFAULT NULL COMMENT 'flink sql statement';

ALTER TABLE dinky_resources CHANGE COLUMN `file_name` `file_name` text DEFAULT NULL COMMENT 'file name';

CALL add_column_if_not_exists('dinky_udf_manage', 'language', 'varchar(10)', 'NULL', 'udf language' , 'class_name');

-- update dashboard
CALL add_column_if_not_exists('dinky_metrics','vertices_title','varchar(255)','NULL','vertices title','vertices' );



CREATE TABLE IF NOT EXISTS `dinky_dashboard` (
                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                   `name` varchar(255) DEFAULT NULL,
                                   `remark` text,
                                   `chart_theme` varchar(255) DEFAULT NULL,
                                   `layouts` longtext,
                                   `create_time` datetime DEFAULT NULL,
                                   `update_time` datetime DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='dashboard';



INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (164, -1, '看板', '/dashboard', './Dashboard', 'dashboard', 'DashboardOutlined', 'C', 0, 162, '2024-06-18 22:04:34', '2024-06-18 22:04:34', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (165, 164, '创建仪表盘', '/dashboard/add', NULL, 'dashboard:add', 'AppstoreAddOutlined', 'F', 0, 163, '2024-06-18 22:05:50', '2024-06-18 22:05:50', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (166, 164, '修改仪表盘', '/dashboard/edit', NULL, 'dashboard:edit', 'EditFilled', 'F', 0, 164, '2024-06-18 22:06:44', '2024-06-18 22:06:44', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (167, 164, '删除仪表盘', '/dashboard/delete', NULL, 'dashboard:delete', 'DeleteOutlined', 'F', 0, 165, '2024-06-18 22:07:04', '2024-06-18 22:07:04', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (168, 164, '进入仪表盘', '/dashboard/view', '', 'dashboard:view', 'FundViewOutlined', 'F', 0, 166, '2024-06-21 10:36:00', '2024-06-21 10:36:00', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (169, 164, '添加看板', '/dashboard/chart/add', NULL, 'dashboard:chart:add', 'AreaChartOutlined', 'F', 0, 167, '2024-06-21 10:53:33', '2024-06-21 10:53:33', NULL);
INSERT INTO `dinky_sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `perms`, `icon`, `type`, `display`, `order_num`, `create_time`, `update_time`, `note`) VALUES (170, 164, '修改看板', '/dashboard/chart/edit', NULL, 'dashboard:chart:edit', 'BarChartOutlined', 'F', 0, 168, '2024-06-21 10:54:26', '2024-06-21 10:54:26', NULL);


SET FOREIGN_KEY_CHECKS = 1;
