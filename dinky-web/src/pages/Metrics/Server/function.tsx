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

export const getSubMinTime = (currentTime: Date, minutes: number) => {
  const pastTime = new Date(currentTime.getTime() - minutes * 60000);
  return pastTime;
};

/**
 * 获取当前时间的前几天 | get current time before several days
 * @param {Date} currentTime | current time
 * @param {number} day sub days
 * @param {boolean} isStart | is start time , if true , the time is 00:00:00.000    if false , the time is 23:59:59.999
 * @returns {Date} | return the time before several days
 */
export const getSubDateTime = (currentTime: Date, day: number, isStart: boolean) => {
  const pastTime = new Date(
    currentTime.getFullYear(),
    currentTime.getMonth(),
    currentTime.getDate() - day,
    isStart ? 0 : 23,
    isStart ? 0 : 59,
    isStart ? 0 : 59,
    isStart ? 0 : 999
  );
  return pastTime;
};

/**
 * 获取当前月的第一天/最后一天 | get the first day / last day of current month
 * @param {Date} currentTime | current time
 * @param {boolean} isFirst | is first day of month, if true , the time is 00:00:00.000    if false , the time is 23:59:59.999
 * @returns {Date} | return the first day / last day of current month
 */
export const getDayOfMonth = (currentTime: Date, isFirst: boolean) => {
  const pastTime = new Date(
    currentTime.getFullYear(),
    currentTime.getMonth() + (isFirst ? 0 : 1),
    isFirst ? 1 : 0,
    isFirst ? 0 : 23,
    isFirst ? 0 : 59,
    isFirst ? 0 : 59,
    isFirst ? 0 : 999
  );
  return pastTime;
};
