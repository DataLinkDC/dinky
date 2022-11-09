package com.dlink.function.util;

import org.apache.flink.runtime.util.EnvironmentInformation;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public class FlinkUtils {
    public static String getFlinkVersion() {
        return EnvironmentInformation.getVersion();
    }

    /**
     * @param version flink version。如：1.14.6
     * @return flink 大版本，如 14
     */
    public static String getFlinkBigVersion(String version) {
        return StrUtil.split(version, ".").get(1);
    }

    /**
     *
     * @return 获取当前 flink 大版本
     */
    public static Integer getCurFlinkBigVersion() {
        return Convert.toInt(getFlinkBigVersion(getFlinkVersion()));
    }
}
