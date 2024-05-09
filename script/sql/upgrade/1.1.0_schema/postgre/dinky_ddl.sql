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

-- Increase class_name column's length from 50 to 100.
ALTER TABLE dinky_udf_manage ALTER COLUMN class_name TYPE VARCHAR(100);

COMMENT ON COLUMN dinky_udf_manage.class_name IS 'Complete class name';

alter table dinky_task add column first_level_owner int;
alter table dinky_task add column second_level_owners varchar(128);

COMMENT ON COLUMN dinky_task.first_level_owner IS 'primary responsible person id';
COMMENT ON COLUMN dinky_task.second_level_owners IS 'list of secondary responsible persons ids';
