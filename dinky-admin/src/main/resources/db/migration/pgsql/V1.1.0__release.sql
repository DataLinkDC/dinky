

update dinky_sys_menu
set "path"='/registration/alert/rule',
    "component"='./RegCenter/Alert/AlertRule',
    "perms"='registration:alert:rule',
    "parent_id"=12
where "id" = 116;

update dinky_sys_menu
set "path"='/registration/alert/rule/add',
    "perms"='registration:alert:rule:add'
where "id" = 117;

update dinky_sys_menu
set "path"='/registration/alert/rule/delete',
    "perms"='registration:alert:rule:delete'
where "id" = 118;

update dinky_sys_menu
set "path"='/registration/alert/rule/edit',
    "perms"='registration:alert:rule:edit'
where "id" = 119;



-- Increase class_name column's length from 50 to 100.
ALTER TABLE dinky_udf_manage ALTER COLUMN class_name TYPE VARCHAR(100);

COMMENT ON COLUMN dinky_udf_manage.class_name IS 'Complete class name';

alter table dinky_task add column first_level_owner int;
alter table dinky_task add column second_level_owners varchar(128);

COMMENT ON COLUMN dinky_task.first_level_owner IS 'primary responsible person id';
COMMENT ON COLUMN dinky_task.second_level_owners IS 'list of secondary responsible persons ids';

update dinky_task set "first_level_owner" = "creator";
