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
  PlusOutlined,
  DownOutlined
} from '@ant-design/icons';
import React from "react";
import {showMetaDataTable} from "@/components/Studio/StudioEvent/DDL";
import {convertToTreeData} from "@/components/Studio/StudioTree/Function";
import {getCatalogueTreeData} from "@/pages/FlinkSqlStudio/service";

const { DirectoryTree } = Tree;
const {Option} = Select;

const StudioMetaData = (props: any) => {

  const {database, dispatch} = props;
  const [databaseId, setDataBaseId] = useState<number>();
  const [treeData, setTreeData] = useState<[]>([]);

  const onRefreshTreeData = ()=>{
    if(!databaseId)return;
    const res = showMetaDataTable(databaseId);
    res.then((result) => {
      let tables = result.datas;
      for(let i=0;i<tables.length;i++){
        tables[i].children=tables[i].tables;
        for(let j=0;j<tables[i].children.length;j++){
          tables[i].children[j].title=tables[i].children[j].name;
          tables[i].children[j].key=tables[i].children[j].name;
        }
        tables[i].title=tables[i].name;
        tables[i].key=tables[i].name;
      }
      setTreeData(result.datas);
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
      <div style={{float: "right"}}>
        <Tooltip title="刷新元数据表">
          <Button
            type="text"
            icon={<ReloadOutlined/>}
            onClick={onRefreshTreeData}
          />
        </Tooltip>
      </div>
      <DirectoryTree
        multiple
        switcherIcon={<DownOutlined/>}
        treeData={treeData}
        height={400}
      />
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  database: Studio.database,
}))(StudioMetaData);
