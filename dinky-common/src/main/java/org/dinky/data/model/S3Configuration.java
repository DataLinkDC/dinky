package org.dinky.data.model;

import java.util.Properties;
import lombok.Data;

/**
 * 配置类 S3Configuration 用于存储 S3 配置信息

 */
public class S3Configuration {
    public static String ACCESS_KEY = "s3.access-key";
    public static String SECRET_KEY = "s3.secret-key";
    public static String ENDPOINT = "s3.endpoint";
    public static String BUCKET_NAME = "s3.bucket-name";
    public static String PATH_STYLE_ACCESS = "s3.path.style.access";
    public static String REGION = "s3.region";
}
