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


export const DATE_RANGE_OPTIONS =(disable:boolean)=> [
    {
        value: '60s',
        label: '60秒',
        disable:!disable
    },
    {
        value: '5min',
        label: '5分钟',
        disable:!disable
    },
    {
        value: '10min',
        label: '10分钟',
        disable:!disable
    },
    {
        value: '1h',
        label: '1小时',
        disable:!disable
    },
    {
        value: '2h',
        label: '2小时',
        disable:!disable
    },
    {
        value: '5h',
        label: '5小时',
        disable:!disable
    },
    {
        value: 'custom',
        label: '自定义',
        disable:disable
    },
]
