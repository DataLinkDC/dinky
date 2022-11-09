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


import {Button, Descriptions, Empty, Tabs} from 'antd';
import CodeShow from "@/components/Common/CodeShow";
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {parseByteStr} from "@/components/Common/function";
import {TaskContainerConfigInfo, TaskManagerConfiguration} from "@/pages/DevOps/data";
import {useEffect, useRef, useState} from "react";
import {HomeOutlined} from "@ant-design/icons";
import {getTaskManagerInfo} from "@/pages/DevOps/service";
import {l} from "@/utils/intl";

const {TabPane} = Tabs;

const TaskManagerInfo = (props: any) => {
  const {job} = props;

  const actionRef = useRef<ActionType>();

  const [activeContainer, setActiveContainer] = useState<TaskContainerConfigInfo>();
  const [taskManager, setTaskManager] = useState<TaskManagerConfiguration[]>();

  const refreshTaskManagerInfo = () => {
    const res = getTaskManagerInfo(job?.history?.jobManagerAddress);
    res.then((result) => {
      setTaskManager(result.datas);
    });
  }

  useEffect(() => {
    refreshTaskManagerInfo();
  }, []);

  const handleBack = () => {
    setActiveContainer(undefined);
  };

  const getMetricsConfigForm = (metrics: any) => {
    let formList = [];
    for (let key in metrics) {
      formList.push(
        <Descriptions.Item label={key}>
          {metrics[key]}
        </Descriptions.Item>
      )
    }
    return formList
  }

  const buildContainerConfigInfo = () => {
    return (
      <>
        <div style={{marginBottom: 4}}>
          <Button title={l('button.back')} onClick={handleBack}> ← {l('button.back')}<HomeOutlined/> </Button>
        </div>
        <Tabs defaultActiveKey="metrics" size="small"

              tabPosition="top" style={{
          border: "1px solid #f0f0f0",
        }}>
          <TabPane tab={<span>&nbsp; Metrics &nbsp;</span>} key="metrics">
            <Descriptions bordered size="small" column={1}>
              {getMetricsConfigForm(activeContainer?.metrics)}
            </Descriptions>
          </TabPane>
          <TabPane tab={<span>&nbsp; Logs &nbsp;</span>} key="logs">
            {(activeContainer?.taskManagerLog) ?
              <CodeShow code={activeContainer?.taskManagerLog} language='java' height='500px'/>
              : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
            }
          </TabPane>
          <TabPane tab={<span>&nbsp; Stdout &nbsp;</span>} key="stdout">
            {(activeContainer?.taskManagerStdout) ?
              <CodeShow code={activeContainer?.taskManagerStdout} language='java' height='500px'/>
              : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
            }
          </TabPane>
          <TabPane tab={<span>&nbsp; Thread Dump &nbsp;</span>} key="threaddump">
            {(activeContainer?.taskManagerThreadDump) ?
              <CodeShow code={activeContainer?.taskManagerThreadDump} language='java' height='500px'/>
              : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
            }
          </TabPane>
        </Tabs>
      </>
    )
  }

  const columns: ProColumns<TaskManagerConfiguration>[] = [
    {
      title: 'ID,Path',
      dataIndex: 'containerId',
      copyable: true,
      render: (dom, entity) => {
        return (
          <>
            <a style={{width: 500}}
               onClick={() => setActiveContainer(entity.taskContainerConfigInfo)}>{entity.containerId}</a>
            <br/>
            <span>{entity.containerPath}</span>
          </>
        );
      },
    },
    {
      title: 'Data Port',
      align: 'center',
      dataIndex: 'dataPort',
    },
    {
      title: 'JMX Port',
      align: 'center',
      sorter: true,
      dataIndex: 'jmxPort',
    },
    {
      title: 'Last Heartbeat',
      align: 'center',
      sorter: true,
      valueType: 'dateTime',
      dataIndex: 'timeSinceLastHeartbeat',
    },
    {
      title: 'All Solts',
      align: 'center',
      sorter: true,
      dataIndex: 'slotsNumber',
    },
    {
      title: 'Free Solts',
      align: 'center',
      sorter: true,
      dataIndex: 'freeSlots',
    },
    {
      title: 'CPU Cores',
      align: 'center',
      sorter: true,
      render: (dom, entity) => {
        return (JSON.parse(entity.totalResource))['cpuCores'];
      },
    },
    {
      title: 'Free Cores',
      align: 'center',
      sorter: true,
      render: (dom, entity) => {
        return (JSON.parse(entity.freeResource))['cpuCores'];
      },
    },
    {
      title: 'Physical Mem',
      align: 'center',
      sorter: true,
      render: (dom, entity) => {
        return parseByteStr((JSON.parse(entity.hardware))['physicalMemory']);
      },
    },
    {
      title: 'Free Mem',
      align: 'center',
      sorter: true,
      render: (dom, entity) => {
        return parseByteStr((JSON.parse(entity.hardware))['freeMemory']);
      },
    },
    {
      title: 'Hardware ManagedMem',
      align: 'center',
      sorter: true,
      render: (dom, entity) => {
        return parseByteStr((JSON.parse(entity.hardware))['managedMemory']);
      },
    },
    {
      title: 'Total ProcessMem',
      align: 'center',
      sorter: true,
      render: (dom, entity) => {
        return parseByteStr((JSON.parse(entity.memoryConfiguration))['totalProcessMemory']);
      },
    },
    {
      title: 'Total FlinkMem',
      align: 'center',
      sorter: true,
      render: (dom, entity) => {
        return parseByteStr((JSON.parse(entity.memoryConfiguration))['totalFlinkMemory']);
      },
    },
    {
      title: 'Task Heap',
      align: 'center',
      sorter: true,
      render: (dom, entity) => {
        return parseByteStr((JSON.parse(entity.memoryConfiguration))['taskHeap']);
      },
    },
    {
      title: 'JVM Heap',
      align: 'center',
      sorter: true,
      render: (dom, entity) => {
        return parseByteStr((JSON.parse(entity.hardware))['freeMemory']);
      },
    },
    {
      title: 'Flink ManagedMem',
      align: 'center',
      sorter: true,
      render: (dom, entity) => {
        return parseByteStr((JSON.parse(entity.hardware))['managedMemory']);
      },
    },
  ];

  return (
    <>
      {activeContainer ? buildContainerConfigInfo() : undefined}
      {(!activeContainer && taskManager?.length > 0) ?
        (<ProTable<TaskManagerConfiguration>
          columns={columns}
          style={{width: '100%'}}
          dataSource={taskManager}
          actionRef={actionRef}
          rowKey="containerId"
          pagination={{
            pageSize: 8,
          }}
          toolBarRender={false}
          dateFormatter="string"
          search={false}
          size="small"
        />)
        : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
      }
    </>
  )
};

export default TaskManagerInfo;
