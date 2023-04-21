
CREATE TABLE DT_addSource_1 (t STRING, data STRING) WITH ('connector' = 'gbuzl', 'table-name' = 'DT');

CREATE TABLE TS_addSource_2 (t STRING, data STRING) WITH ('connector' = 'task', 'table-name' = 'TS');

CREATE VIEW V_DT_addSource_1 AS
SELECT 'task' AS task,
        t AS type,
       JSON_VALUE( data , '$.id' ) AS id,
       JSON_VALUE( data , '$.longitude' ) AS longitude,
       JSON_VALUE( data , '$.latitude' ) AS latitude,
       JSON_VALUE( data , '$.dt[1]' ) AS dt,
       JSON_VALUE( data , '$.value' ) AS v,
       PROCTIME() AS proc_time
FROM DT_addSource_1;

CREATE VIEW V_TS_addSource_2 AS
SELECT CAST( t AS STRING ) AS task,
        JSON_VALUE( data , '$.taskId' ) AS taskId,
        CAST( JSON_VALUE( data , '$.taskStatus' ) AS INT ) AS taskStatus,
        PROCTIME() AS proc_time
FROM TS_addSource_2;


CREATE VIEW _JoinOperator16 AS
SELECT type, id, longitude, latitude, dt, v, d.task, d.taskId, d.taskStatus, d.proc_time
FROM V_DT_addSource_1, LATERAL (SELECT * FROM  V_TS_addSource_2 d WHERE V_DT_addSource_1.proc_time = d.proc_time) AS d;

CREATE VIEW _CepOperator18
AS
    SELECT *
    FROM _JoinOperator16
    MATCH_RECOGNIZE(
    PARTITION BY taskId
    ORDER BY proc_time
    MEASURES
FIRST( A.taskStatus ) AS startTaskStatus,
LAST( A.taskStatus ) AS endTaskStatus
    ONE ROW PER MATCH
    AFTER MATCH SKIP PAST LAST ROW
    PATTERN (A{5})
    DEFINE
        A AS A.taskStatus = 0
    )
;

CREATE VIEW _CommSelectFunctionResult19 AS SELECT longitude, latitude, dt, v, taskId, taskStatus FROM _JoinOperator16 WHERE type='gbu';

CREATE VIEW _CommSelectFunctionResult20 AS SELECT  longitude AS longitude FROM _CommSelectFunctionResult19 ;

CREATE TABLE match_mysqlSink_2 (taskId STRING, startTaskStatus INT, endTaskStatus INT) WITH ('password'='******', 'connector' = 'jdbc', 'url' = 'jdbc:mysql://192.168.17.132:3306/flink?allowPublicKeyRetrieval=true', 'table-name' = 'match', 'username' = 'root');

INSERT INTO match_mysqlSink_2 (startTaskStatus,endTaskStatus,taskId) SELECT startTaskStatus, endTaskStatus, taskId FROM _CepOperator18;

CREATE TABLE ts_mysqlSink_1 (type STRING, id STRING, longitude STRING, latitude STRING, dt STRING, v STRING, taskId STRING, taskStatus INT) WITH ('password'='******', 'connector' = 'jdbc', 'url' = 'jdbc:mysql://192.168.17.132:3306/flink?allowPublicKeyRetrieval=true', 'table-name' = 'ts', 'username' = 'root');

INSERT INTO ts_mysqlSink_1 (longitude) SELECT longitude FROM _CommSelectFunctionResult20;

select * from match_mysqlSink_2;
