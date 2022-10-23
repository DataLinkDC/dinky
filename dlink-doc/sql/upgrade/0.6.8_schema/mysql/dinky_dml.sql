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
-- Records of dlink_tenant
-- ----------------------------
INSERT INTO `dlink_tenant`(`id`, `tenant_code`, `is_delete`, `note`, `create_time`, `update_time`)
VALUES (1, 'DefaultTenant', 0, 'DefaultTenant', current_time, current_time);

-- ----------------------------
-- Records of dlink_role
-- ----------------------------
INSERT INTO `dlink_role`(`id`, `tenant_id`, `role_code`, `role_name`, `is_delete`, `note`, `create_time`, `update_time`)
VALUES (1, 1, 'SuperAdmin', '超级管理员', 0, '超级管理员角色', current_time, current_time);

-- ----------------------------
-- Records of dlink_namespace
-- ----------------------------
INSERT INTO `dlink_namespace`(`id`, `tenant_id`, `namespace_code`, `enabled`, `note`, `create_time`, `update_time`)
VALUES (1, 1, 'DefaultNameSpace', 1, '默认命名空间', current_time, current_time);

-- ----------------------------
-- Records of dlink_role_namespace
-- ----------------------------
INSERT INTO `dlink_role_namespace`(`id`, `role_id`, `namespace_id`, `create_time`, `update_time`)
VALUES (1, 1, 1, current_time, current_time);

-- ----------------------------
-- Records of dlink_user_role
-- ----------------------------
INSERT INTO `dlink_user_role`(`id`, `user_id`, `role_id`, `create_time`, `update_time`)
VALUES (1, 1, 1, current_time, current_time);

-- ----------------------------
-- Records of dlink_user_tenant
-- ----------------------------
INSERT INTO `dlink_user_tenant`(`id`, `user_id`, `tenant_id`, `create_time`, `update_time`)
VALUES (1, 1, 1, current_time, current_time);


-- ----------------------------
-- Records of dlink_udf_template
-- ----------------------------
INSERT INTO `dlink_udf_template` (`id`, `name`, `code_type`, `function_type`, `template_code`, `enabled`, `create_time`, `update_time`)
VALUES (1, 'java_udf', 'java', 'UDF', 'package ${package};\n\nimport org.apache.flink.table.functions.ScalarFunction;\n\npublic class ${className} extends ScalarFunction {\n    public String eval(String s) {\n        return null;\n    }\n}', NULL, '2022-10-19 09:17:37', '2022-10-19 09:17:37');

INSERT INTO `dlink_udf_template` (`id`, `name`, `code_type`, `function_type`, `template_code`, `enabled`, `create_time`, `update_time`)
VALUES (2, 'java_udtf', 'java', 'UDTF', 'package ${package};\n\nimport org.apache.flink.table.functions.ScalarFunction;\n\n@FunctionHint(output = @DataTypeHint(\"ROW<word STRING, length INT>\"))\npublic static class ${className} extends TableFunction<Row> {\n\n  public void eval(String str) {\n    for (String s : str.split(\" \")) {\n      // use collect(...) to emit a row\n      collect(Row.of(s, s.length()));\n    }\n  }\n}', NULL, '2022-10-19 09:22:58', '2022-10-19 10:01:57');



-- 修改历史表的租户编号为默认租户
UPDATE `dlink_alert_group` SET `tenant_id` = 1;
UPDATE `dlink_alert_history` SET `tenant_id` = 1;
UPDATE `dlink_alert_instance` SET `tenant_id` = 1;
UPDATE `dlink_catalogue` SET `tenant_id` = 1;
UPDATE `dlink_cluster` SET `tenant_id` = 1;
UPDATE `dlink_cluster_configuration` SET `tenant_id` = 1;
UPDATE `dlink_database` SET `tenant_id` = 1;
UPDATE `dlink_history` SET `tenant_id` = 1;
UPDATE `dlink_jar` SET `tenant_id` = 1;
UPDATE `dlink_job_instance` SET `tenant_id` = 1;
UPDATE `dlink_savepoints` SET `tenant_id` = 1;
UPDATE `dlink_task` SET `tenant_id` = 1;
UPDATE `dlink_task_statement` SET `tenant_id` = 1;
UPDATE `dlink_task_version` SET `tenant_id` = 1;
UPDATE `dlink_job_history` SET `tenant_id` = 1;

