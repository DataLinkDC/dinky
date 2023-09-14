{
    "msg_type": "interactive",
    "card": {
        "elements": [
            {
                "tag": "note",
                "elements": [
                    {
                        "tag": "plain_text",
                        "content": "${keyword?json_string} Your task there is an abnormality, Please troubleshoot <#list atUsers as key><at id=${key}></at></#list>"
                    }
                ]
            },
            {
                "tag": "markdown",
                "content": "\n${content?json_string}"
            },
            {
                "tag": "hr"
            },
            {
                "tag": "markdown",
                "content": "[Dinky Team](https://github.com/DataLinkDC/dinky)"
            }
        ],
        "header": {
            "template": "orange",
            "title": {
                "content": "Dinky Alert:${title}",
                "tag": "plain_text"
            }
        }
    }
}