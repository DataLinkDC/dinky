---
sidebar_position: 11
position: 11
id: SavepointTrigger
title: Savepoint Trigger
---

> URL: http://localhost:8888/openapi/savepoint
>
> Origin Url: http://localhost:8888/openapi/savepoint
>
> Type: POST

### Request headers

| Header Name | Header Value |
|-------------|--------------|

### Parameters

##### Path parameters

| Parameter | Type | Value | Description |
|-----------|------|-------|-------------|

##### URL parameters

| Required | Parameter | Type | Value | Description |
|----------|-----------|------|-------|-------------|

##### Body parameters

###### JSON

```json lines showLineNumbers

```

###### JSON document

```json lines showLineNumbers

```

##### Form URL-Encoded

| Required | Parameter     | Type   | Value               | Description |
|----------|---------------|--------|---------------------|-------------|
| true     | taskId        | Number | 1                   |             |
| true     | savePointType | String | savePointType_28p0g |             |

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
		"exceptionMsg":"No comment,Type =String",
		"appId":"Application ID",
		"startTime":"No comment,Type =String",
		"jobInfos":[
			{
				"jobId":"Job ID",
				"savePoint":"Save Point",
				"status":"Job Status"
			}
		],
		"endTime":"No comment,Type =String",
		"type":"No comment,Type =String",
		"isSuccess":"No comment,Type =Boolean"
	},
	"success":"Result is Success",
	"time":"Result Time"
}
```

### Demo

```json lines showLineNumbers
 http://127.0.0.1:8888/openapi/savepoint
{
/* required-start */
"jobId": "195352b0a4518e16699983a13205f059",
"savePointType": "trigger", // trigger | stop | cancel
/* required-end */
/* custom-start */
"savePoint": "195352b0a4518e16699983a13205f059",
"address": "127.0.0.1:8081",
"gatewayConfig": {
"clusterConfig": {
"appId": "application_1637739262398_0032",
"flinkConfigPath": "/opt/src/flink-1.13.3_conf/conf",
"flinkLibPath": "hdfs:///flink13/lib/flinklib",
"yarnConfigPath": "/usr/local/hadoop/hadoop-2.7.7/etc/hadoop"
}
}
/* custom-start */
}
```