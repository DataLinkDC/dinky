---
position: 1
id: row_limits
title: 行权限
---


通过添加“行权限”规则，可使指定范围的角色和用户仅能查看指定范围的行数据。例如用户 A 仅能查看数据集“ '班级'字段=高二 ”的数据。但是只在只在flinksql中生效。

![row_limits_add](http://www.aiwenmo.com/dinky/docs/test/row_limits_add.png)

如上图所示，表名为flink中的表名，表达式只需要填where后的语句即可。


