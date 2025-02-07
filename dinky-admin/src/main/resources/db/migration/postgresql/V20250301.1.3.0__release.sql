/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


-- ----------------------------
-- Table structure for dinky_approval
-- ----------------------------

CREATE TABLE IF NOT EXISTS public.dinky_approval
(
    id                    SERIAL PRIMARY KEY          NOT NULL,
    task_id               INT                         NOT NULL,
    tenant_id             INT                         NOT NULL default 1,
    previous_task_version INT                         NOT NULL,
    current_task_version  INT                         NOT NULL,
    status                VARCHAR(255)                null,
    submitter             INT                         NOT NULL,
    submitter_comment     VARCHAR(255)                null,
    reviewer              INT                         NULL,
    reviewer_comment      VARCHAR(255)                null,
    create_time           TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time           TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN public.dinky_approval.id IS 'ID';
COMMENT ON COLUMN public.dinky_approval.task_id IS 'name';
COMMENT ON COLUMN public.dinky_approval.tenant_id IS 'tenant_id';
COMMENT ON COLUMN public.dinky_approval.previous_task_version IS 'previous version of task';
COMMENT ON COLUMN public.dinky_approval.current_task_version IS 'current version to be reviewed of task';
COMMENT ON COLUMN public.dinky_approval.status IS 'approval status';
COMMENT ON COLUMN public.dinky_approval.submitter IS 'submitter user id';
COMMENT ON COLUMN public.dinky_approval.submitter_comment IS 'submitter comment';
COMMENT ON COLUMN public.dinky_approval.reviewer IS 'reviewer user id';
COMMENT ON COLUMN public.dinky_approval.reviewer_comment IS 'reviewer comment';
COMMENT ON COLUMN public.dinky_approval.create_time IS 'create time';
COMMENT ON COLUMN public.dinky_approval.update_tIme IS 'update time';

CREATE INDEX IF NOT EXISTS task_id_current_version_idx ON public.dinky_approval (task_id, current_task_version);
CREATE INDEX IF NOT EXISTS tenant_id_submitter_union_idx ON public.dinky_approval (submitter, tenant_id);
CREATE INDEX IF NOT EXISTS tenant_id_reviewer_union_idx ON public.dinky_approval (reviewer, tenant_id);

CREATE OR REPLACE TRIGGER set_update_time_dinky_approval
    BEFORE UPDATE
    ON public.dinky_approval
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- ----------------------------
-- approval menu
-- ----------------------------

INSERT INTO public.dinky_sys_menu(id, parent_id, name, path, component, perms, icon, type, display, order_num,
                                  create_time, update_time, note)
VALUES (176, 4, '审批发布', '/auth/approval', './AuthCenter/Approval', 'auth:approval:operate', 'AuditOutlined', 'C', 0, 169,
        '2024-12-10 12:13:00', '2024-12-10 12:13:00', null);

insert into public.dinky_sys_menu (id, parent_id, name, path, component, perms, icon, type, display,
                              order_num, create_time, update_time, note)
values (177, 24, '审批配置', '/settings/globalsetting/approval', null, 'settings:globalsetting:approval',
        'SettingOutlined', 'F', 0, 170, '2024-12-30 23:45:30', '2024-12-30 23:45:30', null);

insert into public.dinky_sys_menu (id, parent_id, name, path, component, perms, icon, type, display,
                              order_num, create_time, update_time, note)
values (178, 177, '编辑', '/settings/globalsetting/approval/edit', null, 'settings:globalsetting:approval:edit',
        'EditOutlined', 'F', 0, 171, '2024-12-30 23:45:30', '2024-12-30 23:45:30', null);
