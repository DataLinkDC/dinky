package com.dlink.metadata;

import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import org.junit.Test;

import java.util.List;

/**
 * OracleTest
 *
 * @author wenmo
 * @since 2021/7/21 16:14
 **/
public class OracleTest {

    public Driver getDriver(){
        DriverConfig config = new DriverConfig();
        config.setType("Oracle");
        config.setIp("10.1.51.25");
        config.setPort(1521);
        config.setUsername("cdr");
        config.setPassword("cdr");
        config.setUrl("jdbc:oracle:thin:@10.1.51.25:1521:orcl");
        return Driver.build(config).connect();
    }

    @Test
    public void connectTest(){
        DriverConfig config = new DriverConfig();
        config.setType("Oracle");
        config.setIp("10.1.51.25");
        config.setPort(1521);
        config.setUsername("cdr");
        config.setPassword("cdr");
        config.setUrl("jdbc:oracle:thin:@10.1.51.25:1521:orcl");
        String test = Driver.build(config).test();
        System.out.println(test);
        System.out.println("end...");
    }

    @Test
    public void schemaTest(){
        Driver driver = getDriver();
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        System.out.println("end...");
    }

    @Test
    public void columnTest(){
        Driver driver = getDriver();
        List<Column> columns = driver.listColumns("CDR", "PAT_INFO");
        System.out.println("end...");
    }

    @Test
    public void queryTest(){
        Driver driver = getDriver();
        List query = driver.query("select * from CDR.PAT_INFO where ROWNUM<10");
        System.out.println("end...");
    }
}
