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


CREATE
    OR REPLACE FUNCTION trigger_set_timestamp()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.update_time
        = NOW();
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;



-- ----------------------------
-- Table structure for dinky_alert_group
-- ----------------------------
DROP TABLE IF EXISTS dinky_alert_group;

CREATE TABLE dinky_alert_group
(
    id                 SERIAL PRIMARY KEY NOT NULL,
    name               VARCHAR(50)        NOT NULL,
    tenant_id          INT                NOT NULL DEFAULT 1,
    alert_instance_ids TEXT,
    note               VARCHAR(255)                DEFAULT NULL,
    enabled            SMALLINT                    DEFAULT 1,
    create_time        TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator            INT                         DEFAULT NULL,
    updater            INT                         DEFAULT NULL,
    CONSTRAINT alert_group_un_idx1 UNIQUE (name, tenant_id)
);

COMMENT
    ON COLUMN dinky_alert_group.id IS 'id';
COMMENT
    ON COLUMN dinky_alert_group.name IS 'alert group name';
COMMENT
    ON COLUMN dinky_alert_group.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_alert_group.alert_instance_ids IS 'Alert instance IDS';
COMMENT
    ON COLUMN dinky_alert_group.note IS 'note';
COMMENT
    ON COLUMN dinky_alert_group.enabled IS 'is enable';
COMMENT
    ON COLUMN dinky_alert_group.create_time IS 'create time';
COMMENT
    ON COLUMN dinky_alert_group.update_time IS 'update time';
COMMENT
    ON COLUMN dinky_alert_group.creator IS 'creator user id';
COMMENT
    ON COLUMN dinky_alert_group.updater IS 'updater user id';
COMMENT
    ON TABLE dinky_alert_group IS 'Alert group';

CREATE TRIGGER set_update_time_dinky_alert_group
    BEFORE UPDATE
    ON dinky_alert_group
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- ----------------------------
-- Table structure for dinky_alert_history
-- ----------------------------

DROP TABLE IF EXISTS dinky_alert_history;

