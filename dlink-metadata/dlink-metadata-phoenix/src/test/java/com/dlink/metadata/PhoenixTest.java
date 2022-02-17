package com.dlink.metadata;

import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import org.junit.Before;
import org.junit.Test;
import java.util.LinkedHashMap;
import java.util.List;

public class PhoenixTest {

    private Driver driver;

    @Before
    public void init() {
        DriverConfig config = new DriverConfig();
        config.setType("Phoenix");
        config.setUrl("jdbc:phoenix:xxx");
        try {
            driver = Driver.build(config).connect();
        } catch (Exception e) {
            System.err.println("连接创建失败:" + e.getMessage());
        }
    }


    @Test
    public void testSchema() {
        //schema && table
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        for (Schema schemasAndTable : schemasAndTables) {
            List<Table> tables = schemasAndTable.getTables();
            for (Table table : tables) {
                System.out.println(table.getName() + "  " + table.getSchema());
            }
        }
    }
    @Test
    public void testListTables() {
        List<Table> tables = driver.listTables("");
        for (Table table : tables) {
            System.out.println(table.getName() + "  " + table.getSchema());
        }
    }


    @Test
    public void testColumns() {
        // columns
        List<Column> columns = driver.listColumns(null, "ODS_OUTP_PRESC");
        for (Column column : columns) {
            System.out.println(column.getName() + " " + column.getType() + " " + column.getComment());
        }
    }

    @Test
    public void query() {
        JdbcSelectResult selectResult = driver.query("select * from ODS_OUTP_PRESC ", 10);
        List<LinkedHashMap<String, Object>> rowData = selectResult.getRowData();
        for (LinkedHashMap<String, Object> rowDatum : rowData) {
            System.out.println(rowDatum);
        }
    }
}
