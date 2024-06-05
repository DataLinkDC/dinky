---
sidebar_position: 2
position: 2
id: CancelFlinkJob
title: Cancel Flink Job
---

> URL: http://localhost:8888/openapi/cancel?id=1&withSavePoint=false&forceCancel=true
>
> Origin Url: http://localhost:8888/openapi/cancel
>
> Type: GET

## Request headers

| Header Name | Header Value |
|-------------|--------------|

## Parameters

### Path parameters

| Parameter | Type | Value | Description |
|-----------|------|-------|-------------|

### URL parameters

| Required | Parameter     | Type    | Value | Description |
|----------|---------------|---------|-------|-------------|
| true     | id            | Number  | 1     |             |
| true     | withSavePoint | Boolean | false |             |
| true     | forceCancel   | Boolean | true  |             |

### Body parameters

##### JSON

```json lines showLineNumbers

```

##### JSON document

```json lines showLineNumbers

```

#### Form URL-Encoded

| Required | Parameter | Type | Value | Description |
|----------|-----------|------|-------|-------------|

#### Multipart

| Required | Parameter | Type | Value | Description |
|----------|-----------|------|-------|-------------|

## Response

### Response example

```json lines showLineNumbers

```

### Response document

```json lines showLineNumbers
{
  "msg": "Result Message",
  "code": "Result Code",
  "data": {
    "java.lang.Boolean": "No comment,Type =Boolean"
  },
  "success": "Result is Success",
  "time": "Result Time"
}
```


