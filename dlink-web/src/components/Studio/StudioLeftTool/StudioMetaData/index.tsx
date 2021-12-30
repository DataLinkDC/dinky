import {
  message, Button,Tree, Empty, Select,Tag,
  Tooltip
} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {useState} from "react";
import styles from "./index.less";
import {
  ReloadOutlined,
  DatabaseOutlined,
  DownOutlined,
  TableOutlined, FireOutlined
} from '@ant-design/icons';
import React from "react";
import {showMetaDataTable} from "@/components/Studio/StudioEvent/DDL";
import { Scrollbars } from 'react-custom-scrollbars';
import {
  ModalForm,
} from '@ant-design/pro-form';
import ProDescriptions from "@ant-design/pro-descriptions";
import StudioPreview from "@/components/Studio/StudioConsole/StudioPreview";
import Columns from "@/pages/DataBase/Columns";
import {TreeDataNode} from "@/components/Studio/StudioTree/Function";

const { DirectoryTree } = Tree;
const {Option} = Select;

const StudioMetaData = (props: any) => {

  const {database,toolHeight, dispatch} = props;
  const [databaseId, setDataBaseId] = useState<number>();
  const [treeData, setTreeData] = useState<[]>([]);
  const [modalVisit, setModalVisit] = useState(false);
  const [row, setRow] = useState<TreeDataNode>();

  const onRefreshTreeData = ()=>{
    if(!databaseId)return;
    const res = showMetaDataTable(databaseId);
    res.then((result) => {
      let tables = result.datas;
      for(let i=0;i<tables.length;i++){
        tables[i].title=tables[i].name;
        tables[i].key=tables[i].name;
        tables[i].icon = <DatabaseOutlined />;
        tables[i].children=tables[i].tables;
        for(let j=0;j<tables[i].children.length;j++){
          tables[i].children[j].title=tables[i].children[j].name;
          tables[i].children[j].key=tables[i].name+'.'+tables[i].children[j].name;
          tables[i].children[j].icon=<TableOutlined />;
          tables[i].children[j].isLeaf=true;
          tables[i].children[j].schema=tables[i].name;
          tables[i].children[j].table=tables[i].children[j].name;
        }
      }
      setTreeData(tables);
    });
  };

  const onChangeDataBase = (value: number)=>{
    setDataBaseId(value);
    onRefreshTreeData();
  };

  const getDataBaseOptions = ()=>{
    let itemList = [];
    for (let item of database) {
      let tag = (<><Tag color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias}</>);
      itemList.push(<Option value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const openColumnInfo = (e: React.MouseEvent, node: TreeDataNode) => {
    console.log(node);
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
      <Tooltip title="刷新元数据">
        <Button
          type="text"
          icon={<ReloadOutlined/>}
          onClick={onRefreshTreeData}
        />
      </Tooltip>
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
        // title="新建表单"
        title={ (row?(row.key)+'的':'')+'字段信息'}
        visible={modalVisit}
        onFinish={async () => {
          // setRow(undefined);
          // setModalVisit(false);
        }}
        modalProps={{
          maskClosable:false
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
        {row?
        (<Columns dbId={databaseId} schema={row.schema} table={row.table}/>) : (<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)}
      </ModalForm>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  database: Studio.database,
  toolHeight: Studio.toolHeight,
}))(StudioMetaData);
