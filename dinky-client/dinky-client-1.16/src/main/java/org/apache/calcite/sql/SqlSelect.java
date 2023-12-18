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

package org.apache.calcite.sql;

import org.dinky.context.CustomTableEnvironmentContext;
import org.dinky.context.RowLevelPermissionsContext;
import org.dinky.executor.ExtendedParser;

import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.util.ImmutableNullableList;
import org.apache.flink.table.delegation.Parser;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * A <code>SqlSelect</code> is a node of a parse tree which represents a select statement. It
 * warrants its own node type just because we have a lot of methods to put somewhere.
 *
 * @description: Modify the value method of where to addCondition() to support row-level permission
 *     filtering
 */
public class SqlSelect extends SqlCall {

    private static final Logger LOG = LoggerFactory.getLogger(SqlSelect.class);

    public static final int FROM_OPERAND = 2;
    public static final int WHERE_OPERAND = 3;
    public static final int HAVING_OPERAND = 5;

    SqlNodeList keywordList;
    SqlNodeList selectList;
    SqlNode from;
    SqlNode where;
    SqlNodeList groupBy;
    SqlNode having;
    SqlNodeList windowDecls;
    SqlNodeList orderBy;
    SqlNode offset;
    SqlNode fetch;
    SqlNodeList hints;

    public SqlSelect(
            SqlParserPos pos,
            SqlNodeList keywordList,
            SqlNodeList selectList,
            SqlNode from,
            SqlNode where,
            SqlNodeList groupBy,
            SqlNode having,
            SqlNodeList windowDecls,
            SqlNodeList orderBy,
            SqlNode offset,
            SqlNode fetch,
            SqlNodeList hints) {
        super(pos);
        this.keywordList = Objects.requireNonNull(keywordList != null ? keywordList : new SqlNodeList(pos));
        this.selectList = selectList;
        this.from = from;
        this.groupBy = groupBy;
        this.having = having;
        this.windowDecls = Objects.requireNonNull(windowDecls != null ? windowDecls : new SqlNodeList(pos));
        this.orderBy = orderBy;
        this.offset = offset;
        this.fetch = fetch;
        this.hints = hints;

        // add row level filter condition for where clause
        this.where = addCondition(from, where, false);
    }

    /** The main process of controlling row-level permissions */
    private SqlNode addCondition(SqlNode from, SqlNode where, boolean fromJoin) {
        if (from instanceof SqlIdentifier) {
            String tableName = from.toString();
            // the table name is used as an alias for join
            String tableAlias = fromJoin ? tableName : null;
            return addPermission(where, tableName, tableAlias);
        } else if (from instanceof SqlJoin) {
            SqlJoin sqlJoin = (SqlJoin) from;
            // support recursive processing, such as join for three tables, process left sqlNode
            where = addCondition(sqlJoin.getLeft(), where, true);
            // process right sqlNode
            return addCondition(sqlJoin.getRight(), where, true);
        } else if (from instanceof SqlBasicCall) {
            // Table has an alias or comes from a subquery
            SqlNode[] tableNodes = ((SqlBasicCall) from).getOperands();
            /**
             * If there is a subquery in the Join, row-level filtering has been appended to the
             * subquery. What is returned here is the SqlSelect type, just return the original where
             * directly
             */
            if (!(tableNodes[0] instanceof SqlIdentifier)) {
                return where;
            }
            String tableName = tableNodes[0].toString();
            String tableAlias = tableNodes[1].toString();
            return addPermission(where, tableName, tableAlias);
        }
        return where;
    }

    /** Add row-level filtering based on user-configured permission points */
    private SqlNode addPermission(SqlNode where, String tableName, String tableAlias) {
        SqlBasicCall permissions = null;
        ConcurrentHashMap<String, String> permissionsMap = RowLevelPermissionsContext.get();
        if (permissionsMap != null) {
            String permissionsStatement = permissionsMap.get(tableName);
            if (permissionsStatement != null && !"".equals(permissionsStatement)) {
                Parser parser = CustomTableEnvironmentContext.get().getParser();
                if (parser instanceof ExtendedParser) {
                    ExtendedParser extendedParser = (ExtendedParser) parser;
                    permissions =
                            (SqlBasicCall) (extendedParser.getCustomParser()).parseExpression(permissionsStatement);
                } else {
                    throw new RuntimeException("CustomParser is not set");
                }
            }
        }

        // add an alias
        if (permissions != null && tableAlias != null) {
            ImmutableList<String> namesList = ImmutableList.of(tableAlias, permissions.getOperands()[0].toString());
            permissions.getOperands()[0] = new SqlIdentifier(namesList, null, new SqlParserPos(0, 0), null);
        }

        return buildWhereClause(where, permissions);
    }

