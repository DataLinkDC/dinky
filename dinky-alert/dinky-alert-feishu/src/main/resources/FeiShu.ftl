{
  "msg_type": "interactive",
  <#if sign??>
    "sign":"${sign}",
    "timestamp":"${timestamp}",
  </#if>
  "card": {
    "elements": [
      {
        "tag": "div",
        "text": {
          "content": "${keyword?json_string}${content?json_string}<#list atUsers as key><at id=${key}></at></#list>",
          "tag": "lark_md"
        }
      }
    ],
    "header": {
      "title": {
        "content": "${title}",
        "tag": "plain_text"
      },
      "template":"red"
    }
  }
}