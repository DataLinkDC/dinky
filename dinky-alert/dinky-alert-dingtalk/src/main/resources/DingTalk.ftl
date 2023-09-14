{
    "msgtype": "markdown",
    "markdown": {
            "title": "${keyword?json_string}${title}<#list atMobile as key>  @${key} </#list>",
            "text": "### Dinky Alert:${title}\n> Your task there is an abnormality, Please troubleshoot\n\n${content?json_string}\n ---\n[Dinky Team](https://github.com/DataLinkDC/dinky)"
        },
    "at": {
        <#if atMobiles??>
            "atMobiles": [${atMobiles}],
        </#if>
         "isAtAll": ${atAll},
    }
}