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

CREATE TABLE `_dinky_flyway_schema_history` (
                                                `installed_rank` int NOT NULL,
                                                `version` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
                                                `description` varchar(200) COLLATE utf8mb4_general_ci NOT NULL,
                                                `type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
                                                `script` varchar(1000) COLLATE utf8mb4_general_ci NOT NULL,
                                                `checksum` int DEFAULT NULL,
                                                `installed_by` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                                                `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                `execution_time` int NOT NULL,
                                                `success` tinyint(1) NOT NULL,
                                                PRIMARY KEY (`installed_rank`),
                                                KEY `_dinky_flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


SET FOREIGN_KEY_CHECKS = 1;