CREATE TABLE dinky_alert_history
(
    id              SERIAL PRIMARY KEY NOT NULL,
    tenant_id       INT                NOT NULL DEFAULT 1,
    alert_group_id  INT                         DEFAULT NULL,
    job_instance_id INT                         DEFAULT NULL,
    title           VARCHAR(255)                DEFAULT NULL,
    content         TEXT,
    status          INT                         DEFAULT NULL,
    log             TEXT,
    create_time     TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT
    ON COLUMN dinky_alert_history.id IS 'id';
COMMENT
    ON COLUMN dinky_alert_history.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_alert_history.alert_group_id IS 'Alert group ID';
COMMENT
    ON COLUMN dinky_alert_history.job_instance_id IS 'job instance ID';
COMMENT
    ON COLUMN dinky_alert_history.title IS 'alert title';
COMMENT
    ON COLUMN dinky_alert_history.content IS 'content description';
COMMENT
    ON COLUMN dinky_alert_history.status IS 'alert status';
COMMENT
    ON COLUMN dinky_alert_history.log IS 'log';
COMMENT
    ON COLUMN dinky_alert_history.create_time IS 'create time';
COMMENT
    ON COLUMN dinky_alert_history.update_time IS 'update time';
COMMENT
    ON TABLE dinky_alert_history IS 'Alert history';

CREATE TRIGGER set_update_time_dinky_alert_history
    BEFORE UPDATE
    ON dinky_alert_history
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- ----------------------------
-- Table structure for dinky_alert_instance
-- ----------------------------

DROP TABLE IF EXISTS dinky_alert_instance;

CREATE TABLE dinky_alert_instance
(
    id          SERIAL PRIMARY KEY NOT NULL,
    name        VARCHAR(50)        NOT NULL,
    tenant_id   INT                NOT NULL DEFAULT 1,
    type        VARCHAR(50),
    params      JSON,
    enabled     SMALLINT                    DEFAULT 1,
    create_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator     INT                         DEFAULT NULL,
    updater     INT                         DEFAULT NULL,
    UNIQUE (name, tenant_id)
);

COMMENT
    ON COLUMN dinky_alert_instance.id IS 'id';
COMMENT
    ON COLUMN dinky_alert_instance.name IS 'alert instance name';
COMMENT
    ON COLUMN dinky_alert_instance.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_alert_instance.type IS 'alert instance type such as: DingTalk,Wechat(Webhook,app) Feishu ,email';
COMMENT
    ON COLUMN dinky_alert_instance.params IS 'configuration';
COMMENT
    ON COLUMN dinky_alert_instance.enabled IS 'is enable';
COMMENT
    ON COLUMN dinky_alert_instance.create_time IS 'create time';
COMMENT
    ON COLUMN dinky_alert_instance.update_time IS 'update time';
COMMENT
    ON COLUMN dinky_alert_instance.creator IS 'creator user id';
COMMENT
    ON COLUMN dinky_alert_instance.updater IS 'updater user id';
COMMENT
    ON TABLE dinky_alert_instance IS 'Alert instance';


CREATE TRIGGER set_update_time_dinky_alert_instance
    BEFORE UPDATE
    ON dinky_alert_instance
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- ----------------------------
-- Table structure for dinky_catalogue
-- ----------------------------

DROP TABLE IF EXISTS dinky_catalogue;

CREATE TABLE dinky_catalogue
(
    id          SERIAL PRIMARY KEY NOT NULL,
    tenant_id   INT                NOT NULL DEFAULT 1,
    task_id     INT                         DEFAULT NULL,
    name        VARCHAR(100)       NOT NULL,
    type        VARCHAR(50)                 DEFAULT NULL,
    parent_id   INT                NOT NULL DEFAULT 0,
    enabled     SMALLINT           NOT NULL DEFAULT 1,
    is_leaf     SMALLINT           NOT NULL,
    create_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator     INT                         DEFAULT NULL,
    updater     INT                         DEFAULT NULL,
    UNIQUE (name, parent_id, tenant_id)
);

COMMENT
    ON COLUMN dinky_catalogue.id IS 'ID';
COMMENT
    ON COLUMN dinky_catalogue.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_catalogue.task_id IS 'Job ID';
COMMENT
    ON COLUMN dinky_catalogue.name IS 'Job Name';
COMMENT
    ON COLUMN dinky_catalogue.type IS 'Job Type';
COMMENT
    ON COLUMN dinky_catalogue.parent_id IS 'parent ID';
COMMENT
    ON COLUMN dinky_catalogue.enabled IS 'is enable';
COMMENT
    ON COLUMN dinky_catalogue.is_leaf IS 'is leaf node';
COMMENT
    ON COLUMN dinky_catalogue.create_time IS 'create time';
COMMENT
    ON COLUMN dinky_catalogue.update_time IS 'update time';
COMMENT
    ON COLUMN dinky_catalogue.creator IS 'creator user id';
COMMENT
    ON COLUMN dinky_catalogue.updater IS 'updater user id';
COMMENT
    ON TABLE dinky_catalogue IS 'catalogue';


CREATE TRIGGER set_update_time_dinky_catalogue
    BEFORE UPDATE
    ON dinky_catalogue
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- ----------------------------
-- Table structure for dinky_cluster
-- ----------------------------
DROP TABLE IF EXISTS dinky_cluster;

CREATE TABLE dinky_cluster
(
    id                       SERIAL PRIMARY KEY NOT NULL,
    tenant_id                INT                NOT NULL DEFAULT 1,
    name                     VARCHAR(255)       NOT NULL,
    alias                    VARCHAR(255)                DEFAULT NULL,
    type                     VARCHAR(50)                 DEFAULT NULL,
    hosts                    TEXT                        DEFAULT NULL,
    job_manager_host         VARCHAR(255)                DEFAULT NULL,
    version                  VARCHAR(20)                 DEFAULT NULL,
    status                   INT                         DEFAULT NULL,
    note                     VARCHAR(255)                DEFAULT NULL,
    auto_registers           SMALLINT                    DEFAULT 0,
    cluster_configuration_id INT                         DEFAULT NULL,
    task_id                  INT                         DEFAULT NULL,
    enabled                  SMALLINT           NOT NULL DEFAULT 1,
    create_time              TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time              TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator                  INT                         DEFAULT NULL,
    updater                  INT                         DEFAULT NULL
);

CREATE UNIQUE INDEX cluster_un_idx1 ON dinky_cluster (name, tenant_id);

COMMENT
    ON COLUMN dinky_cluster.id IS 'ID';
COMMENT
    ON COLUMN dinky_cluster.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_cluster.name IS 'cluster instance name';
COMMENT
    ON COLUMN dinky_cluster.alias IS 'cluster instance alias';
COMMENT
    ON COLUMN dinky_cluster.type IS 'cluster types';
COMMENT
    ON COLUMN dinky_cluster.hosts IS 'cluster hosts';
COMMENT
    ON COLUMN dinky_cluster.job_manager_host IS 'Job Manager Host';
COMMENT
    ON COLUMN dinky_cluster.version IS 'version';
COMMENT
    ON COLUMN dinky_cluster.status IS 'cluster status';
COMMENT
    ON COLUMN dinky_cluster.note IS 'note';
COMMENT
    ON COLUMN dinky_cluster.auto_registers IS 'is auto registration';
COMMENT
    ON COLUMN dinky_cluster.cluster_configuration_id IS 'cluster configuration id';
COMMENT
    ON COLUMN dinky_cluster.task_id IS 'task ID';
COMMENT
    ON COLUMN dinky_cluster.enabled IS 'is enable';
COMMENT
    ON COLUMN dinky_cluster.create_time IS 'create time';
COMMENT
    ON COLUMN dinky_cluster.update_time IS 'update time';
COMMENT
    ON COLUMN dinky_cluster.creator IS 'creator user id';
COMMENT
    ON COLUMN dinky_cluster.updater IS 'updater user id';
COMMENT
    ON TABLE dinky_cluster IS 'cluster instance management';


CREATE TRIGGER set_update_time_dinky_cluster
    BEFORE UPDATE
    ON dinky_cluster
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- ----------------------------
-- Table structure for dinky_cluster_configuration
-- ----------------------------
DROP TABLE IF EXISTS dinky_cluster_configuration;

CREATE TABLE dinky_cluster_configuration
(
    id           SERIAL PRIMARY KEY NOT NULL,
    tenant_id    INT                NOT NULL DEFAULT 1,
    name         VARCHAR(255)       NOT NULL,
    type         VARCHAR(50)                 DEFAULT NULL,
    config_json  TEXT                        DEFAULT NULL,
    is_available BOOLEAN            NOT NULL DEFAULT false,
    note         VARCHAR(255)                DEFAULT NULL,
    enabled      BOOLEAN            NOT NULL DEFAULT true,
    create_time  TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator      INT                         DEFAULT NULL,
    updater      INT                         DEFAULT NULL
);

CREATE UNIQUE INDEX cluster_configuration_un_idx1 ON dinky_cluster_configuration (name, tenant_id);

COMMENT
    ON TABLE dinky_cluster_configuration IS 'cluster configuration management';
COMMENT
    ON COLUMN dinky_cluster_configuration.id IS 'ID';
COMMENT
    ON COLUMN dinky_cluster_configuration.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_cluster_configuration.name IS 'cluster configuration name';
COMMENT
    ON COLUMN dinky_cluster_configuration.type IS 'cluster type';
COMMENT
    ON COLUMN dinky_cluster_configuration.config_json IS 'json of configuration';
COMMENT
    ON COLUMN dinky_cluster_configuration.is_available IS 'is available';
COMMENT
    ON COLUMN dinky_cluster_configuration.note IS 'note';
COMMENT
    ON COLUMN dinky_cluster_configuration.enabled IS 'is enable';
COMMENT
    ON COLUMN dinky_cluster_configuration.create_time IS 'create time';
COMMENT
    ON COLUMN dinky_cluster_configuration.update_time IS 'update time';
COMMENT
    ON COLUMN dinky_cluster_configuration.creator IS 'creator user id';
COMMENT
    ON COLUMN dinky_cluster_configuration.updater IS 'updater user id';


CREATE TRIGGER set_update_time_dinky_cluster_configuration
    BEFORE UPDATE
    ON dinky_cluster_configuration
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();



-- ----------------------------
-- Table structure for dinky_database
-- ----------------------------
DROP TABLE IF EXISTS dinky_database;

CREATE TABLE dinky_database
(
    id             SERIAL PRIMARY KEY NOT NULL,
    tenant_id      INT                NOT NULL DEFAULT 1,
    name           VARCHAR(30)        NOT NULL,
    group_name     VARCHAR(255)                DEFAULT 'Default',
    type           VARCHAR(50)        NOT NULL,
    connect_config TEXT               NOT NULL,
    note           VARCHAR(255)                DEFAULT NULL,
    flink_config   TEXT                        DEFAULT NULL,
    flink_template TEXT                        DEFAULT NULL,
    db_version     VARCHAR(255)                DEFAULT NULL,
    status         BOOLEAN                     DEFAULT NULL,
    health_time    TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL,
    heartbeat_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL,
    enabled        BOOLEAN            NOT NULL DEFAULT true,
    create_time    TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator        INT                         DEFAULT NULL,
    updater        INT                         DEFAULT NULL
);

CREATE UNIQUE INDEX database_un_idx1 ON dinky_database (name, tenant_id);

COMMENT
    ON TABLE dinky_database IS 'database management';
COMMENT
    ON COLUMN dinky_database.id IS 'ID';
COMMENT
    ON COLUMN dinky_database.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_database.name IS 'database name';
COMMENT
    ON COLUMN dinky_database.group_name IS 'database belong group name';
COMMENT
    ON COLUMN dinky_database.type IS 'database type';
COMMENT
    ON COLUMN dinky_database.connect_config IS 'connect config';
COMMENT
    ON COLUMN dinky_database.note IS 'note';
COMMENT
    ON COLUMN dinky_database.flink_config IS 'Flink configuration';
COMMENT
    ON COLUMN dinky_database.flink_template IS 'Flink template';
COMMENT
    ON COLUMN dinky_database.db_version IS 'version，such as: 11g of oracle ，2.2.3 of hbase';
COMMENT
    ON COLUMN dinky_database.status IS 'heartbeat status';
COMMENT
    ON COLUMN dinky_database.health_time IS 'last heartbeat time of trigger';
COMMENT
    ON COLUMN dinky_database.heartbeat_time IS 'last heartbeat time';
COMMENT
    ON COLUMN dinky_database.enabled IS 'is enable';
COMMENT
    ON COLUMN dinky_database.create_time IS 'create time';
COMMENT
    ON COLUMN dinky_database.update_time IS 'update time';
COMMENT
    ON COLUMN dinky_database.creator IS 'creator user id';
COMMENT
    ON COLUMN dinky_database.updater IS 'updater user id';



CREATE TRIGGER set_update_time_dinky_database
    BEFORE UPDATE
    ON dinky_database
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();



-- ----------------------------
-- Table structure for dinky_flink_document
-- ----------------------------
DROP TABLE IF EXISTS dinky_flink_document;

CREATE TABLE dinky_flink_document
(
    id          SERIAL PRIMARY KEY NOT NULL,
    category    VARCHAR(255)                DEFAULT NULL,
    type        VARCHAR(255)                DEFAULT NULL,
    subtype     VARCHAR(255)                DEFAULT NULL,
    name        VARCHAR(255)                DEFAULT NULL,
    description TEXT                        DEFAULT NULL,
    fill_value  TEXT                        DEFAULT NULL,
    version     VARCHAR(255)                DEFAULT NULL,
    like_num    INT                         DEFAULT 0,
    enabled     smallint           NOT NULL DEFAULT 1,
    create_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator     INT                         DEFAULT NULL,
    updater     INT                         DEFAULT NULL
);

COMMENT
    ON TABLE dinky_flink_document IS 'flink document management';
COMMENT
    ON COLUMN dinky_flink_document.id IS 'id';
COMMENT
    ON COLUMN dinky_flink_document.category IS 'document category';
COMMENT
    ON COLUMN dinky_flink_document.type IS 'document type';
COMMENT
    ON COLUMN dinky_flink_document.subtype IS 'document subtype';
COMMENT
    ON COLUMN dinky_flink_document.name IS 'document name';
COMMENT
    ON COLUMN dinky_flink_document.description IS 'document description';
COMMENT
    ON COLUMN dinky_flink_document.fill_value IS 'fill value';
COMMENT
    ON COLUMN dinky_flink_document.version IS 'document version such as:(flink1.12, flink1.13, flink1.14, flink1.15, flink1.16, flink1.17, flink1.18)';
COMMENT
    ON COLUMN dinky_flink_document.like_num IS 'like number';
COMMENT
    ON COLUMN dinky_flink_document.enabled IS 'is enable';
COMMENT
    ON COLUMN dinky_flink_document.create_time IS 'create time';
COMMENT
    ON COLUMN dinky_flink_document.update_time IS 'update time';
COMMENT
    ON COLUMN dinky_flink_document.creator IS 'creator user id';
COMMENT
    ON COLUMN dinky_flink_document.updater IS 'updater user id';



CREATE TRIGGER set_update_time_dinky_flink_document
    BEFORE UPDATE
    ON dinky_flink_document
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- ----------------------------
-- Records of dinky_flink_document
-- ----------------------------
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (1, 'Variable', 'FLINK_OPTIONS', '', 'set table.exec.async-lookup.buffer-capacity', e'异步查找连接可以触发的最大异步操作的操作数。
The max number of async i/o operation that the async lookup join can trigger.',
        'Set ''table.exec.async-lookup.buffer-capacity''=''100'';', '1.14', 0, 1, '2022-01-20 15:00:00.000000',
        '2023-12-27 23:58:09.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (2, 'Variable', 'FLINK_OPTIONS', '', 'set table.exec.async-lookup.timeout', e'异步操作完成的超时时间。
The async timeout for the asynchronous operation to complete.', 'Set ''table.exec.async-lookup.timeout''=''3 min'';',
        '1.14', 0, 1, '2022-01-20 15:00:00.000000', '2023-12-27 23:58:09.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (3, 'Variable', 'FLINK_OPTIONS', '', 'set table.exec.disabled-operators', e'禁用指定operators，用逗号分隔
Mainly for testing. A comma-separated list of operator names, each name represents a kind of disabled operator. Operators that can be disabled include "NestedLoopJoin", "ShuffleHashJoin", "BroadcastHashJoin", "SortMergeJoin", "HashAgg", "SortAgg". By default no operator is disabled.',
        'Set ''table.exec.disabled-operators''=''SortMergeJoin'';', '1.14', 0, 1, '2022-01-20 15:00:00.000000',
        '2023-12-27 23:58:09.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (4, 'Variable', 'FLINK_OPTIONS', '', 'set table.exec.mini-batch.allow-latency',
        '最大等待时间可用于MiniBatch缓冲输入记录。 MiniBatch是用于缓冲输入记录以减少状态访问的优化。MiniBatch以允许的等待时间间隔以及达到最大缓冲记录数触发。注意：如果将table.exec.mini-batch.enabled设置为true，则其值必须大于零.',
        'Set ''table.exec.mini-batch.allow-latency''=''-1 ms'';', '1.14', 0, 1, '2022-01-20 15:00:00.000000',
        '2023-12-27 23:58:09.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (5, 'Variable', 'FLINK_OPTIONS', '', 'set table.exec.mini-batch.enabled',
        '指定是否启用MiniBatch优化。 MiniBatch是用于缓冲输入记录以减少状态访问的优化。默认情况下禁用此功能。 要启用此功能，用户应将此配置设置为true。注意：如果启用了mini batch 处理，则必须设置“ table.exec.mini-batch.allow-latency”和“ table.exec.mini-batch.size”.',
        'Set ''table.exec.mini-batch.enabled''=''false'';', '1.14', 0, 1, '2022-01-20 15:00:00.000000',
        '2023-12-27 23:58:09.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (6, 'Variable', 'FLINK_OPTIONS', '', 'set table.exec.mini-batch.size',
        '可以为MiniBatch缓冲最大输入记录数。 MiniBatch是用于缓冲输入记录以减少状态访问的优化。MiniBatch以允许的等待时间间隔以及达到最大缓冲记录数触发。 注意：MiniBatch当前仅适用于非窗口聚合。如果将table.exec.mini-batch.enabled设置为true，则其值必须为正.',
        'Set ''table.exec.mini-batch.size''=''-1'';', '1.14', 0, 1, '2022-01-20 15:00:00.000000',
        '2023-12-27 23:58:09.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (7, 'Variable', 'FLINK_OPTIONS', '', 'set table.exec.resource.default-parallelism', e'设置所有Operator的默认并行度。
Sets default parallelism for all operators (such as aggregate, join, filter) to run with parallel instances. This config has a higher priority than parallelism of StreamExecutionEnvironment (actually, this config overrides the parallelism of StreamExecutionEnvironment). A value of -1 indicates that no default parallelism is set, then it will fallback to use the parallelism of StreamExecutionEnvironment.',
        'Set ''table.exec.resource.default-parallelism''=''1'';', '1.14', 0, 1, '2022-01-20 15:00:00.000000',
        '2023-12-27 23:58:09.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (8, 'Variable', 'FLINK_OPTIONS', '', 'set table.exec.sink.not-null-enforcer', e'对表的NOT NULL列约束强制执行不能将空值插入到表中。Flink支持“error”（默认）和“drop”强制行为
The NOT NULL column constraint on a table enforces that null values can''t be inserted into the table. Flink supports ''error'' (default) and ''drop'' enforcement behavior. By default, Flink will check values and throw runtime exception when null values writing into NOT NULL columns. Users can change the behavior to ''drop'' to silently drop such records without throwing exception.
Possible values:
"ERROR"
"DROP"', 'Set ''table.exec.sink.not-null-enforcer''=''ERROR'';', '1.14', 0, 1, '2022-01-20 15:00:00.000000',
        '2023-12-27 23:58:09.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (9, 'Variable', 'FLINK_OPTIONS', '', 'set table.exec.sink.upsert-materialize', e'由于分布式系统中 Shuffle 导致 ChangeLog 数据混乱，Sink 接收到的数据可能不是全局 upsert 的顺序。因此，在 upsert sink 之前添加 upsert materialize 运算符。它接收上游的变更日志记录并为下游生成一个 upsert 视图。默认情况下，当唯一键出现分布式无序时，会添加具体化操作符。您也可以选择不实现（NONE）或强制实现（FORCE）。
Possible values:
"NONE"
"FORCE"
"AUTO"', 'Set ''table.exec.sink.upsert-materialize''=''AUTO'';', '1.14', 0, 1, '2022-01-20 15:00:00.000000',
        '2023-12-27 23:58:09.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (10, 'Module', 'SQL_TEMPLATE', 'FlinkSql', 'create.table.kafka', 'kafka快速建表格式', e'CREATE TABLE Kafka_Table (
  `event_time` TIMESTAMP(3) METADATA FROM ''timestamp'',
  `partition` BIGINT METADATA VIRTUAL,
  `offset` BIGINT METADATA VIRTUAL,
  `user_id` BIGINT,
  `item_id` BIGINT,
  `behavior` STRING
) WITH (
  ''connector'' = ''kafka'',
  ''topic'' = ''user_behavior'',
  ''properties.bootstrap.servers'' = ''localhost:9092'',
  ''properties.group.id'' = ''testGroup'',
  ''scan.startup.mode'' = ''earliest-offset'',
  ''format'' = ''csv''
);
--可选: ''value.fields-include'' = ''ALL'',
--可选: ''json.ignore-parse-errors'' = ''true'',
--可选: ''key.fields-prefix'' = ''k_'',', '1.14', 0, 1, '2022-01-20 16:59:18.000000', '2023-12-28 00:02:57.000000',
        null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (11, 'Module', 'SQL_TEMPLATE', 'FlinkSql', 'create.table.doris', 'Doris快速建表', e'CREATE TABLE doris_table (
    cid INT,
    sid INT,
    name STRING,
    cls STRING,
    score INT,
    PRIMARY KEY (cid) NOT ENFORCED
) WITH (
''connector'' = ''doris'',
''fenodes'' = ''127.0.0.1:8030'' ,
''table.identifier'' = ''test.scoreinfo'',
''username'' = ''root'',
''password''=''''
);', '1.14', 0, 1, '2022-01-20 17:08:00.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (12, 'Module', 'SQL_TEMPLATE', 'FlinkSql', 'create.table.jdbc', 'JDBC建表语句', e'CREATE TABLE JDBC_table (
  id BIGINT,
  name STRING,
  age INT,
  status BOOLEAN,
  PRIMARY KEY (id) NOT ENFORCED
) WITH (
   ''connector'' = ''jdbc'',
   ''url'' = ''jdbc:mysql://localhost:3306/mydatabase'',
   ''table-name'' = ''users'',
   ''username'' = ''root'',
   ''password'' = ''123456''
);
--可选: ''sink.parallelism''=''1'',
--可选: ''lookup.cache.ttl''=''1000s'',', '1.14', 0, 1, '2022-01-20 17:15:26.000000', '2023-12-28 00:02:57.000000',
        null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (13, 'Module', 'SQL_TEMPLATE', 'FlinkSql', 'create.catalog.hive', '创建HIVE的catalog', 'CREATE CATALOG hive WITH (
    ''type'' = ''hive'',
    ''default-database'' = ''default'',
    ''hive-conf-dir'' = ''/app/wwwroot/MBDC/hive/conf/'', --hive配置文件
    ''hadoop-conf-dir''=''/app/wwwroot/MBDC/hadoop/etc/hadoop/'' --hadoop配置文件，配了环境变量则不需要。
);', '1.14', 0, 1, '2022-01-20 17:18:54.000000', '2023-12-28 00:03:53.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (14, 'Operator', 'SQL_TEMPLATE', 'FlinkSql', 'use.catalog.hive', '使用hive的catalog', 'USE CATALOG hive;',
        '1.14', 0, 1, '2022-01-20 17:22:53.000000', '2023-12-28 00:03:53.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (15, 'Operator', 'SQL_TEMPLATE', 'FlinkSql', 'use.catalog.default', '使用default的catalog',
        'USE CATALOG default_catalog;', '1.14', 0, 1, '2022-01-20 17:23:48.000000', '2023-12-28 00:03:53.000000', null,
        null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (16, 'Variable', 'FLINK_OPTIONS', '', 'set dialect.hive', '使用hive方言', 'Set table.sql-dialect=hive;', '1.14',
        0, 1, '2022-01-20 17:25:37.000000', '2023-12-28 00:04:44.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (17, 'Variable', 'FLINK_OPTIONS', '', 'set dialect.default', '使用default方言', 'Set table.sql-dialect=default;',
        '1.14', 0, 1, '2022-01-20 17:26:19.000000', '2023-12-28 00:04:44.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (39, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value IN (sub-query)', '如果value存在于子查询中，则返回TRUE。',
        '${1:} IN (${2:})', '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (40, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value NOT IN (sub-query)',
        '如果value不存在于子查询中，则返回TRUE。', '${1:} NOT IN (${2:})', '1.12', 0, 1, '2021-02-22 14:44:26.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (41, 'Function', 'FUN_UDF', 'LOGICAL_FUNCTION', 'boolean1 OR boolean2', e'如果BOOLEAN1为TRUE或BOOLEAN2为TRUE，则返回TRUE。支持三值逻辑。

例如，true || Null(Types.BOOLEAN)返回TRUE。', '${1:} OR ${2:}', '1.12', 0, 1, '2021-02-22 14:44:26.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (18, 'Module', 'SQL_TEMPLATE', 'FlinkSql', 'create.stream.table.hive', '创建流式HIVE表', e'CREATE CATALOG hive WITH ( --创建hive的catalog
    ''type'' = ''hive'',
    ''hive-conf-dir'' = ''/app/wwwroot/MBDC/hive/conf/'',
    ''hadoop-conf-dir''=''/app/wwwroot/MBDC/hadoop/etc/hadoop/''
);

USE CATALOG hive;
USE offline_db; --选择库
set table.sql-dialect=hive; --设置方言

CREATE TABLE hive_stream_table (
  user_id STRING,
  order_amount DOUBLE
) PARTITIONED BY (dt STRING, hr STRING) STORED AS parquet TBLPROPERTIES (
  ''partition.time-extractor.timestamp-pattern''=''$dt $hr:00:00'',
  ''sink.partition-commit.trigger''=''partition-time'',
  ''sink.partition-commit.delay''=''1min'',
  ''sink.semantic'' = ''exactly-once'',
  ''sink.rolling-policy.rollover-interval'' =''1min'',
  ''sink.rolling-policy.check-interval''=''1min'',
  ''sink.partition-commit.policy.kind''=''metastore,success-file''
);', '1.14', 0, 1, '2022-01-20 17:34:06.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (19, 'Module', 'SQL_TEMPLATE', 'FlinkSql', 'create.table.mysql_cdc', '创建Mysql_CDC表', e'CREATE TABLE mysql_cdc_table(
    cid INT,
    sid INT,
    cls STRING,
    score INT,
    PRIMARY KEY (cid) NOT ENFORCED
) WITH (
''connector'' = ''mysql-cdc'',
''hostname'' = ''127.0.0.1'',
''port'' = ''3306'',
''username'' = ''test'',
''password'' = ''123456'',
''database-name'' = ''test'',
''server-time-zone'' = ''UTC'',
''scan.incremental.snapshot.enabled'' = ''true'',
''debezium.snapshot.mode''=''latest-offset'' ,-- 或者key是scan.startup.mode，initial表示要历史数据，latest-offset表示不要历史数据
''debezium.datetime.format.date''=''yyyy-MM-dd'',
''debezium.datetime.format.time''=''HH-mm-ss'',
''debezium.datetime.format.datetime''=''yyyy-MM-dd HH-mm-ss'',
''debezium.datetime.format.timestamp''=''yyyy-MM-dd HH-mm-ss'',
''debezium.datetime.format.timestamp.zone''=''UTC+8'',
''table-name'' = ''mysql_cdc_table'');', '1.14', 0, 1, '2022-01-20 17:49:14.000000', '2023-12-28 00:02:57.000000', null,
        null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (20, 'Module', 'SQL_TEMPLATE', 'FlinkSql', 'create.table.hudi', '创建hudi表', e'CREATE TABLE hudi_table
(
    `goods_order_id`  bigint COMMENT ''id'',
    `goods_order_uid` string COMMENT ''goods order id'',
    `customer_uid`    string COMMENT ''customer id'',
    `customer_name`   string COMMENT ''customer name'',
    `create_time`     timestamp(3) COMMENT ''create_time'',
    `update_time`     timestamp(3) COMMENT ''update time'',
    `create_by`       string COMMENT ''create user uid'',
    `update_by`       string COMMENT ''update user uid'',
    PRIMARY KEY (goods_order_id) NOT ENFORCED
) COMMENT ''hudi_table''
WITH (
''connector'' = ''hudi'',
''path'' = ''hdfs://cluster1/data/bizdata/cdc/mysql/order/goods_order'', -- 路径会自动创建
''hoodie.datasource.write.recordkey.field'' = ''goods_order_id'', -- 主键
''write.precombine.field'' = ''update_time'', -- 相同的键值时，取此字段最大值，默认ts字段
''read.streaming.skip_compaction'' = ''true'', -- 避免重复消费问题
''write.bucket_assign.tasks'' = ''2'', -- 并发写的 bucekt 数
''write.tasks'' = ''2'',
''compaction.tasks'' = ''1'',
''write.operation'' = ''upsert'', -- UPSERT（插入更新）\\INSERT（插入）\\BULK_INSERT（批插入）（upsert性能会低些，不适合埋点上报）
''write.rate.limit'' = ''20000'', -- 限制每秒多少条
''table.type'' = ''COPY_ON_WRITE'', -- 默认COPY_ON_WRITE ，
''compaction.async.enabled'' = ''true'', -- 在线压缩
''compaction.trigger.strategy'' = ''num_or_time'', -- 按次数压缩
''compaction.delta_commits'' = ''20'', -- 默认为5
''compaction.delta_seconds'' = ''60'', -- 默认为1小时
''hive_sync.enable'' = ''true'', -- 启用hive同步
''hive_sync.mode'' = ''hms'', -- 启用hive hms同步，默认jdbc
''hive_sync.metastore.uris'' = ''thrift://cdh2.vision.com:9083'', -- required, metastore的端口
''hive_sync.jdbc_url'' = ''jdbc:hive2://cdh1.vision.com:10000'', -- required, hiveServer地址
''hive_sync.table'' = ''order_mysql_goods_order'', -- required, hive 新建的表名 会自动同步hudi的表结构和数据到hive
''hive_sync.db'' = ''cdc_ods'', -- required, hive 新建的数据库名
''hive_sync.username'' = ''hive'', -- required, HMS 用户名
''hive_sync.password'' = ''123456'', -- required, HMS 密码
''hive_sync.skip_ro_suffix'' = ''true'' -- 去除ro后缀
);', '1.14', 0, 1, '2022-01-20 17:56:50.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (21, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value1 <> value2',
        '如果value1不等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} <> ${2:}', '1.12', 4, 1,
        '2021-02-22 10:05:38.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (22, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value1 > value2',
        '如果value1大于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} > ${2:}', '1.12', 2, 1,
        '2021-02-22 14:37:58.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (23, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value1 >= value2',
        '如果value1大于或等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} >= ${2:}', '1.12', 2, 1,
        '2021-02-22 14:38:52.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (24, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value1 < value2',
        '如果value1小于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} < ${2:}', '1.12', 0, 1,
        '2021-02-22 14:39:15.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (25, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value1 <= value2',
        '如果value1小于或等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} <=   ${2:}', '1.12', 0,
        1, '2021-02-22 14:39:40.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (26, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value IS NULL', '如果value为NULL，则返回TRUE 。', '${1:} IS NULL',
        '1.12', 2, 1, '2021-02-22 14:40:39.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (27, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value IS NOT NULL', '如果value不为NULL，则返回TRUE 。',
        '${1:}  IS NOT NULL', '1.12', 0, 1, '2021-02-22 14:41:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (28, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value1 IS DISTINCT FROM value2',
        '如果两个值不相等则返回TRUE。NULL值在这里被视为相同的值。', '${1:} IS DISTINCT FROM ${2:}', '1.12', 0, 1,
        '2021-02-22 14:42:39.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (29, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value1 IS NOT DISTINCT FROM value2',
        '如果两个值相等则返回TRUE。NULL值在这里被视为相同的值。', '${1:} IS NOT DISTINCT FROM ${2:}', '1.12', 0, 1,
        '2021-02-22 14:43:23.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (30, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value1 BETWEEN [ ASYMMETRIC | SYMMETRIC ] value2 AND value3',
        '如果value1大于或等于value2和小于或等于value3 返回true', '${1:} BETWEEN ${2:} AND ${3:}', '1.12', 0, 1,
        '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (31, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION',
        'value1 NOT BETWEEN [ ASYMMETRIC | SYMMETRIC ] value2 AND value3', '如果value1小于value2或大于value3 返回true',
        '${1:} NOT BETWEEN ${2:} AND ${3:}', '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000',
        null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (32, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'string1 LIKE string2 [ ESCAPE char ]',
        '如果STRING1匹配模式STRING2，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。', '${1:} LIKE ${2:}', '1.12',
        0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (33, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'string1 NOT LIKE string2 [ ESCAPE char ]',
        '如果STRING1不匹配模式STRING2，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。', '${1:} NOT LIKE ${2:}',
        '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (34, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'string1 SIMILAR TO string2 [ ESCAPE char ]',
        '如果STRING1与SQL正则表达式STRING2匹配，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。',
        '${1:} SIMILAR TO ${2:}', '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (35, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'string1 NOT SIMILAR TO string2 [ ESCAPE char ]',
        '如果STRING1与SQL正则表达式STRING2不匹配，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。',
        '${1:} NOT SIMILAR TO ${2:}', '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null,
        null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (36, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value1 IN (value2 [, value3]* )', e'如果value1存在于给定列表（value2，value3，...）中，则返回TRUE 。

当（value2，value3，...）包含NULL，如果可以找到该元素，则返回TRUE，否则返回UNKNOWN。

如果value1为NULL，则始终返回UNKNOWN 。', '${1:} IN (${2:} )', '1.12', 0, 1, '2021-02-22 14:44:26.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (37, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value1 NOT IN (value2 [, value3]* )', e'如果value1不存在于给定列表（value2，value3，...）中，则返回TRUE 。

当（value2，value3，...）包含NULL，如果可以找到该元素，则返回TRUE，否则返回UNKNOWN。

如果value1为NULL，则始终返回UNKNOWN 。', '${1:} NOT IN (${2:})', '1.12', 0, 1, '2021-02-22 14:44:26.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (38, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'EXISTS (sub-query)', '如果value存在于子查询中，则返回TRUE。',
        'EXISTS (${1:})', '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (42, 'Function', 'FUN_UDF', 'LOGICAL_FUNCTION', 'boolean1 AND boolean2', e'如果BOOLEAN1和BOOLEAN2均为TRUE，则返回TRUE。支持三值逻辑。

例如，true && Null(Types.BOOLEAN)返回未知。', '${1:} AND ${2:}', '1.12', 0, 1, '2021-02-22 14:44:26.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (43, 'Function', 'FUN_UDF', 'LOGICAL_FUNCTION', 'NOT boolean', e'如果BOOLEAN为FALSE，则返回TRUE ；如果BOOLEAN为TRUE，则返回FALSE 。

如果BOOLEAN为UNKNOWN，则返回UNKNOWN。', 'NOT ${1:} ', '1.12', 0, 1, '2021-02-22 14:44:26.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (44, 'Function', 'FUN_UDF', 'LOGICAL_FUNCTION', 'boolean IS FALSE',
        '如果BOOLEAN为FALSE，则返回TRUE ；如果BOOLEAN为TRUE或UNKNOWN，则返回FALSE 。', '${1:}  IS FALSE', '1.12', 0, 1,
        '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (45, 'Function', 'FUN_UDF', 'LOGICAL_FUNCTION', 'boolean IS NOT FALSE',
        '如果BOOLEAN为TRUE或UNKNOWN，则返回TRUE ；如果BOOLEAN为FALSE，则返回FALSE。', '${1:}  IS NOT FALSE', '1.12', 0, 1,
        '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (46, 'Function', 'FUN_UDF', 'LOGICAL_FUNCTION', 'boolean IS TRUE',
        '如果BOOLEAN为TRUE，则返回TRUE；如果BOOLEAN为FALSE或UNKNOWN，则返回FALSE 。', '${1:}  IS TRUE', '1.12', 0, 1,
        '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (47, 'Function', 'FUN_UDF', 'LOGICAL_FUNCTION', 'boolean IS NOT TRUE',
        '如果BOOLEAN为FALSE或UNKNOWN，则返回TRUE ；如果BOOLEAN为TRUE，则返回FALSE 。', '${1:}  IS NOT TRUE', '1.12', 0, 1,
        '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (48, 'Function', 'FUN_UDF', 'LOGICAL_FUNCTION', 'boolean IS UNKNOWN',
        '如果BOOLEAN为UNKNOWN，则返回TRUE ；如果BOOLEAN为TRUE或FALSE，则返回FALSE 。', '${1:}  IS UNKNOWN', '1.12', 0, 1,
        '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (49, 'Function', 'FUN_UDF', 'LOGICAL_FUNCTION', 'boolean IS NOT UNKNOWN',
        '如果BOOLEAN为TRUE或FALSE，则返回TRUE ；如果BOOLEAN为UNKNOWN，则返回FALSE 。', '${1:}  IS NOT UNKNOWN', '1.12', 0,
        1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (50, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', '+ numeric', '返回NUMERIC。', '+ ${1:} ', '1.12', 0, 1,
        '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (51, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', '- numeric', '返回负数NUMERIC。', '- ${1:} ', '1.12', 0, 1,
        '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (52, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'numeric1 + numeric2', '返回NUMERIC1加NUMERIC2。',
        '${1:}  + ${2:} ', '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (53, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'numeric1 - numeric2', '返回NUMERIC1减去NUMERIC2。',
        '${1:}  - ${2:} ', '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (54, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'numeric1 * numeric2', '返回NUMERIC1乘以NUMERIC2。',
        '${1:} * ${2:} ', '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (55, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'numeric1 / numeric2', '返回NUMERIC1除以NUMERIC2。',
        '${1:}  / ${2:} ', '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (56, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'numeric1 % numeric2',
        '返回NUMERIC1除以NUMERIC2的余数（模）。仅当numeric1为负数时，结果为负数。', '${1:}  % ${2:} ', '1.12', 0, 1,
        '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (57, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'POWER(numeric1, numeric2)', '返回NUMERIC1的NUMERIC2 次幂。',
        'POWER(${1:} , ${2:})', '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (58, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'ABS(numeric)', '返回NUMERIC的绝对值。', 'ABS(${1:})', '1.12',
        0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (59, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'MOD(numeric1, numeric2)',
        '返回numeric1除以numeric2的余数(模)。只有当numeric1为负数时，结果才为负数', 'MOD(${1:} , ${2:} )', '1.12', 0, 1,
        '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (60, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'SQRT(numeric)', '返回NUMERIC的平方根。', 'SQRT(${1:})',
        '1.12', 0, 1, '2021-02-22 14:44:26.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (61, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'LN(numeric)', '返回NUMERIC的自然对数（以e为底）。',
        'LN(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (62, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'LOG10(numeric)', '返回NUMERIC的以10为底的对数。',
        'LOG10(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (63, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'LOG2(numeric)', '返回NUMERIC的以2为底的对数。',
        'LOG2(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (64, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'EXP(numeric)', '返回e 的 NUMERIC 次幂。', 'EXP(${1:})',
        '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (65, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'FLOOR(numeric)',
        '向下舍入NUMERIC，并返回小于或等于NUMERIC的最大整数。', 'FLOOR(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (66, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'SIN(numeric)', '返回NUMERIC的正弦值。', 'SIN(${1:})', '1.12',
        0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (67, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'SINH(numeric)', e'返回NUMERIC的双曲正弦值。

返回类型为DOUBLE。', 'SINH(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null,
        null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (68, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'COS(numeric)', '返回NUMERIC的余弦值。', 'COS(${1:})', '1.12',
        0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (69, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'TAN(numeric)', '返回NUMERIC的正切。', 'TAN(${1:})', '1.12',
        0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (70, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'TANH(numeric)', e'返回NUMERIC的双曲正切值。

返回类型为DOUBLE。', 'TANH(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null,
        null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (71, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'COT(numeric)', '返回NUMERIC的余切。', 'COT(${1:})', '1.12',
        0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (72, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'ASIN(numeric)', '返回NUMERIC的反正弦值。', 'ASIN(${1:})',
        '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (73, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'ACOS(numeric)', '返回NUMERIC的反余弦值。', 'ACOS(${1:})',
        '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (74, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'ATAN(numeric)', '返回NUMERIC的反正切。', 'ATAN(${1:})',
        '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (75, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'ATAN2(numeric1, numeric2)',
        '返回坐标的反正切（NUMERIC1，NUMERIC2）。', 'ATAN2(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (76, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'COSH(numeric)', e'返回NUMERIC的双曲余弦值。

返回值类型为DOUBLE。', 'COSH(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null,
        null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (77, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'DEGREES(numeric)', '返回弧度NUMERIC的度数表示形式',
        'DEGREES(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (78, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'RADIANS(numeric)', '返回度数NUMERIC的弧度表示。',
        'RADIANS(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (79, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'SIGN(numeric)', '返回NUMERIC的符号。', 'SIGN(${1:})', '1.12',
        0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (80, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'ROUND(numeric, integer)',
        '返回一个数字，四舍五入为NUMERIC的INT小数位。', 'ROUND(${1:} , ${2:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (81, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'PI', '返回一个比任何其他值都更接近圆周率的值。', 'PI',
        '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (82, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'E()', '返回一个比任何其他值都更接近e的值。', 'E()', '1.12',
        0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (142, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'YEAR(date)',
        '返回SQL date日期的年份。等价于EXTRACT(YEAR FROM date)。', 'YEAR(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (83, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'RAND()', '返回介于0.0（含）和1.0（不含）之间的伪随机双精度值。',
        'RAND()', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (84, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'RAND(integer)', e'返回带有初始种子INTEGER的介于0.0（含）和1.0（不含）之间的伪随机双精度值。

如果两个RAND函数具有相同的初始种子，它们将返回相同的数字序列。', 'RAND(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (85, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'RAND_INTEGER(integer)',
        '返回介于0（含）和INTEGER（不含）之间的伪随机整数值。', 'RAND_INTEGER(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (86, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'RAND_INTEGER(integer1, integer2)', e'返回介于0（含）和INTEGER2（不含）之间的伪随机整数值，其初始种子为INTEGER1。

如果两个randInteger函数具有相同的初始种子和边界，它们将返回相同的数字序列。', 'RAND_INTEGER(${1:} , ${2:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (87, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'UUID()', e'根据RFC 4122 type 4（伪随机生成）UUID返回UUID（通用唯一标识符）字符串

（例如，“ 3d3c68f7-f608-473f-b60c-b0c44ad4cc4e”）。使用加密强度高的伪随机数生成器生成UUID。', 'UUID()', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (88, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'BIN(integer)', e'以二进制格式返回INTEGER的字符串表示形式。如果INTEGER为NULL，则返回NULL。

例如，4.bin()返回“ 100”并12.bin()返回“ 1100”。', 'BIN(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (89, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', e'HEX(numeric)
HEX(string)', e'以十六进制格式返回整数NUMERIC值或STRING的字符串表示形式。如果参数为NULL，则返回NULL。

例如，数字20导致“ 14”，数字100导致“ 64”，字符串“ hello，world”导致“ 68656C6C6F2C776F726C64”。', 'HEX(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (90, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'TRUNCATE(numeric1, integer2)',
        '返回一个小数点后被截断为integer2位的数字。', 'TRUNCATE(${1:}, ${2:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (91, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'PI()', '返回π (pi)的值。仅在blink planner中支持。', 'PI()',
        '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (92, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'LOG(numeric1)', e'如果不带参数调用，则返回NUMERIC1的自然对数。当使用参数调用时，将NUMERIC1的对数返回到基数NUMERIC2。

注意：当前，NUMERIC1必须大于0，而NUMERIC2必须大于1。', 'LOG(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (93, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'LOG(numeric1, numeric2)', e'如果不带参数调用，则返回NUMERIC1的自然对数。当使用参数调用时，将NUMERIC1的对数返回到基数NUMERIC2。

注意：当前，NUMERIC1必须大于0，而NUMERIC2必须大于1。', 'LOG(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (94, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'CEIL(numeric)',
        '将NUMERIC向上舍入，并返回大于或等于NUMERIC的最小整数。', 'CEIL(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (95, 'Function', 'FUN_UDF', 'ARITHMETIC_FUNCTIONS', 'CEILING(numeric)',
        '将NUMERIC向上舍入，并返回大于或等于NUMERIC的最小整数。', 'CEILING(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (96, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'string1 || string2', '返回string1和string2的连接。',
        '${1:} || ${2:}', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (97, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'UPPER(string)', '以大写形式返回STRING。', 'UPPER(${1:})', '1.12',
        0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (98, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'LOWER(string)', '以小写形式返回STRING。', 'LOWER(${1:})', '1.12',
        0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (99, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'POSITION(string1 IN string2)', e'返回STRING1在STRING2中第一次出现的位置（从1开始）；

如果在STRING2中找不到STRING1，则返回0 。', 'POSITION(${1:} IN ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (100, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'TRIM([ BOTH | LEADING | TRAILING ] string1 FROM string2)',
        '返回一个字符串，该字符串从STRING中删除前导和/或结尾字符。', 'TRIM(${1:} FROM ${2:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (101, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'LTRIM(string)', e'返回一个字符串，该字符串从STRING除去左空格。

例如，" This is a test String.".ltrim()返回“This is a test String.”。', 'LTRIM(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (102, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'RTRIM(string)', e'返回一个字符串，该字符串从STRING中删除正确的空格。

例如，"This is a test String. ".rtrim()返回“This is a test String.”。', 'RTRIM(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (103, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'REPEAT(string, integer)', e'返回一个字符串，该字符串重复基本STRING INT次。

例如，"This is a test String.".repeat(2)返回“This is a test String.This is a test String.”。', 'REPEAT(${1:}, ${2:})',
        '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (104, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'REGEXP_REPLACE(string1, string2, string3)', e'返回字符串STRING1所有匹配正则表达式的子串STRING2连续被替换STRING3。

例如，"foobar".regexpReplace("oo|ar", "")返回“ fb”。', 'REGEXP_REPLACE(${1:} , ${2:} , ${3:} )', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (105, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS',
        'OVERLAY(string1 PLACING string2 FROM integer1 [ FOR integer2 ])',
        '从位置INT1返回一个字符串，该字符串将STRING1的INT2（默认为STRING2的长度）字符替换为STRING2',
        'OVERLAY(${1:} PLACING ${2:} FROM ${3:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000',
        '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (106, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'SUBSTRING(string FROM integer1 [ FOR integer2 ])',
        '返回字符串STRING的子字符串，从位置INT1开始，长度为INT2（默认为结尾）。', 'SUBSTRING${1:} FROM ${2:} )', '1.12', 0,
        1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (107, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'REPLACE(string1, string2, string3)',
        '返回一个新字符串替换其中出现的所有STRING2与STRING3（非重叠）从STRING1。', 'REPLACE(${1:} , ${2:} , ${3:} )',
        '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (108, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'REGEXP_EXTRACT(string1, string2[, integer])',
        '从STRING1返回一个字符串，该字符串使用指定的正则表达式STRING2和正则表达式匹配组索引INTEGER1提取。',
        'REGEXP_EXTRACT(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null,
        null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (109, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'INITCAP(string)',
        '返回一种新形式的STRING，其中每个单词的第一个字符转换为大写，其余字符转换为小写。', 'INITCAP(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (110, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'CONCAT(string1, string2,...)',
        '返回连接STRING1，STRING2，...的字符串。如果任何参数为NULL，则返回NULL。', 'CONCAT(${1:} , ${2:} , ${3:} )', '1.12',
        0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (111, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'CONCAT_WS(string1, string2, string3,...)',
        '返回一个字符串，会连接STRING2，STRING3，......与分离STRING1。', 'CONCAT_WS(${1:} , ${2:} , ${3:} )', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (112, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'LPAD(string1, integer, string2)',
        '返回一个新字符串，该字符串从STRING1的左侧填充STRING2，长度为INT个字符。', 'LPAD(${1:} , ${2:} , ${3:} )', '1.12',
        0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (113, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'RPAD(string1, integer, string2)',
        '返回一个新字符串，该字符串从STRING1右侧填充STRING2，长度为INT个字符。', 'RPAD(${1:} , ${2:} , ${3:} )', '1.12', 0,
        1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (114, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'FROM_BASE64(string)',
        '返回来自STRING的base64解码结果；如果STRING为NULL，则返回null 。', 'FROM_BASE64(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (115, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'TO_BASE64(string)',
        '从STRING返回base64编码的结果；如果STRING为NULL，则返回NULL。', 'TO_BASE64(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (116, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'ASCII(string)',
        '返回字符串的第一个字符的数值。如果字符串为NULL，则返回NULL。仅在blink planner中支持。', 'ASCII(${1:})', '1.12', 0,
        1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (117, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'CHR(integer)',
        '返回与integer在二进制上等价的ASCII字符。如果integer大于255，我们将首先得到integer的模数除以255，并返回模数的CHR。如果integer为NULL，则返回NULL。仅在blink planner中支持。',
        'CHR(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (118, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'DECODE(binary, string)',
        '使用提供的字符集(''US-ASCII''， ''ISO-8859-1''， ''UTF-8''， ''UTF-16BE''， ''UTF-16LE''， ''UTF-16''之一)将第一个参数解码为字符串。如果任意一个参数为空，结果也将为空。仅在blink planner中支持。',
        'DECODE(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (119, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'ENCODE(string1, string2)',
        '使用提供的string2字符集(''US-ASCII''， ''ISO-8859-1''， ''UTF-8''， ''UTF-16BE''， ''UTF-16LE''， ''UTF-16''之一)将string1编码为二进制。如果任意一个参数为空，结果也将为空。仅在blink planner中支持。',
        'ENCODE(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (120, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'INSTR(string1, string2)',
        '返回string2在string1中第一次出现的位置。如果任何参数为空，则返回NULL。仅在blink planner中支持。',
        'INSTR(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (121, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'LEFT(string, integer)',
        '返回字符串中最左边的整数字符。如果整数为负，则返回空字符串。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。',
        'LEFT(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (122, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'RIGHT(string, integer)',
        '返回字符串中最右边的整数字符。如果整数为负，则返回空字符串。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。',
        'RIGHT(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (123, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'LOCATE(string1, string2[, integer])',
        '返回string1在string2中的位置整数之后第一次出现的位置。如果没有找到，返回0。如果任何参数为NULL，则返回NULL仅在blink planner中支持。',
        'LOCATE(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (124, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'PARSE_URL(string1, string2[, string3])',
        '从URL返回指定的部分。string2的有效值包括''HOST''， ''PATH''， ''QUERY''， ''REF''， ''PROTOCOL''， ''AUTHORITY''， ''FILE''和''USERINFO''。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。',
        'PARSE_URL(${1:} , ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null,
        null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (125, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'REGEXP(string1, string2)',
        '如果string1的任何子字符串(可能为空)与Java正则表达式string2匹配，则返回TRUE，否则返回FALSE。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。',
        'REGEXP(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (126, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'REVERSE(string)',
        '返回反向字符串。如果字符串为NULL，则返回NULL仅在blink planner中支持。', 'REVERSE(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (127, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'SPLIT_INDEX(string1, string2, integer1)',
        '通过分隔符string2拆分string1，返回拆分字符串的整数(从零开始)字符串。如果整数为负，返回NULL。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。',
        'SPLIT_INDEX(${1:}, ${2:} , ${3:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000',
        null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (128, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'STR_TO_MAP(string1[, string2, string3]])',
        '使用分隔符将string1分割成键/值对后返回一个映射。string2是pair分隔符，默认为''，''。string3是键值分隔符，默认为''=''。仅在blink planner中支持。',
        'STR_TO_MAP(${1:})', '1.12', 4, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (129, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'SUBSTR(string[, integer1[, integer2]])',
        '返回一个字符串的子字符串，从位置integer1开始，长度为integer2(默认到末尾)。仅在blink planner中支持。',
        'SUBSTR(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (130, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'CHAR_LENGTH(string)', '返回STRING中的字符数。',
        'CHAR_LENGTH(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (131, 'Function', 'FUN_UDF', 'STRING_FUNCTIONS', 'CHARACTER_LENGTH(string)', '返回STRING中的字符数。',
        'CHARACTER_LENGTH(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:58.000000', null,
        null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (132, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'DATE string', '返回以“ yyyy-MM-dd”形式从STRING解析的SQL日期。',
        'DATE(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (133, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'TIME string', '返回以“ HH：mm：ss”的形式从STRING解析的SQL时间。',
        'TIME(${1:})', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (134, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'TIMESTAMP string',
        '返回从STRING解析的SQL时间戳，格式为“ yyyy-MM-dd HH：mm：ss [.SSS]”', 'TIMESTAMP(${1:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (135, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'INTERVAL string range',
        '解析“dd hh:mm:ss”形式的区间字符串。fff表示毫秒间隔，yyyy-mm表示月间隔。间隔范围可以是天、分钟、天到小时或天到秒，以毫秒为间隔;年或年到月的间隔。',
        'INTERVAL ${1:} range', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (136, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'CURRENT_DATE', '返回UTC时区中的当前SQL日期。', 'CURRENT_DATE',
        '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (137, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'CURRENT_TIME', '返回UTC时区的当前SQL时间。', 'CURRENT_TIME',
        '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (138, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'CURRENT_TIMESTAMP', '返回UTC时区内的当前SQL时间戳。',
        'CURRENT_TIMESTAMP', '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (139, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'LOCALTIME', '返回本地时区的当前SQL时间。', 'LOCALTIME', '1.12', 0,
        1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (140, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'LOCALTIMESTAMP', '返回本地时区的当前SQL时间戳。', 'LOCALTIMESTAMP',
        '1.12', 0, 1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (141, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'EXTRACT(timeintervalunit FROM temporal)',
        '返回从时域的timeintervalunit部分提取的长值。', 'EXTRACT(${1:} FROM ${2:})', '1.12', 0, 1,
        '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (143, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'QUARTER(date)',
        '从SQL date date返回一年中的季度(1到4之间的整数)。相当于EXTRACT(从日期起四分之一)。', 'QUARTER(${1:})', '1.12', 0,
        1, '2021-02-22 15:29:35.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (144, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'MONTH(date)',
        '返回SQL date date中的某月(1到12之间的整数)。等价于EXTRACT(MONTH FROM date)。', 'MONTH(${1:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (145, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'WEEK(date)',
        '从SQL date date返回一年中的某个星期(1到53之间的整数)。相当于EXTRACT(从日期开始的星期)。', 'WEEK(${1:})', '1.12',
        0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (146, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'DAYOFYEAR(date)',
        '返回SQL date date中的某一天(1到366之间的整数)。相当于EXTRACT(DOY FROM date)。', 'DAYOFYEAR(${1:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (147, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'DAYOFMONTH(date)',
        '从SQL date date返回一个月的哪一天(1到31之间的整数)。相当于EXTRACT(DAY FROM date)。', 'DAYOFMONTH(${1:})', '1.12',
        0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (148, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'DAYOFWEEK(date)',
        '返回星期几(1到7之间的整数;星期日= 1)从SQL日期日期。相当于提取(道指从日期)。', 'DAYOFWEEK(${1:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (149, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'HOUR(timestamp)',
        '从SQL timestamp timestamp返回一天中的小时(0到23之间的整数)。相当于EXTRACT(HOUR FROM timestamp)。', 'HOUR(${1:})',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (150, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'MINUTE(timestamp)',
        '从SQL timestamp timestamp返回一小时的分钟(0到59之间的整数)。相当于EXTRACT(分钟从时间戳)。', 'MINUTE(${1:})',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (151, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'SECOND(timestamp)',
        '从SQL时间戳返回一分钟中的秒(0到59之间的整数)。等价于EXTRACT(从时间戳开始倒数第二)。', 'SECOND(${1:})', '1.12', 0,
        1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (152, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'FLOOR(timepoint TO timeintervalunit)',
        '返回一个将timepoint舍入到时间单位timeintervalunit的值。', 'FLOOR(${1:} TO ${2:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (153, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'CEIL(timepoint TO timeintervalunit)',
        '返回一个将timepoint舍入到时间单位timeintervalunit的值。', 'CEIL(${1:} TO ${2:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (154, 'Function', 'FUN_UDF', 'TIME_FUNCTION', '(timepoint1, temporal1) OVERLAPS (timepoint2, temporal2)',
        '如果(timepoint1, temporal1)和(timepoint2, temporal2)定义的两个时间间隔重叠，则返回TRUE。时间值可以是时间点或时间间隔。',
        '(${1:} , ${1:}) OVERLAPS (${2:} , ${2:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000',
        '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (155, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'DATE_FORMAT(timestamp, string)',
        '注意这个功能有严重的错误，现在不应该使用。请实现一个自定义的UDF，或者使用EXTRACT作为解决方案。',
        'DATE_FORMAT(${1:}, ''yyyy-MM-dd'')', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000',
        null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (156, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'TIMESTAMPADD(timeintervalunit, interval, timepoint)',
        '返回一个新的时间值，该值将一个(带符号的)整数间隔添加到时间点。间隔的单位由unit参数给出，它应该是以下值之一:秒、分、小时、日、周、月、季度或年。',
        'TIMESTAMPADD(${1:} , ${2:} , ${3:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000',
        null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (157, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'TIMESTAMPDIFF(timepointunit, timepoint1, timepoint2)',
        '返回timepointunit在timepoint1和timepoint2之间的(带符号)数。间隔的单位由第一个参数给出，它应该是以下值之一:秒、分、小时、日、月或年。',
        'TIMESTAMPDIFF(${1:} , ${2:} , ${3:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000',
        '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (158, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'CONVERT_TZ(string1, string2, string3)',
        '将时区string2中的datetime string1(默认ISO时间戳格式''yyyy-MM-dd HH:mm:ss'')转换为时区string3。时区的格式可以是缩写，如“PST”;可以是全名，如“America/Los_Angeles”;或者是自定义ID，如“GMT-8:00”。仅在blink planner中支持。',
        'CONVERT_TZ(${1:} , ${2:} , ${3:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000',
        null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (159, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'FROM_UNIXTIME(numeric[, string])',
        '以字符串格式返回数值参数的表示形式(默认为''yyyy-MM-dd HH:mm:ss'')。numeric是一个内部时间戳值，表示从UTC ''1970-01-01 00:00:00''开始的秒数，例如UNIX_TIMESTAMP()函数生成的时间戳。返回值用会话时区表示(在TableConfig中指定)。仅在blink planner中支持。',
        'FROM_UNIXTIME(${1:} )', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (160, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'UNIX_TIMESTAMP()',
        '获取当前Unix时间戳(以秒为单位)。仅在blink planner中支持。', 'UNIX_TIMESTAMP()', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (161, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'UNIX_TIMESTAMP(string1[, string2])',
        '转换日期时间字符串string1，格式为string2(缺省为yyyy-MM-dd HH:mm:ss，如果没有指定)为Unix时间戳(以秒为单位)，使用表配置中指定的时区。仅在blink planner中支持。',
        'UNIX_TIMESTAMP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (162, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'TO_DATE(string1[, string2])',
        '将格式为string2的日期字符串string1(默认为''yyyy-MM-dd'')转换为日期。仅在blink planner中支持。', 'TO_DATE(${1:})',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (163, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'TO_TIMESTAMP(string1[, string2])',
        '将会话时区(由TableConfig指定)下的日期时间字符串string1转换为时间戳，格式为string2(默认为''yyyy-MM-dd HH:mm:ss'')。仅在blink planner中支持。',
        'TO_TIMESTAMP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (164, 'Function', 'FUN_UDF', 'TIME_FUNCTION', 'NOW()', '返回UTC时区内的当前SQL时间戳。仅在blink planner中支持。',
        'NOW()', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (165, 'Function', 'FUN_UDF', 'CONDITIONAL_FUNCTION', e'CASE value
WHEN value1_1 [, value1_2 ]* THEN result1
[ WHEN value2_1 [, value2_2 ]* THEN result2 ]*
[ ELSE resultZ ]
END', '当第一个时间值包含在(valueX_1, valueX_2，…)中时，返回resultX。如果没有匹配的值，则返回resultZ，否则返回NULL。', e'CASE ${1:}
  WHEN ${2:}  THEN ${3:}
 ELSE ${4:}
END AS ${5:}', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (166, 'Function', 'FUN_UDF', 'CONDITIONAL_FUNCTION', e'CASE
WHEN condition1 THEN result1
[ WHEN condition2 THEN result2 ]*
[ ELSE resultZ ]
END', '当第一个条件满足时返回resultX。当不满足任何条件时，如果提供了resultZ则返回resultZ，否则返回NULL。', e'CASE WHEN ${1:} THEN ${2:}
   ELSE ${3:}
END AS ${4:}', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (167, 'Function', 'FUN_UDF', 'CONDITIONAL_FUNCTION', 'NULLIF(value1, value2)',
        '如果value1等于value2，则返回NULL;否则返回value1。', 'NULLIF(${1:}, ${2:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (168, 'Function', 'FUN_UDF', 'CONDITIONAL_FUNCTION', 'COALESCE(value1, value2 [, value3 ]* )',
        '返回value1, value2， ....中的第一个非空值', 'COALESCE(${1:} )', '1.12', 0, 1, '2021-02-22 15:46:48.000000',
        '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (169, 'Function', 'FUN_UDF', 'CONDITIONAL_FUNCTION', 'IF(condition, true_value, false_value)',
        '如果条件满足则返回true值，否则返回false值。仅在blink planner中支持。', 'IF((${1:}, ${2:}, ${3:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (170, 'Function', 'FUN_UDF', 'CONDITIONAL_FUNCTION', 'IS_ALPHA(string)',
        '如果字符串中所有字符都是字母则返回true，否则返回false。仅在blink planner中支持。', 'IS_ALPHA(${1:})', '1.12', 0,
        1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (171, 'Function', 'FUN_UDF', 'CONDITIONAL_FUNCTION', 'IS_DECIMAL(string)',
        '如果字符串可以被解析为有效的数字则返回true，否则返回false。仅在blink planner中支持。', 'IS_DECIMAL(${1:})',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (172, 'Function', 'FUN_UDF', 'CONDITIONAL_FUNCTION', 'IS_DIGIT(string)',
        '如果字符串中所有字符都是数字则返回true，否则返回false。仅在blink planner中支持。', 'IS_DIGIT(${1:})', '1.12', 0,
        1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (173, 'Function', 'FUN_UDF', 'TYPE_CONVER_FUNCTION', 'CAST(value AS type)', '返回一个要转换为type类型的新值。',
        'CAST(${1:} AS ${2:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (174, 'Function', 'FUN_UDF', 'COLLECTION_FUNCTION', 'CARDINALITY(array)', '返回数组中元素的数量。',
        'CARDINALITY(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (175, 'Function', 'FUN_UDF', 'COLLECTION_FUNCTION', 'array ‘[’ integer ‘]’',
        '返回数组中位于整数位置的元素。索引从1开始。', 'array[${1:}]', '1.12', 0, 1, '2021-02-22 15:46:48.000000',
        '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (176, 'Function', 'FUN_UDF', 'COLLECTION_FUNCTION', 'ELEMENT(array)',
        '返回数组的唯一元素(其基数应为1);如果数组为空，则返回NULL。如果数组有多个元素，则抛出异常。', 'ELEMENT(${1:})',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (177, 'Function', 'FUN_UDF', 'COLLECTION_FUNCTION', 'CARDINALITY(map)', '返回map中的条目数。',
        'CARDINALITY(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (178, 'Function', 'FUN_UDF', 'COLLECTION_FUNCTION', 'map ‘[’ value ‘]’', '返回map中key value指定的值。',
        'map[${1:}]', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (179, 'Function', 'FUN_UDF', 'VALUE_CONSTRUCTION_FUNCTION', 'ARRAY ‘[’ value1 [, value2 ]* ‘]’',
        '返回一个由一系列值(value1, value2，…)创建的数组。', 'ARRAY[ ${1:} ]', '1.12', 0, 1, '2021-02-22 15:46:48.000000',
        '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (180, 'Function', 'FUN_UDF', 'VALUE_CONSTRUCTION_FUNCTION', 'MAP ‘[’ value1, value2 [, value3, value4 ]* ‘]’',
        '返回一个从键值对列表((value1, value2)， (value3, value4)，…)创建的映射。', 'MAP[ ${1:} ]', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (181, 'Function', 'FUN_UDF', 'VALUE_CONSTRUCTION_FUNCTION', e'implicit constructor with parenthesis
(value1 [, value2]*)', '返回从值列表(value1, value2，…)创建的行。', '(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000',
        '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (182, 'Function', 'FUN_UDF', 'VALUE_CONSTRUCTION_FUNCTION', e'explicit ROW constructor
ROW(value1 [, value2]*)', '返回从值列表(value1, value2，…)创建的行。', 'ROW(${1:}) ', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (183, 'Function', 'FUN_UDF', 'VALUE_ACCESS_FUNCTION', 'tableName.compositeType.field',
        '按名称从Flink复合类型(例如，Tuple, POJO)中返回一个字段的值。', 'tableName.compositeType.field', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (184, 'Function', 'FUN_UDF', 'VALUE_ACCESS_FUNCTION', 'tableName.compositeType.*',
        '返回Flink复合类型(例如，Tuple, POJO)的平面表示，它将每个直接子类型转换为一个单独的字段。在大多数情况下，平面表示的字段的名称与原始字段类似，但使用了$分隔符(例如，mypojo$mytuple$f0)。',
        'tableName.compositeType.*', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:08:59.000000', null,
        null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (185, 'Function', 'FUN_UDF', 'GROUP_FUNCTION', 'GROUP_ID()', '返回唯一标识分组键组合的整数', 'GROUP_ID()',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (186, 'Function', 'FUN_UDF', 'GROUP_FUNCTION', e'GROUPING(expression1 [, expression2]* )
GROUPING_ID(expression1 [, expression2]* )', '返回给定分组表达式的位向量。', 'GROUPING(${1:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (187, 'Function', 'FUN_UDF', 'HASH_FUNCTION', 'MD5(string)',
        '以32位十六进制数字的字符串形式返回string的MD5哈希值;如果字符串为NULL，则返回NULL。', 'MD5(${1:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (188, 'Function', 'FUN_UDF', 'HASH_FUNCTION', 'SHA1(string)',
        '返回字符串的SHA-1散列，作为一个由40个十六进制数字组成的字符串;如果字符串为NULL，则返回NULL', 'SHA1(${1:})',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (189, 'Function', 'FUN_UDF', 'HASH_FUNCTION', 'SHA224(string)',
        '以56位十六进制数字的字符串形式返回字符串的SHA-224散列;如果字符串为NULL，则返回NULL。', 'SHA224(${1:})', '1.12',
        0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (190, 'Function', 'FUN_UDF', 'HASH_FUNCTION', 'SHA256(string)',
        '以64位十六进制数字的字符串形式返回字符串的SHA-256散列;如果字符串为NULL，则返回NULL。', 'SHA256(${1:})', '1.12',
        0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (191, 'Function', 'FUN_UDF', 'HASH_FUNCTION', 'SHA384(string)',
        '以96个十六进制数字的字符串形式返回string的SHA-384散列;如果字符串为NULL，则返回NULL。', 'SHA384(${1:})', '1.12',
        0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (192, 'Function', 'FUN_UDF', 'HASH_FUNCTION', 'SHA512(string)',
        '以128位十六进制数字的字符串形式返回字符串的SHA-512散列;如果字符串为NULL，则返回NULL。', 'SHA512(${1:})', '1.12',
        0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (193, 'Function', 'FUN_UDF', 'HASH_FUNCTION', 'SHA2(string, hashLength)',
        '使用SHA-2哈希函数族(SHA-224、SHA-256、SHA-384或SHA-512)返回哈希值。第一个参数string是要散列的字符串，第二个参数hashLength是结果的位长度(224、256、384或512)。如果string或hashLength为NULL，则返回NULL。',
        'SHA2(${1:}, ${2:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (194, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION',
        'COUNT([ ALL ] expression | DISTINCT expression1 [, expression2]*)',
        '默认情况下或使用ALL时，返回表达式不为空的输入行数。对每个值的唯一实例使用DISTINCT。', 'COUNT( DISTINCT ${1:})',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (195, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', e'COUNT(*)
COUNT(1)', '返回输入行数。', 'COUNT(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000',
        null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (196, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'AVG([ ALL | DISTINCT ] expression)',
        '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的平均值(算术平均值)。对每个值的唯一实例使用DISTINCT。',
        'AVG(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (197, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'SUM([ ALL | DISTINCT ] expression)',
        '默认情况下，或使用关键字ALL，返回所有输入行表达式的和。对每个值的唯一实例使用DISTINCT。', 'SUM(${1:})', '1.12', 0,
        1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (198, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'MAX([ ALL | DISTINCT ] expression)',
        '默认情况下或使用关键字ALL，返回表达式在所有输入行中的最大值。对每个值的唯一实例使用DISTINCT。', 'MAX(${1:})',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (199, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'MIN([ ALL | DISTINCT ] expression)',
        '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的最小值。对每个值的唯一实例使用DISTINCT。', 'MIN(${1:})',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (200, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'STDDEV_POP([ ALL | DISTINCT ] expression)',
        '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的总体标准差。对每个值的唯一实例使用DISTINCT。',
        'STDDEV_POP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (201, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'STDDEV_SAMP([ ALL | DISTINCT ] expression)',
        '默认情况下或使用关键字ALL时，返回表达式在所有输入行中的样本标准差。对每个值的唯一实例使用DISTINCT。',
        'STDDEV_SAMP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (202, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'VAR_POP([ ALL | DISTINCT ] expression)',
        '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的总体方差(总体标准差的平方)。对每个值的唯一实例使用DISTINCT。',
        'VAR_POP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (203, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'VAR_SAMP([ ALL | DISTINCT ] expression)',
        '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的样本方差(样本标准差的平方)。对每个值的唯一实例使用DISTINCT。',
        'VAR_SAMP(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (204, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'COLLECT([ ALL | DISTINCT ] expression)',
        '默认情况下，或使用关键字ALL，跨所有输入行返回表达式的多集。空值将被忽略。对每个值的唯一实例使用DISTINCT。',
        'COLLECT(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (205, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'VARIANCE([ ALL | DISTINCT ] expression)',
        'VAR_SAMP的同义词。仅在blink planner中支持。', 'VARIANCE(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000',
        '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (206, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'RANK()',
        '返回值在一组值中的秩。结果是1加上分区顺序中位于当前行之前或等于当前行的行数。这些值将在序列中产生空白。仅在blink planner中支持。',
        'RANK()', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (207, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'DENSE_RANK()',
        '返回值在一组值中的秩。结果是1加上前面分配的秩值。与函数rank不同，dense_rank不会在排序序列中产生空隙。仅在blink planner中支持。',
        'DENSE_RANK()', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (208, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'ROW_NUMBER()',
        '根据窗口分区中的行顺序，为每一行分配一个惟一的连续数字，从1开始。仅在blink planner中支持。', 'ROW_NUMBER()',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (209, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'LEAD(expression [, offset] [, default] )',
        '返回表达式在窗口中当前行之前的偏移行上的值。offset的默认值是1,default的默认值是NULL。仅在blink planner中支持。',
        'LEAD(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (210, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'LAG(expression [, offset] [, default])',
        '返回表达式的值，该值位于窗口中当前行之后的偏移行。offset的默认值是1,default的默认值是NULL。仅在blink planner中支持。',
        'LAG(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (211, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'FIRST_VALUE(expression)',
        '返回一组有序值中的第一个值。仅在blink planner中支持。', 'FIRST_VALUE(${1:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (212, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'LAST_VALUE(expression)',
        '返回一组有序值中的最后一个值。仅在blink planner中支持。', 'LAST_VALUE(${1:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (213, 'Function', 'FUN_UDF', 'AGGREGATE_FUNCTION', 'LISTAGG(expression [, separator])',
        '连接字符串表达式的值，并在它们之间放置分隔符值。分隔符没有添加在字符串的末尾。分隔符的默认值是''，''。仅在blink planner中支持。',
        'LISTAGG(${1:})', '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (214, 'Function', 'FUN_UDF', 'COLUMN_FUNCTION', 'withColumns(…)', '选择的列', 'withColumns(${1:})', '1.12', 0, 1,
        '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (215, 'Function', 'FUN_UDF', 'COLUMN_FUNCTION', 'withoutColumns(…)', '不选择的列', 'withoutColumns(${1:})',
        '1.12', 0, 1, '2021-02-22 15:46:48.000000', '2023-12-28 00:09:00.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (216, 'Function', 'FUN_UDF', 'COMPARE_FUNCTION', 'value1 = value2',
        '如果value1等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '${1:} =${2:}', '1.12', 9, 1,
        '2021-02-22 10:06:49.000000', '2023-12-28 00:08:58.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (217, 'Function', 'FUN_UDF', 'TABLE_AGGREGATE_FUNCTION', 'TO_MAP(string1,object2[, string3])',
        '将非规则一维表转化为规则二维表，string1是key。string2是value。string3为非必填项，表示key的值域（维度），用英文逗号分割。',
        'TO_MAP(${1:})', '1.12', 8, 1, '2021-05-20 19:59:22.000000', '2023-12-28 00:10:10.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (218, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE print', 'Whole library synchronization print', e'EXECUTE CDCSOURCE demo_print WITH (
  ''connector'' = ''mysql-cdc'',
  ''hostname'' = ''127.0.0.1'',
  ''port'' = ''3306'',
  ''username'' = ''root'',
  ''password'' = ''123456'',
  ''checkpoint'' = ''10000'',
  ''scan.startup.mode'' = ''initial'',
  ''parallelism'' = ''1'',
  ''table-name'' = ''test\\.student,test\\.score'',
  ''sink.connector'' = ''print''
);', 'All Versions', 0, 1, '2023-10-31 16:01:45.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (219, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE doris', 'Whole library synchronization doris', e'EXECUTE CDCSOURCE demo_print WITH (
  ''connector'' = ''mysql-cdc'',
  ''hostname'' = ''127.0.0.1'',
  ''port'' = ''3306'',
  ''username'' = ''root'',
  ''password'' = ''123456'',
  ''checkpoint'' = ''10000'',
  ''scan.startup.mode'' = ''initial'',
  ''parallelism'' = ''1'',
  ''table-name'' = ''test\\.student,test\\.score'',
  ''sink.connector'' = ''print''
);', 'All Versions', 0, 1, '2023-10-31 16:02:21.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (220, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE demo_doris_schema_evolution',
        'The entire library is synchronized to doris tape mode evolution', e'EXECUTE CDCSOURCE demo_doris_schema_evolution WITH (
  ''connector'' = ''mysql-cdc'',
  ''hostname'' = ''127.0.0.1'',
  ''port'' = ''3306'',
  ''username'' = ''root'',
  ''password'' = ''123456'',
  ''checkpoint'' = ''10000'',
  ''scan.startup.mode'' = ''initial'',
  ''parallelism'' = ''1'',
  ''table-name'' = ''test\\.student,test\\.score'',
  ''sink.connector'' = ''datastream-doris-schema-evolution'',
  ''sink.fenodes'' = ''127.0.0.1:8030'',
  ''sink.username'' = ''root'',
  ''sink.password'' = ''123456'',
  ''sink.doris.batch.size'' = ''1000'',
  ''sink.sink.max-retries'' = ''1'',
  ''sink.sink.batch.interval'' = ''60000'',
  ''sink.sink.db'' = ''test'',
  ''sink.table.identifier'' = ''#{schemaName}.#{tableName}''
);', 'All Versions', 0, 1, '2023-10-31 16:04:53.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (230, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE mysql2hive',
        'The entire library is synchronized to the sql-catalog of hive', e'EXECUTE CDCSOURCE mysql2hive WITH (
  ''connector'' = ''mysql-cdc'',
  ''hostname'' = ''127.0.0.1'',
  ''port'' = ''3306'',
  ''username'' = ''root'',
  ''password'' = ''123456'',
  ''checkpoint'' = ''10000'',
  ''scan.startup.mode'' = ''initial'',
  ''parallelism'' = ''1'',
  ''table-name'' = ''test\\..*'',
  ''sink.connector'' = ''sql-catalog'',
  ''sink.catalog.name'' = ''hive'',
  ''sink.catalog.type'' = ''hive'',
  ''sink.default-database'' = ''hdb'',
  ''sink.hive-conf-dir'' = ''/usr/local/dlink/hive-conf''
);', 'All Versions', 0, 1, '2023-10-31 16:14:31.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (231, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE  mysql2paimon',
        'The entire library is synchronized to paimon', e'EXECUTE CDCSOURCE mysql2paimon WITH (
  ''connector'' = ''mysql-cdc'',
  ''hostname'' = ''127.0.0.1'',
  ''port'' = ''3306'',
  ''username'' = ''root'',
  ''password'' = ''123456'',
  ''checkpoint'' = ''10000'',
  ''scan.startup.mode'' = ''initial'',
  ''parallelism'' = ''1'',
  ''table-name'' = ''test\\..*'',
  ''sink.connector'' = ''sql-catalog'',
  ''sink.catalog.name'' = ''fts'',
  ''sink.catalog.type'' = ''table-store'',
  ''sink.catalog.warehouse''=''file:/tmp/table_store''
);', 'All Versions', 0, 1, '2023-10-31 16:15:22.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (221, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE StarRocks ', e'The entire library is synchronized to StarRocks
', e'EXECUTE CDCSOURCE demo_hudi WITH (
 ''connector'' = ''mysql-cdc'',
 ''hostname'' = ''127.0.0.1'',
 ''port'' = ''3306'',
 ''username'' = ''root'',
 ''password'' = ''123456'',
 ''checkpoint'' = ''10000'',
 ''scan.startup.mode'' = ''initial'',
 ''parallelism'' = ''1'',
 ''database-name''=''bigdata'',
 ''table-name''=''bigdata\\.products,bigdata\\.orders'',
 ''sink.connector''=''hudi'',
 ''sink.path''=''hdfs://nameservice1/data/hudi/#{tableName}'',
 ''sink.hoodie.datasource.write.recordkey.field''=''${pkList}'',
 ''sink.hoodie.parquet.max.file.size''=''268435456'',
 ''sink.write.tasks''=''1'',
 ''sink.write.bucket_assign.tasks''=''2'',
 ''sink.write.precombine''=''true'',
 ''sink.compaction.async.enabled''=''true'',
 ''sink.write.task.max.size''=''1024'',
 ''sink.write.rate.limit''=''3000'',
 ''sink.write.operation''=''upsert'',
 ''sink.table.type''=''COPY_ON_WRITE'',
 ''sink.compaction.tasks''=''1'',
 ''sink.compaction.delta_seconds''=''20'',
 ''sink.compaction.async.enabled''=''true'',
 ''sink.read.streaming.skip_compaction''=''true'',
 ''sink.compaction.delta_commits''=''20'',
 ''sink.compaction.trigger.strategy''=''num_or_time'',
 ''sink.compaction.max_memory''=''500'',
 ''sink.changelog.enabled''=''true'',
 ''sink.read.streaming.enabled''=''true'',
 ''sink.read.streaming.check.interval''=''3'',
 ''sink.hive_sync.skip_ro_suffix'' = ''true'',
 ''sink.hive_sync.enable''=''true'',
 ''sink.hive_sync.mode''=''hms'',
 ''sink.hive_sync.metastore.uris''=''thrift://bigdata1:9083'',
 ''sink.hive_sync.db''=''qhc_hudi_ods'',
 ''sink.hive_sync.table''=''#{tableName}'',
 ''sink.table.prefix.schema''=''true''
);', 'All Versions', 0, 1, '2023-10-31 16:05:50.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (222, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_mysql',
        'The entire library is synchronized to mysql', e'EXECUTE CDCSOURCE demo_startrocks WITH (
  ''connector'' = ''mysql-cdc'',
  ''hostname'' = ''127.0.0.1'',
  ''port'' = ''3306'',
  ''username'' = ''root'',
  ''password'' = ''123456'',
  ''checkpoint'' = ''3000'',
  ''scan.startup.mode'' = ''initial'',
  ''parallelism'' = ''1'',
  ''table-name'' = ''bigdata\\.products,bigdata\\.orders'',
  ''sink.connector'' = ''starrocks'',
  ''sink.jdbc-url'' = ''jdbc:mysql://127.0.0.1:19035'',
  ''sink.load-url'' = ''127.0.0.1:18035'',
  ''sink.username'' = ''root'',
  ''sink.password'' = ''123456'',
  ''sink.sink.db'' = ''ods'',
  ''sink.table.prefix'' = ''ods_'',
  ''sink.table.lower'' = ''true'',
  ''sink.database-name'' = ''ods'',
  ''sink.table-name'' = ''#{tableName}'',
  ''sink.sink.properties.format'' = ''json'',
  ''sink.sink.properties.strip_outer_array'' = ''true'',
  ''sink.sink.max-retries'' = ''10'',
  ''sink.sink.buffer-flush.interval-ms'' = ''15000'',
  ''sink.sink.parallelism'' = ''1''
);', 'All Versions', 0, 1, '2023-10-31 16:07:08.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (223, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE demo_doris',
        'The entire library is synchronized to mysql', e'EXECUTE CDCSOURCE cdc_mysql WITH (
 ''connector'' = ''mysql-cdc'',
 ''hostname'' = ''127.0.0.1'',
 ''port'' = ''3306'',
 ''username'' = ''root'',
 ''password'' = ''123456'',
 ''checkpoint'' = ''3000'',
 ''scan.startup.mode'' = ''initial'',
 ''parallelism'' = ''1'',
 ''table-name'' = ''bigdata\\.products,bigdata\\.orders'',
 ''sink.connector'' = ''jdbc'',
 ''sink.url'' = ''jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8&useSSL=false'',
 ''sink.username'' = ''root'',
 ''sink.password'' = ''123456'',
 ''sink.sink.db'' = ''test'',
 ''sink.table.prefix'' = ''test_'',
 ''sink.table.lower'' = ''true'',
 ''sink.table-name'' = ''#{tableName}'',
 ''sink.driver'' = ''com.mysql.jdbc.Driver'',
 ''sink.sink.buffer-flush.interval'' = ''2s'',
 ''sink.sink.buffer-flush.max-rows'' = ''100'',
 ''sink.sink.max-retries'' = ''5'',
 ''sink.auto.create'' = ''true''
);', 'All Versions', 0, 1, '2023-10-31 16:07:47.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (224, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_oracle',
        'The entire library is synchronized to cdc_oracle', e'EXECUTE CDCSOURCE cdc_oracle WITH (
 ''connector'' = ''oracle-cdc'',
 ''hostname'' = ''127.0.0.1'',
 ''port'' = ''1521'',
 ''username''=''root'',
 ''password''=''123456'',
 ''database-name''=''ORCL'',
 ''checkpoint'' = ''3000'',
 ''scan.startup.mode'' = ''initial'',
 ''parallelism'' = ''1'',
 ''table-name'' = ''TEST\\..*'',
 ''connector'' = ''jdbc'',
 ''url'' = ''jdbc:oracle:thin:@127.0.0.1:1521:orcl'',
 ''username'' = ''root'',
 ''password'' = ''123456'',
 ''table-name'' = ''TEST2.#{tableName}''
);', 'All Versions', 0, 1, '2023-10-31 16:08:30.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (225, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_kafka_one',
        'The entire library is synchronized to a topic in kafka', e'EXECUTE CDCSOURCE cdc_kafka_one WITH (
 ''connector'' = ''mysql-cdc'',
 ''hostname'' = ''127.0.0.1'',
 ''port'' = ''3306'',
 ''username'' = ''root'',
 ''password'' = ''123456'',
 ''checkpoint'' = ''3000'',
 ''scan.startup.mode'' = ''initial'',
 ''parallelism'' = ''1'',
 ''table-name'' = ''bigdata\\.products,bigdata\\.orders'',
 ''sink.connector''=''datastream-kafka'',
 ''sink.topic''=''cdctest'',
 ''sink.brokers''=''bigdata2:9092,bigdata3:9092,bigdata4:9092''
);', 'All Versions', 0, 1, '2023-10-31 16:10:13.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (226, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_kafka_mul',
        'The entire library is synchronized to a single topic in kafka', e'EXECUTE CDCSOURCE cdc_kafka_mul WITH (
 ''connector'' = ''mysql-cdc'',
 ''hostname'' = ''127.0.0.1'',
 ''port'' = ''3306'',
 ''username'' = ''root'',
 ''password'' = ''123456'',
 ''checkpoint'' = ''3000'',
 ''scan.startup.mode'' = ''initial'',
 ''parallelism'' = ''1'',
 ''table-name'' = ''bigdata\\.products,bigdata\\.orders'',
 ''sink.connector''=''datastream-kafka'',
 ''sink.brokers''=''bigdata2:9092,bigdata3:9092,bigdata4:9092''
)', 'All Versions', 0, 1, '2023-10-31 16:10:59.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (227, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_upsert_kafka',
        'The entire library is synchronized to kafka primary key mode', e'EXECUTE CDCSOURCE cdc_upsert_kafka WITH (
 ''connector'' = ''mysql-cdc'',
 ''hostname'' = ''127.0.0.1'',
 ''port'' = ''3306'',
 ''username'' = ''root'',
 ''password'' = ''123456'',
 ''checkpoint'' = ''3000'',
 ''scan.startup.mode'' = ''initial'',
 ''parallelism'' = ''1'',
 ''table-name'' = ''bigdata\\.products,bigdata\\.orders'',
 ''sink.connector'' = ''upsert-kafka'',
 ''sink.topic'' = ''#{tableName}'',
 ''sink.properties.bootstrap.servers'' = ''bigdata2:9092,bigdata3:9092,bigdata4:9092'',
 ''sink.key.format'' = ''json'',
 ''sink.value.format'' = ''json''
);', 'All Versions', 0, 1, '2023-10-31 16:12:14.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (228, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_postgresql ',
        'The entire library is synchronized to postgresql', e'EXECUTE CDCSOURCE cdc_postgresql WITH (
 ''connector'' = ''mysql-cdc'',
 ''hostname'' = ''127.0.0.1'',
 ''port'' = ''3306'',
 ''username'' = ''root'',
 ''password'' = ''123456'',
 ''checkpoint'' = ''3000'',
 ''scan.startup.mode'' = ''initial'',
 ''parallelism'' = ''1'',
 ''table-name'' = ''bigdata\\.products,bigdata\\.orders'',
 ''sink.connector'' = ''jdbc'',
 ''sink.url'' = ''jdbc:postgresql://127.0.0.1:5432/test'',
 ''sink.username'' = ''test'',
 ''sink.password'' = ''123456'',
 ''sink.sink.db'' = ''test'',
 ''sink.table.prefix'' = ''test_'',
 ''sink.table.lower'' = ''true'',
 ''sink.table-name'' = ''#{tableName}'',
 ''sink.driver'' = ''org.postgresql.Driver'',
 ''sink.sink.buffer-flush.interval'' = ''2s'',
 ''sink.sink.buffer-flush.max-rows'' = ''100'',
 ''sink.sink.max-retries'' = ''5''
)', 'All Versions', 0, 1, '2023-10-31 16:12:54.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (229, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE cdc_clickhouse',
        'Sync the entire library to clickhouse', e'EXECUTE CDCSOURCE cdc_clickhouse WITH (
 ''connector'' = ''mysql-cdc'',
 ''hostname'' = ''127.0.0.1'',
 ''port'' = ''3306'',
 ''username'' = ''root'',
 ''password'' = ''123456'',
 ''checkpoint'' = ''3000'',
 ''scan.startup.mode'' = ''initial'',
 ''parallelism'' = ''1'',
 ''table-name'' = ''bigdata\\.products,bigdata\\.orders'',
  ''sink.connector'' = ''clickhouse'',
  ''sink.url'' = ''clickhouse://127.0.0.1:8123'',
  ''sink.username'' = ''default'',
  ''sink.password'' = ''123456'',
  ''sink.sink.db'' = ''test'',
  ''sink.table.prefix'' = ''test_'',
  ''sink.table.lower'' = ''true'',
  ''sink.database-name'' = ''test'',
  ''sink.table-name'' = ''#{tableName}'',
  ''sink.sink.batch-size'' = ''500'',
  ''sink.sink.flush-interval'' = ''1000'',
  ''sink.sink.max-retries'' = ''3''
);', 'All Versions', 0, 1, '2023-10-31 16:13:33.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (232, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE mysql2dinky_catalog',
        'The entire library is synchronized to dinky''s built-in catalog', e'EXECUTE CDCSOURCE mysql2dinky_catalog WITH (
  ''connector'' = ''mysql-cdc'',
  ''hostname'' = ''127.0.0.1'',
  ''port'' = ''3306'',
  ''username'' = ''root'',
  ''password'' = ''123456'',
  ''checkpoint'' = ''10000'',
  ''scan.startup.mode'' = ''initial'',
  ''parallelism'' = ''1'',
  ''table-name'' = ''test\\..*'',
  ''sink.connector'' = ''sql-catalog'',
  ''sink.catalog.name'' = ''dlinkmysql'',
  ''sink.catalog.type'' = ''dlink_mysql'',
  ''sink.catalog.username'' = ''dlink'',
  ''sink.catalog.password'' = ''dlink'',
  ''sink.catalog.url'' = ''jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC'',
  ''sink.sink.db'' = ''default_database''
);', 'All Versions', 0, 1, '2023-10-31 16:16:22.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (233, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE mysql2multiple_sink',
        'Synchronization of the entire library to multiple data sources (sink)', e'EXECUTE CDCSOURCE mysql2multiple_sink WITH (
  ''connector'' = ''mysql-cdc'',
  ''hostname'' = ''127.0.0.1'',
  ''port'' = ''3306'',
  ''username'' = ''root'',
  ''password'' = ''123456'',
  ''checkpoint'' = ''3000'',
  ''scan.startup.mode'' = ''initial'',
  ''parallelism'' = ''1'',
  ''table-name'' = ''test\\.student,test\\.score'',
  ''sink[0].connector'' = ''doris'',
  ''sink[0].fenodes'' = ''127.0.0.1:8030'',
  ''sink[0].username'' = ''root'',
  ''sink[0].password'' = ''dw123456'',
  ''sink[0].sink.batch.size'' = ''1'',
  ''sink[0].sink.max-retries'' = ''1'',
  ''sink[0].sink.batch.interval'' = ''60000'',
  ''sink[0].sink.db'' = ''test'',
  ''sink[0].table.prefix'' = ''ODS_'',
  ''sink[0].table.upper'' = ''true'',
  ''sink[0].table.identifier'' = ''#{schemaName}.#{tableName}'',
  ''sink[0].sink.label-prefix'' = ''#{schemaName}_${tableName}_1'',
  ''sink[0].sink.enable-delete'' = ''true'',
  ''sink[1].connector''=''datastream-kafka'',
  ''sink[1].topic''=''cdc'',
  ''sink[1].brokers''=''127.0.0.1:9092''
)', 'All Versions', 0, 1, '2023-10-31 16:17:27.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (234, 'Reference', 'FUN_UDF', 'OTHER_FUNCTION', 'ADD JAR', 'ADD JAR', 'ADD JAR ${1:}; -- str path ',
        'All Versions', 0, 1, '2023-10-31 16:19:52.000000', '2023-12-28 00:02:02.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (235, 'Function', 'Other', 'Other', 'SHOW FRAGMENTS', 'SHOW FRAGMENTS', 'SHOW FRAGMENTS;', 'All Versions', 0, 1,
        '2023-10-31 16:20:30.000000', '2023-12-28 09:57:55.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (236, 'Function', 'Other', 'Other', 'SHOW FRAGMENT var1', 'SHOW FRAGMENT var1', 'SHOW FRAGMENT ${1:};',
        'All Versions', 0, 1, '2023-10-31 16:21:23.000000', '2023-12-28 09:57:54.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (237, 'Reference', 'SQL_TEMPLATE', 'FlinkCDC', 'EXECUTE CDCSOURCE demo_hudi',
        'The entire library is synchronized to hudi', e'EXECUTE CDCSOURCE demo_hudi WITH (
 ''connector'' = ''mysql-cdc'',
 ''hostname'' = ''127.0.0.1'',
 ''port'' = ''3306'',
 ''username'' = ''root'',
 ''password'' = ''123456'',
 ''checkpoint'' = ''10000'',
 ''scan.startup.mode'' = ''initial'',
 ''parallelism'' = ''1'',
 ''database-name''=''bigdata'',
 ''table-name''=''bigdata\\.products,bigdata\\.orders'',
 ''sink.connector''=''hudi'',
 ''sink.path''=''hdfs://nameservice1/data/hudi/#{tableName}'',
 ''sink.hoodie.datasource.write.recordkey.field''=''${pkList}'',
 ''sink.hoodie.parquet.max.file.size''=''268435456'',
 ''sink.write.tasks''=''1'',
 ''sink.write.bucket_assign.tasks''=''2'',
 ''sink.write.precombine''=''true'',
 ''sink.compaction.async.enabled''=''true'',
 ''sink.write.task.max.size''=''1024'',
 ''sink.write.rate.limit''=''3000'',
 ''sink.write.operation''=''upsert'',
 ''sink.table.type''=''COPY_ON_WRITE'',
 ''sink.compaction.tasks''=''1'',
 ''sink.compaction.delta_seconds''=''20'',
 ''sink.compaction.async.enabled''=''true'',
 ''sink.read.streaming.skip_compaction''=''true'',
 ''sink.compaction.delta_commits''=''20'',
 ''sink.compaction.trigger.strategy''=''num_or_time'',
 ''sink.compaction.max_memory''=''500'',
 ''sink.changelog.enabled''=''true'',
 ''sink.read.streaming.enabled''=''true'',
 ''sink.read.streaming.check.interval''=''3'',
 ''sink.hive_sync.skip_ro_suffix'' = ''true'',
 ''sink.hive_sync.enable''=''true'',
 ''sink.hive_sync.mode''=''hms'',
 ''sink.hive_sync.metastore.uris''=''thrift://bigdata1:9083'',
 ''sink.hive_sync.db''=''qhc_hudi_ods'',
 ''sink.hive_sync.table''=''#{tableName}'',
 ''sink.table.prefix.schema''=''true''
);', 'All Versions', 0, 1, '2023-10-31 16:24:47.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (238, 'Reference', 'SQL_TEMPLATE', 'FlinkJar', 'EXECUTE JAR ', 'EXECUTE JAR use sql', e'EXECUTE JAR WITH (
''uri''=''rs:///jar/flink/demo/SocketWindowWordCount.jar'',
''main-class''=''org.apache.flink.streaming.examples.socket'',
''args''='' --hostname localhost '',
''parallelism''='''',
''savepoint-path''=''''
);', 'All Versions', 0, 1, '2023-10-31 16:27:53.000000', '2023-12-28 09:57:54.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (239, 'Reference', 'FUN_UDF', 'OTHER_FUNCTION', 'PRINT tablename', 'PRINT table data', 'PRINT ${1:}',
        'All Versions', 0, 1, '2023-10-31 16:30:22.000000', '2023-12-28 00:09:39.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (240, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'CREATE TABLE Like', 'CREATE TABLE Like source table', e'DROP TABLE IF EXISTS sink_table;
CREATE TABLE IF not EXISTS sink_table
WITH (
    ''topic'' = ''motor_vehicle_error''
)
LIKE source_table;', 'All Versions', 0, 1, '2023-10-31 16:33:38.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (241, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'CREATE TABLE like source_table EXCLUDING',
        'CREATE TABLE like source_table EXCLUDING', e'DROP TABLE IF EXISTS sink_table;
CREATE TABLE IF not EXISTS sink_table(
     -- Add watermark definition
    WATERMARK FOR order_time AS order_time - INTERVAL ''5'' SECOND
)
WITH (
    ''topic'' = ''motor_vehicle_error''
)
LIKE source_table (
     -- Exclude everything besides the computed columns which we need to generate the watermark for.
    -- We do not want to have the partitions or filesystem options as those do not apply to kafka.
    EXCLUDING ALL
    INCLUDING GENERATED
);', 'All Versions', 0, 1, '2023-10-31 16:36:13.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (242, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'CREATE TABLE ctas_kafka', 'CREATE TABLE ctas_kafka', e'CREATE TABLE my_ctas_table
WITH (
    ''connector'' = ''kafka''
)
AS SELECT id, name, age FROM source_table WHERE mod(id, 10) = 0;', 'All Versions', 0, 1, '2023-10-31 16:37:33.000000',
        '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (243, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'CREATE TABLE rtas_kafka', 'CREATE TABLE rtas_kafka', e'CREATE OR REPLACE TABLE my_ctas_table
WITH (
    ''connector'' = ''kafka''
)
AS SELECT id, name, age FROM source_table WHERE mod(id, 10) = 0;', 'All Versions', 0, 1, '2023-10-31 16:41:46.000000',
        '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (244, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'datagen job demo', 'datagen job demo', e'DROP TABLE IF EXISTS source_table3;
CREATE TABLE IF NOT EXISTS source_table3(
--订单id
`order_id` BIGINT,
--产品

`product` BIGINT,
--金额
`amount` BIGINT,

--支付时间
`order_time` as CAST(CURRENT_TIMESTAMP AS TIMESTAMP(3)), -- `在这里插入代码片`
--WATERMARK
WATERMARK FOR order_time AS order_time - INTERVAL ''2'' SECOND
) WITH(
''connector'' = ''datagen'',
 ''rows-per-second'' = ''1'',
 ''fields.order_id.min'' = ''1'',
 ''fields.order_id.max'' = ''2'',
 ''fields.amount.min'' = ''1'',
 ''fields.amount.max'' = ''10'',
 ''fields.product.min'' = ''1'',
 ''fields.product.max'' = ''2''
);

-- SELECT * FROM source_table3 LIMIT 10;

DROP TABLE IF EXISTS sink_table5;
CREATE TABLE IF NOT EXISTS sink_table5(
--产品
`product` BIGINT,
--金额
`amount` BIGINT,
--支付时间
`order_time` TIMESTAMP(3),
--1分钟时间聚合总数
`one_minute_sum` BIGINT
) WITH(
''connector''=''print''
);

INSERT INTO sink_table5
SELECT
product,
amount,
order_time,
SUM(amount) OVER(
PARTITION BY product
ORDER BY order_time
-- 标识统计范围是1个 product 的最近 1 分钟的数据
RANGE BETWEEN INTERVAL ''1'' MINUTE PRECEDING AND CURRENT ROW
) as one_minute_sum
FROM source_table3;', 'All Versions', 0, 1, '2023-11-15 15:42:16.000000', '2023-12-28 00:02:57.000000', null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (245, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'checkpoint config', 'checkpoint config', '-- 声明一些调优参数 (checkpoint 等相关配置)
set ''execution.checkpointing.checkpoints-after-tasks-finish.enabled'' =''true'';
SET ''pipeline.operator-chaining'' = ''false'';
set ''state.savepoints.dir''=''file:///opt/data/flink_cluster/savepoints''; -- 目录自行修改
set ''state.checkpoints.dir''= ''file:///opt/data/flink_cluster/checkpoints''; -- 目录自行修改
-- set state.checkpoint-storage=''filesystem'';
set ''state.backend.type''=''rocksdb'';
set ''execution.checkpointing.interval''=''60 s'';
set ''state.checkpoints.num-retained''=''100'';
-- 使 solt 均匀分布在 各个 TM 上
set ''cluster.evenly-spread-out-slots''=''true'';', 'All Versions', 0, 1, '2023-11-15 15:57:42', '2023-12-27 23:58:09',
        null, null);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (246, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'note template', 'note template', e'-- -----------------------------------------------------------------
-- @Description(作业描述): ${1:}
-- @Creator(创建人): ${2:}
-- @Create DateTime(创建时间): ${3:}
-- -----------------------------------------------------------------

${4:}', 'All Versions', 0, 1, '2023-11-17 17:03:24.000000', '2023-12-28 12:05:20.000000', 1, 1);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (247, 'Reference', 'SQL_TEMPLATE', 'FlinkSql', 'dinky_paimon_auto_create_table',
        'dinky paimon auto create table', e'-- -----------------------------------------------------------------
-- 该 demo 用于创建 mysql-cdc 到 paimon 的整库同步案例 并使用自动建表,注意 #{schemaName} 和 #{tableName} 为固定写法,不要修改,用于动态获取库名和表名
-- -----------------------------------------------------------------


EXECUTE CDCSOURCE dinky_paimon_auto_create_table
WITH
  (
    ''connector'' = ''mysql-cdc'',
    ''hostname'' = '''',
    ''port'' = '''',
    ''username'' = '''',
    ''password'' = '''',
    ''checkpoint'' = ''10000'',
    ''parallelism'' = ''1'',
    ''scan.startup.mode'' = ''initial'',
    ''database-name'' = ''dinky'',
    ''sink.connector'' = ''paimon'',
    ''sink.path'' = ''hdfs:/tmp/paimon/#{schemaName}.db/#{tableName}'',
    ''sink.auto-create'' = ''true'',
  );', 'All Versions', 0, 1, '2023-12-27 16:53:37.000000', '2023-12-28 12:05:20.000000', 1, 1);
INSERT INTO public.dinky_flink_document (id, category, type, subtype, name, description, fill_value, version, like_num,
                                         enabled, create_time, update_time, creator, updater)
VALUES (248, 'Variable', 'SQL_TEMPLATE', 'FlinkSql', 'add-customjar',
        'add CUSTOMJAR 为 Dinky 扩展语法 功能实现和 add jar 类似 , 推荐使用此方式', e'-- add CUSTOMJAR 为 Dinky 扩展语法 功能实现和 add jar 类似 , 推荐使用此方式
add CUSTOMJAR ''${1:}'';', 'All Versions', 0, 1, '2023-12-28 10:50:17.000000', '2023-12-28 12:05:20.000000', 1, 1);


-- ----------------------------
-- Table structure for dinky_fragment
-- ----------------------------
-- 首先，检查是否存在同名表，并在存在时删除它
DROP TABLE IF EXISTS dinky_fragment;

-- 使用PostgreSQL语法创建新表
CREATE TABLE dinky_fragment
(
    id             serial PRIMARY KEY          NOT NULL,
    name           varchar(50)                 NOT NULL,
    tenant_id      int                         NOT NULL DEFAULT 1,
    fragment_value text                        NOT NULL,
    note           text,
    enabled        smallint                             DEFAULT 1,
    create_time    timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator        int,
    updater        int,
    UNIQUE (name, tenant_id)
);

COMMENT ON TABLE dinky_fragment is 'fragment management';
COMMENT ON COLUMN dinky_fragment.id is 'id';
COMMENT ON COLUMN dinky_fragment.name is 'fragment name';
COMMENT ON COLUMN dinky_fragment.tenant_id is 'tenant id';
COMMENT ON COLUMN dinky_fragment.fragment_value is 'fragment value';
COMMENT ON COLUMN dinky_fragment.note is 'note';
COMMENT ON COLUMN dinky_fragment.enabled is 'enabled';
COMMENT ON COLUMN dinky_fragment.create_time is 'create time';
COMMENT ON COLUMN dinky_fragment.update_time is 'update time';
COMMENT ON COLUMN dinky_fragment.creator is 'creator';
COMMENT ON COLUMN dinky_fragment.updater is 'updater';

CREATE TRIGGER update_dinky_fragment_modtime
    BEFORE UPDATE
    ON dinky_fragment
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- ----------------------------
-- Table structure for dinky_history
-- ----------------------------

-- Drop the table if it exists
DROP TABLE IF EXISTS dinky_history;

-- Create the table
CREATE TABLE dinky_history
(
    id                       SERIAL PRIMARY KEY NOT NULL,
    tenant_id                INT                NOT NULL DEFAULT 1,
    cluster_id               INT                NOT NULL DEFAULT 0,
    cluster_configuration_id INT                NULL,
    session                  VARCHAR(255)       NULL,
    job_id                   VARCHAR(50)        NULL,
    job_name                 VARCHAR(255)       NULL,
    job_manager_address      VARCHAR(255)       NULL,
    status                   INT                NOT NULL DEFAULT 0,
    batch_model              BOOLEAN            NOT NULL DEFAULT FALSE,
    type                     VARCHAR(50)        NULL,
    statement                TEXT               NULL,
    error                    TEXT               NULL,
    result                   TEXT               NULL,
    config_json              TEXT               NULL,
    start_time               TIMESTAMP          NULL,
    end_time                 TIMESTAMP          NULL,
    task_id                  INT                NULL
);
-- Add indexes
CREATE INDEX task_index ON dinky_history (task_id);
CREATE INDEX cluster_index ON dinky_history (cluster_id);

-- Add comments to columns
COMMENT
    ON COLUMN dinky_history.id IS 'ID';
COMMENT
    ON COLUMN dinky_history.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_history.cluster_id IS 'cluster ID';
COMMENT
    ON COLUMN dinky_history.cluster_configuration_id IS 'cluster configuration id';
COMMENT
    ON COLUMN dinky_history.session IS 'session';
COMMENT
    ON COLUMN dinky_history.job_id IS 'Job ID';
COMMENT
    ON COLUMN dinky_history.job_name IS 'Job Name';
COMMENT
    ON COLUMN dinky_history.job_manager_address IS 'Job Manager Address';
COMMENT
    ON COLUMN dinky_history.status IS 'status';
COMMENT
    ON COLUMN dinky_history.batch_model IS 'is batch model';
COMMENT
    ON COLUMN dinky_history.type IS 'job type';
COMMENT
    ON COLUMN dinky_history.statement IS 'statement set';
COMMENT
    ON COLUMN dinky_history.error IS 'error message';
COMMENT
    ON COLUMN dinky_history.result IS 'result set';
COMMENT
    ON COLUMN dinky_history.config_json IS 'config json';
COMMENT
    ON COLUMN dinky_history.start_time IS 'job start time';
COMMENT
    ON COLUMN dinky_history.end_time IS 'job end time';
COMMENT
    ON COLUMN dinky_history.task_id IS 'task ID';

-- Add comment to the table
COMMENT
    ON TABLE dinky_history IS 'execution history';



-- ----------------------------
-- Table structure for dinky_job_history
-- ----------------------------
-- 如果表存在则删除
DROP TABLE IF EXISTS dinky_job_history;

-- 创建表
CREATE TABLE dinky_job_history
(
    id                         SERIAL PRIMARY KEY NOT NULL,
    tenant_id                  INT                NOT NULL DEFAULT 1,
    job_json                   TEXT               NULL,
    exceptions_json            TEXT               NULL,
    checkpoints_json           TEXT               NULL,
    checkpoints_config_json    TEXT               NULL,
    config_json                TEXT               NULL,
    cluster_json               TEXT               NULL,
    cluster_configuration_json TEXT               NULL,
    update_time                TIMESTAMP          NULL
);

-- 为列添加注释
COMMENT
    ON COLUMN dinky_job_history.id IS 'id';
COMMENT
    ON COLUMN dinky_job_history.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_job_history.job_json IS 'dinky job  json';
COMMENT
    ON COLUMN dinky_job_history.exceptions_json IS 'exception json ';
COMMENT
    ON COLUMN dinky_job_history.checkpoints_json IS 'checkpoint json';
COMMENT
    ON COLUMN dinky_job_history.checkpoints_config_json IS 'checkpoints config json ';
COMMENT
    ON COLUMN dinky_job_history.config_json IS 'config json';
COMMENT
    ON COLUMN dinky_job_history.cluster_json IS 'cluster json';
COMMENT
    ON COLUMN dinky_job_history.cluster_configuration_json IS 'cluster configuration json';
COMMENT
    ON COLUMN dinky_job_history.update_time IS 'update_time';
-- 为表添加注释
COMMENT
    ON TABLE dinky_job_history IS 'dinky_job_history';

CREATE TRIGGER updatetime_dinky_job_history
    BEFORE UPDATE
    ON dinky_job_history
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- ----------------------------
-- Table structure for dinky_job_instance
-- ----------------------------
-- 如果表存在则删除
DROP TABLE IF EXISTS dinky_job_instance;

-- 创建表
CREATE TABLE dinky_job_instance
(
    id                   SERIAL PRIMARY KEY NOT NULL,
    name                 VARCHAR(255)       NULL,
    tenant_id            INT                NOT NULL DEFAULT 1,
    task_id              INT                NULL,
    step                 INT                NULL,
    cluster_id           INT                NULL,
    jid                  VARCHAR(50)        NULL,
    status               VARCHAR(50)        NULL,
    history_id           INT                NULL,
    create_time          TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finish_time          TIMESTAMP          NULL,
    duration             BIGINT             NULL,
    error                TEXT               NULL,
    failed_restart_count INT                NULL,
    creator              INT                NULL,
    updater              INT                NULL,
    operator             INT                NULL
);

-- 添加唯一索引（name、tenant_id、task_id、history_id的组合必须是唯一的）
CREATE UNIQUE INDEX job_instance_un_idx1 ON dinky_job_instance (tenant_id, name, task_id, history_id);

-- 添加普通索引
CREATE INDEX job_instance_task_id_idx1 ON dinky_job_instance (task_id);

-- 为列添加注释
COMMENT
    ON COLUMN dinky_job_instance.id IS 'id';
COMMENT
    ON COLUMN dinky_job_instance.name IS 'job instance name';
COMMENT
    ON COLUMN dinky_job_instance.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_job_instance.task_id IS 'task id ';
COMMENT
    ON COLUMN dinky_job_instance.step IS 'job history step';';
COMMENT
    ON COLUMN dinky_job_instance.cluster_id IS 'cluster id';
COMMENT
    ON COLUMN dinky_job_instance.jid IS 'Flink JobId';
COMMENT
    ON COLUMN dinky_job_instance.status IS 'job instance status';';
COMMENT
    ON COLUMN dinky_job_instance.history_id IS 'history id';
COMMENT
    ON COLUMN dinky_job_instance.create_time IS 'create_time';
COMMENT
    ON COLUMN dinky_job_instance.create_time IS 'create_time';
COMMENT
    ON COLUMN dinky_job_instance.finish_time IS 'finish_time';
COMMENT
    ON COLUMN dinky_job_instance.duration IS 'job duration';
COMMENT
    ON COLUMN dinky_job_instance.error IS 'error log';
COMMENT
    ON COLUMN dinky_job_instance.failed_restart_count IS 'failed restart count';
COMMENT
    ON COLUMN dinky_job_instance.creator IS 'creator';
COMMENT
    ON COLUMN dinky_job_instance.updater IS 'updater';
COMMENT
    ON COLUMN dinky_job_instance.operator IS 'operator uid';

-- 为表添加注释
COMMENT
    ON TABLE dinky_job_instance IS '工作实例';

CREATE TRIGGER updatetime_dinky_job_instance
    BEFORE UPDATE
    ON dinky_job_instance
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- ----------------------------
-- Table structure for dinky_role
-- ----------------------------
-- 如果表存在则删除
DROP TABLE IF EXISTS dinky_role;

-- 创建表
CREATE TABLE dinky_role
(
    id          SERIAL PRIMARY KEY NOT NULL,
    tenant_id   INT                NOT NULL,
    role_code   VARCHAR(64)        NOT NULL,
    role_name   VARCHAR(64)        NOT NULL,
    is_delete   smallint           NOT NULL DEFAULT 0,
    note        VARCHAR(255)       NULL,
    create_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 添加唯一索引
CREATE UNIQUE INDEX role_un_idx1 ON dinky_role (role_code);

-- 为列添加注释
COMMENT
    ON COLUMN dinky_role.id IS 'ID';
COMMENT
    ON COLUMN dinky_role.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_role.role_code IS 'role code';
COMMENT
    ON COLUMN dinky_role.role_name IS 'role name';
COMMENT
    ON COLUMN dinky_role.is_delete IS 'is delete';
COMMENT
    ON COLUMN dinky_role.note IS 'note';
COMMENT
    ON COLUMN dinky_role.create_time IS 'create_time';
COMMENT
    ON COLUMN dinky_role.update_time IS 'update_time';

-- 为表添加注释
COMMENT
    ON TABLE dinky_role IS 'dinky_role';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_dinky_role
    BEFORE UPDATE
    ON dinky_role
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- ----------------------------
-- Records of dinky_role
-- ----------------------------
INSERT INTO dinky_role
VALUES (1, 1, 'SuperAdmin', 'SuperAdmin', 0, 'SuperAdmin of Role', '2022-12-13 05:27:19', '2022-12-13 05:27:19');


-- ----------------------------
-- Table structure for dinky_savepoints
-- ----------------------------

-- 如果表存在则删除
DROP TABLE IF EXISTS dinky_savepoints;

-- 创建表
CREATE TABLE dinky_savepoints
(
    id          SERIAL PRIMARY KEY NOT NULL,
    task_id     INT                NOT NULL,
    tenant_id   INT                NOT NULL DEFAULT 1,
    name        VARCHAR(255)       NOT NULL,
    type        VARCHAR(255)       NOT NULL,
    path        VARCHAR(255)       NOT NULL,
    create_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator     INT                NULL
);

-- 为列添加注释
COMMENT
    ON COLUMN dinky_savepoints.id IS 'ID';
COMMENT
    ON COLUMN dinky_savepoints.task_id IS 'task id';
COMMENT
    ON COLUMN dinky_savepoints.tenant_id IS 'tenant id';
COMMENT
    ON COLUMN dinky_savepoints.name IS 'task name';
COMMENT
    ON COLUMN dinky_savepoints.type IS 'savepoint type';
COMMENT
    ON COLUMN dinky_savepoints.path IS 'savepoint path';
COMMENT
    ON COLUMN dinky_savepoints.create_time IS 'create_time';
COMMENT
    ON COLUMN dinky_savepoints.creator IS 'creator';

-- 为表添加注释
COMMENT
    ON TABLE dinky_savepoints IS 'dinky_savepoints';

-- Table structure for dinky_sys_config
DROP TABLE IF EXISTS dinky_sys_config;
CREATE TABLE dinky_sys_config
(
    id          serial PRIMARY KEY NOT NULL,
    name        varchar(255)       NOT NULL,
    value       text,
    create_time timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT
    ON COLUMN dinky_sys_config.id IS 'ID';
COMMENT
    ON COLUMN dinky_sys_config.name IS 'configuration name';
COMMENT
    ON COLUMN dinky_sys_config.value IS 'configuration value';
COMMENT
    ON COLUMN dinky_sys_config.create_time IS 'create time';
COMMENT
    ON COLUMN dinky_sys_config.update_time IS 'update time';
COMMENT
    ON TABLE dinky_sys_config IS 'system configuration';


-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_dinky_sys_config
    BEFORE UPDATE
    ON dinky_sys_config
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- Table structure for dinky_task
DROP TABLE IF EXISTS dinky_task;
CREATE TABLE dinky_task
(
    id                       serial PRIMARY KEY not null,
    name                     varchar(255)       NOT NULL,
    tenant_id                int                NOT NULL DEFAULT 1,
    dialect                  varchar(50),
    type                     varchar(50),
    check_point              int,
    save_point_strategy      int,
    save_point_path          varchar(255) UNIQUE,
    parallelism              int,
    fragment                 boolean                     DEFAULT FALSE,
    statement_set            boolean                     DEFAULT FALSE,
    batch_model              boolean                     DEFAULT FALSE,
    cluster_id               int,
    cluster_configuration_id int,
    database_id              int,
    env_id                   int,
    alert_group_id           bigint,
    config_json              text,
    note                     varchar(255),
    step                     int                         DEFAULT 1,
    job_instance_id          bigint,
    enabled                  smallint           NOT NULL DEFAULT 1,
    create_time              timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time              timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version_id               int,
    statement                text,
    creator                  int,
    updater                  int,
    operator                 int
);

CREATE UNIQUE INDEX task_un_idx1 ON dinky_task (name, tenant_id);
COMMENT ON TABLE dinky_task IS 'Task';
COMMENT ON COLUMN dinky_task.id IS 'ID';
COMMENT ON COLUMN dinky_task.name IS 'Job name';
COMMENT ON COLUMN dinky_task.tenant_id IS 'tenant id';
COMMENT ON COLUMN dinky_task.dialect IS 'dialect';
COMMENT ON COLUMN dinky_task.type IS 'Job type';
COMMENT ON COLUMN dinky_task.check_point IS 'CheckPoint trigger seconds';
COMMENT ON COLUMN dinky_task.save_point_strategy IS 'SavePoint strategy';
COMMENT ON COLUMN dinky_task.save_point_path IS 'SavePointPath';
COMMENT ON COLUMN dinky_task.parallelism IS 'parallelism';
COMMENT ON COLUMN dinky_task.fragment IS 'fragment';
COMMENT ON COLUMN dinky_task.statement_set IS 'enable statement set';
COMMENT ON COLUMN dinky_task.batch_model IS 'use batch model';
COMMENT ON COLUMN dinky_task.cluster_id IS 'Flink cluster ID';
COMMENT ON COLUMN dinky_task.cluster_configuration_id IS 'cluster configuration ID';
COMMENT ON COLUMN dinky_task.database_id IS 'database ID';
COMMENT ON COLUMN dinky_task.env_id IS 'env id';
COMMENT ON COLUMN dinky_task.alert_group_id IS 'alert group id';
COMMENT ON COLUMN dinky_task.config_json IS 'configuration json';
COMMENT ON COLUMN dinky_task.note IS 'Job Note';
COMMENT ON COLUMN dinky_task.step IS 'Job lifecycle';
COMMENT ON COLUMN dinky_task.job_instance_id IS 'job instance id';
COMMENT ON COLUMN dinky_task.enabled IS 'is enable';
COMMENT ON COLUMN dinky_task.create_time IS 'create_time';
COMMENT ON COLUMN dinky_task.update_time IS 'update_time';
COMMENT ON COLUMN dinky_task.version_id IS 'version id';
COMMENT ON COLUMN dinky_task.statement IS 'sql statement';
COMMENT ON COLUMN dinky_task.creator IS 'creator';
COMMENT ON COLUMN dinky_task.updater IS 'updater';
COMMENT ON COLUMN dinky_task.operator IS 'operator user id';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_dinky_task
    BEFORE UPDATE
    ON dinky_task
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- Table structure for dinky_task_version
DROP TABLE IF EXISTS dinky_task_version;
CREATE TABLE dinky_task_version
(
    id             serial PRIMARY KEY NOT NULL,
    task_id        int                NOT NULL,
    tenant_id      int                NOT NULL DEFAULT 1,
    version_id     int                NOT NULL,
    statement      text,
    name           varchar(255)       NOT NULL,
    dialect        varchar(50),
    type           varchar(50),
    task_configure text               NOT NULL,
    create_time    timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator        int
);
CREATE UNIQUE INDEX task_version_un_idx1 ON dinky_task_version (task_id, tenant_id, version_id);
COMMENT ON TABLE dinky_task_version IS 'job history version';
COMMENT ON COLUMN dinky_task_version.id IS 'ID';
COMMENT ON COLUMN dinky_task_version.task_id IS 'task ID ';
COMMENT ON COLUMN dinky_task_version.tenant_id IS 'tenant id';
COMMENT ON COLUMN dinky_task_version.version_id IS 'version ID ';
COMMENT ON COLUMN dinky_task_version.statement IS 'flink sql statement';
COMMENT ON COLUMN dinky_task_version.name IS 'version name';
COMMENT ON COLUMN dinky_task_version.type IS 'type';
COMMENT ON COLUMN dinky_task_version.task_configure IS 'task configuration';


-- Table structure for dinky_tenant
DROP TABLE IF EXISTS dinky_tenant;
CREATE TABLE dinky_tenant
(
    id          serial PRIMARY KEY NOT NULL,
    tenant_code varchar(64)        NOT NULL,
    is_delete   SMALLINT           NOT NULL DEFAULT 0,
    note        varchar(255),
    create_time timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE dinky_tenant IS 'tenant';
COMMENT ON COLUMN dinky_tenant.id IS 'ID';
COMMENT ON COLUMN dinky_tenant.tenant_code IS 'tenant code';
COMMENT ON COLUMN dinky_tenant.is_delete IS 'is delete';
COMMENT ON COLUMN dinky_tenant.note IS 'note';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_dinky_tenant
    BEFORE UPDATE
    ON dinky_tenant
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Insert default record into dinky_tenant
INSERT INTO dinky_tenant (id, tenant_code, is_delete, note, create_time, update_time)
VALUES (1, 'DefaultTenant', 0, 'DefaultTenant', '2022-12-13 05:27:19', '2022-12-13 05:27:19');


-- Table structure for dinky_udf_template
DROP TABLE IF EXISTS dinky_udf_template;
CREATE TABLE dinky_udf_template
(
    id            serial PRIMARY KEY NOT NULL,
    name          varchar(100),
    code_type     varchar(10),
    function_type varchar(10),
    template_code text,
    enabled       boolean            NOT NULL DEFAULT true,
    create_time   timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator       int,
    updater       int
);
COMMENT ON TABLE dinky_udf_template IS 'udf template';
COMMENT ON COLUMN dinky_udf_template.id IS 'ID';
COMMENT ON COLUMN dinky_udf_template.name IS 'template name';
COMMENT ON COLUMN dinky_udf_template.code_type IS 'code type';
COMMENT ON COLUMN dinky_udf_template.function_type IS 'function type';
COMMENT ON COLUMN dinky_udf_template.template_code IS 'code';
COMMENT ON COLUMN dinky_udf_template.enabled IS 'enabled';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_dinky_udf_template
    BEFORE UPDATE
    ON dinky_udf_template
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- Records of dinky_udf_template
INSERT INTO dinky_udf_template (id, name, code_type, function_type, template_code, enabled, create_time, update_time,
                                creator, updater)
VALUES (1, 'java_udf', 'Java', 'UDF',
        $$${(package=='''')?string('''',''package ''+package+'';'')} import org.apache.flink.table.functions.ScalarFunction;

public class
${className}
extends
ScalarFunction
{
public
String
eval
(
String
s
)
{
return
null;
}
}$$, true, '2022-10-19 09:17:37', '2022-10-25 17:45:57', null, null),
       (2, 'java_udtf', 'Java', 'UDTF', $$
${(package=='''')?string('''',''package ''+package+'';'')}

import
org
.
apache
.
flink
.
table
.
functions
.
ScalarFunction;

@
FunctionHint
(
output
=
@
DataTypeHint
(
"ROW<word STRING, length INT>"
)
)
public
static
class
${className}
extends
TableFunction
<
Row
>
{

public
void
eval
(
String
str
)
{
for
(
String
s
:
str
.
split
(
" "
)
)
{
//
use
collect
(
.
.
.
)
to
emit
a
row
collect
(
Row
.
of
(
s,
s
.
length
(
)
)
);
}
}
}
$$,
        true,
        '2022-10-19 09:22:58',
        '2022-10-25 17:49:30',
        null,
        null),
       (3,
        'scala_udf',
        'Scala',
        'UDF',
        $$
${(package=='''')?string('''',''package ''+package+'';'')}

import
org
.
apache
.
flink
.
table
.
api
.
_
import
org
.
apache
.
flink
.
table
.
functions
.
ScalarFunction

//
定义可参数化的函数逻辑
class
${className}
extends
ScalarFunction
{
def
eval
(
s
:
String,
begin
:
Integer,
end
:
Integer
)
:
String
=
{
"this is scala"
}
}
$$,
        true,
        '2022-10-25 09:21:32',
        '2022-10-25 17:49:46',
        null,
        null),
       (4,
        'python_udf_1',
        'Python',
        'UDF',
        $$from
pyflink
.
table
import
ScalarFunction,
DataTypes
from
pyflink
.
table
.
udf
import
udf

class
${className}
(
ScalarFunction
)
:
def
__init__
(
self
)
:
pass

def
eval
(
self,
variable
)
:
return
str
(
variable
)


${attr!''f''}
=
udf
(
${className}
(
),
result_type
=
DataTypes
.
STRING
(
)
)
$$,
        true,
        '2022-10-25 09:23:07',
        '2022-10-25 09:34:01',
        null,
        null),
       (5,
        'python_udf_2',
        'Python',
        'UDF',
        $$from
pyflink
.
table
import
DataTypes
from
pyflink
.
table
.
udf
import
udf

@
udf
(
result_type
=
DataTypes
.
STRING
(
)
)
def
${className}
(
variable1
:
str
)
:
return
''''
$$,
        true,
        '2022-10-25 09:25:13',
        '2022-10-25 09:34:47',
        null,
        null);


-- Table structure for dinky_user
DROP TABLE IF EXISTS dinky_user;
CREATE TABLE dinky_user
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
    super_admin_flag boolean                     DEFAULT false,
    is_delete        boolean            NOT NULL DEFAULT false,
    create_time      timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE dinky_user IS 'user';
COMMENT ON COLUMN dinky_user.id IS 'id';
COMMENT ON COLUMN dinky_user.username IS 'username';
COMMENT ON COLUMN dinky_user.password IS 'password';
COMMENT ON COLUMN dinky_user.nickname IS 'nickname';
COMMENT ON COLUMN dinky_user.worknum IS 'worknum';
COMMENT ON COLUMN dinky_user.avatar IS 'avatar';
COMMENT ON COLUMN dinky_user.mobile IS 'mobile phone';
COMMENT ON COLUMN dinky_user.enabled IS 'enabled';
COMMENT ON COLUMN dinky_user.super_admin_flag IS 'is super admin(0:false,1true)';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_dinky_user
    BEFORE UPDATE
    ON dinky_user
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- Records of dinky_user
INSERT INTO dinky_user (id, username, user_type, password, nickname, worknum, avatar, mobile, enabled, super_admin_flag,
                        is_delete, create_time, update_time)
VALUES (1, 'admin', 0, '21232f297a57a5a743894a0e4a801fc3', 'Admin', 'Dinky-001', null, '17777777777', true, true, false,
        '2022-12-13 05:27:19', '2023-07-28 23:22:52');

-- Table structure for dinky_user_role
DROP TABLE IF EXISTS dinky_user_role;
CREATE TABLE dinky_user_role
(
    id          serial PRIMARY KEY NOT NULL,
    user_id     int                NOT NULL,
    role_id     int                NOT NULL,
    create_time timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX user_role_un_idx1 ON dinky_user_role (user_id, role_id);

COMMENT ON TABLE dinky_user_role IS 'Relationship between users and roles';
COMMENT ON COLUMN dinky_user_role.id IS 'id';
COMMENT ON COLUMN dinky_user_role.user_id IS 'user id';
COMMENT ON COLUMN dinky_user_role.role_id IS 'role_id';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_dinky_user_role
    BEFORE UPDATE
    ON dinky_user_role
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Records of dinky_user_role
INSERT INTO dinky_user_role (id, user_id, role_id, create_time, update_time)
VALUES (1, 1, 1, '2022-12-13 05:27:19', '2022-12-13 05:27:19');

-- Table structure for dinky_user_tenant
DROP TABLE IF EXISTS dinky_user_tenant;
CREATE TABLE dinky_user_tenant
(
    id                serial PRIMARY KEY,
    user_id           int       NOT NULL,
    tenant_id         int       NOT NULL,
    tenant_admin_flag smallint           DEFAULT 0,
    create_time       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX user_tenant_un_idx1 ON dinky_user_tenant (user_id, tenant_id);

COMMENT ON TABLE dinky_user_tenant IS 'Relationship between users and tenants';
COMMENT ON COLUMN dinky_user_tenant.id IS 'id';
COMMENT ON COLUMN dinky_user_tenant.user_id IS 'user id';
COMMENT ON COLUMN dinky_user_tenant.tenant_id IS 'tenant id';
COMMENT ON COLUMN dinky_user_tenant.tenant_admin_flag IS 'tenant admin flag(0:false,1:true)';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_dinky_user_tenant
    BEFORE UPDATE
    ON dinky_user_tenant
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- Records of dinky_user_tenant
INSERT INTO dinky_user_tenant (id, user_id, tenant_id, tenant_admin_flag, create_time, update_time)
VALUES (1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- Table structure for metadata_column
DROP TABLE IF EXISTS metadata_column;
CREATE TABLE metadata_column
(
    column_name varchar(255) NOT NULL,
    column_type varchar(255) NOT NULL,
    data_type   varchar(255) NOT NULL,
    expr        varchar(255),
    description varchar(255) NOT NULL,
    table_id    int          NOT NULL,
    "primary"   boolean,
    update_time timestamp             DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (table_id, column_name)
);

COMMENT ON TABLE metadata_column IS 'column informations';
COMMENT ON COLUMN metadata_column.column_name IS 'column name';
COMMENT ON COLUMN metadata_column.column_type IS 'column type, such as : Physical , Metadata , Computed , WATERMARK';
COMMENT ON COLUMN metadata_column.data_type IS 'data type';
COMMENT ON COLUMN metadata_column.expr IS 'expression';
COMMENT ON COLUMN metadata_column.description IS 'column description';
COMMENT ON COLUMN metadata_column.table_id IS 'table id';
COMMENT ON COLUMN metadata_column.primary IS 'table primary key';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_metadata_column
    BEFORE UPDATE
    ON metadata_column
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Table structure for metadata_database
DROP TABLE IF EXISTS metadata_database;
CREATE TABLE metadata_database
(
    id            serial PRIMARY KEY NOT NULL,
    database_name varchar(255)       NOT NULL,
    description   varchar(255),
    update_time   timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    create_time   timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE metadata_database IS 'metadata of database information';
COMMENT ON COLUMN metadata_database.database_name IS 'database name';
COMMENT ON COLUMN metadata_database.description IS 'database description';


-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_metadata_database
    BEFORE UPDATE
    ON metadata_database
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- Table structure for metadata_database_property
DROP TABLE IF EXISTS metadata_database_property;
CREATE TABLE metadata_database_property
(
    key         varchar(255) NOT NULL,
    value       varchar(255),
    database_id int          NOT NULL,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (key, database_id)
);

COMMENT ON TABLE metadata_database_property IS 'metadata of database configurations';
COMMENT ON COLUMN metadata_database_property.key IS 'key';
COMMENT ON COLUMN metadata_database_property.value IS 'value';
COMMENT ON COLUMN metadata_database_property.database_id IS 'database_id';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_metadata_database_property
    BEFORE UPDATE
    ON metadata_database_property
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- Table structure for metadata_function
DROP TABLE IF EXISTS metadata_function;
CREATE TABLE metadata_function
(
    id                serial PRIMARY KEY NOT NULL,
    function_name     varchar(255)       NOT NULL,
    class_name        varchar(255)       NOT NULL,
    database_id       int                NOT NULL,
    function_language varchar(255),
    update_time       timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    create_time       timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE metadata_function IS 'UDF informations';
COMMENT ON COLUMN metadata_function.id IS 'id';
COMMENT ON COLUMN metadata_function.function_name IS 'function name';
COMMENT ON COLUMN metadata_function.class_name IS 'class name';
COMMENT ON COLUMN metadata_function.database_id IS 'database id';
COMMENT ON COLUMN metadata_function.function_language IS 'function language';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_metadata_function
    BEFORE UPDATE
    ON metadata_function
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- Table structure for metadata_table
DROP TABLE IF EXISTS metadata_table;
CREATE TABLE metadata_table
(
    id          serial PRIMARY KEY NOT NULL,
    table_name  varchar(255)       NOT NULL,
    table_type  varchar(255)       NOT NULL,
    database_id int                NOT NULL,
    description varchar(255),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE metadata_table IS 'metadata of table information';

COMMENT ON COLUMN metadata_table.id IS '主键';
COMMENT ON COLUMN metadata_table.table_name IS 'table name';
COMMENT ON COLUMN metadata_table.table_type IS 'type，such as：database,table,view';
COMMENT ON COLUMN metadata_table.database_id IS 'database id';
COMMENT ON COLUMN metadata_table.description IS 'table description';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_metadata_table
    BEFORE UPDATE
    ON metadata_table
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Table structure for metadata_table_property
DROP TABLE IF EXISTS metadata_table_property;
CREATE TABLE metadata_table_property
(
    key         varchar(255) NOT NULL,
    value       text,
    table_id    int          NOT NULL,
    update_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (key, table_id)
);

COMMENT ON TABLE metadata_table_property IS 'metadata of table configurations';

COMMENT ON COLUMN metadata_table_property.key IS 'key';
COMMENT ON COLUMN metadata_table_property.value IS 'value';
COMMENT ON COLUMN metadata_table_property.table_id IS 'table id';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_metadata_table_property
    BEFORE UPDATE
    ON metadata_table_property
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Table structure for dinky_row_permissions
DROP TABLE IF EXISTS dinky_row_permissions;
CREATE TABLE dinky_row_permissions
(
    id          serial PRIMARY KEY NOT NULL,
    role_id     int                NOT NULL,
    table_name  varchar(255),
    expression  varchar(255),
    create_time timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator     int,
    updater     int
);
COMMENT ON TABLE dinky_row_permissions IS 'row permissions of select';

COMMENT ON COLUMN dinky_row_permissions.id IS 'id';
COMMENT ON COLUMN dinky_row_permissions.role_id IS 'role id';
COMMENT ON COLUMN dinky_row_permissions.table_name IS 'table name';
COMMENT ON COLUMN dinky_row_permissions.expression IS 'expression';

-- 创建触发器以在每次更新记录时设置update_time
CREATE TRIGGER set_update_time_dinky_row_permissions
    BEFORE UPDATE
    ON dinky_row_permissions
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Table structure for dinky_git_project
DROP TABLE IF EXISTS dinky_git_project;
CREATE TABLE dinky_git_project
(
    id                 SERIAL PRIMARY KEY NOT NULL,
    tenant_id          bigint             NOT NULL,
    name               varchar(255)       NOT NULL,
    url                varchar(1000)      NOT NULL,
    branch             varchar(1000)      NOT NULL,
    username           varchar(255),
    password           varchar(255),
    private_key        varchar(255),
    pom                varchar(255),
    build_args         varchar(255),
    code_type          smallint,
    type               smallint           NOT NULL,
    last_build         timestamp,
    description        varchar(255),
    build_state        smallint           NOT NULL DEFAULT 0,
    build_step         smallint           NOT NULL DEFAULT 0,
    enabled            boolean            NOT NULL DEFAULT true,
    udf_class_map_list text,
    order_line         int                NOT NULL DEFAULT 1,
    create_time        timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        timestamp          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator            int,
    updater            int,
    operator           int
);
CREATE INDEX tenant_id_idx ON dinky_git_project (tenant_id);

COMMENT ON TABLE dinky_git_project IS 'Git project configurations';

COMMENT ON COLUMN dinky_git_project.id IS 'id';
COMMENT ON COLUMN dinky_git_project.private_key IS 'keypath';
COMMENT ON COLUMN dinky_git_project.code_type IS 'code type(1-java,2-python)';
COMMENT ON COLUMN dinky_git_project.type IS '1-http ,2-ssh';
COMMENT ON COLUMN dinky_git_project.id IS 'id';
COMMENT ON COLUMN dinky_git_project.build_state IS '0-notStart 1-process 2-failed 3-success';
COMMENT ON COLUMN dinky_git_project.build_step IS 'different from java and python, when build java project, the step value is as follows: 0: environment check 1: clone project 2: compile and build 3: get artifact 4: analyze UDF 5: finish; when build python project, the step value is as follows: 0: environment check 1: clone project 2: get artifact 3: analyze UDF 4: finish';
COMMENT ON COLUMN dinky_git_project.enabled IS '0-disable 1-enable';
COMMENT ON COLUMN dinky_git_project.udf_class_map_list IS 'scan udf class';
COMMENT ON COLUMN dinky_git_project.order_line IS 'order';
-- dinky_git_project
CREATE TRIGGER update_dinky_git_project_modtime
    BEFORE UPDATE
    ON dinky_git_project
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


-- Records of dinky_git_project
INSERT INTO dinky_git_project (id, tenant_id, name, url, branch, username, password, private_key, pom, build_args,
                               code_type, type, last_build, description, build_state, build_step, enabled,
                               udf_class_map_list, order_line)
VALUES (1, 1, 'java-udf', 'https://github.com/zackyoungh/dinky-quickstart-java.git', 'master', NULL, NULL, NULL, NULL,
        '-P flink-1.14', 1, 1, NULL, NULL, 0, 0, true, '[]', 1),
       (2, 1, 'python-udf', 'https://github.com/zackyoungh/dinky-quickstart-python.git', 'master', NULL, NULL, NULL,
        NULL, '', 2, 1, NULL, NULL, 0, 0, true, '[]', 2);


-- Table structure for dinky_metrics
DROP TABLE IF EXISTS dinky_metrics;
CREATE TABLE dinky_metrics
(
    id          SERIAL PRIMARY KEY          NOT NULL,
    task_id     INT,
    vertices    VARCHAR(255),
    metrics     VARCHAR(255),
    position    INT,
    show_type   VARCHAR(255),
    show_size   VARCHAR(255),
    title       TEXT,
    layout_name VARCHAR(255),
    create_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE dinky_metrics IS 'metrics layout';

COMMENT ON COLUMN dinky_metrics.id IS 'id';
COMMENT ON COLUMN dinky_metrics.task_id IS 'task id';
COMMENT ON COLUMN dinky_metrics.vertices IS 'vertices';
COMMENT ON COLUMN dinky_metrics.metrics IS 'metrics';
COMMENT ON COLUMN dinky_metrics.position IS 'position';
COMMENT ON COLUMN dinky_metrics.show_type IS 'show type';
COMMENT ON COLUMN dinky_metrics.show_size IS 'show size';
COMMENT ON COLUMN dinky_metrics.title IS 'title';
COMMENT ON COLUMN dinky_metrics.layout_name IS 'layout_name';
-- dinky_git_project
CREATE TRIGGER update_dinky_dinky_metrics
    BEFORE UPDATE
    ON dinky_metrics
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Table structure for dinky_resources
DROP TABLE IF EXISTS dinky_resources;
CREATE TABLE dinky_resources
(
    id           SERIAL PRIMARY KEY          NOT NULL,
    file_name    VARCHAR(64),
    description  VARCHAR(255),
    user_id      INT,
    type         SMALLINT,
    size         BIGINT,
    pid          INT,
    full_name    VARCHAR(128),
    is_directory SMALLINT,
    create_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator      INT,
    updater      INT
);

CREATE UNIQUE INDEX dinky_resources_un ON dinky_resources (full_name, type);

COMMENT ON TABLE dinky_resources IS 'resources';

COMMENT ON COLUMN dinky_resources.id IS 'key';
COMMENT ON COLUMN dinky_resources.file_name IS 'file name';
COMMENT ON COLUMN dinky_resources.user_id IS 'user id';
COMMENT ON COLUMN dinky_resources.type IS 'resource type,0:FILE，1:UDF';
COMMENT ON COLUMN dinky_resources.size IS 'resource size';

-- dinky_git_project
CREATE TRIGGER update_dinky_dinky_resources
    BEFORE UPDATE
    ON dinky_resources
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Records of dinky_resources
INSERT INTO dinky_resources (id, file_name, description, user_id, type, size, pid, full_name, is_directory)
VALUES (0, 'Root', 'main folder', 1, 0, 0, -1, '/', 1);

-- Table structure for dinky_sys_login_log
DROP TABLE IF EXISTS dinky_sys_login_log;
CREATE TABLE dinky_sys_login_log
(
    id          SERIAL PRIMARY KEY          NOT NULL,
    user_id     INT                         NOT NULL,
    username    VARCHAR(60)                 NOT NULL,
    login_type  INT                         NOT NULL,
    ip          VARCHAR(40)                 NOT NULL,
    status      INT                         NOT NULL,
    msg         TEXT                        NOT NULL,
    access_time TIMESTAMP WITHOUT TIME ZONE,
    create_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted  SMALLINT                    NOT NULL DEFAULT 0
);
COMMENT ON TABLE dinky_sys_login_log IS 'system login log record';

COMMENT ON COLUMN dinky_sys_login_log.id IS 'key';
COMMENT ON COLUMN dinky_sys_login_log.username IS 'username';
COMMENT ON COLUMN dinky_sys_login_log.user_id IS 'user id';
COMMENT ON COLUMN dinky_sys_login_log.login_type IS 'login type（0:LOCAL,1:LDAP）';
COMMENT ON COLUMN dinky_sys_login_log.ip IS 'ip addr';
COMMENT ON COLUMN dinky_sys_login_log.status IS 'login status';
COMMENT ON COLUMN dinky_sys_login_log.msg IS 'status msg';
COMMENT ON COLUMN dinky_sys_login_log.access_time IS 'access time';

-- dinky_git_project
CREATE TRIGGER update_dinky_dinky_sys_login_log
    BEFORE UPDATE
    ON dinky_sys_login_log
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Table structure for dinky_sys_operate_log
DROP TABLE IF EXISTS dinky_sys_operate_log;
CREATE TABLE dinky_sys_operate_log
(
    id               BIGSERIAL PRIMARY KEY NOT NULL,
    module_name      VARCHAR(50),
    business_type    INT DEFAULT 0,
    method           VARCHAR(100),
    request_method   VARCHAR(10),
    operate_name     VARCHAR(50),
    operate_user_id  INT                   NOT NULL,
    operate_url      VARCHAR(255),
    operate_ip       VARCHAR(50),
    operate_location VARCHAR(255),
    operate_param    TEXT,
    json_result      TEXT,
    status           INT,
    error_msg        TEXT,
    operate_time     TIMESTAMP WITHOUT TIME ZONE
);

COMMENT ON TABLE dinky_sys_operate_log IS 'operate log record';

COMMENT ON COLUMN dinky_sys_operate_log.id IS 'key';
COMMENT ON COLUMN dinky_sys_operate_log.module_name IS 'module name';
COMMENT ON COLUMN dinky_sys_operate_log.business_type IS 'business type';
COMMENT ON COLUMN dinky_sys_operate_log.method IS 'method name';
COMMENT ON COLUMN dinky_sys_operate_log.request_method IS 'request method';
COMMENT ON COLUMN dinky_sys_operate_log.operate_name IS 'operate name';
COMMENT ON COLUMN dinky_sys_operate_log.operate_user_id IS 'operate user id';
COMMENT ON COLUMN dinky_sys_operate_log.operate_url IS 'operate url';
COMMENT ON COLUMN dinky_sys_operate_log.operate_ip IS 'ip';
COMMENT ON COLUMN dinky_sys_operate_log.operate_location IS 'operate location';
COMMENT ON COLUMN dinky_sys_operate_log.operate_param IS 'request param';
COMMENT ON COLUMN dinky_sys_operate_log.json_result IS 'return json result';
COMMENT ON COLUMN dinky_sys_operate_log.status IS 'operate status';
COMMENT ON COLUMN dinky_sys_operate_log.error_msg IS 'error msg';
COMMENT ON COLUMN dinky_sys_operate_log.operate_time IS 'operate time';

-- dinky_git_project
CREATE TRIGGER update_dinky_dinky_sys_operate_log
    BEFORE UPDATE
    ON dinky_sys_operate_log
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Table structure for dinky_sys_menu
DROP TABLE IF EXISTS dinky_sys_menu;
CREATE TABLE dinky_sys_menu
(
    id          BIGSERIAL PRIMARY KEY       not null,
    parent_id   BIGINT                      NOT NULL,
    name        VARCHAR(64)                 NOT NULL,
    path        VARCHAR(64),
    component   VARCHAR(64),
    perms       VARCHAR(64),
    icon        VARCHAR(64),
    type        CHAR(1),
    display     smallint                    NOT NULL DEFAULT 1,
    order_num   INT,
    create_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    note        VARCHAR(255)
);
COMMENT ON TABLE dinky_sys_menu IS 'menu';


COMMENT ON COLUMN dinky_sys_menu.id IS 'id';
COMMENT ON COLUMN dinky_sys_menu.parent_id IS 'parent menu id';
COMMENT ON COLUMN dinky_sys_menu.name IS 'menu button name';
COMMENT ON COLUMN dinky_sys_menu.path IS 'routing path';
COMMENT ON COLUMN dinky_sys_menu.component IS 'routing component component';
COMMENT ON COLUMN dinky_sys_menu.perms IS 'authority id';
COMMENT ON COLUMN dinky_sys_menu.icon IS 'icon';
COMMENT ON COLUMN dinky_sys_menu.type IS 'type(M:directory C:menu F:button)';
COMMENT ON COLUMN dinky_sys_menu.display IS 'whether the menu is displayed';
COMMENT ON COLUMN dinky_sys_menu.order_num IS 'sort';

-- dinky_git_project
CREATE TRIGGER update_dinky_dinky_sys_menu
    BEFORE UPDATE
    ON dinky_sys_menu
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- ----------------------------
-- Records of dinky_sys_menu
-- ----------------------------
BEGIN;
insert into dinky_sys_menu
values (1, -1, '首页', '/home', './Home', 'home', 'HomeOutlined', 'C', 0, 1, '2023-08-11 14:06:52',
        '2023-09-25 18:26:45', null);
insert into dinky_sys_menu
values (2, -1, '运维中心', '/devops', null, 'devops', 'ControlOutlined', 'M', 0, 20, '2023-08-11 14:06:52',
        '2023-09-26 14:53:34', null);
insert into dinky_sys_menu
values (3, -1, '注册中心', '/registration', null, 'registration', 'AppstoreOutlined', 'M', 0, 23, '2023-08-11 14:06:52',
        '2023-09-26 14:54:03', null);
insert into dinky_sys_menu
values (4, -1, '认证中心', '/auth', null, 'auth', 'SafetyCertificateOutlined', 'M', 0, 79, '2023-08-11 14:06:52',
        '2023-09-26 15:08:42', null);
insert into dinky_sys_menu
values (5, -1, '数据开发', '/datastudio', './DataStudio', 'datastudio', 'CodeOutlined', 'C', 0, 4,
        '2023-08-11 14:06:52', '2023-09-26 14:49:12', null);
insert into dinky_sys_menu
values (6, -1, '配置中心', '/settings', null, 'settings', 'SettingOutlined', 'M', 0, 115, '2023-08-11 14:06:53',
        '2023-09-26 15:16:03', null);
insert into dinky_sys_menu
values (7, -1, '关于', '/about', './Other/About', 'about', 'SmileOutlined', 'C', 0, 143, '2023-08-11 14:06:53',
        '2023-09-26 15:21:21', null);
insert into dinky_sys_menu
values (8, -1, '监控', '/metrics', './Metrics', 'metrics', 'DashboardOutlined', 'C', 0, 140, '2023-08-11 14:06:53',
        '2023-09-26 15:20:49', null);
insert into dinky_sys_menu
values (9, 3, '集群', '/registration/cluster', null, 'registration:cluster', 'GoldOutlined', 'M', 0, 24,
        '2023-08-11 14:06:54', '2023-09-26 14:54:19', null);
insert into dinky_sys_menu
values (10, 3, '数据源', '/registration/datasource', '', 'registration:datasource', 'DatabaseOutlined', 'M', 0, 37,
        '2023-08-11 14:06:54', '2024-01-18 21:38:56', null);
insert into dinky_sys_menu
values (11, -1, '个人中心', '/account/center', './Other/PersonCenter', 'account:center', 'UserOutlined', 'C', 0, 144,
        '2023-08-11 14:06:54', '2023-09-26 15:21:29', null);
insert into dinky_sys_menu
values (12, 3, '告警', '/registration/alert', null, 'registration:alert', 'AlertOutlined', 'M', 0, 43,
        '2023-08-11 14:06:54', '2023-09-26 15:01:32', null);
insert into dinky_sys_menu
values (13, 3, '文档', '/registration/document', './RegCenter/Document', 'registration:document', 'BookOutlined', 'C',
        0, 55, '2023-08-11 14:06:54', '2023-09-26 15:03:59', null);
insert into dinky_sys_menu
values (14, 3, '全局变量', '/registration/fragment', './RegCenter/GlobalVar', 'registration:fragment', 'RocketOutlined',
        'C', 0, 59, '2023-08-11 14:06:54', '2023-09-26 15:04:55', null);
insert into dinky_sys_menu
values (15, 3, 'Git 项目', '/registration/gitproject', './RegCenter/GitProject', 'registration:gitproject',
        'GithubOutlined', 'C', 0, 63, '2023-08-11 14:06:54', '2023-09-26 15:05:37', null);
insert into dinky_sys_menu
values (16, 3, 'UDF 模版', '/registration/udf', './RegCenter/UDF', 'registration:udf', 'ToolOutlined', 'C', 0, 69,
        '2023-08-11 14:06:54', '2023-09-26 15:06:40', null);
insert into dinky_sys_menu
values (17, 2, '任务详情', '/devops/job-detail', './DevOps/JobDetail', 'devops:job-detail', 'InfoCircleOutlined', 'C',
        0, 22, '2023-08-11 14:06:54', '2024-01-18 22:36:11', null);
insert into dinky_sys_menu
values (18, 2, '任务列表', '/devops/joblist', './DevOps', 'devops:joblist', 'AppstoreFilled', 'C', 0, 21,
        '2023-08-11 14:06:54', '2024-01-18 22:36:00', null);
insert into dinky_sys_menu
values (19, 3, '资源中心', '/registration/resource', './RegCenter/Resource', 'registration:resource', 'FileZipOutlined',
        'C', 0, 73, '2023-08-11 14:06:54', '2023-09-26 15:07:25', null);
insert into dinky_sys_menu
values (20, 4, '角色', '/auth/role', './AuthCenter/Role', 'auth:role', 'TeamOutlined', 'C', 0, 88,
        '2023-08-11 14:06:54', '2023-09-26 15:10:19', null);
insert into dinky_sys_menu
values (21, 4, '用户', '/auth/user', './AuthCenter/User', 'auth:user', 'UserOutlined', 'C', 0, 80,
        '2023-08-11 14:06:54', '2023-09-26 15:08:51', null);
insert into dinky_sys_menu
values (22, 4, '菜单', '/auth/menu', './AuthCenter/Menu', 'auth:menu', 'MenuOutlined', 'C', 0, 94,
        '2023-08-11 14:06:54', '2023-09-26 15:11:34', null);
insert into dinky_sys_menu
values (23, 4, '租户', '/auth/tenant', './AuthCenter/Tenant', 'auth:tenant', 'SecurityScanOutlined', 'C', 0, 104,
        '2023-08-11 14:06:54', '2023-09-26 15:13:35', null);
insert into dinky_sys_menu
values (24, 6, '全局设置', '/settings/globalsetting', './SettingCenter/GlobalSetting', 'settings:globalsetting',
        'SettingOutlined', 'C', 0, 116, '2023-08-11 14:06:54', '2023-09-26 15:16:12', null);
insert into dinky_sys_menu
values (25, 6, '系统日志', '/settings/systemlog', './SettingCenter/SystemLogs', 'settings:systemlog',
        'InfoCircleOutlined', 'C', 0, 131, '2023-08-11 14:06:55', '2023-09-26 15:18:53', null);
insert into dinky_sys_menu
values (26, 6, '进程', '/settings/process', './SettingCenter/Process', 'settings:process', 'ReconciliationOutlined',
        'C', 0, 135, '2023-08-11 14:06:55', '2023-09-26 15:19:35', null);
insert into dinky_sys_menu
values (27, 4, '行权限', '/auth/rowpermissions', './AuthCenter/RowPermissions', 'auth:rowpermissions',
        'SafetyCertificateOutlined', 'C', 0, 100, '2023-08-11 14:06:55', '2023-09-26 15:12:46', null);
insert into dinky_sys_menu
values (28, 9, 'Flink 实例', '/registration/cluster/instance', './RegCenter/Cluster/Instance',
        'registration:cluster:instance', 'ReconciliationOutlined', 'C', 0, 25, '2023-08-11 14:06:55',
        '2023-09-26 14:54:29', null);
insert into dinky_sys_menu
values (29, 12, '告警组', '/registration/alert/group', './RegCenter/Alert/AlertGroup', 'registration:alert:group',
        'AlertOutlined', 'C', 0, 48, '2023-08-11 14:06:55', '2023-09-26 15:02:23', null);
insert into dinky_sys_menu
values (30, 9, '集群配置', '/registration/cluster/config', './RegCenter/Cluster/Configuration',
        'registration:cluster:config', 'SettingOutlined', 'C', 0, 31, '2023-08-11 14:06:55', '2023-09-26 14:57:57',
        null);
insert into dinky_sys_menu
values (31, 12, '告警实例', '/registration/alert/instance', './RegCenter/Alert/AlertInstance',
        'registration:alert:instance', 'AlertFilled', 'C', 0, 44, '2023-08-11 14:06:55', '2023-09-26 15:01:42', null);
insert into dinky_sys_menu
values (32, 1, '作业监控', '/home/jobOverView', 'JobOverView', 'home:jobOverView', 'AntCloudOutlined', 'F', 0, 2,
        '2023-08-15 16:52:59', '2023-09-26 14:48:50', null);
insert into dinky_sys_menu
values (33, 1, '数据开发', '/home/devOverView', 'DevOverView', 'home:devOverView', 'AimOutlined', 'F', 0, 3,
        '2023-08-15 16:54:47', '2023-09-26 14:49:00', null);
insert into dinky_sys_menu
values (34, 5, '项目列表', '/datastudio/left/project', null, 'datastudio:left:project', 'ConsoleSqlOutlined', 'F', 0, 5,
        '2023-09-01 18:00:39', '2023-09-26 14:49:31', null);
insert into dinky_sys_menu
values (35, 5, '数据源', '/datastudio/left/datasource', null, 'datastudio:left:datasource', 'TableOutlined', 'F', 0, 7,
        '2023-09-01 18:01:09', '2023-09-26 14:49:42', null);
insert into dinky_sys_menu
values (36, 5, 'Catalog', '/datastudio/left/catalog', null, 'datastudio:left:catalog', 'DatabaseOutlined', 'F', 0, 6,
        '2023-09-01 18:01:30', '2024-01-18 22:29:41', null);
insert into dinky_sys_menu
values (37, 5, '作业配置', '/datastudio/right/jobConfig', null, 'datastudio:right:jobConfig', 'SettingOutlined', 'F', 0,
        8, '2023-09-01 18:02:15', '2023-09-26 14:50:24', null);
insert into dinky_sys_menu
values (38, 5, '预览配置', '/datastudio/right/previewConfig', null, 'datastudio:right:previewConfig',
        'InsertRowRightOutlined', 'F', 0, 9, '2023-09-01 18:03:08', '2023-09-26 14:50:54', null);
insert into dinky_sys_menu
values (39, 5, '版本历史', '/datastudio/right/historyVision', null, 'datastudio:right:historyVision', 'HistoryOutlined',
        'F', 0, 10, '2023-09-01 18:03:29', '2023-09-26 14:51:03', null);
insert into dinky_sys_menu
values (40, 5, '保存点', '/datastudio/right/savePoint', null, 'datastudio:right:savePoint', 'FolderOutlined', 'F', 0,
        11, '2023-09-01 18:03:58', '2023-09-26 14:51:13', null);
insert into dinky_sys_menu
values (41, 5, '作业信息', '/datastudio/right/jobInfo', null, 'datastudio:right:jobInfo', 'InfoCircleOutlined', 'F', 0,
        8, '2023-09-01 18:04:31', '2023-09-25 18:26:45', null);
insert into dinky_sys_menu
values (42, 5, '控制台', '/datastudio/bottom/console', null, 'datastudio:bottom:console', 'ConsoleSqlOutlined', 'F', 0,
        12, '2023-09-01 18:04:56', '2023-09-26 14:51:24', null);
insert into dinky_sys_menu
values (43, 5, '结果', '/datastudio/bottom/result', null, 'datastudio:bottom:result', 'SearchOutlined', 'F', 0, 13,
        '2023-09-01 18:05:16', '2023-09-26 14:51:36', null);
insert into dinky_sys_menu
values (45, 5, '血缘', '/datastudio/bottom/lineage', null, 'datastudio:bottom:lineage', 'PushpinOutlined', 'F', 0, 15,
        '2023-09-01 18:07:15', '2023-09-26 14:52:00', null);
insert into dinky_sys_menu
values (46, 5, '表数据监控', '/datastudio/bottom/process', null, 'datastudio:bottom:process', 'TableOutlined', 'F', 0,
        16, '2023-09-01 18:07:55', '2023-09-26 14:52:38', null);
insert into dinky_sys_menu
values (47, 5, '小工具', '/datastudio/bottom/tool', null, 'datastudio:bottom:tool', 'ToolOutlined', 'F', 0, 17,
        '2023-09-01 18:08:18', '2023-09-26 14:53:04', null);
insert into dinky_sys_menu
values (48, 28, '新建', '/registration/cluster/instance/add', null, 'registration:cluster:instance:add', 'PlusOutlined',
        'F', 0, 26, '2023-09-06 08:56:45', '2023-09-26 14:56:54', null);
insert into dinky_sys_menu
values (50, 28, '编辑', '/registration/cluster/instance/edit', null, 'registration:cluster:instance:edit',
        'EditOutlined', 'F', 0, 27, '2023-09-06 08:56:45', '2023-09-26 14:56:54', null);
insert into dinky_sys_menu
values (51, 28, '删除', '/registration/cluster/instance/delete', null, 'registration:cluster:instance:delete',
        'DeleteOutlined', 'F', 0, 28, '2023-09-06 08:57:30', '2023-09-26 14:56:54', null);
insert into dinky_sys_menu
values (52, 30, '新建', '/registration/cluster/config/add', null, 'registration:cluster:config:add', 'PlusOutlined',
        'F', 0, 32, '2023-09-06 09:00:31', '2023-09-26 14:58:50', null);
insert into dinky_sys_menu
values (53, 30, '编辑', '/registration/cluster/config/edit', null, 'registration:cluster:config:edit', 'EditOutlined',
        'F', 0, 33, '2023-09-06 08:56:45', '2023-09-26 14:58:50', null);
insert into dinky_sys_menu
values (54, 30, '删除', '/registration/cluster/config/delete', null, 'registration:cluster:config:delete',
        'DeleteOutlined', 'F', 0, 34, '2023-09-06 08:57:30', '2023-09-26 14:58:50', null);
insert into dinky_sys_menu
values (55, 149, '新建', '/registration/datasource/list/add', null, 'registration:datasource:list:add', 'PlusOutlined',
        'F', 0, 38, '2023-09-06 09:01:05', '2024-01-18 22:08:51', null);
insert into dinky_sys_menu
values (56, 149, '编辑', '/registration/datasource/list/edit', null, 'registration:datasource:list:edit',
        'EditOutlined', 'F', 0, 39, '2023-09-06 08:56:45', '2024-01-18 22:09:01', null);
insert into dinky_sys_menu
values (57, 149, '删除', '/registration/datasource/list/delete', null, 'registration:datasource:list:delete',
        'DeleteOutlined', 'F', 0, 40, '2023-09-06 08:57:30', '2024-01-18 22:09:12', null);
insert into dinky_sys_menu
values (58, 31, '新建', '/registration/alert/instance/add', null, 'registration:alert:instance:add', 'PlusOutlined',
        'F', 0, 46, '2023-09-06 09:01:05', '2023-09-26 15:02:04', null);
insert into dinky_sys_menu
values (59, 31, '编辑', '/registration/alert/instance/edit', null, 'registration:alert:instance:edit', 'EditOutlined',
        'F', 0, 45, '2023-09-06 08:56:45', '2023-09-26 15:01:54', null);
insert into dinky_sys_menu
values (60, 31, '删除', '/registration/alert/instance/delete', null, 'registration:alert:instance:delete',
        'DeleteOutlined', 'F', 0, 47, '2023-09-06 08:57:30', '2023-09-26 15:02:13', null);
insert into dinky_sys_menu
values (61, 29, '新建', '/registration/alert/group/add', null, 'registration:alert:group:add', 'PlusOutlined', 'F', 0,
        49, '2023-09-06 09:01:05', '2023-09-26 15:02:48', null);
insert into dinky_sys_menu
values (62, 29, '编辑', '/registration/alert/group/edit', null, 'registration:alert:group:edit', 'EditOutlined', 'F', 0,
        49, '2023-09-06 08:56:45', '2023-09-26 15:02:36', null);
insert into dinky_sys_menu
values (63, 29, '删除', '/registration/alert/group/delete', null, 'registration:alert:group:delete', 'DeleteOutlined',
        'F', 0, 50, '2023-09-06 08:57:30', '2023-09-26 15:03:01', null);
insert into dinky_sys_menu
values (64, 13, '新建', '/registration/document/add', null, 'registration:document:add', 'PlusOutlined', 'F', 0, 57,
        '2023-09-06 09:01:05', '2023-09-26 15:04:22', null);
insert into dinky_sys_menu
values (65, 13, '编辑', '/registration/document/edit', null, 'registration:document:edit', 'EditOutlined', 'F', 0, 56,
        '2023-09-06 08:56:45', '2023-09-26 15:04:13', null);
insert into dinky_sys_menu
values (66, 13, '删除', '/registration/document/delete', null, 'registration:document:delete', 'DeleteOutlined', 'F', 0,
        58, '2023-09-06 08:57:30', '2023-09-26 15:04:32', null);
insert into dinky_sys_menu
values (68, 14, '新建', '/registration/fragment/add', null, 'registration:fragment:add', 'PlusOutlined', 'F', 0, 61,
        '2023-09-06 09:01:05', '2023-09-26 15:05:13', null);
insert into dinky_sys_menu
values (69, 14, '编辑', '/registration/fragment/edit', null, 'registration:fragment:edit', 'EditOutlined', 'F', 0, 60,
        '2023-09-06 08:56:45', '2023-09-26 15:05:04', null);
insert into dinky_sys_menu
values (70, 14, '删除', '/registration/fragment/delete', null, 'registration:fragment:delete', 'DeleteOutlined', 'F', 0,
        62, '2023-09-06 08:57:30', '2023-09-26 15:05:21', null);
insert into dinky_sys_menu
values (72, 15, '新建', '/registration/gitproject/add', null, 'registration:gitproject:add', 'PlusOutlined', 'F', 0, 65,
        '2023-09-06 09:01:05', '2023-09-26 15:06:01', null);
insert into dinky_sys_menu
values (73, 15, '编辑', '/registration/gitproject/edit', null, 'registration:gitproject:edit', 'EditOutlined', 'F', 0,
        64, '2023-09-06 08:56:45', '2023-09-26 15:05:52', null);
insert into dinky_sys_menu
values (74, 15, '删除', '/registration/gitproject/delete', null, 'registration:gitproject:delete', 'DeleteOutlined',
        'F', 0, 66, '2023-09-06 08:57:30', '2023-09-26 15:06:09', null);
insert into dinky_sys_menu
values (76, 15, '构建', '/registration/gitproject/build', null, 'registration:gitproject:build', 'PlaySquareOutlined',
        'F', 0, 67, '2023-09-06 08:57:30', '2023-09-26 15:06:17', null);
insert into dinky_sys_menu
values (77, 15, '查看日志', '/registration/gitproject/showLog', null, 'registration:gitproject:showLog',
        'SearchOutlined', 'F', 0, 68, '2023-09-06 08:57:30', '2023-09-26 15:06:26', null);
insert into dinky_sys_menu
values (78, 16, '新建', '/registration/udf/template/add', null, 'registration:udf:template:add', 'PlusOutlined', 'F', 0,
        71, '2023-09-06 09:01:05', '2023-09-26 15:07:04', null);
insert into dinky_sys_menu
values (79, 16, '编辑', '/registration/udf/template/edit', null, 'registration:udf:template:edit', 'EditOutlined', 'F',
        0, 70, '2023-09-06 08:56:45', '2023-09-26 15:06:48', null);
insert into dinky_sys_menu
values (80, 16, '删除', '/registration/udf/template/delete', null, 'registration:udf:template:delete', 'DeleteOutlined',
        'F', 0, 72, '2023-09-06 08:57:30', '2023-09-26 15:07:12', null);
insert into dinky_sys_menu
values (82, 19, '上传', '/registration/resource/upload', null, 'registration:resource:upload', 'PlusOutlined', 'F', 0,
        77, '2023-09-06 09:01:05', '2023-09-26 15:08:02', null);
insert into dinky_sys_menu
values (83, 19, '重命名', '/registration/resource/rename', null, 'registration:resource:rename', 'EditOutlined', 'F', 0,
        75, '2023-09-06 08:56:45', '2023-09-26 15:07:45', null);
insert into dinky_sys_menu
values (84, 19, '删除', '/registration/resource/delete', null, 'registration:resource:delete', 'DeleteOutlined', 'F', 0,
        76, '2023-09-06 08:57:30', '2023-09-26 15:07:54', null);
insert into dinky_sys_menu
values (85, 19, '创建文件夹', '/registration/resource/addFolder', null, 'registration:resource:addFolder',
        'PlusOutlined', 'F', 0, 74, '2023-09-06 08:57:30', '2023-09-26 15:07:37', null);
insert into dinky_sys_menu
values (86, 4, 'Token 令牌', '/auth/token', './AuthCenter/Token', 'auth:token', 'SecurityScanFilled', 'C', 0, 111,
        '2023-09-05 23:14:23', '2023-09-26 15:15:22', null);
insert into dinky_sys_menu
values (87, 21, '添加', '/auth/user/add', null, 'auth:user:add', 'PlusOutlined', 'F', 0, 81, '2023-09-22 22:06:52',
        '2023-09-26 15:09:49', null);
insert into dinky_sys_menu
values (88, 21, '重置密码', '/auth/user/reset', null, 'auth:user:reset', 'RollbackOutlined', 'F', 0, 84,
        '2023-09-22 22:08:17', '2023-09-26 15:09:49', null);
insert into dinky_sys_menu
values (89, 21, '恢复用户', '/auth/user/recovery', null, 'auth:user:recovery', 'RadiusSettingOutlined', 'F', 0, 85,
        '2023-09-22 22:08:53', '2023-09-26 15:09:49', null);
insert into dinky_sys_menu
values (90, 21, '删除', '/auth/user/delete', null, 'auth:user:delete', 'DeleteOutlined', 'F', 0, 83,
        '2023-09-22 22:09:29', '2023-09-26 15:09:49', null);
insert into dinky_sys_menu
values (91, 21, '修改密码', '/auth/user/changePassword', null, 'auth:user:changePassword', 'EditOutlined', 'F', 0, 86,
        '2023-09-22 22:10:01', '2023-09-26 15:09:49', null);
insert into dinky_sys_menu
values (92, 21, '分配角色', '/auth/user/assignRole', null, 'auth:user:assignRole', 'ForwardOutlined', 'F', 0, 87,
        '2023-09-22 22:10:31', '2023-09-26 15:09:49', null);
insert into dinky_sys_menu
values (93, 21, '编辑', '/auth/user/edit', null, 'auth:user:edit', 'EditOutlined', 'F', 0, 82, '2023-09-22 22:11:41',
        '2023-09-26 15:09:49', null);
insert into dinky_sys_menu
values (94, 20, '添加', '/auth/role/add', null, 'auth:role:add', 'PlusOutlined', 'F', 0, 89, '2023-09-22 22:06:52',
        '2023-09-26 15:11:10', null);
insert into dinky_sys_menu
values (95, 20, '删除', '/auth/role/delete', null, 'auth:role:delete', 'DeleteOutlined', 'F', 0, 91,
        '2023-09-22 22:09:29', '2023-09-26 15:11:10', null);
insert into dinky_sys_menu
values (96, 20, '分配菜单', '/auth/role/assignMenu', null, 'auth:role:assignMenu', 'AntDesignOutlined', 'F', 0, 92,
        '2023-09-22 22:10:31', '2023-09-26 15:11:10', null);
insert into dinky_sys_menu
values (97, 20, '编辑', '/auth/role/edit', null, 'auth:role:edit', 'EditOutlined', 'F', 0, 90, '2023-09-22 22:11:41',
        '2023-09-26 15:11:10', null);
insert into dinky_sys_menu
values (98, 20, '查看用户列表', '/auth/role/viewUser', null, 'auth:role:viewUser', 'FundViewOutlined', 'F', 0, 93,
        '2023-09-22 22:11:41', '2023-09-26 15:11:10', null);
insert into dinky_sys_menu
values (99, 86, '添加 Token', '/auth/token/add', null, 'auth:token:add', 'PlusOutlined', 'F', 0, 112,
        '2023-09-22 22:11:41', '2023-09-26 15:15:46', null);
insert into dinky_sys_menu
values (100, 86, '删除 Token', '/auth/token/delete', null, 'auth:token:delete', 'DeleteOutlined', 'F', 0, 114,
        '2023-09-22 22:11:41', '2023-09-26 15:15:46', null);
insert into dinky_sys_menu
values (101, 86, '修改 Token', '/auth/token/edit', null, 'auth:token:edit', 'EditOutlined', 'F', 0, 113,
        '2023-09-22 22:11:41', '2023-09-26 15:15:46', null);
insert into dinky_sys_menu
values (102, 27, '添加', '/auth/rowPermissions/add', null, 'auth:rowPermissions:add', 'PlusOutlined', 'F', 0, 101,
        '2023-09-22 22:11:41', '2023-09-26 15:13:12', null);
insert into dinky_sys_menu
values (103, 27, '编辑', '/auth/rowPermissions/edit', null, 'auth:rowPermissions:edit', 'EditOutlined', 'F', 0, 102,
        '2023-09-22 22:11:41', '2023-09-26 15:13:12', null);
insert into dinky_sys_menu
values (104, 27, '删除', '/auth/rowPermissions/delete', null, 'auth:rowPermissions:delete', 'DeleteOutlined', 'F', 0,
        103, '2023-09-22 22:11:41', '2023-09-26 15:13:12', null);
insert into dinky_sys_menu
values (105, 23, '添加', '/auth/tenant/add', null, 'auth:tenant:add', 'PlusOutlined', 'F', 0, 105,
        '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
insert into dinky_sys_menu
values (106, 23, '编辑', '/auth/tenant/edit', null, 'auth:tenant:edit', 'EditOutlined', 'F', 0, 106,
        '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
insert into dinky_sys_menu
values (107, 23, '删除', '/auth/tenant/delete', null, 'auth:tenant:delete', 'DeleteOutlined', 'F', 0, 107,
        '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
insert into dinky_sys_menu
values (108, 23, '分配用户', '/auth/tenant/assignUser', null, 'auth:tenant:assignUser', 'EuroOutlined', 'F', 0, 108,
        '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
insert into dinky_sys_menu
values (109, 23, '查看用户', '/auth/tenant/viewUser', null, 'auth:tenant:viewUser', 'FundViewOutlined', 'F', 0, 109,
        '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
insert into dinky_sys_menu
values (110, 23, '设置/取消租户管理员', '/auth/tenant/modifyTenantManager', null, 'auth:tenant:modifyTenantManager',
        'ExclamationCircleOutlined', 'F', 0, 110, '2023-09-22 22:11:41', '2023-09-26 15:15:02', null);
insert into dinky_sys_menu
values (111, 22, '创建根菜单', '/auth/menu/createRoot', null, 'auth:menu:createRoot', 'FolderAddOutlined', 'F', 0, 95,
        '2023-09-22 22:11:41', '2023-09-26 15:12:26', null);
insert into dinky_sys_menu
values (112, 22, '刷新', '/auth/menu/refresh', null, 'auth:menu:refresh', 'ReloadOutlined', 'F', 0, 97,
        '2023-09-22 22:11:41', '2023-09-26 15:12:26', null);
insert into dinky_sys_menu
values (113, 22, '编辑', '/auth/menu/edit', null, 'auth:menu:edit', 'EditOutlined', 'F', 0, 98, '2023-09-22 22:11:41',
        '2023-09-26 15:12:26', null);
insert into dinky_sys_menu
values (114, 22, '添加子项', '/auth/menu/addSub', null, 'auth:menu:addSub', 'PlusOutlined', 'F', 0, 96,
        '2023-09-22 22:11:41', '2023-09-26 15:12:26', null);
insert into dinky_sys_menu
values (115, 22, '删除', '/auth/menu/delete', null, 'auth:menu:delete', 'DeleteOutlined', 'F', 0, 99,
        '2023-09-22 22:11:41', '2023-09-26 15:12:26', null);
insert into dinky_sys_menu
values (116, 6, '告警策略', '/settings/alertrule', './SettingCenter/AlertRule', 'settings:alertrule', 'AndroidOutlined',
        'C', 0, 136, '2023-09-22 23:31:10', '2023-09-26 15:19:52', null);
insert into dinky_sys_menu
values (117, 116, '添加', '/settings/alertrule/add', null, 'settings:alertrule:add', 'PlusOutlined', 'F', 0, 137,
        '2023-09-22 23:34:51', '2023-09-26 15:20:03', null);
insert into dinky_sys_menu
values (118, 116, '删除', '/settings/alertrule/delete', null, 'settings:alertrule:delete', 'DeleteOutlined', 'F', 0,
        139, '2023-09-22 23:35:20', '2023-09-26 15:20:21', null);
insert into dinky_sys_menu
values (119, 116, '编辑', '/settings/alertrule/edit', null, 'settings:alertrule:edit', 'EditOutlined', 'F', 0, 138,
        '2023-09-22 23:36:32', '2023-09-26 15:20:13', null);
insert into dinky_sys_menu
values (120, 8, 'Dinky 服务监控', '/metrics/server', './Metrics/Server', 'metrics:server', 'DashboardOutlined', 'F', 0,
        141, '2023-09-22 23:37:43', '2023-09-26 15:21:00', null);
insert into dinky_sys_menu
values (121, 8, 'Flink 任务监控', '/metrics/job', './Metrics/Job', 'metrics:job', 'DashboardTwoTone', 'C', 0, 142,
        '2023-09-22 23:38:34', '2023-09-26 15:21:08', null);
insert into dinky_sys_menu
values (122, 24, 'Dinky 环境配置', '/settings/globalsetting/dinky', null, 'settings:globalsetting:dinky',
        'SettingOutlined', 'F', 0, 117, '2023-09-22 23:40:30', '2023-09-26 15:16:20', null);
insert into dinky_sys_menu
values (123, 24, 'Flink 环境配置', '/settings/globalsetting/flink', null, 'settings:globalsetting:flink',
        'SettingOutlined', 'F', 0, 119, '2023-09-22 23:40:30', '2023-09-26 15:16:40', null);
insert into dinky_sys_menu
values (124, 24, 'Maven 配置', '/settings/globalsetting/maven', null, 'settings:globalsetting:maven', 'SettingOutlined',
        'F', 0, 121, '2023-09-22 23:40:30', '2023-09-26 15:17:04', null);
insert into dinky_sys_menu
values (125, 24, 'DolphinScheduler 配置', '/settings/globalsetting/ds', null, 'settings:globalsetting:ds',
        'SettingOutlined', 'F', 0, 123, '2023-09-22 23:40:30', '2023-09-26 15:17:23', null);
insert into dinky_sys_menu
values (126, 24, 'LDAP 配置', '/settings/globalsetting/ldap', null, 'settings:globalsetting:ldap', 'SettingOutlined',
        'F', 0, 125, '2023-09-22 23:40:30', '2023-09-26 15:17:41', null);
insert into dinky_sys_menu
values (127, 24, 'Metrics 配置', '/settings/globalsetting/metrics', null, 'settings:globalsetting:metrics',
        'SettingOutlined', 'F', 0, 127, '2023-09-22 23:40:30', '2023-09-26 15:18:06', null);
insert into dinky_sys_menu
values (128, 24, 'Resource 配置', '/settings/globalsetting/resource', null, 'settings:globalsetting:resource',
        'SettingOutlined', 'F', 0, 129, '2023-09-22 23:40:30', '2023-09-26 15:18:27', null);
insert into dinky_sys_menu
values (129, 122, '编辑', '/settings/globalsetting/dinky/edit', null, 'settings:globalsetting:dinky:edit',
        'EditOutlined', 'F', 0, 118, '2023-09-22 23:44:18', '2023-09-26 15:16:29', null);
insert into dinky_sys_menu
values (130, 123, '编辑', '/settings/globalsetting/flink/edit', null, 'settings:globalsetting:flink:edit',
        'EditOutlined', 'F', 0, 120, '2023-09-22 23:44:18', '2023-09-26 15:16:50', null);
insert into dinky_sys_menu
values (131, 124, '编辑', '/settings/globalsetting/maven/edit', null, 'settings:globalsetting:maven:edit',
        'EditOutlined', 'F', 0, 122, '2023-09-22 23:44:18', '2023-09-26 15:17:13', null);
insert into dinky_sys_menu
values (132, 125, '编辑', '/settings/globalsetting/ds/edit', null, 'settings:globalsetting:ds:edit', 'EditOutlined',
        'F', 0, 124, '2023-09-22 23:44:18', '2023-09-26 15:17:32', null);
insert into dinky_sys_menu
values (133, 126, '编辑', '/settings/globalsetting/ldap/edit', null, 'settings:globalsetting:ldap:edit', 'EditOutlined',
        'F', 0, 126, '2023-09-22 23:44:18', '2023-09-26 15:17:51', null);
insert into dinky_sys_menu
values (134, 127, '编辑', '/settings/globalsetting/metrics/edit', null, 'settings:globalsetting:metrics:edit',
        'EditOutlined', 'F', 0, 128, '2023-09-22 23:44:18', '2023-09-26 15:18:16', null);
insert into dinky_sys_menu
values (135, 128, '编辑', '/settings/globalsetting/resource/edit', null, 'settings:globalsetting:resource:edit',
        'EditOutlined', 'F', 0, 130, '2023-09-22 23:44:18', '2023-09-26 15:18:39', null);
insert into dinky_sys_menu
values (136, 12, '告警模版', '/registration/alert/template', './RegCenter/Alert/AlertTemplate',
        'registration:alert:template', 'AlertOutlined', 'C', 0, 51, '2023-09-23 21:34:43', '2023-09-26 15:03:14', null);
insert into dinky_sys_menu
values (137, 136, '添加', '/registration/alert/template/add', null, 'registration:alert:template:add', 'PlusOutlined',
        'F', 0, 52, '2023-09-23 21:36:37', '2023-09-26 15:03:22', null);
insert into dinky_sys_menu
values (138, 136, '编辑', '/registration/alert/template/edit', null, 'registration:alert:template:edit', 'EditOutlined',
        'F', 0, 53, '2023-09-23 21:37:00', '2023-09-26 15:03:30', null);
insert into dinky_sys_menu
values (139, 136, '删除', '/registration/alert/template/delete', null, 'registration:alert:template:delete',
        'DeleteOutlined', 'F', 0, 54, '2023-09-23 21:37:43', '2023-09-26 15:03:37', null);
insert into dinky_sys_menu
values (140, 25, '系统日志', '/settings/systemlog/rootlog', null, 'settings:systemlog:rootlog', 'BankOutlined', 'F', 0,
        133, '2023-09-23 21:43:57', '2023-09-26 15:19:14', null);
insert into dinky_sys_menu
values (141, 25, '日志列表', '/settings/systemlog/loglist', null, 'settings:systemlog:loglist', 'BankOutlined', 'F', 0,
        134, '2023-09-23 21:45:05', '2023-09-26 15:19:23', null);
insert into dinky_sys_menu
values (142, 30, '部署 Session 集群', '/registration/cluster/config/deploy', null, 'registration:cluster:config:deploy',
        'PlayCircleOutlined', 'F', 0, 35, '2023-09-26 13:42:55', '2023-09-26 14:58:50', null);
insert into dinky_sys_menu
values (143, 30, ' 心跳检测', '/registration/cluster/config/heartbeat', null, 'registration:cluster:config:heartbeat',
        'HeartOutlined', 'F', 0, 36, '2023-09-26 13:44:23', '2023-09-26 14:58:50', null);
insert into dinky_sys_menu
values (144, 28, '心跳检测', '/registration/cluster/instance/heartbeat', null,
        'registration:cluster:instance:heartbeat', 'HeartOutlined', 'F', 0, 30, '2023-09-26 13:51:04',
        '2023-09-26 14:57:42', null);
insert into dinky_sys_menu
values (145, 149, '心跳检测', '/registration/datasource/list/heartbeat', null, 'registration:datasource:list:heartbeat',
        'HeartOutlined', 'F', 0, 41, '2023-09-26 14:00:06', '2024-01-18 22:09:26', null);
insert into dinky_sys_menu
values (146, 149, ' 拷贝', '/registration/datasource/list/copy', null, 'registration:datasource:list:copy',
        'CopyOutlined', 'F', 0, 42, '2023-09-26 14:02:28', '2024-01-18 22:09:41', null);
insert into dinky_sys_menu
values (147, 28, '停止 Flink 实例', '/registration/cluster/instance/kill', null, 'registration:cluster:instance:kill',
        'StopTwoTone', 'F', 0, 145, '2024-01-03 11:08:39', '2024-01-03 11:08:39', null);
insert into dinky_sys_menu
values (148, 5, '全局变量', '/datastudio/left/globalVariable', '', 'datastudio:left:globalVariable',
        'CloudServerOutlined', 'F', 0, 146, '2024-01-12 21:58:35', '2024-01-12 21:58:35', null);
insert into dinky_sys_menu
values (149, 10, '数据源列表', '/registration/datasource/list', './RegCenter/DataSource',
        'registration:datasource:list', 'OrderedListOutlined', 'C', 0, 147, '2024-01-18 21:41:04',
        '2024-01-18 21:42:37', null);
insert into dinky_sys_menu
values (150, 10, '数据源详情', '/registration/datasource/detail', './RegCenter/DataSource/components/DataSourceDetail',
        'registration:datasource:detail', 'InfoCircleOutlined', 'C', 0, 148, '2024-01-18 21:43:35',
        '2024-01-18 21:43:35', null);
insert into dinky_sys_menu
values (151, 150, '数据源详情列表树', '/registration/datasource/detail/tree', null,
        'registration:datasource:detail:tree', 'ControlOutlined', 'C', 0, 149, '2024-01-18 21:50:06',
        '2024-01-18 21:50:06', null);
insert into dinky_sys_menu
values (152, 150, '描述', '/registration/datasource/detail/desc', null, 'registration:datasource:detail:desc',
        'SortDescendingOutlined', 'F', 0, 150, '2024-01-18 21:51:02', '2024-01-18 22:10:11', null);
insert into dinky_sys_menu
values (153, 150, '查询', '/registration/datasource/detail/query', null, 'registration:datasource:detail:query',
        'SearchOutlined', 'F', 0, 151, '2024-01-18 21:51:41', '2024-01-18 22:10:21', null);
insert into dinky_sys_menu
values (154, 150, '生成 SQL', '/registration/datasource/detail/gensql', null, 'registration:datasource:detail:gensql',
        'ConsoleSqlOutlined', 'F', 0, 152, '2024-01-18 21:52:06', '2024-01-18 22:10:29', null);
insert into dinky_sys_menu
values (155, 150, ' 控制台', '/registration/datasource/detail/console', null, 'registration:datasource:detail:console',
        'ConsoleSqlOutlined', 'F', 0, 153, '2024-01-18 21:52:47', '2024-01-18 22:10:37', null);
insert into dinky_sys_menu
values (156, 150, ' 刷新', '/registration/datasource/detail/refresh', null, 'registration:datasource:detail:refresh',
        'ReloadOutlined', 'F', 0, 154, '2024-01-18 22:13:47', '2024-01-18 22:13:47', null);

COMMIT;


-- ----------------------------
-- Table structure for dinky_sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS dinky_sys_role_menu;
CREATE TABLE dinky_sys_role_menu
(
    id          BIGSERIAL PRIMARY KEY       NOT NULL,
    role_id     BIGINT                      NOT NULL,
    menu_id     BIGINT                      NOT NULL,
    create_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX un_role_menu_inx ON dinky_sys_role_menu (role_id, menu_id);


COMMENT ON COLUMN dinky_sys_role_menu.id IS 'id';
COMMENT ON COLUMN dinky_sys_role_menu.role_id IS 'role id';
COMMENT ON COLUMN dinky_sys_role_menu.menu_id IS 'menu id';


-- dinky_git_project
CREATE TRIGGER update_dinky_sys_role_menu
    BEFORE UPDATE
    ON dinky_sys_role_menu
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- ----------------------------
-- Table structure for dinky_alert_template
-- ----------------------------
DROP TABLE IF EXISTS dinky_alert_template;
CREATE TABLE dinky_alert_template
(
    id               SERIAL PRIMARY KEY          NOT NULL,
    name             VARCHAR(20),
    template_content TEXT,
    enabled          SMALLINT                             DEFAULT 1,
    create_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator          INT,
    updater          INT
);

COMMENT ON COLUMN dinky_alert_template.id IS 'id';
COMMENT ON COLUMN dinky_alert_template.name IS 'emplate name';
COMMENT ON COLUMN dinky_alert_template.template_content IS 'template content';
COMMENT ON COLUMN dinky_alert_template.enabled IS 'is enable';
-- dinky_git_project
CREATE TRIGGER update_dinky_alert_template
    BEFORE UPDATE
    ON dinky_alert_template
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();



-- ----------------------------
-- Table structure for dinky_alert_rules
-- ----------------------------
DROP TABLE IF EXISTS dinky_alert_rules;
CREATE TABLE dinky_alert_rules
(
    id                 SERIAL PRIMARY KEY,
    name               VARCHAR(40)                 NOT NULL,
    rule               TEXT,
    template_id        INT,
    rule_type          VARCHAR(10),
    trigger_conditions VARCHAR(20),
    description        TEXT,
    enabled            SMALLINT                             DEFAULT 1,
    create_time        TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator            INT,
    updater            INT
);
CREATE UNIQUE INDEX dinky_alert_rules_name ON dinky_alert_rules (name);

COMMENT ON COLUMN dinky_alert_rules.id IS 'id';
COMMENT ON COLUMN dinky_alert_rules.name IS 'rule name';
COMMENT ON COLUMN dinky_alert_rules.rule IS 'specify rule';
COMMENT ON COLUMN dinky_alert_rules.template_id IS 'template id';
COMMENT ON COLUMN dinky_alert_rules.rule_type IS 'alert rule type';
COMMENT ON COLUMN dinky_alert_rules.trigger_conditions IS 'trigger conditions';
COMMENT ON COLUMN dinky_alert_rules.description IS 'description';

-- dinky_git_project
CREATE TRIGGER update_dinky_alert_rules
    BEFORE UPDATE
    ON dinky_alert_rules
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- ----------------------------
-- Table structure for dinky_udf_manage
-- ----------------------------
DROP TABLE IF EXISTS dinky_udf_manage;
CREATE TABLE dinky_udf_manage
(
    id           SERIAL PRIMARY KEY          NOT NULL,
    name         VARCHAR(50),
    class_name   VARCHAR(50),
    task_id      INT,
    resources_id INT,
    enabled      BOOLEAN                              DEFAULT TRUE,
    create_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator      INT,
    updater      INT
);
CREATE INDEX name_resources_id_idx ON dinky_udf_manage (name, resources_id);


COMMENT ON COLUMN dinky_udf_manage.id IS 'id';
COMMENT ON COLUMN dinky_udf_manage.name IS 'udf name';
COMMENT ON COLUMN dinky_udf_manage.class_name IS 'Complete class name';
COMMENT ON COLUMN dinky_udf_manage.task_id IS 'task_id';
COMMENT ON COLUMN dinky_udf_manage.resources_id IS 'resources_id';

-- dinky_git_project
CREATE TRIGGER update_dinky_udf_manage
    BEFORE UPDATE
    ON dinky_udf_manage
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- ----------------------------
-- Table structure for dinky_sys_token
-- ----------------------------
DROP TABLE IF EXISTS dinky_sys_token;
CREATE TABLE dinky_sys_token
(
    id                BIGSERIAL PRIMARY KEY,
    token_value       VARCHAR(255)                NOT NULL,
    user_id           BIGINT                      NOT NULL,
    role_id           BIGINT                      NOT NULL,
    tenant_id         BIGINT                      NOT NULL,
    expire_type       SMALLINT                    NOT NULL,
    expire_start_time TIMESTAMP WITHOUT TIME ZONE,
    expire_end_time   TIMESTAMP WITHOUT TIME ZONE,
    create_time       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    source            SMALLINT,
    creator           BIGINT,
    updater           BIGINT
);
CREATE UNIQUE INDEX token_value_idx ON dinky_sys_token (token_value);
CREATE INDEX source_idx ON dinky_sys_token USING HASH (source);

COMMENT ON COLUMN dinky_sys_token.id IS 'id';
COMMENT ON COLUMN dinky_sys_token.token_value IS 'token value';
COMMENT ON COLUMN dinky_sys_token.user_id IS 'user id';
COMMENT ON COLUMN dinky_sys_token.role_id IS 'role id';
COMMENT ON COLUMN dinky_sys_token.tenant_id IS 'tenant id';
COMMENT ON COLUMN dinky_sys_token.expire_type IS '1: never expire, 2: expire after a period of time, 3: expire at a certain time';
COMMENT ON COLUMN dinky_sys_token.expire_start_time IS 'expire start time ,when expire_type = 3 , it is the start time of the period';
COMMENT ON COLUMN dinky_sys_token.expire_end_time IS 'expire end time ,when expire_type = 2,3 , it is the end time of the period';
COMMENT ON COLUMN dinky_sys_token.source IS '1:login 2:custom';

-- dinky_git_project
CREATE TRIGGER update_dinky_sys_token
    BEFORE UPDATE
    ON dinky_sys_token
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();



INSERT INTO public.dinky_alert_rules (id, name, rule, template_id, rule_type, trigger_conditions, description, enabled,
                                      create_time, update_time, creator, updater)
VALUES (3, 'alert.rule.jobFail',
        '[{"ruleKey":"jobStatus","ruleOperator":"EQ","ruleValue":"''FAILED''","rulePriority":"1"}]', 1, 'SYSTEM',
        ' or ', '', 1, '1970-01-01 00:00:00', '2023-11-22 17:03:44', null, null);
INSERT INTO public.dinky_alert_rules (id, name, rule, template_id, rule_type, trigger_conditions, description, enabled,
                                      create_time, update_time, creator, updater)
VALUES (4, 'alert.rule.getJobInfoFail',
        '[{"ruleKey":"jobStatus","ruleOperator":"EQ","ruleValue":"''UNKNOWN''","rulePriority":"1"}]', 1, 'SYSTEM',
        ' or ', '', 1, '1970-01-01 00:00:00', '2023-11-22 17:03:44', null, null);
INSERT INTO public.dinky_alert_rules (id, name, rule, template_id, rule_type, trigger_conditions, description, enabled,
                                      create_time, update_time, creator, updater)
VALUES (5, 'alert.rule.jobRestart',
        '[{"ruleKey":"jobStatus","ruleOperator":"EQ","ruleValue":"''RESTARTING''","rulePriority":"1"}]', 1, 'SYSTEM',
        ' or ', '', 1, '1970-01-01 00:00:00', '2023-11-22 17:03:44', null, null);
INSERT INTO public.dinky_alert_rules (id, name, rule, template_id, rule_type, trigger_conditions, description, enabled,
                                      create_time, update_time, creator, updater)
VALUES (6, 'alert.rule.checkpointFail', '[{"ruleKey":"isCheckpointFailed","ruleOperator":"EQ","ruleValue":"true"}]', 1,
        'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-11-22 17:03:44', null, null);
INSERT INTO public.dinky_alert_rules (id, name, rule, template_id, rule_type, trigger_conditions, description, enabled,
                                      create_time, update_time, creator, updater)
VALUES (7, 'alert.rule.jobRunException', '[{"ruleKey":"isException","ruleOperator":"EQ","ruleValue":"true"}]', 1,
        'SYSTEM', ' or ', '', 1, '1970-01-01 00:00:00', '2023-11-22 17:03:44', null, null);

INSERT INTO public.dinky_alert_template
VALUES (1, 'Default', '
- **Job Name :** <font color=''gray''>${jobName}</font>
- **Job Status :** <font color=''red''>${jobStatus}</font>
- **Alert Time :** ${alertTime}
- **Start Time :** ${jobStartTime}
- **End Time :** ${jobEndTime}
- **<font color=''red''>${errorMsg}</font>**
[Go toTask Web](http://${taskUrl})
', 1, '2023-11-24 20:41:23', '2023-11-24 20:41:23', null, null);


