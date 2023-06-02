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

import React, {useRef} from 'react';
import {ProList} from '@ant-design/pro-components';
import {BaseConfigProperties} from '@/types/SettingCenter/data';
import {Descriptions, Input, Space, Switch} from 'antd';
import {l} from '@/utils/intl';
import {BackwardOutlined, SaveTwoTone, SettingTwoTone} from '@ant-design/icons';
import {EditBtn} from '@/components/CallBackButton/EditBtn';
import {SWITCH_OPTIONS} from '@/services/constants';
import {ActionType} from '@ant-design/pro-table';
import {ProListMetas, ProListProps} from '@ant-design/pro-list';

type GeneralConfigProps = {
  data: BaseConfigProperties[];
  tag: React.ReactNode;
  onSave: (data: BaseConfigProperties) => void;
  loading: boolean;
}

const GeneralConfig: React.FC<GeneralConfigProps> = (props) => {

  const {data, tag, onSave: handleSubmit, loading} = props;


  const actionRef = useRef<ActionType>();

  const handleSave = (data: BaseConfigProperties) => {
    handleSubmit(data);
    actionRef.current?.reload();
  };

  /**
   * render actions for each entity in the list
   * @param action startEditable
   * @param entity entity
   */
  const renderActions = (action: any, entity: BaseConfigProperties) => {
    return entity.frontType === 'boolean' ? [] : [
      <EditBtn
        key="edit"
        onClick={() => {
          action.startEditable(entity.key);
        }}
      />
    ];
  };

  /**
   * render title for each entity in the list
   * @param entity
   */
  const renderTitle = (entity: BaseConfigProperties) => {
    return <>
      <Descriptions.Item>
        {l(`sys.${entity.key}`)}
      </Descriptions.Item>
      <Space style={{marginLeft: 15}} size={0}>
        {tag}
      </Space>
    </>;
  };

  /**
   * render value for each entity in the list
   * @param entity
   */
  const renderValue = (entity: BaseConfigProperties) => {
    return <>
      {
        entity.frontType === 'boolean' ?
          <Switch
            {...SWITCH_OPTIONS()}
            style={{width: '4vw'}}
            disabled={false}
            checked={entity.value}
            onChange={(checked) => handleSubmit({...entity, value: checked})}
          /> :
          <Input style={{width: '30vw'}} disabled value={entity.value}/>
      }
    </>;
  };


  const metasRestProps: ProListMetas = {
    title: {
      editable: false,
      render: (dom: any, entity: BaseConfigProperties) => renderTitle(entity),
    },
    avatar: {
      editable: false,
      render: () => <SettingTwoTone/>,
    },
    description: {
      editable: false,
      render: (dom: any, entity: BaseConfigProperties) => <>{l(`sys.${entity.key}.note`)}</>
    },
    content: {
      dataIndex: 'value',
      render: (dom: any, entity: BaseConfigProperties) => renderValue(entity),
    },
    actions: {
      render: (text: any, row: BaseConfigProperties, index: number, action: any) => renderActions(action, row),
    },
  };


  /**
   * rest props for ProList
   */
  const restProps: ProListProps = {
    rowKey: 'key',
    style: {margin: 0},
    loading: loading,
    actionRef: actionRef,
    size: 'small',
    dataSource: data,
    showActions: 'hover',
    metas: {...metasRestProps},
    editable: {
      saveText: <><SaveTwoTone title={l('button.save')}/></>,
      cancelText: <><BackwardOutlined title={l('button.cancel')}/></>,
      actionRender: (row, config, dom) => row.frontType === 'boolean' ? [] : [dom.save, dom.cancel],
      onSave: async (key, record) => handleSave(record),
    }
  };

  /**
   * render list
   */
  return <>
    <ProList<BaseConfigProperties> {...restProps} />
  </>;
};

export default GeneralConfig;
