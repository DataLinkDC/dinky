---
sidebar_position: 3
id: dinky_integration_datasophon
title: Dinky 与 DataSophon 集成
---

:::info 说明

本文档介绍 Dinky 与 DataSophon 集成

注意: 本文档基于 Dinky 1.1.0 版本编写

:::

## 前置要求

- Datasophon 1.2.1
- Dinky 1.1.0

## 1.下载Dinky并准备环境

```
这里以Flink1.16版本为例
wget https://github.com/DataLinkDC/dinky/releases/download/v1.1.0/dinky-release-1.16-1.1.0.tar.gz
tar -xzvf  dinky-release-1.16-1.1.0.tar.gz

复制mysql驱动包mysql-connector-java-8.0.28.jar到dinky-release-1.16-1.1.0的lib目录下
cp  mysql-connector-java-8.0.28.jar  dinky-release-1.16-1.1.0/lib

复制出dinky数据库的sql文件,之后会用到
cp dinky-release-1.16-1.1.0/sql/dinky-mysql.sql  ./

重新打包
tar -czvf dinky-release-1.16-1.1.0.tar.gz  dinky-release-1.16-1.1.0/

mv dinky-release-1.16-1.1.0.tar.gz  /opt/datasophon/DDP/packages 
cd /opt/datasophon/DDP/packages 
 
md5sum dinky-release-1.16-1.1.0.tar.gz  | awk '{print $1}' >dinky-release-1.16-1.1.0.tar.gz.md5
```

## 2.准备服务配置模板

