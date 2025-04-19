SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CALL add_column_if_not_exists('dinky_cluster', 'config', 'varchar(255)', 'NULL', 'A JSON string that contains configuration information such as the ResourceManager and ApplicationID' , 'job_manager_host');

SET FOREIGN_KEY_CHECKS = 1;