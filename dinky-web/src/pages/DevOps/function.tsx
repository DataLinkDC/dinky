/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import {Tag} from "antd";
import {
  CameraOutlined,
  CarryOutOutlined,
  CheckCircleOutlined, ClockCircleOutlined, CloseCircleOutlined,
  EditOutlined,
  MinusCircleOutlined, QuestionCircleOutlined,
  SyncOutlined
} from "@ant-design/icons";
import {l} from "@/utils/intl";
import {JOB_LIFE_CYCLE, JOB_STATUS} from "@/pages/DevOps/constants";


/**
 * Renders a tag for the job life cycle based on the provided step.
 *
 * @param {number} step - The step in the job life cycle.
 * @returns {JSX.Element} - The tag representing the job life cycle.
 */
export const TagJobLifeCycle = (step?:number) => {
  switch (step) {
    case JOB_LIFE_CYCLE.DEVELOP:
      return (<Tag icon={<EditOutlined/>} color="default">{l('global.table.lifecycle.dev')}</Tag>);
    case JOB_LIFE_CYCLE.RELEASE:
      return (<Tag icon={<CameraOutlined/>} color="green">{l('global.table.lifecycle.publish')}</Tag>);
    case JOB_LIFE_CYCLE.ONLINE:
      return (<Tag icon={<CarryOutOutlined/>} color="blue">{l('global.table.lifecycle.online')}</Tag>);
    default:
      return step;
  }
};

/**
 * Renders a tag for the job status based on the provided status.
 *
 * @param {string|undefined} status - The status of the job.
 * @returns {JSX.Element} - The tag representing the job status.
 */
export const TagJobStatus = (status:string|undefined) => {
  switch (status) {
    case JOB_STATUS.RUNNING:
      return <Tag icon={<SyncOutlined spin/>} color="green">RUNNING</Tag>
    case JOB_STATUS.FINISHED:
      return <Tag icon={<CheckCircleOutlined/>} color="blue">FINISHED</Tag>
    case JOB_STATUS.CANCELED:
      return <Tag icon={<MinusCircleOutlined/>} color="orange">CANCELED</Tag>
    case JOB_STATUS.INITIALIZING:
      return <Tag icon={<ClockCircleOutlined/>} color="default">INITIALIZING</Tag>
    case JOB_STATUS.RESTARTING:
      return <Tag icon={<ClockCircleOutlined/>} color="default">RESTARTING</Tag>
    case JOB_STATUS.CREATED:
      return <Tag icon={<ClockCircleOutlined/>} color="default">CREATED</Tag>
    case JOB_STATUS.UNKNOWN:
      return <Tag icon={<QuestionCircleOutlined/>} color="default">UNKNOWN</Tag>
    default:
      return <Tag icon={<CloseCircleOutlined/>} color="error">FAILED</Tag>;
  }
};

/**
 * Generates an array of options for the life cycle filter.
 *
 * @returns {Array} - An array of objects representing the life cycle filter options.
 */
export const LIFECYCLE_FILTER = () => {
  return [
    {text: l('global.table.lifecycle.dev'), value: JOB_LIFE_CYCLE.DEVELOP},
    {text: l('global.table.lifecycle.publish'), value: JOB_LIFE_CYCLE.RELEASE},
    {text: l('global.table.lifecycle.online'), value: JOB_LIFE_CYCLE.ONLINE},
    {text: l('global.table.lifecycle.unknown'), value: JOB_LIFE_CYCLE.UNKNOWN},
  ]
};

/**
 * Generates an array of options for the job status filter.
 *
 * @returns {Array} - An array of objects representing the job status filter options.
 */
export const JOB_STATUS_FILTER = () => {
  return [
    {text: JOB_STATUS.FINISHED, value: JOB_STATUS.FINISHED},
    {text: JOB_STATUS.RUNNING, value: JOB_STATUS.RUNNING},
    {text: JOB_STATUS.FAILED, value: JOB_STATUS.FAILED},
    {text: JOB_STATUS.CANCELED, value: JOB_STATUS.CANCELED},
    {text: JOB_STATUS.INITIALIZING, value: JOB_STATUS.INITIALIZING},
    {text: JOB_STATUS.RESTARTING, value: JOB_STATUS.RESTARTING},
    {text: JOB_STATUS.CREATED, value: JOB_STATUS.CREATED},
    {text: JOB_STATUS.FAILING, value: JOB_STATUS.FAILING},
    {text: JOB_STATUS.SUSPENDED, value: JOB_STATUS.SUSPENDED},
    {text: JOB_STATUS.CANCELLING, value: JOB_STATUS.CANCELLING},
    {text: JOB_STATUS.UNKNOWN, value: JOB_STATUS.UNKNOWN},
  ]
};


/**
 * Checks if a job status indicates that the job is done.
 *
 * @param {string} type - The job status.
 * @returns {boolean} - True if the job status indicates that the job is done, false otherwise.
 */
export function isStatusDone(type: string) {
  if (!type) {
    return true;
  }
  switch (type) {
    case JOB_STATUS.FAILED:
    case JOB_STATUS.CANCELED:
    case JOB_STATUS.FINISHED:
    case JOB_STATUS.UNKNOWN:
      return true;
    default:
      return false;
  }
}