在每个节点的datasophon-worker配置目录下添加配置模板
```
cd  /opt/datasophon/datasophon-worker/conf/templates
1. vim  dinky-application.ftl 

#################################################################################################################
################################################# Common Config #################################################
#################################################################################################################
# Dinky application port
server:
  port: 8888
  shutdown: graceful

spring:
  # Dinky application name
  application:
    name: Dinky
  profiles:
    # The h2 database is used by default. If you need to use other databases, please set the configuration active to: mysql, currently supports [mysql, pgsql, h2]
    # If you use mysql database, please configure mysql database connection information in application-mysql.yml
    # If you use pgsql database, please configure pgsql database connection information in application-pgsql.yml
    # If you use the h2 database, please configure the h2 database connection information in application-h2.yml,
    # note: the h2 database is only for experience use, and the related data that has been created cannot be migrated, please use it with caution
    # 注意这里修改为db_activate参数
    active: ${db_activate} #[h2,mysql,pgsql]
    include:
      - jmx
      - flyway
  lifecycle:
    timeout-per-shutdown-phase: 30s

  # mvc config
  mvc:
    pathmatch:
      # Path matching strategy, default ant_path_matcher, support ant_path_matcher and path_pattern_parser
      matching-strategy: ant_path_matcher
    format:
      date: yyyy-MM-dd HH:mm:ss # date format
      time: HH:mm:ss # time format
      date-time: yyyy-MM-dd HH:mm:ss # date-time format

  # json format global configuration
  jackson:
    time-zone: GMT+8 # Time zone, default is GMT+8
    date-format: yyyy-MM-dd HH:mm:ss # Date format, the default is yyyy-MM-dd HH:mm:ss

  # circular references allowed
  main:
    allow-circular-references: true

  # file upload config of servlet, the default is 500MB
  servlet:
    multipart:
      enabled: true
      max-file-size: 524288000
      max-request-size: 524288000

  #  1. By default, memory cache metadata information is used,
  #  2. DINKY supports Redis caching. If necessary, please change simple to Redis and open the Redis connection configuration below
  #  3. Sub configuration items can be opened or customized as needed
  cache:
    type: simple
    # If the type is configured as Redis, this item can be configured as needed
#    redis:
      # Do you want to cache empty values? Just save by default
#      cache-null-values: false
      # Cache expiration time, 24 hours
#      time-to-live: 86400

  ########################################################## Redis配置 ##########################################################
  # Note: Redis related configurations in DINKY can be used to cache meta-data information (memory caching is used by default) and cache session information of SA TOKEN (dependency needs to be added,please refer to the official documentation of SA TOKEN for Redis caching configuration instructions)
  ## 1. If you need to use Redis to cache metadata information, please configure cache. Type to Redis and then configure the following configuration items
  ## 2. If you need to use Redis to cache SA Token session information, please follow the instructions in the official SA Token documentation to configure it
  # Note: Please pay attention to the indentation after opening comments, otherwise it may cause configuration file parsing errors. Note that when both 1 and 2 are used simultaneously, only the same Redis database can be supported
#  redis:
#    host: localhost
#    port: 6379
#    username:
#    password:
    # Redis database number
#    database: 10
#    jedis:
#      pool:
        # The maximum number of connections in the connection pool (use a negative value to indicate no limit)
#        max-active: 50
        # The maximum blocking waiting time of the connection pool (use a negative value to indicate no limit)
#        max-wait: 3000
        # The maximum number of idle connections in the connection pool
#        max-idle: 20
        # The minimum number of idle connections in the connection pool
#        min-idle: 5
    # Connection timeout (milliseconds)
#    timeout: 5000

---

#################################################################################################################
################################################# Mybatis Config ################################################
######### Please note: that the following configurations are not recommended to be modified #####################
#################################################################################################################
mybatis-plus:
  mapper-locations: classpath:/mapper/*Mapper.xml
  # Entity scanning, multiple packages are separated by commas or semicolons
  typeAliasesPackage: org.dinky.model
  global-config:
    db-config:
      id-type: auto
      # Logic delete configuration : 0: false(Not deleted), 1: true(deleted)
      logic-delete-field: is_delete
      logic-delete-value: 1
      logic-not-delete-value: 0
    banner: false
  configuration:
    ##### mybatis-plus prints complete sql (only for development environment)
    #log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl
  type-handlers-package: org.dinky.data.typehandler

---
#################################################################################################################
################################################# SMS Config ####################################################
#################################################################################################################
sms:
  is-print: false


---
#################################################################################################################
################################################# Sa-Token Config ###############################################
#################################################################################################################
# Sa-Token basic configuration
sa-token:
  # The validity period of the token, the unit is 10 hours by default, -1 means it will never expire
  timeout: 36000
  # The temporary validity period of the token (the token will be considered as expired if there is no operation within the specified time)
  # unit: second , if you do not need to set a temporary token, you can set it to -1
  active-timeout: -1
  # Whether to allow the same account to log in concurrently (when true, allow login together, when false, new login squeezes out old login)
  is-concurrent: false
  # When multiple people log in to the same account, whether to share a token (if true, all logins share a token, and if false, create a new token for each login)
  is-share: true
  # token style
  token-style: uuid
  # Whether to output the operation log
  is-log: false
  # Whether to print banner
  is-print: false
  # The secret key
  jwt-secret-key: 0DA4198858E84F1AADDF846340587A85
  # is write header
  is-write-header: true
  # is read header
  is-read-header: true
  token-name: token
  is-read-cookie: true

---
#################################################################################################################
################################################# knife4j Config ################################################
#################################################################################################################
knife4j:
  enable: true
  setting:
    language: en


---
#################################################################################################################
################################################# Crypto Config #################################################
#################################################################################################################
crypto:
  enabled: false
  encryption-password:



2.vim dinky-application-mysql.ftl
  若配置其他数据源,新建对应的ftl文件,将相应的application-xx.yml文件内容写入,并修改service_ddl.json的配置即可

spring:
  datasource:
    url: ${databaseUrl}
    username: ${username}
    password: ${password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    
分发ftl到其他节点 (若在当前节点安装dinky的话就不用分发)
scp dinky.ftl datasophon02:/opt/datasophon/datasophon-worker/conf/templates
 
重启所有work节点
sh  /opt/datasophon/datasophon-worker/bin/datasophon-worker.sh restart worker
```
## 3.准备配置文件service_ddl.json

