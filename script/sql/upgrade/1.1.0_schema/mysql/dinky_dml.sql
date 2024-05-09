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


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

begin;

update dinky_sys_menu
set `path`='/registration/alert/rule',
    `component`='./RegCenter/Alert/AlertRule',
    `perms`='registration:alert:rule',
    `parent_id`=12
where `id` = 116;

update dinky_sys_menu
set `path`='/registration/alert/rule/add',
    `perms`='registration:alert:rule:add'
where `id` = 117;

update dinky_sys_menu
set `path`='/registration/alert/rule/delete',
    `perms`='registration:alert:rule:delete'
where `id` = 118;

update dinky_sys_menu
set `path`='/registration/alert/rule/edit',
    `perms`='registration:alert:rule:edit'
where `id` = 119;

update dinky_task set first_level_owner = creator;

commit ;