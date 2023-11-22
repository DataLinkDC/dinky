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

import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import { TaskVersionListItem } from '@/types/Studio/data';
import { l } from '@/utils/intl';
import { DeliveredProcedureOutlined } from '@ant-design/icons';
import { List, Skeleton, Space, Tag, Tooltip } from 'antd';
import { ListItemTypeProps } from 'antd/es/list/Item';

export interface VersionListProps {
  data: TaskVersionListItem[];
  onSelectListen?: (value: TaskVersionListItem) => void;
  onDeleteListen?: (value: TaskVersionListItem) => void;
  onRollBackListen?: (value: TaskVersionListItem) => void;
  loading?: boolean;
  header?: string;
  options?: ListItemTypeProps;
}

const VersionList = (props: VersionListProps) => {
  const {
    data,
    onSelectListen = () => {},
    onDeleteListen,
    onRollBackListen,
    loading,
    header,
    options
  } = props;

  const renderItem = (item: TaskVersionListItem) => {
    return (
      <List.Item onClick={() => onSelectListen(item)}>
        <Skeleton title={false} loading={loading} active>
          <List.Item.Meta
            title={
              <a>
                {!item.isLatest ? (
                  `V-${item.versionId}`
                ) : (
                  <Tag key={'v-latest'} color='green'>
                    {l('devops.jobinfo.version.latestVersion')}
                  </Tag>
                )}
              </a>
            }
            description={item.createTime}
          />
          {!item.isLatest && (
            <Space onClick={(e) => e.stopPropagation()}>
              {onRollBackListen && (
                <Tooltip title={l('devops.jobinfo.version.rollBack')}>
                  <DeliveredProcedureOutlined onClick={() => onRollBackListen(item)} />
                </Tooltip>
              )}
              {onDeleteListen && (
                <Tooltip title={l('devops.jobinfo.version.delete')}>
                  <PopconfirmDeleteBtn
                    description={l('devops.jobinfo.version.delete.sure', '', {
                      version: item.versionId
                    })}
                    onClick={() => {
                      onDeleteListen(item);
                    }}
                    options={{
                      size: 'small',
                      type: 'dashed '
                    }}
                  />
                </Tooltip>
              )}
            </Space>
          )}
        </Skeleton>
      </List.Item>
    );
  };

  return (
    <List
      {...options}
      pagination={{
        position: 'bottom',
        align: 'end',
        size: 'small',
        defaultPageSize: 8
      }}
      size={'small'}
      header={header}
      dataSource={data}
      renderItem={(item: TaskVersionListItem) => renderItem(item)}
    ></List>
  );
};

export default VersionList;
