/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { AuthorizedObject, useAccess } from '@/hooks/useAccess';
import useThemeValue from '@/hooks/useThemeValue';
import BottomContainer from '@/pages/DataStudio/BottomContainer';
import { getConsoleData } from '@/pages/DataStudio/BottomContainer/Console/service';
import FooterContainer from '@/pages/DataStudio/FooterContainer';
import {
  getCurrentTab,
  isDataStudioTabsItemType,
  mapDispatchToProps
} from '@/pages/DataStudio/function';
import SecondHeaderContainer from '@/pages/DataStudio/HeaderContainer';
import LeftContainer from '@/pages/DataStudio/LeftContainer';
import { getDataBase } from '@/pages/DataStudio/LeftContainer/MetaData/service';
import { getTaskData, getTaskDetails } from '@/pages/DataStudio/LeftContainer/Project/service';
import MiddleContainer from '@/pages/DataStudio/MiddleContainer';
import {
  StateType,
  TabsItemType,
  TabsPageType,
  TaskDataType,
  VIEW
} from '@/pages/DataStudio/model';
import RightContainer from '@/pages/DataStudio/RightContainer';
import {
  getClusterConfigurationData,
  getEnvData,
  getSessionData
} from '@/pages/DataStudio/RightContainer/JobConfig/service';
import { LeftBottomMoreTabs, LeftBottomSide, LeftSide, RightSide } from '@/pages/DataStudio/route';
import { l } from '@/utils/intl';
import { PageContainer } from '@ant-design/pro-layout';
import { Layout, Menu, Modal, theme, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { PersistGate } from 'redux-persist/integration/react';
import { connect, getDvaApp } from 'umi';

const { Text } = Typography;

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
    updateBottomConsole,
    saveSession,
    saveEnv,
    saveTabs,
    updateCenterContentHeight,
    updateSelectLeftKey,
    updateSelectRightKey,
    updateSelectBottomKey,
    saveClusterConfiguration,
    activeBreadcrumbTitle,
    updateSelectBottomSubKey,
    tabs
  } = props;
  const { token } = useToken();
  const themeValue = useThemeValue();
  const [isModalUpdateTabContentOpen, setIsModalUpdateTabContentOpen] = useState(false);
  const [newTabData, setNewTabData] = useState<TaskDataType>();
  const app = getDvaApp(); // 获取dva的实例
  const persist = app._store.persist;
  const bottomHeight = bottomContainer.selectKey === '' ? 0 : bottomContainer.height;

  const getClientSize = () => ({
    width: document.documentElement.clientWidth,
    height: document.documentElement.clientHeight,
    contentHeight:
      document.documentElement.clientHeight -
      VIEW.headerHeight -
      VIEW.headerNavHeight -
      VIEW.footerHeight -
      VIEW.otherHeight
  });

  const [size, setSize] = useState(getClientSize());

  const onResize = () => {
    setSize(getClientSize());
    const centerContentHeight = getClientSize().contentHeight - bottomHeight;
    updateCenterContentHeight(centerContentHeight);
    updateToolContentHeight(centerContentHeight - VIEW.midMargin);
  };

  useEffect(() => {
    window.addEventListener('resize', onResize);
    onResize();
    return () => window.removeEventListener('resize', onResize);
  }, []);

  useEffect(() => {
    if (isModalUpdateTabContentOpen) {
      Modal.confirm({
        title: l('pages.datastudio.help.sqlChanged'),
        keyboard: true,
        content: (
          <>
            {' '}
            <Text type={'danger'}>{l('pages.datastudio.help.sqlChangedPrompt')}</Text>
          </>
        ),
        onOk: updateTabContent,
        onCancel: () => setIsModalUpdateTabContentOpen(false)
      });
    }
  }, [isModalUpdateTabContentOpen]);

  const loadData = async () => {
    Promise.all([
      getDataBase(),
      getConsoleData(),
      getTaskData(),
      getSessionData(),
      getEnvData(),
      getClusterConfigurationData()
    ]).then((res) => {
      saveDataBase(res[0]);
      updateBottomConsole(res[1]);
      saveProject(res[2]);
      saveSession(res[3]);
      saveEnv(res[4]);
      saveClusterConfiguration(res[5]);
    });

    // 判断是否需要更新tab内容
    if (!tabs.activeKey) {
      return;
    }

    const currentTab = getCurrentTab(tabs.panes, tabs.activeKey);
    if (!isDataStudioTabsItemType(currentTab)) {
      return;
    }

    const params = currentTab.params;
    const res = await getTaskDetails(params.taskId);

    const changed = Object.keys(params.taskData).some((key) => {
      // ignore this property
      if (['updateTime', 'createTime', 'jobInstanceId'].includes(key)) {
        return false;
      }

      if (res && JSON.stringify(res[key]) !== JSON.stringify(params.taskData[key])) {
        return true;
      }
    });

    if (changed) {
      setIsModalUpdateTabContentOpen(true);
      setNewTabData(res);
    }
  };

  useEffect(() => {
    loadData();
    onResize();
  }, []);

  const updateTabContent = () => {
    const currentTab = getCurrentTab(tabs.panes, tabs.activeKey);
    if (!isDataStudioTabsItemType(currentTab)) {
      return;
    }

    if (!newTabData) return;

    currentTab.params.taskData = newTabData;
    saveTabs({ ...tabs });
    setIsModalUpdateTabContentOpen(false);
  };

  const access = useAccess();

  const LeftTopMenu = (
    <Menu
      mode='inline'
      selectedKeys={[leftContainer.selectKey]}
      items={LeftSide.filter((x) => AuthorizedObject({ path: x.auth, children: x, access })).map(
        (x) => ({
          key: x.key,
          label: x.label,
          icon: x.icon
        })
      )}
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
      items={LeftBottomSide.filter((x) =>
        AuthorizedObject({ path: x.auth, children: x, access })
      ).map((x) => ({
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
      items={RightSide.filter((x) => AuthorizedObject({ path: x.auth, children: x, access }))
        .filter((x) => {
          if (!x.isShow) {
            return true;
          }
          if (parseInt(tabs.activeKey) < 0) {
            return TabsPageType.None;
          }
          const v = (tabs.panes as TabsItemType[]).find((item) => item.key === tabs.activeKey);
          return x.isShow(v?.type ?? TabsPageType.None, v?.subType);
        })
        .map((x) => {
          return { key: x.key, label: x.label, icon: x.icon };
        })}
      onClick={(item) =>
        updateSelectRightKey(item.key === rightContainer.selectKey ? '' : item.key)
      }
    />
  );

  return (
    <PageContainer title={false} breadcrumb={{ style: { display: 'none' } }}>
      <PersistGate loading={null} persistor={persist}>
        <div style={{ marginInline: -10, width: size.width }}>
          <SecondHeaderContainer size={size} activeBreadcrumbTitle={activeBreadcrumbTitle} />
          <Layout hasSider style={{ minHeight: size.contentHeight, paddingInline: 0 }}>
            <Sider collapsed collapsedWidth={40}>
              <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
                {LeftTopMenu}
                {LeftBottomMenu}
              </div>
            </Sider>

            <Content style={{ display: 'flex', flexDirection: 'column' }}>
              <div style={{ display: 'flex', width: size.width - VIEW.sideWidth * 2 }}>
                <LeftContainer size={size} />
                <Content
                  style={{
                    width:
                      size.width - 2 * VIEW.sideWidth - leftContainer.width - rightContainer.width
                  }}
                >
                  <MiddleContainer />
                </Content>
                <RightContainer size={size} bottomHeight={bottomHeight} />
              </div>
              {<BottomContainer size={size} />}
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
