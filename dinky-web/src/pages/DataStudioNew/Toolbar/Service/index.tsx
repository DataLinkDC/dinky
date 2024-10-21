import {connect} from "@@/exports";
import {CenterTab, LayoutState} from "@/pages/DataStudioNew/model";
import {mapDispatchToProps} from "@/pages/DataStudioNew/DvaFunction";
import {Flex, Tabs, TabsProps, TreeDataNode} from "antd";
import {Panel, PanelGroup} from "react-resizable-panels";
import DirectoryTree from "antd/es/tree/DirectoryTree";
import "./index.less"
import React, {Key, useEffect, useState} from "react";
import {
  ApartmentOutlined,
  ArrowsAltOutlined,
  AuditOutlined,
  CodeOutlined, HistoryOutlined,
  PartitionOutlined,
  ShrinkOutlined
} from "@ant-design/icons";
import RunToolBarButton from "@/pages/DataStudioNew/components/RunToolBarButton";
import CusPanelResizeHandle from "@/pages/DataStudioNew/components/CusPanelResizeHandle";
import {getIcon} from "@/utils/function";
import Output from "@/pages/DataStudioNew/Toolbar/Service/Output";
import ExecutionHistory from "@/pages/DataStudioNew/Toolbar/Service/ExecutionHistory";
import {KeepAlive} from "react-activation";
import {DataStudioActionType} from "@/pages/DataStudioNew/data.d";
import Explain from "@/pages/DataStudioNew/Toolbar/Service/Explain";
import FlinkGraph from "@/pages/DataStudioNew/Toolbar/Service/FlinkGraph";
import Lineage from "@/pages/DataStudioNew/Toolbar/Service/Lineage";


