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

import { l } from '@/utils/intl';

export const PARAM_DIFF_TABLE_COL = [
  { title: l('pages.datastudio.sql.configItem'), key: 'key', dataIndex: 'key' },
  { title: l('pages.datastudio.sql.cacheConfigItem'), key: 'cache', dataIndex: 'cache' },
  { title: l('pages.datastudio.sql.serverConfigItem'), key: 'server', dataIndex: 'server' }
];

export const DIFF_EDITOR_PARAMS = {
  width: '100%',
  height: '90%',
  options: {
    readOnly: true,
    selectOnLineNumbers: true,
    lineDecorationsWidth: 20,
    mouseWheelZoom: true,
    automaticLayout: true
  },
  language: 'flinksql'
};

export const TASK_VAR_FILTER = [
  'updateTime',
  'createTime',
  'jobInstanceId',
  'useResult',
  'maxRowNum',
  'useChangeLog',
  'useAutoCancel',
  'status',
  'step',
  'jobConfig',
  'note',
  'step',
  'versionId',
  'clusterName',
  'clusterConfigurationName',
  'databaseName',
  'envName',
  'alertGroupName',
  'variables',
  'alertGroup'
];
