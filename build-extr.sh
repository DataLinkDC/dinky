#!/usr/bin/env bash
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# 执行构建命令
# echo "执行命令: $CMD"
# $CMD
#
# 使用说明
#
# 1. 默认构建:直接运行脚本，将使用默认的 jdk11 和 flink-1.18 版本。
#./build.sh
# 2. 指定 JDK 版本:使用 -j 参数来指定 JDK 版本，例如使用 jdk8:
#./build.sh -j jdk8
# 3. 指定 Flink 版本:使用 -f 参数来指定 Flink 版本，例如使用 flink-1.14:
#./build.sh -f flink-1.14
# 4. 同时指定 JDK 和 Flink 版本:你可以同时指定 JDK 和 Flink 版本:
#./build.sh -j jdk8 -f flink-1.15
# 默认参数
JDK_VERSION="jdk-11"
FLINK_VERSION="flink-1.18"

# 帮助信息
usage() {
  echo "Usage: $0 [-j <jdk_version>] [-f <flink_version>]"
  echo "  -j <jdk_version>    指定 JDK 版本 (默认: jdk11, 可选: jdk8)"
  echo "  -f <flink_version>  指定 Flink 版本 (默认: flink-1.18, 可选: flink-1.14, flink-1.15)"
  exit 1
}

# 解析命令行参数
while getopts "j:f:" opt; do
  case ${opt} in
    j )
      JDK_VERSION=$OPTARG
      ;;
    f )
      FLINK_VERSION=$OPTARG
      ;;
    * )
      usage
      ;;
  esac
done

# 构建命令
CMD="./mvnw clean package -Dmaven.test.skip=true -P prod,${JDK_VERSION},flink-single-version,aliyun,${FLINK_VERSION},web"

# 执行构建命令
echo "执行命令: $CMD"
$CMD
