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


import {Button, Col, Empty, Modal, Row, Select, Spin, Tabs, Tag, Tree} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import React, {useState} from "react";
import {CodepenOutlined, DatabaseOutlined, DownOutlined, OrderedListOutlined, TableOutlined} from '@ant-design/icons';
import {clearMetaDataTable, showMetaDataTable} from "@/components/Studio/StudioEvent/DDL";
import {Scrollbars} from 'react-custom-scrollbars';
import Columns from "@/pages/RegistrationCenter/DataBase/Columns";
import Tables from "@/pages/RegistrationCenter/DataBase/Tables";
import {TreeDataNode} from "@/components/Studio/StudioTree/Function";
import Generation from "@/pages/RegistrationCenter/DataBase/Generation";
import {l} from "@/utils/intl";

const {DirectoryTree} = Tree;
const {Option} = Select;
const {TabPane} = Tabs;

const StudioMetaData = (props: any) => {

  const {database, toolHeight, dispatch} = props;
  const [databaseId, setDatabaseId] = useState<number>();
  const [treeData, setTreeData] = useState<[]>([]);
  const [modalVisit, setModalVisit] = useState(false);
  const [row, setRow] = useState<TreeDataNode>();
  const [loadingDatabase, setloadingDatabase] = useState(false);

  const onRefreshTreeData = (databaseId: number) => {
    if (!databaseId) {
      setloadingDatabase(false);
      return;
    }
    setloadingDatabase(true);

    setDatabaseId(databaseId);
    const res = showMetaDataTable(databaseId);
    res.then((result) => {
      setloadingDatabase(false);
      let tables = result.datas;
      if (tables) {
        for (let i = 0; i < tables.length; i++) {
          tables[i].title = tables[i].name;
          tables[i].key = tables[i].name;
          tables[i].icon = <DatabaseOutlined/>;
          tables[i].children = tables[i].tables;
          for (let j = 0; j < tables[i].children.length; j++) {
            tables[i].children[j].title = tables[i].children[j].name;
            tables[i].children[j].key = tables[i].name + '.' + tables[i].children[j].name;
            tables[i].children[j].icon = <TableOutlined/>;
            tables[i].children[j].isLeaf = true;
            tables[i].children[j].schema = tables[i].name;
            tables[i].children[j].table = tables[i].children[j].name;
          }
        }
        setTreeData(tables);
      } else {
        setTreeData([]);
      }
    });
  };

  const onChangeDataBase = (value: number) => {
    onRefreshTreeData(value);
  };

  const getDataBaseOptions = () => {
    return <>{database.map(({id, name, alias, type, enabled}) => (
      <Option value={id}
              label={<><Tag color={enabled ? "processing" : "error"}>{type}</Tag>{alias === "" ? name : alias}</>}>
        <Tag color={enabled ? "processing" : "error"}>{type}</Tag>{alias === "" ? name : alias}
      </Option>
    ))}</>
  };

  const openColumnInfo = (e: React.MouseEvent, node: TreeDataNode) => {
    if (node.isLeaf) {
      setRow(node);
      setModalVisit(true);
    }
  }

  const cancelHandle = () => {
    setRow(undefined);
    setModalVisit(false);
  }
  const refeshDataBase = (value:number) => {
    if (!databaseId) return;
    setloadingDatabase(true);
    clearMetaDataTable(databaseId).then(result => {
      onChangeDataBase(databaseId);
    })
  };

  return (
    <Spin spinning={loadingDatabase} delay={500}>
      <Row>
        <Col span={18}>
          <Select
            style={{width: '90%'}}
            placeholder="选择数据源"
            optionLabelProp="label"
            onChange={onChangeDataBase}
          >
            {getDataBaseOptions()}
          </Select>
        </Col>
        <Col span={1}>
          <Button type="link"
                  onClick={() => {refeshDataBase(databaseId)}}
          >{l('button.refresh')}</Button>
        </Col>
      </Row>

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
            {row ? <Columns dbId={databaseId} schema={row.schema} table={row.table} scroll={{x: 1000}}/> :
              <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
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
            {row ? <Generation dbId={databaseId} schema={row.schema} table={row.table}/> :
              <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
          </TabPane>
        </Tabs>
      </Modal>
    </Spin>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  database: Studio.database,
  toolHeight: Studio.toolHeight,
}))(StudioMetaData);
