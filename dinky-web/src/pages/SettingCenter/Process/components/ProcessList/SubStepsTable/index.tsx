/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from "react";
import {ProcessSteps} from "@/types/SettingCenter/data";
import {ProColumns} from "@ant-design/pro-table";
import {ProTable} from "@ant-design/pro-components";
import ShowLog from "@/pages/SettingCenter/Process/components/ProcessList/SubStepsTable/ShowLog";
import {ShowLogBtn} from "@/components/CallBackButton/ShowLogBtn";
import {MatchProcessStatus} from "@/pages/SettingCenter/Process/components/ProcessList/function";
import {l} from "@/utils/intl";

type SubStepsTableProps = {
  steps: ProcessSteps[];
}

type ShowLogProps = {
  type: string;
  log: string;
}

const SubStepsTable: React.FC<SubStepsTableProps> = (props) => {

  const {steps} = props;

  const [visibleViewLog, setVisibleViewLog] = React.useState<boolean>(false);
  const [viewLogProp, setViewLogProp] = React.useState<ShowLogProps>({
    type: "",
    log: "",
  });

  const handleViewLog = (type: string, log: string) => {
    setViewLogProp({type, log});
    setVisibleViewLog(true);
  };

  const cancelViewLog = () => {
    setViewLogProp({type: "", log: ""});
    setVisibleViewLog(false);
  };


  const renderLog = () => {
    return <ShowLog cancelViewLog={cancelViewLog} visibleViewLog={visibleViewLog} {...viewLogProp}/>;
  };

  const stepsColumns: ProColumns<ProcessSteps>[] = [
    {
      dataIndex: "index",
      valueType: "indexBorder",
      width: 48,
    },
    {
      title: "步骤状态",
      dataIndex: "stepStatus",
      render: (_, record) => {
        return MatchProcessStatus(record.stepStatus);
      }
    },
    {
      title: "步骤信息",
      dataIndex: "info",
      align: "center",
      render: (_, record) => {
        return <ShowLogBtn onClick={() => handleViewLog(l("sys.process.viewInfoLog"), record.info)}/>;
      }
    },
    {
      title: "步骤错误信息",
      dataIndex: "error",
      align: "center",
      render: (_, record) => {
        return <ShowLogBtn onClick={() => handleViewLog(l("sys.process.viewErrorLog"), record.error)}/>;
      }
    },
    {
      title: "步骤开始时间",
      dataIndex: "startTime",
      valueType: "dateTime",
    },
    {
      title: "步骤结束时间",
      dataIndex: "endTime",
      valueType: "dateTime",
    },
    {
      title: "步骤运行时长",
      dataIndex: "time",
    }
  ];

  return <>
    <ProTable<ProcessSteps>
      headerTitle={false}
      toolBarRender={false}
      search={false}
      columns={stepsColumns}
      dataSource={steps}
      pagination={false}
    />
    {visibleViewLog && renderLog()}
  </>;
};

export default SubStepsTable;
