/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import {l} from "@/utils/intl"

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
  if (((second_time / 1000) % 60) < 1) {
    return second_time + l('global.time.millisecond');
  }
  return parseSecondStr(second_time / 1000);
}

export function parseSecondStr(second_time: number) {
  second_time = Math.floor(second_time);
  let time = second_time + l( 'global.time.second');
  if (second_time > 60) {
    let second = second_time % 60;
    let min = Math.floor(second_time / 60);
    time = min + l( 'global.time.minute') + second + l( 'global.time.second');
    if (min > 60) {
      min = Math.floor(second_time / 60) % 60;
      let hour = Math.floor(Math.floor(second_time / 60) / 60);
      time = hour + l( 'global.time.hour') + min + l( 'global.time.minute') + second + l( 'global.time.second');
      if (hour > 24) {
        hour = Math.floor(Math.floor(second_time / 60) / 60) % 24;
        let day = Math.floor(Math.floor(Math.floor(second_time / 60) / 60) / 24);
        time = day + l( 'global.time.day') + hour + l( 'global.time.hour') + min + l( 'global.time.minute') + second + l( 'global.time.second');
      }
    }
  }
  return time;
}
