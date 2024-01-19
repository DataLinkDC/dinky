{
<#if atUsers??>
    "touser" : "<#list atUsers as key>${key}|</#list>",
</#if>
<#if atParty??>
    "toparty" : "<#list atParty as key>${key}|</#list>",
</#if>
<#if atTotag??>
    "totag" : "<#list atTotag as key>${key}|</#list>",
</#if>

"msgtype": "markdown",
"agentid": ${agentId},
"markdown": {
"content": "### Dinky Alert:${title}\n${content?json_string}"
},
"enable_duplicate_check": 0,
"duplicate_check_interval": 1800
}