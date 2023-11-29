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

package org.dinky.daemon.task;

import java.util.Objects;

import lombok.Getter;

@Getter
public class DaemonTaskConfig {

    private final String type;
    private Integer id;

    public DaemonTaskConfig(String type, Integer id) {
        this.type = type;
        this.id = id;
    }

    public DaemonTaskConfig(String type) {
        this.type = type;
    }

    public static DaemonTaskConfig build(String type, Integer id) {
        return new DaemonTaskConfig(type, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DaemonTaskConfig that = (DaemonTaskConfig) o;
        return Objects.equals(type, that.type) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }
}
