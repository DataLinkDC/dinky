


-- Table structure for public.dinky_user

CREATE TABLE IF NOT EXISTS public.dinky_user_backup
(
    id               serial PRIMARY KEY NOT NULL,
    username         varchar(50)        NOT NULL,
    user_type        int                         DEFAULT 1,
    password         varchar(50),
    nickname         varchar(50),
    worknum          varchar(50),
    avatar           bytea,
    mobile           varchar(20),
    enabled          boolean            NOT NULL DEFAULT true,
    super_admin_flag boolean                    DEFAULT false,
    is_delete        boolean            NOT NULL DEFAULT false,
    create_time      timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE public.dinky_user_backup IS 'user';
COMMENT ON COLUMN public.dinky_user_backup.id IS 'id';
COMMENT ON COLUMN public.dinky_user_backup.username IS 'username';
COMMENT ON COLUMN public.dinky_user_backup.password IS 'password';
COMMENT ON COLUMN public.dinky_user_backup.nickname IS 'nickname';
COMMENT ON COLUMN public.dinky_user_backup.worknum IS 'worknum';
COMMENT ON COLUMN public.dinky_user_backup.avatar IS 'avatar';
COMMENT ON COLUMN public.dinky_user_backup.mobile IS 'mobile phone';
COMMENT ON COLUMN public.dinky_user_backup.enabled IS 'enabled';
COMMENT ON COLUMN public.dinky_user_backup.super_admin_flag IS 'is super admin(0:false,1true)';


insert into public.dinky_user_backup(id, username, user_type, password, nickname, worknum, avatar, mobile, enabled, super_admin_flag, is_delete, create_time, update_time)
select id, username, user_type, password, nickname, worknum, avatar, mobile, enabled,
     case when super_admin_flag = 0 then false else true end as super_admin_flag
    , is_delete, create_time, update_time
from public.dinky_user;

drop table if exists public.dinky_user;
alter table public.dinky_user_backup rename to dinky_user;
