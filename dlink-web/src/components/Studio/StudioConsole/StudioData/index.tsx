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

import {Input, Modal, Tabs} from "antd";
import {registerWatchTable, unRegisterWatchTable} from "@/pages/DataStudio/service";
import {Component, useEffect, useState} from "react";
import stompClientUtil from "@/utils/stompClientUtil"
import {Scrollbars} from 'react-custom-scrollbars';
import CodeShow from "@/components/Common/CodeShow";
import {l} from "@/utils/intl";
import {clearConsole} from "@/components/Studio/StudioEvent/DDL";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {Subscription} from "stompjs";
import TabPane = Tabs.TabPane;

const DatePage = (props: any) => {
  const {height, title} = props;
  const {stompClient} = props;
  const [subScription, setSubScription] = useState<Subscription>();
  const [consoleInfo, setConsoleInfo] = useState<string>("");
  const [tableName, setTableName] = useState<string>("");
  let consoleHeight = (height - 37.6);
  const id = Number(localStorage.getItem('dlink-tenantId'));

  const onSearchName = (value: string) => {
    registerWatchTable({id, table: value}).then(res => {
      let ss = stompClient.subscribe(res.msg, (res: any) => {
        setConsoleInfo(preConsoleInfo => preConsoleInfo + "\n" + res.body);
      });
      setSubScription(ss);
    })

    setTableName(value)
  };

  useEffect(() => {
    return () => unRegisterWatchTable({id, table: tableName}).then(res => {
      console.log("unsubscribe: " + tableName)
      subScription?.unsubscribe();
    });
  }, [subScription]);

  const editorDidMountHandle = (editor: any, monaco: any) => {
    onSearchName(title);
    editor.addAction({
      id: 'btn-studio-data-clear',
      label: l('pages.datastudio.editor.clearConsole'),
      contextMenuGroupId: '9_cutcopypaste',
      run: () => {
        clearConsole().then((result) => {
          setConsoleInfo("")
        });
      },
    })
  };

  return (<div style={{width: '100%'}}>
    <Scrollbars style={{height: consoleHeight}}>
      <CodeShow code={consoleInfo} language='text' height={height} theme="vs-dark"
                editorDidMountHandle={editorDidMountHandle}/>
    </Scrollbars>
  </div>)
};

const StudioData = (props: any) => {
  const {height, isActive} = props;
  const [panes, setPanes] = useState<[{ title: string, key: string, content: Component }]>([]);

  const addTab = () => {
    let title: string
    Modal.confirm({
      title: 'Please enter table name',
      content: <Input onChange={e => title = e.target.value}/>,
      onOk() {
        const activeKey = `${panes.length + 1}`;
        const newPanes = [...panes];
        newPanes.push({
          title: title,
          content: <DatePage height={height} title={title} stompClient={stompClientUtil.stompClient}/>,
          key: activeKey
        });
        setPanes(newPanes);
      }
    });
  };

  useEffect(() => {
    stompClientUtil.connect();
  }, [isActive])

  useEffect(()=>{
    return ()=>{
      stompClientUtil.disconnect();
    }
  }, [])

  return (<>
    <Tabs type="editable-card" onEdit={(targetKey, action) => {
      if (action === 'add') {
        addTab();
      } else if (action === 'remove') {
        const newPanes = panes.filter((pane) => pane.key !== targetKey);
        setPanes(newPanes);
      }
    }}>
      {panes.map((pane) => (
        <TabPane tab={pane.title} key={pane.key}>
          {pane.content}
        </TabPane>
      ))}
    </Tabs>
  </>);
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioData);
