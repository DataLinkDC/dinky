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


import {Button, Col, Empty, message, Modal, Row, Select, Tabs, Tooltip, Tree} from "antd";
import {MetaStoreTableType, StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import React, {useState} from "react";
import {
  AppstoreOutlined,
  BlockOutlined,
  CodepenOutlined,
  DownOutlined,
  FunctionOutlined,
  OrderedListOutlined,
  ReloadOutlined,
  TableOutlined,
} from '@ant-design/icons';
import {Scrollbars} from 'react-custom-scrollbars';
import Columns from "@/pages/RegistrationCenter/DataBase/Columns";
import Tables from "@/pages/RegistrationCenter/DataBase/Tables";
import {TreeDataNode} from "@/components/Studio/StudioTree/Function";
import Generation from "@/pages/RegistrationCenter/DataBase/Generation";
import {getMSSchemaInfo} from "@/pages/DataStudio/service";
import {Dispatch} from "@@/plugin-dva/connect";
import {DIALECT} from "@/components/Studio/conf";
import FlinkColumns from "@/pages/Flink/FlinkColumns";
import {l} from "@/utils/intl";

const {DirectoryTree} = Tree;
const {Option, OptGroup} = Select;
const {TabPane} = Tabs;

const StudioMetaStore = (props: any) => {

  const {current, toolHeight} = props;
  const [catalog, setCatalog] = useState<string>();
  const [database, setDatabase] = useState<string>();
  const [treeData, setTreeData] = useState<[]>([]);
  const [modalVisit, setModalVisit] = useState(false);
  const [row, setRow] = useState<TreeDataNode>();

  const onRefreshTreeData = (catalogAndDatabase: string) => {
    if (!current?.task?.dialect || !catalogAndDatabase) {
      return;
    }
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
      envId: current.task.envId,
      fragment: current.task.fragment,
      dialect: current.task.dialect,
      databaseId: current.task.databaseId,
      catalog: catalogTmp,
      database: databaseTmp,
    };
    const result = getMSSchemaInfo(param);
    result.then(res => {
      const tables: MetaStoreTableType[] = [];
      if (res.datas.tables) {
        for (let i = 0; i < res.datas.tables.length; i++) {
          tables.push({
            name: res.datas.tables[i].name,
            columns: res.datas.tables[i].columns,
          });
        }
      }
      const treeDataTmp: [] = [];
      const tablesData: [] = [];
      for (let i = 0; i < tables.length; i++) {
        tablesData.push({
          name: tables[i].name,
          title: tables[i].name,
          key: tables[i].name,
          icon: <TableOutlined/>,
          isLeaf: true,
          catalog: catalogTmp,
          database: databaseTmp,
          isTable: true
        })
      }
      treeDataTmp.push({
        name: 'tables',
        title: 'tables',
        key: 'tables',
        catalog: catalogTmp,
        database: databaseTmp,
        children: tablesData,
      });

      const viewsData: [] = [];
      if (res.datas.views) {
        for (let i = 0; i < res.datas.views.length; i++) {
          viewsData.push({
            name: res.datas.views[i],
            title: res.datas.views[i],
            key: res.datas.views[i],
            icon: <BlockOutlined/>,
            isLeaf: true,
            catalog: catalogTmp,
            database: databaseTmp,
          });
        }
      }
      treeDataTmp.push({
        name: 'views',
        title: 'views',
        key: 'views',
        catalog: catalogTmp,
        database: databaseTmp,
        children: viewsData,
      });

      const functionsData: [] = [];
      if (res.datas.functions) {
        for (let i = 0; i < res.datas.functions.length; i++) {
          functionsData.push({
            name: res.datas.functions[i],
            title: res.datas.functions[i],
            key: res.datas.functions[i],
            icon: <FunctionOutlined/>,
            isLeaf: true,
            catalog: catalogTmp,
            database: databaseTmp,
          });
        }
      }
      treeDataTmp.push({
        name: 'functions',
        title: 'functions',
        key: 'functions',
        catalog: catalogTmp,
        database: databaseTmp,
        children: functionsData,
      });

      const userFunctionsData: [] = [];
      if (res.datas.userFunctions) {
        for (let i = 0; i < res.datas.userFunctions.length; i++) {
          userFunctionsData.push({
            name: res.datas.userFunctions[i],
            title: res.datas.userFunctions[i],
            key: res.datas.userFunctions[i],
            icon: <FunctionOutlined/>,
            isLeaf: true,
            catalog: catalogTmp,
            database: databaseTmp,
          });
        }
      }
      treeDataTmp.push({
        name: 'userFunctions',
        title: 'user functions',
        key: 'userFunctions',
        catalog: catalogTmp,
        database: databaseTmp,
        children: userFunctionsData,
      });

      const modulesData: [] = [];
      if (res.datas.modules) {
        for (let i = 0; i < res.datas.modules.length; i++) {
          modulesData.push({
            name: res.datas.modules[i],
            title: res.datas.modules[i],
            key: res.datas.modules[i],
            icon: <AppstoreOutlined/>,
            isLeaf: true,
            catalog: catalogTmp,
            database: databaseTmp,
          });
        }
      }
      treeDataTmp.push({
        name: 'modules',
        title: 'modules',
        key: 'modules',
        catalog: catalogTmp,
        database: databaseTmp,
        children: modulesData,
      });

      setTreeData(treeDataTmp);
      props.saveMetaStoreTable(current.key, catalogTmp, databaseTmp, tables);
      message.success(`刷新 Catalog 成功`);
    })
  };

  const refreshMetaStoreTables = () => {
    onRefreshTreeData(catalog + '.' + database);
  };

  const onChangeMetaStoreCatalogs = (value: number) => {
    onRefreshTreeData(value);
  };

  const getMetaStoreCatalogsOptions = () => {
    const itemList = [];
    if (current?.metaStore) {
      for (const item of current?.metaStore) {
        itemList.push(<OptGroup label={item.name}>
          {item.databases.map(({name}) => (
            <Option value={item.name + '.' + name} label={item.name + '.' + name}>{name}</Option>
          ))}
        </OptGroup>)
      }
    }
    return itemList;
  };

  const openColumnInfo = (e: React.MouseEvent, node: TreeDataNode) => {
    if (node.isLeaf && node.isTable) {
      setRow(node);
      setModalVisit(true);
    }
  };

  const cancelHandle = () => {
    setRow(undefined);
    setModalVisit(false);
  };

  return (
    <>
      <Row>
        <Col span={24}>
          <Tooltip title="刷新 Catalog">
            <Button
              type="text"
              icon={<ReloadOutlined/>}
              onClick={refreshMetaStoreTables}
            />
          </Tooltip>
        </Col>
      </Row>
      <Select
        style={{width: '95%'}}
        placeholder="选择 Catalog & Database"
        optionLabelProp="label"
        onChange={onChangeMetaStoreCatalogs}
      >
        {getMetaStoreCatalogsOptions()}
      </Select>
      <Scrollbars style={{height: (toolHeight - 32)}}>
        {treeData.length > 0 ? (
          <DirectoryTree
            showIcon
            switcherIcon={<DownOutlined/>}
            treeData={treeData}
            onRightClick={({event, node}: any) => {
              openColumnInfo(event, node)
            }}
          />) : (<Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)}
      </Scrollbars>
      <Modal
        title={row?.key}
        visible={modalVisit}
        width={1000}
        onCancel={() => {
          cancelHandle();
        }}
        footer={[
          <Button key="back" onClick={() => {
            cancelHandle();
          }}>
            {l('button.close')}
          </Button>,
        ]}
      >
        <Tabs defaultActiveKey="tableInfo" size="small">
          <TabPane
            tab={
              <span>
          <TableOutlined/>
          表信息
        </span>
            }
            key="tableInfo"
          >
            {row ? <Tables table={row}/> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
          </TabPane>
          <TabPane
            tab={
              <span>
          <CodepenOutlined/>
          字段信息
        </span>
            }
            key="columnInfo"
          >
            {row ?
              (current.task.dialect === DIALECT.FLINKSQL ?
                  <FlinkColumns envId={current.task.envId} catalog={row.catalog} database={row.database}
                                table={row.name}/>
                  : <Columns dbId={current.task.databaseId} schema={row.database} table={row.name}/>
              ) : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
          </TabPane>
          <TabPane
            tab={
              <span>
          <OrderedListOutlined/>
          SQL 生成
        </span>
            }
            key="sqlGeneration"
          >
            {row ?
              (current.task.dialect === DIALECT.FLINKSQL ?
                  undefined
                  : <Generation dbId={current.task.databaseId} schema={row.database} table={row.name}/>
              ) : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
          </TabPane>
        </Tabs>
      </Modal>
    </>
  );
};

const mapDispatchToProps = (dispatch: Dispatch) => ({
  saveMetaStoreTable: (activeKey: number, catalog: string, database: string, tables: MetaStoreTableType[]) => dispatch({
    type: "Studio/saveMetaStoreTable",
    payload: {
      activeKey,
      catalog,
      database,
      tables,
    },
  }),
});

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  toolHeight: Studio.toolHeight,
}), mapDispatchToProps)(StudioMetaStore);
