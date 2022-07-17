import {Button, Col, Empty, message, Modal, Row, Select, Tabs, Tooltip, Tree} from "antd";
import {MetaStoreTableType, StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import React, {useState} from "react";
import {
  CodepenOutlined,
  DownOutlined,
  OrderedListOutlined,
  ReloadOutlined,
  TableOutlined
} from '@ant-design/icons';
import {Scrollbars} from 'react-custom-scrollbars';
import Columns from "@/pages/DataBase/Columns";
import Tables from "@/pages/DataBase/Tables";
import {TreeDataNode} from "@/components/Studio/StudioTree/Function";
import Generation from "@/pages/DataBase/Generation";
import {getMSTables} from "@/pages/DataStudio/service";
import {Dispatch} from "@@/plugin-dva/connect";

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
    const result = getMSTables(param);
    result.then(res => {
      const tables: MetaStoreTableType[] = [];
      if (res.datas) {
        for (let i = 0; i < res.datas.length; i++) {
          tables.push({
            name: res.datas[i].name,
            columns: res.datas[i].columns,
          });
        }
      }
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
        })
      }
      setTreeData(tablesData);
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
    if (node.isLeaf) {
      setRow(node);
      setModalVisit(true);
    }
  }

  const cancelHandle = () => {
    setRow(undefined);
    setModalVisit(false);
  }

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
            关闭
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
            {row ? <Columns dbId={current.task.databaseId} schema={row.database} table={row.name}/> :
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
            {row ? <Generation dbId={current.task.databaseId} schema={row.database} table={row.name}/> :
              <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
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
