import {connect} from "@umijs/max";
import {DataBaseType, StateType} from "../../model";
import {Button, Col, Empty, Modal, Row, Select, Spin, Tabs, Tag, Tooltip, Tree, TreeDataNode} from "antd";
import React, {useState} from "react";
import {
  AppstoreAddOutlined,
  CodepenOutlined,
  DatabaseOutlined, DownOutlined,
  OrderedListOutlined,
  ReloadOutlined,
  TableOutlined
} from "@ant-design/icons";
import {clearMetaDataTable, showMetaDataTable} from "./service";
import {l} from "@/utils/intl";
import {ProFormSelect} from "@ant-design/pro-components";
import DataSourceDetail from "@/pages/RegCenter/DataSource/components/DataSourceDetail";

const {DirectoryTree} = Tree;
const {Option} = Select;

const MetaData = (props: any) => {

  const {dispatch} = props;
  const database: DataBaseType[] = props.database;
  const [databaseId, setDatabaseId] = useState<number>(0);
  const [treeData, setTreeData] = useState<[]>([]);
  const [modalVisit, setModalVisit] = useState(false);
  const [row, setRow] = useState<TreeDataNode>();
  const [loadingDatabase, setLoadingDatabase] = useState(false);

  const onRefreshTreeData = (databaseId: number) => {
    if (!databaseId) {
      setLoadingDatabase(false);
      return;
    }
    setLoadingDatabase(true);

    setDatabaseId(databaseId);
    const res = showMetaDataTable(databaseId);
    res.then((tables) => {
      setLoadingDatabase(false);
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
  const refreshDataBase = (value: number) => {
    if (!databaseId) return;
    setLoadingDatabase(true);
    clearMetaDataTable(databaseId).then(result => {
      onChangeDataBase(databaseId);
    })
  };
  const tabItems = [
    {
      key: 'tableInfo',
      label: <span>
          <TableOutlined/>
        {l('pages.metadata.TableInfo')}
        </span>,
      children: row ? <DataSourceDetail backClick={()=>console.log()} dataSource={{"id":1}} /> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
      // {row ? <Tables table={row}/> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
    },
    {
      key: 'columnInfo',
      label: <span>
          <CodepenOutlined/>
        {l('pages.metadata.FieldInformation')}
        </span>,
      children: row ? <></> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
      // {/*{row ? <Columns dbId={databaseId} schema={row.schema} table={row.table} scroll={{x: 1000}}/> :*/}
    },
    {
      key: 'sqlGeneration',
      label: <span>
          <OrderedListOutlined/>
        {l('pages.metadata.GenerateSQL')}
        </span>,
      children: row ? <></> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
      // {/*{row ? <Generation dbId={databaseId} schema={row.schema} table={row.table}/> :*/}
    },

  ]
  const getDataBaseOptions = () => {
    return database.map(({id, name, type, enabled}) => (
      {
        label: <> <Tag style={{overflow:"initial",lineHeight:"initial"}} color={enabled ? "processing" : "error"}>{type}</Tag>{name}</>
        , value: id
      }
    ))
  };
  return (

    <Spin spinning={loadingDatabase} delay={500}>
      <Row>
        <Col span={24}>
          <Tooltip title={l('button.refresh')}>
            <Button type="text"
                    icon={<ReloadOutlined/>}
                    onClick={() => {
                      refreshDataBase(databaseId)
                    }}
            />
          </Tooltip>
        </Col>
      </Row>
      <Row>
        <Col span={24} style={{height:"36px"}}>
          <ProFormSelect
            allowClear={false}
            placeholder={l('pages.metadata.selectDatabase')}
            options={getDataBaseOptions()}
            fieldProps={{
              onChange: (e)=>{
                if (e){
                  // onChangeDataBase(e)
                  dispatch({
                    type:"Studio/addTab",
                    // payload:{label:e,children:<DataSourceDetail backClick={()=>console.log()} dataSource={{"id":e}}/> }
                    payload:{label:e,children:'123' }
                  })
                }
              }
            }}
          />
        </Col>
      </Row>

      <div style={{height: (500 - 32)}}>
        {treeData.length > 0 ? (
          <DirectoryTree
            showIcon
            switcherIcon={<DownOutlined/>}
            treeData={treeData}
            onRightClick={({event, node}: any) => {
              openColumnInfo(event, node)
            }}
          />) : (<Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)}
      </div>
      <Modal
        title={row?.key}
        open={modalVisit}
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
        <Tabs items={tabItems} defaultActiveKey="tableInfo" size="small">
        </Tabs>
      </Modal>
    </Spin>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  database: Studio.database,
}))(MetaData);
