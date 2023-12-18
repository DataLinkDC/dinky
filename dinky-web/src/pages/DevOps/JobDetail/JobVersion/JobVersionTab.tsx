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

import CodeShow from '@/components/CustomEditor/CodeShow';
import VersionList from '@/components/VersionList';
import { matchLanguage } from '@/pages/DataStudio/MiddleContainer/function';
import { JobProps } from '@/pages/DevOps/JobDetail/data';
import { handleRemoveById } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { TaskVersionListItem } from '@/types/Studio/data';
import { l } from '@/utils/intl';
import { useRequest } from '@@/exports';
import { SplitPane } from '@andrewray/react-multi-split-pane';
import { Pane } from '@andrewray/react-multi-split-pane/dist/lib/Pane';
import { ProCard } from '@ant-design/pro-components';
import { Tag } from 'antd';
import { useRef, useState } from 'react';

const JobVersionTab = (props: JobProps) => {
  const { jobDetail } = props;
  const latestVersion: TaskVersionListItem = {
    id: -1,
    type: jobDetail.history.type,
    statement: jobDetail.history.statement,
    createTime: jobDetail.history.startTime,
    versionId: 'Current',
    isLatest: true
  };
  const refObject = useRef<HTMLDivElement>(null);

  const [currentVersion, setCurrentVersion] = useState<TaskVersionListItem>(latestVersion);

  const versionList = useRequest(
    {
      url: API_CONSTANTS.GET_JOB_VERSION,
      params: { taskId: jobDetail.history.taskId }
    },
    {
      onSuccess: (data: TaskVersionListItem[], params) => {
        data.splice(0, 0, latestVersion);
      }
    }
  );

  const deleteVersion = async (item: TaskVersionListItem) => {
    await handleRemoveById(API_CONSTANTS.GET_JOB_VERSION, item.id);
    versionList.run();
  };

  const renderVersionList = () => {
    return (
      <>
        <SplitPane
          split={'vertical'}
          defaultSizes={[100, 500]}
          minSize={150}
          className={'split-pane'}
        >
          <Pane
            className={'split-pane'}
            forwardRef={refObject}
            minSize={100}
            size={100}
            split={'horizontal'}
          >
            <VersionList
              loading={versionList.loading}
              data={versionList.data}
              onDeleteListen={deleteVersion}
              onSelectListen={(item) => setCurrentVersion(item)}
              header={l('devops.jobinfo.version.versionList')}
            />
          </Pane>

          <Pane
            className={'split-pane'}
            forwardRef={refObject}
            minSize={100}
            size={100}
            split={'horizontal'}
          >
            <ProCard
              ghost
              hoverable
              bordered
              size={'small'}
              bodyStyle={{ height: parent.innerHeight }}
              title={'V-' + currentVersion?.versionId}
              extra={
                <>
                  <Tag key={'v-type'} color='blue'>
                    {currentVersion?.type}
                  </Tag>
                  <Tag key={'v-dialect'} color='success'>
                    {currentVersion?.dialect}
                  </Tag>
                </>
              }
            >
              <CodeShow
                showFloatButton
                code={currentVersion?.statement ?? ''}
                height={parent.innerHeight - 250}
                language={matchLanguage(currentVersion?.dialect)}
              />
            </ProCard>
          </Pane>
        </SplitPane>
      </>
    );
  };

  return (
    <>
      <ProCard size={'small'} bodyStyle={{ height: 'calc(100vh - 180px)', overflow: 'auto' }}>
        {renderVersionList()}
      </ProCard>
    </>
  );
};

export default JobVersionTab;
