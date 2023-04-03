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

package org.apache.calcite.rel.metadata;

import org.apache.calcite.plan.RelOptTable;

/**
 * Modified based on calcite's source code org.apache.calcite.rel.metadata.RelColumnOrigin
 * <p>
 * Modification point:
 * <ol>
 *  <li>add transform field and related code.
 * </ol>
 *
 * @description: RelColumnOrigin is a data structure describing one of the origins of an
 * output column produced by a relational expression.
 * @author: HamaWhite
 */
public class RelColumnOrigin {
    // ~ Instance fields --------------------------------------------------------

    private final RelOptTable originTable;

    private final int iOriginColumn;

    private final boolean isDerived;

    /**
     * Stores the expression for data conversion,
     * which source table fields are transformed by which expression the target field
     */
    private String transform;

    // ~ Constructors -----------------------------------------------------------

    public RelColumnOrigin(
            RelOptTable originTable,
            int iOriginColumn,
            boolean isDerived) {
        this.originTable = originTable;
        this.iOriginColumn = iOriginColumn;
        this.isDerived = isDerived;
    }

    public RelColumnOrigin(
            RelOptTable originTable,
            int iOriginColumn,
            boolean isDerived,
            String transform) {
        this.originTable = originTable;
        this.iOriginColumn = iOriginColumn;
        this.isDerived = isDerived;
        this.transform = transform;
    }

    // ~ Methods ----------------------------------------------------------------

    /**
     * Returns table of origin.
     */
    public RelOptTable getOriginTable() {
        return originTable;
    }

    /**
     * Returns the 0-based index of column in origin table; whether this ordinal
     * is flattened or unflattened depends on whether UDT flattening has already
     * been performed on the relational expression which produced this
     * description.
     */
    public int getOriginColumnOrdinal() {
        return iOriginColumn;
    }

    /**
     * Consider the query <code>select a+b as c, d as e from t</code>. The
     * output column c has two origins (a and b), both of them derived. The
     * output column d as one origin (c), which is not derived.
     *
     * @return false if value taken directly from column in origin table; true
     * otherwise
     */
    public boolean isDerived() {
        return isDerived;
    }

    public String getTransform() {
        return transform;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RelColumnOrigin)) {
            return false;
        }
        RelColumnOrigin other = (RelColumnOrigin) obj;
        return originTable.getQualifiedName().equals(
                other.originTable.getQualifiedName())
                && (iOriginColumn == other.iOriginColumn)
                && (isDerived == other.isDerived);
    }

    @Override
    public int hashCode() {
        return originTable.getQualifiedName().hashCode()
                + iOriginColumn + (isDerived ? 313 : 0);
    }
}
