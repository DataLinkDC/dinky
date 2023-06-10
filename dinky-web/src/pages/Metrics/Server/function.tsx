/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */


const getSubMinTime = (currentTime : Date ,minutes: number) => {
    const pastTime = new Date(currentTime.getTime() - minutes * 60000);
    return pastTime;
};

const getSubMinutesEndTime = (currentTime : Date ,minutes: number) => {
    const pastTime = new Date(
        currentTime.getFullYear(),
        currentTime.getMonth(),
        currentTime.getDate(),
        currentTime.getHours(),
        59,
        59,59,
    );
    return pastTime;
};

const getSubDateTime = (currentTime : Date ,day: number) => {

    const pastTime = new Date(
        currentTime.getFullYear(),
        currentTime.getMonth(),
        currentTime.getDate() - day,
        0, 0, 0, 0
    );
    return pastTime;
};
const getSubDateEndTime = (currentTime : Date ,day: number) => {
    const pastTime = new Date(
        currentTime.getFullYear(),
        currentTime.getMonth(),
        currentTime.getDate() - day,
        23, 59, 59, 59
    );
    return pastTime;
};

const getSubMon = (currentTime : Date ,month: number) => {
    const pastTime = new Date(
        currentTime.getFullYear(),
        currentTime.getMonth() - month,
        1,
        0, 0, 0, 0
    );
    return pastTime;
};

const getSubMonEnd = (currentTime : Date ,month: number) => {
    const pastTime = new Date(
        currentTime.getFullYear(),
        currentTime.getMonth() - month,
        currentTime.getDay(),
        23, 59, 59, 59
    );
    return pastTime;
};