```
cd /opt/datasophon-manager-1.2.1/conf/meta/DDP-1.2.1
mkdir DINKY && cd DINKY
vim service_ddl.json
```

```
{
  "name": "DINKY",
  "label": "Dinky",
  "description": "流处理极速开发框架,流批一体&湖仓一体的云原生平台,一站式计算平台",
  "version": "1.1.0",
  "sortNum": 19,
  "dependencies":[],
  "packageName": "dinky-release-1.16-1.1.0.tar.gz",
  "decompressPackageName": "dinky-release-1.16-1.1.0",
  "roles": [
    {
      "name": "Dinky",
      "label": "Dinky",
      "roleType": "master",
      "cardinality": "1",
      "logFile": "logs/dinky.log",
      "jmxPort": 10087,
      "startRunner": {
        "timeout": "60",
        "program": "auto.sh",
        "args": [
          "startWithJmx"
        ]
      },
      "stopRunner": {
        "timeout": "600",
        "program": "auto.sh",
        "args": [
          "stop"
        ]
      },
      "statusRunner": {
        "timeout": "60",
        "program": "auto.sh",
        "args": [
          "status"
        ]
      },
      "restartRunner": {
        "timeout": "60",
        "program": "auto.sh",
        "args": [
          "restart"
        ]
      },
      "externalLink": {
        "name": "Dinky Ui",
        "label": "Dinky Ui",
        "url": "http://${host}:${serverPort}"
      }
    }
  ],
  "configWriter": {
    "generators": [
	{
	    "filename": "application.yml",
        "configFormat": "custom",
        "outputDirectory": "config",
        "templateName": "dinky-application.ftl",
        "includeParams": [
          "db_activate"
        ]
	  },
      {
        "filename": "application-mysql.yml",
        "configFormat": "custom",
        "outputDirectory": "config",
        "templateName": "dinky-application-mysql.ftl",
        "includeParams": [
          "databaseUrl",
          "username",
          "password",
          "serverPort"
        ]
      }
    ]
  },
  "parameters": [
      {
      "name": "db_activate",
      "label": "Dinky激活数据库",
      "description": "可选项为h2、mysql、pgsql,默认为mysql。若要修改其他数据库请配置相应数据库的ftl文件",
      "configType": "map",
      "required": true,
      "type": "input",
      "value": "",
      "configurableInWizard": true,
      "hidden": false,
      "defaultValue": "mysql"
    },
    {
      "name": "databaseUrl",
      "label": "Dinky数据库地址",
      "description": "",
      "configType": "map",
      "required": true,
      "type": "input",
      "value": "",
      "configurableInWizard": true,
      "hidden": false,
      "defaultValue": "jdbc:mysql://${apiHost}:3306/dinky?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
    },
    {
      "name": "username",
      "label": "Dinky数据库用户名",
      "description": "",
      "configType": "map",
      "required": true,
      "type": "input",
      "value": "",
      "configurableInWizard": true,
      "hidden": false,
      "defaultValue": "root"
    },
    {
      "name": "password",
      "label": "Dinky数据库密码",
      "description": "",
      "configType": "map",
      "required": true,
      "type": "input",
      "value": "",
      "configurableInWizard": true,
      "hidden": false,
      "defaultValue": "123456"
    },
    {
      "name": "serverPort",
      "label": "Dinky服务端口",
      "description": "",
      "configType": "map",
      "required": true,
      "type": "input",
      "value": "",
      "configurableInWizard": true,
      "hidden": false,
      "defaultValue": "8888"
    }
  ]
}
```

**重启datasophon-manager的api**

```
sh /opt/datasophon-manager-1.2.1/bin/datasophon-api.sh restart api
```

