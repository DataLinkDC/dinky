package org.dinky.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.mitre.dsmiley.httpproxy.URITemplateProxyServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlinkWebURITemplateProxyServlet extends ProxyServlet {

    public static final String FLINK_WEB_PROXY = "/api/flink_web/proxy";
    private static String AUTHORITY;

    protected static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{(.+?)\\}");
    private static final String ATTR_QUERY_STRING =
            URITemplateProxyServlet.class.getSimpleName() + ".queryString";

    protected String targetUriTemplate;//has {name} parts

    @Override
    protected void initTarget() throws ServletException {
        targetUriTemplate = getConfigParam(P_TARGET_URI);
        if (targetUriTemplate == null)
            throw new ServletException(P_TARGET_URI+" is required.");

        //leave this.target* null to prevent accidental mis-use
    }

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
            throws ServletException, IOException {

        if (!servletRequest.getRequestURI().contains(FLINK_WEB_PROXY)) {
            // iframe inner request
            if(AUTHORITY != null) {
                service(servletRequest, servletResponse, AUTHORITY);
            }
            return;
        }

        service(servletRequest, servletResponse, targetUriTemplate);

        if (Objects.equals(servletRequest.getRequestURI(), FLINK_WEB_PROXY)) {
            AUTHORITY = servletRequest.getAttribute(ATTR_TARGET_HOST).toString();
        }
    }

    private void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse, String targetUriTemplateCurrent)
            throws ServletException, IOException  {
        String requestQueryString = servletRequest.getQueryString();
        String queryString = "";
        if (requestQueryString != null) {
            queryString = "?" + requestQueryString;//no "?" but might have "#"
        }
        int hash = queryString.indexOf('#');
        if (hash >= 0) {
            queryString = queryString.substring(0, hash);
        }
        List<NameValuePair> pairs;
        try {
            //note: HttpClient 4.2 lets you parse the string without building the URI
            pairs = URLEncodedUtils.parse(new URI(queryString), "UTF-8");
        } catch (URISyntaxException e) {
            throw new ServletException("Unexpected URI parsing error on " + queryString, e);
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        for (NameValuePair pair : pairs) {
            params.put(pair.getName(), pair.getValue());
        }

        //Now rewrite the URL
        StringBuffer urlBuf = new StringBuffer();//note: StringBuilder isn't supported by Matcher
        Matcher matcher = TEMPLATE_PATTERN.matcher(targetUriTemplateCurrent);
        while (matcher.find()) {
            String arg = matcher.group(1);
            String replacement = params.remove(arg);//note we remove
            if (replacement == null) {
                throw new ServletException("Missing HTTP parameter "+arg+" to fill the template");
            }
            matcher.appendReplacement(urlBuf, replacement);
        }
        matcher.appendTail(urlBuf);
        String newTargetUri = urlBuf.toString();
        servletRequest.setAttribute(ATTR_TARGET_URI, newTargetUri);
        URI targetUriObj;
        try {
            targetUriObj = new URI(newTargetUri);
        } catch (Exception e) {
            throw new ServletException("Rewritten targetUri is invalid: " + newTargetUri,e);
        }
        servletRequest.setAttribute(ATTR_TARGET_HOST, URIUtils.extractHost(targetUriObj));

        //Determine the new query string based on removing the used names
        StringBuilder newQueryBuf = new StringBuilder(queryString.length());
        for (Map.Entry<String, String> nameVal : params.entrySet()) {
            if (newQueryBuf.length() > 0)
                newQueryBuf.append('&');
            newQueryBuf.append(nameVal.getKey()).append('=');
            if (nameVal.getValue() != null)
                newQueryBuf.append( URLEncoder.encode(nameVal.getValue(), "UTF-8"));
        }
        servletRequest.setAttribute(ATTR_QUERY_STRING, newQueryBuf.toString());

        super.service(servletRequest, servletResponse);
    }

    @Override
    protected String rewriteQueryStringFromRequest(HttpServletRequest servletRequest, String queryString) {
        return (String) servletRequest.getAttribute(ATTR_QUERY_STRING);
    }

}
