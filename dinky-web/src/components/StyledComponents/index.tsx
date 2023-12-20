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
import styled from 'styled-components';

const { getDesignToken } = theme;
const token = getDesignToken();

export const DataAction = styled.div`
  display: flex !important;
  justify-content: space-between;
  margin-left: 2vw;
  margin-right: 2vw;
`;

export const DataSourceDetailBackButton = styled.div`
  display: flex !important;
  justify-content: flex-end;
`;

export const Height80VHDiv = styled.div`
  height: 80vh;
  overflow: auto;
`;

export const StartButton = styled.div`
  position: absolute;
  color: #1890ff;
  font-size: large;
  z-index: 2;
  top: 50%;
`;

export const TagAlignCenter = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
`;
export const TagAlignLeft = styled.div`
  display: flex;
  height: 100%;
  justify-content: left;
  align-items: center;
`;

export const FlexCenterDiv = styled.div`
  display: flex;
  height: 100%;
  justify-content: left;
  align-items: center;
`;
