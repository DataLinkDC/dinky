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

package com.dlink.function.catalog;

import org.apache.flink.table.functions.FunctionDefinition;

/**
 * UDFunction
 *
 * @author wenmo
 * @since 2021/6/14 22:14
 */
@Deprecated
public class UDFunction {

    public enum UDFunctionType {
        Scalar, Table, Aggregate, TableAggregate
    }

    private String name;
    private UDFunctionType type;
    private FunctionDefinition function;

    public UDFunction(String name, UDFunctionType type, FunctionDefinition function) {
        this.name = name;
        this.type = type;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UDFunctionType getType() {
        return type;
    }

    public void setType(UDFunctionType type) {
        this.type = type;
    }

    public FunctionDefinition getFunction() {
        return function;
    }

    public void setFunction(FunctionDefinition function) {
        this.function = function;
    }
}
