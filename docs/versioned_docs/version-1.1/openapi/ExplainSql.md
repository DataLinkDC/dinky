---
sidebar_position: 3
position: 3
id: ExplainSql
title: Explain Sql
---

> URL: http://localhost:8888/openapi/explainSql
>
> Origin Url: http://localhost:8888/openapi/explainSql
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
  "name": "Name",
  "dialect": "dialect_kynq4",
  "type": "Local",
  "savePointStrategy": 1,
  "savePointPath": "/savepoints",
  "parallelism": 4,
  "fragment": false,
  "statementSet": "false",
  "batchModel": "true",
  "clusterId": 1,
  "clusterConfigurationId": 2,
  "databaseId": 3,
  "alertGroupId": 7001,
  "note": "note_og81s",
  "step": 1,
  "jobInstanceId": 8001,
  "status": "RUNNING",
  "versionId": 9001,
  "enabled": true,
  "statement": "SELECT * FROM table",
  "clusterName": "clusterName_a2511",
  "configJson": {
    "udfConfig": {
      "templateId": 1,
      "selectKeys": [
        {}
      ],
      "className": "className_xk9o4"
    },
    "customConfig": [
      {
        "key": "test",
        "value": "test"
      }
    ]
  },
  "path": "path_gfm05",
  "clusterConfigurationName": "clusterConfigurationName_zwazn",
  "databaseName": "databaseName_qlxym",
  "envName": "envName_8wwn8",
  "alertGroupName": "alertGroupName_l8a8e",
  "useResult": "true",
  "useChangeLog": "false",
  "useAutoCancel": "false",
  "session": "session_id",
  "jobName": "MyJob",
  "maxRowNum": 100,
  "envId": 1,
  "variables": {}
}
```

###### JSON document

```json lines showLineNumbers
{
  "note": "Note",
  "configJson": {
    "udfConfig": {
      "selectKeys": [
        {}
      ],
      "className": "Class Name",
      "templateId": "Template ID"
    },
    "customConfig": [
      {
        "value": "value",
        "key": "key"
      }
    ]
  },
  "alertGroupId": "Alert Group ID",
  "dialect": "Dialect",
  "jobInstanceId": "Job Instance ID",
  "databaseName": "Database Name",
  "session": "Session",
  "parallelism": "Parallelism",
  "clusterConfigurationId": "Cluster Configuration ID",
  "batchModel": "Batch Model",
  "clusterId": "ClusterInstance ID",
  "type": "Run Mode",
  "statementSet": "Use Statement Set",
  "enabled": "Enabled",
  "path": "Path",
  "clusterName": "ClusterInstance Name",
  "savePointPath": "Save Point Path",
  "statement": "Statement",
  "id": "ID",
  "databaseId": "Database ID",
  "savePointStrategy": "Save Point Strategy",
  "jobName": "Job Name",
  "alertGroupName": "Alert Group Name",
  "useAutoCancel": "Use Auto Cancel",
  "variables": {},
  "clusterConfigurationName": "Cluster Configuration Name",
  "envId": "Environment ID",
  "useResult": "UseResult",
  "maxRowNum": "Max Row Number",
  "fragment": "Fragment Flag",
  "versionId": "Version ID",
  "envName": "Environment Name",
  "name": "Name",
  "useChangeLog": "UseChangeLog",
  "step": "Step",
  "status": "Job status"
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
	"data":{},
	"success":"Result is Success",
	"time":"Result Time"
}
```


