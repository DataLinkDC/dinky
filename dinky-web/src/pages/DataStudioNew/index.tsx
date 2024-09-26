import DockLayout, {DockContext, DropDirection, LayoutBase, LayoutData, PanelBase, PanelData} from "rc-dock";
import React, {useRef, useState} from "react";
import {TabBase, TabData} from "rc-dock/src/DockData";
import {PageContainer} from "@ant-design/pro-layout";
import 'rc-dock/dist/rc-dock.css';
import {AddOutline} from "antd-mobile-icons";
import {CloseOutlined} from "@ant-design/icons";
import {Space} from "antd";

const DataStudioNew: React.FC = (props: any) => {


  const [dockContext, setDockContext] = useState<DockContext>();

  const ref = useRef<HTMLDivElement>();

  const mainLayout: LayoutData = {
    dockbox: {
      mode: 'vertical',
      children: [
        {
          id: 'dock1',
          mode: 'horizontal',
          size: 1000,
          children: [
            {
              panelLock: {
                panelExtra: (panel: PanelData) => {
                  return <div>Extra</div>
                },
                panelStyle: 'main'
              },
              tabs: [
                {
                  id: 'tab1-1',
                  title: 'tab1-1',
                  closable: true,
                  content: <div>Hello World</div>
                }
              ]
            },
            {
              id: 'dock2',
              size: 800,
              mode: 'horizontal',
    
              tabs: [
                {
                  id: 'tab2-1',
                  closable: true,
                  title: 'tab2-1',
                  content: <div>Hello World111</div>
                },
                {
                  id: 'tab2-2',
                  title: 'tab2-2',
                  closable: true,
                  content: <div>Hello World22222</div>
                }
              ],
            },
            {
              id: 'dock3',
              size: 200,
              mode: 'horizontal',
    
              // panelLock: {
              //   panelStyle: 'main'
              // },
              tabs: [
                {
                  id: 'tab3-1',
                  title: 'tab3-1',
                  closable: true,
                  content: <div>Hello World111</div>
                },
                {
                  id: 'tab3-2',
                  title: 'tab3-2',
                  closable: true,
                  content: <div>Hello World22222</div>
                }
              ],
            },
          ],
        },
        
        {
          id: 'dock4',
          mode: 'vertical',
          activeId: 'tab6-1',
          tabs: [
            {
              id: 'tab6-1',
              title: 'tab6-1',
              content: <div>Hello World</div>
            }
          ]
        },
      ]
    }
  };



  /**
   * 保存标签页
   *
   * @param tab 标签页数据
   * @returns 返回标签页的基础信息
   */
  const saveTab = (tab: TabData): TabBase => {
    console.log('saveTab', tab)
    return {id: tab.id}
  };
  /**
   * 加载标签页
   *
   * @param tab 标签页基础信息
   * @returns 标签页数据
   */
  const loadTab = (tab: TabBase): TabData => {
    console.log('loadTab', tab);
    return {id: tab.id, title: tab.id as string, content: <div>Hello World</div>, group: 'dock1'};
  };

  /**
   * 面板加载后的回调函数
   *
   * @param savedPanel 保存的面板对象
   * @param loadedPanel 加载的面板数据
   */
  const afterPanelLoaded = (savedPanel: PanelBase, loadedPanel: PanelData) => {
    console.log('afterPanelLoaded', savedPanel, loadedPanel);
  };

  /**
   * 面板加载完成后回调函数
   *
   * @param savedPanel 保存的面板对象
   * @param loadedPanel 加载的面板数据
   */
  const afterPanelSaved = (savedPanel: PanelBase, panel: PanelData) => {
    console.log('afterPanelLoaded', savedPanel, panel);
  };


  /**
   * 布局改变事件处理函数
   *
   * @param newLayout 新的布局对象
   * @param currentTabId 当前选中的标签页ID
   * @param direction 拖拽方向
   */
  const layoutChange = (newLayout: LayoutBase, currentTabId: string, direction: DropDirection) => {
    console.log('layoutChange', newLayout, currentTabId, direction);
  }

  return <PageContainer breadcrumb={undefined} title={false}>
    <DockLayout
      dockId="dock1"
      layout={mainLayout}
      saveTab={(tab: TabData) => saveTab(tab)}
      loadTab={(tab: TabBase) => loadTab(tab)}
      afterPanelLoaded={afterPanelLoaded}
      afterPanelSaved={afterPanelLoaded}
      onLayoutChange={layoutChange}
      groups={{
        dock1: {
          disableDock: true,
          floatable: true,
          newWindow: true,
          maximizable: true,
          panelExtra: (panel: PanelData, context: DockContext) => {
            setDockContext(context);
            return <Space direction={'horizontal'} align={'baseline'} style={{
              display: "flex",
              justifyContent: 'space-between',
              width: '100%',
            }}>
              <AddOutline/>
              <CloseOutlined/>
            </Space>
          }
        }
      }}
      style={{
        position: "relative", width: "98vw", height: "90vh",
        left: 10,
        top: 10,
        right: 10,
        bottom: 10,
      }}
    />
  </PageContainer>
};

export default DataStudioNew;
