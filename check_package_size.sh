cd build

total_text=$(ls -ls | grep total)
total_size=${total_text/'total '/''}
echo `ls -lh`

if [[ ${total_size} -gt 204800 ]]; then
  echo "尺寸为：$total_size，大于200mb，编译失败"
  exit 1
else
  exit 0
fi
