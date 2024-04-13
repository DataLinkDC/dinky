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

package org.dinky.gateway.utils;

import org.dinky.assertion.Asserts;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author luoshangjie
 * @projectName dinky
 * @description:
 * @date 2024/4/13 12:16
 */
public class ZkUtils {

    private static final String PATH = "/leader/rest_server/connection_info";
    private static final Integer SESSION_TIMEOUT = 3000;

    public static String getJobManagerHost(String quorum, String root, String appId) {
        String jobManagerHost = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try (ZooKeeper zooKeeper = new ZooKeeper(quorum, SESSION_TIMEOUT, watchedEvent -> {}); ) {
            String fullPath = root.endsWith("/") ? root + appId + PATH : root + "/" + appId + PATH;
            Stat stat = zooKeeper.exists(fullPath, false);
            if (Asserts.isNotNull(stat)) {
                byte[] data = zooKeeper.getData(fullPath, false, null);
                if (Asserts.isNotNull(data)) {
                    bais = new ByteArrayInputStream(data);
                    ois = new ObjectInputStream(bais);
                    String leaderAddress = ois.readUTF();
                    if (Asserts.isNotNullString(leaderAddress)) {
                        jobManagerHost = leaderAddress.substring(7);
                    }
                }
            }
        } catch (InterruptedException | KeeperException | IOException e) {
            e.printStackTrace();
        } finally {
            if (Asserts.isNotNull(bais)) {
                try {
                    bais.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            if (Asserts.isNotNull(ois)) {
                try {
                    ois.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        return jobManagerHost;
    }
}
