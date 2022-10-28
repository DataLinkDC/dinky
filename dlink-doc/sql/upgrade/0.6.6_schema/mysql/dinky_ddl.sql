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


-- 0.6.6 2022-07-23
-- ----------------------------
-- ----------------------------
-- Table structure for metadata_database
-- ----------------------------
CREATE TABLE IF NOT EXISTS `metadata_database` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `database_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'database name',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'database description',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='metadata of database information';

-- ----------------------------
-- Table structure for metadata_table
-- ----------------------------
CREATE TABLE IF NOT EXISTS `metadata_table` (
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
-- Table structure for metadata_database_property
-- ----------------------------
CREATE TABLE IF NOT EXISTS  `metadata_database_property` (
  `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'key',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'value',
  `database_id` int NOT NULL COMMENT 'database id',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  PRIMARY KEY (`key`,`database_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='metadata of database configurations';

-- ----------------------------
-- Table structure for metadata_table_property
-- ----------------------------
CREATE TABLE IF NOT EXISTS `metadata_table_property` (
  `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'key',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'value',
  `table_id` int NOT NULL COMMENT 'table id',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create tiime',
  PRIMARY KEY (`key`,`table_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='metadata of table configurations';

-- ----------------------------
-- Table structure for metadata_column
-- ----------------------------
CREATE TABLE IF NOT EXISTS `metadata_column` (
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
-- Table structure for dlink_upload_file_record
-- ----------------------------
CREATE TABLE IF NOT EXISTS `metadata_function` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `function_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'function name',
  `class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'class name',
  `database_id` int NOT NULL COMMENT 'database id',
  `function_language` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'function language',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='UDF informations';

