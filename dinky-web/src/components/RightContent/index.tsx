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

import {chooseTenantSubmit} from '@/services/api';
import { VERSION } from '@/services/constants';
import {parseJsonStr, setTenantStorageAndCookie} from '@/services/function';
import {l} from '@/utils/intl';
import {
  CreditCardOutlined,
  FullscreenExitOutlined,
  FullscreenOutlined,
  GlobalOutlined
} from '@ant-design/icons';
import {ActionType} from '@ant-design/pro-components';
import {useEmotionCss} from '@ant-design/use-emotion-css';
import {SelectLang, useModel} from '@umijs/max';
import {Modal, notification, Select, Space, Tooltip} from 'antd';
import {OptionType} from 'dayjs';
import React, {useRef, useState} from 'react';
import screenfull from 'screenfull';
import Avatar from './AvatarDropdown';

export type SiderTheme = 'light' | 'dark';

const GlobalHeaderRight: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [fullScreen, setFullScreen] = useState(true);

  const {initialState} = useModel('@@initialState');
  const {currentUser} = initialState || {};
  if (!initialState || !initialState.settings) {
    return null;
  }

  const actionClassName = useEmotionCss(({token}) => {
    return {
      display: 'flex',
      float: 'right',
      justifyContent: 'center',
      alignItems: 'center',
      height: '48px',
      marginLeft: 'auto',
      overflow: 'hidden',
      cursor: 'pointer',
      padding: '0 9px',
      color: '#fff',
      borderRadius: token.borderRadius,
      '&:hover': {
        backgroundColor: token.colorBgTextHover,
      },
    };
  });

  const fullScreenClassName = useEmotionCss(({token}) => {
    return {
      display: 'flex',
      float: 'right',
      height: '48px',
      marginLeft: 'auto',
      overflow: 'hidden',
      cursor: 'pointer',
      padding: '0 12px',
      borderRadius: token.borderRadius,
      color: 'red',
      '&:hover': {
        backgroundColor: token.colorPrimary,
      },
    };
  });


  const tenantHandleChange = (option: OptionType) => {
    const result = parseJsonStr(option as string);
    const tenantId = result.value;

    Modal.confirm({
      title: l('menu.account.checkTenant'),
      content: l('menu.account.checkTenantConfirm', '', {tenantCode: result.children}),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        const result = await chooseTenantSubmit({tenantId: tenantId});
        setTenantStorageAndCookie(tenantId);
        result.code == 0 ? notification.success({message: result.msg,}) : notification.error({message: result.msg});
        actionRef.current?.reload();
      },
    });
  };

  const screenFull = () => {
    setFullScreen(screenfull.isFullscreen);
    if (screenfull.isEnabled) {
      screenfull.toggle();
    }
  };

  const genTenantListForm = () => {
    const tenants: any[] = [];
    currentUser?.tenantList?.forEach((item) => {
      tenants.push(
        <Select.Option key={item.id} value={item.id}>
          {item.tenantCode}
        </Select.Option>,
      );
    });
    return tenants;
  };

  return (
    <>
      <Tooltip
        placement="bottom"
        title={<span>{fullScreen ? l('global.fullScreen') : l('global.fullScreen.exit')}</span>}
      >
        {fullScreen ?
          <FullscreenOutlined
            style={{color: 'white'}}
            className={fullScreenClassName}
            onClick={screenFull}
          /> :
          <FullscreenExitOutlined
            style={{color: 'white'}}
            className={fullScreenClassName}
            onClick={screenFull}
          />
        }
      </Tooltip>
      <Avatar menu={true}/>
      {
        <>
          <span className={actionClassName}>{l('menu.tenants')}</span>
          <Select className={actionClassName}
                  style={{width: '18vh'}}
                  defaultValue={currentUser?.currentTenant?.tenantCode?.toString() || ''}
                  onChange={(value, option) => {
                    tenantHandleChange(option as OptionType);
                  }}
          >
            {genTenantListForm()}
          </Select>
        </>
      }
      <Tooltip
        placement="bottom"
        title={<span>{l('menu.version', '', {version: VERSION})}</span>}
      >
        <Space className={actionClassName}>{l('menu.version', '', {version: VERSION})}</Space>
      </Tooltip>
      <SelectLang icon={<GlobalOutlined/>} className={actionClassName}/>
    </>
  );
};
export default GlobalHeaderRight;
