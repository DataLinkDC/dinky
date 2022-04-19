package com.dlink.cdc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkParamConstant;
import com.dlink.model.FlinkCDCConfig;

/**
 * AbstractCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:28
 **/
public abstract class AbstractCDCBuilder {

    protected FlinkCDCConfig config;

    public AbstractCDCBuilder() {
    }

    public AbstractCDCBuilder(FlinkCDCConfig config) {
        this.config = config;
    }

    public FlinkCDCConfig getConfig() {
        return config;
    }

    public void setConfig(FlinkCDCConfig config) {
        this.config = config;
    }

    public List<String> getSchemaList() {
        List<String> schemaList = new ArrayList<>();
        String schema = config.getSchema();
        if (Asserts.isNullString(schema)) {
            return schemaList;
        }
        String[] schemas = schema.split(FlinkParamConstant.SPLIT);
        Collections.addAll(schemaList, schemas);
        List<String> tableList = getTableList();
        for (String tableName : tableList) {
            if (Asserts.isNotNullString(tableName) && tableName.contains(".")) {
                String[] names = tableName.split(".");
                if (!schemaList.contains(names[0])) {
                    schemaList.add(names[0]);
                }
            }
        }
        return schemaList;
    }

    public List<String> getTableList() {
        List<String> tableList = new ArrayList<>();
        String table = config.getTable();
        if (Asserts.isNullString(table)) {
            return tableList;
        }
        String[] tables = table.split(FlinkParamConstant.SPLIT);
        Collections.addAll(tableList, tables);
        return tableList;
    }

    public String getSchemaFieldName() {
        return "schema";
    }
}
