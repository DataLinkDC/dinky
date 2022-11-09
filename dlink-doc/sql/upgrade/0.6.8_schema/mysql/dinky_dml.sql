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
VALUES (1, 1, 'SuperAdmin', 'SuperAdmin', 0, 'SuperAdmin of Role', current_time, current_time);

-- ----------------------------
-- Records of dlink_namespace
-- ----------------------------
INSERT INTO `dlink_namespace`(`id`, `tenant_id`, `namespace_code`, `enabled`, `note`, `create_time`, `update_time`)
VALUES (1, 1, 'DefaultNameSpace', 1, 'DefaultNameSpace', current_time, current_time);

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
VALUES (1, 'java_udf', 'Java', 'UDF', '${(package==\'\')?string(\'\',\'package \'+package+\';\')}\n\nimport org.apache.flink.table.functions.ScalarFunction;\n\npublic class ${className} extends ScalarFunction {\n    public String eval(String s) {\n        return null;\n    }\n}', NULL, '2022-10-19 09:17:37', '2022-10-25 17:45:57');

INSERT INTO `dlink_udf_template` (`id`, `name`, `code_type`, `function_type`, `template_code`, `enabled`, `create_time`, `update_time`)
VALUES (2, 'java_udtf', 'Java', 'UDTF', '${(package==\'\')?string(\'\',\'package \'+package+\';\')}\n\nimport org.apache.flink.table.functions.ScalarFunction;\n\n@FunctionHint(output = @DataTypeHint(\"ROW<word STRING, length INT>\"))\npublic static class ${className} extends TableFunction<Row> {\n\n  public void eval(String str) {\n    for (String s : str.split(\" \")) {\n      // use collect(...) to emit a row\n      collect(Row.of(s, s.length()));\n    }\n  }\n}', NULL, '2022-10-19 09:22:58', '2022-10-25 17:49:30');

INSERT INTO `dlink_udf_template` (`id`, `name`, `code_type`, `function_type`, `template_code`, `enabled`, `create_time`, `update_time`)
VALUES (3, 'scala_udf', 'Scala', 'UDF', '${(package==\'\')?string(\'\',\'package \'+package+\';\')}\n\nimport org.apache.flink.table.api._\nimport org.apache.flink.table.functions.ScalarFunction\n\n// 定义可参数化的函数逻辑\nclass ${className} extends ScalarFunction {\n  def eval(s: String, begin: Integer, end: Integer): String = {\n    \"this is scala\"\n  }\n}', NULL, '2022-10-25 09:21:32', '2022-10-25 17:49:46');

INSERT INTO `dlink_udf_template` (`id`, `name`, `code_type`, `function_type`, `template_code`, `enabled`, `create_time`, `update_time`)
VALUES (4, 'python_udf_1', 'Python', 'UDF', 'from pyflink.table import ScalarFunction, DataTypes\nfrom pyflink.table.udf import udf\n\nclass ${className}(ScalarFunction):\n    def __init__(self):\n        pass\n\n    def eval(self, variable):\n        return str(variable)\n\n\n${attr!\'f\'} = udf(HashCode(), result_type=DataTypes.STRING())', NULL, '2022-10-25 09:23:07', '2022-10-25 09:34:01');

INSERT INTO `dlink_udf_template` (`id`, `name`, `code_type`, `function_type`, `template_code`, `enabled`, `create_time`, `update_time`)
VALUES (5, 'python_udf_2', 'Python', 'UDF', 'from pyflink.table import DataTypes\nfrom pyflink.table.udf import udf\n\n@udf(result_type=DataTypes.STRING())\ndef ${className}(variable1:string):\n  return \'\'', NULL, '2022-10-25 09:25:13', '2022-10-25 09:34:47');


UPDATE `dlink_database` set `group_name` = 'source' WHERE `group_name` = '来源';
UPDATE `dlink_database` set `group_name` = 'warehouse' WHERE `group_name` = '数仓';
UPDATE `dlink_database` set `group_name` = 'application' WHERE `group_name` = '应用';
UPDATE `dlink_database` set `group_name` = 'backup' WHERE `group_name` = '备份';
UPDATE `dlink_database` set `group_name` = 'other' WHERE `group_name` = '其他';

