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

import {Input, Space} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {registerWatchTable, unRegisterWatchTable} from "@/pages/DataStudio/service";
import {connect} from "umi";
import {useEffect, useRef, useState} from "react";
import stompClientUtil from "@/utils/stompClientUtil"
import {Scrollbars} from 'react-custom-scrollbars';
import CodeShow from "@/components/Common/CodeShow";

const {Search} = Input;


const StudioData = (props: any) => {

  const {height, isActive} = props;
  const [consoleInfo, setConsoleInfo] = useState<string>("");
  const preConsoleInfo = useRef(consoleInfo)
  const [tableName, setTableName] = useState<string>("");
  const preTableNameRef = useRef(tableName)
  let consoleHeight = (height - 37.6);
  const id = Number(localStorage.getItem('dlink-tenantId'));

  const onSearchName = (value: string) => {
    unRegisterWatchTable({id, table: preTableNameRef.current}).then(res => {
      setConsoleInfo("")
      stompClientUtil.unsubscribe();

      registerWatchTable({id, table: value}).then(res => {
        stompClientUtil.subScription = stompClientUtil.stompClient.subscribe(res.msg, (res: any) => {
          preConsoleInfo.current = preConsoleInfo.current + "\n" + res.body
          if (preConsoleInfo.current.length > 1024 * 1024) {
            preConsoleInfo.current = preConsoleInfo.current.substring(1024, preConsoleInfo.current.length)
          }
          setConsoleInfo(preConsoleInfo.current);
        })
      })
    })

    setTableName(value)
  };

  useEffect(() => {
    stompClientUtil.connect();
  }, [isActive])

  return (<div style={{width: '100%'}}>
    <Space direction="horizontal" style={{margin: 10}}>
      <Search placeholder="table name" onSearch={onSearchName} style={{width: 200}}/>
    </Space>
    <Scrollbars style={{height: consoleHeight}}>
      <CodeShow code={consoleInfo} language='text' height={height} theme="vs-dark" />
    </Scrollbars>
  </div>)
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioData);
