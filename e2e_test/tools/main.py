import requests

from env import addStandaloneCluster, addYarnCluster, addK8sNativeCluster
from login import login
from task import addCatalogue, Task

if __name__ == '__main__':
    session = requests.session()
    login(session)
    clusterId = addStandaloneCluster(session)
    yarn_cluster_id = addYarnCluster(session)
    k8s_native_cluster_id = addK8sNativeCluster(session)
    catalogue = addCatalogue(session, "flink-sql-task")
    sql = "DROP TABLE IF EXISTS source_table3;\r\nCREATE TABLE IF NOT EXISTS source_table3(\r\n--订单id\r\n`order_id` BIGINT,\r\n--产品\r\n\r\n`product` BIGINT,\r\n--金额\r\n`amount` BIGINT,\r\n\r\n--支付时间\r\n`order_time` as CAST(CURRENT_TIMESTAMP AS TIMESTAMP(3)), -- `在这里插入代码片`\r\n--WATERMARK\r\nWATERMARK FOR order_time AS order_time - INTERVAL '2' SECOND\r\n) WITH(\r\n'connector' = 'datagen',\r\n 'rows-per-second' = '1',\r\n 'fields.order_id.min' = '1',\r\n 'fields.order_id.max' = '2',\r\n 'fields.amount.min' = '1',\r\n 'fields.amount.max' = '10',\r\n 'fields.product.min' = '1',\r\n 'fields.product.max' = '2'\r\n);\r\n\r\n-- SELECT * FROM source_table3 LIMIT 10;\r\n\r\nDROP TABLE IF EXISTS sink_table5;\r\nCREATE TABLE IF NOT EXISTS sink_table5(\r\n--产品\r\n`product` BIGINT,\r\n--金额\r\n`amount` BIGINT,\r\n--支付时间\r\n`order_time` TIMESTAMP(3),\r\n--1分钟时间聚合总数\r\n`one_minute_sum` BIGINT\r\n) WITH(\r\n'connector'='print'\r\n);\r\n\r\nINSERT INTO sink_table5\r\nSELECT\r\nproduct,\r\namount,\r\norder_time,\r\nSUM(amount) OVER(\r\nPARTITION BY product\r\nORDER BY order_time\r\n-- 标识统计范围是1个 product 的最近 1 分钟的数据\r\nRANGE BETWEEN INTERVAL '1' MINUTE PRECEDING AND CURRENT ROW\r\n) as one_minute_sum\r\nFROM source_table3;"
    flink_sql_datagen_test = Task(session, clusterId, yarn_cluster_id,k8s_native_cluster_id, catalogue.id, "flink-sql-datagen-test", sql)
    flink_sql_datagen_test.runFlinkTask(wait_time=10, is_async=True)
