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


-- 0.7.2 2023-2-12
-- ----------------------------
-- ----------------------------
-- Table structure for dlink_role_select_permissions
-- ----------------------------
CREATE TABLE dlink_role_select_permissions
(
    id           int auto_increment comment 'ID'
        primary key,
    role_id      int      not null comment '角色ID',
    table_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL  comment '表名',
    expression varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL  comment '表达式',
    create_time  datetime null comment '创建时间',
    update_time  datetime null comment '更新时间'
)
    COMMENT '角色数据查询权限' COLLATE = utf8mb4_general_ci;
    
-- 0.7.2 2023-2-15
-- ----------------------------
-- ----------------------------
-- Table structure for dlink_cluster 
-- ----------------------------
alter table dlink_cluster add column `resource_manager_addr`  VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci comment 'Resource Manger Address' after task_id;
alter table dlink_cluster add column `application_id` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  comment 'Application Id' after resource_manager_addr;
