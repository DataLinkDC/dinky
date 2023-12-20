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

import { theme } from 'antd';
import React from 'react';

export type ContentScrollProps = {
  height: number | string;
  children?: React.ReactNode;
};
const { useToken } = theme;

const ContentScroll: React.FC<ContentScrollProps> = (props) => {
  const { height, children } = props;
  const { token } = useToken();

  return (
    <div
      className='content-scroll'
      style={{
        height: height,
        display: height < 1 ? 'none' : 'block',
        backgroundColor: token.colorBgBase
      }}
    >
      {children}
    </div>
  );
};
export default ContentScroll;
