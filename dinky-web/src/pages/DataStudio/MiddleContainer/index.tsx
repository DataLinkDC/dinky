import {AndroidOutlined, AppleOutlined} from "@ant-design/icons";
import {Tabs} from "antd";
import React, {useRef, useState} from "react";
import {connect} from "@@/exports";
import {StateType} from "@/pages/DataStudio/model";


type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

const MiddleContainer = (props:any) => {
  const {tabs:{panes,activeKey},dispatch}= props;
  const updateActiveKey = (key: string) => {
    dispatch({
      type: 'Studio/updateTabs',
      payload: parseInt(key),
    })
  };


  const remove = (targetKey: TargetKey) => {
    // const targetIndex = panes.findIndex((pane) => pane.key === targetKey);
    // const newPanes = panes.filter((pane) => pane.key !== targetKey);
    // if (newPanes.length && targetKey === activeKey) {
    //   const {key} = newPanes[targetIndex === newPanes.length ? targetIndex - 1 : targetIndex];
    //   updateActiveKey(key)
    // }
    // setItems(newPanes);
    dispatch({
      type: 'Studio/closeTab',
      payload: targetKey,
    })
  };

  const onEdit = (targetKey: TargetKey, action: 'add' | 'remove') => {
    if (action === 'add') {
    } else {
      remove(targetKey);
    }
  };
  return (
    <Tabs
      hideAdd
      onChange={updateActiveKey}
      activeKey={activeKey}
      type="editable-card"
      onEdit={remove}
      items={panes}
    />
  )
}
export default connect(({Studio}: { Studio: StateType }) => ({
  tabs: Studio.tabs,
}))(MiddleContainer);
