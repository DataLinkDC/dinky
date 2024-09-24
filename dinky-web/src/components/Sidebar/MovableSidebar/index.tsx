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

import { CircleBtn } from '@/components/CallBackButton/CircleBtn';
import useThemeValue from '@/hooks/useThemeValue';
import { MinusOutlined } from '@ant-design/icons';
import { Space, TabPaneProps, theme } from 'antd';
import { Enable, Resizable, ResizeCallback, Size } from 're-resizable';
import React, { useState } from 'react';

export type MovableSidebarProps = {
  title?: React.ReactNode;
  enable?: Enable;
  minWidth?: string | number;
  minHeight?: string | number;
  maxWidth?: string | number;
  maxHeight?: string | number;
  contentHeight?: number;
  defaultSize?: Size;
  visible?: boolean;
  headerVisible?: boolean;
  children?: React.ReactNode;
  handlerMinimize?: () => void;
  handlerMaxsize?: () => void;
  btnGroup?: React.ReactNode[];
  onResize?: ResizeCallback;
  onResizeStop?: ResizeCallback;
  style?: React.CSSProperties;
  tagList?: TabPaneProps[];
};
const { useToken } = theme;
const MovableSidebar: React.FC<MovableSidebarProps> = (props) => {
  const { token } = useToken();
  const themeValue = useThemeValue();

  const {
    style,
    visible,
    onResize,
    onResizeStop,
    defaultSize,
    minWidth,
    maxWidth,
    minHeight,
    maxHeight,
    enable,
    headerVisible = true,
    children,
    title,
    contentHeight,
    handlerMinimize,
    handlerMaxsize,
    tagList
  } = props;
  const [showBtn, setShowBtn] = useState(false);

  return (
    <div onMouseEnter={() => setShowBtn(true)} onMouseLeave={() => setShowBtn(false)}>
      <Resizable
        className={'container'}
        style={{
          ...style,
          display: visible ? 'block' : 'none',
          borderRadius: 5,
          backgroundColor: token.colorBgBase
        }}
        onResize={onResize}
        onResizeStop={onResizeStop}
        defaultSize={defaultSize}
        minWidth={minWidth}
        maxWidth={maxWidth}
        minHeight={minHeight}
        maxHeight={maxHeight}
        enable={enable}
      >
        <>
          {headerVisible && (
            <div
              style={{
                backgroundColor: token.colorBgBase,
                borderBlockColor: themeValue.borderColor
              }}
              className={'container-header'}
            >
              <div>{title}</div>
              <div className={showBtn ? 'show' : 'hide'}>
                <Space size={1}>
                  {props.btnGroup}
                  <CircleBtn onClick={props.handlerMinimize} icon={<MinusOutlined />} />
                </Space>
              </div>
            </div>
          )}
          <div
            style={{
              height: contentHeight,
              backgroundColor: token.colorBgBase,
              overflow: 'auto'
            }}
          >
            {children}
          </div>
        </>
      </Resizable>
    </div>
  );
};
export default MovableSidebar;
