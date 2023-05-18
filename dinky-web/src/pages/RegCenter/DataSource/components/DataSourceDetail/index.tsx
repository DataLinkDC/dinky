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
import {Loading} from '@/pages/Other/Loading';
import {Button, Col, Row, Space} from 'antd';
import {BackwardOutlined, ReloadOutlined} from '@ant-design/icons';
import {DataSourceDetailBackButton} from '@/components/StyledComponents';
import {l} from '@/utils/intl';
import SchemaTree from '@/pages/RegCenter/DataSource/components/DataSourceDetail/SchemaTree';
import RightTagsRouter from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter';
import {useNavigate} from '@umijs/max';
import {API_CONSTANTS, RESPONSE_CODE} from '@/services/constants';
import {getDataByIdReturnResult, queryDataByParams} from '@/services/BusinessCrud';

type DataSourceDetailProps = {
  dataSource: Partial<DataSources.DataSource>;
  backClick: () => void;
}
const DataSourceDetail: React.FC<DataSourceDetailProps> = (props) => {
  const navigate = useNavigate();


  const {dataSource, backClick} = props;
  const [loading, setLoading] = useState<boolean>(false);
  const [treeData, setTreeData] = useState<Partial<any>[]>([]);
  const [tableColumns, setTableColumns] = useState<Partial<DataSources.Column[]>>([]);
  const [tableInfo, setTableInfo] = useState<Partial<DataSources.Table>>({});

  const handleBackClick = () => {
    // go back
    navigate('/registration/database', {state: {from: `/registration/database/detail/${dataSource.id}`}});
    // back click callback
    backClick();
  };


  const querySchemaTree = useCallback(async () => {
    setTableColumns([]);
    setTableInfo({});
    setLoading(true);
    await getDataByIdReturnResult(API_CONSTANTS.DATASOURCE_GET_SCHEMA_TABLES, dataSource.id).then((res) => {
      if (res.code === RESPONSE_CODE.SUCCESS) {
        setTreeData(res.datas);
      }
    });
    setLoading(false);
  }, []);


  useEffect(() => {
    querySchemaTree();
  }, []);

  const onSchemaTreeNodeClick = useCallback(async (info: any) => {
    const {node: nodeInfo, node: {isLeaf, parentId, name, fullInfo}} = info;
    if (isLeaf) {
      console.log(nodeInfo, 'nodeInfo');
      const data = await queryDataByParams(API_CONSTANTS.DATASOURCE_GET_COLUMNS_BY_TABLE, {
        id: dataSource.id,
        schemaName: parentId,
        tableName: name
      });
      setTableColumns(data);
      setTableInfo(fullInfo);
    } else {
      setTableColumns([]);
      setTableInfo({});
    }
  }, []);


  return <>
    <DataSourceDetailBackButton>
      <Space size={'large'}>
        <Button size={'small'} icon={<ReloadOutlined spin={loading}/>} type="primary"
                onClick={() => querySchemaTree()}>{l('button.refresh')}</Button>
        <Button size={'small'} icon={<BackwardOutlined/>} type="primary"
                onClick={handleBackClick}>{l('button.back')}</Button>
      </Space>
    </DataSourceDetailBackButton>
    {
      loading ?
        <Loading loading={loading}/>
        :
        <>
          <Row>
              <Col span={4} className={'siderTree schemaTree'}>
                {/* tree */}
                <SchemaTree onNodeClick={(info: any) => onSchemaTreeNodeClick(info)} treeData={treeData}/>
              </Col>
              <Col span={20}>
                {/* tags */}
                <RightTagsRouter
                  tableInfo={tableInfo}
                  tableColumns={tableColumns}
                />
              </Col>
          </Row>
        </>
    }
  </>;
};

export default DataSourceDetail;
