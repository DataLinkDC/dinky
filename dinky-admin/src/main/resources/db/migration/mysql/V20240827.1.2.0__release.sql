SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Create a stored procedure to determine if an index exists when deleting it. If it does not exist, do not perform the deletion operation to avoid throwing an exception during deletion and terminating Flyway execution
-- Parameter Description:
-- tableName: Table name
-- indexName: index name
DELIMITER $$
DROP PROCEDURE IF EXISTS drop_index_if_exists$$
CREATE PROCEDURE drop_index_if_exists(IN tableName VARCHAR(255), IN indexName VARCHAR(255))
BEGIN
    DECLARE index_exists INT DEFAULT 0;

    -- 检查索引是否存在
    SELECT COUNT(*) INTO index_exists
    FROM information_schema.statistics
    WHERE table_name = tableName
      AND index_name = indexName;

    -- 如果索引存在，则删除它
    IF index_exists > 0 THEN
        SET @sql = CONCAT('DROP INDEX ', indexName, ' ON ', tableName);
        PREPARE stmt FROM @sql;
        SELECT @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

call drop_index_if_exists('dinky_cluster', 'cluster_un_idx1');


SET FOREIGN_KEY_CHECKS = 1;
