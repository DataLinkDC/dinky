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

import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {getCurrentData} from "@/pages/DataStudio/function";
import {useRef, useState} from "react";
import {ActionType, ProDescriptions, ProTable} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import {Drawer} from "antd";
import {ProDescriptionsItemProps} from "@ant-design/pro-descriptions";
import {postAll} from "@/services/api";

const url = '/api/savepoints';

export type SavePointTableListItem = {
  id: number,
  taskId: number,
  name: string,
  type: string,
  path: string,
  createTime: Date,
};
const SavePoints = (props: any) => {

  const { tabs: {panes, activeKey}} = props;
  const current = getCurrentData(panes, activeKey);
  const [row, setRow] = useState<SavePointTableListItem>();
  const actionRef = useRef<ActionType>();
  actionRef.current?.reloadAndRest?.();

  const columns: ProDescriptionsItemProps<SavePointTableListItem>[] = [

    {
      title: l('pages.task.savePointPath'),
      dataIndex: 'path',
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
  ];

  return (
    <>
        <ProTable<SavePointTableListItem>
          actionRef={actionRef}
          rowKey="id"
          request={(params, sorter, filter) => postAll(url, {taskId: current.key, ...params, sorter, filter})}
          columns={columns}
          search={false}
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
            <ProDescriptions<SavePointTableListItem>
              column={2}
              title={row?.name}
              request={async () => ({
                data: row || {},
              })}
              params={{
                id: row?.name,
              }}
              columns={columns}
            />
          )}
        </Drawer>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  tabs: Studio.tabs,
}))(SavePoints);
