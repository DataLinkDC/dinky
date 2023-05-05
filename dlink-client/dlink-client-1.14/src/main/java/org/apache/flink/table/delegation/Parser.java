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

package org.apache.flink.table.delegation;

import org.apache.calcite.sql.SqlNode;
import org.apache.flink.annotation.Internal;
import org.apache.flink.table.api.SqlParserException;
import org.apache.flink.table.catalog.UnresolvedIdentifier;
import org.apache.flink.table.expressions.ResolvedExpression;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.RowType;

import java.util.List;

import javax.annotation.Nullable;

/** Provides methods for parsing SQL objects from a SQL string. */
@Internal
public interface Parser {

    /**
     * Entry point for parsing SQL queries expressed as a String.
     *
     * <p><b>Note:</b>If the created {@link Operation} is a {@link QueryOperation} it must be in a
     * form that will be understood by the {@link Planner#translate(List)} method.
     *
     * <p>The produced Operation trees should already be validated.
     *
     * @param statement the SQL statement to evaluate
     * @return parsed queries as trees of relational {@link Operation}s
     * @throws org.apache.flink.table.api.SqlParserException when failed to parse the statement
     */
    List<Operation> parse(String statement);

    /**
     * Entry point for parsing SQL identifiers expressed as a String.
     *
     * @param identifier the SQL identifier to parse
     * @return parsed identifier
     * @throws org.apache.flink.table.api.SqlParserException when failed to parse the identifier
     */
    UnresolvedIdentifier parseIdentifier(String identifier);

    /**
     * Entry point for parsing SQL expressions expressed as a String.
     *
     * @param sqlExpression the SQL expression to parse
     * @param inputRowType the fields available in the SQL expression
     * @param outputType expected top-level output type if available
     * @return resolved expression
     * @throws org.apache.flink.table.api.SqlParserException when failed to parse the sql expression
     */
    ResolvedExpression parseSqlExpression(
            String sqlExpression, RowType inputRowType, @Nullable LogicalType outputType);

    /**
     * Returns completion hints for the given statement at the given cursor position. The completion
     * happens case insensitively.
     *
     * @param statement Partial or slightly incorrect SQL statement
     * @param position cursor position
     * @return completion hints that fit at the current cursor position
     */
    String[] getCompletionHints(String statement, int position);

    /**
     * Parses a SQL expression into a {@link SqlNode}. The {@link SqlNode} is not yet validated.
     *
     * @param sqlExpression a SQL expression string to parse
     * @return a parsed SQL node
     * @throws SqlParserException if an exception is thrown when parsing the statement
     */
    SqlNode parseExpression(String sqlExpression);

    /**
     * Entry point for parsing SQL and return the abstract syntax tree
     *
     * @param statement the SQL statement to evaluate
     * @return abstract syntax tree
     * @throws org.apache.flink.table.api.SqlParserException when failed to parse the statement
     */
    SqlNode parseSql(String statement);

    /**
     * validate the query
     *
     * @param sqlNode SqlNode to execute on
     * @return validated sqlNode
     */
    SqlNode validate(SqlNode sqlNode);
}
