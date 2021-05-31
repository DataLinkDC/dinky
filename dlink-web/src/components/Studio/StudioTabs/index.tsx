import { Tabs } from 'antd';
import React from 'react';
import StudioEdit from "../StudioEdit";
import {connect} from "umi";
import {StateType} from "@/pages/FlinkSqlStudio/model";

const { TabPane } = Tabs;

const initialPanes = [
  { title: '草稿', key: '0' ,value:'select * from ',closable: false,},
];

class EditorTabs extends React.Component {
  newTabIndex = 1;

  state = {
    activeKey: initialPanes[0].key,
    panes: initialPanes,
  };

  onChange = (activeKey: any) => {
    this.setState({ activeKey });
  };

  onEdit = (targetKey: any, action: any) => {
    this[action](targetKey);
  };

  updateValue = (targetKey: any, val: string)=>{
    const { panes, activeKey } = this.state;
    panes.forEach((pane, i) => {
      if (pane.key === targetKey) {
        pane.value = val;
        return;
      }
    });
    /*debugger;
    this.setState({
      panes:panes,
      activeKey: activeKey,
    });*/
  };

  add = () => {
    const { panes } = this.state;
    const activeKey = this.newTabIndex++;
    const newPanes = [...panes];
    newPanes.push({ title: `未命名${activeKey}`,value:'', key: `${activeKey}` });
    this.setState({
      panes: newPanes,
      activeKey: activeKey,
    });
  };

  remove = (targetKey:any) => {
    const { panes, activeKey } = this.state;
    let newActiveKey = activeKey;
    let lastIndex = 1;
    panes.forEach((pane, i) => {
      if (pane.key === targetKey) {
        lastIndex = i - 1;
      }
    });
    const newPanes = panes.filter(pane => pane.key !== targetKey);
    if (newPanes.length && newActiveKey === targetKey) {
      if (lastIndex >= 0) {
        newActiveKey = newPanes[lastIndex].key;
      } else {
        newActiveKey = newPanes[0].key;
      }
    }
    this.setState({
      panes: newPanes,
      activeKey: newActiveKey,
    });
  };

  render() {
    const { panes, activeKey } = this.state;
    return (
      <Tabs
        type="editable-card"
        size="small"
        onChange={this.onChange}
        activeKey={activeKey}
        onEdit={this.onEdit}
      >
        {panes.map(pane => (
          <TabPane tab={pane.title} key={pane.key} closable={pane.closable}>
            <StudioEdit value={{formulaContent:pane.value}} onChange={
              (val: string)=>{
                this.updateValue(pane.key,val);
            }} />
          </TabPane>
        ))}
      </Tabs>
    );
  }
}

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  catalogue: Studio.catalogue,
  sql: Studio.sql,
}))(EditorTabs);
