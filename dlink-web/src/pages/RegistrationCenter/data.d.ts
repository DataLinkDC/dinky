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


export type AlertGroupTableListItem = {
  id: number,
  name: string,
  alias: string,
  alertInstanceIds: string,
  note: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};


export type AlertInstanceTableListItem = {
  id: number,
  name: string,
  type: string,
  params: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};


export type ClusterTableListItem = {
  id: number,
  name: string,
  alias: string,
  type: string,
  hosts: string,
  jobManagerHost: string,
  version: string,
  status: number,
  note: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};


export type ClusterConfigurationTableListItem = {
  id: number,
  name: string,
  alias: string,
  type: string,
  config: any,
  configJson: string,
  isAvailable: boolean,
  note: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};


export type DataBaseItem = {
  id: number,
  name: string,
  alias: string,
  groupName: string,
  type: string,
  url: string,
  username: string,
  password: string,
  note: string,
  flinkConfig: string,
  flinkTemplate: string,
  dbVersion: string,
  status: boolean,
  healthTime: Date,
  heartbeatTime: Date,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};

export type Column = {
  name: string,
  type: string,
  comment: string,
  keyFlag: boolean,
  autoIncrement: boolean,
  defaultValue: string,
  nullable: string,
  javaType: string,
  columnFamily: string,
  position: number,
  precision: number,
  scale: number,
  characterSet: string,
  collation: string,
};

export type Table = {
  name: string,
  schema: string,
  catalog: string,
  comment: string,
  type: string,
  engine: string,
  options: string,
  rows: number,
  createTime: string,
  updateTime: string,
};


export type DocumentTableListItem = {
  id: number,
  name: string,
  category: string,
  type: string,
  subtype: string,
  description: string,
  fillValue: string,
  version: string,
  likeNum: number,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};


export type FragmentVariableTableListItem = {
  id: number,
  name: string,
  alias: string,
  fragmentValue: string,
  note: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};


export type JarTableListItem = {
  id: number,
  name: string,
  alias: string,
  type: string,
  path: string,
  mainClass: string,
  paras: string,
  note: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};
