import { PageContainer } from '@ant-design/pro-layout'; //

import styles from './index.less';
import { Row, Col, Tabs, Select, Tag, Empty, Tree, Spin } from 'antd';
import React, { Key, useEffect, useState } from 'react';
import { showMetaDataTable } from '@/components/Studio/StudioEvent/DDL';
import { Scrollbars } from 'react-custom-scrollbars';
import { getData } from '@/components/Common/crud';
import {
  BarsOutlined,
  ConsoleSqlOutlined,
  DatabaseOutlined,
  DownOutlined,
  ReadOutlined,
  TableOutlined,
} from '@ant-design/icons';
import { TreeDataNode } from '@/components/Studio/StudioTree/Function';
import Tables from '@/pages/DataBase/Tables';
import Columns from '@/pages/DataBase/Columns';
import Divider from 'antd/es/divider';
import Generation from '@/pages/DataBase/Generation';
import TableData from '@/pages/DataCenter/MetaData/TableData';
const { DirectoryTree } = Tree;
const { TabPane } = Tabs;

const Container: React.FC<{}> = (props: any) => {
  // const { dispatch} = props;
  let [database, setDatabase] = useState([
    {
      id: '',
      name: '',
      alias: '',
      type: '',
      enabled: '',
    },
  ]);
  const [databaseId, setDatabaseId] = useState<number>();
  const [treeData, setTreeData] = useState<[]>([]);
  const [row, setRow] = useState<TreeDataNode>();
  const [loadingDatabase, setloadingDatabase] = useState(false);

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
          tables[i].icon = <DatabaseOutlined />;
          tables[i].children = tables[i].tables;

          for (let j = 0; j < tables[i].children.length; j++) {
            tables[i].children[j].title = tables[i].children[j].name;
            tables[i].children[j].key = tables[i].name + '.' + tables[i].children[j].name;
            tables[i].children[j].icon = <TableOutlined />;
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
    setloadingDatabase(false);
  };

  useEffect(() => {
    fetchDatabaseList();
  }, []);

  const getDataBaseOptions = () => {
    return (
      <>
        {database.map(({ id, name, alias, type, enabled }) => (
          <Select.Option
            key={id}
            value={id}
            label={
              <>
                <Tag color={enabled ? 'processing' : 'error'}>{type}</Tag>
                {alias === '' ? name : alias}
              </>
            }
          >
            <Tag color={enabled ? 'processing' : 'error'}>{type}</Tag>
            {alias === '' ? name : alias}
          </Select.Option>
        ))}
      </>
    );
  };

  const onChangeDataBase = (value: number) => {
    onRefreshTreeData(value);
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

  return (
    <div className={styles.container}>
      <div>
        <>
          <Row gutter={24}>
            <Col span={4}>
              <Select
                style={{
                  width: '90%',
                }}
                placeholder="选择数据源"
                optionLabelProp="label"
                onChange={onChangeDataBase}
              >
                {getDataBaseOptions()}
              </Select>
              <Spin spinning={loadingDatabase} delay={500}>
                <Scrollbars
                  style={{
                    height: '90vh',
                  }}
                >
                  {treeData.length > 0 ? (
                    <DirectoryTree
                      showIcon
                      switcherIcon={<DownOutlined />}
                      treeData={treeData}
                      onSelect={(
                        selectedKeys: Key[],
                        { event, selected, node, selectedNodes, nativeEvent }
                      ) => {
                        showTableInfo(selected, node);
                      }}
                    />
                  ) : (
                    <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
                  )}
                </Scrollbars>
              </Spin>
            </Col>

            <Col span={20}>
              <div>
                <div>
                  <Tabs defaultActiveKey="describe">
                    <TabPane
                      tab={
                        <span>
                          <ReadOutlined />
                          描述
                        </span>
                      }
                      key="describe"
                    >
                      <Divider orientation="left" plain>
                        表信息
                      </Divider>
                      {row ? (
                        <Tables table={row} />
                      ) : (
                        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
                      )}
                      <Divider orientation="left" plain>
                        字段信息
                      </Divider>
                      {row ? (
                        <Columns dbId={databaseId} schema={row.schema} table={row.table} />
                      ) : (
                        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
                      )}
                    </TabPane>

                    <TabPane
                      tab={
                        <span>
                          <BarsOutlined />
                          数据查询
                        </span>
                      }
                      key="exampleData"
                    >
                      {row ? (
                        <TableData dbId={databaseId} schema={row.schema} table={row.table} rows={row.rows}/>
                      ) : (
                        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
                      )}
                    </TabPane>

                    <TabPane
                      tab={
                        <span>
                          <ConsoleSqlOutlined />
                          SQL 生成
                        </span>
                      }
                      key="sqlGeneration"
                    >
                      {row ? (
                        <Generation dbId={databaseId} schema={row.schema} table={row.table} />
                      ) : (
                        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
                      )}
                    </TabPane>

                    {/*<TabPane tab={<span><ForkOutlined />血缘关系</span>} key="--">*/}
                    {/*  开发ing*/}
                    {/*</TabPane>*/}
                  </Tabs>
                </div>
              </div>
            </Col>
          </Row>
        </>
      </div>
    </div>
  );
};

export default () => {
  return (
    <PageContainer>
      <Container />
    </PageContainer>
  );
};
