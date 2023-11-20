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
import {
  CompassTwoTone,
  CopyrightTwoTone,
  CopyTwoTone,
  DeleteTwoTone,
  EditTwoTone,
  PlusCircleTwoTone,
  UpCircleTwoTone
} from '@ant-design/icons';
import { MenuItemType } from 'antd/es/menu/hooks/useItems';
import { DefaultOptionType } from 'antd/es/select';

/**
 * 目录级别:
 *   新建子目录
 *   新建作业
 *   删除(删除时检测是否有下级)
 *   重命名
 *   粘贴
 *   取消

 * @type {({icon: JSX.Element, label: string, key: string} | {icon: JSX.Element, label: string, key: string} | {icon: JSX.Element, label: string, key: string} | {icon: JSX.Element, label: string, key: string})[]}
 */
export const FOLDER_RIGHT_MENU = (disabled = false): MenuItemType[] => [
  {
    key: 'addSubFolder',
    icon: <PlusCircleTwoTone />,
    label: l('right.menu.createSubFolder')
  },
  {
    key: 'createTask',
    icon: <PlusCircleTwoTone />,
    label: l('right.menu.createTask')
  },
  {
    key: 'delete',
    icon: <DeleteTwoTone twoToneColor={'red'} />,
    label: l('button.delete')
  },
  {
    key: 'renameFolder',
    icon: <EditTwoTone />,
    label: l('right.menu.rename')
  },
  {
    key: 'paste',
    icon: <CompassTwoTone />,
    label: l('right.menu.paste'),
    disabled: !disabled
  }
];

/**
 * 作业级别:
 * 非目录(即任务)
 *    导出 json
 *    修改
 *    复制
 *    剪切
 *    删除
 * @type {({icon: JSX.Element, label: string, key: string} | {icon: JSX.Element, label: string, key: string} | {icon: JSX.Element, label: string, key: string} | {icon: JSX.Element, label: string, key: string})[]}
 */
export const JOB_RIGHT_MENU = (disabled = false): MenuItemType[] => [
  {
    key: 'edit',
    icon: <EditTwoTone />,
    label: l('button.edit')
  },
  {
    key: 'exportJson',
    icon: <UpCircleTwoTone />,
    label: l('right.menu.exportJson'),
    disabled: true // todo: 此功能暂时不实现 先禁用掉
  },
  {
    key: 'copy',
    icon: <CopyTwoTone />,
    label: l('right.menu.copy')
  },
  {
    key: 'cut',
    icon: <CopyrightTwoTone />,
    label: l('right.menu.cut'),
    disabled: disabled
  },
  {
    key: 'delete',
    icon: <DeleteTwoTone twoToneColor={'red'} />,
    label: l('button.delete')
  }
];

/**
 * 作业 方言类型
 * @type {({options: ({label: string, value: string} | {label: string, value: string} | {label: string, value: string} | {label: string, value: string})[], label: string} | {options: ({label: string, value: string} | {label: string, value: string} | {label: string, value: string} | {label: string, value: string} | {label: string, value: string} | {label: string, value: string} | {label: string, value: string} | {label: string, value: string} | {label: string, value: string} | {label: string, value: string} | {label: string, value: string})[], label: string} | {options: ({label: string, value: string} | {label: string, value: string} | {label: string, value: string})[], label: string})[]}
 */
export const JOB_TYPE: DefaultOptionType[] = [
  {
    label: 'Flink Job Type',
    options: [
      {
        value: 'FlinkSql',
        label: 'FlinkSql'
      },
      {
        value: 'FlinkJar',
        label: 'FlinkJar'
      },
      {
        value: 'FlinkSqlEnv',
        label: 'FlinkSqlEnv'
      }
    ]
  },
  {
    label: 'Jdbc Query Type',
    options: [
      {
        value: 'Mysql',
        label: 'Mysql'
      },
      {
        value: 'Oracle',
        label: 'Oracle'
      },
      {
        value: 'SqlServer',
        label: 'SqlServer'
      },
      {
        value: 'PostgreSql',
        label: 'PostgreSql'
      },
      {
        value: 'ClickHouse',
        label: 'ClickHouse'
      },
      {
        value: 'Doris',
        label: 'Doris'
      },
      {
        value: 'Hive',
        label: 'Hive'
      },
      {
        value: 'Phoenix',
        label: 'Phoenix'
      },
      {
        value: 'StarRocks',
        label: 'StarRocks'
      },
      {
        value: 'Presto',
        label: 'Presto'
      }
    ]
  },
  {
    label: 'Other Type',
    options: [
      {
        value: 'Java',
        label: 'Java'
      },
      {
        value: 'Scala',
        label: 'Scala'
      },
      {
        value: 'Python',
        label: 'Python'
      }
    ]
  }
];
