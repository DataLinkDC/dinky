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

CREATE TABLE "public"."_dinky_flyway_schema_history" (
                                                         "installed_rank" int4 NOT NULL,
                                                         "version" varchar(50) COLLATE "pg_catalog"."default",
                                                         "description" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
                                                         "type" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
                                                         "script" varchar(1000) COLLATE "pg_catalog"."default" NOT NULL,
                                                         "checksum" int4,
                                                         "installed_by" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                                         "installed_on" timestamp(6) NOT NULL DEFAULT now(),
                                                         "execution_time" int4 NOT NULL,
                                                         "success" bool NOT NULL DEFAULT false,
                                                         CONSTRAINT "_dinky_flyway_schema_history_pk" PRIMARY KEY ("installed_rank")
)
;

CREATE INDEX "_dinky_flyway_schema_history_s_idx" ON "public"."_dinky_flyway_schema_history" USING btree (
    "success" "pg_catalog"."bool_ops" ASC NULLS LAST
    );