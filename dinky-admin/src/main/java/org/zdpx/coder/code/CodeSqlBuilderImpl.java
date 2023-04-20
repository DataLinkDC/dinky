package org.zdpx.coder.code;

/**
 *
 */
public class CodeSqlBuilderImpl implements CodeBuilder {
    private final StringBuilder result = new StringBuilder();
    private static final String SEMICOLON = ";";

    @Override
    public void registerUdfFunction(String udfFunctionName, String functionClass) {
        String sql = String.format("CREATE TEMPORARY FUNCTION %s AS '%s'", udfFunctionName, functionClass);
        result.append(sql);
        result.append(SEMICOLON);
    }

    @Override
    public void firstBuild() {

    }

    @Override
    public void generate(String sql) {
        result.append("\r\n");
        result.append(sql);
        result.append(SEMICOLON);
        result.append(System.lineSeparator());
    }

    @Override
    public String lastBuild() {
        return result.toString();
    }
}
