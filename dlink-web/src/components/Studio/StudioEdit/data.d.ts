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


export type BaseDataSourceField = {
  fields: [{
    label?: string,
    displayName?: string,
    aliasName?: string,
    kind?: any,
    insertText?: string,
    insertTextRules?: any,
    detail?: string,
  }]
}

export type BaseDataSourceHeader = {
  fields: [{
    label?: string,
    displayName?: string,
    aliasName?: string,
    kind?: any,
    insertText?: string,
    insertTextRules?: any,
    detail?: string,
  }]
}

export type CompletionItem = {
  label: string,
  kind?: any,
  insertText: string,
  insertTextRules?: any,
  detail?: string,
}
export type StudioParam = {
  useSession:boolean;
  session: string,
  useRemote?:boolean;
  clusterId?: number,
  useResult:boolean;
  maxRowNum?: number,
  statement: string,
  fragment?: boolean,
  jobName?:string,
  parallelism?: number,
  checkPoint?: number,
  savePointPath?: string,
}

export type StudioMetaStoreParam = {
  statement?: string,
  fragment?: boolean,
  dialect?: string,
  envId?: number,
  databaseId?: number,
}

export type CAParam = {
  statement: string,
  statementSet: boolean,
  type: number,
  dialect?: string,
  fragment?: boolean,
  databaseId?: number,
  envId?: number,
}
