package com.dlink.constant;

import java.io.File;

/**
 * @author ZackYoung
 * @version 1.0
 * @since 2022/10/4
 */
public class PathConstant {
    public static final String WORK_DIR = System.getProperty("user.dir");
    public static final String TMP_PATH = WORK_DIR + File.separator + "tmp";
    public static final String UDF_PATH = TMP_PATH + File.separator + "udf";
    public static final String UDF_JAR_NAME = "udf.jar";
    public static final String UDF_JAR_PATH = UDF_PATH + File.separator + UDF_JAR_NAME;

}
