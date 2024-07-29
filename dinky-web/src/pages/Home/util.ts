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

/**
 * 根据前缀和个数获取国际化提示语 | Get internationalization tips based on prefix and count
 */
function getTips(prefix: string, count: number): string[] {
  const tips: string[] = [];
  for (let i = 1; i <= count; i++) {
    tips.push(l(`${prefix}.${i}`));
  }
  return tips;
}

const greetings: Record<string, string[]> = {
  morning: getTips('home.header.tips.morning', 10),
  forenoon: getTips('home.header.tips.forenoon', 6),
  noon: getTips('home.header.tips.noon', 5),
  afternoon: getTips('home.header.tips.afternoon', 5),
  evening: getTips('home.header.tips.evening', 5),
  lateNight: getTips('home.header.tips.lateNight', 5)
};
// 获取当前时间并判断时间段
const getTimeSegment = () => {
  const now = new Date();
  const hour = now.getHours();
  if (hour < 6) return 'lateNight'; // 深夜
  if (hour < 9) return 'morning'; // 早上
  if (hour < 12) return 'forenoon'; // 上午
  if (hour < 14) return 'noon'; // 中午
  if (hour < 17) return 'afternoon'; // 下午
  if (hour < 19) return 'evening'; // 傍晚
  return 'lateNight'; // 深夜
};

// 随机选择一条关怀话语
export const getRandomGreeting = (user: string) => {
  let greetingsArray: string[] = greetings[getTimeSegment()] ?? [];
  const randomIndex = Math.floor(Math.random() * greetingsArray.length);
  const txt = greetingsArray[randomIndex];
  return txt.replace('{user}', user);
};

export const getCurrentDateStr = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};
