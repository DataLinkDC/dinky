package com.dlink.metadata.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

public class Click20SchemaStatVisitor extends SchemaStatVisitor implements Clickhouse20Visitor {
        {
            dbType = DbType.antspark;
        }

    public Click20SchemaStatVisitor() {
            super(DbType.antspark);
        }

    public Click20SchemaStatVisitor(SchemaRepository repository) {
            super(repository);
        }
}
