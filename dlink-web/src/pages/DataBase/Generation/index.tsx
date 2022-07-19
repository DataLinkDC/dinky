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
  }, [dbId,table,schema]);

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
