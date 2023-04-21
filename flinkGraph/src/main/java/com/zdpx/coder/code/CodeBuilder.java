package com.zdpx.coder.code;

/**
 *
 */
public interface CodeBuilder {

    void registerUdfFunction(String udfFunctionName, String functionClass);

    void firstBuild();

    void generate(String sql);

    String lastBuild();
}
