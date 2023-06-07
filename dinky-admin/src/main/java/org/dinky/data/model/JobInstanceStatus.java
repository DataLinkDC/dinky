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

package org.dinky.data.model;

/**
 * JobInstanceStatus
 *
 * @since 2022/2/28 22:25
 */
public class JobInstanceStatus {

    private Integer all = 0;
    private Integer initializing = 0;
    private Integer running = 0;
    private Integer finished = 0;
    private Integer failed = 0;
    private Integer canceled = 0;
    private Integer restarting = 0;
    private Integer created = 0;
    private Integer failing = 0;
    private Integer cancelling = 0;
    private Integer suspended = 0;
    private Integer reconciling = 0;
    private Integer unknown = 0;

    public JobInstanceStatus() {}

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }

    public Integer getInitializing() {
        return initializing;
    }

    public void setInitializing(Integer initializing) {
        this.initializing = initializing;
    }

    public Integer getRunning() {
        return running;
    }

    public void setRunning(Integer running) {
        this.running = running;
    }

    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getCanceled() {
        return canceled;
    }

    public void setCanceled(Integer canceled) {
        this.canceled = canceled;
    }

    public Integer getRestarting() {
        return restarting;
    }

    public void setRestarting(Integer restarting) {
        this.restarting = restarting;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public Integer getFailing() {
        return failing;
    }

    public void setFailing(Integer failing) {
        this.failing = failing;
    }

    public Integer getCancelling() {
        return cancelling;
    }

    public void setCancelling(Integer cancelling) {
        this.cancelling = cancelling;
    }

    public Integer getSuspended() {
        return suspended;
    }

    public void setSuspended(Integer suspended) {
        this.suspended = suspended;
    }

    public Integer getReconciling() {
        return reconciling;
    }

    public void setReconciling(Integer reconciling) {
        this.reconciling = reconciling;
    }

    public Integer getUnknown() {
        return unknown;
    }

    public void setUnknown(Integer unknown) {
        this.unknown = unknown;
    }
}
