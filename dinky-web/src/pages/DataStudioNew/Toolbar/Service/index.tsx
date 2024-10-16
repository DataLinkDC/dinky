import {connect} from "@@/exports";
import {LayoutState} from "@/pages/DataStudioNew/model";
import {mapDispatchToProps} from "@/pages/DataStudioNew/DvaFunction";
import {Flex, Tabs, TabsProps, TreeDataNode} from "antd";
import {Panel, PanelGroup, PanelResizeHandle} from "react-resizable-panels";
import DirectoryTree from "antd/es/tree/DirectoryTree";
import "./index.less"
import React from "react";
import {ArrowsAltOutlined, CodeOutlined, PartitionOutlined, ShrinkOutlined, TableOutlined} from "@ant-design/icons";
import RunToolBarButton from "@/pages/DataStudioNew/components/RunToolBarButton";
import {FlinkSQLSvg} from "@/components/Icons/CodeLanguageIcon";
import CusPanelResizeHandle from "@/pages/DataStudioNew/components/CusPanelResizeHandle";

const items: TabsProps['items'] = [
  {
    key: '1',
    label: '控制台',
    icon: <CodeOutlined />,
    children: 'Content of Tab Pane 1',
  },
  {
    key: '2',
    label: '结果',
    icon: <TableOutlined />,
    children: 'Content of Tab Pane 2',
  },
  {
    key: '3',
    label: '血缘',
    icon: <PartitionOutlined />,
    children: 'Content of Tab Pane 3',
  },
];

const treeData: TreeDataNode[] = [
  {
    title: 'Database',
    key: '0-0',
    children: [
      {
        title: 'Flink SQL',
        key: '0-0-1',
        icon: <FlinkSQLSvg/>,
        children: [{title: 'task1', key: '0-0-1-0',icon:<FlinkSQLSvg/>}],
      },
    ],
  }
];

const Service = (props: any) => {
  const {showDesc} = props;
  return (<PanelGroup direction={"horizontal"}>
    <Panel defaultSize={10}>
      <Flex justify={"right"}>
        <RunToolBarButton showDesc={showDesc} desc={"折叠"} icon={<ShrinkOutlined/>}/>
        <RunToolBarButton showDesc={showDesc} desc={"展开"} icon={<ArrowsAltOutlined/>}/>

      </Flex>
      <DirectoryTree
        defaultExpandedKeys={['0-0-0', '0-0-1']}
        defaultSelectedKeys={['0-0-0', '0-0-1']}
        defaultCheckedKeys={['0-0-0', '0-0-1']}
        treeData={treeData}
        expandAction={false}
        blockNode
      />
    </Panel>

    <CusPanelResizeHandle />

    <Panel style={{paddingInline: 10}}>
      <Tabs activeKey={undefined} items={items}  size={"small"} style={{height: '100%'}}/>
    </Panel>

  </PanelGroup>)
}


export default connect(
  ({DataStudio}: { DataStudio: LayoutState }) => ({
    project: DataStudio.toolbar.project,
    action: DataStudio.action,
    showDesc: DataStudio.toolbar.showDesc,
  }), mapDispatchToProps)(Service);
