package com.dlink.init;

import com.dlink.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * SystemInit
 *
 * @author wenmo
 * @since 2021/11/18
 **/
@Component
@Order(value = 1)
public class SystemInit implements ApplicationRunner {


    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        sysConfigService.initSysConfig();
    }
}
