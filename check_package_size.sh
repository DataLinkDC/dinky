cd build

total_text=$(ls -ls | grep total)
total_size=${total_text/'total '/''}
echo `ls -lh`

if [[ ${total_size} -gt 256000 ]]; then
  echo "尺寸为：$total_size，大于250mb，编译失败"
  exit 1
else
  exit 0
fi
