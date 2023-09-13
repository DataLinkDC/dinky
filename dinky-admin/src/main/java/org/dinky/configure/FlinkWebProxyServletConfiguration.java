package org.dinky.configure;

import org.dinky.utils.FlinkWebURITemplateProxyServlet;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FlinkWebProxyServletConfiguration implements EnvironmentAware {
    private static final String TARGET_URL = "http://{_authority}/#/job/running/{_jid}/overview";

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new FlinkWebURITemplateProxyServlet());
        servletRegistrationBean.addUrlMappings("/flink_web/proxy/*", "/flink_web/*");
         servletRegistrationBean.addInitParameter(ProxyServlet.P_TARGET_URI, TARGET_URL);
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, "true");
        return servletRegistrationBean;
    }

    @Override
    public void setEnvironment(Environment environment) {

    }
}
