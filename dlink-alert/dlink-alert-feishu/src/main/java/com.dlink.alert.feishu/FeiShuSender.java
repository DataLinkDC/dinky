
package com.dlink.alert.feishu;

import com.dlink.alert.AlertMsg;
import com.dlink.alert.AlertResult;
import com.dlink.alert.ShowType;
import com.dlink.utils.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Author: zhumingye
 * @date: 2022/4/2
 * @Description: 飞书消息发送器
 */
public final class FeiShuSender {
    private static final Logger logger = LoggerFactory.getLogger(FeiShuSender.class);

    static final String FEI_SHU_PROXY_ENABLE_REGX = "{isEnableProxy}";
    static final String FEI_SHU_PROXY_REGX = "{proxy}";
    static final String FEI_SHU_PORT_REGX = "{port}";
    static final String FEI_SHU_USER_REGX = "{users}";
    static final String FEI_SHU_PASSWORD_REGX = "{password}";
    static final String MSG_RESULT_REGX = "{msg}";
    static final String MSG_TYPE_REGX = "{msg_type}";
    static final String FEI_SHU_MSG_TYPE_REGX = "{keyword}";

    private final String url;
    private final String msgType;
    private final Boolean enableProxy;
    private final String secret;
    private final String keyword;
    private String proxy;
    private Integer port;
    private String user;
    private String password;
    private final Boolean atAll;
    private  String atUserIds;

    FeiShuSender(Map<String, String> config) {
        url = config.get(FeiShuConstants.WEB_HOOK);
        msgType = config.get(FeiShuConstants.MSG_TYPE);
        keyword= config.get(FeiShuConstants.KEY_WORD).replace("\r\n", "");
        enableProxy = Boolean.valueOf(config.get(FeiShuConstants.FEI_SHU_PROXY_ENABLE));
        secret = config.get(FeiShuConstants.SECRET);
        if (Boolean.TRUE.equals(enableProxy)) {
            proxy = config.get(FeiShuConstants.FEI_SHU_PROXY);
            port = Integer.parseInt(config.get(FeiShuConstants.FEI_SHU_PORT));
            user = config.get(FeiShuConstants.FEI_SHU_USER);
            password = config.get(FeiShuConstants.FEI_SHU_PASSWORD);
        }
        atAll = Boolean.valueOf(config.get(FeiShuConstants.AT_ALL));
        if (Boolean.FALSE.equals(atAll)) {
            atUserIds = config.get(FeiShuConstants.AT_USERS);
        }
    }

    private  String toJsonSendMsg(AlertMsg alertMsg) {
        String jsonResult ="";
        byte[] byt = StringUtils.getBytesUtf8(formatContent(alertMsg));
        String contentResult = StringUtils.newStringUtf8(byt);
        String userIdsToText = mkUserIdsToText(atUserIds);
        if (StringUtils.equals(ShowType.TEXT.getValue(), msgType)) {
            jsonResult = FeiShuConstants.FEI_SHU_TEXT_TEMPLATE.replace(MSG_TYPE_REGX, msgType)
                    .replace(MSG_RESULT_REGX, contentResult).replace(FEI_SHU_USER_REGX, userIdsToText).replaceAll("/n", "\\\\n");
        }else {
            jsonResult = FeiShuConstants.FEI_SHU_POST_TEMPLATE.replace(MSG_TYPE_REGX, msgType)
                    .replace(FEI_SHU_MSG_TYPE_REGX, keyword).replace(MSG_RESULT_REGX, contentResult)
                    .replace(FEI_SHU_USER_REGX, userIdsToText).replaceAll("/n", "\\\\n");
        }
        return jsonResult;
    }

    private  String mkUserIdsToText(String users){
        String userIdsToText="";
        String[] userList = users.split(",");
            if (org.apache.commons.lang3.StringUtils.isEmpty(users) && Boolean.TRUE.equals(atAll)) {
                if (msgType.equals(ShowType.TEXT.getValue())) {
                    userIdsToText="<at user_id=\\\"all\\\">所有人</at>";
                }else{
                    userIdsToText="{\"tag\":\"at\",\"user_id\":\"all\",\"user_name\":\"所有人\"}";
                }
            } else {
                if (msgType.equals(ShowType.TEXT.getValue())) {
                    StringBuilder sb = new StringBuilder();
                    for (String user : userList) {
                        sb.append("<at user_id=\\\"").append(user).append("\\\"></at>");
                    }
                    userIdsToText = sb.toString();
                }else{
                    StringBuilder sb = new StringBuilder();
                    for (String user : userList) {
                        sb.append("{\"tag\":\"at\",\"user_id\":\"").append(user).append("\"},");
                    }
                    sb.deleteCharAt(sb.length()-1);
                    userIdsToText = sb.toString();
                }

            }
            return userIdsToText;
    }

