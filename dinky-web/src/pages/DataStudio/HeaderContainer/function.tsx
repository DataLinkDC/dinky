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

import {HomeOutlined} from "@ant-design/icons";
import React from "react";

/**
 * @description: 生成面包屑
 * @type {({title: JSX.Element} | {title: string})[]}
 */
export const buildBreadcrumbItems = (breadcrumb :string) => {
    const items = [
        {title: <HomeOutlined/>,}
    ];

    // 如果有 activeBreadcrumbTitle, 则切割 activeBreadcrumbTitle, 生成面包屑数组, 并映射
    const activeBreadcrumbTitleList = Array.from(breadcrumb.split("/") ).map(title => ( {title: <>{title}</>}));
    items.push(...activeBreadcrumbTitleList);
    return items;
};