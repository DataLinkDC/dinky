package com.dlink.metadata.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStructDataType;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.dlink.metadata.ast.Clickhouse20CreateTableStatement;

import java.util.List;

public class Clickhouse20OutputVisitor extends SQLASTOutputVisitor implements Clickhouse20Visitor {
    public Clickhouse20OutputVisitor(Appendable appender) {
        super(appender, DbType.clickhouse);
    }

    public Clickhouse20OutputVisitor(Appendable appender, DbType dbType) {
        super(appender, dbType);
    }

    public Clickhouse20OutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(SQLWithSubqueryClause.Entry x) {
        if (x.getExpr() != null) {
            x.getExpr().accept(this);
        } else if (x.getSubQuery() != null) {
            print('(');
            println();
            SQLSelect query = x.getSubQuery();
            if (query != null) {
                query.accept(this);
            } else {
                x.getReturningStatement().accept(this);
            }
            println();
            print(')');
        }
        print(' ');
        print0(ucase ? "AS " : "as ");
        print0(x.getAlias());

        return false;
    }

    public boolean visit(SQLStructDataType x) {
        print0(ucase ? "NESTED (" : "nested (");
        incrementIndent();
        println();
        printlnAndAccept(x.getFields(), ",");
        decrementIndent();
        println();
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLStructDataType.Field x) {
        SQLName name = x.getName();
        if (name != null) {
            name.accept(this);
        }
        SQLDataType dataType = x.getDataType();

        if (dataType != null) {
            print(' ');
            dataType.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(Clickhouse20CreateTableStatement x) {
        super.visit((SQLCreateTableStatement) x);

        SQLExpr partitionBy = x.getPartitionBy();
        if (partitionBy != null) {
            println();
            print0(ucase ? "PARTITION BY " : "partition by ");
            partitionBy.accept(this);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            orderBy.accept(this);
        }

        SQLExpr sampleBy = x.getSampleBy();
        if (sampleBy != null) {
            println();
            print0(ucase ? "SAMPLE BY " : "sample by ");
            sampleBy.accept(this);
        }

        List<SQLAssignItem> settings = x.getSettings();
        if (!settings.isEmpty()) {
            println();
            print0(ucase ? "SETTINGS " : "settings ");
            printAndAccept(settings, ", ");
        }
        return false;
    }

    public boolean visit(SQLAlterTableAddColumn x) {
        print0(ucase ? "ADD COLUMN " : "add column ");
        printAndAccept(x.getColumns(), ", ");
        return false;
    }
}