    public static AlertResult checkSendFeiShuSendMsgResult(String result) {
        AlertResult alertResult = new AlertResult();
        alertResult.setSuccess(false);

        if (org.apache.commons.lang3.StringUtils.isBlank(result)) {
            alertResult.setMessage("send fei shu msg error");
            logger.info("send fei shu msg error,fei shu server resp is null");
            return alertResult;
        }
        FeiShuSendMsgResponse sendMsgResponse = JSONUtil.parseObject(result, FeiShuSendMsgResponse.class);

        if (null == sendMsgResponse) {
            alertResult.setMessage("send fei shu msg fail");
            logger.info("send fei shu msg error,resp error");
            return alertResult;
        }
        if (sendMsgResponse.statusCode == 0) {
            alertResult.setSuccess(true);
            alertResult.setMessage("send fei shu msg success");
            return alertResult;
        }
        alertResult.setMessage(String.format("alert send fei shu msg error : %s", sendMsgResponse.getStatusMessage()));
        logger.info("alert send fei shu msg error : {} ,Extra : {} ", sendMsgResponse.getStatusMessage(), sendMsgResponse.getExtra());
        return alertResult;
    }

    public static String formatContent(AlertMsg alertMsg) {
        if (alertMsg.getContent() != null) {
            List<Map> list = JSONUtil.toList(alertMsg.getContent(), Map.class);
            if (list.isEmpty()) {
                return alertMsg.getName() + alertMsg.getContent();
            }

            StringBuilder contents = new StringBuilder(100);
            contents.append(String.format("`%s`/n ", alertMsg.getName()));
            for (Map map : list) {
                for (Entry<String, Object> entry : (Iterable<Entry<String, Object>>) map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    contents.append(FeiShuConstants.MARKDOWN_QUOTE);
                    contents.append(key + "：" + value);
                    contents.append(" /n ");
                }
            }
            return contents.toString();
        }
        return null;
    }

    public AlertResult send(String title,String content) {
        AlertResult alertResult;
        AlertMsg alertMsg = new AlertMsg();
        alertMsg.setName(title);
        alertMsg.setContent(content);
        try {
            String resp = sendMsg(alertMsg);
            return checkSendFeiShuSendMsgResult(resp);
        } catch (Exception e) {
            logger.info("send fei shu alert msg  exception : {}", e.getMessage());
            alertResult = new AlertResult();
            alertResult.setSuccess(false);
            alertResult.setMessage("send fei shu alert fail.");
        }
        return alertResult;
    }

    private String sendMsg(AlertMsg alertMsg) throws IOException {

        String msgToJson = toJsonSendMsg(alertMsg);
        HttpPost httpPost = HttpRequestUtil.constructHttpPost(url, msgToJson);
        CloseableHttpClient httpClient;
        httpClient = HttpRequestUtil.getHttpClient(enableProxy, proxy, port, user, password);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                logger.error("send feishu message error, return http status code: {} ", statusCode);
            }
            String resp;
            try {
                HttpEntity entity = response.getEntity();
                resp = EntityUtils.toString(entity, "utf-8");
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
            logger.info("Fei Shu send title :{} ,content :{}, resp: {}", alertMsg.getName(), alertMsg.getContent(), resp);
            return resp;
        } finally {
            httpClient.close();
        }
    }

    static final class FeiShuSendMsgResponse {
        @JsonProperty("Extra")
        private String extra;
        @JsonProperty("StatusCode")
        private Integer statusCode;
        @JsonProperty("StatusMessage")
        private String statusMessage;

        public FeiShuSendMsgResponse() {
        }

        public String getExtra() {
            return this.extra;
        }

        @JsonProperty("Extra")
        public void setExtra(String extra) {
            this.extra = extra;
        }

        public Integer getStatusCode() {
            return this.statusCode;
        }

        @JsonProperty("StatusCode")
        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusMessage() {
            return this.statusMessage;
        }

        @JsonProperty("StatusMessage")
        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }

        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof FeiShuSendMsgResponse)) {
                return false;
            }
            final FeiShuSendMsgResponse other = (FeiShuSendMsgResponse) o;
            final Object this$extra = this.getExtra();
            final Object other$extra = other.getExtra();
            if (this$extra == null ? other$extra != null : !this$extra.equals(other$extra)) {
                return false;
            }
            final Object this$statusCode = this.getStatusCode();
            final Object other$statusCode = other.getStatusCode();
            if (this$statusCode == null ? other$statusCode != null : !this$statusCode.equals(other$statusCode)) {
                return false;
            }
            final Object this$statusMessage = this.getStatusMessage();
            final Object other$statusMessage = other.getStatusMessage();
            if (this$statusMessage == null ? other$statusMessage != null : !this$statusMessage.equals(other$statusMessage)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $extra = this.getExtra();
            result = result * PRIME + ($extra == null ? 43 : $extra.hashCode());
            final Object $statusCode = this.getStatusCode();
            result = result * PRIME + ($statusCode == null ? 43 : $statusCode.hashCode());
            final Object $statusMessage = this.getStatusMessage();
            result = result * PRIME + ($statusMessage == null ? 43 : $statusMessage.hashCode());
            return result;
        }

        public String toString() {
            return "FeiShuSender.FeiShuSendMsgResponse(extra=" + this.getExtra() + ", statusCode=" + this.getStatusCode() + ", statusMessage=" + this.getStatusMessage() + ")";
        }
    }
}
