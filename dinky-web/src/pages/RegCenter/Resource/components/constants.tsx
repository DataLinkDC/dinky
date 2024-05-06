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
  CopyOutlined,
  DeleteOutlined,
  EditOutlined,
  PlusCircleOutlined,
  UploadOutlined
} from '@ant-design/icons';

export enum ResourceRightMenuKey {
  COPY_TO_ADD_CUSTOM_JAR = 'copy_to_add_custom_jar',
  COPY_TO_ADD_JAR = 'copy_to_add_jar',
  COPY_TO_ADD_FILE = 'copy_to_add_file',
  COPY_TO_ADD_RS_PATH = 'copy_to_add_rs_path',
  DELETE = 'delete',
  RENAME = 'rename',
  CREATE_FOLDER = 'createFolder',
  UPLOAD = 'upload'
}

/**
 *  the right context menu
 * @param {boolean} isDisabled - is disabled or not , if disabled , the menu will be disabled too
 * @returns {[{icon: JSX.Element, disabled: boolean, label: string, key: string}, {icon: JSX.Element, disabled: boolean, label: string, key: string}, {icon: JSX.Element, disabled: boolean, label: string, key: string}]}
 * @constructor
 */
export const RIGHT_CONTEXT_FILE_MENU = [
  {
    key: ResourceRightMenuKey.COPY_TO_ADD_CUSTOM_JAR,
    icon: <CopyOutlined />,
    label: l('rc.resource.copy_to_add_custom_jar'),
    path: PermissionConstants.REGISTRATION_RESOURCE_DELETE
  },
  {
    key: ResourceRightMenuKey.COPY_TO_ADD_JAR,
    icon: <CopyOutlined />,
    label: l('rc.resource.copy_to_add_jar'),
    path: PermissionConstants.REGISTRATION_RESOURCE_DELETE
  },
  {
    key: ResourceRightMenuKey.COPY_TO_ADD_FILE,
    icon: <CopyOutlined />,
    label: l('rc.resource.copy_to_add_file'),
    path: PermissionConstants.REGISTRATION_RESOURCE_DELETE
  },
  {
    key: ResourceRightMenuKey.COPY_TO_ADD_RS_PATH,
    icon: <CopyOutlined />,
    label: l('rc.resource.copy_to_add_rs_path'),
    path: PermissionConstants.REGISTRATION_RESOURCE_DELETE
  },
  {
    key: ResourceRightMenuKey.DELETE,
    icon: <DeleteOutlined />,
    label: l('right.menu.delete'),
    path: PermissionConstants.REGISTRATION_RESOURCE_DELETE
  },
  {
    key: ResourceRightMenuKey.RENAME,
    icon: <EditOutlined />,
    label: l('right.menu.rename'),
    path: PermissionConstants.REGISTRATION_RESOURCE_RENAME
  }
];
export const RIGHT_CONTEXT_FOLDER_MENU = [
  {
    key: ResourceRightMenuKey.CREATE_FOLDER,
    icon: <PlusCircleOutlined />,
    label: l('right.menu.createFolder'),
    path: PermissionConstants.REGISTRATION_RESOURCE_ADD_FOLDER
  },
  {
    key: ResourceRightMenuKey.UPLOAD,
    icon: <UploadOutlined />,
    label: l('button.upload'),
    path: PermissionConstants.REGISTRATION_RESOURCE_UPLOAD
  },
  {
    key: ResourceRightMenuKey.DELETE,
    icon: <DeleteOutlined />,
    label: l('right.menu.delete'),
    path: PermissionConstants.REGISTRATION_RESOURCE_DELETE
  },
  {
    key: ResourceRightMenuKey.RENAME,
    icon: <EditOutlined />,
    label: l('right.menu.rename'),
    path: PermissionConstants.REGISTRATION_RESOURCE_RENAME
  }
];
