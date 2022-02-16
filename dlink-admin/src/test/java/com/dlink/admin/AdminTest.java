package com.dlink.admin;

import cn.dev33.satoken.secure.SaSecureUtil;
import org.junit.Test;

/**
 * SqlParserTest
 *
 * @author wenmo
 * @since 2021/6/14 17:03
 */
public class AdminTest {

    @Test
    public void adminTest(){
        String admin = SaSecureUtil.md5("admin");
        System.out.println(admin);
    }

}
