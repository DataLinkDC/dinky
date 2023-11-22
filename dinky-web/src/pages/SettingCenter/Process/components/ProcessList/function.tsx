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

import {
  ProcessStatus,
  ProcessType
} from '@/pages/SettingCenter/Process/components/ProcessList/constants';
import { Tag } from 'antd';

export const MatchProcessType = (type: string) => {
  switch (type) {
    case ProcessType.FLINK_EXPLAIN:
      return <Tag color={'blue'}>Flink Explain</Tag>;
    case ProcessType.FLINK_EXECUTE:
      return <Tag color={'pink'}>Flink Execute</Tag>;
    case ProcessType.FLINK_SUBMIT:
      return <Tag color={'purple'}>Flink Submit</Tag>;
    case ProcessType.SQL_EXPLAIN:
      return <Tag color={'cyan'}>SQL Explain</Tag>;
    case ProcessType.SQL_EXECUTE:
      return <Tag color={'orange'}>SQL Execute</Tag>;
    case ProcessType.SQL_SUBMIT:
      return <Tag color={'green'}>SQL Submit</Tag>;
    case ProcessType.LINEAGE:
      return <Tag color={'magenta'}>Lineage</Tag>;
    case ProcessType.UNKNOWN:
      return <Tag color={'default'}>UNKNOWN</Tag>;
  }
};

export const MatchProcessStatus = (status: string) => {
  switch (status) {
    case ProcessStatus.INIT:
      return <Tag color={'default'}>INITIALIZING</Tag>;
    case ProcessStatus.RUNNING:
      return <Tag color={'processing'}>RUNNING</Tag>;
    case ProcessStatus.FAILED:
      return <Tag color={'error'}>FAILED</Tag>;
    case ProcessStatus.CANCELED:
      return <Tag color={'warning'}>CANCELED</Tag>;
    case ProcessStatus.FINISHED:
      return <Tag color={'success'}>FINISHED</Tag>;
    case ProcessStatus.UNKNOWN:
      return <Tag color={'default'}>UNKNOWN</Tag>;
  }
};
