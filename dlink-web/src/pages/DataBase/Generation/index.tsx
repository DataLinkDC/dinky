import {Typography,Tabs} from 'antd';
import {useEffect, useState} from "react";
import {getData} from "@/components/Common/crud";

const {Paragraph} = Typography;
const { TabPane } = Tabs;

type SqlGeneration = {
  flinkSqlCreate?: string;
  sqlSelect?: string;
  sqlCreate?: string;
}
const Generation = (props: any) => {

  const {dbId,table,schema} = props;
  const [sqlGeneration, setSqlGeneration] = useState<SqlGeneration>({});

  const getSqlGeneration = async () => {
    const msg = await getData('api/database/getSqlGeneration', {id:dbId,schemaName:schema,tableName:table});
    setSqlGeneration(msg.datas);
  };

  useEffect(() => {
    getSqlGeneration();
  }, []);

  return (<>
    <Paragraph>
      <Tabs defaultActiveKey="FlinkDDL" size="small"  tabPosition="left" >
        <TabPane
          tab={
            <span>
          FlinkDDL
        </span>
          }
          key="FlinkDDL"
        >
          <Paragraph copyable={{text: sqlGeneration.flinkSqlCreate}}></Paragraph>
          <pre style={{height: '300px'}}>{sqlGeneration.flinkSqlCreate}</pre>
        </TabPane>
        <TabPane
          tab={
            <span>
          SELECT
        </span>
          }
          key="SQLSelect"
        >
          <Paragraph copyable={{text: sqlGeneration.sqlSelect}}></Paragraph>
          <pre style={{height: '300px'}}>{sqlGeneration.sqlSelect}</pre>
        </TabPane>
        <TabPane
          tab={
            <span>
          SQLDDL
        </span>
          }
          key="SQLDDL"
        >
          <Paragraph copyable={{text: sqlGeneration.sqlCreate}}></Paragraph>
          <pre style={{height: '300px'}}>{sqlGeneration.sqlCreate}</pre>
        </TabPane>
      </Tabs>

    </Paragraph></>)
};

export default Generation;
