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
-- Table structure for dlink_fragment
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dlink_fragment`(
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'fragment name',
  `alias` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'alias',
  `fragment_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'fragment value',
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'note',
  `enabled` tinyint DEFAULT '1' COMMENT 'is enable',
  `create_time` datetime DEFAULT NULL COMMENT 'create time',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `un_idx1` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='fragment management';


-- 0.6.7 2022-09-02
-- -----------------------
-- ----------------------------
-- Table structure for dlink_upload_file_record
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dlink_upload_file_record` (
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

