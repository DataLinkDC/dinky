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
import { NormalDeleteBtn } from '@/components/CallBackButton/NormalDeleteBtn';
import { DataAction } from '@/components/StyledComponents';
import { Authorized, HasAuthority } from '@/hooks/useAccess';
import { StateType, STUDIO_MODEL } from '@/pages/DataStudio/model';
import DataSourceDetail from '@/pages/RegCenter/DataSource/components/DataSourceDetail';
import { renderDBIcon } from '@/pages/RegCenter/DataSource/components/function';
import { handleTest, saveOrUpdateHandle } from '@/pages/RegCenter/DataSource/service';
import { queryList } from '@/services/api';
import {
  handleOption,
  handlePutDataByParams,
  handleRemoveById,
  updateDataByParam
} from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC, PRO_LIST_CARD_OPTIONS } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { DataSources } from '@/types/RegCenter/data.d';
import { l } from '@/utils/intl';
import { WarningMessage } from '@/utils/messages';
import { useNavigate } from '@@/exports';
import {
  CheckCircleOutlined,
  CopyTwoTone,
  ExclamationCircleOutlined,
  HeartTwoTone
} from '@ant-design/icons';
import { ActionType, ProList } from '@ant-design/pro-components';
import { Button, Descriptions, Modal, Space, Tag, Tooltip } from 'antd';
import DescriptionsItem from 'antd/es/descriptions/Item';
import React, { useEffect, useState } from 'react';
import { connect } from 'umi';
import DataSourceModal from '../DataSourceModal';

