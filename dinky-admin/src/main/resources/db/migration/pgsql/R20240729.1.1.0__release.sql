# note: Rolling back SQL statements is only necessary to perform a rollback operation in the event of an automatic upgrade failure. The following SQL statements need to be manually executed


delete from public.dinky_sys_menu where parent_id = -1 and path = '/home';

delete from public.dinky_sys_menu where parent_id = 164 and path = '/dashboard/list';

update public.dinky_sys_menu set path = '/dashboard/add', perms='dashboard:add' where path = '/dashboard/list/add';
update public.dinky_sys_menu set path = '/dashboard/edit', perms='dashboard:edit' where path = '/dashboard/list/edit';
update public.dinky_sys_menu set path = '/dashboard/delete', perms='dashboard:delete' where path = '/dashboard/list/delete';
update public.dinky_sys_menu set path = '/dashboard/view', perms='dashboard:view' where path = '/dashboard/list/view';


delete from public.dinky_sys_menu where `parent_id` = 164 and path = '/dashboard/dashboard-layout';
delete from public.dinky_sys_menu where `parent_id` = 164 and path = '/dashboard/chart/add';
delete from public.dinky_sys_menu where `parent_id` = 164 and path = '/dashboard/chart/edit';
