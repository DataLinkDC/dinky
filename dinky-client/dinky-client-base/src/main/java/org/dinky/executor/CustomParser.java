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

package org.dinky.executor;

import org.apache.calcite.sql.SqlNode;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.ExtendedParser;

import java.util.List;

public interface CustomParser {
    List<Operation> parse(String statement);

    Parser getParser();

    ExtendedParser getDinkyParser();

    SqlNode parseExpression(String sqlExpression);

    /**
     * Entry point for parsing a SQL and return the abstract syntax tree
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
