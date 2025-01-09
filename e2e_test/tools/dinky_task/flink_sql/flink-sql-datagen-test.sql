DROP TABLE IF EXISTS source_table3;

CREATE TABLE IF NOT EXISTS source_table3 (
  -- 订单id
  `order_id` BIGINT,
  --产品
  `product` BIGINT,
  --金额
  `amount` BIGINT,
  -- 支付时间
  `order_time` as CAST(CURRENT_TIMESTAMP AS TIMESTAMP(3)), -- `在这里插入代码片`
  --WATERMARK
  WATERMARK FOR order_time AS order_time - INTERVAL '2' SECOND
)
WITH
  (
    'connector' = 'datagen',
    'rows-per-second' = '1',
    'fields.order_id.min' = '1',
    'fields.order_id.max' = '2',
    'fields.amount.min' = '1',
    'fields.amount.max' = '10',
    'fields.product.min' = '1',
    'fields.product.max' = '2'
  );

-- SELECT * FROM source_table3 LIMIT 10;
DROP TABLE IF EXISTS sink_table5;

CREATE TABLE IF NOT EXISTS sink_table5 (
  --产品
  `product` BIGINT,
  --金额
  `amount` BIGINT,
  --支付时间
  `order_time` TIMESTAMP(3),
  -- 1分钟时间聚合总数
  `one_minute_sum` BIGINT
)
WITH
  ('connector' = 'print');

INSERT INTO
  sink_table5
SELECT
  product,
  amount,
  order_time,
  SUM(amount) OVER (
    PARTITION BY
      product
    ORDER BY
      order_time
      -- 标识统计范围是1个 product 的最近 1 分钟的数据
      RANGE BETWEEN INTERVAL '1' MINUTE PRECEDING
      AND CURRENT ROW
  ) as one_minute_sum
FROM
  source_table3;
