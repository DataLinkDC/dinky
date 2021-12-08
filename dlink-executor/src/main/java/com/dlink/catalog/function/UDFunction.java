package com.dlink.catalog.function;

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
