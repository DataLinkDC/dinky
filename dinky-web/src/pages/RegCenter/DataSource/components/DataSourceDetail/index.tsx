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

import React, {useCallback, useEffect, useState} from 'react';
import {DataSources} from '@/types/RegCenter/data';
import {Button, Space} from 'antd';
import {BackwardOutlined, ReloadOutlined} from '@ant-design/icons';
import {DataSourceDetailBackButton} from '@/components/StyledComponents';
import {l} from '@/utils/intl';
import SchemaTree from '@/pages/RegCenter/DataSource/components/DataSourceDetail/SchemaTree';
import RightTagsRouter from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter';
import {useNavigate} from '@umijs/max';
import {API_CONSTANTS, RESPONSE_CODE} from '@/services/constants';
import {getDataByIdReturnResult} from '@/services/BusinessCrud';
import {Key, ProCard} from '@ant-design/pro-components';
import {QueryParams} from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/data';
import {connect} from "@@/exports";
import {StateType} from "@/pages/DataStudio/model";

const DataSourceDetail = (props : any) => {
  const navigate = useNavigate();


  const {dataSource, backClick , dispatch ,database: { dbData , selectDatabaseId , expandKeys, selectKeys}} = props;
  const [loading, setLoading] = useState<boolean>(false);
  const [disabled, setDisabled] = useState<boolean>(true);
  const [treeData, setTreeData] = useState<Partial<any>[]>([]);
  const [tableInfo, setTableInfo] = useState<Partial<DataSources.Table>>({});
  const [params, setParams] = useState<QueryParams>({id: 0, schemaName: '', tableName: ''});
  const selectDb = (dbData as DataSources.DataSource[]).filter(x=> x.id === selectDatabaseId)[0]

  const handleBackClick = () => {
    // go back
    navigate('/registration/database', {state: {from: `/registration/database/detail/${dataSource.id}`}});
    // back click callback
    backClick();
  };

  const clearState = () => {
    setDisabled(true);
    setTableInfo({});
    setParams({
      id: 0,
      schemaName: '',
      tableName: ''
    });
  };


  const querySchemaTree = useCallback(async () => {
    clearState();
    setLoading(true);
    await getDataByIdReturnResult(API_CONSTANTS.DATASOURCE_GET_SCHEMA_TABLES, dataSource.id).then((res) => {
      if (res.code === RESPONSE_CODE.SUCCESS) {
        setTreeData(res.datas);
      }
    });
    setLoading(false);
  }, [dataSource.id]);


  useEffect(() => {
    querySchemaTree();
  }, []);

  /**
   * tree node click
   */
  const onSchemaTreeNodeClick = useCallback(async (keys: Key[] ,info: any) => {
    const {node: {isLeaf, parentId: schemaName, name: tableName, fullInfo}} = info;
    // 选中的key
    dispatch({
      type: "Studio/updateDatabaseSelectKey",
      payload: keys
    })
    if (isLeaf) {
      const queryParams =  {id: selectDatabaseId , schemaName, tableName};
      dispatch({
        type: "Studio/addTab",
        payload: {
          icon: selectDb.type,
          id: selectDatabaseId + schemaName + tableName,
          breadcrumbLabel: [selectDb.type,selectDb.name].join("/"),
          label: schemaName + '.' + tableName ,
          params:{ queryParams: queryParams, tableInfo: fullInfo},
          type: "metadata"
        }
      })
    }
    if (isLeaf) {
      setParams({
        id: dataSource.id as number,
        schemaName,
        tableName
      });
      setDisabled(false);
      /**
       * get table columns
       */

      setTableInfo(fullInfo);
    } else {
      clearState();
    }
  }, []);


  /**
   * 树节点展开事件
   * @param {Key[]} expandedKeys
   */
  const handleTreeExpand = (expandedKeys: Key[]) => {
    dispatch({
      type: "Studio/updateDatabaseExpandKey",
      payload: expandedKeys
    })
  }


  /**
   * render back button and refresh button
   * @return {JSX.Element}
   */
  const renderBackButton = () => {
    return <>
      <DataSourceDetailBackButton>
        <Space size={'middle'}>
          <Button size={'middle'} icon={<ReloadOutlined spin={loading}/>} type="primary"
                  onClick={() => querySchemaTree()}>{l('button.refresh')}</Button>
          <Button size={'middle'} icon={<BackwardOutlined/>} type="primary"
                  onClick={handleBackClick}>{l('button.back')}</Button>
        </Space>
      </DataSourceDetailBackButton>
    </>;
  };

  /**
   * render
   */
  return <>
    <ProCard loading={loading} ghost gutter={[16, 16]} split="vertical">
      <ProCard hoverable bordered className={'siderTree schemaTree'} colSpan="16%">
        {/* tree */}
        <SchemaTree selectKeys={selectKeys} expandKeys={expandKeys} onExpand={handleTreeExpand} onNodeClick={onSchemaTreeNodeClick} treeData={treeData}/>
      </ProCard>
      <ProCard hoverable colSpan="84%" ghost headerBordered>
        {/* tags */}
        <RightTagsRouter
          tableInfo={tableInfo}
          queryParams={params}
          rightButtons={renderBackButton()}
          tagDisabled={disabled}
        />
      </ProCard>
    </ProCard>
  </>;
};

export default connect(({Studio}: { Studio: StateType }) => ({
  database: Studio.database,
}))(DataSourceDetail); ;
