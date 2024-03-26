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

package org.dinky.gateway.kubernetes.utils;

import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Represents Java Bean properties for YAML serialization with LogRepresenter.
 * If the property value is null, it is ignored.
 */
public class IgnoreNullRepresenter extends Representer {
    /**
     * Represents the Java Bean property, ignoring null and empty values.
     *
     * @param javaBean      The Java Bean object.
     * @param property      The property to represent.
     * @param propertyValue The value of the property.
     * @param tag           The custom tag.
     * @return The represented node tuple, or null if the property value is null.
     */
    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag tag) {
        // Ignore null value
        if (propertyValue == null) {
            return null;
        }
        // Ignore empty map and list
        if (propertyValue instanceof Map) {
            if (((Map<?, ?>) propertyValue).isEmpty()) {
                return null;
            }
        }
        if (propertyValue instanceof List) {
            if (((List<?>) propertyValue).isEmpty()) {
                return null;
            }
        }

        return super.representJavaBeanProperty(javaBean, property, propertyValue, tag);
    }
}
