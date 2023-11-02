package org.dinky.utils;

import cn.hutool.core.util.StrUtil;
import org.dinky.function.constant.PathConstant;

import java.io.File;

/**
 * Resources Utils
 */
public class RSUtils {
    public static String getFilePath(String path) {
        return StrUtil.join(File.separator, PathConstant.TMP_PATH, "rs", path);
    }
}
