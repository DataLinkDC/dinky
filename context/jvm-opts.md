# JVM_OPTS 启动参数

Dinky 进程的堆参数通过环境变量 `JVM_OPTS` 注入，`script/bin/auto.sh` 不再写死覆盖。

## 解析规则

启动脚本在 sourcing `/etc/profile.d/dinky_env`、`/etc/profile.d/dinky_db`、`/etc/profile` 之后执行：

```
JVM_OPTS="${JVM_OPTS:-${DEFAULT_JVM_OPTS}}"
```

- 已设置且非空：原样使用（整段替换，不是追加）。只想加 GC/诊断参数时，必须把堆参数一并写进 `JVM_OPTS`
- 未设置或为空：回退到脚本默认值 `-Xms512M -Xmx2048M`（空字符串会盖掉镜像 `ENV`，容器里也会落到这组物理机堆参数）
- 启动/重启命令会打印实际生效值

不要再设置 `-XX:PermSize` / `-XX:MaxPermSize`：PermGen 在 JDK 8 已移除，HotSpot 会忽略并告警。也不在默认值里加 `-XX:MaxMetaspaceSize`，避免给原先无 Metaspace 上限的进程引入新的 OOM。

## 部署默认值

| 场景 | 默认 `JVM_OPTS` | 覆盖方式 |
| --- | --- | --- |
| 物理机 / 虚拟机 | `-Xms512M -Xmx2048M` | `export JVM_OPTS=...` 或写入 `/etc/profile.d/dinky_env` |
| Docker 镜像 | `-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0` | `docker run -e JVM_OPTS=...` |
| Helm | 使用镜像默认；未设置则不注入该环境变量 | `spec.extraEnv.jvmOpts` |

容器场景不要再对 `auto.sh` 做 `sed` 替换。镜像用 `ENV JVM_OPTS` 提供默认值，运行时覆盖即可。

## 约束

- `deploy/docker/.env` 会同时注入 Dinky 与 Flink JM/TM，不要在该文件里设置 `JVM_OPTS`。
- docker-compose 的 dinky 服务不要写空的 `environment.JVM_OPTS`：空字符串会覆盖镜像 `ENV`，脚本会回退到物理机堆参数。
- 使用 `MaxRAMPercentage` 时必须给容器/Pod 设置 memory limit，否则百分比按宿主机可见内存计算，可能过大。
- 容器默认不设 `InitialRAMPercentage`，避免启动时按 70% 提交堆，把 Metaspace/线程/native 挤出 cgroup。
- `java ${JVM_OPTS}` 按空格拆分参数，参数值本身不要含未转义空格。
