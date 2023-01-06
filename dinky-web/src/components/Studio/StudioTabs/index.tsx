/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


import {Dropdown, Menu, message, Tabs} from 'antd';
import React from 'react';
import {connect} from 'umi';
import {StateType} from '@/pages/DataStudio/model';
import styles from './index.less';
import StudioEdit from '../StudioEdit';
import {DIALECT} from '../conf';
import StudioHome from "@/components/Studio/StudioHome";
import {Dispatch} from "@@/plugin-dva/connect";
import StudioKubernetes from "@/components/Studio/StudioKubernetes";
import {l} from "@/utils/intl";

const {TabPane} = Tabs;

const EditorTabs = (props: any) => {

  const {tabs, current, toolHeight, width, height} = props;

  const onChange = (activeKey: any) => {
    props.saveToolHeight(toolHeight);
    props.changeActiveKey(activeKey);
  };

  const onEdit = (targetKey: any, action: any) => {
    if (action === 'add') {
      add();
    } else if (action === 'remove') {
      props.saveToolHeight(toolHeight - 0.0001);
      // if (current.isModified) {
      //   saveTask(current, dispatch);
      // }
      remove(targetKey);
    }
  };

  const add = () => {
    message.warn(l('global.stay.tuned'));
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
    props.saveTabs(newPanes, newActiveKey);
  };

  const handleClickMenu = (e: any, current) => {
    props.closeTabs(current, e.key);
  };

  const menu = (pane) => (
    <Menu onClick={(e) => handleClickMenu(e, pane)}>
      <Menu.Item key="CLOSE_ALL">
        <span>{l('right.menu.closeAll')}</span>
      </Menu.Item>
      <Menu.Item key="CLOSE_OTHER">
        <span>{l('right.menu.closeOther')}</span>
      </Menu.Item>
    </Menu>
  );

  const Tab = (pane: any) => (
    <span>
      {pane.key === 0 ? (
        <>{pane.icon} {pane.title}</>
      ) : (
        <Dropdown overlay={menu(pane)} trigger={['contextMenu']}>
          <span className="ant-dropdown-link">
            <>{pane.icon} {pane.title}</>
          </span>
        </Dropdown>
      )}
    </span>
  );

  // as different dialet return different Panle
  const getTabPane = (pane, i) => {
    if (pane.task.dialect == DIALECT.KUBERNETES_APPLICATION) {
      return (
        <TabPane tab={Tab(pane)} key={pane.key} closable={pane.closable}>
          <StudioKubernetes
            tabsKey={pane.key}
            conf={pane.value}
            monaco={pane.monaco}
            height={height ? height : (toolHeight - 32)}
            width={width}
          />
        </TabPane>
      )
    } else {
      return (<TabPane tab={Tab(pane)} key={pane.key} closable={pane.closable}>
        <StudioEdit
          tabsKey={pane.key}
          sql={pane.value}
          monaco={pane.monaco}
          sqlMetaData={pane.sqlMetaData}
          height={height ? height : (toolHeight - 32)}
          width={width}
          language={getLanguage(current.task.dialect)}
        />
      </TabPane>)
    }
  }
  const getLanguage = (dialect: string) => {
    switch (dialect) {
      case DIALECT.JAVA:
        return DIALECT.JAVA.toLowerCase()
      case DIALECT.SCALA:
        return DIALECT.SCALA.toLowerCase()
      case DIALECT.PYTHON:
        return DIALECT.PYTHON.toLowerCase()
      default:
        return DIALECT.SQL.toLowerCase()
    }

  }

  return (
    <>
      {tabs.panes.length === 0 ? <StudioHome width={width}/> :
        <Tabs
          hideAdd
          type="editable-card"
          size="small"
          onChange={onChange}
          activeKey={tabs.activeKey + ''}
          onEdit={onEdit}
          className={styles['edit-tabs']}
          style={{height: height ? height : toolHeight}}
        >
          {tabs.panes.map((pane, i) => getTabPane(pane, i))}
        </Tabs>}
    </>
  );
};

const mapDispatchToProps = (dispatch: Dispatch) => ({
  closeTabs: (current: any, key: string) => dispatch({
    type: 'Studio/closeTabs',
    payload: {
      deleteType: key,
      current
    },
  }), saveTabs: (newPanes: any, newActiveKey: number) => dispatch({
    type: 'Studio/saveTabs',
    payload: {
      activeKey: newActiveKey,
      panes: newPanes,
    },
  }), saveToolHeight: (toolHeight: number) => dispatch({
    type: 'Studio/saveToolHeight',
    payload: toolHeight - 0.0001,
  }), changeActiveKey: (activeKey: number) => dispatch({
    type: 'Studio/changeActiveKey',
    payload: activeKey,
  }),
})

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  sql: Studio.sql,
  tabs: Studio.tabs,
  toolHeight: Studio.toolHeight,
}), mapDispatchToProps)(EditorTabs);
