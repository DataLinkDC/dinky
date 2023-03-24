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

 
  const [subscribeObj, setSubscribeObj] = useState<object>();//显示的code内容
  const [subscribeMsg, setSubscribeMsg] = useState<string>("");//显示的code内容
  const [tableName, setTableName] = useState<string>("Orders");//传入的table名称
  const preTableNameRef=useRef(tableName)
  const preTableName=preTableNameRef.current
  let consoleHeight = (height - 37.6);
  const id = Number(localStorage.getItem('dlink-tenantId'));//用户id


  const onSearchName = (value: string) => {
    //表格名称发生变化 取消上次订阅
    setConsoleInfo("")
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
  useEffect(()=>{
    stompClient.con();
    //取消订阅
    unRegisterWatchTable({id,table:preTableName}).then(res=>{
      stompClient.unsubscribe();
    })
    
  },isActive)
  //用户输入表发生变化重新注册
  useEffect(() => {
    debugger
    unRegisterWatchTable({id,table:preTableName}).then(res=>{
      stompClient.unsubscribe();
    })
    //注册订阅
    registerWatchTable({ id, table: tableName }).then(res => {

      //订阅 （res.msg 返回订阅topic）
      // subscribe(res.msg).then(res => {
      //   //更新info
      //   debugger
      //   setSubscribeMsg(res as string)
      //   // setConsoleInfo(consoleInfo  + res as string)
      // })
      stompClient.subObj= stompClient.mqClient.subscribe(res.msg,(res)=>{
        setSubscribeMsg(res.body)
      })
      // let subObj = stompClient.subscribe(res.msg, (res) => {
      //   // 订阅回调
      //   setSubscribeMsg(res.body)
      // });
      


    })

  }, [tableName]);

  //订阅信息发生变化，回显到面板
  useEffect(() => {
    debugger
    setConsoleInfo(consoleInfo + subscribeMsg)


  }, [subscribeMsg])



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
