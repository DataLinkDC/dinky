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

import { CreateBtn } from '@/components/CallBackButton/CreateBtn';
import { EditBtn } from '@/components/CallBackButton/EditBtn';
import { EnableSwitchBtn } from '@/components/CallBackButton/EnableSwitchBtn';
import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import { Authorized, HasAuthority } from '@/hooks/useAccess';
import useHookRequest from '@/hooks/useHookRequest';
import { CLUSTER_INSTANCE_TYPE } from '@/pages/RegCenter/Cluster/Instance/components/contants';
import { renderWebUiRedirect } from '@/pages/RegCenter/Cluster/Instance/components/function';
import InstanceModal from '@/pages/RegCenter/Cluster/Instance/components/InstanceModal';
import { getData } from '@/services/api';
import {
  handleAddOrUpdate,
  handleOption,
  handlePutDataByParams,
  handleRemoveById,
  updateDataByParam
} from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC, PRO_LIST_CARD_OPTIONS } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { PermissionConstants } from '@/types/Public/constants';
import { Cluster } from '@/types/RegCenter/data.d';
import { InitClusterInstanceState } from '@/types/RegCenter/init.d';
import { ClusterInstanceState } from '@/types/RegCenter/state.d';
import { l } from '@/utils/intl';
import {
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  HeartTwoTone,
  StopTwoTone
} from '@ant-design/icons';
import { ProList } from '@ant-design/pro-components';
import {
  Badge,
  Button,
  Card,
  Col,
  Descriptions,
  Divider,
  Input,
  List,
  Row,
  Space,
  Switch,
  Tag,
  Tooltip,
  Typography
} from 'antd';
import { useState } from 'react';

const { Text, Paragraph, Link } = Typography;

