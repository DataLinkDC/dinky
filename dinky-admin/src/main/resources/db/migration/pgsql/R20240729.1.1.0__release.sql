# note: Rolling back SQL statements is only necessary to perform a rollback operation in the event of an automatic upgrade failure. The following SQL statements need to be manually executed


delete from public.dinky_sys_menu where parent_id = -1 and path = '/home';

