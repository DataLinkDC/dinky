CREATE TABLE json_table (
  total BIGINT,
  `data` ARRAY<ROW<
        deal_date STRING,
        close_date STRING,
        card_no STRING,
        deal_value STRING,
        deal_type STRING,
        company_name STRING,
        car_no STRING,
        station STRING,
        conn_mark STRING,
        deal_money STRING,
        equ_no STRING
    >>,
  `page` BIGINT,
  `rows` BIGINT
)
WITH
  (
    'connector' = 'filesystem',
    'path' = 'hdfs:///userFiles/U0000001/2018record3.jsons',
    'format' = 'json'
  );

CREATE TABLE `default_catalog`.`default_database`.szt_data1 WITH (
   'connector' = 'blackhole'
) AS
SELECT
  total,
  data_row.d1,
  data_row.close_date,
  data_row.card_no1,
  data_row.deal_value,
  data_row.deal_type,
  data_row.company_name,
  data_row.car_no,
  data_row.station,
  data_row.conn_mark,
  data_row.deal_money,
  data_row.equ_no,
  page,
  `rows`
FROM
  json_table,
  UNNEST(json_table.data) AS data_row (
    d1,
    close_date,
    card_no1,
    deal_value,
    deal_type,
    company_name,
    car_no,
    station,
    conn_mark,
    deal_money,
    equ_no
  );