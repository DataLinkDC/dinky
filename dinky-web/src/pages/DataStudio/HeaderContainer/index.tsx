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
import {FlexCenterDiv} from '@/components/StyledComponents';
import {getCurrentData, getCurrentTab, mapDispatchToProps} from '@/pages/DataStudio/function';
import Explain from '@/pages/DataStudio/HeaderContainer/Explain';
import FlinkGraph from '@/pages/DataStudio/HeaderContainer/FlinkGraph';
import {
  buildBreadcrumbItems,
  projectCommonShow
} from '@/pages/DataStudio/HeaderContainer/function';
import {
  cancelTask,
  executeSql,
  getJobPlan,
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
import {ConfigStateType} from '@/pages/SettingCenter/GlobalSetting/model';
import {SettingConfigKeyEnum} from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import {handlePutDataJson} from '@/services/BusinessCrud';
import {BaseConfigProperties} from '@/types/SettingCenter/data';
import {l} from '@/utils/intl';
import {ErrorNotification} from '@/utils/messages';
import {connect} from '@@/exports';
import {
  ApartmentOutlined,
  CaretRightFilled,
  EnvironmentOutlined,
  MoreOutlined, PauseOutlined, SaveOutlined, ScheduleOutlined,
  SendOutlined
} from '@ant-design/icons';
import {Breadcrumb,  Descriptions, message, Modal,  Space} from 'antd';
import React, {CSSProperties, useEffect, useState} from 'react';
import {ButtonType} from "antd/es/button/buttonHelpers";
import {LoadingBtn} from "@/components/CallBackButton/LoadingBtn";

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
  style?: CSSProperties | undefined;
  type?: ButtonType;
  isDanger?: boolean;
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
    tabs: {panes, activeKey},
    saveTabs,
    updateJobRunningMsg,
    queryDsConfig,
    dsConfig
  } = props;

  const [modal, contextHolder] = Modal.useModal();
  // const [notificationApi, notificationContextHolder] = notification.useNotification();
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
        cancelTask(l('pages.datastudio.editor.stop.job'), current.id).then(
          () => {
            (getCurrentTab(panes, activeKey)?.params as DataStudioParams).taskData.jobInstanceId = 0;
            saveTabs({...props.tabs});
          }
        );
      }
    });
  };

  const handlerExec = async () => {
    const current = getCurrentData(panes, activeKey);
    if (!current) {
      return;
    }

    const param: TaskDataType = {
      ...current,
      jobName: current.name,
      taskId: current.id
    };

    const res = await executeSql(l('pages.datastudio.editor.submitting', '', {jobName: param.name}), param);

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
      saveTabs({...props.tabs});
    } else {
      ErrorNotification(
        res.datas.error,
        l('pages.datastudio.editor.exec.error', '', {jobName: param.name}),
        null
      );
    }

  };

  const routes: ButtonRoute[] = [
    // 保存按钮 icon
    {
      icon: <SaveOutlined />,
      title: l('button.save'),
      click: async () => {
        const current = getCurrentData(panes, activeKey);
        await handlePutDataJson('/api/task', current);
        saveTabs({...props.tabs});
      },
      hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's',
      hotKeyDesc: 'Ctrl+S',
      isShow: projectCommonShow
    },
    {
      // 执行图按钮
      icon: <ApartmentOutlined />,
      title: l('button.graph'),
      click: async () => {
        const currentData = getCurrentData(panes, activeKey);
        const result = await getJobPlan(l('pages.datastudio.editor.explan.tip'), currentData);
        if (result) {
          modal.confirm({
            title: l('pages.datastudio.editor.explan.tip'),
            width: '100%',
            icon: null,
            content: <FlinkGraph data={result.datas}/>,
            cancelButtonProps: {style: {display: 'none'}}
          });
        }
      },
      // hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's'
      isShow: projectCommonShow
    },
    {
      // 检查 sql按钮
      icon: <ScheduleOutlined />,
      title: l('pages.datastudio.editor.check'),
      click: () => {
        modal.confirm({
          title: l('pages.datastudio.explain.validate.msg'),
          width: '100%',
          icon: null,
          content: <Explain/>,
          cancelButtonProps: {style: {display: 'none'}}
        });
      },
      isShow: projectCommonShow
      // hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's'
    },
    {
      // 执行按钮
      style: {background: "#52c41a"},
      type: 'primary',
      isDanger: false,
      icon: <CaretRightFilled />,
      title: l('pages.datastudio.editor.exec'),
      click: handlerExec,
      hotKey: (e: KeyboardEvent) => e.shiftKey && e.key === 'F10',
      hotKeyDesc: 'Shift+F10',
      isShow: (type?: TabsPageType, subType?: string, data?: any) =>
        type === TabsPageType.project && !data?.jobInstanceId
    },
    {
      // 停止按钮
      type: 'primary',
      isDanger: true,
      icon: <PauseOutlined />,
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
      icon: <SendOutlined className={'blue-icon'}/>,
      title: l('button.push'),
      click: () => {
      },
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
      icon: <MoreOutlined/>,
      title: '',
      click: () => {
      },
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
          <EnvironmentOutlined/>
          <span>Guide Page</span>
        </Space>
      );
    }

    return (
      <FlexCenterDiv style={{width: (size.width - 2 * VIEW.paddingInline) / 2}}>
        <Breadcrumb separator={'>'} items={buildBreadcrumbItems(activeBreadcrumbTitle)}/>
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
    const currentTab = getCurrentTab(panes, activeKey);
    const taskData = (currentTab as DataStudioTabsItemType)?.params.taskData;
    return (
      <div style={{padding: '4px'}}>
        <Space size={'small'} align={'center'} direction={'horizontal'} wrap>
          {routes
            .filter((x) => {
              if (x.isShow) {
                if (currentTab) {
                  return x.isShow(
                    currentTab?.type,
                    currentTab?.subType,
                    taskData
                  );
                }
              }
              return false;
            })
            .map((route) => {
              const {style, icon, title, click, hotKeyDesc, isDanger, type} = route;
              return (
                <LoadingBtn
                  style={style}
                  key={title}
                  size={'small'}
                  type={type || "text"}
                  icon={icon}
                  danger={isDanger || false}
                  title={title + (hotKeyDesc ? ' ' + hotKeyDesc : '')}
                  onClick={click}
                >{title}</LoadingBtn>
              );
            })}
        </Space>
        {contextHolder}
        {/*{notificationContextHolder}*/}
        {messageContextHolder}
      </div>
    );
  };

  /**
   * render
   */
  return (
    <Descriptions column={2} size={'middle'} layout={'horizontal'} key={'h'} style={headerStyle}>
      <Descriptions.Item>{renderBreadcrumbItems()}</Descriptions.Item>
      <Descriptions.Item contentStyle={{display: 'flex', flexDirection: 'row-reverse'}}>
        {renderRightButtons()}
      </Descriptions.Item>
    </Descriptions>
  );
};

export default connect(
  ({Studio, Config}: { Studio: StateType; Config: ConfigStateType }) => ({
    tabs: Studio.tabs,
    dsConfig: Config.dsConfig
  }),
  mapDispatchToProps
)(HeaderContainer);
