{
    "msgtype": "markdown",
    "markdown": {
            "title": "${keyword?json_string}${title}<#list atMobile as key>  @${key} </#list>",
            "text": "${content?json_string}"
        },
    "at": {
        "atMobiles": [${atMobiles}],
         "isAtAll": ${atAll},
    }
}