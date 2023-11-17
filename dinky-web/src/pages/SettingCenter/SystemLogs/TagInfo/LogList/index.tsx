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

import LogsShow from '@/pages/SettingCenter/SystemLogs/TagInfo/LogList/LogsShow';
import LogsTree from '@/pages/SettingCenter/SystemLogs/TagInfo/LogList/LogsTree';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { LogInfo } from '@/types/SettingCenter/data';
import { ProCard } from '@ant-design/pro-components';
import { useEffect, useState } from 'react';

const LogList = () => {
  const [treeData, setTreeData] = useState<LogInfo[]>([]);
  const [log, setLog] = useState<string>('');
  const [clickFileName, setClickFileName] = useState<any>();

  const queryLogList = async () => {
    await queryDataByParams<LogInfo[]>(API_CONSTANTS.SYSTEM_ROOT_LOG_LIST).then((res) => {
      setTreeData(res ?? []);
    });
  };

  const queryLogContent = async (fileName: string) => {
    await queryDataByParams(API_CONSTANTS.SYSTEM_ROOT_LOG_READ, {
      path: fileName
    }).then((res) => {
      setLog(res as string);
    });
  };

  useEffect(() => {
    queryLogList();
  }, []);

  const handleNodeClick = async (info: any) => {
    const {
      node: { path, isLeaf }
    } = info;
    if (isLeaf) {
      setClickFileName(path);
      await queryLogContent(path);
    }
  };

  const refreshLogByClickNode = async () => {
    await queryLogContent(clickFileName);
  };

  return (
    <>
      <ProCard bodyStyle={{ height: parent.innerHeight - 160 }} ghost bordered>
        <ProCard
          ghost
          bodyStyle={{ height: parent.innerHeight }}
          hoverable
          bordered
          colSpan={'18%'}
          className={'siderTree'}
        >
          <LogsTree treeData={treeData} onNodeClick={(info: any) => handleNodeClick(info)} />
        </ProCard>
        <ProCard.Divider type={'vertical'} />
        <ProCard
          ghost
          bodyStyle={{ height: parent.innerHeight }}
          hoverable
          bordered
          colSpan={'auto'}
          className={'siderTree'}
        >
          <LogsShow code={log} refreshLogCallback={() => refreshLogByClickNode()} />
        </ProCard>
      </ProCard>
    </>
  );
};

export default LogList;
