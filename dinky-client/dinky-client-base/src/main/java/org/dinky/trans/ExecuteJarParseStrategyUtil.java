package org.dinky.trans;

import cn.hutool.core.lang.Singleton;
import org.dinky.trans.parse.ExecuteJarParseStrategy;

public class ExecuteJarParseStrategyUtil {

    public static boolean match(String statement){
        return Singleton.get(ExecuteJarParseStrategy.class).match(statement);
    }
}
