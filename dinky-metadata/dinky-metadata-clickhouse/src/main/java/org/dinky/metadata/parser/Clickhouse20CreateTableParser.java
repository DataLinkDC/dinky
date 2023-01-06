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

package org.dinky.metadata.parser;

import org.dinky.metadata.ast.Clickhouse20CreateTableStatement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class Clickhouse20CreateTableParser extends SQLCreateTableParser {
    public Clickhouse20CreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    protected SQLCreateTableStatement newCreateStatement() {
        return new Clickhouse20CreateTableStatement();
    }

    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        Clickhouse20CreateTableStatement ckStmt = (Clickhouse20CreateTableStatement) stmt;
        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            stmt.setEngine(
                    this.exprParser.expr()
            );
        }

        if (lexer.identifierEquals("PARTITION")) {
            lexer.nextToken();
            accept(Token.BY);
            SQLExpr expr = this.exprParser.expr();
            ckStmt.setPartitionBy(expr);
        }

        if (lexer.token() == Token.PRIMARY) {
            lexer.nextToken();
            accept(Token.KEY);
            SQLExpr expr = this.exprParser.expr();
            ckStmt.setPrimaryKey(expr);
        }

        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = this.exprParser.parseOrderBy();
            ckStmt.setOrderBy(orderBy);
        }

        if (lexer.identifierEquals("SAMPLE")) {
            lexer.nextToken();
            accept(Token.BY);
            SQLExpr expr = this.exprParser.expr();
            ckStmt.setSampleBy(expr);
        }

        if (lexer.identifierEquals("SETTINGS")) {
            lexer.nextToken();
            for (; ; ) {
                SQLAssignItem item = this.exprParser.parseAssignItem();
                item.setParent(ckStmt);
                ckStmt.getSettings().add(item);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
        }
    }
}
