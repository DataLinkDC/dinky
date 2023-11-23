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

export enum ProcessType {
  FLINK_EXPLAIN = 'FLINK_EXPLAIN',
  FLINK_EXECUTE = 'FLINK_EXECUTE',
  FLINK_SUBMIT = 'FLINK_SUBMIT',
  SQL_EXPLAIN = 'SQL_EXPLAIN',
  SQL_EXECUTE = 'SQL_EXECUTE',
  SQL_SUBMIT = 'SQL_SUBMIT',
  LINEAGE = 'LINEAGE',
  UNKNOWN = 'UNKNOWN'
}

export enum ProcessStatus {
  INIT = 'INITIALIZING',
  RUNNING = 'RUNNING',
  FAILED = 'FAILED',
  CANCELED = 'CANCELED',
  FINISHED = 'FINISHED',
  UNKNOWN = 'UNKNOWN'
}
