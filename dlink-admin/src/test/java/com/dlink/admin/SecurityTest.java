package com.dlink.admin;


import com.dlink.service.security.SecurityAspect;
import org.junit.Assert;
import org.junit.Test;

public class SecurityTest {


    @Test
    public void testMask() {
        String sql = "stock_trade:='connector' = 'jdbc'," +
                "useUnicode=yes&characterEncoding=UTF-8&useSSL=false',\\n   'username' = 'trade'," +
                "\\n 'password' = 'c6634672b535f968b'\\n;\\ntidb_test:='connector' = 'jdbc'," +
                "\\n'url' = 'jdbc:mysql://localhost:4000/test?useUnicode=yes&characterEncoding=UTF-8&useSSL=false'," +
                "\\n   'username' = 'root',\\n 'password' = 'wwz@test'\\n;";

        String out = SecurityAspect.mask(sql);

        // System.out.println(out);

        Assert.assertEquals(out.contains(SecurityAspect.MASK), true);
    }
}
