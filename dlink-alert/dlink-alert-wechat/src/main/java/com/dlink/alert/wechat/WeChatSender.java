package com.dlink.alert.wechat;

import com.dlink.alert.AlertException;
import com.dlink.alert.AlertResult;
import com.dlink.alert.AlertSendResponse;
import com.dlink.alert.ShowType;
import com.dlink.assertion.Asserts;
import com.dlink.utils.JSONUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * WeChatSender
 *
 * @author wenmo
 * @since 2022/2/23 21:11
 **/
public class WeChatSender {
    private static final Logger logger = LoggerFactory.getLogger(WeChatSender.class);
    private static final String ALERT_STATUS = "false";
    private static final String AGENT_ID_REG_EXP = "{agentId}";
    private static final String MSG_REG_EXP = "{msg}";
    private static final String USER_REG_EXP = "{toUser}";
    private static final String CORP_ID_REGEX = "{corpId}";
    private static final String SECRET_REGEX = "{secret}";
    private static final String TOKEN_REGEX = "{token}";
    private static final String SHOW_TYPE_REGEX = "{showType}";
    private final String weChatAgentId;
    private final String weChatUsers;
    private final String weChatUserSendMsg;
    private final String weChatTokenUrlReplace;
    private final String weChatToken;
    private final String sendType;
    private final String showType;

    WeChatSender(Map<String, String> config) {
        weChatAgentId = config.get(WeChatConstants.AGENT_ID);
        weChatUsers = config.get(WeChatConstants.USERS);
        String weChatCorpId = config.get(WeChatConstants.CORP_ID);
        String weChatSecret = config.get(WeChatConstants.SECRET);
        String weChatTokenUrl = WeChatConstants.TOKEN_URL;
        weChatUserSendMsg = WeChatConstants.USER_SEND_MSG;
        sendType = config.get(WeChatConstants.SEND_TYPE);
        showType = config.get(WeChatConstants.SHOW_TYPE);
        requireNonNull(showType, WeChatConstants.SHOW_TYPE + " must not null");
        weChatTokenUrlReplace = weChatTokenUrl
                .replace(CORP_ID_REGEX, weChatCorpId)
                .replace(SECRET_REGEX, weChatSecret);
        weChatToken = getToken();
    }


    public AlertResult send(String title, String content) {
        AlertResult alertResult = new AlertResult();
        List<String> userList = new ArrayList<>();
        if (Asserts.isNotNullString(weChatUsers)) {
            userList = Arrays.asList(weChatUsers.split(","));
        }
        String data = markdownByAlert(title, content);
        String msg = weChatUserSendMsg.replace(USER_REG_EXP, mkString(userList))
                .replace(AGENT_ID_REG_EXP, weChatAgentId).replace(MSG_REG_EXP, data)
                .replace(SHOW_TYPE_REGEX,showType);
        if (Asserts.isNullString(weChatToken)) {
            alertResult.setMessage("send we chat alert fail,get weChat token error");
            alertResult.setSuccess(false);
            return alertResult;
        }
        String enterpriseWeChatPushUrlReplace = "";
        if (sendType.equals(WeChatType.APP.getValue())) {
            enterpriseWeChatPushUrlReplace = WeChatConstants.PUSH_URL.replace(TOKEN_REGEX, weChatToken);
        } else if (sendType.equals(WeChatType.APPCHAT.getValue())) {
            enterpriseWeChatPushUrlReplace = WeChatConstants.APP_CHAT_PUSH_URL.replace(TOKEN_REGEX, weChatToken);
        }
        try {
            return checkWeChatSendMsgResult(post(enterpriseWeChatPushUrlReplace, msg));
        } catch (Exception e) {
            logger.info("send we chat alert msg  exception : {}", e.getMessage());
            alertResult.setMessage("send we chat alert fail");
            alertResult.setSuccess(false);
        }
        return alertResult;
    }

