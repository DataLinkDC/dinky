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

import { RUN_MODE } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Cluster } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { Typography } from 'antd';
import { RuleObject } from 'rc-field-form/es/interface';

const { Text, Paragraph, Link } = Typography;

/**
 * validatorJMHAAdderess
 * @param rule
 * @param value
 */
export const validatorJMHAAdderess = (rule: RuleObject, value = '') => {
  let hostArray = [];
  if (value.trim().length === 0) {
    return Promise.reject(new Error(l('rc.ci.jmhaPlaceholder')));
  } else {
    hostArray = value.split(',');
    for (let i = 0; i < hostArray.length; i++) {
      if (hostArray[i].includes('/')) {
        return Promise.reject(new Error(l('rc.ci.jmha.validate.slash')));
      }
      if (parseInt(hostArray[i].split(':')[1]) >= 65535) {
        return Promise.reject(new Error(l('rc.ci.jmha.validate.port')));
      }
    }
    return Promise.resolve();
  }
};

/**
 * render WebUi icon button
 * @param record
 */
export const renderWebUiRedirect = (record: Cluster.Instance) => {
  if (
    record.status &&
    (record.type === RUN_MODE.YARN_SESSION ||
      record.type === RUN_MODE.STANDALONE ||
      record.type === RUN_MODE.YARN_APPLICATION ||
      record.type === RUN_MODE.YARN_PER_JOB)
  ) {
    return (
      <Link
        href={`${API_CONSTANTS.BASE_URL}/api/flink/${record.jobManagerHost}/#/overview`}
        key={`${record.id}_webui`}
        target='_blank'
      >
        {record.jobManagerHost}
      </Link>
    );
  }
  return <Link>{record.hosts}</Link>;
};
