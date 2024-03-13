{
    "msgtype": "markdown",
    "markdown": {
            "title": "${keyword?json_string}${title}<#list atMobiles as key>  @${key} </#list>",
            "text": "### Dinky Alert:${title}\n> Your task there is an abnormality, Please troubleshoot\n\n${content?json_string}\n ---\n[Dinky Team](https://github.com/DataLinkDC/dinky)"
        },
    "at": {
        <#if atMobiles??>
            "atMobiles": [
                <#list atMobiles as key>
                    "${key}"<#if key_has_next>,</#if>
                </#list>
            ],
        </#if>
         "isAtAll": ${isAtAll?c},
    }
}