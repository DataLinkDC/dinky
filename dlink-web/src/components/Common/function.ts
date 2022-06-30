export function parseByteStr(limit: number) {
  let size = "";
  if (limit < 0.1 * 1024) {                            //小于0.1KB，则转化成B
    size = limit.toFixed(2) + "B"
  } else if (limit < 0.1 * 1024 * 1024) {            //小于0.1MB，则转化成KB
    size = (limit / 1024).toFixed(2) + "KB"
  } else if (limit < 0.1 * 1024 * 1024 * 1024) {        //小于0.1GB，则转化成MB
    size = (limit / (1024 * 1024)).toFixed(2) + "MB"
  } else {                                            //其他转化成GB
    size = (limit / (1024 * 1024 * 1024)).toFixed(2) + "GB"
  }

  let sizeStr = size + "";                        //转成字符串
  let index = sizeStr.indexOf(".");                    //获取小数点处的索引
  let dou = sizeStr.substr(index + 1, 2)            //获取小数点后两位的值
  if (dou == "00") {                                //判断后两位是否为00，如果是则删除00
    return sizeStr.substring(0, index) + sizeStr.substr(index + 3, 2)
  }
  return size;
}

export function parseNumStr(num: number) {
  let c = (num.toString().indexOf('.') !== -1) ? num.toLocaleString() : num.toString().replace(/(\d)(?=(\d{3})+$)/g, '$1,');
  return c;
}

export function parseMilliSecondStr(second_time: number) {
  if(((second_time/1000) %60) < 1){
    return second_time + "毫秒";
  }
  return parseSecondStr(second_time/1000);
}

export function parseSecondStr(second_time: number) {
  second_time = Math.floor(second_time);
  let time = second_time + "秒";
  if (second_time > 60) {
    let second = second_time % 60;
    let min = Math.floor(second_time / 60);
    time = min + "分" + second + "秒";
    if (min > 60) {
      min = Math.floor(second_time / 60) % 60;
      let hour = Math.floor(Math.floor(second_time / 60) / 60);
      time = hour + "小时" + min + "分" + second + "秒";
      if (hour > 24) {
        hour = Math.floor(Math.floor(second_time / 60) / 60) % 24;
        let day = Math.floor(Math.floor(Math.floor(second_time / 60) / 60) / 24);
        time = day + "天" + hour + "小时" + min + "分" + second + "秒";
      }
    }
  }
  return time;
}
