---
sidebar_position: 12
position: 12
id: SubmitTaskJob
title: Submit TaskJob
---

> URL: http://localhost:8888/openapi/submitTask
>
> Origin Url: http://localhost:8888/openapi/submitTask
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
{
  "id": 6,
  "savePointPath": "/savepoints",
  "variables": {}
}
```

###### JSON document

```json lines showLineNumbers
{
	"variables":{},
	"savePointPath":"Save Point Path",
	"id":"ID"
}
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
		"jobConfig":{
			"checkpoint":"Check Point",
			"jobName":"Name of the job",
			"configJson":{},
			"useAutoCancel":"Flag indicating whether to use auto-cancel",
			"variables":{},
			"address":"Job manager address",
			"parallelism":"Parallelism level",
			"clusterConfigurationId":"Cluster configuration ID",
			"batchModel":"Flag indicating whether to use batch model",
			"clusterId":"Cluster ID",
			"type":"Flink run mode",
			"statementSet":"Flag indicating whether to use statement set",
			"pyFiles":[
				"List of Python files"
			],
			"useResult":"Flag indicating whether to use the result",
			"jarFiles":[
				"List of JAR files"
			],
			"maxRowNum":"Maximum number of rows",
			"fragment":"Flag indicating whether to use SQL fragment",
			"savePointPath":"Save Point Path",
			"useChangeLog":"Flag indicating whether to use change log",
			"step":"Task JobLifeCycle",
			"savePointStrategy":"Save point strategy",
			"useRemote":"Flag indicating whether to use remote execution",
			"taskId":"Task ID",
			"gatewayConfig":{
				"appConfig":{
					"userJarParas":[
						"User JAR file parameters"
					],
					"userJarMainAppClass":"Main application class in the JAR file",
					"userJarPath":"Path to user JAR file"
				},
				"kubernetesConfig":{
					"dockerConfig":{},
					"tmPodTemplate":"TaskManager pod template for Flink jobs",
					"configuration":{},
					"jmPodTemplate":"JobManager pod template for Flink jobs",
					"podTemplate":"Pod template for Flink jobs",
					"kubeConfig":"KubeConfig"
				},
				"jarPaths":[
					"Paths to the JAR files"
				],
				"clusterConfig":{
					"hadoopConfigPath":"Path to YARN configuration file",
					"flinkConfigPath":"Path to Flink configuration file",
					"flinkLibPath":"Path to Flink library directory",
					"appId":"YARN application ID",
					"hadoopConfigMap":{},
					"hadoopConfigList":[
						{
							"name":"Custom Config Name",
							"value":"Custom Config Value"
						}
					]
				},
				"type":"Type of gateway (e.g., YARN, Kubernetes)",
				"taskId":"ID of the task associated with the job",
				"flinkConfig":{
					"jobName":"Name of the Flink job",
					"jobId":"ID of the Flink job",
					"flinkConfigList":[
						{
							"name":"Custom Config Name",
							"value":"Custom Config Value"
						}
					],
					"configuration":{},
					"savePoint":"Path to savepoint",
					"action":"Action to perform (e.g., START, STOP)",
					"flinkVersion":"Flink version",
					"savePointType":"Type of savepoint (e.g., TRIGGER, CANCEL)"
				}
			}
		},
		"jobInstanceId":"Unique identifier for the job instance",
		"jobManagerAddress":"Address of the job manager",
		"error":"Error message in case of job failure",
		"result":{},
		"jobId":"Unique identifier for the job",
		"success":"Flag indicating whether the job was successful",
		"statement":"SQL statement executed by the job",
		"startTime":"Start time of job execution",
		"id":"Unique identifier for the job result",
		"endTime":"End time of job execution",
		"results":[
			{
				"total":"No comment,Type =Number",
				"columns":[
					"columns_50yua"
				],
				"success":"No comment,Type =Boolean",
				"limit":"No comment,Type =Number",
				"rowData":[
					{
						"loadFactor":1.0,
						"threshold":1,
						"accessOrder":true
					}
				],
				"startTime":"No comment,Type =String",
				"page":"No comment,Type =Number",
				"endTime":"No comment,Type =String",
				"time":"No comment,Type =Number",
				"error":"No comment,Type =String"
			}
		],
		"status":"Status of the job"
	},
	"success":"Result is Success",
	"time":"Result Time"
}
```


