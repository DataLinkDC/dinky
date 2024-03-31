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

package org.dinky.job;

import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.SavePointResult;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ServerExecutorService extends Remote {
    void init(JobConfig config, boolean isPlanMode) throws RemoteException;

    boolean close() throws RemoteException;

    ObjectNode getJarStreamGraphJson(String statement) throws RemoteException;

    JobResult executeJarSql(String statement) throws RemoteException;

    JobResult executeSql(String statement) throws RemoteException;

    IResult executeDDL(String statement) throws RemoteException;

    ExplainResult explainSql(String statement) throws RemoteException;

    ObjectNode getStreamGraph(String statement) throws RemoteException;

    String getJobPlanJson(String statement) throws RemoteException;

    boolean cancelNormal(String jobId) throws RemoteException;

    SavePointResult savepoint(String jobId, SavePointType savePointType, String savePoint) throws RemoteException;

    String exportSql(String sql) throws RemoteException;

    Job getJob() throws RemoteException;

    void prepare(String statement) throws RemoteException;
}
