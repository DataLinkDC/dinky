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
import { StateType, STUDIO_MODEL } from '@/pages/DataStudio/model';
import DataSourceDetail from '@/pages/RegCenter/DataSource/components/DataSourceDetail';
import { renderDBIcon } from '@/pages/RegCenter/DataSource/components/function';
import { queryList } from '@/services/api';
import {
  handleAddOrUpdate,
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
  const navigate = useNavigate();

  const { dispatch } = props;

  /**
   * state
   */
  const actionRef = React.useRef<ActionType>();
  const [loading, setLoading] = React.useState<boolean>(false);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [detailPage, setDetailPage] = useState<boolean>(false);
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
  const [dataSource, setDataSource] = useState<DataSources.DataSource[]>([]);
  const [formValues, setFormValues] = useState<Partial<DataSources.DataSource>>({});

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
  const executeAndCallbackRefresh = async (callback: () => Promise<void>) => {
    setLoading(true);
    await callback();
    await queryDataSourceList();
    setLoading(false);
  };

  /**
   * handle add or update
   * @param item
   */
  const saveOrUpdateHandle = async (item: Partial<DataSources.DataSource>) => {
    await executeAndCallbackRefresh(async () => {
      await handleAddOrUpdate(API_CONSTANTS.DATASOURCE, item);
    });
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
      onOk: async () => {
        await executeAndCallbackRefresh(async () => {
          await handleRemoveById(API_CONSTANTS.DATASOURCE_DELETE, id);
        });
      }
    });
  };

  /**
   * handle enable
   * @param item
   */
  const handleEnable = async (item: DataSources.DataSource) => {
    await executeAndCallbackRefresh(async () => {
      await updateDataByParam(API_CONSTANTS.DATASOURCE_ENABLE, { id: item.id });
    });
  };

  /**
   * handle test
   * @param item
   */
  const handleTest = async (item: Partial<DataSources.DataSource>) => {
    await handleOption(API_CONSTANTS.DATASOURCE_TEST, l('button.test'), item);
  };

  /**
   * handle check heart
   * @param item
   */
  const handleCheckHeartBeat = async (item: DataSources.DataSource) => {
    await executeAndCallbackRefresh(async () => {
      await handlePutDataByParams(
        API_CONSTANTS.DATASOURCE_CHECK_HEARTBEAT_BY_ID,
        l('button.heartbeat'),
        {
          id: item.id
        }
      );
    });
  };

  const onCopyDataBase = async (item: DataSources.DataSource) => {
    await executeAndCallbackRefresh(async () => {
      await handleOption(API_CONSTANTS.DATASOURCE_COPY, l('button.copy'), item);
    });
  };

  /**
   * query  list
   */
  useEffect(() => {
    queryDataSourceList();
  }, []);

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
    setUpdateModalVisible(!modalVisible);
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
      navigate(`/registration/database/detail/${item.id}`, {
        state: { from: '/registration/database' }
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
      <EditBtn key={`${item.id}_edit`} onClick={() => editClick(item)} />,
      <NormalDeleteBtn key={`${item.id}_delete`} onClick={() => handleDeleteSubmit(item.id)} />,
      <Button
        className={'options-button'}
        key={`${item.id}_heart`}
        onClick={() => handleCheckHeartBeat(item)}
        title={l('button.heartbeat')}
        icon={<HeartTwoTone twoToneColor={item.status ? '#1ac431' : '#e10d0d'} />}
      />,
      <Button
        className={'options-button'}
        key={`${item.id}_copy`}
        onClick={() => onCopyDataBase(item)}
        title={l('button.copy')}
        icon={<CopyTwoTone />}
      />
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
        <EnableSwitchBtn record={item} onChange={() => handleEnable(item)} />
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
    setUpdateModalVisible(false);
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
              <CreateBtn key={'CreateBtn'} onClick={() => setModalVisible(true)} />
            ]}
            dataSource={renderDataSource}
          />

          {/* added */}
          <DataSourceModal
            values={{}}
            visible={modalVisible}
            onCancel={cancelAll}
            onTest={(value) => handleTest(value)}
            onSubmit={(value) => saveOrUpdateHandle(value)}
          />

          {/* modify*/}
          <DataSourceModal
            values={formValues}
            visible={updateModalVisible}
            onCancel={cancelAll}
            onTest={(value) => handleTest(value)}
            onSubmit={(value) => saveOrUpdateHandle(value)}
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
