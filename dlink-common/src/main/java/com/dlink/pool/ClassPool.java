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


package com.dlink.pool;

import java.util.List;
import java.util.Vector;

/**
 * ClassPool
 *
 * @author wenmo
 * @since 2022/1/12 23:52
 */
public class ClassPool {

    private static volatile List<ClassEntity> classList = new Vector<>();

    public static boolean exist(String name) {
        for (ClassEntity executorEntity : classList) {
            if (executorEntity.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean exist(ClassEntity entity) {
        for (ClassEntity executorEntity : classList) {
            if (executorEntity.equals(entity)) {
                return true;
            }
        }
        return false;
    }

    public static Integer push(ClassEntity executorEntity) {
        if (exist(executorEntity.getName())) {
            remove(executorEntity.getName());
        }
        classList.add(executorEntity);
        return classList.size();
    }

    public static Integer remove(String name) {
        int count = classList.size();
        for (int i = 0; i < classList.size(); i++) {
            if (name.equals(classList.get(i).getName())) {
                classList.remove(i);
                break;
            }
        }
        return count - classList.size();
    }

    public static ClassEntity get(String name) {
        for (ClassEntity executorEntity : classList) {
            if (executorEntity.getName().equals(name)) {
                return executorEntity;
            }
        }
        return null;
    }
}
