import { message, Tabs, Menu, Dropdown } from 'antd';
import React, { useState } from 'react';
import { connect } from 'umi';
import { StateType } from '@/pages/FlinkSqlStudio/model';
import styles from './index.less';
import StudioEdit from '../StudioEdit';
import { saveTask } from '@/components/Studio/StudioEvent/DDL';
import { DIALECT } from '../conf';

const { TabPane } = Tabs;

const EditorTabs = (props: any) => {
  const { tabs, dispatch, current, toolHeight, width } = props;

  const onChange = (activeKey: any) => {
    dispatch &&
      dispatch({
        type: 'Studio/saveToolHeight',
        payload: toolHeight - 0.0001,
      });
    dispatch({
      type: 'Studio/changeActiveKey',
      payload: activeKey,
    });
  };

  const onEdit = (targetKey: any, action: any) => {
    if (action == 'add') {
      add();
    } else if (action == 'remove') {
      dispatch &&
        dispatch({
          type: 'Studio/saveToolHeight',
          payload: toolHeight - 0.0001,
        });
      if (current.isModified) {
        saveTask(current, dispatch);
      }
      remove(targetKey);
    }
  };

  const add = () => {
    message.warn('敬请期待');
  };

  const remove = (targetKey: any) => {
    let newActiveKey = tabs.activeKey;
    let lastIndex = 0;
    tabs.panes.forEach((pane, i) => {
      if (pane.key.toString() === targetKey) {
        lastIndex = i - 1;
      }
    });
    let panes = tabs.panes;
    const newPanes = panes.filter((pane) => pane.key.toString() != targetKey);
    if (newPanes.length && newActiveKey.toString() === targetKey) {
      if (lastIndex > 0) {
        newActiveKey = newPanes[lastIndex].key;
      } else {
        newActiveKey = newPanes[0].key;
      }
    }
    dispatch({
      type: 'Studio/saveTabs',
      payload: {
        activeKey: newActiveKey,
        panes: newPanes,
      },
    });
  };

  const handleClickMenu = (e: any, current) => {
    dispatch({
      type: 'Studio/closeTabs',
      payload: {
        deleteType: e.key,
        current
      },
    });
  };

  const menu = (pane) => (
    <Menu onClick={(e) => handleClickMenu(e, pane)}>
      <Menu.Item key="CLOSE_OTHER">
        <span>关闭其他</span>
      </Menu.Item>
      <Menu.Item key="CLOSE_ALL">
        <span>关闭所有</span>
      </Menu.Item>
    </Menu>
  );

  const Tab = (pane: any) => (
    <span>
      {pane.key === 0 ? (
        pane.title
      ) : (
        <Dropdown overlay={menu(pane)} trigger={['contextMenu']}>
          <span className="ant-dropdown-link">
            {pane.title}
          </span>
        </Dropdown>
      )}
    </span>
  );

  return (
    <Tabs
      hideAdd
      type="editable-card"
      size="small"
      onChange={onChange}
      activeKey={tabs.activeKey + ''}
      onEdit={onEdit}
      className={styles['edit-tabs']}
      style={{ height: toolHeight }}
    >
      {tabs.panes.map((pane) => (
        <TabPane tab={Tab(pane)} key={pane.key} closable={pane.closable}>
          <StudioEdit
            tabsKey={pane.key}
            height={toolHeight - 32}
            width={width}
            language={current.task.dialect === DIALECT.JAVA ? 'java' : 'sql'}
          />
        </TabPane>
      ))}
    </Tabs>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  sql: Studio.sql,
  tabs: Studio.tabs,
  toolHeight: Studio.toolHeight,
}))(EditorTabs);
