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

export type StudioParam = {
  useSession: boolean;
  session: string;
  useRemote?: boolean;
  clusterId?: number;
  useResult: boolean;
  maxRowNum?: number;
  statement: string;
  fragment?: boolean;
  jobName?: string;
  parallelism?: number;
  checkPoint?: number;
  savePointPath?: string;
};

export enum LeftMenuKey {
  PROJECT_KEY = 'menu.datastudio.project',
  CATALOG_KEY = 'menu.datastudio.catalog',
  DATASOURCE_KEY = 'menu.datastudio.datasource',
  FRAGMENT_KEY = 'menu.registration.fragment'
}
export enum RightMenuKey {
  JOB_CONFIG_KEY = 'menu.datastudio.jobConfig',
  PREVIEW_CONFIG_KEY = 'menu.datastudio.previewConfig',
  SAVEPOINT_KEY = 'menu.datastudio.savePoint',
  HISTORY_VISION_KEY = 'menu.datastudio.historyVision',
  JOB_INFO_KEY = 'menu.datastudio.jobInfo'
}

export enum LeftBottomKey {
  CONSOLE_KEY = 'menu.datastudio.console',
  RESULT_KEY = 'menu.datastudio.result',
  LINEAGE_KEY = 'menu.datastudio.lineage',
  HISTORY_KEY = 'menu.datastudio.history',
  TABLE_DATA_KEY = 'menu.datastudio.table-data',
  TOOLS_KEY = 'menu.datastudio.tool'
}
