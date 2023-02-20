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

-- ----------------------------
-- Table structure for dlink_alert_group
-- ----------------------------
DROP TABLE IF EXISTS `dlink_alert_group`;
CREATE TABLE `dlink_alert_group` (
                                     `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                     `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'alert group name',
                                     `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                     `alert_instance_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Alert instance IDS',
                                     `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
                                     `enabled` tinyint DEFAULT '1' COMMENT 'is enable',
                                     `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                     `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE KEY `dlink_alert_instance_un` (`name`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Alert group';

-- ----------------------------
-- Table structure for dlink_alert_history
-- ----------------------------
DROP TABLE IF EXISTS `dlink_alert_history`;
CREATE TABLE `dlink_alert_history` (
                                       `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                       `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                       `alert_group_id` int DEFAULT NULL COMMENT 'Alert group ID',
                                       `job_instance_id` int DEFAULT NULL COMMENT 'job instance ID',
                                       `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'alert title',
                                       `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'content description',
                                       `status` int DEFAULT NULL COMMENT 'alert status',
                                       `log` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'log',
                                       `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                       `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Alert history';

-- ----------------------------
-- Table structure for dlink_alert_instance
-- ----------------------------
DROP TABLE IF EXISTS `dlink_alert_instance`;
CREATE TABLE `dlink_alert_instance` (
                                        `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                        `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'alert instance name',
                                        `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                        `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'alert instance type such as: DingTalk,Wechat(Webhook,app) Feishu ,email',
                                        `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'configuration',
                                        `enabled` tinyint DEFAULT '1' COMMENT 'is enable',
                                        `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                        `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE KEY `dlink_alert_instance_un` (`name`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Alert instance';

-- ----------------------------
-- Table structure for dlink_catalogue
-- ----------------------------
DROP TABLE IF EXISTS `dlink_catalogue`;
CREATE TABLE `dlink_catalogue` (
                                   `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                   `task_id` int DEFAULT NULL COMMENT 'Job ID',
                                   `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Job Name',
                                   `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Job Type',
                                   `parent_id` int NOT NULL DEFAULT '0' COMMENT 'parent ID',
                                   `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is enable',
                                   `is_leaf` tinyint(1) NOT NULL COMMENT 'is leaf node',
                                   `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                   `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE KEY `dlink_catalogue_un` (`name`,`parent_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='catalogue';

-- ----------------------------
-- Table structure for dlink_cluster
-- ----------------------------
DROP TABLE IF EXISTS `dlink_cluster`;
CREATE TABLE `dlink_cluster` (
                                 `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                 `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                 `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cluster instance name',
                                 `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'cluster instance alias',
                                 `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'cluster types',
                                 `hosts` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'cluster hosts',
                                 `job_manager_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Job Manager Host',
                                 `version` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'version',
                                 `status` int DEFAULT NULL COMMENT 'cluster status',
                                 `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
                                 `auto_registers` tinyint(1) DEFAULT '0' COMMENT 'is auto registration',
                                 `cluster_configuration_id` int DEFAULT NULL COMMENT 'cluster configuration id',
                                 `task_id` int DEFAULT NULL COMMENT 'task ID',
                                 `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is enable',
                                 `application_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Resource Manger Address',
                                 `resource_manager_addr` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Application Id',
                                 `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                 `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE KEY `dlink_cluster_un` (`name`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='cluster instance management';

-- ----------------------------
-- Table structure for dlink_cluster_configuration
-- ----------------------------
DROP TABLE IF EXISTS `dlink_cluster_configuration`;
CREATE TABLE `dlink_cluster_configuration` (
                                               `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                               `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                               `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cluster configuration name',
                                               `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'cluster configuration alias',
                                               `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'cluster type',
                                               `config_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'json of configuration',
                                               `is_available` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'is available',
                                               `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
                                               `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is enable',
                                               `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                               `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                               PRIMARY KEY (`id`),
                                               UNIQUE KEY `dlink_cluster_configuration_un` (`name`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='cluster configuration management';

-- ----------------------------
-- Table structure for dlink_database
-- ----------------------------
DROP TABLE IF EXISTS `dlink_database`;
CREATE TABLE `dlink_database` (
                                  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                  `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'database name',
                                  `alias` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'database alias',
                                  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'Default' COMMENT 'database belong group name',
                                  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'database type',
                                  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'database ip',
                                  `port` int DEFAULT NULL COMMENT 'database port',
                                  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'database url',
                                  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'username',
                                  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'password',
                                  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
                                  `flink_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Flink configuration',
                                  `flink_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Flink template',
                                  `db_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'version，such as: 11g of oracle ，2.2.3 of hbase',
                                  `status` tinyint(1) DEFAULT NULL COMMENT 'heartbeat status',
                                  `health_time` datetime DEFAULT NULL COMMENT 'last heartbeat time of trigger',
                                  `heartbeat_time` datetime DEFAULT NULL COMMENT 'last heartbeat time',
                                  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is enable',
                                  `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                  `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE KEY `dlink_database_un` (`name`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='database management';

-- ----------------------------
-- Table structure for dlink_flink_document
-- ----------------------------
DROP TABLE IF EXISTS `dlink_flink_document`;
CREATE TABLE `dlink_flink_document` (
                                        `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                        `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'document category',
                                        `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'document type',
                                        `subtype` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'document subtype',
                                        `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'document name',
                                        `description` longtext COLLATE utf8mb4_general_ci,
                                        `fill_value` longtext COLLATE utf8mb4_general_ci COMMENT 'fill value',
                                        `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'document version such as:(flink1.12,flink1.13,flink1.14,flink1.15)',
                                        `like_num` int DEFAULT '0' COMMENT 'like number',
                                        `enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'is enable',
                                        `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                        `update_time` datetime DEFAULT NULL COMMENT 'update_time',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='flink document management';
-- ----------------------------
-- Records of dlink_flink_document
-- ----------------------------
INSERT INTO `dlink_flink_document` VALUES (1, 'Variable', '优化参数', 'Batch/Streaming', 'set table.exec.async-lookup.buffer-capacity', '异步查找连接可以触发的最大异步操作的操作数。 \nThe max number of async i/o operation that the async lookup join can trigger.', 'Set \'table.exec.async-lookup.buffer-capacity\'=\'100\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (2, 'Variable', '优化参数', 'Batch/Streaming', 'set table.exec.async-lookup.timeout', '异步操作完成的超时时间。 \nThe async timeout for the asynchronous operation to complete.', 'Set \'table.exec.async-lookup.timeout\'=\'3 min\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (3, 'Variable', '优化参数', 'Batch', 'set table.exec.disabled-operators', '禁用指定operators，用逗号分隔 \nMainly for testing. A comma-separated list of operator names, each name represents a kind of disabled operator. Operators that can be disabled include \"NestedLoopJoin\", \"ShuffleHashJoin\", \"BroadcastHashJoin\", \"SortMergeJoin\", \"HashAgg\", \"SortAgg\". By default no operator is disabled.', 'Set \'table.exec.disabled-operators\'=\'SortMergeJoin\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (4, 'Variable', '优化参数', 'Streaming', 'set table.exec.mini-batch.allow-latency', '最大等待时间可用于MiniBatch缓冲输入记录。 MiniBatch是用于缓冲输入记录以减少状态访问的优化。MiniBatch以允许的等待时间间隔以及达到最大缓冲记录数触发。注意：如果将table.exec.mini-batch.enabled设置为true，则其值必须大于零.', 'Set \'table.exec.mini-batch.allow-latency\'=\'-1 ms\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (5, 'Variable', '优化参数', 'Streaming', 'set table.exec.mini-batch.enabled', '指定是否启用MiniBatch优化。 MiniBatch是用于缓冲输入记录以减少状态访问的优化。默认情况下禁用此功能。 要启用此功能，用户应将此配置设置为true。注意：如果启用了mini batch 处理，则必须设置“ table.exec.mini-batch.allow-latency”和“ table.exec.mini-batch.size”.', 'Set \'table.exec.mini-batch.enabled\'=\'false\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (6, 'Variable', '优化参数', 'Streaming', 'set table.exec.mini-batch.size', '可以为MiniBatch缓冲最大输入记录数。 MiniBatch是用于缓冲输入记录以减少状态访问的优化。MiniBatch以允许的等待时间间隔以及达到最大缓冲记录数触发。 注意：MiniBatch当前仅适用于非窗口聚合。如果将table.exec.mini-batch.enabled设置为true，则其值必须为正.', 'Set \'table.exec.mini-batch.size\'=\'-1\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (7, 'Variable', '优化参数', 'Batch/Streaming', 'set table.exec.resource.default-parallelism', '设置所有Operator的默认并行度。 \nSets default parallelism for all operators (such as aggregate, join, filter) to run with parallel instances. This config has a higher priority than parallelism of StreamExecutionEnvironment (actually, this config overrides the parallelism of StreamExecutionEnvironment). A value of -1 indicates that no default parallelism is set, then it will fallback to use the parallelism of StreamExecutionEnvironment.', 'Set \'table.exec.resource.default-parallelism\'=\'1\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (8, 'Variable', '优化参数', 'Batch/Streaming', 'set table.exec.sink.not-null-enforcer', '对表的NOT NULL列约束强制执行不能将空值插入到表中。Flink支持“error”（默认）和“drop”强制行为 \nThe NOT NULL column constraint on a table enforces that null values can\'t be inserted into the table. Flink supports \'error\' (default) and \'drop\' enforcement behavior. By default, Flink will check values and throw runtime exception when null values writing into NOT NULL columns. Users can change the behavior to \'drop\' to silently drop such records without throwing exception.\nPossible values:\n\"ERROR\" \n\"DROP\"', 'Set \'table.exec.sink.not-null-enforcer\'=\'ERROR\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (9, 'Variable', '优化参数', 'Streaming', 'set table.exec.sink.upsert-materialize', '由于分布式系统中 Shuffle 导致 ChangeLog 数据混乱，Sink 接收到的数据可能不是全局 upsert 的顺序。因此，在 upsert sink 之前添加 upsert materialize 运算符。它接收上游的变更日志记录并为下游生成一个 upsert 视图。默认情况下，当唯一键出现分布式无序时，会添加具体化操作符。您也可以选择不实现（NONE）或强制实现（FORCE）。\nPossible values:\n\"NONE\" \n\"FORCE\" \n\"AUTO\"', 'Set \'table.exec.sink.upsert-materialize\'=\'AUTO\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (10, 'Module', '建表语句', 'Other', 'create.table.kafka', 'kafka快速建表格式', 'CREATE TABLE Kafka_Table (\n  `event_time` TIMESTAMP(3) METADATA FROM \'timestamp\',\n  `partition` BIGINT METADATA VIRTUAL,\n  `offset` BIGINT METADATA VIRTUAL,\n  `user_id` BIGINT,\n  `item_id` BIGINT,\n  `behavior` STRING\n) WITH (\n  \'connector\' = \'kafka\',\n  \'topic\' = \'user_behavior\',\n  \'properties.bootstrap.servers\' = \'localhost:9092\',\n  \'properties.group.id\' = \'testGroup\',\n  \'scan.startup.mode\' = \'earliest-offset\',\n  \'format\' = \'csv\'\n);\n--可选: \'value.fields-include\' = \'ALL\',\n--可选: \'json.ignore-parse-errors\' = \'true\',\n--可选: \'key.fields-prefix\' = \'k_\',', '1.14', 0, 1, '2022-01-20 16:59:18', '2022-01-20 17:57:32');
INSERT INTO `dlink_flink_document` VALUES (11, 'Module', '建表语句', 'Other', 'create.table.doris', 'Doris快速建表', 'CREATE TABLE doris_table (\n    cid INT,\n    sid INT,\n    name STRING,\n    cls STRING,\n    score INT,\n    PRIMARY KEY (cid) NOT ENFORCED\n) WITH (       \n\'connector\' = \'doris\',\n\'fenodes\' = \'127.0.0.1:8030\' ,\n\'table.identifier\' = \'test.scoreinfo\',\n\'username\' = \'root\',\n\'password\'=\'\'\n);', '1.14', 0, 1, '2022-01-20 17:08:00', '2022-01-20 17:57:26');
INSERT INTO `dlink_flink_document` VALUES (12, 'Module', '建表语句', 'Other', 'create.table.jdbc', 'JDBC建表语句', 'CREATE TABLE JDBC_table (\n  id BIGINT,\n  name STRING,\n  age INT,\n  status BOOLEAN,\n  PRIMARY KEY (id) NOT ENFORCED\n) WITH (\n   \'connector\' = \'jdbc\',\n   \'url\' = \'jdbc:mysql://localhost:3306/mydatabase\',\n   \'table-name\' = \'users\',\n   \'username\' = \'root\',\n   \'password\' = \'123456\'\n);\n--可选: \'sink.parallelism\'=\'1\',\n--可选: \'lookup.cache.ttl\'=\'1000s\',', '1.14', 0, 1, '2022-01-20 17:15:26', '2022-01-20 17:57:20');
INSERT INTO `dlink_flink_document` VALUES (13, 'Module', 'CataLog', 'Other', 'create.catalog.hive', '创建HIVE的catalog', 'CREATE CATALOG hive WITH ( \n    \'type\' = \'hive\',\n    \'default-database\' = \'default\',\n    \'hive-conf-dir\' = \'/app/wwwroot/MBDC/hive/conf/\', --hive配置文件\n    \'hadoop-conf-dir\'=\'/app/wwwroot/MBDC/hadoop/etc/hadoop/\' --hadoop配置文件，配了环境变量则不需要。\n);', '1.14', 0, 1, '2022-01-20 17:18:54', '2022-01-20 17:18:54');
INSERT INTO `dlink_flink_document` VALUES (14, 'Operator', 'CataLog', 'Other', 'use.catalog.hive', '使用hive的catalog', 'USE CATALOG hive;', '1.14', 0, 1, '2022-01-20 17:22:53', '2022-01-20 17:22:53');
INSERT INTO `dlink_flink_document` VALUES (15, 'Operator', 'CataLog', 'Other', 'use.catalog.default', '使用default的catalog', 'USE CATALOG default_catalog;', '1.14', 0, 1, '2022-01-20 17:23:48', '2022-01-20 17:24:23');
INSERT INTO `dlink_flink_document` VALUES (16, 'Variable', '设置参数', 'Other', 'set dialect.hive', '使用hive方言', 'Set table.sql-dialect=hive;', '1.14', 0, 1, '2022-01-20 17:25:37', '2022-01-20 17:27:23');
INSERT INTO `dlink_flink_document` VALUES (17, 'Variable', '设置参数', 'Other', 'set dialect.default', '使用default方言', 'Set table.sql-dialect=default;', '1.14', 0, 1, '2022-01-20 17:26:19', '2022-01-20 17:27:20');
INSERT INTO `dlink_flink_document` VALUES (18, 'Module', '建表语句', 'Other', 'create.stream.table.hive', '创建流式HIVE表', 'CREATE CATALOG hive WITH ( --创建hive的catalog\n    \'type\' = \'hive\',\n    \'hive-conf-dir\' = \'/app/wwwroot/MBDC/hive/conf/\',\n    \'hadoop-conf-dir\'=\'/app/wwwroot/MBDC/hadoop/etc/hadoop/\'\n);\n\nUSE CATALOG hive; \nUSE offline_db; --选择库\nset table.sql-dialect=hive; --设置方言\n\nCREATE TABLE hive_stream_table (\n  user_id STRING,\n  order_amount DOUBLE\n) PARTITIONED BY (dt STRING, hr STRING) STORED AS parquet TBLPROPERTIES (\n  \'partition.time-extractor.timestamp-pattern\'=\'$dt $hr:00:00\',\n  \'sink.partition-commit.trigger\'=\'partition-time\',\n  \'sink.partition-commit.delay\'=\'1min\',\n  \'sink.semantic\' = \'exactly-once\',\n  \'sink.rolling-policy.rollover-interval\' =\'1min\',\n  \'sink.rolling-policy.check-interval\'=\'1min\',\n  \'sink.partition-commit.policy.kind\'=\'metastore,success-file\'\n);', '1.14', 0, 1, '2022-01-20 17:34:06', '2022-01-20 17:46:41');
INSERT INTO `dlink_flink_document` VALUES (19, 'Module', '建表语句', 'Other', 'create.table.mysql_cdc', '创建Mysql_CDC表', 'CREATE TABLE mysql_cdc_table(\n    cid INT,\n    sid INT,\n    cls STRING,\n    score INT,\n    PRIMARY KEY (cid) NOT ENFORCED\n) WITH (\n\'connector\' = \'mysql-cdc\',\n\'hostname\' = \'127.0.0.1\',\n\'port\' = \'3306\',\n\'username\' = \'test\',\n\'password\' = \'123456\',\n\'database-name\' = \'test\',\n\'server-time-zone\' = \'UTC\',\n\'scan.incremental.snapshot.enabled\' = \'true\',\n\'debezium.snapshot.mode\'=\'latest-offset\' ,-- 或者key是scan.startup.mode，initial表示要历史数据，latest-offset表示不要历史数据\n\'debezium.datetime.format.date\'=\'yyyy-MM-dd\',\n\'debezium.datetime.format.time\'=\'HH-mm-ss\',\n\'debezium.datetime.format.datetime\'=\'yyyy-MM-dd HH-mm-ss\',\n\'debezium.datetime.format.timestamp\'=\'yyyy-MM-dd HH-mm-ss\',\n\'debezium.datetime.format.timestamp.zone\'=\'UTC+8\',\n\'table-name\' = \'mysql_cdc_table\');', '1.14', 0, 1, '2022-01-20 17:49:14', '2022-01-20 17:52:20');
INSERT INTO `dlink_flink_document` VALUES (20, 'Module', '建表语句', 'Other', 'create.table.hudi', '创建hudi表', 'CREATE TABLE hudi_table\n(\n    `goods_order_id`  bigint COMMENT \'自增主键id\',\n    `goods_order_uid` string COMMENT \'订单uid\',\n    `customer_uid`    string COMMENT \'客户uid\',\n    `customer_name`   string COMMENT \'客户name\',\n    `create_time`     timestamp(3) COMMENT \'创建时间\',\n    `update_time`     timestamp(3) COMMENT \'更新时间\',\n    `create_by`       string COMMENT \'创建人uid（唯一标识）\',\n    `update_by`       string COMMENT \'更新人uid（唯一标识）\',\n    PRIMARY KEY (goods_order_id) NOT ENFORCED\n) COMMENT \'hudi_table\'\nWITH (\n\'connector\' = \'hudi\',\n\'path\' = \'hdfs://cluster1/data/bizdata/cdc/mysql/order/goods_order\', -- 路径会自动创建\n\'hoodie.datasource.write.recordkey.field\' = \'goods_order_id\', -- 主键\n\'write.precombine.field\' = \'update_time\', -- 相同的键值时，取此字段最大值，默认ts字段\n\'read.streaming.skip_compaction\' = \'true\', -- 避免重复消费问题\n\'write.bucket_assign.tasks\' = \'2\', -- 并发写的 bucekt 数\n\'write.tasks\' = \'2\',\n\'compaction.tasks\' = \'1\',\n\'write.operation\' = \'upsert\', -- UPSERT（插入更新）\\INSERT（插入）\\BULK_INSERT（批插入）（upsert性能会低些，不适合埋点上报）\n\'write.rate.limit\' = \'20000\', -- 限制每秒多少条\n\'table.type\' = \'COPY_ON_WRITE\', -- 默认COPY_ON_WRITE ，\n\'compaction.async.enabled\' = \'true\', -- 在线压缩\n\'compaction.trigger.strategy\' = \'num_or_time\', -- 按次数压缩\n\'compaction.delta_commits\' = \'20\', -- 默认为5\n\'compaction.delta_seconds\' = \'60\', -- 默认为1小时\n\'hive_sync.enable\' = \'true\', -- 启用hive同步\n\'hive_sync.mode\' = \'hms\', -- 启用hive hms同步，默认jdbc\n\'hive_sync.metastore.uris\' = \'thrift://cdh2.vision.com:9083\', -- required, metastore的端口\n\'hive_sync.jdbc_url\' = \'jdbc:hive2://cdh1.vision.com:10000\', -- required, hiveServer地址\n\'hive_sync.table\' = \'order_mysql_goods_order\', -- required, hive 新建的表名 会自动同步hudi的表结构和数据到hive\n\'hive_sync.db\' = \'cdc_ods\', -- required, hive 新建的数据库名\n\'hive_sync.username\' = \'hive\', -- required, HMS 用户名\n\'hive_sync.password\' = \'123456\', -- required, HMS 密码\n\'hive_sync.skip_ro_suffix\' = \'true\' -- 去除ro后缀\n);', '1.14', 0, 1, '2022-01-20 17:56:50', '2022-01-20 17:56:50');
INSERT INTO `dlink_flink_document` VALUES (21, 'Function', '内置函数', '比较函数', 'value1 <> value2', '如果value1不等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} <> ${2:}', '1.12', 4, 1, '2021-02-22 10:05:38', '2021-03-11 09:58:48');
INSERT INTO `dlink_flink_document` VALUES (22, 'Function', '内置函数', '比较函数', 'value1 > value2', '如果value1大于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} > ${2:}', '1.12', 2, 1, '2021-02-22 14:37:58', '2021-03-10 11:58:06');
INSERT INTO `dlink_flink_document` VALUES (23, 'Function', '内置函数', '比较函数', 'value1 >= value2', '如果value1大于或等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} >= ${2:}', '1.12', 2, 1, '2021-02-22 14:38:52', '2022-03-29 19:05:54');
INSERT INTO `dlink_flink_document` VALUES (24, 'Function', '内置函数', '比较函数', 'value1 < value2', '如果value1小于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} < ${2:}', '1.12', 0, 1, '2021-02-22 14:39:15', '2022-03-29 19:04:58');
INSERT INTO `dlink_flink_document` VALUES (25, 'Function', '内置函数', '比较函数', 'value1 <= value2', '如果value1小于或等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} <=   ${2:}', '1.12', 0, 1, '2021-02-22 14:39:40', '2022-03-29 19:05:17');
INSERT INTO `dlink_flink_document` VALUES (26, 'Function', '内置函数', '比较函数', 'value IS NULL', '如果value为NULL，则返回TRUE 。', '${1:} IS NULL', '1.12', 2, 1, '2021-02-22 14:40:39', '2021-03-10 11:57:51');
INSERT INTO `dlink_flink_document` VALUES (27, 'Function', '内置函数', '比较函数', 'value IS NOT NULL', '如果value不为NULL，则返回TRUE 。', '${1:}  IS NOT NULL', '1.12', 0, 1, '2021-02-22 14:41:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (28, 'Function', '内置函数', '比较函数', 'value1 IS DISTINCT FROM value2', '如果两个值不相等则返回TRUE。NULL值在这里被视为相同的值。', '${1:} IS DISTINCT FROM ${2:}', '1.12', 0, 1, '2021-02-22 14:42:39', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (29, 'Function', '内置函数', '比较函数', 'value1 IS NOT DISTINCT FROM value2', '如果两个值相等则返回TRUE。NULL值在这里被视为相同的值。', '${1:} IS NOT DISTINCT FROM ${2:}', '1.12', 0, 1, '2021-02-22 14:43:23', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (30, 'Function', '内置函数', '比较函数', 'value1 BETWEEN [ ASYMMETRIC | SYMMETRIC ] value2 AND value3', '如果value1大于或等于value2和小于或等于value3 返回true', '${1:} BETWEEN ${2:} AND ${3:}', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (31, 'Function', '内置函数', '比较函数', 'value1 NOT BETWEEN [ ASYMMETRIC | SYMMETRIC ] value2 AND value3', '如果value1小于value2或大于value3 返回true', '${1:} NOT BETWEEN ${2:} AND ${3:}', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (32, 'Function', '内置函数', '比较函数', 'string1 LIKE string2 [ ESCAPE char ]', '如果STRING1匹配模式STRING2，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。', '${1:} LIKE ${2:}', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (33, 'Function', '内置函数', '比较函数', 'string1 NOT LIKE string2 [ ESCAPE char ]', '如果STRING1不匹配模式STRING2，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。', '${1:} NOT LIKE ${2:}', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (34, 'Function', '内置函数', '比较函数', 'string1 SIMILAR TO string2 [ ESCAPE char ]', '如果STRING1与SQL正则表达式STRING2匹配，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。', '${1:} SIMILAR TO ${2:}', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-03-10 11:57:28');
INSERT INTO `dlink_flink_document` VALUES (35, 'Function', '内置函数', '比较函数', 'string1 NOT SIMILAR TO string2 [ ESCAPE char ]', '如果STRING1与SQL正则表达式STRING2不匹配，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。', '${1:} NOT SIMILAR TO ${2:}', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (36, 'Function', '内置函数', '比较函数', 'value1 IN (value2 [, value3]* )', '如果value1存在于给定列表（value2，value3，...）中，则返回TRUE 。\n\n当（value2，value3，...）包含NULL，如果可以找到该元素，则返回TRUE，否则返回UNKNOWN。\n\n如果value1为NULL，则始终返回UNKNOWN 。', '${1:} IN (${2:} )', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (37, 'Function', '内置函数', '比较函数', 'value1 NOT IN (value2 [, value3]* )', '如果value1不存在于给定列表（value2，value3，...）中，则返回TRUE 。\n\n当（value2，value3，...）包含NULL，如果可以找到该元素，则返回TRUE，否则返回UNKNOWN。\n\n如果value1为NULL，则始终返回UNKNOWN 。', '${1:} NOT IN (${2:})', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (38, 'Function', '内置函数', '比较函数', 'EXISTS (sub-query)', '如果value存在于子查询中，则返回TRUE。', 'EXISTS (${1:})', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (39, 'Function', '内置函数', '比较函数', 'value IN (sub-query)', '如果value存在于子查询中，则返回TRUE。', '${1:} IN (${2:})', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (40, 'Function', '内置函数', '比较函数', 'value NOT IN (sub-query)', '如果value不存在于子查询中，则返回TRUE。', '${1:} NOT IN (${2:})', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (41, 'Function', '内置函数', '逻辑函数', 'boolean1 OR boolean2', '如果BOOLEAN1为TRUE或BOOLEAN2为TRUE，则返回TRUE。支持三值逻辑。\n\n例如，true || Null(Types.BOOLEAN)返回TRUE。', '${1:} OR ${2:}', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (42, 'Function', '内置函数', '逻辑函数', 'boolean1 AND boolean2', '如果BOOLEAN1和BOOLEAN2均为TRUE，则返回TRUE。支持三值逻辑。\n\n例如，true && Null(Types.BOOLEAN)返回未知。', '${1:} AND ${2:}', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (43, 'Function', '内置函数', '逻辑函数', 'NOT boolean', '如果BOOLEAN为FALSE，则返回TRUE ；如果BOOLEAN为TRUE，则返回FALSE 。\n\n如果BOOLEAN为UNKNOWN，则返回UNKNOWN。', 'NOT ${1:} ', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (44, 'Function', '内置函数', '逻辑函数', 'boolean IS FALSE', '如果BOOLEAN为FALSE，则返回TRUE ；如果BOOLEAN为TRUE或UNKNOWN，则返回FALSE 。', '${1:}  IS FALSE', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (45, 'Function', '内置函数', '逻辑函数', 'boolean IS NOT FALSE', '如果BOOLEAN为TRUE或UNKNOWN，则返回TRUE ；如果BOOLEAN为FALSE，则返回FALSE。', '${1:}  IS NOT FALSE', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (46, 'Function', '内置函数', '逻辑函数', 'boolean IS TRUE', '如果BOOLEAN为TRUE，则返回TRUE；如果BOOLEAN为FALSE或UNKNOWN，则返回FALSE 。', '${1:}  IS TRUE', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (47, 'Function', '内置函数', '逻辑函数', 'boolean IS NOT TRUE', '如果BOOLEAN为FALSE或UNKNOWN，则返回TRUE ；如果BOOLEAN为TRUE，则返回FALSE 。', '${1:}  IS NOT TRUE', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (48, 'Function', '内置函数', '逻辑函数', 'boolean IS UNKNOWN', '如果BOOLEAN为UNKNOWN，则返回TRUE ；如果BOOLEAN为TRUE或FALSE，则返回FALSE 。', '${1:}  IS UNKNOWN', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (49, 'Function', '内置函数', '逻辑函数', 'boolean IS NOT UNKNOWN', '如果BOOLEAN为TRUE或FALSE，则返回TRUE ；如果BOOLEAN为UNKNOWN，则返回FALSE 。', '${1:}  IS NOT UNKNOWN', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (50, 'Function', '内置函数', '算术函数', '+ numeric', '返回NUMERIC。', '+ ${1:} ', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (51, 'Function', '内置函数', '算术函数', '- numeric', '返回负数NUMERIC。', '- ${1:} ', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (52, 'Function', '内置函数', '算术函数', 'numeric1 + numeric2', '返回NUMERIC1加NUMERIC2。', '${1:}  + ${2:} ', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (53, 'Function', '内置函数', '算术函数', 'numeric1 - numeric2', '返回NUMERIC1减去NUMERIC2。', '${1:}  - ${2:} ', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (54, 'Function', '内置函数', '算术函数', 'numeric1 * numeric2', '返回NUMERIC1乘以NUMERIC2。', '${1:} * ${2:} ', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (55, 'Function', '内置函数', '算术函数', 'numeric1 / numeric2', '返回NUMERIC1除以NUMERIC2。', '${1:}  / ${2:} ', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (56, 'Function', '内置函数', '算术函数', 'numeric1 % numeric2', '返回NUMERIC1除以NUMERIC2的余数（模）。仅当numeric1为负数时，结果为负数。', '${1:}  % ${2:} ', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (57, 'Function', '内置函数', '算术函数', 'POWER(numeric1, numeric2)', '返回NUMERIC1的NUMERIC2 次幂。', 'POWER(${1:} , ${2:})', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (58, 'Function', '内置函数', '算术函数', 'ABS(numeric)', '返回NUMERIC的绝对值。', 'ABS(${1:})', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (59, 'Function', '内置函数', '算术函数', 'MOD(numeric1, numeric2)', '返回numeric1除以numeric2的余数(模)。只有当numeric1为负数时，结果才为负数', 'MOD(${1:} , ${2:} )', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (60, 'Function', '内置函数', '算术函数', 'SQRT(numeric)', '返回NUMERIC的平方根。', 'SQRT(${1:})', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (61, 'Function', '内置函数', '算术函数', 'LN(numeric)', '返回NUMERIC的自然对数（以e为底）。', 'LN(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (62, 'Function', '内置函数', '算术函数', 'LOG10(numeric)', '返回NUMERIC的以10为底的对数。', 'LOG10(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (63, 'Function', '内置函数', '算术函数', 'LOG2(numeric)', '返回NUMERIC的以2为底的对数。', 'LOG2(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (64, 'Function', '内置函数', '算术函数', 'EXP(numeric)', '返回e 的 NUMERIC 次幂。', 'EXP(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (65, 'Function', '内置函数', '算术函数', 'FLOOR(numeric)', '向下舍入NUMERIC，并返回小于或等于NUMERIC的最大整数。', 'FLOOR(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (66, 'Function', '内置函数', '算术函数', 'SIN(numeric)', '返回NUMERIC的正弦值。', 'SIN(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (67, 'Function', '内置函数', '算术函数', 'SINH(numeric)', '返回NUMERIC的双曲正弦值。\n\n返回类型为DOUBLE。', 'SINH(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (68, 'Function', '内置函数', '算术函数', 'COS(numeric)', '返回NUMERIC的余弦值。', 'COS(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (69, 'Function', '内置函数', '算术函数', 'TAN(numeric)', '返回NUMERIC的正切。', 'TAN(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (70, 'Function', '内置函数', '算术函数', 'TANH(numeric)', '返回NUMERIC的双曲正切值。\n\n返回类型为DOUBLE。', 'TANH(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (71, 'Function', '内置函数', '算术函数', 'COT(numeric)', '返回NUMERIC的余切。', 'COT(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (72, 'Function', '内置函数', '算术函数', 'ASIN(numeric)', '返回NUMERIC的反正弦值。', 'ASIN(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (73, 'Function', '内置函数', '算术函数', 'ACOS(numeric)', '返回NUMERIC的反余弦值。', 'ACOS(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (74, 'Function', '内置函数', '算术函数', 'ATAN(numeric)', '返回NUMERIC的反正切。', 'ATAN(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (75, 'Function', '内置函数', '算术函数', 'ATAN2(numeric1, numeric2)', '返回坐标的反正切（NUMERIC1，NUMERIC2）。', 'ATAN2(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (76, 'Function', '内置函数', '算术函数', 'COSH(numeric)', '返回NUMERIC的双曲余弦值。\n\n返回值类型为DOUBLE。', 'COSH(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (77, 'Function', '内置函数', '算术函数', 'DEGREES(numeric)', '返回弧度NUMERIC的度数表示形式', 'DEGREES(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (78, 'Function', '内置函数', '算术函数', 'RADIANS(numeric)', '返回度数NUMERIC的弧度表示。', 'RADIANS(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (79, 'Function', '内置函数', '算术函数', 'SIGN(numeric)', '返回NUMERIC的符号。', 'SIGN(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (80, 'Function', '内置函数', '算术函数', 'ROUND(numeric, integer)', '返回一个数字，四舍五入为NUMERIC的INT小数位。', 'ROUND(${1:} , ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (81, 'Function', '内置函数', '算术函数', 'PI', '返回一个比任何其他值都更接近圆周率的值。', 'PI', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (82, 'Function', '内置函数', '算术函数', 'E()', '返回一个比任何其他值都更接近e的值。', 'E()', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (83, 'Function', '内置函数', '算术函数', 'RAND()', '返回介于0.0（含）和1.0（不含）之间的伪随机双精度值。', 'RAND()', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (84, 'Function', '内置函数', '算术函数', 'RAND(integer)', '返回带有初始种子INTEGER的介于0.0（含）和1.0（不含）之间的伪随机双精度值。\n\n如果两个RAND函数具有相同的初始种子，它们将返回相同的数字序列。', 'RAND(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (85, 'Function', '内置函数', '算术函数', 'RAND_INTEGER(integer)', '返回介于0（含）和INTEGER（不含）之间的伪随机整数值。', 'RAND_INTEGER(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (86, 'Function', '内置函数', '算术函数', 'RAND_INTEGER(integer1, integer2)', '返回介于0（含）和INTEGER2（不含）之间的伪随机整数值，其初始种子为INTEGER1。\n\n如果两个randInteger函数具有相同的初始种子和边界，它们将返回相同的数字序列。', 'RAND_INTEGER(${1:} , ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (87, 'Function', '内置函数', '算术函数', 'UUID()', '根据RFC 4122 type 4（伪随机生成）UUID返回UUID（通用唯一标识符）字符串\n\n（例如，“ 3d3c68f7-f608-473f-b60c-b0c44ad4cc4e”）。使用加密强度高的伪随机数生成器生成UUID。', 'UUID()', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (88, 'Function', '内置函数', '算术函数', 'BIN(integer)', '以二进制格式返回INTEGER的字符串表示形式。如果INTEGER为NULL，则返回NULL。\n\n例如，4.bin()返回“ 100”并12.bin()返回“ 1100”。', 'BIN(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (89, 'Function', '内置函数', '算术函数', 'HEX(numeric)\nHEX(string)', '以十六进制格式返回整数NUMERIC值或STRING的字符串表示形式。如果参数为NULL，则返回NULL。\n\n例如，数字20导致“ 14”，数字100导致“ 64”，字符串“ hello，world”导致“ 68656C6C6F2C776F726C64”。', 'HEX(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (90, 'Function', '内置函数', '算术函数', 'TRUNCATE(numeric1, integer2)', '返回一个小数点后被截断为integer2位的数字。', 'TRUNCATE(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (91, 'Function', '内置函数', '算术函数', 'PI()', '返回π (pi)的值。仅在blink planner中支持。', 'PI()', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (92, 'Function', '内置函数', '算术函数', 'LOG(numeric1)', '如果不带参数调用，则返回NUMERIC1的自然对数。当使用参数调用时，将NUMERIC1的对数返回到基数NUMERIC2。\n\n注意：当前，NUMERIC1必须大于0，而NUMERIC2必须大于1。', 'LOG(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (93, 'Function', '内置函数', '算术函数', 'LOG(numeric1, numeric2)', '如果不带参数调用，则返回NUMERIC1的自然对数。当使用参数调用时，将NUMERIC1的对数返回到基数NUMERIC2。\n\n注意：当前，NUMERIC1必须大于0，而NUMERIC2必须大于1。', 'LOG(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (94, 'Function', '内置函数', '算术函数', 'CEIL(numeric)', '将NUMERIC向上舍入，并返回大于或等于NUMERIC的最小整数。', 'CEIL(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (95, 'Function', '内置函数', '算术函数', 'CEILING(numeric)', '将NUMERIC向上舍入，并返回大于或等于NUMERIC的最小整数。', 'CEILING(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (96, 'Function', '内置函数', '字符串函数', 'string1 || string2', '返回string1和string2的连接。', '${1:} || ${2:}', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (97, 'Function', '内置函数', '字符串函数', 'UPPER(string)', '以大写形式返回STRING。', 'UPPER(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (98, 'Function', '内置函数', '字符串函数', 'LOWER(string)', '以小写形式返回STRING。', 'LOWER(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (99, 'Function', '内置函数', '字符串函数', 'POSITION(string1 IN string2)', '返回STRING1在STRING2中第一次出现的位置（从1开始）；\n\n如果在STRING2中找不到STRING1，则返回0 。', 'POSITION(${1:} IN ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (100, 'Function', '内置函数', '字符串函数', 'TRIM([ BOTH | LEADING | TRAILING ] string1 FROM string2)', '返回一个字符串，该字符串从STRING中删除前导和/或结尾字符。', 'TRIM(${1:} FROM ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (101, 'Function', '内置函数', '字符串函数', 'LTRIM(string)', '返回一个字符串，该字符串从STRING除去左空格。\n\n例如，\" This is a test String.\".ltrim()返回“This is a test String.”。', 'LTRIM(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (102, 'Function', '内置函数', '字符串函数', 'RTRIM(string)', '返回一个字符串，该字符串从STRING中删除正确的空格。\n\n例如，\"This is a test String. \".rtrim()返回“This is a test String.”。', 'RTRIM(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (103, 'Function', '内置函数', '字符串函数', 'REPEAT(string, integer)', '返回一个字符串，该字符串重复基本STRING INT次。\n\n例如，\"This is a test String.\".repeat(2)返回“This is a test String.This is a test String.”。', 'REPEAT(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (104, 'Function', '内置函数', '字符串函数', 'REGEXP_REPLACE(string1, string2, string3)', '返回字符串STRING1所有匹配正则表达式的子串STRING2连续被替换STRING3。\n\n例如，\"foobar\".regexpReplace(\"oo|ar\", \"\")返回“ fb”。', 'REGEXP_REPLACE(${1:} , ${2:} , ${3:} )', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (105, 'Function', '内置函数', '字符串函数', 'OVERLAY(string1 PLACING string2 FROM integer1 [ FOR integer2 ])', '从位置INT1返回一个字符串，该字符串将STRING1的INT2（默认为STRING2的长度）字符替换为STRING2', 'OVERLAY(${1:} PLACING ${2:} FROM ${3:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (106, 'Function', '内置函数', '字符串函数', 'SUBSTRING(string FROM integer1 [ FOR integer2 ])', '返回字符串STRING的子字符串，从位置INT1开始，长度为INT2（默认为结尾）。', 'SUBSTRING${1:} FROM ${2:} )', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (107, 'Function', '内置函数', '字符串函数', 'REPLACE(string1, string2, string3)', '返回一个新字符串替换其中出现的所有STRING2与STRING3（非重叠）从STRING1。', 'REPLACE(${1:} , ${2:} , ${3:} )', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (108, 'Function', '内置函数', '字符串函数', 'REGEXP_EXTRACT(string1, string2[, integer])', '从STRING1返回一个字符串，该字符串使用指定的正则表达式STRING2和正则表达式匹配组索引INTEGER1提取。', 'REGEXP_EXTRACT(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (109, 'Function', '内置函数', '字符串函数', 'INITCAP(string)', '返回一种新形式的STRING，其中每个单词的第一个字符转换为大写，其余字符转换为小写。', 'INITCAP(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (110, 'Function', '内置函数', '字符串函数', 'CONCAT(string1, string2,...)', '返回连接STRING1，STRING2，...的字符串。如果任何参数为NULL，则返回NULL。', 'CONCAT(${1:} , ${2:} , ${3:} )', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (111, 'Function', '内置函数', '字符串函数', 'CONCAT_WS(string1, string2, string3,...)', '返回一个字符串，会连接STRING2，STRING3，......与分离STRING1。', 'CONCAT_WS(${1:} , ${2:} , ${3:} )', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (112, 'Function', '内置函数', '字符串函数', 'LPAD(string1, integer, string2)', '返回一个新字符串，该字符串从STRING1的左侧填充STRING2，长度为INT个字符。', 'LPAD(${1:} , ${2:} , ${3:} )', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (113, 'Function', '内置函数', '字符串函数', 'RPAD(string1, integer, string2)', '返回一个新字符串，该字符串从STRING1右侧填充STRING2，长度为INT个字符。', 'RPAD(${1:} , ${2:} , ${3:} )', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (114, 'Function', '内置函数', '字符串函数', 'FROM_BASE64(string)', '返回来自STRING的base64解码结果；如果STRING为NULL，则返回null 。', 'FROM_BASE64(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (115, 'Function', '内置函数', '字符串函数', 'TO_BASE64(string)', '从STRING返回base64编码的结果；如果STRING为NULL，则返回NULL。', 'TO_BASE64(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (116, 'Function', '内置函数', '字符串函数', 'ASCII(string)', '返回字符串的第一个字符的数值。如果字符串为NULL，则返回NULL。仅在blink planner中支持。', 'ASCII(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (117, 'Function', '内置函数', '字符串函数', 'CHR(integer)', '返回与integer在二进制上等价的ASCII字符。如果integer大于255，我们将首先得到integer的模数除以255，并返回模数的CHR。如果integer为NULL，则返回NULL。仅在blink planner中支持。', 'CHR(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (118, 'Function', '内置函数', '字符串函数', 'DECODE(binary, string)', '使用提供的字符集(\'US-ASCII\'， \'ISO-8859-1\'， \'UTF-8\'， \'UTF-16BE\'， \'UTF-16LE\'， \'UTF-16\'之一)将第一个参数解码为字符串。如果任意一个参数为空，结果也将为空。仅在blink planner中支持。', 'DECODE(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (119, 'Function', '内置函数', '字符串函数', 'ENCODE(string1, string2)', '使用提供的string2字符集(\'US-ASCII\'， \'ISO-8859-1\'， \'UTF-8\'， \'UTF-16BE\'， \'UTF-16LE\'， \'UTF-16\'之一)将string1编码为二进制。如果任意一个参数为空，结果也将为空。仅在blink planner中支持。', 'ENCODE(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (120, 'Function', '内置函数', '字符串函数', 'INSTR(string1, string2)', '返回string2在string1中第一次出现的位置。如果任何参数为空，则返回NULL。仅在blink planner中支持。', 'INSTR(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (121, 'Function', '内置函数', '字符串函数', 'LEFT(string, integer)', '返回字符串中最左边的整数字符。如果整数为负，则返回空字符串。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。', 'LEFT(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (122, 'Function', '内置函数', '字符串函数', 'RIGHT(string, integer)', '返回字符串中最右边的整数字符。如果整数为负，则返回空字符串。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。', 'RIGHT(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (123, 'Function', '内置函数', '字符串函数', 'LOCATE(string1, string2[, integer])', '返回string1在string2中的位置整数之后第一次出现的位置。如果没有找到，返回0。如果任何参数为NULL，则返回NULL仅在blink planner中支持。', 'LOCATE(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (124, 'Function', '内置函数', '字符串函数', 'PARSE_URL(string1, string2[, string3])', '从URL返回指定的部分。string2的有效值包括\'HOST\'， \'PATH\'， \'QUERY\'， \'REF\'， \'PROTOCOL\'， \'AUTHORITY\'， \'FILE\'和\'USERINFO\'。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。', 'PARSE_URL(${1:} , ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (125, 'Function', '内置函数', '字符串函数', 'REGEXP(string1, string2)', '如果string1的任何子字符串(可能为空)与Java正则表达式string2匹配，则返回TRUE，否则返回FALSE。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。', 'REGEXP(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (126, 'Function', '内置函数', '字符串函数', 'REVERSE(string)', '返回反向字符串。如果字符串为NULL，则返回NULL仅在blink planner中支持。', 'REVERSE(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (127, 'Function', '内置函数', '字符串函数', 'SPLIT_INDEX(string1, string2, integer1)', '通过分隔符string2拆分string1，返回拆分字符串的整数(从零开始)字符串。如果整数为负，返回NULL。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。', 'SPLIT_INDEX(${1:}, ${2:} , ${3:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (128, 'Function', '内置函数', '字符串函数', 'STR_TO_MAP(string1[, string2, string3]])', '使用分隔符将string1分割成键/值对后返回一个映射。string2是pair分隔符，默认为\'，\'。string3是键值分隔符，默认为\'=\'。仅在blink planner中支持。', 'STR_TO_MAP(${1:})', '1.12', 4, 1, '2021-02-22 15:29:35', '2021-05-20 19:59:50');
INSERT INTO `dlink_flink_document` VALUES (129, 'Function', '内置函数', '字符串函数', 'SUBSTR(string[, integer1[, integer2]])', '返回一个字符串的子字符串，从位置integer1开始，长度为integer2(默认到末尾)。仅在blink planner中支持。', 'SUBSTR(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (130, 'Function', '内置函数', '字符串函数', 'CHAR_LENGTH(string)', '返回STRING中的字符数。', 'CHAR_LENGTH(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (131, 'Function', '内置函数', '字符串函数', 'CHARACTER_LENGTH(string)', '返回STRING中的字符数。', 'CHARACTER_LENGTH(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (132, 'Function', '内置函数', '时间函数', 'DATE string', '返回以“ yyyy-MM-dd”形式从STRING解析的SQL日期。', 'DATE(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (133, 'Function', '内置函数', '时间函数', 'TIME string', '返回以“ HH：mm：ss”的形式从STRING解析的SQL时间。', 'TIME(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (134, 'Function', '内置函数', '时间函数', 'TIMESTAMP string', '返回从STRING解析的SQL时间戳，格式为“ yyyy-MM-dd HH：mm：ss [.SSS]”', 'TIMESTAMP(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (135, 'Function', '内置函数', '时间函数', 'INTERVAL string range', '解析“dd hh:mm:ss”形式的区间字符串。fff表示毫秒间隔，yyyy-mm表示月间隔。间隔范围可以是天、分钟、天到小时或天到秒，以毫秒为间隔;年或年到月的间隔。', 'INTERVAL ${1:} range', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (136, 'Function', '内置函数', '时间函数', 'CURRENT_DATE', '返回UTC时区中的当前SQL日期。', 'CURRENT_DATE', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (137, 'Function', '内置函数', '时间函数', 'CURRENT_TIME', '返回UTC时区的当前SQL时间。', 'CURRENT_TIME', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (138, 'Function', '内置函数', '时间函数', 'CURRENT_TIMESTAMP', '返回UTC时区内的当前SQL时间戳。', 'CURRENT_TIMESTAMP', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (139, 'Function', '内置函数', '时间函数', 'LOCALTIME', '返回本地时区的当前SQL时间。', 'LOCALTIME', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (140, 'Function', '内置函数', '时间函数', 'LOCALTIMESTAMP', '返回本地时区的当前SQL时间戳。', 'LOCALTIMESTAMP', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (141, 'Function', '内置函数', '时间函数', 'EXTRACT(timeintervalunit FROM temporal)', '返回从时域的timeintervalunit部分提取的长值。', 'EXTRACT(${1:} FROM ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (142, 'Function', '内置函数', '时间函数', 'YEAR(date)', '返回SQL date日期的年份。等价于EXTRACT(YEAR FROM date)。', 'YEAR(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (143, 'Function', '内置函数', '时间函数', 'QUARTER(date)', '从SQL date date返回一年中的季度(1到4之间的整数)。相当于EXTRACT(从日期起四分之一)。', 'QUARTER(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (144, 'Function', '内置函数', '时间函数', 'MONTH(date)', '返回SQL date date中的某月(1到12之间的整数)。等价于EXTRACT(MONTH FROM date)。', 'MONTH(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (145, 'Function', '内置函数', '时间函数', 'WEEK(date)', '从SQL date date返回一年中的某个星期(1到53之间的整数)。相当于EXTRACT(从日期开始的星期)。', 'WEEK(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (146, 'Function', '内置函数', '时间函数', 'DAYOFYEAR(date)', '返回SQL date date中的某一天(1到366之间的整数)。相当于EXTRACT(DOY FROM date)。', 'DAYOFYEAR(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (147, 'Function', '内置函数', '时间函数', 'DAYOFMONTH(date)', '从SQL date date返回一个月的哪一天(1到31之间的整数)。相当于EXTRACT(DAY FROM date)。', 'DAYOFMONTH(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (148, 'Function', '内置函数', '时间函数', 'DAYOFWEEK(date)', '返回星期几(1到7之间的整数;星期日= 1)从SQL日期日期。相当于提取(道指从日期)。', 'DAYOFWEEK(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (149, 'Function', '内置函数', '时间函数', 'HOUR(timestamp)', '从SQL timestamp timestamp返回一天中的小时(0到23之间的整数)。相当于EXTRACT(HOUR FROM timestamp)。', 'HOUR(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (150, 'Function', '内置函数', '时间函数', 'MINUTE(timestamp)', '从SQL timestamp timestamp返回一小时的分钟(0到59之间的整数)。相当于EXTRACT(分钟从时间戳)。', 'MINUTE(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (151, 'Function', '内置函数', '时间函数', 'SECOND(timestamp)', '从SQL时间戳返回一分钟中的秒(0到59之间的整数)。等价于EXTRACT(从时间戳开始倒数第二)。', 'SECOND(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (152, 'Function', '内置函数', '时间函数', 'FLOOR(timepoint TO timeintervalunit)', '返回一个将timepoint舍入到时间单位timeintervalunit的值。', 'FLOOR(${1:} TO ${2:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (153, 'Function', '内置函数', '时间函数', 'CEIL(timepoint TO timeintervalunit)', '返回一个将timepoint舍入到时间单位timeintervalunit的值。', 'CEIL(${1:} TO ${2:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (154, 'Function', '内置函数', '时间函数', '(timepoint1, temporal1) OVERLAPS (timepoint2, temporal2)', '如果(timepoint1, temporal1)和(timepoint2, temporal2)定义的两个时间间隔重叠，则返回TRUE。时间值可以是时间点或时间间隔。', '(${1:} , ${1:}) OVERLAPS (${2:} , ${2:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (155, 'Function', '内置函数', '时间函数', 'DATE_FORMAT(timestamp, string)', '注意这个功能有严重的错误，现在不应该使用。请实现一个自定义的UDF，或者使用EXTRACT作为解决方案。', 'DATE_FORMAT(${1:}, \'yyyy-MM-dd\')', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (156, 'Function', '内置函数', '时间函数', 'TIMESTAMPADD(timeintervalunit, interval, timepoint)', '返回一个新的时间值，该值将一个(带符号的)整数间隔添加到时间点。间隔的单位由unit参数给出，它应该是以下值之一:秒、分、小时、日、周、月、季度或年。', 'TIMESTAMPADD(${1:} , ${2:} , ${3:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (157, 'Function', '内置函数', '时间函数', 'TIMESTAMPDIFF(timepointunit, timepoint1, timepoint2)', '返回timepointunit在timepoint1和timepoint2之间的(带符号)数。间隔的单位由第一个参数给出，它应该是以下值之一:秒、分、小时、日、月或年。', 'TIMESTAMPDIFF(${1:} , ${2:} , ${3:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (158, 'Function', '内置函数', '时间函数', 'CONVERT_TZ(string1, string2, string3)', '将时区string2中的datetime string1(默认ISO时间戳格式\'yyyy-MM-dd HH:mm:ss\')转换为时区string3。时区的格式可以是缩写，如“PST”;可以是全名，如“America/Los_Angeles”;或者是自定义ID，如“GMT-8:00”。仅在blink planner中支持。', 'CONVERT_TZ(${1:} , ${2:} , ${3:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (159, 'Function', '内置函数', '时间函数', 'FROM_UNIXTIME(numeric[, string])', '以字符串格式返回数值参数的表示形式(默认为\'yyyy-MM-dd HH:mm:ss\')。numeric是一个内部时间戳值，表示从UTC \'1970-01-01 00:00:00\'开始的秒数，例如UNIX_TIMESTAMP()函数生成的时间戳。返回值用会话时区表示(在TableConfig中指定)。仅在blink planner中支持。', 'FROM_UNIXTIME(${1:} )', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (160, 'Function', '内置函数', '时间函数', 'UNIX_TIMESTAMP()', '获取当前Unix时间戳(以秒为单位)。仅在blink planner中支持。', 'UNIX_TIMESTAMP()', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (161, 'Function', '内置函数', '时间函数', 'UNIX_TIMESTAMP(string1[, string2])', '转换日期时间字符串string1，格式为string2(缺省为yyyy-MM-dd HH:mm:ss，如果没有指定)为Unix时间戳(以秒为单位)，使用表配置中指定的时区。仅在blink planner中支持。', 'UNIX_TIMESTAMP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (162, 'Function', '内置函数', '时间函数', 'TO_DATE(string1[, string2])', '将格式为string2的日期字符串string1(默认为\'yyyy-MM-dd\')转换为日期。仅在blink planner中支持。', 'TO_DATE(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (163, 'Function', '内置函数', '时间函数', 'TO_TIMESTAMP(string1[, string2])', '将会话时区(由TableConfig指定)下的日期时间字符串string1转换为时间戳，格式为string2(默认为\'yyyy-MM-dd HH:mm:ss\')。仅在blink planner中支持。', 'TO_TIMESTAMP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (164, 'Function', '内置函数', '时间函数', 'NOW()', '返回UTC时区内的当前SQL时间戳。仅在blink planner中支持。', 'NOW()', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (165, 'Function', '内置函数', '条件函数', 'CASE value\nWHEN value1_1 [, value1_2 ]* THEN result1\n[ WHEN value2_1 [, value2_2 ]* THEN result2 ]*\n[ ELSE resultZ ]\nEND', '当第一个时间值包含在(valueX_1, valueX_2，…)中时，返回resultX。如果没有匹配的值，则返回resultZ，否则返回NULL。', 'CASE ${1:}\n  WHEN ${2:}  THEN ${3:}\n ELSE ${4:}\nEND AS ${5:}', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (166, 'Function', '内置函数', '条件函数', 'CASE\nWHEN condition1 THEN result1\n[ WHEN condition2 THEN result2 ]*\n[ ELSE resultZ ]\nEND', '当第一个条件满足时返回resultX。当不满足任何条件时，如果提供了resultZ则返回resultZ，否则返回NULL。', 'CASE WHEN ${1:} THEN ${2:}\n   ELSE ${3:}\nEND AS ${4:}', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (167, 'Function', '内置函数', '条件函数', 'NULLIF(value1, value2)', '如果value1等于value2，则返回NULL;否则返回value1。', 'NULLIF(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (168, 'Function', '内置函数', '条件函数', 'COALESCE(value1, value2 [, value3 ]* )', '返回value1, value2， ....中的第一个非空值', 'COALESCE(${1:} )', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (169, 'Function', '内置函数', '条件函数', 'IF(condition, true_value, false_value)', '如果条件满足则返回true值，否则返回false值。仅在blink planner中支持。', 'IF((${1:}, ${2:}, ${3:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (170, 'Function', '内置函数', '条件函数', 'IS_ALPHA(string)', '如果字符串中所有字符都是字母则返回true，否则返回false。仅在blink planner中支持。', 'IS_ALPHA(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (171, 'Function', '内置函数', '条件函数', 'IS_DECIMAL(string)', '如果字符串可以被解析为有效的数字则返回true，否则返回false。仅在blink planner中支持。', 'IS_DECIMAL(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (172, 'Function', '内置函数', '条件函数', 'IS_DIGIT(string)', '如果字符串中所有字符都是数字则返回true，否则返回false。仅在blink planner中支持。', 'IS_DIGIT(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (173, 'Function', '内置函数', '类型转换函数功能', 'CAST(value AS type)', '返回一个要转换为type类型的新值。', 'CAST(${1:} AS ${2:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (174, 'Function', '内置函数', 'Collection 函数', 'CARDINALITY(array)', '返回数组中元素的数量。', 'CARDINALITY(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (175, 'Function', '内置函数', 'Collection 函数', 'array ‘[’ integer ‘]’', '返回数组中位于整数位置的元素。索引从1开始。', 'array[${1:}]', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (176, 'Function', '内置函数', 'Collection 函数', 'ELEMENT(array)', '返回数组的唯一元素(其基数应为1);如果数组为空，则返回NULL。如果数组有多个元素，则抛出异常。', 'ELEMENT(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (177, 'Function', '内置函数', 'Collection 函数', 'CARDINALITY(map)', '返回map中的条目数。', 'CARDINALITY(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (178, 'Function', '内置函数', 'Collection 函数', 'map ‘[’ value ‘]’', '返回map中key value指定的值。', 'map[${1:}]', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (179, 'Function', '内置函数', 'Value Construction函数', 'ARRAY ‘[’ value1 [, value2 ]* ‘]’', '返回一个由一系列值(value1, value2，…)创建的数组。', 'ARRAY[ ${1:} ]', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (180, 'Function', '内置函数', 'Value Construction函数', 'MAP ‘[’ value1, value2 [, value3, value4 ]* ‘]’', '返回一个从键值对列表((value1, value2)， (value3, value4)，…)创建的映射。', 'MAP[ ${1:} ]', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (181, 'Function', '内置函数', 'Value Construction函数', 'implicit constructor with parenthesis\n(value1 [, value2]*)', '返回从值列表(value1, value2，…)创建的行。', '(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (182, 'Function', '内置函数', 'Value Construction函数', 'explicit ROW constructor\nROW(value1 [, value2]*)', '返回从值列表(value1, value2，…)创建的行。', 'ROW(${1:}) ', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (183, 'Function', '内置函数', 'Value Access函数', 'tableName.compositeType.field', '按名称从Flink复合类型(例如，Tuple, POJO)中返回一个字段的值。', 'tableName.compositeType.field', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (184, 'Function', '内置函数', 'Value Access函数', 'tableName.compositeType.*', '返回Flink复合类型(例如，Tuple, POJO)的平面表示，它将每个直接子类型转换为一个单独的字段。在大多数情况下，平面表示的字段的名称与原始字段类似，但使用了$分隔符(例如，mypojo$mytuple$f0)。', 'tableName.compositeType.*', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (185, 'Function', '内置函数', '分组函数', 'GROUP_ID()', '返回唯一标识分组键组合的整数', 'GROUP_ID()', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (186, 'Function', '内置函数', '分组函数', 'GROUPING(expression1 [, expression2]* )\nGROUPING_ID(expression1 [, expression2]* )', '返回给定分组表达式的位向量。', 'GROUPING(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (187, 'Function', '内置函数', 'hash函数', 'MD5(string)', '以32位十六进制数字的字符串形式返回string的MD5哈希值;如果字符串为NULL，则返回NULL。', 'MD5(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (188, 'Function', '内置函数', 'hash函数', 'SHA1(string)', '返回字符串的SHA-1散列，作为一个由40个十六进制数字组成的字符串;如果字符串为NULL，则返回NULL', 'SHA1(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (189, 'Function', '内置函数', 'hash函数', 'SHA224(string)', '以56位十六进制数字的字符串形式返回字符串的SHA-224散列;如果字符串为NULL，则返回NULL。', 'SHA224(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (190, 'Function', '内置函数', 'hash函数', 'SHA256(string)', '以64位十六进制数字的字符串形式返回字符串的SHA-256散列;如果字符串为NULL，则返回NULL。', 'SHA256(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (191, 'Function', '内置函数', 'hash函数', 'SHA384(string)', '以96个十六进制数字的字符串形式返回string的SHA-384散列;如果字符串为NULL，则返回NULL。', 'SHA384(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (192, 'Function', '内置函数', 'hash函数', 'SHA512(string)', '以128位十六进制数字的字符串形式返回字符串的SHA-512散列;如果字符串为NULL，则返回NULL。', 'SHA512(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (193, 'Function', '内置函数', 'hash函数', 'SHA2(string, hashLength)', '使用SHA-2哈希函数族(SHA-224、SHA-256、SHA-384或SHA-512)返回哈希值。第一个参数string是要散列的字符串，第二个参数hashLength是结果的位长度(224、256、384或512)。如果string或hashLength为NULL，则返回NULL。', 'SHA2(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (194, 'Function', '内置函数', '聚合函数', 'COUNT([ ALL ] expression | DISTINCT expression1 [, expression2]*)', '默认情况下或使用ALL时，返回表达式不为空的输入行数。对每个值的唯一实例使用DISTINCT。', 'COUNT( DISTINCT ${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (195, 'Function', '内置函数', '聚合函数', 'COUNT(*)\nCOUNT(1)', '返回输入行数。', 'COUNT(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (196, 'Function', '内置函数', '聚合函数', 'AVG([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的平均值(算术平均值)。对每个值的唯一实例使用DISTINCT。', 'AVG(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (197, 'Function', '内置函数', '聚合函数', 'SUM([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回所有输入行表达式的和。对每个值的唯一实例使用DISTINCT。', 'SUM(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (198, 'Function', '内置函数', '聚合函数', 'MAX([ ALL | DISTINCT ] expression)', '默认情况下或使用关键字ALL，返回表达式在所有输入行中的最大值。对每个值的唯一实例使用DISTINCT。', 'MAX(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (199, 'Function', '内置函数', '聚合函数', 'MIN([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的最小值。对每个值的唯一实例使用DISTINCT。', 'MIN(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (200, 'Function', '内置函数', '聚合函数', 'STDDEV_POP([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的总体标准差。对每个值的唯一实例使用DISTINCT。', 'STDDEV_POP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (201, 'Function', '内置函数', '聚合函数', 'STDDEV_SAMP([ ALL | DISTINCT ] expression)', '默认情况下或使用关键字ALL时，返回表达式在所有输入行中的样本标准差。对每个值的唯一实例使用DISTINCT。', 'STDDEV_SAMP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (202, 'Function', '内置函数', '聚合函数', 'VAR_POP([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的总体方差(总体标准差的平方)。对每个值的唯一实例使用DISTINCT。', 'VAR_POP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (203, 'Function', '内置函数', '聚合函数', 'VAR_SAMP([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的样本方差(样本标准差的平方)。对每个值的唯一实例使用DISTINCT。', 'VAR_SAMP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (204, 'Function', '内置函数', '聚合函数', 'COLLECT([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，跨所有输入行返回表达式的多集。空值将被忽略。对每个值的唯一实例使用DISTINCT。', 'COLLECT(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (205, 'Function', '内置函数', '聚合函数', 'VARIANCE([ ALL | DISTINCT ] expression)', 'VAR_SAMP的同义词。仅在blink planner中支持。', 'VARIANCE(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (206, 'Function', '内置函数', '聚合函数', 'RANK()', '返回值在一组值中的秩。结果是1加上分区顺序中位于当前行之前或等于当前行的行数。这些值将在序列中产生空白。仅在blink planner中支持。', 'RANK()', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (207, 'Function', '内置函数', '聚合函数', 'DENSE_RANK()', '返回值在一组值中的秩。结果是1加上前面分配的秩值。与函数rank不同，dense_rank不会在排序序列中产生空隙。仅在blink planner中支持。', 'DENSE_RANK()', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (208, 'Function', '内置函数', '聚合函数', 'ROW_NUMBER()', '根据窗口分区中的行顺序，为每一行分配一个惟一的连续数字，从1开始。仅在blink planner中支持。', 'ROW_NUMBER()', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (209, 'Function', '内置函数', '聚合函数', 'LEAD(expression [, offset] [, default] )', '返回表达式在窗口中当前行之前的偏移行上的值。offset的默认值是1,default的默认值是NULL。仅在blink planner中支持。', 'LEAD(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (210, 'Function', '内置函数', '聚合函数', 'LAG(expression [, offset] [, default])', '返回表达式的值，该值位于窗口中当前行之后的偏移行。offset的默认值是1,default的默认值是NULL。仅在blink planner中支持。', 'LAG(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (211, 'Function', '内置函数', '聚合函数', 'FIRST_VALUE(expression)', '返回一组有序值中的第一个值。仅在blink planner中支持。', 'FIRST_VALUE(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (212, 'Function', '内置函数', '聚合函数', 'LAST_VALUE(expression)', '返回一组有序值中的最后一个值。仅在blink planner中支持。', 'LAST_VALUE(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (213, 'Function', '内置函数', '聚合函数', 'LISTAGG(expression [, separator])', '连接字符串表达式的值，并在它们之间放置分隔符值。分隔符没有添加在字符串的末尾。分隔符的默认值是\'，\'。仅在blink planner中支持。', 'LISTAGG(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (214, 'Function', '内置函数', '列函数', 'withColumns(…)', '选择的列', 'withColumns(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (215, 'Function', '内置函数', '列函数', 'withoutColumns(…)', '不选择的列', 'withoutColumns(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (216, 'Function', '内置函数', '比较函数', 'value1 = value2', '如果value1等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} =${2:}', '1.12', 9, 1, '2021-02-22 10:06:49', '2021-02-24 09:40:30');
INSERT INTO `dlink_flink_document` VALUES (217, 'Function', 'UDF', '表值聚合函数', 'TO_MAP(string1,object2[, string3])', '将非规则一维表转化为规则二维表，string1是key。string2是value。string3为非必填项，表示key的值域（维度），用英文逗号分割。', 'TO_MAP(${1:})', '1.12', 8, 1, '2021-05-20 19:59:22', '2021-05-20 20:00:54');



-- ----------------------------
-- Table structure for dlink_history
-- ----------------------------
DROP TABLE IF EXISTS `dlink_history`;
CREATE TABLE `dlink_history` (
                                 `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                 `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                 `cluster_id` int NOT NULL DEFAULT '0' COMMENT 'cluster ID',
                                 `cluster_configuration_id` int DEFAULT NULL COMMENT 'cluster configuration id',
                                 `session` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'session',
                                 `job_id` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Job ID',
                                 `job_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Job Name',
                                 `job_manager_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'JJobManager Address',
                                 `status` int NOT NULL DEFAULT '0' COMMENT 'status',
                                 `type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'job type',
                                 `statement` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'statement set',
                                 `error` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'error message',
                                 `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'result set',
                                 `config_json` json DEFAULT NULL COMMENT 'config json',
                                 `start_time` datetime DEFAULT NULL COMMENT 'job start time',
                                 `end_time` datetime DEFAULT NULL COMMENT 'job end time',
                                 `task_id` int DEFAULT NULL COMMENT 'task ID',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `task_index` (`task_id`) USING BTREE,
                                 KEY `cluster_index` (`cluster_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='execution history';

-- ----------------------------
-- Table structure for dlink_jar
-- ----------------------------
DROP TABLE IF EXISTS `dlink_jar`;
CREATE TABLE `dlink_jar` (
                             `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                             `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'jar name',
                             `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'jar alias',
                             `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'jar type',
                             `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'file path',
                             `main_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'application of main class',
                             `paras` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'main class of args',
                             `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
                             `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is enable',
                             `create_time` datetime DEFAULT NULL COMMENT 'create time',
                             `update_time` datetime DEFAULT NULL COMMENT 'update time',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `dlink_jar_un` (`tenant_id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='jar management';

-- ----------------------------
-- Table structure for dlink_job_history
-- ----------------------------
DROP TABLE IF EXISTS `dlink_job_history`;
CREATE TABLE `dlink_job_history` (
                                     `id` int NOT NULL COMMENT 'id',
                                     `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                     `job_json` json DEFAULT NULL COMMENT 'Job information json',
                                     `exceptions_json` json DEFAULT NULL COMMENT 'error message json',
                                     `checkpoints_json` json DEFAULT NULL COMMENT 'checkpoints json',
                                     `checkpoints_config_json` json DEFAULT NULL COMMENT 'checkpoints configuration json',
                                     `config_json` json DEFAULT NULL COMMENT 'configuration',
                                     `jar_json` json DEFAULT NULL COMMENT 'Jar configuration',
                                     `cluster_json` json DEFAULT NULL COMMENT 'cluster instance configuration',
                                     `cluster_configuration_json` json DEFAULT NULL COMMENT 'cluster config',
                                     `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Job history details';

-- ----------------------------
-- Table structure for dlink_job_instance
-- ----------------------------
DROP TABLE IF EXISTS `dlink_job_instance`;
CREATE TABLE `dlink_job_instance` (
                                      `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                      `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'job instance name',
                                      `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                      `task_id` int DEFAULT NULL COMMENT 'task ID',
                                      `step` int DEFAULT NULL COMMENT 'job lifecycle',
                                      `cluster_id` int DEFAULT NULL COMMENT 'cluster ID',
                                      `jid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Flink JobId',
                                      `status` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'job instance status',
                                      `history_id` int DEFAULT NULL COMMENT 'execution history ID',
                                      `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                      `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                      `finish_time` datetime DEFAULT NULL COMMENT 'finish time',
                                      `duration` bigint DEFAULT NULL COMMENT 'job duration',
                                      `error` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'error logs',
                                      `failed_restart_count` int DEFAULT NULL COMMENT 'failed restart count',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE KEY `dlink_job_instance_un` (`tenant_id`,`name`,`task_id`,`history_id`),
                                      KEY `dlink_job_instance_task_id_IDX` (`task_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='job instance';

-- ----------------------------
-- Table structure for dlink_savepoints
-- ----------------------------
DROP TABLE IF EXISTS `dlink_savepoints`;
CREATE TABLE `dlink_savepoints` (
                                    `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                    `task_id` int NOT NULL COMMENT 'task ID',
                                    `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'task name',
                                    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'savepoint type',
                                    `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'savepoint path',
                                    `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='job savepoint management';

-- ----------------------------
-- Table structure for dlink_schema_history
-- ----------------------------
DROP TABLE IF EXISTS `dlink_schema_history`;
CREATE TABLE `dlink_schema_history` (
                                        `installed_rank` int NOT NULL,
                                        `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                        `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                        `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                        `script` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                        `checksum` int DEFAULT NULL,
                                        `installed_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                        `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        `execution_time` int NOT NULL,
                                        `success` tinyint(1) NOT NULL,
                                        PRIMARY KEY (`installed_rank`) USING BTREE,
                                        KEY `dlink_schema_history_s_idx` (`success`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for dlink_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `dlink_sys_config`;
CREATE TABLE `dlink_sys_config` (
                                    `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'configuration name',
                                    `value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'configuration value',
                                    `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                    `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='system configuration';

-- ----------------------------
-- Table structure for dlink_task
-- ----------------------------
DROP TABLE IF EXISTS `dlink_task`;
CREATE TABLE `dlink_task` (
                              `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                              `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Job name',
                              `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                              `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Job alias',
                              `dialect` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'dialect',
                              `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Job type',
                              `check_point` int DEFAULT NULL COMMENT 'CheckPoint trigger seconds',
                              `save_point_strategy` int DEFAULT NULL COMMENT 'SavePoint strategy',
                              `save_point_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'SavePointPath',
                              `parallelism` int DEFAULT NULL COMMENT 'parallelism',
                              `fragment` tinyint(1) DEFAULT '0' COMMENT 'fragment',
                              `statement_set` tinyint(1) DEFAULT '0' COMMENT 'enable statement set',
                              `batch_model` tinyint(1) DEFAULT '0' COMMENT 'use batch model',
                              `cluster_id` int DEFAULT NULL COMMENT 'Flink cluster ID',
                              `cluster_configuration_id` int DEFAULT NULL COMMENT 'cluster configuration ID',
                              `database_id` int DEFAULT NULL COMMENT 'database ID',
                              `jar_id` int DEFAULT NULL COMMENT 'Jar ID',
                              `env_id` int DEFAULT NULL COMMENT 'env id',
                              `alert_group_id` bigint DEFAULT NULL COMMENT 'alert group id',
                              `config_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'configuration json',
                              `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Job Note',
                              `step` int DEFAULT NULL COMMENT 'Job lifecycle',
                              `job_instance_id` bigint DEFAULT NULL COMMENT 'job instance id',
                              `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is enable',
                              `create_time` datetime DEFAULT NULL COMMENT 'create time',
                              `update_time` datetime DEFAULT NULL COMMENT 'update time',
                              `version_id` int DEFAULT NULL COMMENT 'version id',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE KEY `dlink_task_un` (`name`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Task';

-- ----------------------------
-- Table structure for dlink_task_statement
-- ----------------------------
DROP TABLE IF EXISTS `dlink_task_statement`;
CREATE TABLE `dlink_task_statement` (
                                        `id` int NOT NULL COMMENT 'ID',
                                        `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                        `statement` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'statement set',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE KEY `dlink_task_statement_un` (`tenant_id`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='statement';

-- ----------------------------
-- Table structure for dlink_task_version
-- ----------------------------
DROP TABLE IF EXISTS `dlink_task_version`;
CREATE TABLE `dlink_task_version` (
                                      `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                      `task_id` int NOT NULL COMMENT 'task ID ',
                                      `tenant_id` int NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                      `version_id` int NOT NULL COMMENT 'version ID ',
                                      `statement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'flink sql statement',
                                      `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'version name',
                                      `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'alisa',
                                      `dialect` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'dialect',
                                      `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'type',
                                      `task_configure` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'task configuration',
                                      `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE KEY `dlink_task_version_un` (`task_id`,`tenant_id`,`version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='job history version';


-- ----------------------------
-- Table structure for dlink_upload_file_record
-- ----------------------------
DROP TABLE IF EXISTS `dlink_upload_file_record`;
CREATE TABLE `dlink_upload_file_record` (
                                            `id` tinyint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                            `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'upload file name',
                                            `enabled` tinyint(1) DEFAULT NULL COMMENT 'is enable',
                                            `file_type` tinyint DEFAULT '-1' COMMENT 'upload file type ，such as：hadoop-conf(1)、flink-conf(2)、flink-lib(3)、user-jar(4)、dlink-jar(5)，default is -1 ',
                                            `target` tinyint NOT NULL COMMENT 'upload file of target ，such as：local(1)、hdfs(2)',
                                            `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'file name',
                                            `file_parent_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'file parent path',
                                            `file_absolute_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'fila absolute path',
                                            `is_file` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is file',
                                            `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                            `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='file upload history';


-- ----------------------------
-- Table structure for dlink_user
-- ----------------------------
DROP TABLE IF EXISTS `dlink_user`;
CREATE TABLE `dlink_user` (
                              `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                              `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'username',
                              `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'password',
                              `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'nickname',
                              `worknum` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'worknum',
                              `avatar` blob COMMENT 'avatar',
                              `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'mobile phone',
                              `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is enable',
                              `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'is delete',
                              `create_time` datetime DEFAULT NULL COMMENT 'create time',
                              `update_time` datetime DEFAULT NULL COMMENT 'update time',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='user';
-- ----------------------------
-- Records of dlink_user
-- ----------------------------
INSERT INTO `dlink_user`(`id`, `username`, `password`, `nickname`, `worknum`, `avatar`, `mobile`, `enabled`, `is_delete`, `create_time`, `update_time`)
VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'Admin', NULL, NULL, NULL, 1, 0, current_time, current_time);



-- ----------------------------
-- Table structure for dlink_fragment
-- ----------------------------
DROP TABLE IF EXISTS `dlink_fragment`;
CREATE TABLE `dlink_fragment` (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                  `name` varchar(50) NOT NULL COMMENT 'fragment name',
                                  `alias` varchar(50) DEFAULT NULL COMMENT 'alias',
                                  `tenant_id` int(11) NOT NULL DEFAULT '1' COMMENT 'tenant id',
                                  `fragment_value` text NOT NULL COMMENT 'fragment value',
                                  `note` text COMMENT 'note',
                                  `enabled` tinyint(4) DEFAULT '1' COMMENT 'is enable',
                                  `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                  `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE KEY `un_idx1` (`name`,`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='fragment management';


-- ----------------------------------------------------------------------------- Metadata related data table the start -------------------------------------------------------------------------------------------

-- ----------------------------
-- Table structure for metadata_column
-- ----------------------------
DROP TABLE IF EXISTS `metadata_column`;
CREATE TABLE `metadata_column` (
                                   `column_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'column name',
                                   `column_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'column type, such as : Physical , Metadata , Computed , WATERMARK',
                                   `data_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'data type',
                                   `expr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'expression',
                                   `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'column description',
                                   `table_id` int NOT NULL COMMENT 'table id',
                                   `primary` bit(1) DEFAULT NULL COMMENT 'table primary key',
                                   `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                   PRIMARY KEY (`table_id`,`column_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='column informations';

-- ----------------------------
-- Table structure for metadata_database
-- ----------------------------
DROP TABLE IF EXISTS `metadata_database`;
CREATE TABLE `metadata_database` (
                                     `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                     `database_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'database name',
                                     `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'database description',
                                     `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='metadata of database information';

-- ----------------------------
-- Table structure for metadata_database_property
-- ----------------------------
DROP TABLE IF EXISTS `metadata_database_property`;
CREATE TABLE `metadata_database_property` (
                                              `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'key',
                                              `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'value',
                                              `database_id` int NOT NULL COMMENT 'database id',
                                              `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                              PRIMARY KEY (`key`,`database_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='metadata of database configurations';

-- ----------------------------
-- Table structure for metadata_function
-- ----------------------------
DROP TABLE IF EXISTS `metadata_function`;
CREATE TABLE `metadata_function` (
                                     `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `function_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'function name',
                                     `class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'class name',
                                     `database_id` int NOT NULL COMMENT 'database id',
                                     `function_language` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'function language',
                                     `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='UDF informations';

-- ----------------------------
-- Table structure for metadata_table
-- ----------------------------
DROP TABLE IF EXISTS `metadata_table`;
CREATE TABLE `metadata_table` (
                                  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'table name',
                                  `table_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'type，such as：database,table,view',
                                  `database_id` int NOT NULL COMMENT 'database id',
                                  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'table description',
                                  `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='metadata of table information';

-- ----------------------------
-- Table structure for metadata_table_property
-- ----------------------------
DROP TABLE IF EXISTS `metadata_table_property`;
CREATE TABLE `metadata_table_property` (
                                           `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'key',
                                           `value` MEDIUMTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'value',
                                           `table_id` int NOT NULL COMMENT 'table id',
                                           `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create tiime',
                                           PRIMARY KEY (`key`,`table_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='metadata of table configurations';

-- ----------------------------------------------------------------------------- Metadata related data table the end -------------------------------------------------------------------------------------------


-- ----------------------------------------------------------------------------- multi-tenant of end -------------------------------------------------------------------------------------------

-- ----------------------------
-- Table structure for dlink_tenant
-- ----------------------------
DROP TABLE IF EXISTS `dlink_tenant`;
CREATE TABLE `dlink_tenant` (
                                `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'tenant code',
                                `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'is delete',
                                `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
                                `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='tenant';
-- ----------------------------
-- Records of dlink_tenant
-- ----------------------------
INSERT INTO `dlink_tenant`(`id`, `tenant_code`, `is_delete`, `note`, `create_time`, `update_time`)
VALUES (1, 'DefaultTenant', 0, 'DefaultTenant', current_time, current_time);


-- ----------------------------
-- Table structure for dlink_namespace
-- ----------------------------
DROP TABLE IF EXISTS `dlink_namespace`;
CREATE TABLE `dlink_namespace` (
                                   `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `tenant_id` int NOT NULL COMMENT 'tenant id',
                                   `namespace_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'namespace code',
                                   `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is enable',
                                   `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
                                   `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                   `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE KEY `dlink_namespace_un` (`namespace_code`,`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='namespace';
-- ----------------------------
-- Records of dlink_namespace
-- ----------------------------
INSERT INTO `dlink_namespace`(`id`, `tenant_id`, `namespace_code`, `enabled`, `note`, `create_time`, `update_time`)
VALUES (1, 1, 'DefaultNameSpace', 1, 'DefaultNameSpace', current_time, current_time);


-- ----------------------------
-- Table structure for dlink_role
-- ----------------------------
DROP TABLE IF EXISTS `dlink_role`;
CREATE TABLE `dlink_role` (
                              `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                              `tenant_id` int NOT NULL COMMENT 'tenant id',
                              `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'role code',
                              `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'role name',
                              `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'is delete',
                              `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
                              `create_time` datetime DEFAULT NULL COMMENT 'create time',
                              `update_time` datetime DEFAULT NULL COMMENT 'update time',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE KEY `dlink_role_un` (`role_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='role';
-- ----------------------------
-- Records of dlink_role
-- ----------------------------
INSERT INTO `dlink_role`(`id`, `tenant_id`, `role_code`, `role_name`, `is_delete`, `note`, `create_time`, `update_time`)
VALUES (1, 1, 'SuperAdmin', 'SuperAdmin', 0, 'SuperAdmin of Role', current_time, current_time);

-- ----------------------------
-- Table structure for dlink_role_namespace
-- ----------------------------
DROP TABLE IF EXISTS `dlink_role_namespace`;
CREATE TABLE `dlink_role_namespace` (
                                        `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                        `role_id` int NOT NULL COMMENT 'user id',
                                        `namespace_id` int NOT NULL COMMENT 'namespace id',
                                        `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                        `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE KEY `dlink_role_namespace_un` (`role_id`,`namespace_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Role and namespace relationship';
-- ----------------------------
-- Records of dlink_role_namespace
-- ----------------------------
INSERT INTO `dlink_role_namespace`(`id`, `role_id`, `namespace_id`, `create_time`, `update_time`)
VALUES (1, 1, 1, current_time, current_time);


-- ----------------------------
-- Table structure for dlink_user_role
-- ----------------------------
DROP TABLE IF EXISTS `dlink_user_role`;
CREATE TABLE `dlink_user_role` (
                                   `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `user_id` int NOT NULL COMMENT 'user id',
                                   `role_id` int NOT NULL COMMENT 'role id',
                                   `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                   `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE KEY `dlink_user_role_un` (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Relationship between users and roles';
-- ----------------------------
-- Records of dlink_user_role
-- ----------------------------
INSERT INTO `dlink_user_role`(`id`, `user_id`, `role_id`, `create_time`, `update_time`)
VALUES (1, 1, 1, current_time, current_time);

-- ----------------------------
-- Table structure for dlink_user_tenant
-- ----------------------------
DROP TABLE IF EXISTS `dlink_user_tenant`;
CREATE TABLE `dlink_user_tenant` (
                                     `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                     `user_id` int NOT NULL COMMENT 'user id',
                                     `tenant_id` int NOT NULL COMMENT 'tenant id',
                                     `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                     `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE KEY `dlink_user_role_un` (`user_id`,`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Relationship between users and tenants';
-- ----------------------------
-- Records of dlink_user_tenant
-- ----------------------------
INSERT INTO `dlink_user_tenant`(`id`, `user_id`, `tenant_id`, `create_time`, `update_time`)
VALUES (1, 1, 1, current_time, current_time);

-- ----------------------------------------------------------------------------- multi-tenant of end -------------------------------------------------------------------------------------------


-- ----------------------------
-- Table structure for dlink_udf
-- ----------------------------
DROP TABLE IF EXISTS `dlink_udf`;
CREATE TABLE `dlink_udf` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'udf name',
                             `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Complete class name',
                             `source_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'source code',
                             `compiler_code` binary(255) DEFAULT NULL COMMENT 'compiler product',
                             `version_id` int DEFAULT NULL COMMENT 'version',
                             `version_description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'version description',
                             `is_default` tinyint(1) DEFAULT NULL COMMENT 'Is it default',
                             `document_id` int DEFAULT NULL COMMENT 'corresponding to the document id',
                             `from_version_id` int DEFAULT NULL COMMENT 'Based on udf version id',
                             `code_md5` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'source code of md5',
                             `dialect` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'dialect',
                             `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'type',
                             `step` int DEFAULT NULL COMMENT 'job lifecycle step',
                             `enable` tinyint(1) DEFAULT NULL COMMENT 'is enable',
                             `create_time` datetime DEFAULT NULL COMMENT 'create time',
                             `update_time` datetime DEFAULT NULL COMMENT 'update time',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='udf';

-- ----------------------------
-- Table structure for dlink_udf_template
-- ----------------------------
DROP TABLE IF EXISTS `dlink_udf_template`;
CREATE TABLE `dlink_udf_template` (
                                      `id` int NOT NULL AUTO_INCREMENT,
                                      `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模板名称',
                                      `code_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '代码类型',
                                      `function_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '函数类型',
                                      `template_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '模板代码',
                                      `enabled` tinyint(1) DEFAULT NULL COMMENT 'is enable',
                                      `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                      `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='udf template';

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
VALUES (5, 'python_udf_2', 'Python', 'UDF', 'from pyflink.table import DataTypes\nfrom pyflink.table.udf import udf\n\n@udf(result_type=DataTypes.STRING())\ndef ${className}(variable1:str):\n  return \'\'', NULL, '2022-10-25 09:25:13', '2022-10-25 09:34:47');

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
SET FOREIGN_KEY_CHECKS = 1;
