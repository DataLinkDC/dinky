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

package org.apache.flink.table.functions.python;

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.catalog.DataTypeFactory;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.inference.TypeInference;
import org.apache.flink.table.types.inference.TypeStrategies;
import org.apache.flink.table.types.utils.TypeConversions;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** The wrapper of user defined python table function. */
@Internal
public class PythonTableFunction extends TableFunction<Row> implements PythonFunction {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final byte[] serializedTableFunction;
    private final PythonFunctionKind pythonFunctionKind;
    private final boolean deterministic;
    private final PythonEnv pythonEnv;
    private final boolean takesRowAsInput;

    private TypeInformation[] inputTypes;
    private String[] inputTypesString;
    private TypeInformation resultType;
    private String resultTypeString;

    public PythonTableFunction(
            String name,
            byte[] serializedScalarFunction,
            TypeInformation[] inputTypes,
            TypeInformation resultType,
            PythonFunctionKind pythonFunctionKind,
            boolean deterministic,
            boolean takesRowAsInput,
            PythonEnv pythonEnv) {
        this(name, serializedScalarFunction, pythonFunctionKind, deterministic, takesRowAsInput, pythonEnv);
        this.inputTypes = inputTypes;
        this.resultType = resultType;
    }

    public PythonTableFunction(
            String name,
            byte[] serializedScalarFunction,
            String[] inputTypesString,
            String resultTypeString,
            PythonFunctionKind pythonFunctionKind,
            boolean deterministic,
            boolean takesRowAsInput,
            PythonEnv pythonEnv) {
        this(name, serializedScalarFunction, pythonFunctionKind, deterministic, takesRowAsInput, pythonEnv);
        this.inputTypesString = inputTypesString;
        this.resultTypeString = resultTypeString;
    }

    public PythonTableFunction(
            String name,
            byte[] serializedScalarFunction,
            PythonFunctionKind pythonFunctionKind,
            boolean deterministic,
            boolean takesRowAsInput,
            PythonEnv pythonEnv) {
        this.name = name;
        this.serializedTableFunction = serializedScalarFunction;
        this.pythonFunctionKind = pythonFunctionKind;
        this.deterministic = deterministic;
        this.pythonEnv = pythonEnv;
        this.takesRowAsInput = takesRowAsInput;
    }

    public void eval(Object... args) {
        throw new UnsupportedOperationException("This method is a placeholder and should not be called.");
    }

    @Override
    public byte[] getSerializedPythonFunction() {
        return serializedTableFunction;
    }

    @Override
    public PythonEnv getPythonEnv() {
        return pythonEnv;
    }

    @Override
    public PythonFunctionKind getPythonFunctionKind() {
        return pythonFunctionKind;
    }

    @Override
    public boolean takesRowAsInput() {
        return takesRowAsInput;
    }

    @Override
    public boolean isDeterministic() {
        return deterministic;
    }

    @Override
    public TypeInformation[] getParameterTypes(Class[] signature) {
        if (inputTypes != null) {
            return inputTypes;
        } else {
            return super.getParameterTypes(signature);
        }
    }

    @Override
    public TypeInformation<Row> getResultType() {
        if (resultType == null && resultTypeString != null) {
            throw new RuntimeException(
                    "String format result type is not supported in old type system. The `register_function` is deprecated, please Use `create_temporary_system_function` instead.");
        }
        return (TypeInformation<Row>) resultType;
    }

    @Override
    public TypeInference getTypeInference(DataTypeFactory typeFactory) {
        TypeInference.Builder builder = TypeInference.newBuilder();

        if (inputTypes != null) {
            final List<DataType> argumentDataTypes = Stream.of(inputTypes)
                    .map(TypeConversions::fromLegacyInfoToDataType)
                    .collect(Collectors.toList());
            builder.typedArguments(argumentDataTypes);
        }

        return builder.outputTypeStrategy(TypeStrategies.explicit(TypeConversions.fromLegacyInfoToDataType(resultType)))
                .build();
    }

    @Override
    public String toString() {
        return name;
    }
}
