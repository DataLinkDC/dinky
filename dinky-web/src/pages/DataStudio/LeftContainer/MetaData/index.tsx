import {connect} from "@umijs/max";
import { StateType} from "../../model";
import {Select, Space, Spin, Tag} from "antd";
import React, {useEffect, useState} from "react";
import {
  DatabaseOutlined, ReloadOutlined,
  TableOutlined
} from "@ant-design/icons";
import {clearMetaDataTable, showMetaDataTable} from "./service";
import {l} from "@/utils/intl";
import {Key, ProForm, ProFormSelect} from "@ant-design/pro-components";
import {TagAlignLeft} from "@/components/StyledComponents";
import SchemaTree from "@/pages/RegCenter/DataSource/components/DataSourceDetail/SchemaTree";
import {DataSources} from "@/types/RegCenter/data";
import ContentScroll from "@/components/Scroll/ContentScroll";

const MetaData = (props: any) => {

  const {dispatch ,toolContentHeight,leftContainer, database: { dbData , selectDatabaseId , expandKeys, selectKeys} } = props;
  const [treeData, setTreeData] = useState<[]>([]);
  const [loadingDatabase, setLoadingDatabase] = useState(false);
  const selectDb = (dbData as DataSources.DataSource[]).filter(x=> x.id === selectDatabaseId)[0]

  /**
   * @description: 刷新树数据
   * @param {number} databaseId
   */
  const onRefreshTreeData = (databaseId: number) => {
    if (!databaseId) {
      setLoadingDatabase(false);
      return;
    }
    setLoadingDatabase(true);

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

  useEffect(()=>{
    if (selectDatabaseId){
      onRefreshTreeData(selectDatabaseId)
    }
  },[])

  /**
   * 数据库选择改变时间时 刷新树数据
   * @param {number} value
   */
  const onChangeDataBase = (value: number) => {
    onRefreshTreeData(value);
  };
  /**
   * 刷新数据库列表
   */
  const refreshDataBase = () => {
    if (!selectDatabaseId) return;
    setLoadingDatabase(true);
    clearMetaDataTable(selectDatabaseId).then(() => {
      onChangeDataBase(selectDatabaseId);
    })
  };

  /**
   * 构建数据库列表 下拉框
   * @returns {{label: JSX.Element, value: number, key: number}[]}
   */
  const getDataBaseOptions = () => {
    return dbData.map(({id, name, type, enabled,status}: DataSources.DataSource) => (
        {
          key: id,
          value: id,
          label: <TagAlignLeft><Tag key={id} color={enabled ? "processing" : "error"}>{type}</Tag>{name}</TagAlignLeft>,
          disabled: !enabled || !status
        }
    ))
  };
  /**
   * 树节点点击事件 添加tab页 并传递参数
   * @param keys
   * @param info
   * @returns {Promise<void>}
   */
  const handleTreeNodeClick = async (keys: Key[] ,info: any) => {
    // 选中的key
    dispatch({
        type: "Studio/updateDatabaseSelectKey",
        payload: keys
    })
    const {node: {isLeaf, parentId: schemaName, name: tableName, fullInfo}} = info;
    if (isLeaf) {
      const queryParams =  {id: selectDatabaseId , schemaName, tableName};
      dispatch({
        type: "Studio/addTab",
        payload: {
          icon: selectDb.type,
          id: selectDatabaseId + schemaName + tableName,
          breadcrumbLabel: [selectDb.type,selectDb.name].join("/"),
          label: schemaName + '.' + tableName ,
          params:{ queryParams: queryParams, tableInfo: fullInfo},
          type: "metadata"
        }
      })
    }
  };

  /**
   * 数据库选择改变事件
   * @param {number} databaseId
   */
  const handleSelectDataBaseId = (databaseId: number) => {
    dispatch({
      type: "Studio/updateSelectDatabaseId",
      payload: databaseId
    })
    onChangeDataBase(databaseId);
  }

  /**
   * 树节点展开事件
   * @param {Key[]} expandedKeys
   */
  const handleTreeExpand = (expandedKeys: Key[]) => {
    dispatch({
      type: "Studio/updateDatabaseExpandKey",
      payload: expandedKeys
    })
  }


  return (

    <Spin spinning={loadingDatabase} delay={500}>
      <ProForm style={{height: 40}} initialValues={{selectDb:selectDatabaseId}} submitter={false}>
        <ProFormSelect
            width={leftContainer.width - 45 }
            addonAfter={<ReloadOutlined spin={loadingDatabase} title={l('button.refresh')} onClick={() => refreshDataBase()} />}
            allowClear={false}
            name={"selectDb"}
            placeholder={l('pages.metadata.selectDatabase')}
            options={getDataBaseOptions()}
            fieldProps={{onSelect: (selectId) => handleSelectDataBaseId(selectId)}}
        />
      </ProForm>
        <SchemaTree selectKeys={selectKeys} expandKeys={expandKeys} style={{height: (toolContentHeight - 64 - 20 )}} onNodeClick={handleTreeNodeClick} treeData={treeData} onExpand={handleTreeExpand}/>
    </Spin>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  toolContentHeight:Studio.toolContentHeight,
  leftContainer: Studio.leftContainer,
  database: Studio.database,
}))(MetaData);
