---
sidebar_position: 6
position: 6
id: GetJobInstanceByTaskId
title: Get Job Instance By Task Id
---

> URL: http://localhost:8888/openapi/getJobInstanceByTaskId?id=1
>
> Origin Url: http://localhost:8888/openapi/getJobInstanceByTaskId
>
> Type: GET

### Request headers

| Header Name | Header Value |
|-------------|--------------|

### Parameters

##### Path parameters

| Parameter | Type | Value | Description |
|-----------|------|-------|-------------|

##### URL parameters

| Required | Parameter | Type   | Value | Description |
|----------|-----------|--------|-------|-------------|
| true     | id        | Number | 1     | Task Id     |

##### Body parameters

###### JSON

```json lines showLineNumbers

```

###### JSON document

```json lines showLineNumbers

```

##### Form URL-Encoded

| Required | Parameter | Type | Value | Description |
|----------|-----------|------|-------|-------------|

##### Multipart

| Required | Parameter | Type | Value | Description |
|----------|-----------|------|-------|-------------|

### Response

##### Response example

```json lines showLineNumbers

```

##### Response document

```json lines showLineNumbers
{
	"msg":"Result Message",
	"code":"Result Code",
	"data":{
		"finishTime":"Finish Time",
		"creator":"Creator",
		"jid":"JID",
		"count":"Group by count",
		"updateTime":"Update Time",
		"clusterId":"ClusterInstance ID",
		"error":"Error",
		"operator":"Operator",
		"updater":"updater",
		"duration":"Duration",
		"createTime":"Create Time",
		"historyId":"History ID",
		"tenantId":"Tenant ID",
		"name":"Name",
		"step":"Step",
		"id":"ID",
		"failedRestartCount":"Failed Restart Count",
		"taskId":"Task ID",
		"status":"Status"
	},
	"success":"Result is Success",
	"time":"Result Time"
}
```


