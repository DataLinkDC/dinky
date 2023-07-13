import {connect} from "@umijs/max";
import {DataBaseType, StateType} from "../../model";
import {Button, Col, Row, Spin, Tag, Tooltip} from "antd";
import React, {useState} from "react";
import {
  DatabaseOutlined, ReloadOutlined,
  TableOutlined
} from "@ant-design/icons";
import {clearMetaDataTable, showMetaDataTable} from "./service";
import {l} from "@/utils/intl";
import {ProFormSelect} from "@ant-design/pro-components";
import {TagAlignLeft} from "@/components/StyledComponents";
import SchemaTree from "@/pages/RegCenter/DataSource/components/DataSourceDetail/SchemaTree";

const MetaData = (props: any) => {

  const {dispatch} = props;
  const database: DataBaseType[] = props.database;
  const [databaseId, setDatabaseId] = useState<number>(0);
  const [treeData, setTreeData] = useState<[]>([]);
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
  const refreshDataBase = (value: number) => {
    if (!databaseId) return;
    setLoadingDatabase(true);
    clearMetaDataTable(databaseId).then(result => {
      onChangeDataBase(databaseId);
    })
  };
  const getDataBaseOptions = () => {
    return database.map(({id, name, type, enabled}) => (
      {
        label: <TagAlignLeft> <Tag color={enabled ? "processing" : "error"}>{type}</Tag>{name}</TagAlignLeft>
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
                    onClick={() => refreshDataBase(databaseId)}
            />
          </Tooltip>
        </Col>
      </Row>
      <Row>
        <Col span={24} style={{height: "36px"}}>
          <ProFormSelect
            allowClear={false}
            placeholder={l('pages.metadata.selectDatabase')}
            options={getDataBaseOptions()}
            fieldProps={{
              onChange: (e) => {
                if (e) {
                  onChangeDataBase(e)
                }
              }
            }}
          />
        </Col>
      </Row>

      <div style={{height: (props.toolContentHeight-64-4),backgroundColor:`#fff`}}>
        <SchemaTree onNodeClick={async (info: any) => {
          const {node: {isLeaf, parentId: schemaName, name: tableName, fullInfo}} = info;
          if (isLeaf) {
           const queryParams =  {
              id: databaseId ,
              schemaName,
              tableName
            };

            dispatch({
              type: "Studio/addTab",
              payload: {id:databaseId+schemaName+tableName,label: schemaName+"\\" + tableName , params:{queryParams:queryParams,tableInfo:fullInfo},type:"metadata"}
            })
          }

        }} treeData={treeData}/>
      </div>

    </Spin>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  toolContentHeight:Studio.toolContentHeight,
  database: Studio.database,
}))(MetaData);
