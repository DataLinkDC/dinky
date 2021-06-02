import {message, Tabs } from 'antd';
import React, {useState} from 'react';
import StudioEdit from "../StudioEdit";
import {connect} from "umi";
import {StateType} from "@/pages/FlinkSqlStudio/model";

const { TabPane } = Tabs;

const EditorTabs = (props: any) => {
  const {tabs,dispatch} = props;
  const [newTabIndex, setNewTabIndex] = useState<number>(0);
  const [activeKey, setActiveKey] = useState<number>(tabs.activeKey);
  const [panes, setPanes] = useState<any>(tabs.panes);

  const onChange = (activeKey: any) => {
    dispatch({
      type: "Studio/changeActiveKey",
      payload: activeKey,
    });
  };

  const onEdit = (targetKey: any, action: any) => {
    if(action=='add'){
      add();
    }else if(action=='remove'){
      remove(targetKey);
    }
  };

  const add = () => {
    message.warn('敬请期待');
    /*let index = newTabIndex + 1;
    const newPanes = [...panes];
    newPanes.push({ title: `未命名${index}`,value:'', key: -index });
    setPanes(newPanes);
    setActiveKey(-index);
    setNewTabIndex(index);*/
  };

  const remove = (targetKey:any) => {
    let newActiveKey = tabs.activeKey;
    let lastIndex = 0;
    tabs.panes.forEach((pane, i) => {
      if (pane.key === targetKey) {
        lastIndex = i - 1;
      }
    });
    let panes = tabs.panes;
    const newPanes = panes.filter(pane => pane.key != targetKey);
    if (newPanes.length && newActiveKey === targetKey) {
      if (lastIndex > 0) {
        newActiveKey = newPanes[lastIndex].key;
      } else {
        newActiveKey = newPanes[0].key;
      }
    }
    dispatch({
      type: "Studio/saveTabs",
      payload: {
        activeKey:newActiveKey,
        panes:newPanes,
      },
    });
  };

  return (
    <>
      <Tabs
        type="editable-card"
        size="small"
        onChange={onChange}
        activeKey={tabs.activeKey+''}
        onEdit={onEdit}
      >
        {tabs.panes.map(pane => (
          <TabPane tab={pane.title} key={pane.key} closable={pane.closable}>
          </TabPane>
        ))}
      </Tabs>
      </>
    )
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  sql: Studio.sql,
  tabs: Studio.tabs,
}))(EditorTabs);
