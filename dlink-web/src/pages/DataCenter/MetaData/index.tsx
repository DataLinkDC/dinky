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


import {PageContainer} from '@ant-design/pro-layout'; //
import styles from './index.less';
import {Button, Card, Col, Empty, Image, Row, Segmented, Spin, Tabs, Tag, Tree} from 'antd';
import React, {Key, useEffect, useState} from 'react';
import {clearMetaDataTable, showMetaDataTable} from '@/components/Studio/StudioEvent/DDL';
import {getData} from '@/components/Common/crud';
import {
  BarsOutlined,
  CheckCircleOutlined,
  ConsoleSqlOutlined,
  DatabaseOutlined,
  DownOutlined,
  ExclamationCircleOutlined,
  ReadOutlined,
  TableOutlined,
} from '@ant-design/icons';
import {Scrollbars} from 'react-custom-scrollbars';
import {TreeDataNode} from '@/components/Studio/StudioTree/Function';
import Tables from '@/pages/DataBase/Tables';
import Columns from '@/pages/DataBase/Columns';
import Divider from 'antd/es/divider';
import Generation from '@/pages/DataBase/Generation';
import TableData from '@/pages/DataCenter/MetaData/TableData';
import {FALLBACK, getDBImage} from "@/pages/DataBase/DB";
import Meta from "antd/lib/card/Meta";

const {DirectoryTree} = Tree;
const {TabPane} = Tabs;

const MetaDataContainer: React.FC<{}> = (props: any) => {


  let [database, setDatabase] = useState<[{
    id: number,
    name: string,
    alias: string,
    type: string,
    enabled: string,
    groupName: string,
    status: string,
    time: string
  }]>([{
    id: -1,
    name: '',
    alias: '',
    type: '',
    enabled: '',
    groupName: '',
    status: '',
    time: ''
  }]);
  const [databaseId, setDatabaseId] = useState<number>();
  const [treeData, setTreeData] = useState<{ tables: [], updateTime: string }>({tables: [], updateTime: "none"});
  const [row, setRow] = useState<TreeDataNode>();
  const [loadingDatabase, setloadingDatabase] = useState(false);
  const [tableChecked, setTableChecked] = useState(true);
  const [dataBaseChecked, setDatabaseChecked] = useState(false);

  const fetchDatabaseList = async () => {
    const res = getData('api/database/listEnabledAll');
    await res.then((result) => {
      database = result.datas;
    });
    setDatabase(database);
  };

  const fetchDatabase = async (databaseId: number) => {
    setloadingDatabase(true);
    setDatabaseId(databaseId);
    const res = showMetaDataTable(databaseId);
    await res.then((result) => {
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

        setTreeData({tables: tables, updateTime: result.time});
      } else {
        setTreeData({tables: [], updateTime: "none"});
      }
    });
    setloadingDatabase(false);
  };

  useEffect(() => {
    fetchDatabaseList();
  }, []);

  const onChangeDataBase = (value: string | number) => {
    onRefreshTreeData(Number(value));
    setRow(null);
  };

  const refeshDataBase = (value: string | number) => {
    setloadingDatabase(true);
    clearMetaDataTable(Number(databaseId)).then(result => {
      onChangeDataBase(Number(value));
    })
  };

  const showTableInfo = (selected: boolean, node: TreeDataNode) => {
    if (node.isLeaf) {
      setRow(node);
    }
  };

  const onRefreshTreeData = (databaseId: number) => {
    if (!databaseId) return;
    fetchDatabase(databaseId);
  };


  const buildDatabaseBar = () => {
    return database.map((item) => {
        return {
          label: (
            <Card className={styles.headerCard}
                  hoverable={false} bordered={false}>
              <Row>
                <Col span={11}>
                  <Image style={{float: "left", paddingRight: "10px"}}
                         height={50}
                         preview={false}
                         src={getDBImage(item.type)}
                         fallback={FALLBACK}
                  />
                </Col>
                <Col span={11}>
                  <div>
                    <p>{item.alias}</p>
                    <Tag color="blue" key={item.groupName}>
                      {item.groupName}
                    </Tag>
                    {(item.status) ?
                      (<Tag icon={<CheckCircleOutlined/>} color="success">
                        正常
                      </Tag>) :
                      <Tag icon={<ExclamationCircleOutlined/>} color="warning">
                        异常
                      </Tag>}
                  </div>
                </Col>
              </Row>
            </Card>
          ),
          value: item.id,
        }
      }
    )
  };

  const buildListTitle = () => {
    for (let item of database) {
      if (item.id == databaseId) {
        return (
          <div>
            <div style={{position: "absolute", right: "10px"}}>
              <Button type="link" size="small"
                      loading={loadingDatabase}
                      onClick={() => {
                        refeshDataBase(databaseId)
                        setTableChecked(true)
                      }}
              >刷新</Button>
            </div>
            <div>{item.alias}</div>
          </div>
        )
      }
    }
    return (<div>未选择数据库</div>)
  }


  return (
    <PageContainer title={false}>
      <div className={styles.headerBarContent}>
        <Segmented className={styles.headerBar}
                   options={buildDatabaseBar()}
                   onChange={onChangeDataBase}
        />
      </div>
      <div className={styles.container}>
        <Row gutter={24}>
          <Col span={4}>
            <Spin spinning={loadingDatabase} delay={500}>
              <Card
                type="inner"
                bodyStyle={{padding: 0}}
              >
                <Meta title={buildListTitle()}
                      className={styles.tableListHead}
                      description={"上次更新：" + treeData.updateTime}
                />
                {treeData.tables.length > 0 ? (
                  <Scrollbars style={{height: 800}}>
                    <DirectoryTree
                      showIcon
                      switcherIcon={<DownOutlined/>}
                      treeData={treeData.tables}
                      onSelect={(
                        selectedKeys: Key[],
                        {event, selected, node, selectedNodes, nativeEvent}
                      ) => {
                        showTableInfo(selected, node);
                        setTableChecked(false)
                      }}
                    />
                  </Scrollbars>
                ) : (
                  <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
                )}
              </Card>
            </Spin>
          </Col>

          <Col span={20}>
            <div>
              <div>
                <Tabs defaultActiveKey="describe">
                  <TabPane disabled={tableChecked}
                           tab={
                             <span>
                          <ReadOutlined/>
                          描述
                        </span>
                           }
                           key="describe"
                  >
                    <Divider orientation="left" plain>
                      表信息
                    </Divider>
                    {row ? (
                      <Tables table={row}/>
                    ) : (
                      <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
                    )}
                    <Divider orientation="left" plain>
                      字段信息
                    </Divider>
                    {row ? (
                      <Columns dbId={databaseId} schema={row.schema} table={row.table}/>
                    ) : (
                      <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
                    )}
                  </TabPane>

                  <TabPane disabled={tableChecked}
                           tab={
                             <span>
                          <BarsOutlined/>
                          数据查询
                        </span>
                           }
                           key="exampleData"
                  >
                    {row ? (
                      <TableData dbId={databaseId} schema={row.schema} table={row.table}/>
                    ) : (
                      <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
                    )}
                  </TabPane>

                  <TabPane disabled={tableChecked}
                           tab={
                             <span>
                          <ConsoleSqlOutlined/>
                          SQL 生成
                        </span>
                           }
                           key="sqlGeneration"
                  >
                    {row ? (
                      <Generation dbId={databaseId} schema={row.schema} table={row.table}/>
                    ) : (
                      <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
                    )}
                  </TabPane>

                </Tabs>
              </div>
            </div>
          </Col>
        </Row>
      </div>
    </PageContainer>
  );
};

export default MetaDataContainer;
