ARG DINKY_VERSION
ARG FLINK_VERSION
FROM dinkydocker/dinky-standalone-server:${DINKY_VERSION}-flink${FLINK_VERSION}
ARG DINKY_VERSION
ARG FLINK_VERSION
ENV FLINK_VERSION=${FLINK_VERSION}
ENV DINKY_VERSION=${DINKY_VERSION}

RUN pwd && ls -l

# 创建一个临时目录来解压文件
WORKDIR /tmp

# 复制tar.gz文件到临时目录
COPY ./dinky-release-1.18-1.1.0.tar.gz .

# 解压文件，移动到/opt/dinky，然后清理
RUN tar -xzf dinky-release-${FLINK_VERSION}-${DINKY_VERSION}.tar.gz && \
    rm -rf /opt/dinky && \
    mv dinky-release-* /opt/dinky && \
    rm dinky-release-${FLINK_VERSION}-${DINKY_VERSION}.tar.gz

# 设置工作目录为/opt/dinky
WORKDIR /opt/dinky