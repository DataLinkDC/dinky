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
import { isSql } from '@/pages/DataStudio/HeaderContainer/function';
import { BtnRoute, useTasksDispatch } from '@/pages/DataStudio/LeftContainer/BtnContext';
import { TableDataNode } from '@/pages/DataStudio/LeftContainer/Catalog/data';
import { StateType } from '@/pages/DataStudio/model';
import SchemaDesc from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SchemaDesc';
import { DIALECT } from '@/services/constants';
import { l } from '@/utils/intl';
import {
  AppstoreOutlined,
  BlockOutlined,
  DownOutlined,
  FunctionOutlined,
  TableOutlined
} from '@ant-design/icons';
import { connect } from '@umijs/max';
import { Button, Col, Empty, Modal, Row, Select, Spin } from 'antd';
import { DataNode } from 'antd/es/tree';
import DirectoryTree from 'antd/es/tree/DirectoryTree';
import { DefaultOptionType } from 'rc-select/lib/Select';
import React, { useEffect, useState } from 'react';
import { getMSCatalogs, getMSColumns, getMSSchemaInfo } from './service';

const Catalog: React.FC = (props: connect) => {
  const { tabs } = props;
  const currentData = getCurrentData(tabs.panes, tabs.activeKey);
  if (!currentData) {
    return <Empty description={l('pages.datastudio.catalog.selectDatasource')} />;
  }
  const dialect = currentData?.dialect.toLowerCase() ?? '';
  const fragment = currentData?.fragment ?? true;
  let envId: number | undefined;
  let databaseId: number | undefined;
  let engine: string | undefined;
  if (dialect === DIALECT.FLINKSQLENV) {
    envId = currentData?.id;
    engine = 'Flink';
  } else if (dialect === DIALECT.FLINK_SQL) {
    envId = currentData?.envId;
    engine = 'Flink';
  } else if (isSql(dialect)) {
    databaseId = currentData?.databaseId;
    if (!databaseId) {
      return <Empty description={l('pages.datastudio.catalog.openMission')} />;
    }
  }
  envId = envId ?? -1;
  const [catalogSelect, setCatalogSelect] = useState<DefaultOptionType[]>([]);
  const [catalog, setCatalog] = useState<string>('default_catalog');
  const [database, setDatabase] = useState<string>('');
  const [table, setTable] = useState<string>('');
  const [treeData, setTreeData] = useState<DataNode[]>([]);
  const [modalVisit, setModalVisit] = useState(false);
  const [row, setRow] = useState<TableDataNode>();
  const [loading, setLoading] = useState<boolean>(false);
  const [columnData, setColumnData] = useState([]);
  const btnDispatch = useTasksDispatch();
  const currentTabName = 'menu.datastudio.catalog';
  const btnEvent = [...BtnRoute[currentTabName]];

  btnEvent[0].onClick = () => {
    refreshMetaStoreTables();
  };
  btnDispatch({
    type: 'change',
    selectKey: currentTabName,
    payload: btnEvent
  });

  useEffect(() => {
    getCatalogs();
  }, [envId, databaseId]);

  useEffect(() => {
    if (table) {
      setLoading(true);
      setColumnData([]);
      getMSColumns({
        envId,
        catalog,
        database,
        table,
        dialect,
        databaseId
      })
        .then((res) => {
          setLoading(false);
          setColumnData(res);
        })
        .catch(() => {});
    }
  }, [table]);

  const onRefreshTreeData = (catalogAndDatabase: string) => {
    setTreeData([]);
    setLoading(true);
    const names = catalogAndDatabase.split('.');
    let catalogTmp = 'default_catalog';
    let databaseTmp = 'default_database';
    if (names.length > 1) {
      catalogTmp = names[0];
      databaseTmp = names[1];
    } else if (names.length == 1) {
      databaseTmp = names[0];
    }
    setCatalog(catalogTmp);
    setDatabase(databaseTmp);
    let param = {
      envId: envId,
      fragment: fragment,
      dialect: dialect,
      catalog: catalogTmp,
      database: databaseTmp,
      databaseId
    };
    const result = getMSSchemaInfo(param);
    result
      .then((res) => {
        setLoading(false);
        const tables: any[] = [];
        if (res.tables) {
          for (let i = 0; i < res.tables.length; i++) {
            tables.push(res.tables[i]);
          }
        }
        const treeDataTmp: DataNode[] = [];
        const tablesData: TableDataNode[] = [];
        for (const t of tables) {
          tablesData.push({
            title: t.name,
            key: t.name,
            icon: <TableOutlined />,
            isLeaf: true,
            isTable: true,
            name: t.name,
            schema: databaseTmp,
            catalog: catalog,
            comment: t.comment,
            type: t.type,
            engine: engine ?? t.engine,
            options: t.options,
            rows: t.rows,
            createTime: t.createTime,
            updateTime: t.updateTime
          });
        }
        treeDataTmp.push({
          title: 'tables',
          key: 'tables',
          children: tablesData
        });

        const viewsData: DataNode[] = [];
        if (res.views) {
          for (let i = 0; i < res.views.length; i++) {
            viewsData.push({
              title: res.views[i],
              key: res.views[i],
              icon: <BlockOutlined />,
              isLeaf: true
            });
          }
        }
        treeDataTmp.push({
          title: 'views',
          key: 'views',
          children: viewsData
        });

        const functionsData: DataNode[] = [];
        if (res.functions) {
          for (let i = 0; i < res.functions.length; i++) {
            functionsData.push({
              title: res.functions[i],
              key: res.functions[i],
              icon: <FunctionOutlined />,
              isLeaf: true
            });
          }
        }
        treeDataTmp.push({
          title: 'functions',
          key: 'functions',
          children: functionsData
        });

        const userFunctionsData: DataNode[] = [];
        if (res.userFunctions) {
          for (let i = 0; i < res.userFunctions.length; i++) {
            userFunctionsData.push({
              title: res.userFunctions[i],
              key: res.userFunctions[i],
              icon: <FunctionOutlined />,
              isLeaf: true
            });
          }
        }
        treeDataTmp.push({
          title: 'user functions',
          key: 'userFunctions',
          children: userFunctionsData
        });

        const modulesData: DataNode[] = [];
        if (res.modules) {
          for (let i = 0; i < res.modules.length; i++) {
            modulesData.push({
              title: res.modules[i],
              key: res.modules[i],
              icon: <AppstoreOutlined />,
              isLeaf: true
            });
          }
        }
        treeDataTmp.push({
          title: 'modules',
          key: 'modules',
          children: modulesData
        });

        setTreeData(treeDataTmp);
      })
      .catch(() => {});
  };

  const getCatalogs = () => {
    if (envId || databaseId) {
      setLoading(true);
      setTreeData([]);
      setCatalogSelect([]);
      setDatabase('');
      let param = {
        envId: envId,
        fragment: fragment,
        dialect: dialect,
        databaseId
      };
      getMSCatalogs(param)
        .then((d) => {
          setCatalogSelect(
            (d as any[]).map((item) => {
              setLoading(false);
              return {
                label: item.name,
                options: (item.schemas as any[]).map((schema) => {
                  return {
                    label: schema.name,
                    value: item.name + '.' + schema.name
                  };
                })
              };
            })
          );
        })
        .catch(() => {});
    }
  };

  const refreshMetaStoreTables = () => {
    if (database) {
      onRefreshTreeData(catalog + '.' + database);
    } else {
      getCatalogs();
    }
  };

  const onChangeMetaStoreCatalogs = (value: string) => {
    onRefreshTreeData(value);
  };

  const openColumnInfo = (node: TableDataNode) => {
    if (node && node.isLeaf && node.isTable) {
      setTable(node.name);
      setRow(node);
      setModalVisit(true);
    }
  };

  const cancelHandle = () => {
    setRow(undefined);
    setModalVisit(false);
  };

  return (
    <Spin spinning={loading}>
      <div style={{ paddingInline: 10, paddingBlock: 5 }}>
        <Row style={{ paddingBlock: 10 }}>
          <Col span={24}>
            <Select
              value={database ? database : null}
              style={{ width: '100%' }}
              placeholder={l('pages.datastudio.catalog.catalogSelect')}
              optionLabelProp='label'
              onChange={onChangeMetaStoreCatalogs}
              options={catalogSelect}
            />
          </Col>
        </Row>

        {treeData.length > 0 ? (
          <DirectoryTree
            showIcon
            switcherIcon={<DownOutlined />}
            treeData={treeData}
            onRightClick={({ node }: any) => openColumnInfo(node)}
            onSelect={(_, info: any) => openColumnInfo(info.node)}
          />
        ) : (
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
        )}
      </div>
      <Modal
        title={<>{row?.key}</> ?? <></>}
        open={modalVisit}
        width={'85%'}
        onCancel={() => {
          cancelHandle();
        }}
        footer={[
          <Button
            key='back'
            onClick={() => {
              cancelHandle();
            }}
          >
            {l('button.close')}
          </Button>
        ]}
      >
        <SchemaDesc tableInfo={row} tableColumns={columnData} />
      </Modal>
    </Spin>
  );
};
export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs
}))(Catalog);
