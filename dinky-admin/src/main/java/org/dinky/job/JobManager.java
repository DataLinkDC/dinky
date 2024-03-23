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

import org.dinky.ServerExecutorService;
import org.dinky.data.annotations.ProcessStep;
import org.dinky.data.enums.ProcessStepType;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;
import org.dinky.data.result.ResultPool;
import org.dinky.data.result.SelectResult;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.SavePointResult;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobManager {
    private ServerExecutorService jobManagerHandler;

    private JobManager(JobConfig config, boolean isPlanMode) {
        registerRemote();
        try {
            jobManagerHandler.init(config, isPlanMode);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerRemote() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");

            // 从Registry中检索远程对象的存根/代理
            jobManagerHandler = (ServerExecutorService) registry.lookup("Compute");
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    public static JobManager build(JobConfig config) {
        return build(config, false);
    }

    public static JobManager build(JobConfig config, boolean isPlanMode) {
        return new JobManager(config, isPlanMode);
    }

    public boolean close() {
        try {
            return jobManagerHandler.close();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectNode getJarStreamGraphJson(String statement) {
        try {
            return jobManagerHandler.getJarStreamGraphJson(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void prepare(String statement) {
        try {
            jobManagerHandler.prepare(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @ProcessStep(type = ProcessStepType.SUBMIT_EXECUTE)
    public JobResult executeJarSql(String statement) throws Exception {
        return jobManagerHandler.executeJarSql(statement);
    }

    @ProcessStep(type = ProcessStepType.SUBMIT_EXECUTE)
    public JobResult executeSql(String statement) throws Exception {
        return jobManagerHandler.executeSql(statement);
    }

    public IResult executeDDL(String statement) {
        try {
            return jobManagerHandler.executeDDL(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static SelectResult getJobData(String jobId) {
        return ResultPool.get(jobId);
    }

    public ExplainResult explainSql(String statement) {
        try {
            return jobManagerHandler.explainSql(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectNode getStreamGraph(String statement) {
        try {
            return jobManagerHandler.getStreamGraph(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String getJobPlanJson(String statement) {
        try {
            return jobManagerHandler.getJobPlanJson(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean cancelNormal(String jobId) {
        try {
            return jobManagerHandler.cancelNormal(jobId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public SavePointResult savepoint(String jobId, SavePointType savePointType, String savePoint) {
        try {
            return jobManagerHandler.savepoint(jobId, savePointType, savePoint);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String exportSql(String sql) {
        try {
            return jobManagerHandler.exportSql(sql);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Job getJob() {
        try {
            return jobManagerHandler.getJob();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
