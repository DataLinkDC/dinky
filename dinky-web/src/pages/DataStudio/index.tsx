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

import { AuthorizedObject, useAccess } from '@/hooks/useAccess';
import { useEditor } from '@/hooks/useEditor';
import useThemeValue from '@/hooks/useThemeValue';
import BottomContainer from '@/pages/DataStudio/BottomContainer';
import { LeftMenuKey } from '@/pages/DataStudio/data.d';
import FooterContainer from '@/pages/DataStudio/FooterContainer';
import { isProjectTabs, mapDispatchToProps } from '@/pages/DataStudio/function';
import SecondHeaderContainer from '@/pages/DataStudio/HeaderContainer';
import LeftContainer from '@/pages/DataStudio/LeftContainer';
import { BtnProvider } from '@/pages/DataStudio/LeftContainer/BtnContext';
import { getDataSourceList } from '@/pages/DataStudio/LeftContainer/DataSource/service';
import { getTaskData } from '@/pages/DataStudio/LeftContainer/Project/service';
import MiddleContainer from '@/pages/DataStudio/MiddleContainer';
import {
  StateType,
  TabsItemType,
  TabsPageSubType,
  TabsPageType,
  VIEW
} from '@/pages/DataStudio/model';
import RightContainer from '@/pages/DataStudio/RightContainer';
import {
  getClusterConfigurationData,
  getEnvData,
  getSessionData
} from '@/pages/DataStudio/RightContainer/JobConfig/service';
import { LeftBottomMoreTabs, LeftBottomSide, LeftSide, RightSide } from '@/pages/DataStudio/route';
import { PageContainer } from '@ant-design/pro-layout';
import { useAsyncEffect } from 'ahooks';
import { Layout, Menu, theme } from 'antd';
import { useEffect, useState } from 'react';
import { PersistGate } from 'redux-persist/integration/react';
import { connect, getDvaApp } from 'umi';

const { Sider, Content } = Layout;

const { useToken } = theme;

