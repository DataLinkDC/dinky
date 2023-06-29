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

import {BaseBeanColumns} from '@/types/Public/data';

/**
 * about alert
 */
declare namespace Alert {
  /**
   * alert group
   */
  export type AlertGroup = BaseBeanColumns & {
    alertInstanceIds: string,
    note: string,
  };

  /**
   * alert instance
   */
  export type AlertInstance = BaseBeanColumns & {
    type: string,
    params: string,
  };
}


export type AlertConfig = {
  type: string,
}

export const ALERT_TYPE = {
  DINGTALK: 'DingTalk',
  WECHAT: 'WeChat',
  FEISHU: 'FeiShu',
  EMAIL: 'Email',
  SMS: 'Sms',
  GROUP: 'Group',
};

export const ALERT_CONFIG_LIST: AlertConfig[] = [{
  type: ALERT_TYPE.DINGTALK,
}, {
  type: ALERT_TYPE.WECHAT,
}, {
  type: ALERT_TYPE.FEISHU,
}, {
  type: ALERT_TYPE.EMAIL,
}, {
  type: ALERT_TYPE.SMS,
}
];


/**
 * about flink cluster
 */
declare namespace Cluster {
  /**
   * flink cluster instance
   * mainly used for `yarn session` `standalone`
   */
  export type Instance = BaseBeanColumns & {
    alias: string,
    type: string,
    hosts: string,
    jobManagerHost: string,
    autoRegisters: boolean,
    version: string,
    status: number,
    note: string,
  };

  /**
   * flink cluster config
   * mainly used for `projob` `application` `k8s` and start a new session cluster
   */
  export type Config = BaseBeanColumns & {
    type: string,
    config?: any,
    configJson: string,
    isAvailable?: boolean,
    note: string,
  };
}

/**
 * about database and metadata
 */
declare namespace DataSources {

  /**
   * database info
   */
  export type DataSource = BaseBeanColumns & {
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
  };


  /**
   * table info
   */
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

  /**
   * table columns info
   */
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

  /**
   * table columns info
   */
  export type SqlGeneration = {
    flinkSqlCreate: string,
    sqlSelect: string,
    sqlCreate: string,
  };

}


/**
 * about document
 */
export type Document = BaseBeanColumns & {
  category: string,
  type: string,
  subtype: string,
  description: string,
  fillValue: string,
  version: string,
  likeNum: number,
};


/**
 * global variable
 */
export type GlobalVar = BaseBeanColumns & {
  fragmentValue: string,
  note: string,
};

export type BuildJarList = {
  jarPath: string;
  orderLine: number;
  classList: string[];
}

/**
 * git project
 */
export type GitProject = BaseBeanColumns & {
  url: string,
  branch: string,
  username: string,
  password: string,
  privateKey: string,
  pom: string,
  buildArgs: string,
  codeType: number,
  type: number,
  lastBuild: Date,
  description: string,
  buildState: number,
  buildStep: number,
  udfClassMapList: string,
  orderLine: number,
}


export type GitProjectTreeNode = {
  name: string;
  path: string;
  content: string;
  size: number;
  leaf: boolean;
  children: GitProjectTreeNode[];
}

export type UDFTemplate = BaseBeanColumns & {
  codeType: string,
  functionType: string,
  templateCode: string
}
