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

import React, { ReactNode, useCallback, useEffect, useState } from 'react';
import { Button, Tooltip } from 'antd';
import { sleep } from '@antfu/utils';

// 快捷键属性
export type HotKeyProps = {
  enable: boolean;
  hotKeyDesc: string;
  hotKeyHandle: (e: KeyboardEvent) => boolean;
};
export type RunToolBarButtonProps = {
  showDesc: boolean;
  desc: string;
  icon: ReactNode;
  onClick?: () => Promise<void>;
  color?: string;
  sleepTime?: number;
  hotKey?: HotKeyProps;
  isShow?: boolean;
  disabled?: boolean;
};

export default (props: RunToolBarButtonProps) => {
  const {
    showDesc,
    desc,
    icon,
    onClick,
    color,
    sleepTime,
    hotKey,
    isShow = true,
    disabled = false
  } = props;
  const [loading, setLoading] = useState(false);
  const style = color ? { color: color } : {};

  const onClickHandle = useCallback(async () => {
    setLoading(true);
    if (onClick) {
      try {
        await onClick();
      } catch (e) {}
    }
    await sleep(sleepTime ?? 100);
    setLoading(false);
  }, [onClick, sleepTime]);
  useEffect(() => {
    const hotKeyFuncHandle = (e: KeyboardEvent) => {
      if (hotKey?.hotKeyHandle(e)) {
        e.preventDefault();
        onClickHandle().then();
      }
    };
    if (hotKey?.enable) {
      document.addEventListener('keydown', hotKeyFuncHandle);
    }
    return () => {
      if (hotKey?.enable) {
        document.removeEventListener('keydown', hotKeyFuncHandle);
      }
    };
  }, [hotKey?.enable, onClickHandle]);

  const tooltipDesc = hotKey?.enable ? `${desc} : (${hotKey.hotKeyDesc})` : desc;
  return (
    isShow && (
      <Tooltip title={tooltipDesc} placement={'bottom'}>
        <Button
          disabled={disabled}
          loading={loading}
          htmlType={'submit'}
          type='text'
          icon={icon}
          onClick={onClickHandle}
          style={{ ...style, padding: '1px 6px' }}
        >
          {showDesc ? desc : ''}
        </Button>
      </Tooltip>
    )
  );
};
