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

/**
 * JobContextHolder
 *
 * @since 2021/6/26 23:29
 */
public class JobContextHolder {

    private static final ThreadLocal<Job> CONTEXT = new ThreadLocal<>();

    public static void setJob(Job job) {
        CONTEXT.set(job);
    }

    public static Job getJob() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
