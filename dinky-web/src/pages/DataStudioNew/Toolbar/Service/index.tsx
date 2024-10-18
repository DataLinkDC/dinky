import {connect} from "@@/exports";
import {CenterTab, LayoutState} from "@/pages/DataStudioNew/model";
import {mapDispatchToProps} from "@/pages/DataStudioNew/DvaFunction";
import {Flex, Tabs, TabsProps, TreeDataNode} from "antd";
import {Panel, PanelGroup} from "react-resizable-panels";
import DirectoryTree from "antd/es/tree/DirectoryTree";
import "./index.less"
import React, {Key, useState} from "react";
import {ArrowsAltOutlined, CodeOutlined, PartitionOutlined, ShrinkOutlined, TableOutlined} from "@ant-design/icons";
import RunToolBarButton from "@/pages/DataStudioNew/components/RunToolBarButton";
import CusPanelResizeHandle from "@/pages/DataStudioNew/components/CusPanelResizeHandle";
import {getIcon} from "@/utils/function";




const Service = (props: { showDesc: boolean, tabs: CenterTab[] }) => {
  const {showDesc, tabs} = props;

  const [selectedKey, setSelectedKey] = useState<Key[]>([]);

  const items: TabsProps['items'] = [
    {
      key: '1',
      label: '控制台',
      icon: <CodeOutlined/>,
      children: 'Content of Tab Pane 1',
    },
    {
      key: '2',
      label: '结果',
      icon: <TableOutlined/>,
      children: 'Content of Tab Pane 2',
    },
    {
      key: '3',
      label: '血缘',
      icon: <PartitionOutlined/>,
      children: 'Content of Tab Pane 3',
    },
  ];
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
          let currentDialectTree = node.children!!.find((child) => child.key === dialect) as TreeDataNode ;
          if (!currentDialectTree) {
            node.children!!.push( {title: dialect, key: dialect, icon: icon, children: []})
            currentDialectTree=node.children!!.find((child) => child.key === dialect) as TreeDataNode
          }
          currentDialectTree.children!!.push(
            {title: tab.title, key: tab.id, icon: icon,isLeaf: true}
          )
        }
      })
    }
  })

  const renderContent=()=>{
    if (selectedKey.length===1){
      return <Tabs items={items} size={"small"} style={{height: '100%'}}/>
    }
  }
  return (<PanelGroup direction={"horizontal"}>
    <Panel defaultSize={10}>
      <Flex justify={"right"}>
        <RunToolBarButton showDesc={showDesc} desc={"折叠"} icon={<ShrinkOutlined/>}/>
        <RunToolBarButton showDesc={showDesc} desc={"展开"} icon={<ArrowsAltOutlined/>}/>

      </Flex>
      <DirectoryTree
        selectedKeys={selectedKey}
        defaultExpandAll
        treeData={treeData}
        expandAction={false}
        onSelect={(selectedKeys,{node}) => {

          node.isLeaf && setSelectedKey(selectedKeys)
        }}
        blockNode
      />
    </Panel>

    <CusPanelResizeHandle/>

    <Panel style={{paddingInline: 10}}>
      {renderContent()}
    </Panel>

  </PanelGroup>)
}


export default connect(
  ({DataStudio}: { DataStudio: LayoutState }) => ({
    project: DataStudio.toolbar.project,
    action: DataStudio.action,
    showDesc: DataStudio.toolbar.showDesc,
    tabs: DataStudio.centerContent.tabs
  }), mapDispatchToProps)(Service);
