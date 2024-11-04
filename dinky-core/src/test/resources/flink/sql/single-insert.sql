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

INSERT INTO print_sink
SELECT id,
       name
from datagen_source;