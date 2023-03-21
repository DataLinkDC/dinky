/*
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
        component: './User/Login',
      },
    ],
  },

  {
    path: '/',
    redirect: '/datastudio',
  },

  {
    path: '/datastudio',
    name: 'datastudio',
    icon: 'CodeTwoTone',
    footerRender: false,
    component: './DataStudio',
  },
  {
    path: '/devops',
    name: 'devops',
    icon: 'ControlTwoTone',
    // component: './DevOps',
  },
  {
    path: '/job',
    name: 'job',
    // component: './DevOps/JobInfo',
    hideInMenu: true,
  },
  {
    path: '/datacenter',
    name: 'datacenter',
    icon: 'DatabaseTwoTone',
    routes: [
      {
        path: '/datacenter',
        redirect: '/datacenter/metadata',
      },
      {
        path: '/datacenter/metadata',
        name: 'metadata',
        icon: 'DatabaseTwoTone',
        // component: './DataCenter/MetaData',
      },
    ],
  },

  {
    path: '/registration',
    name: 'registration',
    icon: 'AppstoreTwoTone',
    routes: [
      {
        path: '/registration',
        redirect: '/registration/cluster/instance',
      },
      {
        path: '/registration/cluster',
        name: 'cluster',
        icon: 'GoldTwoTone',
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
        path: '/registration/jar',
        name: 'jar',
        icon: 'FileTwoTone',
        // component: './RegistrationCenter/Jar',
      },
      {
        path: '/registration/database',
        name: 'database',
        icon: 'DatabaseTwoTone',
        // component: './RegistrationCenter/DataBase',
      },
      {
        path: '/registration/alert',
        name: 'alert',
        icon: 'AlertTwoTone',
        routes: [
          {
            path: '/registration/alert/instance',
            name: 'instance',
            // component: './RegistrationCenter/AlertManage/AlertInstance',
          },
          {
            path: '/registration/alert/group',
            name: 'group',
            // component: './RegistrationCenter/AlertManage/AlertGroup',
          },
        ],
      },
      {
        path: '/registration/document',
        name: 'document',
        icon: 'BookTwoTone',
        // component: './RegistrationCenter/Document',
      },
      {
        path: '/registration/fragment',
        name: 'fragment',
        icon: 'RocketTwoTone',
        // component: './RegistrationCenter/FragmentVariable',
      },
    ],
  },
  {
    name: 'authentication',
    icon: 'SafetyCertificateTwoTone',
    path: '/authentication',
    // access: "canAdmin",
    routes: [
      {
        path: '/authentication',
        redirect: '/authentication/usermanager',
      },
      {
        path: '/authentication/usermanager',
        name: 'usermanager',
        icon: 'UserOutlined',
        component: './AuthenticationCenter/UserManager',
      },
      {
        path: '/authentication/role',
        name: 'role',
        icon: 'TeamOutlined',
        // component: './AuthenticationCenter/RoleManager',
      },
      {
        path: '/authentication/namespace',
        name: 'namespace',
        icon: 'BulbTwoTone',
        // component: './AuthenticationCenter/NamespaceManager',
      },
      {
        path: '/authentication/tenant',
        name: 'tenant',
        icon: 'SecurityScanTwoTone',
        // component: './AuthenticationCenter/TenantManager',
      },
    ],
  },

  {
    name: 'settings',
    icon: 'SettingTwoTone',
    path: '/settings',
    routes: [
      {
        path: '/settings',
        redirect: '/settings/flinksetting',
      },
      {
        path: '/settings/flinksetting',
        name: 'flinksetting',
        icon: 'SettingTwoTone',
        // component: './SettingCenter/FlinkSettings',
      },
      {
        path: '/settings/udf',
        name: 'udf',
        icon: 'ToolTwoTone',
        // component: './SettingCenter/UDFTemplate',
      },
      {
        path: '/settings/system',
        name: 'system',
        icon: 'InfoCircleTwoTone',
        // component: './SettingCenter/SystemInfo',
      },
      {
        path: '/settings/process',
        name: 'process',
        icon: 'ReconciliationTwoTone',
        // component: './SettingCenter/ProcessList',
      },
      {
        path: '/settings/services',
        name: 'services',
        icon: 'CloudTwoTone',
        // component: './SettingCenter/Service',
      },
    ],
  },
  {
    path: '/metrics',
    name: 'metrics',
    icon: 'DashboardTwoTone',
    // component: './Metrics',
  },
  {
    path: '/about',
    name: 'about',
    icon: 'SmileTwoTone',
    component: './About',
  },
  {
    path: '*',
    layout: false,
    component: './404',
  },
];
