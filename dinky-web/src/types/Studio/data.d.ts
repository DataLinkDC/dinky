/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */


export type Catalogue = {
    id: number;
    name: string;
    tenantId: number;
    taskId: number;
    type: string;
    parentId: number;
    isLeaf: boolean;
    createTime: Date;
    updateTime: Date;
    children: Catalogue[];
    configJson: Object<string, object>;
    task: Task;
};


export type Task = {
    id: number;
    name: string;
    dialect: string;
    tenantId: number;
    type: string;
    checkPoint: number;
    savePointStrategy: number;
    savePointPath: string;
    parallelism: number;
    fragment: boolean;
    statementSet: boolean;
    batchModel: boolean;
    clusterId: number;
    clusterConfigurationId: number;
    databaseId: number;
    jarId: number;
    envId: number;
    alertGroupId: number;
    note: string;
    step: number;
    jobInstanceId: number;
    versionId: number;
    statement: string;
    clusterName: string;
    savePoints: SavePoint[];
    configJson: Object<string, object>;
    path: string;
    jarName: string;
    clusterConfigurationName: string;
    databaseName: string;
    envName: string;
    alertGroupName: string;
    createTime: Date;
    updateTime: Date;
};


export type SavePoint = {
    id: number,
    taskId: number,
    name: string,
    type: string,
    path: string,
    createTime: Date,
};