const DataSourceTable: React.FC<connect & StateType> = (props) => {
  const { dispatch } = props;
  const navigate = useNavigate();

  /**
   * state
   */
  const [loading, setLoading] = React.useState<boolean>(false);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [detailPage, setDetailPage] = useState<boolean>(false);
  const [dataSource, setDataSource] = useState<DataSources.DataSource[]>([]);
  const [formValues, setFormValues] = useState<Partial<DataSources.DataSource>>({});
  const actionRef = React.useRef<ActionType>();

  /**
   * query  list
   */
  useEffect(() => {
    queryDataSourceList();
  }, []);

  /**
   * execute query  list
   * set   list
   */
  const queryDataSourceList = async () => {
    const res = await queryList(API_CONSTANTS.DATASOURCE);
    setDataSource(res.data);
  };

  /**
   * extra callback
   * @param callback
   */
  const executeAndCallbackRefresh = async (callback: () => Promise<any>) => {
    setLoading(true);
    await callback();
    await queryDataSourceList();
    setLoading(false);
  };

  /**
   * handle delete
   * @param id
   */
  const handleDeleteSubmit = async (id: number) => {
    Modal.confirm({
      title: l('rc.ds.delete'),
      content: l('rc.ds.deleteConfirm'),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () =>
        executeAndCallbackRefresh(async () => handleRemoveById(API_CONSTANTS.DATASOURCE_DELETE, id))
    });
  };

  /**
   * handle enable
   * @param item
   */
  const handleEnable = async (item: DataSources.DataSource) => {
    await executeAndCallbackRefresh(async () =>
      updateDataByParam(API_CONSTANTS.DATASOURCE_ENABLE, { id: item.id })
    );
  };

  /**
   * handle check heart
   * @param item
   */
  const handleCheckHeartBeat = async (item: DataSources.DataSource) => {
    await executeAndCallbackRefresh(async () =>
      handlePutDataByParams(API_CONSTANTS.DATASOURCE_CHECK_HEARTBEAT_BY_ID, l('button.heartbeat'), {
        id: item.id
      })
    );
  };

  const onCopyDataBase = async (item: DataSources.DataSource) => {
    await executeAndCallbackRefresh(async () =>
      handleOption(API_CONSTANTS.DATASOURCE_COPY, l('button.copy'), item)
    );
  };

  /**
   * render sub title
   * @param item
   */
  const renderDataSourceSubTitle = (item: DataSources.DataSource) => {
    return (
      <Descriptions size={'small'} layout={'vertical'} column={1}>
        <DescriptionsItem className={'hidden-overflow'} key={item.id}>
          <Tooltip key={item.name} title={item.name}>
            {item.name}
          </Tooltip>
        </DescriptionsItem>
      </Descriptions>
    );
  };

  /**
   * edit click callback
   * @param item
   */
  const editClick = (item: DataSources.DataSource) => {
    setFormValues(item);
    setModalVisible(!modalVisible);
  };

  /**
   * enter details page callback
   * @param item
   */
  const enterDetailPageClickHandler = async (item: DataSources.DataSource) => {
    // if status is true, enter detail page, else show error message , do nothing
    if (item.status) {
      dispatch({
        type: STUDIO_MODEL.updateSelectDatabaseId,
        payload: item.id
      });
      setFormValues(item);
      navigate(`/registration/datasource/detail/${item.id}`, {
        state: { from: '/registration/datasource' }
      });
      setDetailPage(!detailPage);
    } else {
      await WarningMessage(l('rc.ds.enter.error'));
    }
  };

  /**
   * render alert instance action button
   * @param item
   */
  const renderDataSourceActionButton = (item: DataSources.DataSource) => {
    return [
      <Authorized key={`${item.id}_edit`} path='/registration/datasource/add'>
        <EditBtn key={`${item.id}_edit`} onClick={() => editClick(item)} />
      </Authorized>,
      <Authorized key={`${item.id}_delete`} path='/registration/datasource/delete'>
        <NormalDeleteBtn key={`${item.id}_delete`} onClick={() => handleDeleteSubmit(item.id)} />
      </Authorized>,
      <Authorized key={`${item.id}_detail`} path='/registration/datasource/heartbeat'>
        <Button
          className={'options-button'}
          key={`${item.id}_heart`}
          onClick={() => handleCheckHeartBeat(item)}
          title={l('button.heartbeat')}
          icon={<HeartTwoTone twoToneColor={item.status ? '#1ac431' : '#e10d0d'} />}
        />
      </Authorized>,
      <Authorized key={`${item.id}_test`} path='/registration/datasource/copy'>
        <Button
          className={'options-button'}
          key={`${item.id}_copy`}
          onClick={() => onCopyDataBase(item)}
          title={l('button.copy')}
          icon={<CopyTwoTone />}
        />
      </Authorized>
    ];
  };
  /**
   * render alert instance action button
   * @param item
   */
  const renderDataSourceContent = (item: DataSources.DataSource) => {
    return (
      <Space className={'hidden-overflow'}>
        <Tag color='cyan'>{item.type}</Tag>
        <EnableSwitchBtn
          record={item}
          onChange={() => handleEnable(item)}
          disabled={!HasAuthority('/registration/datasource/edit')}
        />
        <Tag
          icon={item.status ? <CheckCircleOutlined /> : <ExclamationCircleOutlined />}
          color={item.status ? 'success' : 'warning'}
        >
          {item.status ? l('global.table.status.normal') : l('global.table.status.abnormal')}
        </Tag>
      </Space>
    );
  };

  /**
   * render data source
   */
  const renderDataSource = dataSource.map((item) => ({
    subTitle: renderDataSourceSubTitle(item),
    actions: <DataAction>{renderDataSourceActionButton(item)}</DataAction>,
    avatar: (
      <Space onClick={() => enterDetailPageClickHandler(item)}>{renderDBIcon(item.type, 60)}</Space>
    ),
    content: renderDataSourceContent(item),
    key: item.id
  }));

  /**
   * cancel all
   */
  const cancelAll = () => {
    setModalVisible(false);
    setFormValues({});
  };

  /**
   * render
   */
  return (
    <>
      {!detailPage ? (
        <>
          <ProList<DataSources.DataSource>
            {...PROTABLE_OPTIONS_PUBLIC}
            {...(PRO_LIST_CARD_OPTIONS as any)}
            loading={loading}
            tooltip={l('rc.ds.enter')}
            actionRef={actionRef}
            headerTitle={l('rc.ds.management')}
            toolBarRender={() => [
              <Authorized key='create' path='/registration/datasource/add'>
                <CreateBtn key={'CreateBtn'} onClick={() => setModalVisible(true)} />
              </Authorized>
            ]}
            dataSource={renderDataSource}
          />

          {/* added */}
          <DataSourceModal
            values={formValues}
            visible={modalVisible}
            onCancel={cancelAll}
            onTest={(value) => handleTest(value)}
            onSubmit={(value) => executeAndCallbackRefresh(async () => saveOrUpdateHandle(value))}
          />
        </>
      ) : (
        <DataSourceDetail backClick={() => setDetailPage(false)} dataSource={formValues} />
      )}
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  database: Studio.database
}))(DataSourceTable);
