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
  `id` int NOT NULL AUTO_INCREMENT COMMENT '实例主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一名称',
  `alias` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '别名',
  `fragment_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '变量值',
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '说明/描述',
  `enabled` tinyint DEFAULT '1' COMMENT '是否启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `un_idx1` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='全局变量';


-- 0.6.7 2022-09-02
-- -----------------------
-- ----------------------------
-- Table structure for dlink_upload_file_record
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dlink_upload_file_record` (
  `id` tinyint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上传文件的类型名称，目前有：hadoop-conf(1)、flink-conf(2)、flink-lib(3)、user-jar(4)、dlink-jar(5)',
  `enabled` tinyint(1) DEFAULT NULL COMMENT '是否可用',
  `file_type` tinyint DEFAULT '-1' COMMENT '上传文件的类型ID，目前有：hadoop-conf(1)、flink-conf(2)、flink-lib(3)、user-jar(4)、dlink-jar(5)，默认值-1表示无类型',
  `target` tinyint NOT NULL COMMENT '上传文件的目的地，目前有：local(1)、hdfs(2)',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件名称',
  `file_parent_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件父路径',
  `file_absolute_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件完全绝对父路径',
  `is_file` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否为文件',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='上传文件记录';

