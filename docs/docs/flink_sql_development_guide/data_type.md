---
sidebar_position: 3
id: data_type
title: 数据类型
---

FlinkSQL 采用符合 ANSI SQL 规范的定义，支持丰富的数据类型。用户在使用 `CREATE TABLE` 语句定义一个数据表时，可以用这些数据类型来定义每个字段的类型。

## 支持的数据类型

| 类型名称                                                     | 说明                                                         |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| CHAR<br/> CHAR(n)                                            | 固定长度字符串。n 表示容纳的字符数，默认为1，即 CHAR 等价于 CHAR(1) |
| VARCHAR<br/> VARCHAR(n)<br/> STRING                          | 可变长度字符串。n 表示最多容纳的字符数，默认为1，<br/>即 VARCHAR 等价于 VARCHAR(1)。STRING 等价于 VARCHAR(2147483647) |
| BINARY BINARY(n)                                             | 类型可以使用 BINARY (n)声明。n 表示容纳的字节数量，默认为1，<br/>即 BINARY 等价于 BINARY(1) |
| VARBINARY VARBINARY(n)  BYTES                                | 类型可以使用 VARBINARY (n)声明。其中 n 是最大字节数。N 的值<br/>必须介于1和2之间，147,483,647(包括两者)。如果没有指定长度，<br/>则 n 等于1。BYTES 等价于 VARBINARY(2147483647) |
| DECIMAL <br/> DECIMAL(p)<br/>  DECIMAL(p,s)<br/>  DEC<br/>  DEC(p)<br/> DEC(p, s)<br/> NUMERIC<br/>NUMERIC(p)<br/>NUMERIC(p, s) | 类型可以使用 DECIMAL (p，s)声明,p 表示数字的总位数（精度），<br/>取值区间为[1，38]，默认值是10。s 表示小数点右边的位数<br/>（尾数），取值区间为[0，p]，默认值是0。NUMERIC (p，s)<br/>和 DEC (p，s)与这种类型等价，可以互换使用 |
| TINYINT                                                      | 1字节有符号整数的数据类型，取值范围是[-128，127]             |
| SMALLINT                                                     | 2字节有符号整数的数据类型，取值范围是[-32768，32767]         |
| INT<br/>INTEGER                                              | 4字节有符号整数的数据类型，取值范围是[-2147483648，<br/>2147483647]。INTEGER等价于这个类型 |
| BIGINT                                                       | 8字节有符号整数的数据类型，取值范围是[-9223372036854775808，9223372036854775807] |
| FLOAT                                                        | 4字节单精度浮点数的数据类型，                                |
| DOUBLE<br/>DOUBLE PRECISION                                  | 8个字节的双精度浮点数,DOUBLE PRECISION等价于这个类型         |
| DATE                                                         | 由年月日组成的数据类型,取值范围是[0000-01-01，9999-12-31]    |
| TIME<br/>TIME(p)                                             | 不带时区的时间的数据类型，包括小时: 分钟: 秒。<br/>取值范围是[00:00:00.000000000，23:59:59.999999999]。<br/>p 表示秒的小数位精度，取值范围是[0，9]。如果未指定，默认为0 |
| TIMESTAMP<br/> TIMESTAMP(p)<br/> TIMESTAMP WITHOUT TIME ZONE<br/> TIMESTAMP(p) WITHOUT TIME ZONE | 不带时区的时间戳的数据类型。取值范围<br/>是[0000-01-01 00:00:00.000000000，<br/>9999-12-31 23:59:59.999999999]。<br/>p 的值必须介于0和9之间(两者都包含在内)。<br/>如果未指定精度，则 p 等于6。 |
| TIMESTAMP WITH TIME ZONE<br/> TIMESTAMP(p) WITH TIME ZONE    | 带有时区的时间戳的数据类型。取值范围是<br/>[0000-01-01 00:00:00.000000000 +14:59，<br/>9999-12-31 23:59:59.999999999 -14:59]。<br/> 类型可以使用 TIMESTAMP (p) WITH TIME ZONE 声明，<br/>其中 p 是小数秒的位数(精度)。P 的值必须介于0和9之<br/>间(两者都包含在内)。如果没有<br/>如果指定了精度，p 等于6 |
| INTERVAL YEAR <br/>INTERVAL YEAR(p) <br/>INTERVAL YEAR(p) TO MONTH<br/>INTERVAL MONTH | 类型可以使用上面的组合声明，其中 p 是年的位数(年精度)。P 的值必须介于1和4之间(两者都包含在内)。如果未指定年份精度，则 p 等于2。取值范围是[-9999-11，+9999-11] |
| INTERVAL DAY  <br/>INTERVAL DAY(p1)  <br/>INTERVAL DAY(p1) TO HOUR  <br/>INTERVAL DAY(p1) TO MINUTE  <br/>INTERVAL DAY(p1) TO SECOND(p2)  <br/>INTERVAL HOUR  <br/>INTERVAL HOUR TO MINUTE  <br/>INTERVAL HOUR TO SECOND(p2)  <br/>INTERVAL MINUTE  <br/>INTERVAL MINUTE TO SECOND(p2)  <br/>INTERVAL SECOND <br/> INTERVAL SECOND(p2) | 表示以天、时、分、秒、纳秒表示的细粒度时间间隔，最高精度为纳秒。  <br/> 取值范围是[-999999 23:59:59.999999999，+999999 23:59:59.999999999]<br/> p1是天的位数(日精度) ，P1的值必须介于1和6之间(两者都包括在内)。<br/> p2是小数秒的位数(小数精度)。P2的值必须介于0和9之间(两者都包含在内)。默认情况下它等于2。 |
| ARRAY                                                        | 类型可以使用 `ARRAY<t>` 声明，其中 t 是所包含元素的数据类型  |
| MAP                                                          | 类型可以使用 `MAP<kt, vt>` 来声明，其中 kt 是关键元素的数据类型，vt 是值元素的数据类型 |
| MULTISET                                                     | 类型可以使用 `MULTISET <t>` 声明，其中 t 是所包含元素的数据类型。 |
| ROW                                                          | 可以使用 `ROW < n0 t0’d 0’，n1 t1’d 1’，... >` 声明类型，其中 n 是字段的唯一名称，t 是字段的逻辑类型，d 是字段的描述.ROW (...)是更接近 SQL 标准的同义词。例如，`ROW (myField INT，myOtherField BOOLEAN)`等价于 `ROW < myField INT，myOtherField BOOLEAN >` |
| BOOLEAN                                                      | 任意序列化类型的数据类型。这种类型是表生态系统中的一个黑盒子，仅在边缘反序列化 |
| RAW                                                          | 类型可以使用 RAW (‘ class’，‘ snapshot’)声明，其中类是原始类，快照是 base64编码中的序列化 TypeSerializerSnapshot。通常，类型字符串不是直接声明的，而是在持久化类型时生成的。 |
| NULL                                                         | 表示非类型化 NULL 值的数据类型                               |

