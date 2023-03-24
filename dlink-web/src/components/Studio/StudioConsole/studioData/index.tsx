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


import { Input, Space } from "antd";



const { Search } = Input;

import { StateType } from "@/pages/DataStudio/model";
import { registerWatchTable, unRegisterWatchTable } from "@/pages/DataStudio/service";
import { connect } from "umi";
import { useEffect, useState, useRef } from "react";
// import { con, subscribe, close, stompClient } from "@/utils/stompClient";
import stompClient from "@/utils/stompClient"
import { Scrollbars } from 'react-custom-scrollbars';
import CodeShow from "@/components/Common/CodeShow";

// import {l} from "@/utils/intl";

const StudioData = (props: any) => {

  const { height, isActive } = props;
  const [consoleInfo, setConsoleInfo] = useState<string>("");//显示的code内容

  const [tableName, setTableName] = useState<string>("Orders");//传入的table名称
  const preTableNameRef = useRef(tableName)
  const preTableName = preTableNameRef.current
  let consoleHeight = (height - 37.6);
  const id = Number(localStorage.getItem('dlink-tenantId'));//用户id


  const onSearchName = (value: string) => {
    //表格名称发生变化 取消上次订阅
    console.log("test", id, preTableName);
    console.log("test", id, value);
    //取消上次订阅
    unRegisterWatchTable({ id, table: preTableName }).then(res => {
      setConsoleInfo("")
      stompClient.unsubscribe();

      registerWatchTable({ id, table: value }).then(res => {
        stompClient.subObj = stompClient.mqClient.subscribe(res.msg, (res) => {
          setConsoleInfo(consoleInfo + res.body)
        })
      })

    })
   
    //注册订阅

    //修改订阅




    setTableName(value)
  };


  // useEffect(() => {
  //   //建立链接 切换tab关闭链接
  //   con()
  //   return () => {
  //     close();
  //   }
  // }, [isActive])
  // useEffect(() => {
  //   //注册订阅
  //   registerWatchTable({ id, table: tableName }).then(res => {
  //    //订阅 （res.msg 返回订阅topic）
  //     subscribe(res.msg).then(res => {
  //       //更新info
  //       setConsoleInfo(consoleInfo  + res as string)
  //     })


  //   })

  // }, [tableName, consoleInfo]);
  useEffect(() => {
    stompClient.con();
    //取消订阅

  }, [isActive])
  return (<div style={{ width: '100%' }}>

    <Space direction="horizontal" style={{ margin: 10 }}>

      <Search placeholder="请输入表名" onSearch={onSearchName} style={{ width: 200 }} />
    </Space>
    <Scrollbars style={{ height: consoleHeight }}>
      {/* <CodeShow code={consoleInfo} language='Markdown' height={height} theme="vs" /> */}
      <CodeShow code={JSON.stringify((consoleInfo ? consoleInfo : ""), null, "\t")} language='json'
        height={height} theme="vs-dark" />
    </Scrollbars>


  </div>)


};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioData);
