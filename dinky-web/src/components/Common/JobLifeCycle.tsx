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


import {Tag} from 'antd';
import {CameraOutlined, CarryOutOutlined, EditOutlined,} from "@ant-design/icons";
import {l} from "@/utils/intl";

export type JobLifeCycleFormProps = {
  step: number | undefined;
};

export const JOB_LIFE_CYCLE = {
  UNKNOWN: 0,
  CREATE: 1,
  DEVELOP: 2,
  DEBUG: 3,
  RELEASE: 4,
  ONLINE: 5,
  CANCEL: 6,
};

export const isDeletedTask = (taskStep: number) => {
  if (taskStep && taskStep === JOB_LIFE_CYCLE.CANCEL) {
    return true;
  }
  return false;
};

const JobLifeCycle = (props: JobLifeCycleFormProps) => {

  const {step} = props;

  const renderJobLifeCycle = () => {
    switch (step) {
      case JOB_LIFE_CYCLE.DEVELOP:
        return (<Tag icon={<EditOutlined/>} color="default">{l('global.table.lifecycle.dev')}</Tag>);
      case JOB_LIFE_CYCLE.RELEASE:
        return (<Tag icon={<CameraOutlined/>} color="green">{l('global.table.lifecycle.publish')}</Tag>);
      case JOB_LIFE_CYCLE.ONLINE:
        return (<Tag icon={<CarryOutOutlined/>} color="blue">{l('global.table.lifecycle.online')}</Tag>);
      default:
        return undefined;
    }
  };

  return (<>{renderJobLifeCycle()}</>)
};

export default JobLifeCycle;
