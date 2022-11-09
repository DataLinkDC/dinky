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


import {Button, Empty, message, Modal, Tabs, Tooltip} from "antd";
import {SearchOutlined, SnippetsOutlined} from "@ant-design/icons";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {getLineage, getStreamGraph} from "@/pages/DataStudio/service";
import {useState} from "react";
import Lineage from "@/components/Lineage";
import CodeShow from "@/components/Common/CodeShow";
import {DIALECT} from "@/components/Studio/conf";
import {l} from "@/utils/intl";

const {TabPane} = Tabs;

const StudioCA = (props: any) => {
  const {current} = props;
  const [data, setData] = useState(undefined);

  const handleLineage = () => {
    setData(undefined);
    const res = getLineage({
      statement: current.value,
      statementSet: current.task.statementSet,
      dialect: current.task.dialect,
      databaseId: current.task.databaseId,
      type: 1,
    });
    res.then((result) => {
      if (result.datas) {
        setData(result.datas);
      } else {
        message.error(`获取作业血缘失败，原因：\n${result.msg}`);
      }
    })
  };

  const handleExportStreamGraphPlan = () => {
    const res = getStreamGraph({
      ...current.task,
      configJson: JSON.stringify(current.task.config),
      statement: current.value,
    });
    res.then((result) => {
      Modal.info({
        title: current.task.alias + '的 StreamGraphPlan',
        width: 1000,
        content: (
          <CodeShow code={JSON.stringify((result.datas ? result.datas : result.msg), null, "\t")} language='json'
                    height='500px' theme="vs-dark"/>
        ),
        onOk() {
        },
      });
    })
  };

  return (<>
    <Tabs defaultActiveKey="Lineage" size="small" tabPosition="top" style={{border: "1px solid #f0f0f0"}}
          tabBarExtraContent={
            <>
              <Tooltip title="重新计算血缘">
                <Button
                  type="text"
                  icon={<SearchOutlined/>}
                  onClick={handleLineage}
                >
                  计算血缘
                </Button>
              </Tooltip>
              {(!current.task.dialect || current.task.dialect == DIALECT.FLINKSQL) ?
                <Tooltip title="导出 StreamGraphPlan">
                  <Button
                    type="text"
                    icon={<SnippetsOutlined/>}
                    onClick={handleExportStreamGraphPlan}
                  >
                    StreamGraphPlan
                  </Button>
                </Tooltip> : undefined
              }
            </>}
    >
      <TabPane tab={<span>血缘分析</span>} key="Lineage">
        {data ? <Lineage datas={data}/> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
      </TabPane>
    </Tabs>
  </>)
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioCA);
