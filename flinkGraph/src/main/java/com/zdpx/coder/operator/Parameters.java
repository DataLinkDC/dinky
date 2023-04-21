package com.zdpx.coder.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 计算图参数集
 *
 * @author Licho Sun
 */
public class Parameters {
    private List<Parameter> parameterList = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <T> T getParameterByName(String key) {
        return getParameterList().stream()
                .filter(t -> Objects.equals(t.getKey(), key))
                .findAny()
                .map(value -> (T) value.getValue())
                .orElse(null);
    }

    public void addParameter(String name) {
        addParameter(new Parameter(name));
    }

    public void addParameter(Parameter parameter) {
        parameterList.add(parameter);
    }

    //region g/s
    public List<Parameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<Parameter> parameterList) {
        this.parameterList = parameterList;
    }
    //endregion
}