## 4.安装Dinky
![PixPin_2024-08-12_21-17-28](https://github.com/user-attachments/assets/4781facf-155f-4aff-ae33-534cbe14211d)


### 4.1 手动创建数据库并且运行sql

```
mysql -u root -p -e "create database dinky" 
mysql -u root -p -D dinky < dinky-mysql.sql
```
## 5.Dinky集成grafana监控

### 5.1 datasophon1.2.1默认存在，可跳过

```
cd /opt/datasophon/prometheus
 
vim prometheus.yml 检查是否有dinky配置文件  如果没有则添加
  - job_name: 'dinky'
    file_sd_configs:
     - files:
       - configs/dinky.json
       
 cd /opt/datasophon/prometheus/configs
 vim dinky.json  检查是否有dinky的配置文件  如果没有添加
 
[
 {
  "targets":["datasophon01:10087"]
 }
] 
然后重启Prometheus服务(以上配置文件1.2.1默认存在，若不存在添加后重启prometheus)
```

### 5.2 Grafana 配置

通过下图展示的url进去grafana配置图表，默认登录账户密码：admin ：admin
![bbbb](https://github.com/user-attachments/assets/9f8478ad-3a64-4d3a-bf26-e97036e2d3c0)

### 5.3 创建Grafana 模板文件

vim dinky.json

```
{
  "annotations": {
    "list": [
      {
        "builtIn": 1,
        "datasource": {
          "type": "grafana",
          "uid": "-- Grafana --"
        },
        "enable": true,
        "hide": true,
        "iconColor": "rgba(0, 211, 255, 1)",
        "name": "Annotations & Alerts",
        "target": {
          "limit": 100,
          "matchAny": false,
          "tags": [],
          "type": "dashboard"
        },
        "type": "dashboard"
      }
    ]
  },
  "editable": true,
  "fiscalYearStartMonth": 0,
  "graphTooltip": 0,
  "id": 33,
  "links": [],
  "liveNow": false,
  "panels": [
    {
      "datasource": {
        "type": "prometheus",
        "uid": "hj6gjW44z"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [
            {
              "options": {
                "0": {
                  "text": "下线"
                },
                "1": {
                  "text": "正常"
                }
              },
              "type": "value"
            },
            {
              "options": {
                "match": "null",
                "result": {
                  "text": "下线"
                }
              },
              "type": "special"
            }
          ],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "#d44a3a",
                "value": null
              },
              {
                "color": "#e24d42",
                "value": 0
              },
              {
                "color": "#299c46",
                "value": 1
              }
            ]
          },
          "unit": "none"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 4,
        "w": 4,
        "x": 0,
        "y": 0
      },
      "hideTimeOverride": false,
      "id": 8,
      "links": [
        {
          "targetBlank": true,
          "title": "Tomcat dashboard",
          "url": "/d/chanjarster-tomcat-dashboard/tomcat-dashboard?$__url_time_range&$__all_variables"
        }
      ],
      "maxDataPoints": 100,
      "options": {
        "colorMode": "value",
        "graphMode": "none",
        "justifyMode": "auto",
        "orientation": "horizontal",
        "reduceOptions": {
          "calcs": [
            "lastNotNull"
          ],
          "fields": "",
          "values": false
        },
        "text": {
          "valueSize": 38
        },
        "textMode": "auto"
      },
      "pluginVersion": "9.1.6",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "hj6gjW44z"
          },
          "editorMode": "code",
          "expr": "up{job=\"dinky\"}",
          "format": "time_series",
          "instant": true,
          "interval": "",
          "intervalFactor": 1,
          "legendFormat": "",
          "refId": "A"
        }
      ],
      "title": "Dinky状态",
      "type": "stat"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "hj6gjW44z"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [
            {
              "options": {
                "match": "null",
                "result": {
                  "text": "N/A"
                }
              },
              "type": "special"
            }
          ],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "dateTimeAsIso"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 4,
        "w": 8,
        "x": 4,
        "y": 0
      },
      "id": 2,
      "links": [],
      "maxDataPoints": 100,
      "options": {
        "colorMode": "value",
        "graphMode": "none",
        "justifyMode": "auto",
        "orientation": "horizontal",
        "reduceOptions": {
          "calcs": [
            "lastNotNull"
          ],
          "fields": "",
          "values": false
        },
        "text": {
          "valueSize": 38
        },
        "textMode": "auto"
      },
      "pluginVersion": "9.1.6",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "hj6gjW44z"
          },
          "editorMode": "code",
          "expr": "process_start_time_seconds{job=\"dinky\"}*1000",
          "legendFormat": "__auto",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "Dinky启动时间",
      "type": "stat"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "hj6gjW44z"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [
            {
              "options": {
                "match": "null",
                "result": {
                  "text": "N/A"
                }
              },
              "type": "special"
            }
          ],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "s"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 4,
        "w": 4,
        "x": 12,
        "y": 0
      },
      "id": 4,
      "links": [],
      "maxDataPoints": 100,
      "options": {
        "colorMode": "value",
        "graphMode": "none",
        "justifyMode": "auto",
        "orientation": "horizontal",
        "reduceOptions": {
          "calcs": [
            "lastNotNull"
          ],
          "fields": "",
          "values": false
        },
        "text": {
          "valueSize": 38
        },
        "textMode": "auto"
      },
      "pluginVersion": "9.1.6",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "hj6gjW44z"
          },
          "editorMode": "code",
          "expr": "time() - process_start_time_seconds{job=\"dinky\"}",
          "interval": "",
          "legendFormat": "",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "Dinky运行时长",
      "type": "stat"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "hj6gjW44z"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [
            {
              "options": {
                "match": "null",
                "result": {
                  "text": "N/A"
                }
              },
              "type": "special"
            }
          ],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "bytes"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 4,
        "w": 4,
        "x": 16,
        "y": 0
      },
      "id": 6,
      "links": [],
      "maxDataPoints": 100,
      "options": {
        "colorMode": "value",
        "graphMode": "none",
        "justifyMode": "auto",
        "orientation": "horizontal",
        "reduceOptions": {
          "calcs": [
            "lastNotNull"
          ],
          "fields": "",
          "values": false
        },
        "text": {
          "valueSize": 38
        },
        "textMode": "auto"
      },
      "pluginVersion": "9.1.6",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "hj6gjW44z"
          },
          "editorMode": "code",
          "expr": "jvm_memory_bytes_max{job=\"dinky\",area=\"heap\"}",
          "legendFormat": "__auto",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "Dinky堆内存",
      "type": "stat"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "hj6gjW44z"
      },
      "fieldConfig": {
        "defaults": {
          "mappings": [],
          "max": 100,
          "min": 0,
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              },
              {
                "color": "red",
                "value": 80
              }
            ]
          },
          "unit": "%"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 4,
        "w": 4,
        "x": 20,
        "y": 0
      },
      "id": 10,
      "options": {
        "orientation": "auto",
        "reduceOptions": {
          "calcs": [
            "lastNotNull"
          ],
          "fields": "",
          "values": false
        },
        "showThresholdLabels": false,
        "showThresholdMarkers": true
      },
      "pluginVersion": "9.1.6",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "hj6gjW44z"
          },
          "editorMode": "code",
          "expr": "jvm_memory_bytes_used{area=\"heap\",job=\"dinky\"}*100/jvm_memory_bytes_max{area=\"heap\",job=\"dinky\"}",
          "legendFormat": "__auto",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "Dinky堆内存使用率",
      "type": "gauge"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "hj6gjW44z"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "none",
            "hideFrom": {
              "legend": false,
              "tooltip": false,
              "viz": false
            },
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "links": [],
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              },
              {
                "color": "red",
                "value": 80
              }
            ]
          },
          "unit": "bytes"
        },
        "overrides": [
          {
            "matcher": {
              "id": "byName",
              "options": "Usage %"
            },
            "properties": [
              {
                "id": "custom.drawStyle",
                "value": "bars"
              },
              {
                "id": "custom.fillOpacity",
                "value": 100
              },
              {
                "id": "color",
                "value": {
                  "fixedColor": "#6d1f62",
                  "mode": "fixed"
                }
              },
              {
                "id": "custom.lineWidth",
                "value": 0
              },
              {
                "id": "unit",
                "value": "percentunit"
              },
              {
                "id": "min",
                "value": 0
              },
              {
                "id": "max",
                "value": 1
              }
            ]
          }
        ]
      },
      "gridPos": {
        "h": 10,
        "w": 12,
        "x": 0,
        "y": 4
      },
      "id": 12,
      "links": [],
      "options": {
        "legend": {
          "calcs": [
            "mean",
            "max"
          ],
          "displayMode": "table",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "pluginVersion": "9.1.6",
      "repeat": "memarea",
      "repeatDirection": "h",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "hj6gjW44z"
          },
          "editorMode": "code",
          "expr": "jvm_memory_bytes_used{area=\"heap\",job=\"dinky\"}",
          "legendFormat": "已用内存",
          "range": true,
          "refId": "A"
        },
        {
          "datasource": {
            "type": "prometheus",
            "uid": "hj6gjW44z"
          },
          "editorMode": "code",
          "expr": " jvm_memory_bytes_max{area=\"heap\",job=\"dlink\"}",
          "hide": false,
          "legendFormat": "总内存",
          "range": true,
          "refId": "B"
        },
        {
          "datasource": {
            "type": "prometheus",
            "uid": "hj6gjW44z"
          },
          "editorMode": "code",
          "expr": "jvm_memory_bytes_used{area=\"heap\",job=\"dlink\"} / jvm_memory_bytes_max >= 0",
          "hide": false,
          "legendFormat": "使用率",
          "range": true,
          "refId": "C"
        }
      ],
      "title": "Dinky堆内存使用趋势",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "hj6gjW44z"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "none",
            "hideFrom": {
              "legend": false,
              "tooltip": false,
              "viz": false
            },
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "links": [],
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              },
              {
                "color": "red",
                "value": 80
              }
            ]
          },
          "unit": "s"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 10,
        "w": 12,
        "x": 12,
        "y": 4
      },
      "id": 14,
      "links": [],
      "options": {
        "legend": {
          "calcs": [
            "lastNotNull",
            "max",
            "min"
          ],
          "displayMode": "table",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "pluginVersion": "9.1.6",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "hj6gjW44z"
          },
          "editorMode": "code",
          "expr": "increase(jvm_gc_collection_seconds_sum{job=\"dinky\"}[$__interval])",
          "format": "time_series",
          "interval": "60s",
          "intervalFactor": 1,
          "legendFormat": "{{gc}}",
          "metric": "jvm_gc_collection_seconds_sum",
          "range": true,
          "refId": "A",
          "step": 10
        }
      ],
      "title": "Dinky GC时间趋势图",
      "type": "timeseries"
    }
  ],
  "schemaVersion": 37,
  "style": "dark",
  "tags": [],
  "templating": {
    "list": []
  },
  "time": {
    "from": "now-6h",
    "to": "now"
  },
  "timepicker": {},
  "timezone": "",
  "title": "Dinky",
  "uid": "9qU9T1OVk",
  "version": 4,
  "weekStart": ""
}
```

### 5.4 导入创建的模板文件
![aaaddsds08-12_21-37-54](https://github.com/user-attachments/assets/1a284002-5d7a-40b2-b015-f8eaf449e1db)
![aaaaaaaaa-38-32](https://github.com/user-attachments/assets/024d61b8-e8d5-4f0e-87f8-ce95c7ad30b3)

**查看datasophon数据库中t_ddh_cluster_service_dashboard表中是否原就存在dinky 如果不存在添加**

```
19 DINKY http://${grafanaHost}:3000/d/9qU9T1OVk/dinky?kiosk&refresh=1m
```
![3845420da980622d83c4f9d0a3ec91bb](https://github.com/user-attachments/assets/06252190-8682-445c-ae56-97a3b15cf841)

回到datasophon的dinky服务，刷新即可在总览看到详细监控信息
![nnnnnnn](https://github.com/user-attachments/assets/58d1d988-12d7-4697-b4ef-e0845d5d1589)

