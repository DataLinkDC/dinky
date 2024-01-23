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

import VersionList from '@/components/VersionList';
import { getCurrentData } from '@/pages/DataStudio/function';
import { StateType } from '@/pages/DataStudio/model';
import { handleOption, handleRemoveById } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { TaskVersionListItem } from '@/types/Studio/data';
import { convertCodeEditTheme } from '@/utils/function';
import { l } from '@/utils/intl';
import { useRequest } from '@@/exports';
import { RocketOutlined, SyncOutlined } from '@ant-design/icons';
import { DiffEditor } from '@monaco-editor/react';
import { Button, Card, Modal, Tag } from 'antd';
import moment from 'moment';
import React, { useEffect, useState } from 'react';
import { connect } from 'umi';

const HistoryVersion = (props: any) => {
  const {
    tabs: { panes, activeKey }
  } = props;

  const current = getCurrentData(panes, activeKey);
  const versionList = useRequest({
    url: API_CONSTANTS.GET_JOB_VERSION,
    params: { taskId: current?.id }
  });

  useEffect(() => {
    if (current?.id) {
      versionList.run();
    }
  }, [current, activeKey]);

  const [versionDiffVisible, setVersionDiffVisible] = useState<boolean>(false);
  const [versionDiffRow, setVersionDiffRow] = useState<TaskVersionListItem>();

  const VersionDiffForm = () => {
    let leftTitle = l('pages.datastudio.label.version.leftTitle', '', {
      versionId: versionDiffRow?.versionId,
      createTime: moment(versionDiffRow?.createTime).format('YYYY-MM-DD HH:mm:ss')
    });

    let rightTitle = l('pages.datastudio.label.version.rightTitle', '', {
      createTime: moment(current?.createTime).format('YYYY-MM-DD HH:mm:ss'),
      updateTime: moment(current?.updateTime).format('YYYY-MM-DD HH:mm:ss')
    });
    let originalValue = versionDiffRow?.statement;
    let currentValue = current?.statement;

    return (
      <Modal
        title={l('pages.datastudio.label.version.diff')}
        open={versionDiffVisible}
        destroyOnClose={true}
        width={'85%'}
        bodyStyle={{ height: '70vh' }}
        onCancel={() => setVersionDiffVisible(false)}
        footer={[
          <Button key='back' onClick={() => setVersionDiffVisible(false)}>
            {l('button.close')}
          </Button>
        ]}
      >
        <div style={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-between' }}>
          <Tag color='green' style={{ height: '20px' }}>
            <RocketOutlined /> {leftTitle}
          </Tag>
          <Tag color='blue' style={{ height: '20px' }}>
            <SyncOutlined spin /> {rightTitle}
          </Tag>
        </div>
        <br />
        <React.StrictMode>
          <DiffEditor
            height={'95%'}
            options={{
              readOnly: true,
              selectOnLineNumbers: true,
              lineDecorationsWidth: 20,
              mouseWheelZoom: true,
              automaticLayout: true
            }}
            language={'sql'}
            original={originalValue}
            modified={currentValue}
            theme={convertCodeEditTheme()}
          />
        </React.StrictMode>
      </Modal>
    );
  };

  const onRollBackVersion = async (row: TaskVersionListItem) => {
    Modal.confirm({
      title: l('pages.datastudio.label.version.rollback.flinksql'),
      content: l('pages.datastudio.label.version.rollback.flinksqlConfirm', '', {
        versionId: row.versionId
      }),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        const TaskVersionRollbackItem = {
          taskId: row.taskId,
          versionId: row.versionId
        };
        await handleOption(
          'api/task/rollbackTask',
          l('pages.datastudio.label.version.rollback.flinksql'),
          TaskVersionRollbackItem
        );
      }
    });
  };

  const deleteVersion = async (item: TaskVersionListItem) => {
    await handleRemoveById(API_CONSTANTS.GET_JOB_VERSION, item.id);
    versionList.run();
  };

  return (
    <Card>
      <VersionList
        loading={versionList.loading}
        data={versionList.data}
        onDeleteListen={deleteVersion}
        onRollBackListen={onRollBackVersion}
        onSelectListen={(item) => {
          setVersionDiffRow(item);
          setVersionDiffVisible(true);
        }}
      />
      {VersionDiffForm()}
    </Card>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs
}))(HistoryVersion);
