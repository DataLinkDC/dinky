/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import CodeShow from "@/components/CustomEditor/CodeShow";
import {Modal} from "antd";
import {l} from "@/utils/intl";
import {GitProject} from "@/types/RegCenter/data";
import React, {useEffect} from "react";
import {getData} from "@/services/api";

type ShowLogProps = {
  onCancel: (flag?: boolean) => void;
  modalVisible: boolean;
  values: Partial<GitProject>;
};

/**
 * code edit props
 */
const CodeEditProps = {
  height: "60vh",
  width: "100%",
  lineNumbers: "on",
  language: "java",
};


export const ShowLog: React.FC<ShowLogProps> = (props) => {
  const {values, modalVisible, onCancel} = props;

  const [log, setLog] = React.useState<string>("2023-04-23 10:21:41 JRebel: Reconfiguring reprocessed bean 'studioController' [org.dinky.controller.StudioController]\n" +
    "[dinky] 2023-04-23 10:48:50.014   WARN 17928 --- [nio-8888-exec-4] com.alibaba.druid.pool.DruidAbstractDataSource: discard long time none received connection. , jdbcUrl : jdbc:mysql://127.0.0.1:33061/dinky?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true, version : 1.2.8, lastPacketReceivedIdleMillis : 1660388\n" +
    "[dinky] 2023-04-23 10:48:50.017   WARN 17928 --- [nio-8888-exec-4] com.alibaba.druid.pool.DruidAbstractDataSource: discard long time none received connection. , jdbcUrl : jdbc:mysql://127.0.0.1:33061/dinky?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true, version : 1.2.8, lastPacketReceivedIdleMillis : 1710581\n" +
    "[dinky] 2023-04-23 10:50:19.717   WARN 17928 --- [nio-8888-exec-3] com.alibaba.druid.pool.DruidAbstractDataSource: discard long time none received connection. , jdbcUrl : jdbc:mysql://127.0.0.1:33061/dinky?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true, version : 1.2.8, lastPacketReceivedIdleMillis : 88667\n" +
    "[dinky] 2023-04-23 10:57:38.001   WARN 17928 --- [nio-8888-exec-1] com.alibaba.druid.pool.DruidAbstractDataSource: discard long time none received connection. , jdbcUrl : jdbc:mysql://127.0.0.1:33061/dinky?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true, version : 1.2.8, lastPacketReceivedIdleMillis : 435393 ");

  const queryAllStepLogs = async () => {
    await getData('/api/git/build-step-logs', {
       id: values.id,
       step: -1, // -1: all steps
     }).then((res) => {
       setLog(res.data);
    });
  };

  const queryStepLog = (step: number) => {

  };


  useEffect(() => {
    // queryAllStepLogs();
  }, [modalVisible]);


  return (
    <Modal
      title={l("rc.gp.log")}
      width={"75%"}
      open={modalVisible}
      onCancel={() => onCancel()}
      cancelText={l("button.close")}
      okButtonProps={{style: {display: "none"}}}
    >
      <CodeShow {...CodeEditProps} code={log} showFloatButton={true} />
    </Modal>
  );
};