    private static String post(String url, String data) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(data, WeChatConstants.CHARSET));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String resp;
            try {
                HttpEntity entity = response.getEntity();
                resp = EntityUtils.toString(entity, WeChatConstants.CHARSET);
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
//            logger.info("Enterprise WeChat send [{}], param:{}, resp:{}", url, data, resp);
            return resp;
        }
    }

    private static String mkString(Iterable<String> list) {
        if (Asserts.isNull(list)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first) {
                first = false;
            } else {
                sb.append("|");
            }
            sb.append(item);
        }
        return sb.toString();
    }

    private String markdownByAlert(String title, String content) {
        String result = "";
        if (showType.equals(ShowType.TABLE.getValue())) {
            result = markdownTable(title, content);
        } else if (showType.equals(ShowType.TEXT.getValue())) {
            result = markdownText(title, content);
        }
        return result;
    }

    private static String markdownTable(String title, String content) {
        List<LinkedHashMap> mapItemsList = JSONUtil.toList(content, LinkedHashMap.class);
        if (null == mapItemsList || mapItemsList.isEmpty()) {
            logger.error("itemsList is null");
            throw new RuntimeException("itemsList is null");
        }
        StringBuilder contents = new StringBuilder(200);
        for (LinkedHashMap mapItems : mapItemsList) {
            Set<Map.Entry<String, Object>> entries = mapItems.entrySet();
            Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
            StringBuilder t = new StringBuilder(String.format("`%s`%s", title, WeChatConstants.MARKDOWN_ENTER));

            while (iterator.hasNext()) {

                Map.Entry<String, Object> entry = iterator.next();
                t.append(WeChatConstants.MARKDOWN_QUOTE);
                t.append(entry.getKey()).append("：").append(entry.getValue());
                t.append(WeChatConstants.MARKDOWN_ENTER);
            }
            contents.append(t);
        }

        return contents.toString();
    }

    private static String markdownText(String title, String content) {
        if (Asserts.isNotNullString(content)) {
            List<LinkedHashMap> mapItemsList = JSONUtil.toList(content, LinkedHashMap.class);
            if (null == mapItemsList || mapItemsList.isEmpty()) {
                logger.error("itemsList is null");
                throw new AlertException("itemsList is null");
            }

            StringBuilder contents = new StringBuilder(100);
            contents.append(String.format("`%s`%n", title));
            for (LinkedHashMap mapItems : mapItemsList) {

                Set<Map.Entry<String, Object>> entries = mapItems.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    contents.append(WeChatConstants.MARKDOWN_QUOTE);
                    contents.append(entry.getKey()).append("：").append(entry.getValue());
                    contents.append(WeChatConstants.MARKDOWN_ENTER);
                }

            }
            return contents.toString();
        }
        return null;
    }


    private String getToken() {
        try {
            return get(weChatTokenUrlReplace);
        } catch (IOException e) {
            logger.info("we chat alert get token error{}", e.getMessage());
        }
        return null;
    }

    private static String get(String url) throws IOException {
        String resp;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                resp = EntityUtils.toString(entity, WeChatConstants.CHARSET);
                EntityUtils.consume(entity);
            }
            HashMap<String, Object> map = JSONUtil.parseObject(resp, HashMap.class);
            if (map != null && null != map.get("access_token")) {
                return map.get("access_token").toString();
            } else {
                return null;
            }
        }
    }

    private static AlertResult checkWeChatSendMsgResult(String result) {
        AlertResult alertResult = new AlertResult();
        alertResult.setSuccess(false);
        if (null == result) {
            alertResult.setMessage("we chat send fail");
            logger.info("send we chat msg error,resp is null");
            return alertResult;
        }
        AlertSendResponse sendMsgResponse = JSONUtil.parseObject(result, AlertSendResponse.class);
        if (null == sendMsgResponse) {
            alertResult.setMessage("we chat send fail");
            logger.info("send we chat msg error,resp error");
            return alertResult;
        }
        if (sendMsgResponse.getErrcode() == 0) {
            alertResult.setSuccess(true);
            alertResult.setMessage("we chat alert send success");
            return alertResult;
        }
        alertResult.setSuccess(false);
        alertResult.setMessage(sendMsgResponse.getErrmsg());
        return alertResult;
    }
}