const Service = (props: { showDesc: boolean, tabs: CenterTab[], action: any }) => {
  const {showDesc, tabs, action: {actionType, params}} = props;
  const [selectedKey, setSelectedKey] = useState<Key[]>([]);
  const [taskItems, setTaskItems] = useState<Record<string, TabsProps['items']>>({});
  const [tabActiveKey, setTabActiveKey] = useState<Record<string, string>>({});

  useEffect(() => {
    if (!actionType || !params) {
      return
    }
    const taskItem = taskItems[params.taskId] ?? [];
    setSelectedKey([params.taskId])
    if (actionType === DataStudioActionType.TASK_RUN_CHECK) {
      if (!taskItem.some((item) => item.key === actionType)) {
        setTaskItems(prevState => (
          {
            ...prevState,
            [params.taskId]: [...taskItem,
              {
                key: actionType,
                label: '检查',
                icon: <AuditOutlined/>,
                children: <Explain data={params.data}/>,
              },
            ]
          }
        ))
      }else {
        setTaskItems(prevState => {
          const item = prevState[params.taskId]!!
          item.find((item) => item.key === actionType)!!.children = <Explain data={params.data}/>
          return {...prevState}
        })
      }
      setTabActiveKey(prevState => (
        {
          ...prevState,
          [params.taskId]: actionType
        }
      ))

    }else if (actionType === DataStudioActionType.TASK_RUN_DAG) {
      if (!taskItem.some((item) => item.key === actionType)) {
        setTaskItems(prevState => (
          {
            ...prevState,
            [params.taskId]: [...taskItem,
              {
                key: actionType,
                label: 'DAG',
                icon: <ApartmentOutlined/>,
                children: <FlinkGraph data={params.data}/>,
              },
            ]
          }
        ))
      }else {
        setTaskItems(prevState => {
          const item = prevState[params.taskId]!!
          item.find((item) => item.key === actionType)!!.children = <FlinkGraph data={params.data}/>
          return {...prevState}
        })
      }
      setTabActiveKey(prevState => (
        {
          ...prevState,
          [params.taskId]: actionType
        }
      ))
    }else if (actionType === DataStudioActionType.TASK_RUN_LINEAGE) {
      if (!taskItem.some((item) => item.key === actionType)) {
        setTaskItems(prevState => (
          {
            ...prevState,
            [params.taskId]: [...taskItem,
              {
                key: actionType,
                label: '血缘',
                icon: <PartitionOutlined/>,
                children: <Lineage data={params.data}/>,
              },
            ]
          }
        ))
      }else {
        setTaskItems(prevState => {
          const item = prevState[params.taskId]!!
          item.find((item) => item.key === actionType)!!.children = <Lineage data={params.data}/>
          return {...prevState}
        })
      }
      setTabActiveKey(prevState => (
        {
          ...prevState,
          [params.taskId]: actionType
        }
      ))
    }

  }, [props.action]);


  const treeData: TreeDataNode[] = [
    {
      title: 'Database',
      key: 'Database',
      children: [],
    }
  ];
  tabs.forEach((tab) => {
    if (tab.tabType === 'task') {
      console.log(tab.params)
      // 查找到对应的task，并添加，不存在的节点，添加
      // 1. 查找到对应的Database
      // 2. 查找到对应的FlinkSql
      // 3. 查找到对应的task

      treeData.forEach((node) => {
        const dialect = tab.params.dialect
        const icon = getIcon(dialect)
        if (node.key === 'Database') {
          let currentDialectTree = node.children!!.find((child) => child.key === dialect) as TreeDataNode;
          if (!currentDialectTree) {
            node.children!!.push({title: dialect, key: dialect, icon: icon, children: []})
            currentDialectTree = node.children!!.find((child) => child.key === dialect) as TreeDataNode
          }
          currentDialectTree.children!!.push(
            {title: tab.title, key: tab.params.taskId, icon: icon, isLeaf: true}
          )
        }
      })
    }
  })

  const renderContent = () => {
    if (selectedKey.length === 1) {
      const taskId = selectedKey[0] as number;
      const items: TabsProps['items'] = [
        {
          key: 'output',
          label: '输出',
          icon: <CodeOutlined/>,
          children: <Output taskId={taskId}/>,
        }
        ,
        {
          key: 'history',
          label: '执行历史',
          icon: <HistoryOutlined/>,
          children: <ExecutionHistory taskId={taskId}/>,
        },
      ];
      const taskItem = taskItems[taskId] ?? [];
      return <Tabs activeKey={tabActiveKey[taskId]} items={[...items, ...taskItem]} size={"small"}
                   onChange={(activeKey) => {
                     setTabActiveKey(prevState => (
                       {
                         ...prevState,
                         [taskId]: activeKey
                       }
                     ))
                   }}
                   style={{height: '100%'}}/>
    }
  }
  return (<PanelGroup direction={"horizontal"}>
    <Panel defaultSize={20}>
      <Flex justify={"right"}>
        <RunToolBarButton showDesc={showDesc} desc={"折叠"} icon={<ShrinkOutlined/>}/>
        <RunToolBarButton showDesc={showDesc} desc={"展开"} icon={<ArrowsAltOutlined/>}/>

      </Flex>
      <DirectoryTree
        selectedKeys={selectedKey}
        defaultExpandAll
        treeData={treeData}
        expandAction={false}
        onSelect={(selectedKeys, {node}) => {

          node.isLeaf && setSelectedKey(selectedKeys)
        }}
        blockNode
      />
    </Panel>

    <CusPanelResizeHandle/>

    <Panel style={{paddingInline: 10}}>
      <KeepAlive cacheKey={"service:" + selectedKey[0]}>
        {renderContent()}
      </KeepAlive>
    </Panel>

  </PanelGroup>)
}


export default connect(
  ({DataStudio}: { DataStudio: LayoutState }) => ({
    project: DataStudio.toolbar.project,
    action: DataStudio.action,
    showDesc: DataStudio.toolbar.showDesc,
    tabs: DataStudio.centerContent.tabs,
  }), mapDispatchToProps)(Service);
