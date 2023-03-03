package org.dinky.metadata.driver;

import org.dinky.model.Column;
import org.dinky.model.ColumnType;
import org.dinky.model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class MySqlDriverTest {

    private Table table;
    private String flinkConfig;

    @BeforeEach
    void setUp() {
        List<Column> columns =
                Arrays.asList(
                        Column.builder()
                                .name("column1")
                                .type("int")
                                .javaType(ColumnType.INT)
                                .comment("comment abc")
                                .keyFlag(true)
                                .build(),
                        Column.builder()
                                .name("column2")
                                .type("varchar")
                                .javaType(ColumnType.STRING)
                                .comment("comment 'abc'")
                                .keyFlag(true)
                                .build(),
                        Column.builder()
                                .name("column3")
                                .type("double")
                                .javaType(ColumnType.DOUBLE)
                                .comment("comment \"abc\"")
                            .build());

        table = new Table("TableNameOrigin", "SchemaOrigin", columns);

        flinkConfig =
                "${schemaName}=schemaName, ${tableName}=tableName, ${abc}=abc, ${}=null, bcd=bcd";
    }

    @Test
    void genTable() {
        MySqlDriver sqlDriver = new MySqlDriver();
        String gen_table_sql = sqlDriver.genTable(table);

        String expect = "CREATE TABLE IF NOT EXISTS `SchemaOrigin`.`TableNameOrigin` (\n" +
                "  `column1`  int NOT  NULL  COMMENT 'comment abc',\n" +
                "  `column2`  varchar NOT  NULL  COMMENT 'comment 'abc'',\n" +
                "  `column3`  double NOT  NULL  COMMENT 'comment \"abc\"',\n" +
                "  PRIMARY KEY (`column1`,`column2`)\n" +
                ")\n" +
                " ENGINE=null;";
        assertThat(gen_table_sql, equalTo(expect));
    }
}
