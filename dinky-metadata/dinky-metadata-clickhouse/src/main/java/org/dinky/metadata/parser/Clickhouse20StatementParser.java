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

import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class Clickhouse20StatementParser extends SQLStatementParser {

    public Clickhouse20StatementParser(String sql) {
        super(new Clickhouse20ExprParser(sql));
    }

    public Clickhouse20StatementParser(String sql, SQLParserFeature... features) {
        super(new Clickhouse20ExprParser(sql, features));
    }

    public Clickhouse20StatementParser(Lexer lexer) {
        super(new Clickhouse20ExprParser(lexer));
    }

    @Override
    public SQLWithSubqueryClause parseWithQuery() {
        SQLWithSubqueryClause withQueryClause = new SQLWithSubqueryClause();
        if (lexer.hasComment() && lexer.isKeepComments()) {
            withQueryClause.addBeforeComment(lexer.readAndResetComments());
        }

        accept(Token.WITH);

        for (; ; ) {
            SQLWithSubqueryClause.Entry entry = new SQLWithSubqueryClause.Entry();
            entry.setParent(withQueryClause);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                switch (lexer.token()) {
                    case VALUES:
                    case WITH:
                    case SELECT:
                        entry.setSubQuery(this.createSQLSelectParser().select());
                        break;
                    default:
                        break;
                }
                accept(Token.RPAREN);

            } else {
                entry.setExpr(exprParser.expr());
            }

            accept(Token.AS);
            String alias = this.lexer.stringVal();
            lexer.nextToken();
            entry.setAlias(alias);

            withQueryClause.addEntry(entry);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return withQueryClause;
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new Clickhouse20CreateTableParser(this.exprParser);
    }
}
