package com.dlink.constant;

import java.io.File;

/**
 * 文件路径常量
 * @author ZackYoung
 * @since 0.6.8
 */
public class PathConstant {

    /**
     * 基本路径，dinky 部署的路径
     */
    public static final String WORK_DIR = System.getProperty("user.dir");
    /**
     * tmp路径
     */
    public static final String TMP_PATH = WORK_DIR + File.separator + "tmp" + File.separator;
    /**
     * udf路径
     */
    public static final String UDF_PATH = TMP_PATH + "udf" + File.separator;
    /**
     * udf jar规则
     */
    public static final String UDF_JAR_RULE = "udf-\\d+.jar";
    /**
     * udf版本规则
     */
    public static final String UDF_VERSION_RULE = "\\d+";
    /**
     * udf jar tmp名字
     */
    public static final String UDF_JAR_TMP_NAME = "udf-tmp.jar";
    /**
     * udf jar tmp路径
     */
    public static final String UDF_JAR_TMP_PATH = UDF_PATH + UDF_JAR_TMP_NAME;

}
