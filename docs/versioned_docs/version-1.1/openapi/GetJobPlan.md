---
sidebar_position: 7
position: 7
id: GetJobPlan
title: Get Job Plan
---

> URL: http://localhost:8888/openapi/getJobPlan
>
> Origin Url: http://localhost:8888/openapi/getJobPlan
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
  "dialect": "dialect_ujgla",
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
  "note": "note_x9b2i",
  "step": 1,
  "jobInstanceId": 8001,
  "status": "RUNNING",
  "versionId": 9001,
  "enabled": true,
  "statement": "SELECT * FROM table",
  "clusterName": "clusterName_spzq9",
  "configJson": {
    "udfConfig": {
      "templateId": 1,
      "selectKeys": [
        {}
      ],
      "className": "className_6pfjd"
    },
    "customConfig": [
      {
        "key": "test",
        "value": "test"
      }
    ]
  },
  "path": "path_4wg3s",
  "clusterConfigurationName": "clusterConfigurationName_6oq9w",
  "databaseName": "databaseName_dsgrr",
  "envName": "envName_v1ukq",
  "alertGroupName": "alertGroupName_hln14",
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
  "msg": "Result Message",
  "code": "Result Code",
  "data": {
    "_children": {},
    "_nodeFactory": {
      "_cfgBigDecimalExact": "No comment,Type =Boolean"
    }
  },
  "success": "Result is Success",
  "time": "Result Time"
}
```

### Demo

```json lines showLineNumbers
 http://127.0.0.1:8888/openapi/getJobPlan
{
/* required-start */
"statement": "CREATE TABLE Orders (\r\n    order_number INT,\r\n    price        DECIMAL(32,2),\r\n    order_time   TIMESTAMP(3)\r\n) WITH (\r\n  'connector' = 'datagen',\r\n  'rows-per-second' = '1',\r\n  'fields.order_number.kind' = 'sequence',\r\n  'fields.order_number.start' = '1',\r\n  'fields.order_number.end' = '1000'\r\n);\r\nCREATE TABLE pt (\r\nordertotal INT,\r\nnumtotal INT\r\n) WITH (\r\n 'connector' = 'print'\r\n);\r\ninsert into pt select 1 as ordertotal ,sum(order_number)*2 as numtotal from Orders",
/* required-end */
/* default-start */
"useStatementSet": false,
"fragment": false,
"parallelism": 1,
/* default-start */
/* custom-start */
"configuration": {
"table.exec.resource.default-parallelism": 2
}
/* custom-end */
}
```