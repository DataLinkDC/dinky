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


import React, { useState,useEffect, useRef, useLayoutEffect } from 'react';
import { GridContent } from '@ant-design/pro-layout';
import { Menu } from 'antd';
import FlinkConfigView from './components/flinkConfig';
import styles from './style.less';
import {loadSettings} from "@/pages/Settings/function";
import {SettingsStateType} from "@/pages/Settings/model";
import {connect,useModel} from "umi";
import UserTableList from '../user';
import { useIntl, Link, history, FormattedMessage, SelectLang} from 'umi';

const { Item } = Menu;

type SettingsStateKeys = 'userManager' |'flinkConfig' | 'sysConfig';
type SettingsState = {
  mode: 'inline' | 'horizontal';
  selectKey: SettingsStateKeys;
};

type SettingsProps = {
  dispatch:any;
};



const Settings: React.FC<SettingsProps> = (props) => {
  const intl = useIntl();

  const { initialState, setInitialState } = useModel('@@initialState');
  const menuMapAdmin: Record<string, React.ReactNode> = {
    userManager: intl.formatMessage({id: 'pages.settings.UserManagement', defaultMessage: '用户管理',}),
    flinkConfig: intl.formatMessage({id: 'pages.settings.Flink', defaultMessage: 'Flink 设置',}),
  };
  const menuMapUser: Record<string, React.ReactNode> = {
    flinkConfig: intl.formatMessage({id: 'pages.settings.Flink', defaultMessage: 'Flink 设置',}),
  };

  const menuMap: Record<string, React.ReactNode> = (initialState?.currentUser?.isAdmin)?menuMapAdmin:menuMapUser;

  const {dispatch} = props;
  const [initConfig, setInitConfig] = useState<SettingsState>({
    mode: 'inline',
    selectKey: 'flinkConfig',
  });
  const dom = useRef<HTMLDivElement>();
  loadSettings(dispatch);
  const resize = () => {
    requestAnimationFrame(() => {
      if (!dom.current) {
        return;
      }
      let mode: 'inline' | 'horizontal' = 'inline';
      const { offsetWidth } = dom.current;
      if (dom.current.offsetWidth < 641 && offsetWidth > 400) {
        mode = 'horizontal';
      }
      if (window.innerWidth < 768 && offsetWidth > 400) {
        mode = 'horizontal';
      }
      setInitConfig({ ...initConfig, mode: mode as SettingsState['mode'] });
    });
  };

  useLayoutEffect(() => {
    if (dom.current) {
      window.addEventListener('resize', resize);
      resize();
    }
    return () => {
      window.removeEventListener('resize', resize);
    };
  }, [dom.current]);

  const getMenu = () => {
    console.log(menuMap);
    return Object.keys(menuMap).map((item) => <Item key={item}>{menuMap[item]}</Item>);
  };

  const renderChildren = () => {
    const { selectKey } = initConfig;
    switch (selectKey) {
      case 'userManager':
        return <UserTableList />;
      case 'flinkConfig':
        return <FlinkConfigView />;
      default:
        return null;
    }
  };

  return (
    <GridContent>
      <div
        className={styles.main}
        ref={(ref) => {
          if (ref) {
            dom.current = ref;
          }
        }}
      >
        <div className={styles.leftMenu}>
          <Menu
            mode={initConfig.mode}
            selectedKeys={[initConfig.selectKey]}
            onClick={({ key }) => {
              setInitConfig({
                ...initConfig,
                selectKey: key as SettingsStateKeys,
              });
            }}
          >
            {getMenu()}
          </Menu>
        </div>
        <div className={styles.right}>
          <div className={styles.title}>{menuMap[initConfig.selectKey]}</div>
          {renderChildren()}
        </div>
      </div>
    </GridContent>
  );
};
export default connect(({Settings}: { Settings: SettingsStateType }) => ({
  sqlSubmitJarPath: Settings.sqlSubmitJarPath,
  sqlSubmitJarParas: Settings.sqlSubmitJarParas,
  sqlSubmitJarMainAppClass: Settings.sqlSubmitJarMainAppClass,
}))(Settings);
