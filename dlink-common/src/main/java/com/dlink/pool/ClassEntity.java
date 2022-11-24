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

import com.dlink.assertion.Asserts;

import lombok.Getter;
import lombok.Setter;

/**
 * ClassEntity
 *
 * @author wenmo
 * @since 2022/1/12 23:52
 */
@Getter
@Setter
public class ClassEntity {
    private String name;
    private String code;
    private byte[] classByte;

    public ClassEntity(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public ClassEntity(String name, String code, byte[] classByte) {
        this.name = name;
        this.code = code;
        this.classByte = classByte;
    }

    public static ClassEntity build(String name, String code) {
        return new ClassEntity(name, code);
    }

    public boolean equals(ClassEntity entity) {
        if (Asserts.isEquals(name, entity.getName()) && Asserts.isEquals(code, entity.getCode())) {
            return true;
        } else {
            return false;
        }
    }
}