    /** Rebuild the where clause */
    private SqlNode buildWhereClause(SqlNode where, SqlBasicCall permissions) {
        if (permissions != null) {
            if (where == null) {
                return permissions;
            }
            SqlBinaryOperator sqlBinaryOperator =
                    new SqlBinaryOperator(SqlKind.AND.name(), SqlKind.AND, 0, true, null, null, null);
            SqlNode[] operands = new SqlNode[2];
            operands[0] = where;
            operands[1] = permissions;
            SqlParserPos sqlParserPos = new SqlParserPos(0, 0);
            return new SqlBasicCall(sqlBinaryOperator, operands, sqlParserPos);
        }
        return where;
    }

    @Override
    public SqlOperator getOperator() {
        return SqlSelectOperator.INSTANCE;
    }

    @Override
    public SqlKind getKind() {
        return SqlKind.SELECT;
    }

    @Override
    public List<SqlNode> getOperandList() {
        return ImmutableNullableList.of(
                keywordList, selectList, from, where, groupBy, having, windowDecls, orderBy, offset, fetch, hints);
    }

    @Override
    public void setOperand(int i, SqlNode operand) {
        switch (i) {
            case 0:
                keywordList = Objects.requireNonNull((SqlNodeList) operand);
                break;
            case 1:
                selectList = (SqlNodeList) operand;
                break;
            case 2:
                from = operand;
                break;
            case 3:
                where = operand;
                break;
            case 4:
                groupBy = (SqlNodeList) operand;
                break;
            case 5:
                having = operand;
                break;
            case 6:
                windowDecls = Objects.requireNonNull((SqlNodeList) operand);
                break;
            case 7:
                orderBy = (SqlNodeList) operand;
                break;
            case 8:
                offset = operand;
                break;
            case 9:
                fetch = operand;
                break;
            default:
                throw new AssertionError(i);
        }
    }

    public final boolean isDistinct() {
        return getModifierNode(SqlSelectKeyword.DISTINCT) != null;
    }

    public final SqlNode getModifierNode(SqlSelectKeyword modifier) {
        for (SqlNode keyword : keywordList) {
            SqlSelectKeyword keyword2 = ((SqlLiteral) keyword).symbolValue(SqlSelectKeyword.class);
            if (keyword2 == modifier) {
                return keyword;
            }
        }
        return null;
    }

    public final SqlNode getFrom() {
        return from;
    }

    public void setFrom(SqlNode from) {
        this.from = from;
    }

    public final SqlNodeList getGroup() {
        return groupBy;
    }

    public void setGroupBy(SqlNodeList groupBy) {
        this.groupBy = groupBy;
    }

    public final SqlNode getHaving() {
        return having;
    }

    public void setHaving(SqlNode having) {
        this.having = having;
    }

    public final SqlNodeList getSelectList() {
        return selectList;
    }

    public void setSelectList(SqlNodeList selectList) {
        this.selectList = selectList;
    }

    public final SqlNode getWhere() {
        return where;
    }

    public void setWhere(SqlNode whereClause) {
        this.where = whereClause;
    }

    @Nonnull
    public final SqlNodeList getWindowList() {
        return windowDecls;
    }

    public final SqlNodeList getOrderList() {
        return orderBy;
    }

    public void setOrderBy(SqlNodeList orderBy) {
        this.orderBy = orderBy;
    }

    public final SqlNode getOffset() {
        return offset;
    }

    public void setOffset(SqlNode offset) {
        this.offset = offset;
    }

    public final SqlNode getFetch() {
        return fetch;
    }

    public void setFetch(SqlNode fetch) {
        this.fetch = fetch;
    }

    public void setHints(SqlNodeList hints) {
        this.hints = hints;
    }

    public SqlNodeList getHints() {
        return this.hints;
    }

    public boolean hasHints() {
        // The hints may be passed as null explicitly.
        return this.hints != null && this.hints.size() > 0;
    }

    @Override
    public void validate(SqlValidator validator, SqlValidatorScope scope) {
        validator.validateQuery(this, scope, validator.getUnknownType());
    }

    /** Override SqlCall, to introduce a sub-query frame. */
    @Override
    public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
        if (!writer.inQuery()) {
            // If this SELECT is the topmost item in a sub-query, introduce a new
            // frame. (The topmost item in the sub-query might be a UNION or
            // ORDER. In this case, we don't need a wrapper frame.)
            final SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.SUB_QUERY, "(", ")");
            writer.getDialect().unparseCall(writer, this, 0, 0);
            writer.endList(frame);
        } else {
            writer.getDialect().unparseCall(writer, this, leftPrec, rightPrec);
        }
    }

    public boolean hasOrderBy() {
        return orderBy != null && orderBy.size() != 0;
    }

    public boolean hasWhere() {
        return where != null;
    }

    public boolean isKeywordPresent(SqlSelectKeyword targetKeyWord) {
        return getModifierNode(targetKeyWord) != null;
    }
}
