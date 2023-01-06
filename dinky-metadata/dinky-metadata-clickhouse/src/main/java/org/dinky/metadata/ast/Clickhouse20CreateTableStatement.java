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

package com.dlink.metadata.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.ClickhouseVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class Clickhouse20CreateTableStatement extends SQLCreateTableStatement {
    protected final List<SQLAssignItem> settings = new ArrayList<SQLAssignItem>();
    private SQLOrderBy orderBy;
    private SQLExpr partitionBy;
    private SQLExpr primaryKey;
    private SQLExpr sampleBy;

    public Clickhouse20CreateTableStatement() {
        super(DbType.clickhouse);
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy x) {
        if (x != null) {
            x.setParent(this);
        }

        this.orderBy = x;
    }

    public SQLExpr getPartitionBy() {
        return partitionBy;
    }

    public void setPartitionBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }

        this.partitionBy = x;
    }

    public SQLExpr getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }

        this.primaryKey = x;
    }

    public SQLExpr getSampleBy() {
        return sampleBy;
    }

    public void setSampleBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }

        this.sampleBy = x;
    }

    public List<SQLAssignItem> getSettings() {
        return settings;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof ClickhouseVisitor) {
            ClickhouseVisitor vv = (ClickhouseVisitor) v;
            if (vv.visit(this)) {
                acceptChild(vv);
            }
            vv.endVisit(this);
            return;
        }

        if (v.visit(this)) {
            acceptChild(v);
        }
        v.endVisit(this);
    }
}
