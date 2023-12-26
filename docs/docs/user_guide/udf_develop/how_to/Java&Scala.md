---
sidebar_position: 1
id: java&scala_udf
title: Java&Scala UDF
---

### 初始环境准备

1. 创建对应的表。
    ```sql
    CREATE TABLE `Order`
    (
        id         INT,
        product_id INT,
        quantity   INT,
        order_time TIMESTAMP,
        PRIMARY KEY (id) NOT ENFORCED
    ) WITH (
          'connector' = 'datagen',
          'fields.id.kind' = 'sequence',
          'fields.id.start' = '1',
          'fields.id.end' = '100000',
          'fields.product_id.min' = '1',
          'fields.product_id.max' = '100',
          'rows-per-second' = '1'
    );
    
    CREATE TABLE `Product`
    (
        id    INT,
        name  VARCHAR,
        price DOUBLE,
        PRIMARY KEY (id) NOT ENFORCED
    ) WITH (
          'connector' = 'datagen',
          'fields.id.min' = '1',
          'fields.id.max' = '100',
          'rows-per-second' = '1'
    );
    ```

    在数据开发界面点击保存，并且检查成功没有问题一会，运行就会在 **Catalog** 界面看到创建的 ```Order``` 和 ```Product``` 表。

    ![catalog界面](http://pic.dinky.org.cn/dinky/docs/test/yuanshuj.png)

2. 执行查询任务。

    ```sql
    SELECT o.id, p.name, o.order_time
    FROM `Order` o
    INNER JOIN
    `Product` p
    ON o.product_id = p.id;
    ```

    点击调试以后得到下面的效果图。

    ![查询任务图](http://pic.dinky.org.cn/dinky/docs/test/yuanshishuj.png)


### 定义UDF函数

创建一个UDF函数

对应函数的类名。

```java
com.test.SubFunction
```

![udf函数创建](http://pic.dinky.org.cn/dinky/docs/test/duiyinghangshu.png)

下面是udf函数的代码。
 
```java
package com.test;

import org.apache.flink.table.functions.ScalarFunction;
    
public class SubFunction extends ScalarFunction {
    public String eval(String s) {
        if(null==s){
            return s;
        }
        return s.substring(0,3);
    }
}
```

如下图所示。

![udf函数代码](http://pic.dinky.org.cn/dinky/docs/test/hanshubaocun.png)

### 函数的使用

定义一个flinksql任务，执行下面代码。
```sql
create temporary function sb_j as 'com.test.SubFunction';

SELECT o.id, sb_j(p.name), o.order_time
FROM `Order` o
INNER JOIN
`Product` p
ON o.product_id = p.id;
```

看到如下那么表示执行成功了。

![使用成功](http://pic.dinky.org.cn/dinky/docs/test/hangshushiyongchenggonlg.png)