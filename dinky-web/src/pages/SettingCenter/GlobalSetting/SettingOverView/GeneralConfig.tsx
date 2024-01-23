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

import { EditBtn } from '@/components/CallBackButton/EditBtn';
import { BackIcon } from '@/components/Icons/CustomIcons';
import { HasAuthority } from '@/hooks/useAccess';
import { ButtonFrontendType } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import { SWITCH_OPTIONS } from '@/services/constants';
import { BaseConfigProperties } from '@/types/SettingCenter/data';
import { l } from '@/utils/intl';
import { SaveTwoTone, SettingTwoTone } from '@ant-design/icons';
import { ProList } from '@ant-design/pro-components';
import { ProListMetas, ProListProps } from '@ant-design/pro-list';
import { ActionType } from '@ant-design/pro-table';
import { Descriptions, Input, Radio, RadioChangeEvent, Space, Switch } from 'antd';
import React, { useRef } from 'react';

type GeneralConfigProps = {
  data: BaseConfigProperties[];
  tag: React.ReactNode;
  onSave: (data: BaseConfigProperties) => void;
  loading: boolean;
  toolBarRender?: any;
  selectChanges?: (e: RadioChangeEvent) => void;
  auth: string;
};

const GeneralConfig: React.FC<GeneralConfigProps> = (props) => {
  const { data, tag, auth, onSave: handleSubmit, loading, toolBarRender, selectChanges } = props;

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
    return entity.frontType === ButtonFrontendType.BOOLEAN ||
      entity.frontType === ButtonFrontendType.OPTION
      ? []
      : [
          <EditBtn
            key='edit'
            disabled={!HasAuthority(auth)}
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
    return (
      <>
        <Descriptions.Item>{entity.name}</Descriptions.Item>
        <Space style={{ marginLeft: 15 }} size={0}>
          {tag}
        </Space>
      </>
    );
  };

  const renderValuesOfForm = (entity: BaseConfigProperties) => {
    if (entity.frontType === ButtonFrontendType.BOOLEAN) {
      return (
        <Switch
          {...SWITCH_OPTIONS()}
          style={{ width: '4vw' }}
          disabled={!HasAuthority(auth)}
          checked={entity.value}
          onChange={(checked) => handleSubmit({ ...entity, value: checked })}
        />
      );
    } else if (entity.frontType === ButtonFrontendType.OPTION) {
      return (
        <Radio.Group
          onChange={selectChanges}
          value={entity.value.toLowerCase()}
          disabled={!HasAuthority(auth)}
        >
          {entity.example.map((item: any) => (
            <Radio.Button key={item} value={item.toLowerCase()}>
              {item}
            </Radio.Button>
          ))}
        </Radio.Group>
      );
    } else {
      return <Input style={{ width: '30vw' }} disabled value={entity.value} />;
    }
  };

  const metasRestProps: ProListMetas = {
    title: {
      editable: false,
      render: (dom: any, entity: BaseConfigProperties) => renderTitle(entity)
    },
    avatar: {
      editable: false,
      render: () => <SettingTwoTone />
    },
    description: {
      editable: false,
      render: (dom: any, entity: BaseConfigProperties) => <>{entity.note}</>
    },
    content: {
      dataIndex: 'value',
      render: (dom: any, entity: BaseConfigProperties) => renderValuesOfForm(entity)
    },
    actions: {
      render: (text: any, row: BaseConfigProperties, index: number, action: any) =>
        renderActions(action, row)
    }
  };

  /**
   * rest props for ProList
   */
  const restProps: ProListProps<BaseConfigProperties> = {
    toolBarRender: toolBarRender,
    rowKey: 'key',
    style: { margin: 0 },
    loading: loading,
    actionRef: actionRef,
    size: 'small',
    dataSource: data,
    showActions: 'hover',
    metas: { ...metasRestProps },
    editable: {
      saveText: <SaveTwoTone title={l('button.save')} />,
      cancelText: <BackIcon title={l('button.back')} />,
      actionRender: (row, config, dom) =>
        row.frontType === ButtonFrontendType.BOOLEAN || row.frontType === ButtonFrontendType.OPTION
          ? []
          : [dom.save, dom.cancel],
      onSave: async (key, record) => handleSave(record)
    }
  };

  /**
   * render list
   */
  return <ProList<BaseConfigProperties> {...restProps} />;
};

export default GeneralConfig;
