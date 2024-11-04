CREATE TABLE datagen_source
(
    id   BIGINT,
    name STRING
) WITH (
      'connector' = 'datagen'
      );

CREATE TABLE print_sink
(
    id   BIGINT,
    name STRING
) WITH (
      'connector' = 'print'
      );

CREATE TABLE print_sink2
(
    id2   BIGINT,
    name2 STRING
) WITH (
      'connector' = 'print'
      );

INSERT INTO print_sink
SELECT id,
       name
from datagen_source;


INSERT INTO print_sink2
SELECT id as id2,
       name as name2
from datagen_source;