package com.dlink.utils;

import com.dlink.constant.UploadFileConstant;

import org.apache.commons.lang3.StringUtils;

/**
 * File path handle
 **/
public class FilePathUtil {

    /**
     * Add a file separator '/' at the end of the file path
     *
     * @param filePath File path
     */
    public static String addFileSeparator(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        } else {
            if (filePath.endsWith("/")) {
                return filePath;
            } else {
                return filePath + "/";
            }
        }
    }

    /**
     * Remove a file separator '/' at the end of the file path
     *
     * @param filePath File path
     */
    public static String removeFileSeparator(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        } else {
            if (filePath.endsWith("/")) {
                return filePath.substring(0, filePath.length() - 1);
            } else {
                return filePath;
            }
        }
    }

    /**
     * Get dir type, refer {@link UploadFileConstant}
     *
     * @param dir Directory
     * @return Refer {@link UploadFileConstant}
     */
    public static byte getDirTarget(String dir) {
        if (StringUtils.isEmpty(dir)) {
            return UploadFileConstant.TARGET_OTHER;
        } else if (dir.contains("hdfs")) {
            return UploadFileConstant.TARGET_HDFS;
        } else {
            return UploadFileConstant.TARGET_LOCAL;
        }
    }

}
