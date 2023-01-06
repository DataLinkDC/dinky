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

package com.dlink.process.context;

import com.dlink.assertion.Asserts;
import com.dlink.process.model.ProcessEntity;
import com.dlink.process.pool.ProcessPool;

/**
 * ProcessContextHolder
 *
 * @author wenmo
 * @since 2022/10/16 16:57
 */
public class ProcessContextHolder {

    private static final ThreadLocal<ProcessEntity> PROCESS_CONTEXT = new ThreadLocal<>();

    public static void setProcess(ProcessEntity process) {
        PROCESS_CONTEXT.set(process);
    }

    public static ProcessEntity getProcess() {
        if (Asserts.isNull(PROCESS_CONTEXT.get())) {
            return ProcessEntity.NULL_PROCESS;
        }
        return PROCESS_CONTEXT.get();
    }

    public static void clear() {
        PROCESS_CONTEXT.remove();
    }

    public static ProcessEntity registerProcess(ProcessEntity process) {
        Asserts.checkNull(process, "Process can not be null.");
        setProcess(process);
        ProcessPool.getInstance().push(process.getName(), process);
        return process;
    }

}
