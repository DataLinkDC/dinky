CREATE TABLE datagen_source
(
    id   BIGINT,
    name STRING,
    sex  INT,
    age  INT
) WITH (
      'connector' = 'datagen'
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

INSERT INTO print_sink
SELECT id,
       name,
       sex,
       age
from datagen_source;