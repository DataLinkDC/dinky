package com.dlink.alert.feishu;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public final class HttpRequestUtil {
    private HttpRequestUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static CloseableHttpClient getHttpClient(boolean enableProxy, String proxy, Integer port, String user, String password) {
        if (enableProxy) {
            HttpHost httpProxy = new HttpHost(proxy, port);
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(new AuthScope(httpProxy), new UsernamePasswordCredentials(user, password));
            return HttpClients.custom().setDefaultCredentialsProvider(provider).build();
        } else {
            return HttpClients.createDefault();
        }
    }

    public static HttpPost constructHttpPost(String url, String msg) {
        HttpPost post = new HttpPost(url);
        StringEntity entity = new StringEntity(msg, ContentType.APPLICATION_JSON);
        post.setEntity(entity);
        return post;
    }
}
