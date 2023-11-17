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
import { JobProps } from '@/pages/DevOps/JobDetail/data';
import { handleRemoveById } from '@/services/BusinessCrud';
import { DIALECT } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { TaskVersionListItem } from '@/types/Studio/data';
import { l } from '@/utils/intl';
import { useRequest } from '@@/exports';
import { Card, Col, Row, Tag } from 'antd';
import { useState } from 'react';

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
      <Row>
        <Col span={3}>
          <VersionList
            loading={versionList.loading}
            data={versionList.data}
            onDeleteListen={deleteVersion}
            onSelectListen={(item) => setCurrentVersion(item)}
            header={l('devops.jobinfo.version.versionList')}
          />
        </Col>
        <Col span={21}>
          <Card
            title={'V-' + currentVersion?.versionId}
            bordered={false}
            extra={
              <>
                <Tag key={'v-type'} color='blue'>
                  {currentVersion?.type}
                </Tag>
                <Tag key={'v-dialect'} color='yellow'>
                  {currentVersion?.dialect}
                </Tag>
              </>
            }
          >
            <CodeShow
              code={currentVersion?.statement ?? ''}
              height={500}
              language={
                currentVersion?.dialect?.toLowerCase() === DIALECT.FLINK_SQL ? 'flinksql' : 'sql'
              }
            />
          </Card>
        </Col>
      </Row>
    );
  };

  return (
    <>
      <Card>{renderVersionList()}</Card>
    </>
  );
};

export default JobVersionTab;
