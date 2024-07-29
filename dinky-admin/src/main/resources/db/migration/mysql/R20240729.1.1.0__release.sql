# note: Rolling back SQL statements is only necessary to perform a rollback operation in the event of an automatic upgrade failure. The following SQL statements need to be manually executed

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

delete from dinky_sys_menu where `parent_id` = -1 and path = '/home';

SET FOREIGN_KEY_CHECKS = 1;
