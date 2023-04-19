package org.dinky.zdpx.coder.configure;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 *
 */
//@Configuration
public class ProxyServletConfiguration {
        /**
     * 读取配置文件中路由设置
     */
    @Value("${proxy.grafana.servlet_url}")
    private String servlet_url;

    /**
     * 读取配置中代理目标地址
     */
    @Value("${proxy.grafana.target_url}")
    private String target_url;

    @Bean
    public ServletRegistrationBean proxyServletRegistration() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new GrafanaProxyServlet(), servlet_url);
        //设置网址以及参数
        Map<String, String> params = ImmutableMap.of("targetUri", target_url, "log", "true");
        registrationBean.setInitParameters(params);
        return registrationBean;
    }
}