const DataStudio = (props: any) => {
  const {
    bottomContainer,
    leftContainer,
    rightContainer,
    saveDataBase,
    saveProject,
    updateToolContentHeight,
    updateBottomHeight,
    saveSession,
    saveEnv,
    updateCenterContentHeight,
    updateSelectLeftKey,
    updateSelectRightKey,
    updateSelectBottomKey,
    saveClusterConfiguration,
    activeBreadcrumbTitle,
    updateSelectBottomSubKey,
    tabs: { panes, activeKey }
  } = props;
  const isProject = isProjectTabs(panes, activeKey);
  const { token } = useToken();
  const themeValue = useThemeValue();
  const app = getDvaApp(); // 获取dva的实例
  const persist = app._store.persist;
  const { fullscreen } = useEditor();

  const getClientSize = () => ({
    width: document.documentElement.clientWidth,
    height: document.documentElement.clientHeight,
    contentHeight:
      document.documentElement.clientHeight -
      VIEW.headerNavHeight -
      VIEW.headerHeight -
      VIEW.footerHeight -
      VIEW.otherHeight
  });

  const [size, setSize] = useState(getClientSize());

  const onResize = () => {
    setSize(getClientSize());
    const newBottomHeight = !isProject
      ? 0
      : bottomContainer.selectKey === ''
      ? 0
      : bottomContainer.height;
    const centerContentHeight = getClientSize().contentHeight - newBottomHeight;
    updateCenterContentHeight(centerContentHeight);
    updateToolContentHeight(centerContentHeight - VIEW.leftMargin);
  };

  useEffect(() => {
    window.addEventListener('resize', onResize);
    onResize();
    return () => window.removeEventListener('resize', onResize);
  }, []);

  const loadData = async () => {
    Promise.all([
      getDataSourceList(),
      getTaskData(),
      getSessionData(),
      getEnvData(),
      getClusterConfigurationData()
    ]).then((res) => {
      saveDataBase(res[0]);
      saveProject(res[1]);
      saveSession(res[2]);
      saveEnv(res[3]);
      saveClusterConfiguration(res[4]);
    });
  };

  useEffect(() => {
    const newBottomHeight = !isProject
      ? 0
      : bottomContainer.selectKey === ''
      ? 0
      : bottomContainer.height;
    const centerContentHeight = size.contentHeight - newBottomHeight;
    updateCenterContentHeight(centerContentHeight);
    updateToolContentHeight(centerContentHeight - VIEW.leftMargin);
  }, [activeKey, panes]);

  useAsyncEffect(async () => {
    await loadData();
  }, []);

  const access = useAccess();

  const LeftTopMenu = (
    <Menu
      mode='inline'
      activeKey={leftContainer.selectKey}
      selectedKeys={[leftContainer.selectKey]}
      items={LeftSide.filter((tab) => AuthorizedObject({ path: tab.auth, children: tab, access }))
        .filter((tab) => {
          if (!tab.isShow) {
            return true;
          }
          if (parseInt(activeKey) < 0) {
            return TabsPageType.None;
          }
          const currentTab = (panes as TabsItemType[]).find((item) => item.key === activeKey);
          const show = tab.isShow(
            currentTab?.type ?? TabsPageType.None,
            currentTab?.subType ?? TabsPageSubType.None
          );
          // 如果当前打开的菜单等于 状态存的菜单 且 菜单不显示状态下，先切换到项目key(因为项目key 不可能不显示) 在关闭这个
          // if current open menu equal status menu and menu is not show status, first switch to project key(because project key is not show) and close this
          if (tab.key === leftContainer.selectKey && !show && panes.length > 0) {
            updateSelectLeftKey(LeftMenuKey.PROJECT_KEY);
          }
          return show;
        })
        .map((x) => ({
          key: x.key,
          label: x.label,
          icon: x.icon
        }))}
      style={{
        flexGrow: 1,
        borderBlockStart: `1px solid ${themeValue.borderColor}`,
        borderInlineEnd: `1px solid ${themeValue.borderColor}`
      }}
      onClick={(item) => updateSelectLeftKey(item.key === leftContainer.selectKey ? '' : item.key)}
    />
  );

  const LeftBottomMenu = (
    <Menu
      mode='inline'
      selectedKeys={[bottomContainer.selectKey]}
      activeKey={bottomContainer.selectKey}
      items={LeftBottomSide.filter((x) => AuthorizedObject({ path: x.auth, children: x, access }))
        .filter((tab) => {
          if (!tab.isShow) {
            return true;
          }
          if (parseInt(activeKey) < 0) {
            return TabsPageType.None;
          }
          const currentTab = (panes as TabsItemType[]).find((item) => item.key === activeKey);
          return tab.isShow(currentTab?.type ?? TabsPageType.None, currentTab?.subType);
        })
        .map((x) => ({
          key: x.key,
          label: x.label,
          icon: x.icon
        }))}
      style={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'flex-end',
        borderInlineEnd: `1px solid ${themeValue.borderColor}`
      }}
      onClick={(item) => {
        updateSelectBottomKey(item.key === bottomContainer.selectKey ? '' : item.key);
        if (
          bottomContainer.selectKey !== '' &&
          !bottomContainer.selectSubKey[item.key] &&
          LeftBottomMoreTabs[item.key]
        ) {
          updateSelectBottomSubKey(LeftBottomMoreTabs[item.key][0].key);
        }
      }}
    />
  );

  const RightTopMenu = (
    <Menu
      selectedKeys={[rightContainer.selectKey]}
      mode='inline'
      style={{
        height: '100%',
        borderInlineStart: `1px solid ${themeValue.borderColor}`,
        borderBlockStart: `1px solid ${themeValue.borderColor}`
      }}
      activeKey={rightContainer.selectKey}
      items={RightSide.filter((tab) => AuthorizedObject({ path: tab.auth, children: tab, access }))
        .filter((tab) => {
          if (!tab.isShow) {
            return true;
          }
          if (parseInt(activeKey) < 0) {
            return TabsPageType.None;
          }
          const currentTab = (panes as TabsItemType[]).find((item) => item.key === activeKey);
          return tab.isShow(currentTab?.type ?? TabsPageType.None, currentTab?.subType);
        })
        .map((x) => {
          return { key: x.key, label: x.label, icon: x.icon };
        })}
      onClick={(item) =>
        updateSelectRightKey(item.key === rightContainer.selectKey ? '' : item.key)
      }
    />
  );

  return fullscreen ? (
    <MiddleContainer />
  ) : (
    <PageContainer title={false} breadcrumb={{ style: { display: 'none' } }}>
      <PersistGate loading={null} persistor={persist}>
        <div style={{ marginInline: -10, marginTop: -6, width: size.width }}>
          <SecondHeaderContainer size={size} activeBreadcrumbTitle={activeBreadcrumbTitle} />
          <Layout
            hasSider
            style={{
              minHeight: size.contentHeight,
              maxHeight: size.contentHeight,
              paddingInline: 0
            }}
          >
            <Sider collapsed collapsedWidth={40}>
              <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
                {LeftTopMenu}
                {isProject && LeftBottomMenu}
              </div>
            </Sider>

            <Content style={{ display: 'flex', flexDirection: 'column', minWidth: 0 }}>
              <div style={{ display: 'flex' }}>
                <BtnProvider>
                  <LeftContainer
                    size={size}
                    leftContainer={leftContainer}
                    rightContainer={rightContainer}
                  />
                </BtnProvider>
                <Content
                  style={{
                    width:
                      size.width - 2 * VIEW.sideWidth - leftContainer.width - rightContainer.width
                  }}
                >
                  <MiddleContainer />
                </Content>
                <RightContainer size={size} bottomHeight={bottomContainer.height} />
              </div>
              {isProject && <BottomContainer size={size} height={bottomContainer.height} />}
            </Content>

            <Sider collapsed collapsedWidth={40}>
              {RightTopMenu}
            </Sider>
          </Layout>
          {<FooterContainer token={token} />}
        </div>
      </PersistGate>
    </PageContainer>
  );
};

export default connect(
  ({ Studio }: { Studio: StateType }) => ({
    leftContainer: Studio.leftContainer,
    rightContainer: Studio.rightContainer,
    bottomContainer: Studio.bottomContainer,
    activeBreadcrumbTitle: Studio.tabs.activeBreadcrumbTitle,
    tabs: Studio.tabs
  }),
  mapDispatchToProps
)(DataStudio);
