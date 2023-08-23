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

import useThemeValue from '@/hooks/useThemeValue';
import BottomContainer from '@/pages/DataStudio/BottomContainer';
import { getConsoleData } from '@/pages/DataStudio/BottomContainer/Console/service';
import FooterContainer from '@/pages/DataStudio/FooterContainer';
import {
  getCurrentTab,
  isDataStudioTabsItemType,
  mapDispatchToProps
} from '@/pages/DataStudio/function';
import HeaderContainer from '@/pages/DataStudio/HeaderContainer';
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
import { Layout, Menu, Modal, theme, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { PersistGate } from 'redux-persist/integration/react';
import { connect, getDvaApp } from 'umi';

const { Text } = Typography;

const { Sider, Content } = Layout;

const { useToken } = theme;
const format = (percent?: number, successPercent?: number) => (
  <div style={{ position: 'relative', height: '100%' }}>
    <div
      style={{
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)'
      }}
    >
      {`${percent}%`}
    </div>
  </div>
);
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

      if (JSON.stringify(res[key]) !== JSON.stringify(params.taskData[key])) {
        console.log('key', key, res[key], params.taskData[key]);
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

  /**
   * 渲染头部
   */
  const renderHeaderContainer = () => (
    <HeaderContainer size={size} activeBreadcrumbTitle={activeBreadcrumbTitle} />
  );

  /**
   * 渲染左侧侧边栏
   */
  const renderLeftContainer = () => <LeftContainer size={size} />;

  /**
   * 渲染右侧侧边栏
   */
  const renderRightContainer = () => <RightContainer size={size} bottomHeight={bottomHeight} />;

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

  return (
    <PersistGate loading={null} persistor={persist}>
      <div style={{ marginInline: -10, marginBlock: -5 }}>
        {/* 渲染 header */}
        {renderHeaderContainer()}
        <Layout hasSider style={{ minHeight: size.contentHeight, paddingInline: 0 }}>
          {/*渲染左侧侧边栏*/}
          <Sider collapsed collapsedWidth={40}>
            <Menu
              mode='inline'
              selectedKeys={[leftContainer.selectKey]}
              items={LeftSide.map((x) => ({
                key: x.key,
                label: x.label,
                icon: x.icon
              }))}
              style={{
                height: '50%',
                borderBlockStart: `1px solid ${themeValue.borderColor}`,
                borderInlineEnd: `1px solid ${themeValue.borderColor}`
              }}
              onClick={(item) =>
                updateSelectLeftKey(item.key === leftContainer.selectKey ? '' : item.key)
              }
            />

            {/*底部菜单*/}
            <Menu
              mode='inline'
              selectedKeys={[bottomContainer.selectKey]}
              items={LeftBottomSide.map((x) => ({
                key: x.key,
                label: x.label,
                icon: x.icon
              }))}
              style={{
                display: 'flex',
                height: '50%',
                flexDirection: 'column-reverse',
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
          </Sider>

          <Content
            style={{
              flexDirection: 'column-reverse',
              display: 'flex',
              height: size.contentHeight
            }}
          >
            {/*渲染底部内容*/}
            {<BottomContainer size={size} />}

            <div
              style={{
                display: 'flex',
                position: 'absolute',
                top: VIEW.headerHeight,
                width: size.width - VIEW.sideWidth * 2
              }}
            >
              {renderLeftContainer()}

              <Content
                style={{
                  width:
                    size.width - 2 * VIEW.sideWidth - leftContainer.width - rightContainer.width
                }}
              >
                <MiddleContainer />
              </Content>

              {renderRightContainer()}
            </div>
          </Content>

          {/* 渲染右侧侧边栏 */}
          <Sider collapsed collapsedWidth={40}>
            <Menu
              selectedKeys={[rightContainer.selectKey]}
              mode='inline'
              style={{
                height: '100%',
                borderInlineStart: '1px solid ' + themeValue.borderColor,
                borderBlockStart: '1px solid ' + themeValue.borderColor
              }}
              items={RightSide.filter((x) => {
                if (!x.isShow) {
                  return true;
                }
                if (parseInt(tabs.activeKey) < 0) {
                  return TabsPageType.None;
                }
                const v = (tabs.panes as TabsItemType[]).find(
                  (item) => item.key === tabs.activeKey
                );
                return x.isShow(v?.type ?? TabsPageType.None, v?.subType);
              }).map((x) => {
                return { key: x.key, label: x.label, icon: x.icon };
              })}
              onClick={(item) =>
                updateSelectRightKey(item.key === rightContainer.selectKey ? '' : item.key)
              }
            />
          </Sider>
        </Layout>
        {/* 页脚 */}
        {<FooterContainer token={token} />}
      </div>
    </PersistGate>
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
