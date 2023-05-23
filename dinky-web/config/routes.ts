﻿/*
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

/**
 * @name umi 的路由配置
 * @description 只支持 path,component,routes,redirect,wrappers,name,icon 的配置
 * @param path  path 只支持两种占位符配置，第一种是动态参数 :id 的形式，第二种是 * 通配符，通配符只能出现路由字符串的最后。
 * @param component 配置 location 和 path 匹配后用于渲染的 React 组件路径。可以是绝对路径，也可以是相对路径，如果是相对路径，会从 src/pages 开始找起。
 * @param routes 配置子路由，通常在需要为多个路径增加 layout 组件时使用。
 * @param redirect 配置路由跳转
 * @param wrappers 配置路由组件的包装组件，通过包装组件可以为当前的路由组件组合进更多的功能。 比如，可以用于路由级别的权限校验
 * @param name 配置路由的标题，默认读取国际化文件 menu.ts 中 menu.xxxx 的值，如配置 name 为 login，则读取 menu.ts 中 menu.login 的取值作为标题
 * @param icon 配置路由的图标，取值参考 https://ant.design/components/icon-cn， 注意去除风格后缀和大小写，如想要配置图标为 <StepBackwardOutlined /> 则取值应为 stepBackward 或 StepBackward，如想要配置图标为 <UserOutlined /> 则取值应为 user 或者 User
 * @doc https://umijs.org/docs/guides/routes
 * todo: 如何引入自定义 icon
 */
export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        name: 'login',
        path: '/user/login',
        component: './Other/Login',
      },
    ],
  },
  {
    path: '/',
    redirect: '/home',
  },
  {
    path: '/home',
    name: 'home',
    icon: 'HomeOutlined',
    footerRender: false,
    component: './Home',
  },
  {
    path: '/datastudio',
    name: 'datastudio',
    icon: 'CodeOutlined',
    footerRender: false,
    component: './DataStudio',
  },
  {
    path: '/devops',
    name: 'devops',
    icon: 'ControlOutlined',
    routes: [
      {
        path: '/devops',
        redirect: '/devops/job',
      },
      {
        path: '/devops/job',
        name: 'job',
        hideInMenu: true,
        // component: './DevOps/JobInfo',
      },
    ],
  },
  //todo: data center will be merge to the registration center's datasource module , it will be removed in the future

  // {
  //   path: '/datacenter',
  //   name: 'datacenter',
  //   icon: 'DatabaseOutlined',
  //   routes: [
  //     {
  //       path: '/datacenter',
  //       redirect: '/datacenter/metadata',
  //     },
  //     {
  //       path: '/datacenter/metadata',
  //       name: 'metadata',
  //       icon: 'DatabaseOutlined',
  //       // component: './DataCenter/MetaData',
  //     },
  //   ],
  // },
  {
    path: '/registration',
    name: 'registration',
    icon: 'AppstoreOutlined',
    routes: [
      {
        path: '/registration',
        redirect: '/registration/cluster/instance',
      },
      {
        path: '/registration/cluster',
        name: 'cluster',
        icon: 'GoldOutlined',
        routes: [
          {
            path: '/registration/cluster/instance',
            name: 'cluster-instance',
            // component: './RegistrationCenter/ClusterManage/Cluster',
          },
          {
            path: '/registration/cluster/config',
            name: 'cluster-config',
            // component: './RegistrationCenter/ClusterManage/ClusterConfiguration',
          },
        ],
      },
      {
        path: '/registration/database',
        name: 'database',
        icon: 'DatabaseOutlined',
        component: './RegCenter/DataSource',
        routes: [
          {
            path: '/registration/database/detail/:id',
          }
        ]
      },
      {
        path: '/registration/alert',
        name: 'alert',
        icon: 'AlertOutlined',
        routes: [
          {
            path: '/registration/alert/instance',
            name: 'instance',
            component: './RegCenter/Alert/AlertInstance',
          },
          {
            path: '/registration/alert/group',
            name: 'group',
            component: './RegCenter/Alert/AlertGroup',
          },
        ],
      },
      {
        path: '/registration/document',
        name: 'document',
        icon: 'BookOutlined',
        component: './RegCenter/Document',
      },
      {
        path: '/registration/fragment',
        name: 'fragment',
        icon: 'RocketOutlined',
        component: './RegCenter/GlobalVar',
      },
      {
        path: '/registration/gitprojects',
        name: 'gitprojects',
        icon: 'GithubOutlined',
        component: './RegCenter/GitProject',
      },
      {
        path: '/registration/udf',
        name: 'udf',
        icon: 'ToolOutlined',
        component: './RegCenter/UDF',
      },
    ],
  },
  {
    name: 'auth',
    icon: 'SafetyCertificateOutlined',
    path: '/auth',
    access: 'canAdmin',
    routes: [
      {
        path: '/auth',
        redirect: '/auth/user',
      },
      {
        path: '/auth/user',
        name: 'user',
        icon: 'UserOutlined',
        component: './AuthCenter/User',
      },
      {
        path: '/auth/role',
        name: 'role',
        icon: 'TeamOutlined',
        component: './AuthCenter/Role',
      },
      {
        path: '/auth/rowpermissions',
        name: 'rowpermissions',
        icon: 'SafetyCertificateOutlined',
        component: './AuthCenter/RowPermissions',
      },
      {
        path: '/auth/tenant',
        name: 'tenant',
        icon: 'SecurityScanOutlined',
        component: './AuthCenter/Tenant',
      },
    ],
  },

  {
    name: 'settings',
    icon: 'SettingOutlined',
    path: '/settings',
    routes: [
      {
        path: '/settings',
        redirect: '/settings/flinksetting',
      },
      {
        path: '/settings/flinksetting',
        name: 'flinksetting',
        icon: 'SettingOutlined',
        // component: './SettingCenter/FlinkSettings',
      },
      {
        path: '/settings/system',
        name: 'system',
        icon: 'InfoCircleOutlined',
        component: './SettingCenter/SystemInfo',
      },
      {
        path: '/settings/process',
        name: 'process',
        icon: 'ReconciliationOutlined',
        component: './SettingCenter/Process',
      },
      {
        path: '/settings/services',
        name: 'services',
        icon: 'CloudServerOutlined',
        // component: './SettingCenter/Service',
      },
    ],
  },
  {
    path: '/metrics',
    name: 'metrics',
    icon: 'DashboardOutlined',
    // component: './Metrics',
  },
  {
    path: '/about',
    name: 'about',
    icon: 'SmileOutlined',
    component: './Other/About',
  },
  {
    path: '*',
    layout: false,
    component: './Other/404',
  },
];
