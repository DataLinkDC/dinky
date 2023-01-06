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

package com.dlink.constant;

public interface NetConstant {
    /**
     * http://
     */
    String HTTP = "http://";
    /**
     * 冒号:
     */
    String COLON = ":";
    /**
     * 斜杠/
     */
    String SLASH = "/";
    /**
     * 连接运行服务器超时时间  1000
     */
    Integer SERVER_TIME_OUT_ACTIVE = 1000;
    /**
     * 读取服务器超时时间  3000
     */
    Integer READ_TIME_OUT = 3000;
    /**
     * 连接FLINK历史服务器超时时间  2000
     */
    Integer SERVER_TIME_OUT_HISTORY = 3000;
}
