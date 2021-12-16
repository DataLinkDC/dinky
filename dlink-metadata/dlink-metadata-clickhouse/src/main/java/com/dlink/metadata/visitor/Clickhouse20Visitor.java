package com.dlink.metadata.visitor;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.dlink.metadata.ast.Clickhouse20CreateTableStatement;

public interface Clickhouse20Visitor extends SQLASTVisitor {
    default boolean visit(Clickhouse20CreateTableStatement x) {
        return true;
    }

    default void endVisit(Clickhouse20CreateTableStatement x) {
    }
}
