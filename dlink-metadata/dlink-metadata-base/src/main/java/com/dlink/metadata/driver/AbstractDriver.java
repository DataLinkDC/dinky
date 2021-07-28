package com.dlink.metadata.driver;

import com.dlink.assertion.Asserts;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.model.Schema;
import com.dlink.model.Table;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AbstractDriver
 *
 * @author wenmo
 * @since 2021/7/19 23:32
 */
public abstract class AbstractDriver implements Driver {

//    public Logger logger = LoggerFactory.getLogger(this.getClass());

    protected DriverConfig config;

    public abstract IDBQuery getDBQuery();

    public abstract ITypeConvert getTypeConvert();

    public boolean canHandle(String type){
        return Asserts.isEqualsIgnoreCase(getType(),type);
    }

    public Driver setDriverConfig(DriverConfig config) {
        this.config = config;
        return this;
    }

    public List<Schema> getSchemasAndTables(){
        return listSchemas().stream().peek(schema -> schema.setTables(listTables(schema.getName()))).sorted().collect(Collectors.toList());
    }

    public List<Table> getTablesAndColumns(String schema){
        return listTables(schema).stream().peek(table -> table.setColumns(listColumns(schema,table.getName()))).sorted().collect(Collectors.toList());
    }

    @Override
    public boolean existTable(Table table){
        return listTables(table.getSchema()).stream().anyMatch(tableItem -> Asserts.isEquals(tableItem.getName(),table.getName()));
    }
}