import {message, Tabs } from 'antd';
import React, {useState} from 'react';
import {connect} from "umi";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import styles from './index.less';
import StudioEdit from '../StudioEdit';
import {saveTask} from "@/components/Studio/StudioEvent/DDL";
const { TabPane } = Tabs;

const EditorTabs = (props: any) => {
  const {tabs,dispatch,current} = props;

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
      if(current.isModified){
        saveTask(current,dispatch);
      }
      remove(targetKey);
    }
  };

  const add = () => {
    message.warn('敬请期待');
  };

  const remove = (targetKey:any) => {
    let newActiveKey = tabs.activeKey;
    let lastIndex = 0;
    tabs.panes.forEach((pane, i) => {
      if (pane.key.toString() === targetKey) {
        lastIndex = i - 1;
      }
    });
    let panes = tabs.panes;
    const newPanes = panes.filter(pane => pane.key.toString() != targetKey);
    if (newPanes.length && newActiveKey.toString() === targetKey) {
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
      <Tabs
        hideAdd
        type="editable-card"
        size="small"
        onChange={onChange}
        activeKey={tabs.activeKey+''}
        onEdit={onEdit}
        className={styles["edit-tabs"]}
        style={{height:"50%"}}
      >
        {tabs.panes.map(pane => (
          <TabPane tab={pane.title} key={pane.key} closable={pane.closable}>
            <StudioEdit tabsKey={pane.key} height='100%'/>
          </TabPane>
        ))}
      </Tabs>

    )
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  sql: Studio.sql,
  tabs: Studio.tabs,
}))(EditorTabs);
