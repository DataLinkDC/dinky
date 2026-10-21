-- note: Rolling back SQL statements is only necessary to perform a rollback operation in the event of an automatic upgrade failure. The following SQL statements need to be manually executed
update ${dinky_schema}.dinky_sys_menu set "path"='/settings/alertrule',
                          "component"='./SettingCenter/AlertRule',
                          "perms"='settings:alertrule',
                          "parent_id"=6
where "id" = 116;

update ${dinky_schema}.dinky_sys_menu set "path"='/settings/alertrule/add',
                          "perms"='settings:alertrule:add'
where "id" = 117;
update ${dinky_schema}.dinky_sys_menu set "path"='/settings/alertrule/delete',
                          "perms"='settings:alertrule:delete'
where "id" = 118;
update ${dinky_schema}.dinky_sys_menu set "path"='/settings/alertrule/edit',
                          "perms"='settings:alertrule:edit'
where "id" = 119;

ALTER TABLE ${dinky_schema}.dinky_task DROP COLUMN "first_level_owner";
ALTER TABLE ${dinky_schema}.dinky_task DROP COLUMN "second_level_owners";


-- Delete the 1.1.0 record in the _dinky_flyway_schema_history table
DELETE FROM ${dinky_schema}."_dinky_flyway_schema_history" WHERE version = '1.1.0';

UPDATE ${dinky_schema}.dinky_user SET "password" = '21232f297a57a5a743894a0e4a801fc3' WHERE "id" =1 and "password"= 'f4b3a484ee745b98d64cd69c429b2aa2';


ALTER TABLE ${dinky_schema}.dinky_resources ALTER COLUMN file_name TYPE varchar(64);

alter table ${dinky_schema}.dinky_udf_manage drop column "language";
