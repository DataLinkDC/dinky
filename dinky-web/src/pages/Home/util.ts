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

const greetings = {
  morning: [
    '早上好,{user}！新的一天，新的代码，新的挑战。',
    '早安！{user}！早晨的阳光带来了新的开始，加油！。',
    '早上好,{user}，程序员！愿你的bug像早晨的雾气一样消散。',
    '早安，{user}，新的一天，新的创意，期待你的灵感爆发。',
    '早上好,{user}，早晨的咖啡已经准备好，是时候开始工作了。',
    '早安，{user}，愿你的早晨像一杯热茶，温暖而舒适。',
    '早上好,{user}，愿你的代码像早晨的露水一样清新。',
    '早安，{user}，记得微笑，它会让你的一天更加美好。',
    '早上好,{user}，保持积极，美好的事情即将发生。',
    '早安，{user}，愿你的编程之旅充满发现和创新。'
  ],
  forenoon: [
    '上午好,{user}，愿你的代码像上午的阳光一样明亮。',
    '上午好,{user}，早上的代码写累了吗？休息一下，再继续加油。',
    '上午好,{user}，上午的阳光正好，照亮你的工作台。',
    '上午好,{user}，享受上午的工作，每项任务都值得全力以赴。',
    '上午好,{user}，愿你的代码像上午的咖啡一样提神。',
    '上午好,{user}，愿你的代码像上午的天空一样广阔。'
  ],
  noon: [
    '中午好,{user}，午餐时间，给自己一个休息和充电的机会。',
    '中午好,{user}，中午的阳光提醒我们，是时候放慢脚步，享受片刻宁静。',
    '中午好,{user}，中午的短暂休息，可以让你的下午更加精力充沛.',
    '中午好,{user}，中午的阳光，是一天中温暖的拥抱。',
    '中午好,{user}，一顿丰盛的午餐，是对自己辛勤工作的最好奖赏。'
  ],
  afternoon: [
    '下午好,{user}，下午的工作开始了，保持热情和专注。',
    '下午好,{user}，下午的阳光温柔，适合思考或冥想，找到内心的平静。',
    '下午好,{user}，下午的微风，带来清新的空气，愿你的心情也如此清新。',
    '下午好,{user}，忙碌的下午，也别忘了给自己一点甜头哦！(✿✪‿✪｡)',
    '下午好,{user}，下午的微风，愿你的心情像它一样轻松。(✧∀✧)'
  ],
  evening: [
    '傍晚好,{user}，傍晚好，一天的忙碌结束了，好好放松一下吧！(✿✪‿✪｡)',
    '傍晚好,{user}，傍晚的晚霞，愿你的心情像它一样绚烂。(✧∇✧)',
    '傍晚好,{user}，享受傍晚的宁静，让一天的忙碌慢慢沉淀。(✧◡◡✧)',
    '傍晚好,{user}，傍晚的天空，星星开始闪烁，愿你的梦想也随之点亮。(✧✧✧)',
    '傍晚好,{user}，愿你的代码像傍晚的星光一样璀璨。'
  ],
  lateNight: [
    '深夜好,{user}，深夜的宁静，愿你有一个平静美好的夜晚。(✿✉✿)',
    '深夜好,{user}，深夜的星空，为你的思考带来无限灵感。',
    '深夜好,{user}，深夜的星空，提醒我们宇宙的广阔和思维的可能性。',
    '深夜好,{user}，深夜的工作，愿你的专注和坚持带来成果。',
    '深夜好,{user}，愿你的代码像深夜的思考一样深邃。'
  ]
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
  let greetingsArray: string[];
  greetingsArray = greetings[getTimeSegment()];
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
