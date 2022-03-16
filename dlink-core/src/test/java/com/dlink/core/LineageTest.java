package com.dlink.core;

import com.dlink.explainer.lineage.LineageBuilder;
import com.dlink.explainer.lineage.LineageResult;
import org.junit.Test;

/**
 * LineageTest
 *
 * @author wenmo
 * @since 2022/3/15 23:08
 */
public class LineageTest {

    @Test
    public void sumTest() {
        String sql = "CREATE TABLE ST (\n" +
                "    a STRING,\n" +
                "    b STRING,\n" +
                "    c STRING\n" +
                ") WITH (\n" +
                "  'connector' = 'datagen',\n" +
                "  'rows-per-second' = '1'\n" +
                ");\n" +
                "CREATE TABLE TT (\n" +
                "  A STRING,\n" +
                "  B STRING\n" +
                ") WITH (\n" +
                " 'connector' = 'print'\n" +
                ");\n" +
                "insert into TT select a||c A ,b B from ST";
        LineageResult result = LineageBuilder.getLineage(sql);
        System.out.println("end");
    }
}
