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


export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        path: '/user',
        routes: [
          {
            name: 'login',
            path: '/user/login',
            component: './user/Login',
          },
        ],
      },
    ],
  },
  {
    path: '/datastudio',
    name: 'datastudio',
    icon: 'consoleSql',
    footerRender: false,
    component: './DataStudio',
  },
  {
    path: '/devops',
    name: 'devops',
    icon: 'control',
    component: './DevOps',
  },
  {
    path: '/job',
    name: 'job',
    component: './DevOps/JobInfo',
    hideInMenu: true,
  },
  {
    path: '/registration',
    name: 'registration',
    icon: 'appstore',
    routes: [
      {
        path: '/registration/cluster',
        name: 'cluster',
        icon: 'cluster',
        routes: [
          {
            path: '/registration/cluster/clusterInstance',
            name: 'clusterInstance',
            component: './Cluster',
          },
          {
            path: '/registration/cluster/clusterConfiguration',
            name: 'clusterConfiguration',
            component: './ClusterConfiguration',
          },
        ],
      },
      {
        path: '/registration/jar',
        name: 'jar',
        icon: 'code-sandbox',
        component: './Jar',
      },
      {
        path: '/registration/database',
        name: 'database',
        icon: 'database',
        component: './DataBase',
      },
      {
        path: '/registration/alert',
        name: 'alert',
        icon: 'alert',
        routes: [
          {
            path: '/registration/alert/alertInstance',
            name: 'alertInstance',
            component: './AlertInstance',
          },
          {
            path: '/registration/alert/alertGroup',
            name: 'alertGroup',
            component: './AlertGroup',
          },
        ],
      },{
        path: '/registration/document',
        name: 'document',
        icon: 'container',
        component: './Document',
      },{
        path: '/registration/fragment',
        name: 'fragment',
        icon: "cloud",
        component: './FragmentVariable',
      },
    ],
  },
  {
    path: '/',
    redirect: '/datastudio',
  },
  {
    name: 'settings',
    icon: 'setting',
    path: '/settings',
    component: './Settings',
  },
  {
    path: '/about',
    name: 'about',
    icon: 'smile',
    component: './Welcome',
  },
  {
    component: './404',
  },
];
