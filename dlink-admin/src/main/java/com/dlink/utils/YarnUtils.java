package com.dlink.utils;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.methods.HttpPost;

import java.util.Map;

/**
 * @ClassName YarnUtils
 * @Author ltz
 * @Date 2023/2/13 14:49
 * @Verion 1.0
 **/
public class YarnUtils {
  public static ObjectNode getApplicationInstants(String resourceManagerAddr, String applicationId) {
    String jsonStr = "";
    try {
      String url = String.format("http://%s/ws/v1/cluster/apps/%s",
          resourceManagerAddr, applicationId);
      jsonStr = HttpUtil.get(url);
      ObjectNode jsonNodes = JSONUtil.parseObject(jsonStr);
      if (jsonNodes.get("RemoteException") != null)
        throw new RuntimeException(jsonNodes.get("RemoteException").get("message").asText());
      return jsonNodes;
    } catch (Exception e) {
      if (e instanceof JsonParseException){
        throw new RuntimeException("obtain applicationId failed,Please check url is corrected:" + jsonStr,e);
      }
      throw new RuntimeException("obtain applicationId failed,Please check url is corrected:" + e.getMessage(),e);
    }
  }

  public static String getApplicationStatus(ObjectNode objectNode) {

    return objectNode.get("app").get("state").asText();
  }


  public static String getApplicationAddress(ObjectNode objectNode) {
    return objectNode.get("app").get("amRPCAddress").asText();
  }
}
