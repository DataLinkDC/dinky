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

package org.dinky.data.flink.table;

import lombok.Getter;

@Getter
public class FlinkTableObjectIdentifier {
    private final String catalogName;
    private final String databaseName;
    private final String objectName;

    public static FlinkTableObjectIdentifier of(String catalogName, String databaseName, String objectName) {
        return new FlinkTableObjectIdentifier(catalogName, databaseName, objectName);
    }

    public static FlinkTableObjectIdentifier of(String objectName) {
        return of(null, null, objectName);
    }

    private FlinkTableObjectIdentifier(String catalogName, String databaseName, String objectName) {
        this.catalogName = catalogName;
        this.databaseName = databaseName;
        this.objectName = objectName;
        if (objectName == null) {
            throw new IllegalArgumentException("objectName can not be null");
        }
    }

    /**
     *
     * @return catalogName.`databaseName`.`objectName`
     */
    public String toTablePath() {
        StringBuilder sb = new StringBuilder();
        if (catalogName != null) {
            sb.append(catalogName);
        }
        if (databaseName != null) {
            sb.append(".`");
            sb.append(databaseName);
            sb.append("`.");
        }
        sb.append("`");
        sb.append(objectName);
        sb.append("`");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toTablePath();
    }
}
