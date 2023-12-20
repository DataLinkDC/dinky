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

import { TabsItemType, TaskDataType } from '@/pages/DataStudio/model';
import { Tab } from '@/pages/DataStudio/route';
import { Button } from 'antd';
import React from 'react';

export type CircleButtonProps = {
  icon: React.ReactNode;
  loading?: boolean;
  onClick?: () => void;
  title?: string;
  key?: string;
};
export type CircleBottomButtonProps = {
  icon: React.ReactNode;
  loading?: boolean;
  onClick?: (
    tabs: Tab[],
    key: string,
    data: TaskDataType | undefined,
    refresh: any
  ) => Promise<void>;
  title?: string;
  key?: string;
};
export type CircleDataStudioButtonProps = {
  icon: React.ReactNode;
  loading?: boolean;
  onClick?: (panes: TabsItemType[], activeKey: string) => void;
  title?: string;
  key?: string;
  isShow?: boolean;
};

export const CircleBtn: React.FC<CircleButtonProps> = (props) => {
  const { onClick, title, icon, loading } = props;

  return (
    <Button
      title={title}
      loading={loading}
      icon={icon}
      block
      type={'text'}
      shape={'circle'}
      onClick={onClick}
    />
  );
};
