/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
import { FlexCenterDiv } from '@/components/StyledComponents';
import { getCurrentData, getCurrentTab, mapDispatchToProps } from '@/pages/DataStudio/function';
import Explain from '@/pages/DataStudio/HeaderContainer/Explain';
import FlinkGraph from '@/pages/DataStudio/HeaderContainer/FlinkGraph';
import {
  buildBreadcrumbItems,
  projectCommonShow
} from '@/pages/DataStudio/HeaderContainer/function';
import {
  executeSql,
  getJobPlan,
  isOnline,
  isSql,
  offLineTask
} from '@/pages/DataStudio/HeaderContainer/service';
import {
  DataStudioParams,
  DataStudioTabsItemType,
  StateType,
  TabsPageType,
  TaskDataType,
  VIEW
} from '@/pages/DataStudio/model';
import { ConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { SettingConfigKeyEnum } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import { handlePutDataJson } from '@/services/BusinessCrud';
import { BaseConfigProperties } from '@/types/SettingCenter/data';
import { l } from '@/utils/intl';
import { ErrorNotification } from '@/utils/messages';
import { connect } from '@@/exports';
import {
  EnvironmentOutlined,
  FlagTwoTone,
  MoreOutlined,
  PauseCircleTwoTone,
  PlayCircleTwoTone,
  SafetyCertificateTwoTone,
  SaveTwoTone,
  SendOutlined,
  SmileOutlined
} from '@ant-design/icons';
import { Breadcrumb, Button, Descriptions, message, Modal, notification, Space } from 'antd';
import React, { useEffect, useState } from 'react';

const headerStyle: React.CSSProperties = {
  display: 'inline-flex',
  lineHeight: VIEW.headerHeight + 'px',
  height: VIEW.headerHeight,
  fontStyle: 'normal',
  fontWeight: 'bold',
  fontSize: '16px',
  padding: '4px 10px'
};

type ButtonRoute = {
  icon: React.ReactNode;
  title: string;
  click: () => void;
  hotKey?: (e: KeyboardEvent) => boolean;
  hotKeyDesc?: string;
  isShow?: (type?: TabsPageType, subType?: string, data?: any) => boolean;
};

const HeaderContainer = (props: any) => {
  const {
    size,
    activeBreadcrumbTitle,
    tabs: { panes, activeKey },
    saveTabs,
    updateJobRunningMsg,
    queryDsConfig,
    dsConfig
  } = props;

  const [modal, contextHolder] = Modal.useModal();
  const [notificationApi, notificationContextHolder] = notification.useNotification();
  const [messageApi, messageContextHolder] = message.useMessage();
  const [enableDs, setEnableDs] = useState<boolean>(false);

  useEffect(() => {
    queryDsConfig(SettingConfigKeyEnum.DOLPHIN_SCHEDULER.toLowerCase());
  }, []);

  useEffect(() => {
    // 检查是否开启 ds 配置 & 如果
    if (!dsConfig) {
      dsConfig.map((item: BaseConfigProperties) => {
        if (item.key === 'dolphinscheduler.settings.enable') {
          setEnableDs(item.value === 'true');
        }
      });
    }
  }, [dsConfig]);

  const handlerStop = () => {
    const current = getCurrentData(panes, activeKey);
    if (!current) {
      return;
    }

    modal.confirm({
      title: l('pages.datastudio.editor.stop.job'),
      content: l('pages.datastudio.editor.stop.jobConfirm', '', {
        jobName: current.name
      }),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        offLineTask(l('pages.datastudio.editor.stop.job'), current.id, 'canceljob').then(
          (result) => {
            (
              getCurrentTab(panes, activeKey)?.params as DataStudioParams
            ).taskData.jobInstanceId = 0;
            saveTabs({ ...props.tabs });
          }
        );
      }
    });
  };
  const handlerExec = () => {
    const current = getCurrentData(panes, activeKey);
    if (!current) {
      return;
    }

    if (!isSql(current.dialect) && !isOnline(current.type)) {
      messageApi.warning(l('pages.datastudio.editor.execute.warn', '', { type: current.type }));
      return;
    }

    const param: TaskDataType = {
      ...current,
      jobName: current.name,
      taskId: current.id
    };

    const taskKey = Math.random() * 1000 + '';

    notificationApi.success({
      message: l('pages.datastudio.editor.submitting', '', {
        jobName: param.name
      }),
      description: param.statement.substring(0, 40) + '...',
      duration: null,
      key: taskKey,
      icon: <SmileOutlined style={{ color: '#108ee9' }} />
    });

    executeSql(l('pages.datastudio.editor.submitting', '', { jobName: param.name }), param).then(
      (res) => {
        notificationApi.destroy(taskKey);
        if (!res) {
          return;
        }
        updateJobRunningMsg({
          taskId: current.id,
          jobName: current.name,
          jobState: res.datas.status,
          runningLog: res.msg
        });
        if (res.datas.success) {
          messageApi.success(l('pages.datastudio.editor.exec.success'));
          (getCurrentTab(panes, activeKey)?.params as DataStudioParams).taskData.jobInstanceId =
            res.datas.jobInstanceId;
          saveTabs({ ...props.tabs });
        }
      }
    );
  };

  const routes: ButtonRoute[] = [
    // 保存按钮 icon
    {
      icon: <SaveTwoTone />,
      title: l('button.save'),
      click: () => {
        const current = getCurrentData(panes, activeKey);
        handlePutDataJson('/api/task', current).then(() => saveTabs({ ...props.tabs }));
      },
      hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's',
      hotKeyDesc: 'Ctrl+S',
      isShow: projectCommonShow
    },
    {
      // 检查 sql按钮
      icon: <SafetyCertificateTwoTone />,
      title: l('pages.datastudio.editor.check'),
      click: () => {
        modal.confirm({
          title: l('pages.datastudio.explain.validate.msg'),
          width: '100%',
          icon: null,
          content: <Explain />,
          cancelButtonProps: { style: { display: 'none' } }
        });
      },
      isShow: projectCommonShow
      // hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's'
    },
    {
      // 执行图按钮
      icon: <FlagTwoTone />,
      title: l('button.graph'),
      click: () => {
        const currentData = getCurrentData(panes, activeKey);
        const res = getJobPlan(l('pages.datastudio.editor.explan.tip'), currentData);
        res.then((result) => {
          if (result) {
            modal.confirm({
              title: l('pages.datastudio.editor.explan.tip'),
              width: '100%',
              icon: null,
              content: <FlinkGraph data={result.datas} />,
              cancelButtonProps: { style: { display: 'none' } }
            });
          }
        });
      },
      // hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's'
      isShow: projectCommonShow
    },
    {
      // 执行按钮
      icon: <PlayCircleTwoTone />,
      title: l('pages.datastudio.editor.exec'),
      click: handlerExec,
      hotKey: (e: KeyboardEvent) => e.shiftKey && e.key === 'F10',
      hotKeyDesc: 'Shift+F10',
      isShow: (type?: TabsPageType, subType?: string, data?: any) =>
        type === TabsPageType.project && !data?.jobInstanceId
    },
    {
      // 停止按钮
      icon: <PauseCircleTwoTone />,
      title: l('pages.datastudio.editor.stop'),
      click: handlerStop,
      isShow: (type?: TabsPageType, subType?: string, data?: any) =>
        type === TabsPageType.project && data?.jobInstanceId
      // hotKey: (e: KeyboardEvent) => e.shiftKey && e.key === 'F10',
      // hotKeyDesc: "Shift+F10"
    },
    // {
    //   // 异步提交按钮
    //   icon: <RocketTwoTone/>,
    //   title: l('button.async'),
    //   click: () => {
    //     console.log("ctrl+s")
    //   },
    //   // hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's'
    // },
    {
      // 推送海豚, 此处需要将系统设置中的 ds 的配置拿出来做判断 启用才展示
      icon: <SendOutlined className={'blue-icon'} />,
      title: l('button.push'),
      click: () => {},
      hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's',
      isShow: () => enableDs
    },
    // {
    //   // 发布按钮
    //   icon: <PauseCircleTwoTone/>,
    //   title: l('button.publish'),
    //   click: () => {
    //     console.log("ctrl+s")
    //   },
    //   // hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's'
    // }, {
    //   // api 按钮
    //   icon: <ApiTwoTone/>,
    //   title: l('button.api'),
    //   click: () => {
    //     console.log("ctrl+s")
    //   },
    //   // hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's'
    // },
    {
      //
      icon: <MoreOutlined />,
      title: 'More',
      click: () => {},
      isShow: () => true
      // hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's'
    }
  ];

  /**
   * @description: 生成面包屑
   */
  const renderBreadcrumbItems = () => {
    if (!activeBreadcrumbTitle) {
      return (
        <Space>
          <EnvironmentOutlined />
          <span>Guide Page</span>
        </Space>
      );
    }

    return (
      <FlexCenterDiv style={{ width: (size.width - 2 * VIEW.paddingInline) / 2 }}>
        <Breadcrumb separator={'>'} items={buildBreadcrumbItems(activeBreadcrumbTitle)} />
      </FlexCenterDiv>
    );
  };
  const renderHotkey = () => {
    document.onkeydown = (e) => {
      routes
        .filter((r) => r.hotKey?.(e))
        .forEach((r) => {
          r.click();
          e.preventDefault();
        });
    };
  };
  renderHotkey();

  /**
   * @description: 渲染右侧按钮
   */
  const renderRightButtons = () => {
    return (
      <>
        <Space size={'middle'} align={'center'} direction={'horizontal'} wrap>
          {routes
            .filter((x) => {
              if (x.isShow) {
                const currentTab = getCurrentTab(panes, activeKey);
                if (currentTab) {
                  return x.isShow(
                    currentTab?.type,
                    currentTab?.subType,
                    (currentTab as DataStudioTabsItemType)?.params.taskData
                  );
                }
              }
              return false;
            })
            .map((route) => {
              const { icon, title, click, hotKeyDesc } = route;
              return (
                <Button
                  key={title}
                  size={'small'}
                  type={'text'}
                  icon={icon}
                  title={title + (hotKeyDesc ? ' ' + hotKeyDesc : '')}
                  onClick={click}
                />
              );
            })}
        </Space>
        {contextHolder}
        {notificationContextHolder}
        {messageContextHolder}
      </>
    );
  };

  /**
   * render
   */
  return (
    <Descriptions column={2} size={'middle'} layout={'horizontal'} key={'h'} style={headerStyle}>
      <Descriptions.Item>{renderBreadcrumbItems()}</Descriptions.Item>
      <Descriptions.Item contentStyle={{ display: 'flex', flexDirection: 'row-reverse' }}>
        {renderRightButtons()}
      </Descriptions.Item>
    </Descriptions>
  );
};

export default connect(
  ({ Studio, Config }: { Studio: StateType; Config: ConfigStateType }) => ({
    tabs: Studio.tabs,
    dsConfig: Config.dsConfig
  }),
  mapDispatchToProps
)(HeaderContainer);
