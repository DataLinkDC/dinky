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


import {Tag, Tooltip} from 'antd';

export type StatusCountsType = {
  CREATED: number;
  INITIALIZING: number;
  DEPLOYING: number;
  RUNNING: number;
  FAILED: number;
  FINISHED: number;
  RECONCILING: number;
  SCHEDULED: number;
  CANCELING: number;
  CANCELED: number;
};

export type StatusCountsFormProps = {
  statusCounts: StatusCountsType;
};

const StatusCounts = (props: StatusCountsFormProps) => {

  const {statusCounts} = props;

  return (<>
    {statusCounts?.CREATED > 0 ? (
      <Tooltip title="CREATED"><Tag color="#666">{statusCounts.CREATED}</Tag></Tooltip>) : undefined}
    {statusCounts?.INITIALIZING > 0 ? (
      <Tooltip title="INITIALIZING"><Tag color="#666">{statusCounts.INITIALIZING}</Tag></Tooltip>) : undefined}
    {statusCounts?.DEPLOYING > 0 ? (
      <Tooltip title="DEPLOYING"><Tag color="#666">{statusCounts.DEPLOYING}</Tag></Tooltip>) : undefined}
    {statusCounts?.RUNNING > 0 ? (
      <Tooltip title="RUNNING"><Tag color="#44b549">{statusCounts.RUNNING}</Tag></Tooltip>) : undefined}
    {statusCounts?.FAILED > 0 ? (
      <Tooltip title="FAILED"><Tag color="#ff4d4f">{statusCounts.FAILED}</Tag></Tooltip>) : undefined}
    {statusCounts?.FINISHED > 0 ? (
      <Tooltip title="FINISHED"><Tag color="#108ee9">{statusCounts.FINISHED}</Tag></Tooltip>) : undefined}
    {statusCounts?.RECONCILING > 0 ? (
      <Tooltip title="RECONCILING"><Tag color="#666">{statusCounts.RECONCILING}</Tag></Tooltip>) : undefined}
    {statusCounts?.SCHEDULED > 0 ? (
      <Tooltip title="SCHEDULED"><Tag color="#666">{statusCounts.SCHEDULED}</Tag></Tooltip>) : undefined}
    {statusCounts?.CANCELING > 0 ? (
      <Tooltip title="CANCELING"><Tag color="#feb72b">{statusCounts.CANCELING}</Tag></Tooltip>) : undefined}
    {statusCounts?.CANCELED > 0 ? (
      <Tooltip title="CANCELED"><Tag color="#db970f">{statusCounts.CANCELED}</Tag></Tooltip>) : undefined}
  </>)
};

export default StatusCounts;
