#!/bin/bash

# Check whether the number of parameters is correct
if [ $# -ne 2 ]; then
    echo "Please enter the parameters in the correct format, the format is: $0 yaml_file_path key"
    exit 1
fi


yaml_file_path="$1"
key="$2"
# Split keys into arrays according to . (used to handle hierarchical keys)
IFS='.' read -ra key_parts <<< "$key"

# Used to store the finally found value
value=""

# Process the file first and remove comment lines (assuming the comment starts with #) and blank lines
temp_file=$(mktemp)
grep -Ev '^(#|$)' "$yaml_file_path" > "$temp_file"

# Start looking for values by levels
current_data=$(cat "$temp_file")
for part in "${key_parts[@]}"; do
    found=false
    while IFS= read -r line; do

        if [[ $line =~ ^$part: ]]; then
            # If it is the last key, extract the value
            if [ "$part" == "${key_parts[${#key_parts[@]}-1]}" ]; then
                value=$(echo "$line" | sed 's/.*: //')
                found=true
                break
            else
                # If it is not the last key, get the data range of the next level
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

# Delete temporary files
rm -f "$temp_file"

# Output the found value to standard output for easy external acquisition
if [ -n "$value" ]; then
    echo "$value"
else
    echo ""
fi