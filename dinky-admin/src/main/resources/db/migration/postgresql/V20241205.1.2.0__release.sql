
CREATE OR REPLACE TRIGGER set_update_time_dinky_user
              BEFORE UPDATE
                         ON public.dinky_user
                         FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();




-- Table structure for public.dinky_resources
CREATE TABLE IF NOT EXISTS public.dinky_resources_backup
(
    id           SERIAL PRIMARY KEY          NOT NULL,
    file_name    VARCHAR(64),
    description  VARCHAR(255),
    user_id      INT,
    type         SMALLINT,
    size         BIGINT,
    pid          INT,
    full_name    VARCHAR(255),
    is_directory boolean,
    create_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator      INT,
    updater      INT
);

CREATE UNIQUE INDEX IF NOT EXISTS dinky_resources_un1 ON public.dinky_resources_backup (full_name, type);

COMMENT ON TABLE public.dinky_resources_backup IS 'resources';

COMMENT ON COLUMN public.dinky_resources_backup.id IS 'key';
COMMENT ON COLUMN public.dinky_resources_backup.file_name IS 'file name';
COMMENT ON COLUMN public.dinky_resources_backup.user_id IS 'user id';
COMMENT ON COLUMN public.dinky_resources_backup.type IS 'resource type,0:FILE，1:UDF';
COMMENT ON COLUMN public.dinky_resources_backup.size IS 'resource size';

-- public.dinky_git_project
CREATE OR REPLACE TRIGGER update_dinky_dinky_resources
              BEFORE UPDATE
                         ON public.dinky_resources_backup
                         FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

insert into public.dinky_resources_backup(id, file_name, description, user_id, type, size, pid, full_name, is_directory, create_time, update_time, creator, updater)
select id, file_name, description, user_id, type, size, pid, full_name,
       case when is_directory = 0 then false else true end as is_directory
        , create_time, update_time, creator, updater
from public.dinky_resources;

drop table if exists public.dinky_resources;
alter table public.dinky_resources_backup rename to dinky_resources;







-- ----------------------------
-- Table structure for public.dinky_alert_instance
-- ----------------------------


CREATE TABLE IF NOT EXISTS public.dinky_alert_instance_backup
(
    id          SERIAL PRIMARY KEY NOT NULL,
    name        VARCHAR(50)        NOT NULL,
    tenant_id   INT                NOT NULL DEFAULT 1,
    type        VARCHAR(50),
    params      TEXT,
    enabled     boolean                    DEFAULT false,
    create_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator     INT                         DEFAULT NULL,
    updater     INT                         DEFAULT NULL,
    UNIQUE (name, tenant_id)
);

COMMENT
    ON COLUMN public.dinky_alert_instance_backup.id IS 'id';
COMMENT
    ON COLUMN public.dinky_alert_instance_backup.name IS 'alert instance name';
COMMENT
    ON COLUMN public.dinky_alert_instance_backup.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN public.dinky_alert_instance_backup.type IS 'alert instance type such as: DingTalk,Wechat(Webhook,app) Feishu ,email';
COMMENT
    ON COLUMN public.dinky_alert_instance_backup.params IS 'configuration';
COMMENT
    ON COLUMN public.dinky_alert_instance_backup.enabled IS 'is enable';
COMMENT
    ON COLUMN public.dinky_alert_instance_backup.create_time IS 'create time';
COMMENT
    ON COLUMN public.dinky_alert_instance_backup.update_time IS 'update time';
COMMENT
    ON COLUMN public.dinky_alert_instance_backup.creator IS 'creator user id';
COMMENT
    ON COLUMN public.dinky_alert_instance_backup.updater IS 'updater user id';
COMMENT
    ON TABLE public.dinky_alert_instance_backup IS 'Alert instance';


CREATE OR REPLACE TRIGGER set_update_time_dinky_alert_instance
              BEFORE UPDATE
                         ON public.dinky_alert_instance_backup
                         FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


insert into public.dinky_alert_instance_backup(id, name, tenant_id, type, params, enabled, create_time, update_time, creator, updater)
select id, name, tenant_id, type, params,
        case when enabled = 0 then false else true end as enabled
       , create_time, update_time, creator, updater
