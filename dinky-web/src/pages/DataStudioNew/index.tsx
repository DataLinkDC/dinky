import DockLayout, {LayoutData, PanelBase, PanelData} from "rc-dock";
import React from "react";
import {TabBase, TabData} from "rc-dock/src/DockData";
import {PageContainer} from "@ant-design/pro-layout";
import 'rc-dock/dist/rc-dock.css';

const DataStudioNew: React.FC = (props: any) => {

  const mainLayout: LayoutData = {
    dockbox: {
      mode: 'horizontal',
      children: [
        {
          id: 'dock1',
          mode: 'horizontal',
          size: 200,
          tabs: [
            {
              id: 'tab1-1', 
              title: 'tab1-1', 
              content: <div>Hello World</div>
            }
          ]
        },
        {
          id: 'dock2',
          size: 400,
          tabs: [
            {
              id: 'tab2-1',
              title: 'tab2-1',
            
              content: <div>Hello World111</div>
            },
            {
              id: 'tab2-2',
              title: 'tab2-2',
              content: <div>Hello World22222</div>
            }
          ],
        },
        {
          id: 'dock3',
          size: 200,
          panelLock: {
            panelStyle: 'main'
          },
          tabs: [
            {
              id: 'tab3-1',
              title: 'tab3-1',
              content: <div>Hello World111</div>
            },
            {
              id: 'tab3-2',
              title: 'tab3-2',
              content: <div>Hello World22222</div>
            }
          ],
        },
      ]
    }
  };


  const footerLayout: LayoutData = {
    dockbox: {
      mode: 'vertical',
      children: [
        {
          id: 'dock1',
          mode: 'vertical',
          size: 200,
          tabs: [
            {
              id: 'tab1-1', 
              title: 'tab1-1', 
              content: <div>Hello World</div>
            }
          ]
        },
      ]
    }
  };


  const saveTab = (tab: TabData): TabBase => {
    console.log('saveTab', tab)
    return {id: tab.id}
  };

  const loadTab = (tab: TabBase) : TabData => {
    console.log('loadTab', tab);
    return {id: tab.id, title: 'tab1', content: <div>Hello World</div> , group: 'allowWindow'};
  };

  // add tab0 to the main panel
  const afterPanelLoaded = (savedPanel: PanelBase, loadedPanel: PanelData) => {
    console.log('afterPanelLoaded', savedPanel, loadedPanel);
  };

  return <PageContainer breadcrumb={undefined} title={false} >
    <DockLayout 
      dockId="dock1"
      layout={mainLayout} 
      saveTab={(tab: TabData) => saveTab(tab)}
      loadTab={(tab: TabBase) => loadTab(tab)}
      groups={{
        allowWindow: {
          floatable: true,
          newWindow: true,
          maximizable: true,
        }
      }}
      afterPanelLoaded={afterPanelLoaded}
      afterPanelSaved={(savedPanel: PanelBase, panel: PanelData) => {
        console.log('afterPanelSaved', savedPanel);
      }}
      onLayoutChange={(layout) => {
        console.log('onLayoutChange', layout);
      }}
      style={{
        position: "relative", width: "98vw", height: "65vh",
        left: 10,
        top: 10,
        right: 10,
        bottom: 10,
      }}
    />
     <DockLayout 
      dockId="dock2"
      layout={footerLayout} 
      saveTab={(tab: TabData) => saveTab(tab)}
      loadTab={(tab: TabBase) => loadTab(tab)}
      groups={{
        allowWindow: {
          floatable: true,
          newWindow: true,
          maximizable: true,
        }
      }}
      afterPanelLoaded={afterPanelLoaded}
      afterPanelSaved={(savedPanel: PanelBase, panel: PanelData) => {
        console.log('afterPanelSaved', savedPanel);
      }}
      onLayoutChange={(layout) => {
        console.log('onLayoutChange', layout);
      }}
      style={{
        position: "relative", width: "98vw", height: "25vh",
        left: 10,
        top: 10,
        right: 10,
      }}
    />
  </PageContainer>
};

export default DataStudioNew;
