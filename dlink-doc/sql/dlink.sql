/*
 Navicat Premium Data Transfer

 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Schema         : dlink

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 24/11/2021 09:19:12
*/
create database if not exists dlink;
use dlink;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dlink_catalogue
-- ----------------------------
DROP TABLE IF EXISTS `dlink_catalogue`;
CREATE TABLE `dlink_catalogue`  (
                                    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                    `task_id` int(11) NULL DEFAULT NULL COMMENT '任务ID',
                                    `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
                                    `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
                                    `parent_id` int(11) NOT NULL DEFAULT 0 COMMENT '父ID',
                                    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
                                    `is_leaf` tinyint(1) NOT NULL COMMENT '是否为文件夹',
                                    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `idx_name`(`name`, `parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '目录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_cluster
-- ----------------------------
DROP TABLE IF EXISTS `dlink_cluster`;
CREATE TABLE `dlink_cluster`  (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
                                  `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
                                  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
                                  `hosts` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'HOSTS',
                                  `job_manager_host` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'JMhost',
                                  `version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本',
                                  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
                                  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
                                  `auto_registers` tinyint(1) NULL DEFAULT 0 COMMENT '是否自动注册',
                                  `cluster_configuration_id` int(11) NULL DEFAULT NULL COMMENT '集群配置ID',
                                  `task_id` int(11) NULL DEFAULT NULL COMMENT '任务ID',
                                  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
                                  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '集群' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_cluster_configuration
-- ----------------------------
DROP TABLE IF EXISTS `dlink_cluster_configuration`;
CREATE TABLE `dlink_cluster_configuration`  (
                                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                                `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
                                                `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
                                                `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
                                                `config_json` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '配置JSON',
                                                `is_available` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否可用',
                                                `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
                                                `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
                                                `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                                `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_database
-- ----------------------------
DROP TABLE IF EXISTS `dlink_database`;
CREATE TABLE `dlink_database`  (
                                   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源名',
                                   `alias` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源标题',
                                   `group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'Default' COMMENT '数据源分组',
                                   `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型',
                                   `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'IP',
                                   `port` int(11) NULL DEFAULT NULL COMMENT '端口号',
                                   `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'url',
                                   `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
                                   `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
                                   `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
                                   `flink_config` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'Flink配置',
                                   `flink_template` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'Flink模板',
                                   `db_version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本，如oracle的11g，hbase的2.2.3',
                                   `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态',
                                   `health_time` datetime(0) NULL DEFAULT NULL COMMENT '最近健康时间',
                                   `heartbeat_time` datetime(0) NULL DEFAULT NULL COMMENT '最近心跳监测时间',
                                   `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
                                   `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                   `update_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE INDEX `db_index`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_flink_document
-- ----------------------------
DROP TABLE IF EXISTS `dlink_flink_document`;
CREATE TABLE `dlink_flink_document`  (
                                         `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `category` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文档类型',
                                         `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
                                         `subtype` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '子类型',
                                         `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '信息',
                                         `description` LONGTEXT CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
                                         `fill_value` LONGTEXT CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '填充值',
                                         `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本号',
                                         `like_num` int(255) NULL DEFAULT 0 COMMENT '喜爱值',
                                         `enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用',
                                         `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                         `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 264 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文档管理' ROW_FORMAT = Dynamic;

INSERT INTO `dlink_flink_document` VALUES (1, 'Variable', '优化参数', 'Batch/Streaming', 'set table.exec.async-lookup.buffer-capacity', '异步查找连接可以触发的最大异步操作的操作数。 \nThe max number of async i/o operation that the async lookup join can trigger.', 'Set \'table.exec.async-lookup.buffer-capacity\'=\'100\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (2, 'Variable', '优化参数', 'Batch/Streaming', 'set table.exec.async-lookup.timeout', '异步操作完成的超时时间。 \nThe async timeout for the asynchronous operation to complete.', 'Set \'table.exec.async-lookup.timeout\'=\'3 min\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (3, 'Variable', '优化参数', 'Batch', 'set table.exec.disabled-operators', '禁用指定operators，用逗号分隔 \nMainly for testing. A comma-separated list of operator names, each name represents a kind of disabled operator. Operators that can be disabled include \"NestedLoopJoin\", \"ShuffleHashJoin\", \"BroadcastHashJoin\", \"SortMergeJoin\", \"HashAgg\", \"SortAgg\". By default no operator is disabled.', 'Set \'table.exec.disabled-operators\'=\'SortMergeJoin\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (4, 'Variable', '优化参数', 'Streaming', 'set table.exec.mini-batch.allow-latency', '最大等待时间可用于MiniBatch缓冲输入记录。 MiniBatch是用于缓冲输入记录以减少状态访问的优化。MiniBatch以允许的等待时间间隔以及达到最大缓冲记录数触发。注意：如果将table.exec.mini-batch.enabled设置为true，则其值必须大于零.', 'Set \'table.exec.mini-batch.allow-latency\'=\'-1 ms\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (5, 'Variable', '优化参数', 'Streaming', 'set table.exec.mini-batch.enabled', '指定是否启用MiniBatch优化。 MiniBatch是用于缓冲输入记录以减少状态访问的优化。默认情况下禁用此功能。 要启用此功能，用户应将此配置设置为true。注意：如果启用了mini batch 处理，则必须设置“ table.exec.mini-batch.allow-latency”和“ table.exec.mini-batch.size”.', 'Set \'table.exec.mini-batch.enabled\'=\'false\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (6, 'Variable', '优化参数', 'Streaming', 'set table.exec.mini-batch.size', '可以为MiniBatch缓冲最大输入记录数。 MiniBatch是用于缓冲输入记录以减少状态访问的优化。MiniBatch以允许的等待时间间隔以及达到最大缓冲记录数触发。 注意：MiniBatch当前仅适用于非窗口聚合。如果将table.exec.mini-batch.enabled设置为true，则其值必须为正.', 'Set \'table.exec.mini-batch.size\'=\'-1\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (7, 'Variable', '优化参数', 'Batch/Streaming', 'set table.exec.resource.default-parallelism', '设置所有Operator的默认并行度。 \nSets default parallelism for all operators (such as aggregate, join, filter) to run with parallel instances. This config has a higher priority than parallelism of StreamExecutionEnvironment (actually, this config overrides the parallelism of StreamExecutionEnvironment). A value of -1 indicates that no default parallelism is set, then it will fallback to use the parallelism of StreamExecutionEnvironment.', 'Set \'table.exec.resource.default-parallelism\'=\'1\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (8, 'Variable', '优化参数', 'Batch/Streaming', 'set table.exec.sink.not-null-enforcer', '对表的NOT NULL列约束强制执行不能将空值插入到表中。Flink支持“error”（默认）和“drop”强制行为 \nThe NOT NULL column constraint on a table enforces that null values can\'t be inserted into the table. Flink supports \'error\' (default) and \'drop\' enforcement behavior. By default, Flink will check values and throw runtime exception when null values writing into NOT NULL columns. Users can change the behavior to \'drop\' to silently drop such records without throwing exception.\nPossible values:\n\"ERROR\" \n\"DROP\"', 'Set \'table.exec.sink.not-null-enforcer\'=\'ERROR\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (9, 'Variable', '优化参数', 'Streaming', 'set table.exec.sink.upsert-materialize', '由于分布式系统中 Shuffle 导致 ChangeLog 数据混乱，Sink 接收到的数据可能不是全局 upsert 的顺序。因此，在 upsert sink 之前添加 upsert materialize 运算符。它接收上游的变更日志记录并为下游生成一个 upsert 视图。默认情况下，当唯一键出现分布式无序时，会添加具体化操作符。您也可以选择不实现（NONE）或强制实现（FORCE）。\nPossible values:\n\"NONE\" \n\"FORCE\" \n\"AUTO\"', 'Set \'table.exec.sink.upsert-materialize\'=\'AUTO\';', '1.14', 0, 1, '2022-01-20 15:00:00', '2022-01-20 15:00:00');
INSERT INTO `dlink_flink_document` VALUES (10, 'Module', '建表语句', NULL, 'create.table.kafka', 'kafka快速建表格式', 'CREATE TABLE Kafka_Table (\n  `event_time` TIMESTAMP(3) METADATA FROM \'timestamp\',\n  `partition` BIGINT METADATA VIRTUAL,\n  `offset` BIGINT METADATA VIRTUAL,\n  `user_id` BIGINT,\n  `item_id` BIGINT,\n  `behavior` STRING\n) WITH (\n  \'connector\' = \'kafka\',\n  \'topic\' = \'user_behavior\',\n  \'properties.bootstrap.servers\' = \'localhost:9092\',\n  \'properties.group.id\' = \'testGroup\',\n  \'scan.startup.mode\' = \'earliest-offset\',\n  \'format\' = \'csv\'\n);\n--可选: \'value.fields-include\' = \'ALL\',\n--可选: \'json.ignore-parse-errors\' = \'true\',\n--可选: \'key.fields-prefix\' = \'k_\',', '1.14', 0, 1, '2022-01-20 16:59:18', '2022-01-20 17:57:32');
INSERT INTO `dlink_flink_document` VALUES (11, 'Module', '建表语句', NULL, 'create.table.doris', 'Doris快速建表', 'CREATE TABLE doris_table (\n    cid INT,\n    sid INT,\n    name STRING,\n    cls STRING,\n    score INT,\n    PRIMARY KEY (cid) NOT ENFORCED\n) WITH (       \n\'connector\' = \'doris\',\n\'fenodes\' = \'127.0.0.1:8030\' ,\n\'table.identifier\' = \'test.scoreinfo\',\n\'username\' = \'root\',\n\'password\'=\'\'\n);', '1.14', 0, 1, '2022-01-20 17:08:00', '2022-01-20 17:57:26');
INSERT INTO `dlink_flink_document` VALUES (12, 'Module', '建表语句', NULL, 'create.table.jdbc', 'JDBC建表语句', 'CREATE TABLE JDBC_table (\n  id BIGINT,\n  name STRING,\n  age INT,\n  status BOOLEAN,\n  PRIMARY KEY (id) NOT ENFORCED\n) WITH (\n   \'connector\' = \'jdbc\',\n   \'url\' = \'jdbc:mysql://localhost:3306/mydatabase\',\n   \'table-name\' = \'users\',\n   \'username\' = \'root\',\n   \'password\' = \'123456\'\n);\n--可选: \'sink.parallelism\'=\'1\',\n--可选: \'lookup.cache.ttl\'=\'1000s\',', '1.14', 0, 1, '2022-01-20 17:15:26', '2022-01-20 17:57:20');
INSERT INTO `dlink_flink_document` VALUES (13, 'Module', '创建catalog模块', NULL, 'create.catalog.hive', '创建HIVE的catalog', 'CREATE CATALOG hive WITH ( \n    \'type\' = \'hive\',\n    \'default-database\' = \'default\',\n    \'hive-conf-dir\' = \'/app/wwwroot/MBDC/hive/conf/\', --hive配置文件\n    \'hadoop-conf-dir\'=\'/app/wwwroot/MBDC/hadoop/etc/hadoop/\' --hadoop配置文件，配了环境变量则不需要。\n);', '1.14', 0, 1, '2022-01-20 17:18:54', '2022-01-20 17:18:54');
INSERT INTO `dlink_flink_document` VALUES (14, 'Operator', '', NULL, 'use.catalog.hive', '使用hive的catalog', 'USE CATALOG hive;', '1.14', 0, 1, '2022-01-20 17:22:53', '2022-01-20 17:22:53');
INSERT INTO `dlink_flink_document` VALUES (15, 'Operator', NULL, NULL, 'use.catalog.default', '使用default的catalog', 'USE CATALOG default_catalog; \n', '1.14', 0, 1, '2022-01-20 17:23:48', '2022-01-20 17:24:23');
INSERT INTO `dlink_flink_document` VALUES (16, 'Variable', '设置参数', NULL, 'set dialect.hive', '使用hive方言', 'Set table.sql-dialect=hive;', '1.14', 0, 1, '2022-01-20 17:25:37', '2022-01-20 17:27:23');
INSERT INTO `dlink_flink_document` VALUES (17, 'Variable', '设置参数', NULL, 'set dialect.default', '使用default方言', 'Set table.sql-dialect=default;', '1.14', 0, 1, '2022-01-20 17:26:19', '2022-01-20 17:27:20');
INSERT INTO `dlink_flink_document` VALUES (18, 'Module', '建表语句', NULL, 'create.stream.table.hive', '创建流式HIVE表', 'CREATE CATALOG hive WITH ( --创建hive的catalog\n    \'type\' = \'hive\',\n    \'hive-conf-dir\' = \'/app/wwwroot/MBDC/hive/conf/\',\n    \'hadoop-conf-dir\'=\'/app/wwwroot/MBDC/hadoop/etc/hadoop/\'\n);\n\nUSE CATALOG hive; \nUSE offline_db; --选择库\nset table.sql-dialect=hive; --设置方言\n\nCREATE TABLE hive_stream_table (\n  user_id STRING,\n  order_amount DOUBLE\n) PARTITIONED BY (dt STRING, hr STRING) STORED AS parquet TBLPROPERTIES (\n  \'partition.time-extractor.timestamp-pattern\'=\'$dt $hr:00:00\',\n  \'sink.partition-commit.trigger\'=\'partition-time\',\n  \'sink.partition-commit.delay\'=\'1min\',\n  \'sink.semantic\' = \'exactly-once\',\n  \'sink.rolling-policy.rollover-interval\' =\'1min\',\n  \'sink.rolling-policy.check-interval\'=\'1min\',\n  \'sink.partition-commit.policy.kind\'=\'metastore,success-file\'\n);', '1.14', 0, 1, '2022-01-20 17:34:06', '2022-01-20 17:46:41');
INSERT INTO `dlink_flink_document` VALUES (19, 'Module', '建表语句', NULL, 'create.table.mysql_cdc', '创建Mysql_CDC表', 'CREATE TABLE mysql_cdc_table(\n    cid INT,\n    sid INT,\n    cls STRING,\n    score INT,\n    PRIMARY KEY (cid) NOT ENFORCED\n) WITH (\n\'connector\' = \'mysql-cdc\',\n\'hostname\' = \'127.0.0.1\',\n\'port\' = \'3306\',\n\'username\' = \'test\',\n\'password\' = \'123456\',\n\'database-name\' = \'test\',\n\'server-time-zone\' = \'UTC\',\n\'scan.incremental.snapshot.enabled\' = \'true\',\n\'debezium.snapshot.mode\'=\'latest-offset\' ,-- 或者key是scan.startup.mode，initial表示要历史数据，latest-offset表示不要历史数据\n\'debezium.datetime.format.date\'=\'yyyy-MM-dd\',\n\'debezium.datetime.format.time\'=\'HH-mm-ss\',\n\'debezium.datetime.format.datetime\'=\'yyyy-MM-dd HH-mm-ss\',\n\'debezium.datetime.format.timestamp\'=\'yyyy-MM-dd HH-mm-ss\',\n\'debezium.datetime.format.timestamp.zone\'=\'UTC+8\',\n\'table-name\' = \'mysql_cdc_table\');', '1.14', 0, 1, '2022-01-20 17:49:14', '2022-01-20 17:52:20');
INSERT INTO `dlink_flink_document` VALUES (20, 'Module', '建表语句', NULL, 'create.table.hudi', '创建hudi表', 'CREATE TABLE hudi_table\n(\n    `goods_order_id`  bigint COMMENT \'自增主键id\',\n    `goods_order_uid` string COMMENT \'订单uid\',\n    `customer_uid`    string COMMENT \'客户uid\',\n    `customer_name`   string COMMENT \'客户name\',\n    `create_time`     timestamp(3) COMMENT \'创建时间\',\n    `update_time`     timestamp(3) COMMENT \'更新时间\',\n    `create_by`       string COMMENT \'创建人uid（唯一标识）\',\n    `update_by`       string COMMENT \'更新人uid（唯一标识）\',\n    PRIMARY KEY (goods_order_id) NOT ENFORCED\n) COMMENT \'hudi_table\'\nWITH (\n\'connector\' = \'hudi\',\n\'path\' = \'hdfs://cluster1/data/bizdata/cdc/mysql/order/goods_order\', -- 路径会自动创建\n\'hoodie.datasource.write.recordkey.field\' = \'goods_order_id\', -- 主键\n\'write.precombine.field\' = \'update_time\', -- 相同的键值时，取此字段最大值，默认ts字段\n\'read.streaming.skip_compaction\' = \'true\', -- 避免重复消费问题\n\'write.bucket_assign.tasks\' = \'2\', -- 并发写的 bucekt 数\n\'write.tasks\' = \'2\',\n\'compaction.tasks\' = \'1\',\n\'write.operation\' = \'upsert\', -- UPSERT（插入更新）\\INSERT（插入）\\BULK_INSERT（批插入）（upsert性能会低些，不适合埋点上报）\n\'write.rate.limit\' = \'20000\', -- 限制每秒多少条\n\'table.type\' = \'COPY_ON_WRITE\', -- 默认COPY_ON_WRITE ，\n\'compaction.async.enabled\' = \'true\', -- 在线压缩\n\'compaction.trigger.strategy\' = \'num_or_time\', -- 按次数压缩\n\'compaction.delta_commits\' = \'20\', -- 默认为5\n\'compaction.delta_seconds\' = \'60\', -- 默认为1小时\n\'hive_sync.enable\' = \'true\', -- 启用hive同步\n\'hive_sync.mode\' = \'hms\', -- 启用hive hms同步，默认jdbc\n\'hive_sync.metastore.uris\' = \'thrift://cdh2.vision.com:9083\', -- required, metastore的端口\n\'hive_sync.jdbc_url\' = \'jdbc:hive2://cdh1.vision.com:10000\', -- required, hiveServer地址\n\'hive_sync.table\' = \'order_mysql_goods_order\', -- required, hive 新建的表名 会自动同步hudi的表结构和数据到hive\n\'hive_sync.db\' = \'cdc_ods\', -- required, hive 新建的数据库名\n\'hive_sync.username\' = \'hive\', -- required, HMS 用户名\n\'hive_sync.password\' = \'123456\', -- required, HMS 密码\n\'hive_sync.skip_ro_suffix\' = \'true\' -- 去除ro后缀\n);', '1.14', 0, 1, '2022-01-20 17:56:50', '2022-01-20 17:56:50');


-- ----------------------------
-- Table structure for dlink_history
-- ----------------------------
DROP TABLE IF EXISTS `dlink_history`;
CREATE TABLE `dlink_history`  (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                  `cluster_id` int(11) NOT NULL DEFAULT 0 COMMENT '集群ID',
                                  `cluster_configuration_id` int(11) NULL DEFAULT NULL,
                                  `session` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会话',
                                  `job_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'JobID',
                                  `job_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作业名',
                                  `job_manager_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'JM地址',
                                  `status` int(1) NOT NULL DEFAULT 0 COMMENT '状态',
                                  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
                                  `statement` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '语句集',
                                  `error` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '异常信息',
                                  `result` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '结果集',
                                  `config_json` json NULL COMMENT '配置JSON',
                                  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
                                  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
                                  `task_id` int(11) NULL DEFAULT NULL COMMENT '作业ID',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `task_index`(`task_id`) USING BTREE,
                                  INDEX `cluster_index`(`cluster_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 209 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '执行历史' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_jar
-- ----------------------------
DROP TABLE IF EXISTS `dlink_jar`;
CREATE TABLE `dlink_jar`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                              `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
                              `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
                              `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
                              `path` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '路径',
                              `main_class` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '启动类',
                              `paras` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '启动类入参',
                              `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
                              `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
                              `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                              `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_savepoints
-- ----------------------------
DROP TABLE IF EXISTS `dlink_savepoints`;
CREATE TABLE `dlink_savepoints`  (
                                     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                     `task_id` int(11) NOT NULL COMMENT '任务ID',
                                     `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
                                     `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
                                     `path` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '路径',
                                     `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_schema_history
-- ----------------------------
DROP TABLE IF EXISTS `dlink_schema_history`;
CREATE TABLE `dlink_schema_history`  (
                                         `installed_rank` int(11) NOT NULL,
                                         `version` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                         `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                         `type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                         `script` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                         `checksum` int(11) NULL DEFAULT NULL,
                                         `installed_by` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                         `installed_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         `execution_time` int(11) NOT NULL,
                                         `success` tinyint(1) NOT NULL,
                                         PRIMARY KEY (`installed_rank`) USING BTREE,
                                         INDEX `dlink_schema_history_s_idx`(`success`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `dlink_sys_config`;
CREATE TABLE `dlink_sys_config`  (
                                     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                     `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配置名',
                                     `value` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '值',
                                     `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                     `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_task
-- ----------------------------
DROP TABLE IF EXISTS `dlink_task`;
CREATE TABLE `dlink_task`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                               `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
                               `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
                               `dialect` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '方言',
                               `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
                               `check_point` int(11) NULL DEFAULT NULL COMMENT 'CheckPoint ',
                               `save_point_strategy` int(1) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT 'SavePoint策略',
                               `save_point_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'SavePointPath',
                               `parallelism` int(4) NULL DEFAULT NULL COMMENT 'parallelism',
                               `fragment` tinyint(1) NULL DEFAULT NULL COMMENT 'fragment',
                               `statement_set` tinyint(1) NULL DEFAULT NULL COMMENT '启用语句集',
                               `batch_model` tinyint(1) NULL DEFAULT 0 COMMENT '使用批模式',
                               `cluster_id` int(11) NULL DEFAULT NULL COMMENT 'Flink集群ID',
                               `cluster_configuration_id` int(11) NULL DEFAULT NULL COMMENT '集群配置ID',
                               `database_id` int(11) NULL DEFAULT NULL COMMENT '数据源ID',
                               `jar_id` int(11) NULL DEFAULT NULL COMMENT 'jarID',
                               `env_id` int(11) NULL DEFAULT NULL COMMENT '环境ID',
                               `alert_group_id` int(11) NULL DEFAULT NULL COMMENT '报警组ID',
                               `config_json` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '配置JSON',
                               `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
                               `step` int(11) NULL DEFAULT NULL COMMENT '作业生命周期',
                               `job_instance_id` int(11) NULL DEFAULT NULL COMMENT '作业实例ID',
                               `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
                               `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                               `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '作业' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_task_statement
-- ----------------------------
DROP TABLE IF EXISTS `dlink_task_statement`;
CREATE TABLE `dlink_task_statement`  (
                                         `id` int(11) NOT NULL COMMENT 'ID',
                                         `statement` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '语句',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '语句' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_user
-- ----------------------------
DROP TABLE IF EXISTS `dlink_user`;
CREATE TABLE `dlink_user`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                               `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录名',
                               `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
                               `nickname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
                               `worknum` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工号',
                               `avatar` blob NULL COMMENT '头像',
                               `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
                               `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
                               `is_delete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否被删除',
                               `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                               `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`id`)
)  ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
INSERT INTO `dlink_user`(`id`, `username`, `password`, `nickname`, `worknum`, `avatar`, `mobile`, `enabled`, `is_delete`, `create_time`, `update_time`) VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'Admin', NULL, NULL, NULL, 1, 0, '2021-11-28 17:19:27', '2021-11-28 17:19:31');

-- ----------------------------
-- Table structure for dlink_job_instance
-- ----------------------------
DROP TABLE IF EXISTS `dlink_job_instance`;
create table dlink_job_instance
(
    id                   int auto_increment comment '自增主键'
        primary key,
    name                 varchar(255) null comment '作业实例名',
    task_id              int         null comment 'taskID',
    step              int         null comment '生命周期',
    cluster_id           int         null comment '集群ID',
    jid                  varchar(50) null comment 'FlinkJobId',
    status               varchar(50) null comment '实例状态',
    history_id           int         null comment '提交历史ID',
    create_time          datetime    null comment '创建时间',
    update_time          datetime    null comment '更新时间',
    finish_time          datetime    null comment '完成时间',
    duration             bigint      null comment '耗时',
    error                text        null comment '异常日志',
    failed_restart_count int         null comment '重启次数'
) comment '作业实例';

DROP TABLE IF EXISTS `dlink_alert_instance`;
create table dlink_alert_instance
(
    id int auto_increment comment '自增主键'
        primary key,
    name varchar(50) not null comment '名称',
    type varchar(50) null comment '类型',
    params text null comment '配置',
    enabled tinyint default 1 null comment '是否启用',
    create_time datetime null comment '创建时间',
    update_time datetime null comment '更新时间'
)
    comment 'Alert实例';

DROP TABLE IF EXISTS `dlink_alert_group`;
create table dlink_alert_group
(
    id int auto_increment comment '自增主键'
        primary key,
    name varchar(50) not null comment '名称',
    alert_instance_ids text null comment 'Alert实例IDS',
    note varchar(255) null comment '说明',
    enabled tinyint default 1 null comment '是否启用',
    create_time datetime null comment '创建时间',
    update_time datetime null comment '更新时间'
)
    comment 'Alert组';

DROP TABLE IF EXISTS `dlink_alert_history`;
create table dlink_alert_history
(
    id int auto_increment comment '自增主键'
        primary key,
    alert_group_id int null comment 'Alert组ID',
    job_instance_id int null comment '作业实例ID',
    title varchar(255) null comment '标题',
    content text null comment '正文',
    status int null comment '状态',
    log text null comment '日志',
    create_time datetime null comment '创建时间',
    update_time datetime null comment '更新时间'
)
    comment 'Alert历史';

DROP TABLE IF EXISTS `dlink_job_history`;
create table dlink_job_history
(
    id int comment '实例主键'
        primary key,
    job_json json null comment 'Job信息',
    exceptions_json json null comment '异常日志',
    checkpoints_json json null comment '保存点',
    checkpoints_config_json json null comment '保存点配置',
    config_json json null comment '配置',
    jar_json json null comment 'Jar配置',
    cluster_json json null comment '集群实例',
    cluster_configuration_json json null comment '集群配置',
    update_time datetime null comment '更新时间'
)
    comment 'Job历史详情';

CREATE INDEX dlink_job_instance_task_id_IDX USING BTREE ON dlink_job_instance (task_id);

SET FOREIGN_KEY_CHECKS = 1;