from public.dinky_alert_instance;

drop table if exists public.dinky_alert_instance;
alter table public.dinky_alert_instance_backup rename to dinky_alert_instance;









-- ----------------------------
-- Table structure for public.dinky_alert_group
-- ----------------------------

CREATE TABLE IF NOT EXISTS public.dinky_alert_group_backup
(
    id                 SERIAL PRIMARY KEY NOT NULL,
    name               VARCHAR(50)        NOT NULL,
    tenant_id          INT                NOT NULL DEFAULT 1,
    alert_instance_ids TEXT,
    note               VARCHAR(255)                DEFAULT NULL,
    enabled            boolean                    DEFAULT false,
    create_time        TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator            INT                         DEFAULT NULL,
    updater            INT                         DEFAULT NULL,
    CONSTRAINT alert_group_un_idx UNIQUE (name, tenant_id)
);

COMMENT
    ON COLUMN public.dinky_alert_group_backup.id IS 'id';
COMMENT
    ON COLUMN public.dinky_alert_group_backup.name IS 'alert group name';
COMMENT
    ON COLUMN public.dinky_alert_group_backup.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN public.dinky_alert_group_backup.alert_instance_ids IS 'Alert instance IDS';
COMMENT
    ON COLUMN public.dinky_alert_group_backup.note IS 'note';
COMMENT
    ON COLUMN public.dinky_alert_group_backup.enabled IS 'is enable';
COMMENT
    ON COLUMN public.dinky_alert_group_backup.create_time IS 'create time';
COMMENT
    ON COLUMN public.dinky_alert_group_backup.update_time IS 'update time';
COMMENT
    ON COLUMN public.dinky_alert_group_backup.creator IS 'creator user id';
COMMENT
    ON COLUMN public.dinky_alert_group_backup.updater IS 'updater user id';
COMMENT
    ON TABLE public.dinky_alert_group_backup IS 'Alert group';

CREATE OR REPLACE TRIGGER set_update_time_dinky_alert_group
    BEFORE UPDATE
    ON public.dinky_alert_group_backup
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

insert into public.dinky_alert_group_backup(id, name, tenant_id, alert_instance_ids, note, enabled, create_time, update_time, creator, updater)
select id, name, tenant_id, alert_instance_ids, note,
        case when enabled = 0 then false else true end as enabled
       , create_time, update_time, creator, updater
from public.dinky_alert_group;
drop table if exists public.dinky_alert_group;
alter table public.dinky_alert_group_backup rename to dinky_alert_group;




-- ----------------------------
-- Table structure for public.dinky_alert_template
-- ----------------------------
CREATE TABLE IF NOT EXISTS public.dinky_alert_template_backup
(
    id               SERIAL PRIMARY KEY          NOT NULL,
    name             VARCHAR(20),
    template_content TEXT,
    enabled          boolean                             DEFAULT false,
    create_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator          INT,
    updater          INT,
    CONSTRAINT dinky_alert_template_unx_idx UNIQUE (name)
);

COMMENT ON COLUMN public.dinky_alert_template_backup.id IS 'id';
COMMENT ON COLUMN public.dinky_alert_template_backup.name IS 'emplate name';
COMMENT ON COLUMN public.dinky_alert_template_backup.template_content IS 'template content';
COMMENT ON COLUMN public.dinky_alert_template_backup.enabled IS 'is enable';
-- public.dinky_git_project
CREATE OR REPLACE TRIGGER update_dinky_alert_template
    BEFORE UPDATE
    ON public.dinky_alert_template_backup
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

insert into public.dinky_alert_template_backup(id, name, template_content, enabled, create_time, update_time, creator, updater)
select id, name, template_content,
        case when enabled = 0 then false else true end as enabled
       , create_time, update_time, creator, updater
from public.dinky_alert_template;
drop table if exists public.dinky_alert_template;
alter table public.dinky_alert_template_backup rename to dinky_alert_template;




