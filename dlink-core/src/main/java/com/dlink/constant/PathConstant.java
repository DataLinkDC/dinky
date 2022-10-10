package com.dlink.constant;

import java.io.File;

/**
 * @author ZackYoung
 * @version 1.0
 * @since 2022/10/4
 */
public class PathConstant {
    public static final String WORK_DIR = System.getProperty("user.dir");
    public static final String TMP_PATH = WORK_DIR + File.separator + "tmp" + File.separator;
    public static final String UDF_PATH = TMP_PATH + "udf" + File.separator;
    public static final String UDF_JAR_RULE = "udf-\\d+.jar";
    public static final String UDF_VERSION_RULE = "\\d+";
    public static final String UDF_JAR_TMP_NAME = "udf-tmp.jar";
    public static final String UDF_JAR_TMP_PATH = UDF_PATH + UDF_JAR_TMP_NAME;

}
