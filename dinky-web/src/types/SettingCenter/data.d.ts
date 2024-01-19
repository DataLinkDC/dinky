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

export type ProcessSteps = {
  stepStatus: string;
  info: string;
  error: string;
  startTime: Date;
  endTime: Date;
  time: string;
};

export type Process = {
  pid: string;
  name: string;
  taskId: number;
  taskName: string;
  type: string;
  status: string;
  startTime: Date;
  endTime: Date;
  time: string;
  nullProcess: boolean;
  stepIndex: number;
  steps: ProcessSteps[];
  userId: number;
};

export type Rule = {
  ruleKey: string;
  ruleOperator: string;
  ruleValue: number;
};

export type AlertRule = {
  id: number;
  name: string;
  templateId: number;
  description: string;
  ruleType: string;
  triggerConditions: string;
  rule: any;
  enabled: boolean;
};

// ============================  System Settings ============================
export type Settings = {
  dolphinscheduler: BaseConfigProperties[];
  env: BaseConfigProperties[];
  flink: BaseConfigProperties[];
  maven: BaseConfigProperties[];
  ldap: BaseConfigProperties[];
  metrics: BaseConfigProperties[];
  resource: BaseConfigProperties[];
};

export type BaseConfigProperties = {
  key: string;
  name: string;
  value: any;
  note: string;
  frontType: string;
  example: string[];
};

export interface LogInfo {
  id: number;
  name: string;
  path: string;
  content: string;
  parentId: number;
  size: number;
  desc: string;
  children: LogInfo[];
  leaf: boolean;
}
