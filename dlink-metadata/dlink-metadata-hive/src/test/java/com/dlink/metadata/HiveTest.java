package com.dlink.metadata;

import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MysqlTest
 *
 * @author wenmo
 * @since 2021/7/20 15:32
 **/
public class HiveTest {

    private static final String IP = "cdh1";
    private static final Integer PORT = 10000;
    private static final String hiveDB = "test";
    private static final String username = "zhumingye";
    private static final String passwd = "123456";
    private static final String hive="Hive";

    private static String url = "jdbc:hive2://"+IP+":"+PORT+"/"+hiveDB;

    public Driver getDriver() {
        DriverConfig config = new DriverConfig();
        config.setType(hive);
        config.setName(hive);
        config.setIp(IP);
        config.setPort(PORT);
        config.setUsername(username);
        config.setPassword(passwd);
        config.setUrl(url);
        return Driver.build(config);
    }

    @Test
    public void connectTest() {
        DriverConfig config = new DriverConfig();
        config.setType(hive);
        config.setName(hive);
        config.setIp(IP);
        config.setPort(PORT);
        config.setUsername(username);
        config.setPassword(passwd);
        config.setUrl(url);
        String test = Driver.build(config).test();
        System.out.println(test);
        System.err.println("end...");
    }

    @Test
    public void getDBSTest() {
        Driver driver = getDriver();
        List<Schema> schemasAndTables = driver.listSchemas();
        schemasAndTables.forEach(schema -> {
            System.out.println(schema.getName()+"\t\t"+schema.getTables().toString());
        });
        System.err.println("end...");
    }



    @Test
    public void getTablesByDBTest() throws Exception {
        Driver driver = getDriver();
        driver.execute("use odsp ");
        List<Table> tableList = driver.listTables(hiveDB);
        tableList.forEach(schema -> {
            System.out.println(schema.getName());
        });
        System.err.println("end...");
    }

    @Test
    public void getColumnsByTableTest() {
        Driver driver = getDriver();
        List<Column> columns= driver.listColumns(hiveDB, "biz_college_planner_mysql_language_score_item");
        for (Column column : columns) {
            System.out.println(column.getName()+" \t "+column.getType()+" \t "+column.getComment());
        }
        System.err.println("end...");
    }

    @Test
    public void getCreateTableTest() throws Exception {
        Driver driver = getDriver();
//        JdbcSelectResult jdbcSelectResult = driver.executeSql("show create table odsp.biz_college_planner_mysql_language_score_item", 1);
        Table driverTable = driver.getTable(hiveDB, "biz_college_planner_mysql_language_score_item");
        String createTableSql = driver.getCreateTableSql(driverTable);
        System.out.println(createTableSql);
        System.err.println("end...");
    }


    @Test
    public void getTableExtenedInfoTest() throws Exception {
        Driver driver = getDriver();
        Table driverTable = driver.getTable(hiveDB, "employees");
        for (Column column : driverTable.getColumns()) {
            System.out.println(column.getName()+"\t\t"+column.getType()+"\t\t"+column.getComment());
        }
    }

    /**
     * @Author: zhumingye
     * @date: 202/3/23
     * @Description: 测试hive多条SQL执行
     * @Param:
     * @return:
     */
    @Test
    public void MultipleSQLTest() throws Exception {
        Driver driver = getDriver();
        String sql ="select\n" +
                "   date_format(create_time,'yyyy-MM') as  pay_success_time,\n" +
                "   sum(pay_amount)/100 as amount\n" +
                "from\n" +
                "    odsp.pub_pay_mysql_pay_order\n" +
                "    group by date_format(create_time,'yyyy-MM') ;\n" +
                "select\n" +
                "   *\n" +
                "from\n" +
                "    odsp.pub_pay_mysql_pay_order ;";
        JdbcSelectResult selectResult = driver.executeSql(sql,100);
        for (LinkedHashMap<String, Object> rowDatum : selectResult.getRowData()) {
            Set<Map.Entry<String, Object>> entrySet = rowDatum.entrySet();
            for (Map.Entry<String, Object> stringObjectEntry : entrySet) {
                System.out.println(stringObjectEntry.getKey()+"\t\t"+stringObjectEntry.getValue());
            }
        }
    }

}
