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

import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import React, {useEffect, useState} from "react";
import CodeShow from "@/components/Common/CodeShow";
import {getConsoleInfo} from "@/pages/SettingCenter/ProcessList/service";
import { clearConsole } from "../../StudioEvent/DDL";
import {l} from "@/utils/intl";

const StudioMsg = (props: any) => {

  const {current, height, isActive} = props;
  console.log(props);
  
  
  const [consoleInfo, setConsoleInfo] = useState<string>("");

  useEffect(() => {
    refreshConsoleInfo();
    let dataPolling = setInterval(refreshConsoleInfo, 3000);
    return () => {
      clearInterval(dataPolling);
    };
  }, [isActive]);

  const refreshConsoleInfo = () => {
    if (isActive) {
      const res = getConsoleInfo();
      res.then((result) => {
        result.datas && setConsoleInfo(result.datas);
      });
    }
  }

  const editorDidMountHandle = (editor: any, monaco: any) => {
    editor.addAction({
      id: 'btn-studio-msg-clear', // menu id
      label: l('pages.datastudio.editor.clearConsole'), // menu name
      contextMenuGroupId: '9_cutcopypaste', // menu group
      run: () => {
        clearConsole().then((result) => {
          setConsoleInfo("")
          refreshConsoleInfo()
        });
      }, // 点击后执行的操作
    })
  };



  return (
    <>
      <CodeShow code={consoleInfo} language='java' height={height} theme="vs" editorDidMountHandle={editorDidMountHandle}/>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioMsg);
