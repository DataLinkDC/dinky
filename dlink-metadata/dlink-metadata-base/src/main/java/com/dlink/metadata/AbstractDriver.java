package com.dlink.metadata;

import com.dlink.assertion.Asserts;
import com.dlink.metadata.result.SelectResult;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AbstractDriver
 *
 * @author wenmo
 * @since 2021/7/19 23:32
 */
public abstract class AbstractDriver implements Driver{

    public Logger logger = LoggerFactory.getLogger(this.getClass());

    protected DriverConfig config;

    public boolean canHandle(String type){
        return Asserts.isEqualsIgnoreCase(getType(),type);
    }

    public Driver setDriverConfig(DriverConfig config) {
        this.config = config;
        return this;
    }

    public abstract String getType();

    public abstract boolean test();

    public abstract Driver connect();

    public abstract void close();

    public abstract List<Schema> listSchemas();

    public abstract List<Table> listTables(String schema);

    public abstract List<Column> listColumns(String schema, String table);

    public List<Schema> getSchemasAndTables(){
        return listSchemas().stream().peek(schema -> schema.setTables(listTables(schema.getName()))).sorted().collect(Collectors.toList());
    }

    public List<Table> getTablesAndColumns(String schema){
        return listTables(schema).stream().peek(table -> table.setColumns(listColumns(schema,table.getName()))).sorted().collect(Collectors.toList());
    }

    public abstract boolean existTable(Table table);

    public abstract boolean createTable(Table table);

    public abstract String getCreateTableSql(Table table);

    public abstract boolean deleteTable(Table table);

    public abstract boolean truncateTable(Table table);

    public abstract boolean insert(Table table, JsonNode data);

    public abstract boolean update(Table table, JsonNode data);

    public abstract boolean delete(Table table, JsonNode data);

    public abstract SelectResult select(String sql);

}