# note: Rolling back SQL statements is only necessary to perform a rollback operation in the event of an automatic upgrade failure. The following SQL statements need to be manually executed

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

delete from dinky_sys_menu where `parent_id` = -1 and path = '/home';

delete from dinky_sys_menu where `parent_id` = 164 and path = '/dashboard/list';

update dinky_sys_menu set path = '/dashboard/add', perms='dashboard:add' where path = '/dashboard/list/add';
update dinky_sys_menu set path = '/dashboard/edit', perms='dashboard:edit' where path = '/dashboard/list/edit';
update dinky_sys_menu set path = '/dashboard/delete', perms='dashboard:delete' where path = '/dashboard/list/delete';
update dinky_sys_menu set path = '/dashboard/view', perms='dashboard:view' where path = '/dashboard/list/view';

delete from dinky_sys_menu where `parent_id` = 164 and path = '/dashboard/dashboard-layout';
delete from dinky_sys_menu where `parent_id` = 164 and path = '/dashboard/chart/add';
delete from dinky_sys_menu where `parent_id` = 164 and path = '/dashboard/chart/edit';


SET FOREIGN_KEY_CHECKS = 1;
