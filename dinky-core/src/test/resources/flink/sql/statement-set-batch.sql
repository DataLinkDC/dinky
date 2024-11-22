CREATE TABLE datagen_source
(
    id   BIGINT,
    name STRING,
    sex  INT,
    age  INT
) WITH (
      'connector' = 'datagen',
      'number-of-rows' = '10'
      );

CREATE TABLE print_sink
(
    id   BIGINT,
    name STRING,
    sex  INT,
    age  INT
) WITH (
      'connector' = 'print'
      );

CREATE TABLE print_sink2
(
    sex   BIGINT,
    total   BIGINT
) WITH (
      'connector' = 'print'
      );

CREATE TABLE print_sink3
(
    id   BIGINT,
    name STRING,
    sex  INT,
    age  INT
) WITH (
      'connector' = 'print'
      );

INSERT INTO print_sink
SELECT id,
       name,
       sex,
       age
from datagen_source /*+ OPTIONS('rows-per-second'='1') */ ;

SELECT id as select_id,
       name as select_name
from datagen_source;

SHOW TABLES;

WITH sex_with AS (
    SELECT id, sex
    FROM datagen_source
)
SELECT sex, 1 as cnt
FROM sex_with
GROUP BY sex;

INSERT INTO print_sink2
SELECT sex, 2 as total
FROM datagen_source
GROUP BY sex;

CREATE TABLE print_sink4
    WITH (
        'connector' = 'print'
        )
AS SELECT id, name, sex, age FROM datagen_source WHERE mod(id, 10) = 4;

REPLACE TABLE print_sink3
WITH (
    'connector' = 'print'
)
AS SELECT id, name, sex, age FROM datagen_source WHERE mod(id, 10) = 0;

CREATE VIEW t1(s) AS VALUES ('c'), ('a'), ('b'), ('b'), ('c');

CREATE VIEW t2(s) AS VALUES ('d'), ('e'), ('a'), ('b'), ('b');

(SELECT s FROM t1) UNION (SELECT s FROM t2);

ALTER TABLE print_sink3 RENAME TO print_sink5;

DESCRIBE print_sink5;

USE MODULES core;

SHOW TABLES;

SHOW CREATE VIEW t1;

UNLOAD MODULE core;

SET 'table.local-time-zone' = 'Europe/Berlin';

DROP TABLE print_sink5;