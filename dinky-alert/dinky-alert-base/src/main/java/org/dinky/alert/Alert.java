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

package org.dinky.alert;

import org.dinky.assertion.Asserts;

import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Alert
 *
 * @since 2022/2/23 19:05
 */
public interface Alert {

    static Optional<Alert> get(AlertConfig config) {
        Asserts.checkNotNull(config, "报警组件配置不能为空");
        ServiceLoader<Alert> alerts = ServiceLoader.load(Alert.class);
        for (Alert alert : alerts) {
            if (alert.canHandle(config.getType())) {
                return Optional.of(alert.setConfig(config));
            }
        }
        return Optional.empty();
    }

    static Alert build(AlertConfig config) {
        String key = config.getName();
        if (AlertPool.exist(key)) {
            return AlertPool.get(key);
        }
        Optional<Alert> optionalDriver = Alert.get(config);
        if (!optionalDriver.isPresent()) {
            throw new AlertException("不支持报警组件类型【" + config.getType() + "】，请在 lib 下添加扩展依赖");
        }
        Alert driver = optionalDriver.get();
        AlertPool.push(key, driver);
        return driver;
    }

    static Alert buildTest(AlertConfig config) {
        Optional<Alert> optionalDriver = Alert.get(config);
        if (!optionalDriver.isPresent()) {
            throw new AlertException("不支持报警组件类型【" + config.getType() + "】，请在 lib 下添加扩展依赖");
        }
        return optionalDriver.get();
    }

    Alert setConfig(AlertConfig config);

    default boolean canHandle(String type) {
        return Asserts.isEqualsIgnoreCase(getType(), type);
    }

    String getType();

    AlertResult send(String title, String content);
}
