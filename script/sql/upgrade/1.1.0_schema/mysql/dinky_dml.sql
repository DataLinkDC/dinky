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

begin;
INSERT INTO `_dinky_flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`,
                                            `installed_by`, `installed_on`, `execution_time`, `success`)
VALUES (1, '1.0.2', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, 'root', '2024-05-20 01:32:29',
        0, 1);

commit ;
