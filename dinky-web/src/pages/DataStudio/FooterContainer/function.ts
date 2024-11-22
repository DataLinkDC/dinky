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

import { l } from '@/utils/intl';

export function formatDate(inputDate: string) {
  const now = new Date();
  const then = new Date(inputDate);

  // 计算时间差
  const diff = (now - then) / 1000; // 转换为秒
  const diffMinutes = Math.floor(diff / 60); // 转换为分钟
  const diffHours = Math.floor(diff / 3600); // 转换为小时

  if (diff < 60) {
    // 如果小于1分钟，显示“刚刚”
    return l('pages.datastudio.label.lastUpdateJust');
  } else if (diff < 3600 && now.toDateString() === then.toDateString()) {
    // 如果小于1小时且是同一天，显示几分钟前
    return `${diffMinutes}${l('pages.datastudio.label.lastUpdateMinutesAgo')}`;
  } else if (diff < 86400 && now.toDateString() === then.toDateString()) {
    // 如果小于1天且是同一天，显示几小时前
    return `${diffHours}${l('pages.datastudio.label.lastUpdateHoursAgo')}`;
  } else {
    // 否则显示日期和时间
    const options = {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    } as Intl.DateTimeFormatOptions;
    return then.toLocaleString('zh-CN', options).replace(/\//g, '-').slice(0, -3);
  }
}
