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

import { getCurrentData } from '@/pages/DataStudio/function';
import { StateType, TaskDataType } from '@/pages/DataStudio/model';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { SavePoint } from '@/types/Studio/data';
import { l } from '@/utils/intl';
import { ActionType, ProDescriptions, ProTable } from '@ant-design/pro-components';
import { ProDescriptionsItemProps } from '@ant-design/pro-descriptions';
import { ProColumns } from '@ant-design/pro-table';
import { Drawer } from 'antd';
import { useEffect, useRef, useState } from 'react';
import { connect } from 'umi';

const url = '/api/savepoints';

const SavePoints = (props: any) => {
  const {
    tabs: { panes, activeKey }
  } = props;
  const current = getCurrentData(panes, activeKey) as TaskDataType;
  const [row, setRow] = useState<SavePoint>();
  const actionRef = useRef<ActionType>();

  const [savepointData, setSavepointData] = useState<SavePoint[]>([]);

  useEffect(() => {
    queryDataByParams<Partial<SavePoint[]>>(API_CONSTANTS.GET_SAVEPOINT_LIST_BY_TASK_ID, {
      taskId: current?.id
    }).then((res) => {
      setSavepointData((res as SavePoint[]) ?? []);
    });
  }, [current?.id]);

  const columns: ProColumns<SavePoint>[] | ProDescriptionsItemProps<SavePoint[]> = [
    {
      title: l('pages.task.savePointPath'),
      dataIndex: 'path',
      hideInForm: true,
      ellipsis: true,
      tooltip: true,
      copyable: true,
      hideInSearch: true
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      }
    }
  ];

  return (
    <>
      <ProTable<SavePoint>
        actionRef={actionRef}
        rowKey='id'
        dataSource={savepointData}
        columns={columns}
        size={'small'}
        search={false}
        options={false}
      />
      <Drawer
        width={600}
        open={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.name && (
          <ProDescriptions<SavePoint>
            column={2}
            title={row?.name}
            request={async () => ({
              data: row || {}
            })}
            params={{
              id: row?.name
            }}
            columns={columns as ProDescriptionsItemProps<SavePoint>[]}
          />
        )}
      </Drawer>
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs
}))(SavePoints);
