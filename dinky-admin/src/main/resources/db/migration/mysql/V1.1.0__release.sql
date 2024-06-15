
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

SET FOREIGN_KEY_CHECKS = 1;
