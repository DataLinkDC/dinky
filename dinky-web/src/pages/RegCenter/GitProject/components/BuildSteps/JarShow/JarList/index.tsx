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

import { handleOption } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { BuildJarList } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { ProList } from '@ant-design/pro-components';
import { ActionType, DragSortTable, ProColumns } from '@ant-design/pro-table';
import { List, Tag } from 'antd';
import React, { useEffect, useRef, useState } from 'react';

/**
 * props
 */
type JarListProps = {
  projectId: number;
  jarAndClassesList: Partial<BuildJarList[]>;
};

const JarList: React.FC<JarListProps> = (props) => {
  /**
   * state
   */
  const { jarAndClassesList, projectId } = props;
  const actionRef = useRef<ActionType>();
  const [loading, setLoading] = useState(false);
  const [classes, setClasses] = useState<Partial<BuildJarList[]>>();

  /**
   * init
   */
  useEffect(() => {
    setClasses(jarAndClassesList);
  }, [jarAndClassesList]);

  /**
   * columns
   */
  const columns: ProColumns<BuildJarList>[] = [
    {
      title: l('rc.gp.level'),
      dataIndex: 'orderLine',
      tooltip: l('rc.gp.ucl.orderLine.tooltip'),
      copyable: true,
      render: (dom: any, record: BuildJarList) => {
        return (
          <Tag
            style={{ marginLeft: 10 }}
            color={record.orderLine > 3 ? 'default' : 'success'}
          >{`No.${record.orderLine}`}</Tag>
        );
      }
    },
    {
      dataIndex: 'index',
      valueType: 'indexBorder'
    },
    {
      title: l('rc.gp.ucl.jarPath'),
      dataIndex: 'jarPath',
      copyable: true
    }
  ];

  /**
   * render sublist of udf
   * @param {BuildJarList} record
   * @returns {JSX.Element}
   */
  const renderUdf = (record: BuildJarList): JSX.Element => (
    <ProList
      dataSource={record.classList as any[]}
      rowKey='index'
      size={'small'}
      pagination={{
        pageSize: 5,
        hideOnSinglePage: true,
        showSizeChanger: false
      }}
      renderItem={(item, index) => {
        return (
          <List.Item className={'child-list'} key={index}>
            {item}
          </List.Item>
        );
      }}
    />
  );

  /**
   * drag sort call
   * @param {BuildJarList[]} newDataSource
   * @returns {Promise<void>}
   */
  const handleDragSortEnd = async (newDataSource: BuildJarList[]): Promise<void> => {
    setLoading(true);
    const updatedItems = newDataSource.map((item: BuildJarList, index: number) => ({
      ...item,
      orderLine: index + 1
    }));
    await handleOption(API_CONSTANTS.GIT_DRAGEND_SORT_JAR, l('rc.gp.ucl.jarOrder'), {
      projectId,
      jars: updatedItems
    });
    setClasses(updatedItems);
    setLoading(false);
    actionRef.current?.reload();
  };

  /**
   * render
   */
  return (
    <DragSortTable<BuildJarList>
      style={{
        overflowY: 'auto',
        msOverflowY: 'hidden',
        marginLeft: '0.5vw'
      }}
      columns={columns}
      toolBarRender={false}
      sortDirections={['ascend']}
      showHeader={false}
      actionRef={actionRef}
      loading={loading}
      dataSource={classes as BuildJarList[]}
      search={false}
      rowKey='orderLine'
      revalidateOnFocus
      pagination={{
        defaultPageSize: 5,
        hideOnSinglePage: true
      }}
      expandable={{
        expandRowByClick: false,
        expandedRowRender: (record) => renderUdf(record)
      }}
      dragSortKey={'orderLine'}
      onDragSortEnd={(beforeIndex, afterIndex, newDataSource) => handleDragSortEnd(newDataSource)}
    />
  );
};

export default JarList;
