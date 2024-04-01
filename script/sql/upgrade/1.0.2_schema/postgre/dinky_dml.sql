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


begin ;


update dinky_sys_menu set "type"= 'F' where "id"= 151;
update dinky_sys_menu set "path"= '/datastudio/bottom/table-data' , "perms"= 'datastudio:bottom:table-data' where "id"= 46;

delete from dinky_sys_menu where "id"= 26;

update dinky_git_project set "url"= 'https://github.com/DataLinkDC/dinky-quickstart-java.git' where "id"= 1;
update dinky_git_project set "url"= 'https://github.com/DataLinkDC/dinky-quickstart-python.git' where "id"= 2;

commit ;

