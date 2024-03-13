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

import { PermissionConstants } from '@/types/Public/constants';
import { l } from '@/utils/intl';
import {
  DeleteOutlined,
  EditOutlined,
  PlusCircleOutlined,
  UploadOutlined
} from '@ant-design/icons';

/**
 *  the right context menu
 * @param {boolean} isDisabled - is disabled or not , if disabled , the menu will be disabled too
 * @returns {[{icon: JSX.Element, disabled: boolean, label: string, key: string}, {icon: JSX.Element, disabled: boolean, label: string, key: string}, {icon: JSX.Element, disabled: boolean, label: string, key: string}]}
 * @constructor
 */
export const RIGHT_CONTEXT_FILE_MENU = [
  {
    key: 'delete',
    icon: <DeleteOutlined />,
    label: l('right.menu.delete'),
    path: PermissionConstants.REGISTRATION_RESOURCE_DELETE
  },
  {
    key: 'rename',
    icon: <EditOutlined />,
    label: l('right.menu.rename'),
    path: PermissionConstants.REGISTRATION_RESOURCE_RENAME
  }
];
export const RIGHT_CONTEXT_FOLDER_MENU = [
  {
    key: 'createFolder',
    icon: <PlusCircleOutlined />,
    label: l('right.menu.createFolder'),
    path: PermissionConstants.REGISTRATION_RESOURCE_ADD_FOLDER
  },
  {
    key: 'upload',
    icon: <UploadOutlined />,
    label: l('button.upload'),
    path: PermissionConstants.REGISTRATION_RESOURCE_UPLOAD
  },
  {
    key: 'delete',
    icon: <DeleteOutlined />,
    label: l('right.menu.delete'),
    path: PermissionConstants.REGISTRATION_RESOURCE_DELETE
  },
  {
    key: 'rename',
    icon: <EditOutlined />,
    label: l('right.menu.rename'),
    path: PermissionConstants.REGISTRATION_RESOURCE_RENAME
  }
];
