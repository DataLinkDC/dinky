package org.dinky.explainermock;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockStatementExplainerTest {
    public static final String PATTERN_STR = "CREATE\\s+TABLE\\s+(\\w+)\\s*\\(\\s*([\\s\\S]*?)\\s*\\)\\s*WITH\\s*\\(\\s*([\\s\\S]*?)\\s*\\)";
    public static final Pattern PATTERN = Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE);
    public static void main(String[] args) {
        String sql = "CREATE " +
                "   TABLE     skuSink (" +
                "sku_id BIGINT," +
                "sku_name STRING" +
                ")" +
                "\nWITH ( " +
                "\n'connector' = 'blackhole')";
        Matcher matcher = PATTERN.matcher(sql);

        System.out.println(matcher.find());
        System.out.println(matcher.group(1));
        System.out.println(matcher.group(2));
        System.out.println(matcher.group(3));
    }

}
