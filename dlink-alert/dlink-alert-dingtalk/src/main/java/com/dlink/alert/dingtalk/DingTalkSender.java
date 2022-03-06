package com.dlink.alert.dingtalk;

import com.dlink.alert.AlertResult;
import com.dlink.alert.AlertSendResponse;
import com.dlink.assertion.Asserts;
import com.dlink.utils.JSONUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * DingTalkSender
 *
 * @author wenmo
 * @since 2022/2/23 19:34
 **/
public class DingTalkSender {

    private static final Logger logger = LoggerFactory.getLogger(DingTalkSender.class);
    private final String url;
    private final String keyword;
    private final String secret;
    private String msgType;
    private final String atMobiles;
    private final String atUserIds;
    private final Boolean atAll;
    private final Boolean enableProxy;
    private String proxy;
    private Integer port;
    private String user;
    private String password;

    DingTalkSender(Map<String, String> config) {
        url = config.get(DingTalkConstants.WEB_HOOK);
        keyword = config.get(DingTalkConstants.KEYWORD);
        secret = config.get(DingTalkConstants.SECRET);
        msgType = config.get(DingTalkConstants.MSG_TYPE);
        atMobiles = config.get(DingTalkConstants.AT_MOBILES);
        atUserIds = config.get(DingTalkConstants.AT_USERIDS);
        atAll = Boolean.valueOf(config.get(DingTalkConstants.AT_ALL));
        enableProxy = Boolean.valueOf(config.get(DingTalkConstants.PROXY_ENABLE));
        if (Boolean.TRUE.equals(enableProxy)) {
            port = Integer.parseInt(config.get(DingTalkConstants.PORT));
            proxy = config.get(DingTalkConstants.PROXY);
            user = config.get(DingTalkConstants.USER);
            password = config.get(DingTalkConstants.PASSWORD);
        }
    }

    public AlertResult send(String title, String content) {
        AlertResult alertResult;
        try {
            String resp = sendMsg(title, content);
            return checkMsgResult(resp);
        } catch (Exception e) {
            logger.info("send ding talk alert msg  exception : {}", e.getMessage());
            alertResult = new AlertResult();
            alertResult.setSuccess(false);
            alertResult.setMessage("send ding talk alert fail.");
        }
        return alertResult;
    }

    private String sendMsg(String title, String content) throws IOException {
        String msg = generateMsgJson(title, content);
        String httpUrl = url;
        if(Asserts.isNotNullString(secret)){
            httpUrl = generateSignedUrl();
        }
        HttpPost httpPost = new HttpPost(httpUrl);
        StringEntity stringEntity = new StringEntity(msg, StandardCharsets.UTF_8);
        httpPost.setEntity(stringEntity);
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        CloseableHttpClient httpClient;
        if (Boolean.TRUE.equals(enableProxy)) {
            HttpHost httpProxy = new HttpHost(proxy, port);
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(new AuthScope(httpProxy), new UsernamePasswordCredentials(user, password));
            httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build();
            RequestConfig rcf = RequestConfig.custom().setProxy(httpProxy).build();
            httpPost.setConfig(rcf);
        } else {
            httpClient = HttpClients.createDefault();
        }
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String resp;
            try {
                HttpEntity httpEntity = response.getEntity();
                resp = EntityUtils.toString(httpEntity, "UTF-8");
                EntityUtils.consume(httpEntity);
            } finally {
                response.close();
            }
            return resp;
        } finally {
            httpClient.close();
        }
    }

    private String generateMsgJson(String title, String content) {
        if (Asserts.isNullString(msgType)) {
            msgType = DingTalkConstants.MSG_TYPE_TEXT;
        }
        Map<String, Object> items = new HashMap<>();
        items.put("msgtype", msgType);
        Map<String, Object> text = new HashMap<>();
        items.put(msgType, text);
        if (DingTalkConstants.MSG_TYPE_MARKDOWN.equals(msgType)) {
            generateMarkdownMsg(title, content, text);
        } else {
            generateTextMsg(title, content, text);
        }
        setMsgAt(items);
        return JSONUtil.toJsonString(items);
    }

    private void generateTextMsg(String title, String content, Map<String, Object> text) {
        StringBuilder builder = new StringBuilder(title);
        builder.append("\n");
        builder.append(content);
        if (Asserts.isNotNullString(keyword)) {
            builder.append(" ");
            builder.append(keyword);
        }
        text.put("content", builder.toString());
    }

    private void generateMarkdownMsg(String title, String content, Map<String, Object> text) {
        StringBuilder builder = new StringBuilder(content);
        if (Asserts.isNotNullString(keyword)) {
            builder.append(" ");
            builder.append(keyword);
        }
        builder.append("\n\n");
        if (Asserts.isNotNullString(atMobiles)) {
            Arrays.stream(atMobiles.split(",")).forEach(value -> {
                builder.append("@");
                builder.append(value);
                builder.append(" ");
            });
        }
        if (Asserts.isNotNullString(atUserIds)) {
            Arrays.stream(atUserIds.split(",")).forEach(value -> {
                builder.append("@");
                builder.append(value);
                builder.append(" ");
            });
        }
        text.put("title", title);
        text.put("text", builder.toString());
    }

    private String generateSignedUrl() {
        Long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        String sign = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
        } catch (Exception e) {
            logger.error("generate sign error, message:{}", e);
        }
        return url + "&timestamp=" + timestamp + "&sign=" + sign;
    }

    private void setMsgAt(Map<String, Object> items) {
        Map<String, Object> at = new HashMap<>();
        String[] atMobileArray = Asserts.isNotNullString(atMobiles) ? atMobiles.split(",") : new String[0];
        String[] atUserArray = Asserts.isNotNullString(atUserIds) ? atUserIds.split(",") : new String[0];
        boolean isAtAll = Objects.isNull(atAll) ? false : atAll;
        at.put("atMobiles", atMobileArray);
        at.put("atUserIds", atUserArray);
        at.put("isAtAll", isAtAll);
        items.put("at", at);
    }

    private AlertResult checkMsgResult(String result) {
        AlertResult alertResult = new AlertResult();
        alertResult.setSuccess(false);

        if (null == result) {
            alertResult.setMessage("send ding talk msg error");
            logger.info("send ding talk msg error,ding talk server resp is null");
            return alertResult;
        }
        AlertSendResponse response = JSONUtil.parseObject(result, AlertSendResponse.class);
        if (null == response) {
            alertResult.setMessage("send ding talk msg fail");
            logger.info("send ding talk msg error,resp error");
            return alertResult;
        }
        if (response.getErrcode() == 0) {
            alertResult.setSuccess(true);
            alertResult.setMessage("send ding talk msg success");
            return alertResult;
        }
        alertResult.setMessage(String.format("alert send ding talk msg error : %s", response.getErrmsg()));
        logger.info("alert send ding talk msg error : {}", response.getErrmsg());
        return alertResult;
    }
}
