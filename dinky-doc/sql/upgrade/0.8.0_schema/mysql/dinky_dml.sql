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


delete from `dinky_task_statement` where id in (select id from `dinky_task` where `name` = 'dlink_default_catalog');

delete from `dinky_task` where `name` = 'dlink_default_catalog';

update dinky_udf_template set template_code= 'from pyflink.table import DataTypes\nfrom pyflink.table.udf import udf\n\n\n@udf(result_type=DataTypes.STRING())\ndef ${className}(variable1:str):\n    return \'\''  where id = 5