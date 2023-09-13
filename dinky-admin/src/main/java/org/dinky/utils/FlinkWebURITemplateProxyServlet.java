package org.dinky.utils;

import org.mitre.dsmiley.httpproxy.URITemplateProxyServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class FlinkWebURITemplateProxyServlet extends URITemplateProxyServlet {

    public static final String FLINK_WEB_PROXY = "/flink_web/proxy";
    public static final String FLINK_WEB_PROXY_ORIGIN = "/flink_web";
    private static String AUTHORITY;


    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
            throws ServletException, IOException {

        if (!servletRequest.getRequestURI().contains(FLINK_WEB_PROXY)) {

            // iframe inner request
            if(AUTHORITY != null) {
                String preTargetUriTemplate = targetUriTemplate;
                try {
                    String origin_url = servletRequest.getRequestURL().toString();
//                    String last = origin_url.substring(origin_url.indexOf(FLINK_WEB_PROXY_ORIGIN) + FLINK_WEB_PROXY_ORIGIN.length());
                    targetUriTemplate = AUTHORITY;
                    servletRequest.setAttribute(ATTR_TARGET_URI, targetUriTemplate);
                    super.service(servletRequest, servletResponse);
                }finally {
                    targetUriTemplate = preTargetUriTemplate;
                }
            }
            return;
        }

        super.service(servletRequest, servletResponse);

        if (Objects.equals(servletRequest.getRequestURI(), FLINK_WEB_PROXY)) {
            AUTHORITY = servletRequest.getAttribute(ATTR_TARGET_HOST).toString();
        }
    }

}
