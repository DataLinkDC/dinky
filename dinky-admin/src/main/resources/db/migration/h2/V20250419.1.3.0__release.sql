SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE dinky_cluster ADD COLUMN config varchar(255) DEFAULT NULL comment 'A JSON string that contains configuration information such as the ResourceManager and ApplicationID' AFTER job_manager_host;

SET FOREIGN_KEY_CHECKS = 1;