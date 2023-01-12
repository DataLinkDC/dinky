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

package org.dinky.constant;

/**
 * FlinkConstant
 *
 * @author wenmo
 * @since 2021/5/25 14:39
 */
public interface FlinkConstant {

    /** flink端口 */
    Integer FLINK_REST_DEFAULT_PORT = 8081;
    /** flink会话默认个数 */
    Integer DEFAULT_SESSION_COUNT = 256;
    /** flink加载因子 */
    Double DEFAULT_FACTOR = 0.75;
    /** 本地模式host */
    String LOCAL_HOST = "localhost:8081";
    /** changlog op */
    String OP = "op";
}
