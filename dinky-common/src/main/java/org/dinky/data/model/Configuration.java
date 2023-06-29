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

package org.dinky.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class Configuration<T> implements Serializable {
    private final String key;
    @JsonIgnore private final Class<T> type;
    @JsonIgnore private transient Function<T, T> desensitizedHandler = null;
    private final String frontType;
    private final List<String> example = new ArrayList<>();
    private String note;

    private final T defaultValue;
    @JsonIgnore private final transient List<Consumer<T>> changeEventConsumer = new LinkedList<>();

    @JsonIgnore
    private final transient List<Consumer<T>> parameterCheckConsumer = new LinkedList<>();

    private T value;
    private static final List<Class<?>> NUMBER_LIST =
            CollUtil.newArrayList(Double.class, Float.class, Integer.class);

    public Configuration<T> note(String note) {
        this.note = note;
        return this;
    }

    public void setValue(Object value) {
        if (getType() == Enum.class) {
            this.value = (T) EnumUtil.fromString((Class<? extends Enum>) type, (String) value);
            return;
        }
        this.value = type.isInstance(value) ? (T) value : Convert.convert(getType(), value);
    }

    public Configuration<T> desensitizedHandler(Function<T, T> desensitizedHandler) {
        this.desensitizedHandler = desensitizedHandler;
        return this;
    }

    public Configuration(String key, Class<T> type, String note, T defaultValue) {
        this.key = key;
        this.type = type;
        this.note = note;
        this.defaultValue = defaultValue;
        if (type.equals(String.class)) {
            this.frontType = "string";
        } else if (type.equals(Boolean.class)) {
            this.frontType = "boolean";
        } else if (type.equals(Date.class)) {
            this.frontType = "date";
        } else if (NUMBER_LIST.contains(type)) {
            this.frontType = "number";
        } else if (type.isEnum()) {
            this.frontType = "option";
            this.example.addAll(EnumUtil.getNames((Class<? extends Enum<?>>) type));
        } else {
            this.frontType = type.getSimpleName();
        }
    }

    @AllArgsConstructor
    public static class ValueType<T> {
        private final String key;
        private final Class<T> clazz;

        public Configuration<T> defaultValue(T value) {
            return new Configuration<>(key, clazz, "", value);
        }
    }

    public static OptionBuilder key(String key) {
        Assert.notNull(key);
        return new OptionBuilder(key);
    }

    public static class OptionBuilder {

        private final String key;

        public OptionBuilder(String key) {
            this.key = key;
        }

        public ValueType<Boolean> booleanType() {
            return new ValueType<>(key, Boolean.class);
        }

        public ValueType<Integer> intType() {
            return new ValueType<>(key, Integer.class);
        }

        public ValueType<Double> doubleType() {
            return new ValueType<>(key, Double.class);
        }

        public ValueType<Float> floatType() {
            return new ValueType<>(key, Float.class);
        }

        public ValueType<String> stringType() {
            return new ValueType<>(key, String.class);
        }

        public ValueType<Date> dateType() {
            return new ValueType<>(key, Date.class);
        }

        public <E extends Enum<E>> ValueType<E> enumType(Class<E> enumClass) {
            return new ValueType<>(key, enumClass);
        }
    }

    /** @return */
    public Configuration<T> show() {
        if (desensitizedHandler == null) {
            return this;
        } else {
            Configuration<T> tConfiguration = ObjectUtil.clone(this);
            tConfiguration.setValue(desensitizedHandler.apply(value));
            return tConfiguration;
        }
    }

    public void addChangeEvent(Consumer<T> consumer) {
        getChangeEventConsumer().add(consumer);
    }

    public void addParameterCheck(Consumer<T> consumer) {
        getParameterCheckConsumer().add(consumer);
    }

    public void runParameterCheck() {
        getParameterCheckConsumer()
                .forEach(
                        x -> {
                            x.accept(getValue());
                        });
    }

    public void runChangeEvent() {
        getChangeEventConsumer()
                .forEach(
                        x -> {
                            try {
                                x.accept(getValue());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
    }
}