-- ----------------------------
-- Table structure for public.dinky_alert_rules
-- ----------------------------
CREATE TABLE IF NOT EXISTS public.dinky_alert_rules_backup
(
    id                 SERIAL PRIMARY KEY,
    name               VARCHAR(40)                 NOT NULL,
    rule               TEXT,
    template_id        INT,
    rule_type          VARCHAR(10),
    trigger_conditions VARCHAR(20),
    description        TEXT,
    enabled            boolean                             DEFAULT false,
    create_time        TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator            INT,
    updater            INT
);
CREATE UNIQUE INDEX IF NOT EXISTS dinky_alert_rules_name_unx ON public.dinky_alert_rules (name);

COMMENT ON COLUMN public.dinky_alert_rules_backup.id IS 'id';
COMMENT ON COLUMN public.dinky_alert_rules_backup.name IS 'rule name';
COMMENT ON COLUMN public.dinky_alert_rules_backup.rule IS 'specify rule';
COMMENT ON COLUMN public.dinky_alert_rules_backup.template_id IS 'template id';
COMMENT ON COLUMN public.dinky_alert_rules_backup.rule_type IS 'alert rule type';
COMMENT ON COLUMN public.dinky_alert_rules_backup.trigger_conditions IS 'trigger conditions';
COMMENT ON COLUMN public.dinky_alert_rules_backup.description IS 'description';

-- public.dinky_git_project
CREATE OR REPLACE TRIGGER update_dinky_alert_rules
    BEFORE UPDATE
    ON public.dinky_alert_rules_backup
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


insert into public.dinky_alert_rules_backup(id, name, rule, template_id, rule_type, trigger_conditions, description, enabled, create_time, update_time, creator, updater)
select id, name, rule, template_id, rule_type, trigger_conditions, description,
        case when enabled = 0 then false else true end as enabled
       , create_time, update_time, creator, updater
from public.dinky_alert_rules;
drop table if exists public.dinky_alert_rules;
alter table public.dinky_alert_rules_backup rename to dinky_alert_rules;





-- ----------------------------
-- Table structure for public.dinky_fragment
-- ----------------------------

CREATE TABLE IF NOT EXISTS public.dinky_fragment_backup
(
    id             serial PRIMARY KEY          NOT NULL,
    name           varchar(50)                 NOT NULL,
    tenant_id      int                         NOT NULL DEFAULT 1,
    fragment_value text                        NOT NULL,
    note           text,
    enabled        boolean                             DEFAULT false,
    create_time    timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator        int,
    updater        int,
    UNIQUE (name, tenant_id)
);

COMMENT ON TABLE public.dinky_fragment_backup is 'fragment management';
COMMENT ON COLUMN public.dinky_fragment_backup.id is 'id';
COMMENT ON COLUMN public.dinky_fragment_backup.name is 'fragment name';
COMMENT ON COLUMN public.dinky_fragment_backup.tenant_id is 'tenant id';
COMMENT ON COLUMN public.dinky_fragment_backup.fragment_value is 'fragment value';
COMMENT ON COLUMN public.dinky_fragment_backup.note is 'note';
COMMENT ON COLUMN public.dinky_fragment_backup.enabled is 'enabled';
COMMENT ON COLUMN public.dinky_fragment_backup.create_time is 'create time';
COMMENT ON COLUMN public.dinky_fragment_backup.update_time is 'update time';
COMMENT ON COLUMN public.dinky_fragment_backup.creator is 'creator';
COMMENT ON COLUMN public.dinky_fragment_backup.updater is 'updater';

CREATE OR REPLACE TRIGGER update_dinky_fragment_modtime
    BEFORE UPDATE
    ON public.dinky_fragment_backup
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

insert into public.dinky_fragment_backup(id, name, tenant_id, fragment_value, note, enabled, create_time, update_time, creator, updater)
select id, name, tenant_id, fragment_value, note,
        case when enabled = 0 then false else true end as enabled
       , create_time, update_time, creator, updater
from public.dinky_fragment;
drop table if exists public.dinky_fragment;
alter table public.dinky_fragment_backup rename to dinky_fragment;












