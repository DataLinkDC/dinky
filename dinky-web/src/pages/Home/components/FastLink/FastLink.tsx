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

import { Button, Card, Space, Typography } from 'antd';
import {
  AlertInstanceIcon,
  ClusterConfigIcon,
  ClusterInstanceIcon,
  DatabaseIcon
} from '@/components/Icons/HomeIcon';
import React from 'react';
import { history } from 'umi';
import { l } from '@/utils/intl';

const FastLink = () => {
  const imgStyle = {
    display: 'block',
    width: 20,
    height: 20
  };

  const links = [
    {
      title: l('menu.datastudio'),
      href: '/datastudio',
      icon: <ClusterInstanceIcon style={imgStyle} />
    },
    {
      title: l('menu.registration.alert.instance'),
      href: '/registration/alert/instance',
      icon: <AlertInstanceIcon style={imgStyle} />
    },
    {
      title: l('menu.registration.datasource'),
      href: '/registration/datasource/list',
      icon: <DatabaseIcon style={imgStyle} />
    },
    {
      title: l('menu.registration.cluster.cluster-config'),
      href: '/registration/cluster/instance',
      icon: <ClusterInstanceIcon style={imgStyle} />
    },
    {
      title: l('menu.registration.resource'),
      href: '/registration/resource',
      icon: <ClusterConfigIcon style={imgStyle} />
    }
  ];

  return (
    <Space size={[12, 8]} wrap>
      {links.map((link) => {
        return (
          <Button
            key={link.title}
            type='text'
            ghost
            size={'large'}
            icon={link.icon}
            onClick={() => history.push(link.href)}
          >
            {link.title}
          </Button>
        );
      })}
    </Space>
  );
};
export default FastLink;
