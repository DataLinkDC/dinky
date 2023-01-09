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
 * @param access 权限相关 需要在 src/access.ts内定义权限 使用名称 引用即可
 *              组件内的权限则使用 Hooks  + <Access /> 组件实现 详情见: https://umijs.org/docs/max/access
 * @doc https://umijs.org/docs/guides/routes
 * demo : https://github.com/ant-design/ant-design-pro/blob/master/config/routes.ts
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
    path: '/dataStudio',
    name: 'dataStudio',
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
    path: '/datacenter',
    name: 'datacenter',
    icon: 'database',
    routes: [
      {
        path: '/datacenter',
        redirect: '/datacenter/metadata',
      },
      {
        component: './DataCenter/MetaData',
        path: '/datacenter/metadata',
        name: 'metadata',
        icon: 'cluster',
      },
    ],
  },

  {
    path: '/registration',
    name: 'registration',
    icon: 'appstore',
    routes: [
      {
        path: '/registration',
        redirect: '/registration/cluster/clusterInstance',
      },
      {
        path: '/registration/cluster',
        name: 'cluster',
        icon: 'cluster',
        routes: [
          {
            path: '/registration/cluster/clusterInstance',
            name: 'clusterInstance',
            component: './RegistrationCenter/ClusterManage/Cluster',
          },
          {
            path: '/registration/cluster/clusterConfiguration',
            name: 'clusterConfiguration',
            component: './RegistrationCenter/ClusterManage/ClusterConfiguration',
          },
        ],
      },
      {
        path: '/registration/jar',
        name: 'jar',
        icon: 'code-sandbox',
        component: './RegistrationCenter/Jar',
      },
      {
        path: '/registration/database',
        name: 'database',
        icon: 'database',
        component: './RegistrationCenter/DataBase',
      },
      {
        path: '/registration/alert',
        name: 'alert',
        icon: 'alert',
        routes: [
          {
            path: '/registration/alert/alertInstance',
            name: 'alertInstance',
            component: './RegistrationCenter/AlertManage/AlertInstance',
          },
          {
            path: '/registration/alert/alertGroup',
            name: 'alertGroup',
            component: './RegistrationCenter/AlertManage/AlertGroup',
          },
        ],
      }, {
        path: '/registration/document',
        name: 'document',
        icon: 'container',
        component: './RegistrationCenter/Document',
      },{
        path: '/registration/fragment',
        name: 'fragment',
        icon: "cloud",
        component: './RegistrationCenter/FragmentVariable',
      }
    ],
  },
  {
    name: 'authenticationCenter',
    icon: 'SafetyCertificateOutlined',
    path: '/authenticationCenter',
    access: "canAdmin",
    routes: [
      {
        path: '/authenticationCenter',
        redirect: '/authenticationCenter/userManager',
      },
      {
        path: '/authenticationCenter/userManager',
        name: 'userManager',
        icon: 'UserOutlined',
        component: './AuthenticationCenter/UserManager',
      },
      {
        path: '/authenticationCenter/roleManager',
        name: 'roleManager',
        icon: 'TeamOutlined',
        component: './AuthenticationCenter/RoleManager',
      },
      {
        path: '/authenticationCenter/namespaceManager',
        name: 'namespaceManager',
        icon: 'BulbOutlined',
        component: './AuthenticationCenter/NamespaceManager',
      },
      {
        path: '/authenticationCenter/tenantManager',
        name: 'tenantManager',
        icon: 'SecurityScanOutlined',
        component: './AuthenticationCenter/TenantManager',
      },
    ],
  },
  {
    path: '/',
    redirect: '/dataStudio',
  },
  {
    name: 'settings',
    icon: 'setting',
    path: '/settingCenter',
    routes: [
      {
        path: '/settingCenter',
        redirect: '/settingCenter/flinkSettings',
      },
      {
        path: '/settingCenter/flinkSettings',
        name: 'flinkConfig',
        icon: 'setting',
        component: './SettingCenter/FlinkSettings',
      },
      {
        path: '/settingCenter/udfTemplate',
        name: 'udfTemplate',
        icon: 'setting',
        component: './SettingCenter/UDFTemplate',
      },
      {
        path: '/settingCenter/systemInfo',
        name: 'systemInfo',
        icon: 'desktop',
        component: './SettingCenter/SystemInfo',
      },
      {
        path: '/settingCenter/processList',
        name: 'processList',
        icon: 'desktop',
        component: './SettingCenter/ProcessList',
      },
    ],
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
