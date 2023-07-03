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

import {DeleteOutlined, EditOutlined, PlusCircleOutlined, UndoOutlined, UploadOutlined} from "@ant-design/icons";
import {l} from "@/utils/intl";
import React from "react";


/**
 *  the right context menu
 * @param {boolean} isDisabled - is disabled or not , if disabled , the menu will be disabled too
 * @returns {[{icon: JSX.Element, disabled: boolean, label: string, key: string}, {icon: JSX.Element, disabled: boolean, label: string, key: string}, {icon: JSX.Element, disabled: boolean, label: string, key: string}]}
 * @constructor
 */
export const RIGHT_CONTEXT_MENU = (isDisabled = false) => [
  {
    key: 'createFolder',
    icon: <PlusCircleOutlined/>,
    label: l('right.menu.createFolder'),
    disabled: isDisabled,
  },
  {
    key: 'upload',
    icon: <UploadOutlined/>,
    label: l('button.upload'),
    disabled: isDisabled,
  },
  {
    key: 'delete',
    icon: <DeleteOutlined/>,
    label: l('right.menu.delete'),
    disabled: isDisabled,
  },
  {
    key: 'rename',
    icon: <EditOutlined/>,
    label: l('right.menu.rename'),
    disabled: isDisabled,
  },{
    key: 'refresh',
    icon: <UndoOutlined />,
    label: l('right.menu.refresh'),
    disabled: isDisabled,
  },
]
