package com.dlink.core;

import com.dlink.executor.Executor;
import com.dlink.interceptor.FlinkInterceptor;
import org.junit.Assert;
import org.junit.Test;

/**
 * FlinkInterceptorTest
 *
 * @author wenmo
 * @since 2022/4/9 17:48
 **/
public class FlinkInterceptorTest {

    @Test
    public void replaceFragmentTest(){
        String statement = "nullif1:=NULLIF(1, 0) as val;" +
            "nullif2:=NULLIF(0, 0) as val$null;" +
            "select ${nullif1},${nullif2}";
        String pretreatStatement = FlinkInterceptor.pretreatStatement(Executor.build(), statement);
        Assert.assertEquals("select NULLIF(1, 0) as val,NULLIF(0, 0) as val$null",pretreatStatement);
    }
}