export default () => {
  /**
   * state
   */
  const [clusterInstanceStatus, setClusterInstanceStatus] =
    useState<ClusterInstanceState>(InitClusterInstanceState);
  const [isAutoCreate, setIsAutoCreate] = useState<boolean>(false);
  const [searchKeyWord, setSearchKeyword] = useState<string>('');

  const { data, loading, refresh } = useHookRequest(getData, {
    refreshDeps: [searchKeyWord, isAutoCreate],
    defaultParams: [
      API_CONSTANTS.CLUSTER_INSTANCE_LIST,
      { searchKeyWord: searchKeyWord, isAutoCreate: isAutoCreate }
    ]
  });

  /**
   * execute and callback function
   * @param {() => void} callback
   * @returns {Promise<void>}
   */
  const executeAndCallback = async (callback: () => void) => {
    setClusterInstanceStatus((prevState) => ({ ...prevState, loading: true }));
    await callback();
    setClusterInstanceStatus((prevState) => ({ ...prevState, loading: false }));
    await refresh();
  };

  /**
   * cancel
   */
  const handleCancel = async () => {
    setClusterInstanceStatus((prevState) => ({
      ...prevState,
      addedOpen: false,
      editOpen: false,
      value: {}
    }));
  };

  /**
   * submit add or update
   * @param value
   */
  const handleSubmit = async (value: Partial<Cluster.Instance>) => {
    await executeAndCallback(async () => {
      await handleAddOrUpdate(API_CONSTANTS.CLUSTER_INSTANCE, value);
      await handleCancel();
    });
  };

  /**
   * edit open
   * @param value
   */
  const handleEdit = async (value: Partial<Cluster.Instance>) => {
    setClusterInstanceStatus((prevState) => ({
      ...prevState,
      editOpen: true,
      value: value
    }));
  };

  /**
   * delete by id
   * @param id
   */
  const handleDelete = async (id: number) => {
    await executeAndCallback(async () =>
      handleRemoveById(API_CONSTANTS.CLUSTER_INSTANCE_DELETE, id)
    );
  };
  const handleKill = async (id: number) => {
    await executeAndCallback(async () =>
      handlePutDataByParams(API_CONSTANTS.CLUSTER_INSTANCE_KILL, l('rc.ci.kill'), { id })
    );
  };

  /**
   * enable or disable
   * @param record
   */
  const handleChangeEnable = async (record: Partial<Cluster.Instance>) => {
    await executeAndCallback(async () =>
      updateDataByParam(API_CONSTANTS.CLUSTER_INSTANCE_ENABLE, { id: record.id })
    );
  };

  /**
   * check heart beat
   */
  const handleHeartBeat = async () => {
    await executeAndCallback(async () =>
      handleOption(API_CONSTANTS.CLUSTER_INSTANCE_HEARTBEATS, l('rc.ci.heartbeat'), null)
    );
  };

  /**
   * tool bar render
   */
  const renderActionButton = (record: Cluster.Instance) => (
    <Space wrap direction={'vertical'} align={'center'}>
      <br />
      <Authorized
        key={`${record.id}_edit_auth`}
        path={PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_EDIT}
      >
        <EditBtn key={`${record.id}_edit`} onClick={() => handleEdit(record)} />
      </Authorized>
      <Authorized
        key={`${record.id}_delete_auth`}
        path={PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_DELETE}
      >
        <PopconfirmDeleteBtn
          key={`${record.id}_delete`}
          onClick={() => handleDelete(record.id)}
          description={l('rc.ci.deleteConfirm')}
        />
      </Authorized>
      {record.autoRegisters && record.status === 1 && (
        <Authorized
          key={`${record.id}_kill_auth`}
          path={PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_KILL}
        >
          <PopconfirmDeleteBtn
            key={`${record.id}_kill`}
            onClick={() => handleKill(record.id)}
            buttonIcon={<StopTwoTone />}
            title={l('rc.ci.kill')}
            description={l('rc.ci.killConfirm')}
          />
        </Authorized>
      )}
    </Space>
  );

  /**
   * render content
   * @param item
   */
  const renderDataContent = (item: Cluster.Instance) => {
    return (
      <>
        <Row wrap={false}>
          <Col flex='85%'>
            <Paragraph>
              <blockquote>
                {l('rc.ci.jma')}: {renderWebUiRedirect(item)}
              </blockquote>
              <blockquote>
                {l('rc.ci.version')}: <Link>{item.version}</Link>
              </blockquote>
              <Text title={item.alias} ellipsis>
                {(item.alias || item.alias === '') && (
                  <blockquote>
                    {l('rc.ci.alias')}: {item.alias}
                  </blockquote>
                )}
              </Text>
            </Paragraph>

            <Space size={8} align={'baseline'} className={'hidden-overflow'}>
              <EnableSwitchBtn
                record={item}
                onChange={() => handleChangeEnable(item)}
                disabled={!HasAuthority(PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_EDIT)}
              />
              <Tag color='cyan'>
                {CLUSTER_INSTANCE_TYPE().find((record) => item.type === record.value)?.label}
              </Tag>
              <Tag
                icon={item.status === 1 ? <CheckCircleOutlined /> : <ExclamationCircleOutlined />}
                color={item.status === 1 ? 'success' : 'warning'}
              >
                {item.status === 1
                  ? l('global.table.status.normal')
                  : l('global.table.status.abnormal')}
              </Tag>
            </Space>
          </Col>
          <Divider type={'vertical'} style={{ height: '100%' }} />
          <Col className={'card-button-list'} flex='auto'>
            {renderActionButton(item)}
          </Col>
        </Row>
      </>
    );
  };

  /**
   * render sub title
   * @param item
   */
  const renderTitle = (item: Cluster.Instance) => {
    return (
      <Descriptions size={'small'} layout={'vertical'} column={1}>
        <Descriptions.Item className={'hidden-overflow'} key={item.id}>
          <Tooltip key={item.id} title={item.note ? `${l('rc.ci.desc')}: ${item.note}` : ''}>
            {item.name}
          </Tooltip>
        </Descriptions.Item>
      </Descriptions>
    );
  };

  /**
   * tool bar render
   */
  const toolBarRender = () => [
    <Switch
      checkedChildren={l('rc.ci.ar')}
      unCheckedChildren={l('rc.ci.mr')}
      onChange={(v) => setIsAutoCreate(v)}
    />,
    <Authorized key={`_add_auth`} path={PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_ADD}>
      <CreateBtn
        key={`_add`}
        onClick={() => setClusterInstanceStatus((prevState) => ({ ...prevState, addedOpen: true }))}
      />
    </Authorized>,
    <Authorized
      key={`_add_heartbeat`}
      path={PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_HEARTBEATS}
    >
      <Button
        key={`_add_heartbeat_btn`}
        type={'primary'}
        icon={<HeartTwoTone />}
        onClick={() => handleHeartBeat()}
      >
        {l('button.heartbeat')}
      </Button>
    </Authorized>
  ];

  const renderListItem = (item: Cluster.Instance) => {
    return (
      <List.Item className={'card-list-item-wrapper'} key={item.id}>
        <Badge.Ribbon
          className={'card-list-item-wrapper'}
          color={item.autoRegisters ? '#95de64' : '#ffec3d'}
          text={
            item.autoRegisters ? (
              l('rc.ci.ar')
            ) : (
              <span style={{ color: '#69b1ff' }}>{l('rc.ci.mr')}</span>
            )
          }
        >
          <Card
            headStyle={{ minHeight: '10px' }}
            bodyStyle={{ width: '100%', padding: '10px 4px' }}
            className={'card-list-item'}
            key={item.id}
            hoverable
            title={renderTitle(item)}
          >
            <Card.Meta style={{ width: '100%' }} description={renderDataContent(item)} />
          </Card>
        </Badge.Ribbon>
      </List.Item>
    );
  };

  /**
   * render
   */
  return (
    <>
      <ProList<Cluster.Instance>
        headerTitle={
          <Input.Search
            loading={clusterInstanceStatus.loading}
            key={`_search`}
            allowClear
            placeholder={l('rc.ci.search')}
            onSearch={(v) => setSearchKeyword(v)}
          />
        }
        toolBarRender={toolBarRender}
        {...PROTABLE_OPTIONS_PUBLIC}
        {...(PRO_LIST_CARD_OPTIONS as any)}
        grid={{ gutter: 24, column: 4 }}
        pagination={{ size: 'small', defaultPageSize: 12, hideOnSinglePage: true }}
        dataSource={data}
        loading={loading}
        itemLayout={'vertical'}
        renderItem={renderListItem}
      />

      {/*added*/}
      <InstanceModal
        visible={clusterInstanceStatus.addedOpen}
        onClose={handleCancel}
        value={{}}
        onSubmit={handleSubmit}
      />
      {/*modify*/}
      <InstanceModal
        visible={clusterInstanceStatus.editOpen}
        onClose={handleCancel}
        value={clusterInstanceStatus.value}
        onSubmit={handleSubmit}
      />
    </>
  );
};
