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

import { handleAddOrUpdate, handleOption } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { DataSources } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';

/**
 * handle test
 * @param item
 */
export const handleTest = async (item: Partial<DataSources.DataSource>) => {
  await handleOption(API_CONSTANTS.DATASOURCE_TEST, l('button.test'), item);
};

/**
 * handle add or update
 * @param item
 */
export const saveOrUpdateHandle = async (item: Partial<DataSources.DataSource>) => {
  await handleAddOrUpdate(API_CONSTANTS.DATASOURCE_ADD_OR_UPDATE, item);
};
