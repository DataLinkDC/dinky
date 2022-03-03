/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.phoenix.compat.hbase;

import org.apache.hadoop.hbase.ipc.RpcScheduler;

/**
 * {@link RpcScheduler} that first checks to see if this is an index or metadata update before
 * passing off the call to the delegate {@link RpcScheduler}.
 */
public abstract class CompatPhoenixRpcScheduler extends RpcScheduler {
    protected RpcScheduler delegate;

    @Override
    public int getMetaPriorityQueueLength() {
        return this.delegate.getMetaPriorityQueueLength();
    }

    @Override
    public int getActiveGeneralRpcHandlerCount() {
        return this.delegate.getActiveGeneralRpcHandlerCount();
    }

    @Override
    public int getActivePriorityRpcHandlerCount() {
        return this.delegate.getActivePriorityRpcHandlerCount();
    }

    @Override
    public int getActiveMetaPriorityRpcHandlerCount() {
        return this.delegate.getActiveMetaPriorityRpcHandlerCount();
    }

    @Override
    public int getActiveReplicationRpcHandlerCount() {
        return this.delegate.getActiveReplicationRpcHandlerCount();
    }

}
