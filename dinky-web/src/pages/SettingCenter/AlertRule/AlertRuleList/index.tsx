/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { CreateBtn } from '@/components/CallBackButton/CreateBtn';
import { EditBtn } from '@/components/CallBackButton/EditBtn';
import { EnableSwitchBtn } from '@/components/CallBackButton/EnableSwitchBtn';
import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import RuleEditForm from '@/pages/SettingCenter/AlertRule/AlertRuleList/RuleEditForm';
import { queryList } from '@/services/api';
import { handleAddOrUpdate, handleRemoveById } from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC, STATUS_ENUM, STATUS_MAPPING } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { AlertRule } from '@/types/SettingCenter/data';
import { InitAlertRuleState } from '@/types/SettingCenter/init.d';
import { AlertRuleListState } from '@/types/SettingCenter/state';
import { l } from '@/utils/intl';
import { ActionType, ProTable } from '@ant-design/pro-components';
import { ProColumns } from '@ant-design/pro-table';
import React, { useRef, useState } from 'react';
import {RuleType} from "@/pages/SettingCenter/AlertRule/constants";

const AlertRuleList: React.FC = () => {
  const [ruleState, setRuleState] = useState<AlertRuleListState>(InitAlertRuleState);
  const actionRef = useRef<ActionType>(); // table action

  const editClick = (item: AlertRule) => {
    setRuleState((prevState) => ({
      ...prevState,
      editOpen: !prevState.editOpen,
      value: item
    }));
  };

  const handleCleanState = () => {
    setRuleState((prevState) => ({
      ...prevState,
      value: {},
      addedOpen: false,
      editOpen: false
    }));
  };

  async function handleSubmit(rule: AlertRule) {
    await handleAddOrUpdate(API_CONSTANTS.ALERT_RULE, rule);
    actionRef.current?.reload?.();
    handleCleanState();
  }

  const initData = async (params: any, sorter: any, filter: any) => {
    const result = await queryList(API_CONSTANTS.ALERT_RULE_LIST, {
      ...params,
      sorter,
      filter
    });
    const data = result.data.map((t: AlertRule) => {
      t.rule = JSON.parse(t.rule);
      return t;
    });
    return { data: data };
  };

  const columns: ProColumns<AlertRule>[] = [
    {
      title: 'id',
      dataIndex: 'id'
    },
    {
      title: l('sys.alert.rule.name'),
      dataIndex: 'name'
    },
    {
      title: l('sys.alert.rule.ruleType'),
      dataIndex: 'ruleType'
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInSearch: true,
      render: (_: any, record: AlertRule) => {
        return (
          <EnableSwitchBtn
            key={`${record.id}_enable`}
            record={record}
            onChange={() => {
              record.enabled = !record.enabled;
              record.rule = JSON.stringify(record.rule);
              handleSubmit(record);
            }}
          />
        );
      },
      filters: STATUS_MAPPING(),
      filterMultiple: false,
      valueEnum: STATUS_ENUM()
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      valueType: 'dateTime'
    },
    {
      title: l('global.table.updateTime'),
      dataIndex: 'updateTime',
      valueType: 'dateTime'
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      render: (_text: any, record: AlertRule) => [
        <EditBtn key={`${record.id}_edit`} onClick={() => editClick(record)} />,
        record.ruleType != RuleType.SYSTEM && (
          <PopconfirmDeleteBtn
            key={`${record.id}_delete`}
            onClick={async () => await handleRemoveById(API_CONSTANTS.ALERT_RULE, record.id)}
            description={l('user.status')}
          />
        )
      ]
    }
  ];

  return (
    <>
      <ProTable<AlertRule>
        actionRef={actionRef}
        headerTitle={false}
        {...PROTABLE_OPTIONS_PUBLIC}
        toolBarRender={() => [
          <CreateBtn
            key={'CreateRule'}
            onClick={() => setRuleState((prevState) => ({ ...prevState, addedOpen: true }))}
          />
        ]}
        rowKey='id'
        size={'small'}
        search={false}
        columns={columns}
        request={initData}
      />

      <RuleEditForm
        onSubmit={handleSubmit}
        onCancel={handleCleanState}
        modalVisible={ruleState.addedOpen}
        values={{}}
      />

      {ruleState.value && Object.keys(ruleState.value).length > 0 && (
        <RuleEditForm
          onSubmit={handleSubmit}
          onCancel={handleCleanState}
          modalVisible={ruleState.editOpen}
          values={ruleState.value}
        />
      )}
    </>
  );
};

export default AlertRuleList;
