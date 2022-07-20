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


import {Typography, Divider, Badge, Empty,Tag} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {FireOutlined, ScheduleOutlined} from '@ant-design/icons';
import StudioSqlConfig from "@/components/Studio/StudioRightTool/StudioSqlConfig";
import {DIALECT, isSql} from "@/components/Studio/conf";

const { Title, Paragraph, Text, Link } = Typography;

const StudioMsg = (props:any) => {

  const {current} = props;

  const renderCommonSqlContent = () => {
    return (<>
      <Paragraph>
        <blockquote> <Divider type="vertical"/>{current.console.result.startTime}
          <Divider type="vertical"/>{current.console.result.endTime}
          <Divider type="vertical"/>
          {!(current.console.result.success) ? <><Badge status="error"/><Text type="danger">Error</Text></> :
            <><Badge status="success"/><Text type="success">Success</Text></>}
          <Divider type="vertical"/>
        </blockquote>
        {current.console.result.statement && (<pre style={{height: '100px'}}>{current.console.result.statement}</pre>)}
        {current.console.result.error && (<pre style={{height: '100px'}}>{current.console.result.error}</pre>)}
      </Paragraph>
    </>)
  };

  const renderFlinkSqlContent = () => {
    return (<>
      <Paragraph>
        <blockquote><Link href={`http://${current.console.result.jobConfig?.address}`} target="_blank">
          [{current.console.result.jobConfig?.session}:{current.console.result.jobConfig?.address}]
        </Link> <Divider type="vertical"/>{current.console.result.startTime}
          <Divider type="vertical"/>{current.console.result.endTime}
          <Divider type="vertical"/>
          {!(current.console.result.status==='SUCCESS') ? <><Badge status="error"/><Text type="danger">Error</Text></> :
            <><Badge status="success"/><Text type="success">Success</Text></>}
          <Divider type="vertical"/>
          {current.console.result.jobConfig?.jobName&&<Text code>{current.console.result.jobConfig?.jobName}</Text>}
          {current.console.result.jobId&&
            (<>
              <Divider type="vertical"/>
              <Tag color="blue" key={current.console.result.jobId}>
                <FireOutlined /> {current.console.result.jobId}
              </Tag>
            </>)}
        </blockquote>
        {current.console.result.statement && (<pre style={{height: '100px'}}>{current.console.result.statement}</pre>)}
        {current.console.result.error && (<pre style={{height: '100px'}}>{current.console.result.error}</pre>)}
      </Paragraph>
    </>)
  };


  return (
    <Typography>
      {current?.task&&current.console.result.startTime?(isSql(current.task.dialect) ? renderCommonSqlContent():
        renderFlinkSqlContent() ):<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      }
    </Typography>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioMsg);
