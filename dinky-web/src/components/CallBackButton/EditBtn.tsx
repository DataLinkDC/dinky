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

import { l } from '@/utils/intl';
import { EditTwoTone } from '@ant-design/icons';
import { Button } from 'antd';
import React from 'react';

type EditButtonProps = {
  onClick: () => void;
  disabled?: boolean;
};

export const EditBtn: React.FC<EditButtonProps> = (props) => {
  const { onClick, disabled = false } = props;

  return (
    <Button
      className={'options-button'}
      icon={<EditTwoTone />}
      disabled={disabled}
      title={l('button.edit')}
      onClick={() => onClick()}
    />
  );
};
