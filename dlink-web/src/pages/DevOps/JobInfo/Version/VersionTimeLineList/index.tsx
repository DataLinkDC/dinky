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


import React, {useEffect, useState,} from "react";
import {BackTop, Button, Card, Radio, RadioChangeEvent, Timeline} from "antd";
import {queryData} from "@/components/Common/crud";
import moment from "moment";
import {TaskVersion} from "@/pages/DevOps/data";
import {CheckCircleOutlined, SyncOutlined} from "@ant-design/icons";
import {l} from "@/utils/intl";
// import {Scrollbars} from "react-custom-scrollbars";

const url = '/api/task/version';

const VersionTimeLineList = (props: any) => {
  const {job} = props;


  const [mode, setMode] = useState<'left' | 'alternate' | 'right'>('alternate');
  const [reverse, setReverse] = useState(false);
  const onChange = (e: RadioChangeEvent) => {
    setMode(e.target.value);
  };

  const [taskVersionData, setTaskVersionData] = useState<TaskVersion>();

  let taskId = job.instance.taskId;
  const getVersionData = () => {
    setTaskVersionData(undefined);
    const res = queryData(url, {taskId, sorter: {versionId: 'descend'}});
    res.then((result) => {
      setTaskVersionData(result.data);
    });
  };

  useEffect(() => {
    getVersionData();
  }, []);


  const handleClick = () => {
    setReverse(!reverse);
  };

  const style: React.CSSProperties = {
    height: 40,
    width: 40,
    lineHeight: '40px',
    borderRadius: 4,
    backgroundColor: '#1088e9',
    color: '#fff',
    textAlign: 'center',
    fontSize: 14,
  };

  const getTimelineForm = () => {
    let formList = [];
    let tempData = taskVersionData
    for (let key in tempData) {
      formList.push(
        <Timeline.Item dot={(<CheckCircleOutlined/>)}
                       label={moment(tempData[key].createTime).format("YYYY-MM-DD HH:mm:ss")} color="green">
          <p>{tempData[key].name}: Create Version 【{tempData[key].versionId}】
            {/*site 【{ moment(tempData[key].createTime).format("YYYY-MM-DD HH:mm:ss")}】*/}
          </p>
          <p>Execute Mode: 【{tempData[key].type}】</p>
        </Timeline.Item>
      )
    }
    return formList
  }


  function refresh() {
    getVersionData()
  }

  return (
    <div style={{
      marginTop: "20px",
    }}>
      <Button type="primary" style={{margin: "5px"}} onClick={handleClick}>
        {reverse ? "倒序" : "正序"}
      </Button>
      <Button type="primary" style={{margin: "5px"}} onClick={refresh}>
        {l('button.refresh')}
      </Button>
      <Radio.Group
        onChange={onChange}
        value={mode}
      >
        <Radio value="left">Left</Radio>
        <Radio value="right">Right</Radio>
        <Radio value="alternate">Alternate</Radio>
      </Radio.Group>
      <Card size="small" style={{width: "auto"}}>
        {/*<Scrollbars  style={{height: "450px"}} >*/}
        <br/><br/>
        <Timeline mode={mode} pending={moment().format("YYYY-MM-DD HH:mm:ss") + " Developing..."} reverse={reverse}
                  pendingDot={<SyncOutlined spin/>}>
          {getTimelineForm()}
        </Timeline>
        <BackTop>
          <div style={style}>Top</div>
        </BackTop>
        {/*</Scrollbars>*/}
      </Card>


    </div>
  )
};

export default VersionTimeLineList;
