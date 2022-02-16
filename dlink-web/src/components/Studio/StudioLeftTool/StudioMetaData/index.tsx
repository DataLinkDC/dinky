import {
  Tabs, Button,Tree, Empty, Select,Tag,
  Tooltip
} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {useState} from "react";
import styles from "./index.less";
import {
  TableOutlined,
  DatabaseOutlined,
  DownOutlined,
  OrderedListOutlined, CodepenOutlined
} from '@ant-design/icons';
import React from "react";
import {showMetaDataTable} from "@/components/Studio/StudioEvent/DDL";
import { Scrollbars } from 'react-custom-scrollbars';
import {
  ModalForm,
} from '@ant-design/pro-form';
import Columns from "@/pages/DataBase/Columns";
import Tables from "@/pages/DataBase/Tables";
import {TreeDataNode} from "@/components/Studio/StudioTree/Function";
import Generation from "@/pages/DataBase/Generation";

const { DirectoryTree } = Tree;
const {Option} = Select;
const { TabPane } = Tabs;

const StudioMetaData = (props: any) => {

  const {database,toolHeight, dispatch} = props;
  const [databaseId, setDatabaseId] = useState<number>();
  const [treeData, setTreeData] = useState<[]>([]);
  const [modalVisit, setModalVisit] = useState(false);
  const [row, setRow] = useState<TreeDataNode>();

  const onRefreshTreeData = (databaseId: number)=>{
    if(!databaseId)return;
    setDatabaseId(databaseId);
    const res = showMetaDataTable(databaseId);
    res.then((result) => {
      let tables = result.datas;
      if(tables) {
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
      }else{
        setTreeData([]);
      }
    });
  };

  const onChangeDataBase = (value: number)=>{
    onRefreshTreeData(value);
  };

  const getDataBaseOptions = ()=>{
    return <>{database.map(({ id, alias, type, enabled }) => (
      <Option value={id} label={<><Tag color={enabled ? "processing" : "error"}>{type}</Tag>{alias}</>}>
        <Tag color={enabled ? "processing" : "error"}>{type}</Tag>{alias}
      </Option>
    ))}</>
  };

  const openColumnInfo = (e: React.MouseEvent, node: TreeDataNode) => {
    if(node.isLeaf){
      setRow(node);
      setModalVisit(true);
    }
  }

  return (
    <>
      <Select
        // style={{width: '100%'}}
        placeholder="选择数据源"
        optionLabelProp="label"
        onChange={onChangeDataBase}
      >
        {getDataBaseOptions()}
      </Select>
      <Scrollbars style={{height: (toolHeight - 32)}}>
        {treeData.length>0?(
          <DirectoryTree
          showIcon
          switcherIcon={<DownOutlined/>}
          treeData={treeData}
          onRightClick={({event, node}: any) => {
            openColumnInfo(event, node)
          }}
        />):(<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)}
      </Scrollbars>
      <ModalForm
        title={row?.key}
        visible={modalVisit}
        width={1000}
        onFinish={async () => {
          // setRow(undefined);
          // setModalVisit(false);
        }}
        modalProps={{
          maskClosable:false,
          bodyStyle:{
            padding: '5px'
          }
        }}
        onVisibleChange={setModalVisit}
        submitter={{
          submitButtonProps: {
            style: {
              display: 'none',
            },
          },
        }}

      >
        <Tabs defaultActiveKey="tableInfo" size="small">
          <TabPane
            tab={
              <span>
          <TableOutlined />
          表信息
        </span>
            }
            key="tableInfo"
          >
            {row?<Tables table={row}/>:<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
          </TabPane>
          <TabPane
            tab={
              <span>
          <CodepenOutlined />
          字段信息
        </span>
            }
            key="columnInfo"
          >
            {row? <Columns dbId={databaseId} schema={row.schema} table={row.table}/> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
          </TabPane>
          <TabPane
            tab={
              <span>
          <OrderedListOutlined />
          SQL 生成
        </span>
            }
            key="sqlGeneration"
          >
            {row? <Generation dbId={databaseId} schema={row.schema} table={row.table}/> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
          </TabPane>
        </Tabs>
        </ModalForm>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  database: Studio.database,
  toolHeight: Studio.toolHeight,
}))(StudioMetaData);
