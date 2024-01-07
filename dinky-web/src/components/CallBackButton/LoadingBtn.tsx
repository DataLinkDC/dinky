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

import { ButtonRoute } from '@/types/Studio/data';
import { Button } from 'antd';
import React, { useState } from 'react';

export const LoadingBtn: React.FC<ButtonRoute> = (route) => {
  const { click, title, icon, hotKeyDesc, props } = route;
  const [loading, setLoading] = useState(false);

  const handleClick = async (event: React.MouseEvent<HTMLElement, MouseEvent>) => {
    if (click) {
      setLoading(true);
      const result = await click();
      if (result instanceof Promise) {
        await result;
      }
      setLoading(false);
    }
  };

  return (
    <Button
      key={title}
      size={'small'}
      type={'text'}
      icon={icon}
      title={hotKeyDesc}
      loading={loading}
      onClick={(event) => handleClick(event)}
      {...props}
    >
      {title}
    </Button>
  );
};
