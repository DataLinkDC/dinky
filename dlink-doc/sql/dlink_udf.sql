SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `dlink_udf`;
CREATE TABLE `dlink_udf` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `name` varchar(50) DEFAULT NULL COMMENT 'udf名',
                             `class_name` varchar(50) DEFAULT NULL COMMENT '完整的类名',
                             `source_code` text COMMENT '源码',
                             `compiler_code` binary(255) DEFAULT NULL COMMENT '编译产物',
                             `version_id` int(11) DEFAULT NULL COMMENT '版本',
                             `version_description` varchar(50) DEFAULT NULL COMMENT '版本描述',
                             `is_default` tinyint(1) DEFAULT NULL COMMENT '是否默认',
                             `document_id` int(11) DEFAULT NULL COMMENT '对应文档id',
                             `from_version_id` int(11) DEFAULT NULL COMMENT '基于udf版本id',
                             `code_md5` varchar(50) DEFAULT NULL COMMENT '源码',
                             `dialect` varchar(50) DEFAULT NULL COMMENT '方言',
                             `type` varchar(50) DEFAULT NULL COMMENT '类型',
                             `step` int(255) DEFAULT NULL COMMENT '作业生命周期',
                             `enable` tinyint(1) DEFAULT NULL COMMENT '是否启用',
                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for dlink_udf_template
-- ----------------------------
DROP TABLE IF EXISTS `dlink_udf_template`;
CREATE TABLE `dlink_udf_template` (
                                      `id` int(11) NOT NULL AUTO_INCREMENT,
                                      `name` varchar(20) DEFAULT NULL COMMENT '模板名称',
                                      `code_type` varchar(10) DEFAULT NULL COMMENT '代码类型',
                                      `function_type` varchar(10) DEFAULT NULL COMMENT '函数类型',
                                      `template_code` text COMMENT '模板代码',
                                      `enabled` tinyint(1) DEFAULT NULL,
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of dlink_udf_template
-- ----------------------------
BEGIN;
INSERT INTO `dlink_udf_template` (`id`, `name`, `code_type`, `function_type`, `template_code`, `enabled`, `create_time`, `update_time`) VALUES (1, 'java_udf', 'java', 'UDF', 'package ${package};\n\nimport org.apache.flink.table.functions.ScalarFunction;\n\npublic class ${className} extends ScalarFunction {\n    public String eval(String s) {\n        return null;\n    }\n}', NULL, '2022-10-19 09:17:37', '2022-10-19 09:17:37');
INSERT INTO `dlink_udf_template` (`id`, `name`, `code_type`, `function_type`, `template_code`, `enabled`, `create_time`, `update_time`) VALUES (2, 'java_udtf', 'java', 'UDTF', 'package ${package};\n\nimport org.apache.flink.table.functions.ScalarFunction;\n\n@FunctionHint(output = @DataTypeHint(\"ROW<word STRING, length INT>\"))\npublic static class ${className} extends TableFunction<Row> {\n\n  public void eval(String str) {\n    for (String s : str.split(\" \")) {\n      // use collect(...) to emit a row\n      collect(Row.of(s, s.length()));\n    }\n  }\n}', NULL, '2022-10-19 09:22:58', '2022-10-19 10:01:57');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;