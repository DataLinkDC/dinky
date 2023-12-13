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
import { NormalDeleteBtn } from '@/components/CallBackButton/NormalDeleteBtn';
import { Authorized } from '@/hooks/useAccess';
import AlertTemplateForm from '@/pages/RegCenter/Alert/AlertTemplate/components/AlertTemplateForm';
import { handleAddOrUpdate,handleRemoveById } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { Alert } from '@/types/RegCenter/data';
import { InitAlertTemplateState } from '@/types/RegCenter/init.d';
import { AlertTemplateState } from '@/types/RegCenter/state';
import { l } from '@/utils/intl';
import { useRequest } from '@@/exports';
import { PlusOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import { Button,Card,List,Modal } from 'antd';
import { useState } from 'react';
import Markdown from 'react-markdown';

export default () => {
  const [alertTemplateState, setAlertTemplateState] =
    useState<AlertTemplateState>(InitAlertTemplateState);

  const { data, loading, run } = useRequest({ url: API_CONSTANTS.ALERT_TEMPLATE });

  /**
   * edit click callback
   * @param item
   */
  const editClick = (item: Alert.AlertTemplate) => {
    setAlertTemplateState((prevState) => ({
      ...prevState,
      editOpen: !prevState.editOpen,
      value: item
    }));
  };

  /**
   * handle delete alert template
   * @param id
   */
  const handleDeleteSubmit = async (id: number) => {
    Modal.confirm({
      title: l('rc.template.delete'),
      content: l('rc.template.deleteConfirm'),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        await handleRemoveById(API_CONSTANTS.ALERT_TEMPLATE, id);
        run();
      }
    });
  };

  /**
   * cancel callback
   */
  const handleCleanState = () => {
    setAlertTemplateState((prevState) => ({
      ...prevState,
      value: {},
      addedOpen: false,
      editOpen: false
    }));
  };

  /**
   * handle add alert instance
   */
  const handleSubmit = async (value: Alert.AlertTemplate) => {
    await handleAddOrUpdate(
      API_CONSTANTS.ALERT_TEMPLATE,
      value,
      () => {},
      () => handleCleanState()
    );
    run();
  };

  /**
   * Draw a template Card Action
   */
  const renderAlertTemplateActionButton = (item: Alert.AlertTemplate) => {
    return [
      <Authorized key={item.id} path='/registration/alert/template/edit'>
        <EditBtn key={`${item.id}_edit`} onClick={() => editClick(item)} />
      </Authorized>,
      <Authorized key={item.id} path='/registration/alert/template/delete'>
        <NormalDeleteBtn key={`${item.id}_delete`} onClick={() => handleDeleteSubmit(item.id)} />
      </Authorized>
    ];
  };

  /**
   * Draw a template Card
   */
  const renderTemplateCard = (item: Alert.AlertTemplate) => {
    if (item && item.id) {
      return (
        <List.Item key={item.id}>
          <Card hoverable actions={renderAlertTemplateActionButton(item)}>
            <Card.Meta
              style={{ width: '100%', height: '15vh' }}
              title={<a>{item.name}</a>}
              description={
                <Markdown skipHtml={true} unwrapDisallowed>
                  {item.templateContent}
                </Markdown>
              }
            />
          </Card>
        </List.Item>
      );
    }

    return (
      <List.Item>
        <Authorized key={item.id} path='/registration/alert/template/add'>
          <Button
            type='dashed'
            style={{ height: '25vh', width: '100%' }}
            onClick={() =>
              setAlertTemplateState((prevState) => ({ ...prevState, addedOpen: true }))
            }
          >
            <PlusOutlined /> {l('rc.alert.template.new')}
          </Button>
        </Authorized>
      </List.Item>
    );
  };

  return (
    <PageContainer>
      <Button
        style={{ width: '100%', margin: '10px 0' }}
        type={'dashed'}
        icon={<PlusOutlined />}
        onClick={() =>
          setAlertTemplateState((prevState) => ({
            ...prevState,
            addedOpen: true
          }))
        }
      >
        {l('button.create')}
      </Button>
      <List<Alert.AlertTemplate>
        rowKey='id'
        loading={loading}
        grid={{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 3, xl: 4, xxl: 4 }}
        dataSource={data ?? []}
        renderItem={(item) => renderTemplateCard(item)}
      />

      <AlertTemplateForm
        onSubmit={handleSubmit}
        onCancel={handleCleanState}
        modalVisible={alertTemplateState.addedOpen}
        values={{}}
      />

      {alertTemplateState.value && Object.keys(alertTemplateState.value).length > 0 && (
        <AlertTemplateForm
          onSubmit={handleSubmit}
          onCancel={handleCleanState}
          modalVisible={alertTemplateState.editOpen}
          values={alertTemplateState.value}
        />
      )}
    </PageContainer>
  );
};
