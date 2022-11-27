#!/bin/bash
# 使用单行命令，避免报错
mkdir logs && touch logs/dlink.log && sh auto.sh start && tail -f -n200 /opt/dinky-release/logs/dlink.log