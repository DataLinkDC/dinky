#!/bin/bash

# 检查参数个数是否正确
if [ $# -ne 2 ]; then
    echo "请按正确格式输入参数，格式为: $0 yaml_file_path key"
    exit 1
fi

#yaml_file_path="/Users/zhumingye/idea_works/dinky/dinky-admin/src/main/resources/application.yml"
#key="spring.port"

yaml_file_path="$1"
key="$2"
# 将键按照.分割为数组（用于处理层级结构的键）
IFS='.' read -ra key_parts <<< "$key"

# 用于存储最终找到的值
value=""

# 先处理文件，去除注释行（假设注释以#开头）和空白行
temp_file=$(mktemp)
grep -Ev '^(#|$)' "$yaml_file_path" > "$temp_file"

# 开始按层级查找值
current_data=$(cat "$temp_file")
for part in "${key_parts[@]}"; do
    found=false
    while IFS= read -r line; do

        if [[ $line =~ ^$part: ]]; then
            # 如果是最后一个键，提取值
            if [ "$part" == "${key_parts[${#key_parts[@]}-1]}" ]; then
                value=$(echo "$line" | sed 's/.*: //')
                found=true
                break
            else
                # 如果不是最后一个键，获取下一层级的数据范围
                start_line_num=$(grep -n "$line" "$temp_file" | cut -d: -f1)
                end_line_num=$(awk -v start="$start_line_num" '$0 ~ /^[a-zA-Z]/ && NR > start {print NR - 1; exit}' "$temp_file")
                if [ -z "$end_line_num" ]; then
                    end_line_num=$(wc -l < "$temp_file")
                fi

                current_data=$(sed -n "$((start_line_num + 1)),$((end_line_num))p" "$temp_file")
                current_data=$(echo "$current_data" | sed 's/^[[:space:]]*//')
                found=true
                break
            fi
        fi
    done <<< "$current_data"
    if [ "$found" = false ]; then
        value=""
        break
    fi
done

# 删除临时文件
rm -f "$temp_file"

# 将找到的值输出到标准输出，方便外部获取
if [ -n "$value" ]; then
    echo "$value"
else
    echo ""
fi