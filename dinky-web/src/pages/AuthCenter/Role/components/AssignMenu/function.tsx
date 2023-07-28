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

import {Menu} from "@/types/RegCenter/data";

export const buildMenuTreeData = (data: Menu[], searchValue = '') => {
    return data.map((item: any) => {
        return {
            isLeaf: false,
            name: item.name,
            parentId: item.parentId,
            icon: item.icon,
            content: item.name,
            path: item.path,
            title: item.name,
            key: item.id,
            children: item.childrens
                // filter table by search value and map table to tree node
                .filter((sub: any) => (sub.name.indexOf(searchValue) > -1))
                .map((sub: any) => {
                    return {
                        isLeaf: true,
                        name: sub.name,
                        parentId: item.parentId,
                        icon: item.icon,
                        content: sub.name,
                        path: item.path
                    }
                })
        }})
}