import {Button, Descriptions, Empty, Tabs} from 'antd';
import CodeShow from "@/components/Common/CodeShow";
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {parseByteStr} from "@/components/Common/function";
import {TaskManagerConfiguration} from "@/pages/DevOps/data";
import {useRef, useState} from "react";
import {history} from "@@/core/history";
import {HomeOutlined} from "@ant-design/icons";

const {TabPane} = Tabs;

const TaskManagerConfigurationForm = (props: any) => {
  const {job} = props;
  const actionRef = useRef<ActionType>();

  const [isHistory, setIsHistory] = useState<boolean>(false);

  const handleHistorySwicthChange = (checked: boolean) => {
    setIsHistory(checked);
  };

  const taskManagerContainerListDataSource: TaskManagerConfiguration[] = [];
  job?.taskManagerConfiguration?.forEach((entity: TaskManagerConfiguration) => {
      taskManagerContainerListDataSource.push({
        containerId: entity.containerId,
        containerPath: entity.containerPath,
        dataPort: entity.dataPort,
        jmxPort: entity.jmxPort,
        timeSinceLastHeartbeat: entity.timeSinceLastHeartbeat,
        slotsNumber: entity.slotsNumber,
        freeSlots: entity.freeSlots,
        totalResource: entity.totalResource,
        freeResource: entity.freeResource,
        hardware: entity.hardware,
        memoryConfiguration: entity.memoryConfiguration,
        taskContainerConfigInfo: entity.taskContainerConfigInfo,
      });
    }
  );
  const handleBack = () => {
    history.goBack();
  };

  const getMetricsConfigForm =(metrics:any) => {
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


  const buildContainerConfigInfo =(entity: TaskManagerConfiguration) =>{
    return (
      <>
      <div style={{ marginBottom: 16 }}>
        <Button title={'返回'} onClick={handleBack}> ← Back<HomeOutlined /> </Button>
      </div>
      <Tabs defaultActiveKey="metrics" size="small"

            tabPosition="top" style={{
        border: "1px solid #f0f0f0",
      }}>
        <TabPane tab={<span>&nbsp; Metrics &nbsp;</span>} key="metrics">
          {getMetricsConfigForm(entity?.taskContainerConfigInfo?.metrics)}
        </TabPane>

        <TabPane tab={<span>&nbsp; Logs &nbsp;</span>} key="logs">
          {(entity?.taskContainerConfigInfo?.taskManagerLog === "" || entity?.taskContainerConfigInfo?.taskManagerLog === null) ?
            <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
            : <CodeShow code={entity?.taskContainerConfigInfo?.taskManagerLog} language='java' height='500px'/>
          }
        </TabPane>
        <TabPane tab={<span>&nbsp; Stdout &nbsp;</span>} key="stdout">
          {(entity?.taskContainerConfigInfo?.taskManagerStdout === "" || entity?.taskContainerConfigInfo?.taskManagerLog === null) ?
            <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
            : <CodeShow code={entity?.taskContainerConfigInfo?.taskManagerStdout} language='java' height='500px'/>
          }
        </TabPane>

        <TabPane tab={<span>&nbsp; Thread Dump &nbsp;</span>} key="threaddump">
          {(entity?.taskContainerConfigInfo?.taskManagerThreadDump === "" || entity?.taskContainerConfigInfo?.taskManagerThreadDump === null) ?
            <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
            : <CodeShow code={entity?.taskContainerConfigInfo?.taskManagerThreadDump} language='java' height='500px'/>
          }
        </TabPane>

      </Tabs>
      </>
    )
  }

  // TODO: 点击[containerId]跳转到容器配置信息页面(buildContainerConfigInfo) 容器页面有返回按钮 可以返回到个列表页面
  const columns: ProColumns<TaskManagerConfiguration>[] = [
    {
      title: 'ID,Path',
      dataIndex: 'containerId',
      copyable: true,
      render: (dom, entity) => {
        return  (
          <>
            <a style={{ width: 500 }} >{entity.containerId}</a>
            <br/>
            <span >{entity.containerPath}</span>
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
      {job?.taskManagerConfiguration?.length > 0 ?
      <ProTable<TaskManagerConfiguration>
        columns={columns}
        style={{width: '100%'}}
        dataSource={ taskManagerContainerListDataSource }
        onDataSourceChange={(dataSource) => {
          actionRef.current?.reload();
        }}
        actionRef={actionRef}
        rowKey="containerId"
        pagination={{
          pageSize: 8,
        }}
        toolBarRender={false}
        dateFormatter="string"
        search={false}
        size="small"
      />: <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
    </>
  )
};

export default TaskManagerConfigurationForm;
