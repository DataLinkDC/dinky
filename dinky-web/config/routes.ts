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
        component: './Other/Login'
      }
    ]
  },
  {
    path: '/',
    redirect: '/redirect'
  },
  {
    path: '/redirect',
    component: './Other/Redirect',
    layout: false,
    hideInMenu: true
  },
  // {
  //   path: '/home',
  //   name: 'home',
  //   icon: 'HomeOutlined',
  //   footerRender: false,
  //   component: './Home'
  // },
  {
    path: '/datastudio',
    name: 'datastudio',
    icon: 'CodeOutlined',
    footerRender: false,
    component: './DataStudio'
  },
  {
    path: '/devops',
    name: 'devops',
    icon: 'ControlOutlined',
    footerRender: false,
    routes: [
      {
        path: '/devops',
        redirect: '/devops/joblist'
      },
      {
        path: '/devops/joblist',
        name: 'job',
        hideInMenu: true,
        component: './DevOps'
      },
      {
        path: '/devops/job-detail',
        name: 'job-detail',
        hideInMenu: true,
        component: './DevOps/JobDetail'
      }
    ]
  },
  {
    path: '/registration',
    name: 'registration',
    icon: 'AppstoreOutlined',
    footerRender: false,
    routes: [
      {
        path: '/registration',
        redirect: '/registration/cluster/instance'
      },
      {
        path: '/registration/cluster',
        name: 'cluster',
        icon: 'GoldOutlined',
        routes: [
          {
            path: '/registration/cluster/instance',
            name: 'cluster-instance',
            component: './RegCenter/Cluster/Instance'
          },
          {
            path: '/registration/cluster/config',
            name: 'cluster-config',
            component: './RegCenter/Cluster/Configuration'
          }
        ]
      },
      {
        path: '/registration/datasource',
        name: 'datasource',
        icon: 'DatabaseOutlined',
        routes: [
          {
            path: '/registration/datasource',
            redirect: '/registration/datasource/list'
          },
          {
            path: '/registration/datasource/list',
            name: 'list',
            hideInMenu: true,
            component: './RegCenter/DataSource'
          },
          {
            path: '/registration/datasource/detail',
            name: 'detail',
            hideInMenu: true,
            component: './RegCenter/DataSource/components/DataSourceDetail'
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
            component: './RegCenter/Alert/AlertInstance'
          },
          {
            path: '/registration/alert/group',
            name: 'group',
            component: './RegCenter/Alert/AlertGroup'
          },
          {
            path: '/registration/alert/template',
            name: 'template',
            component: './RegCenter/Alert/AlertTemplate'
          }
        ]
      },
      {
        path: '/registration/document',
        name: 'document',
        icon: 'BookOutlined',
        component: './RegCenter/Document'
      },
      {
        path: '/registration/fragment',
        name: 'fragment',
        icon: 'RocketOutlined',
        component: './RegCenter/GlobalVar'
      },
      {
        path: '/registration/gitproject',
        name: 'gitproject',
        icon: 'GithubOutlined',
        component: './RegCenter/GitProject'
      },
      {
        path: '/registration/udf',
        name: 'udf',
        icon: 'ToolOutlined',
        component: './RegCenter/UDF'
      },
      {
        path: '/registration/resource',
        name: 'resource',
        icon: 'FileZipOutlined',
        component: './RegCenter/Resource'
      }
    ]
  },
  {
    name: 'auth',
    icon: 'SafetyCertificateOutlined',
    path: '/auth',
    access: 'canAdmin',
    footerRender: false,
    routes: [
      {
        path: '/auth',
        redirect: '/auth/user'
      },
      {
        path: '/auth/user',
        name: 'user',
        icon: 'UserOutlined',
        component: './AuthCenter/User'
      },
      {
        path: '/auth/role',
        name: 'role',
        icon: 'TeamOutlined',
        component: './AuthCenter/Role'
      },
      {
        path: '/auth/menu',
        name: 'menu',
        icon: 'MenuOutlined',
        component: './AuthCenter/Menu'
      },
      {
        path: '/auth/rowpermissions',
        name: 'rowpermissions',
        icon: 'SafetyCertificateOutlined',
        component: './AuthCenter/RowPermissions'
      },
      {
        path: '/auth/tenant',
        name: 'tenant',
        icon: 'SecurityScanOutlined',
        component: './AuthCenter/Tenant'
      },
      {
        path: '/auth/token',
        name: 'token',
        icon: 'SecurityScanOutlined',
        component: './AuthCenter/Token'
      }
    ]
  },

  {
    name: 'settings',
    icon: 'SettingOutlined',
    path: '/settings',
    footerRender: false,
    routes: [
      {
        path: '/settings',
        redirect: '/settings/globalsetting'
      },
      {
        path: '/settings/globalsetting',
        name: 'globalsetting',
        icon: 'SettingOutlined',
        component: './SettingCenter/GlobalSetting'
      },
      {
        path: '/settings/systemlog',
        name: 'systemlog',
        icon: 'InfoCircleOutlined',
        component: './SettingCenter/SystemLogs'
      },
      // {
      //   path: '/settings/process',
      //   name: 'process',
      //   icon: 'ReconciliationOutlined',
      //   component: './SettingCenter/Process'
      // },
      {
        path: '/settings/alertrule',
        name: 'alertrule',
        icon: 'ReconciliationOutlined',
        component: './SettingCenter/AlertRule'
      },
      {
        path: '/settings/classloaderjars',
        name: 'classloaderjars',
        icon: 'CodepenOutlined',
        component: './SettingCenter/ClassLoaderJars'
      }
    ]
  },
  {
    path: '/metrics',
    name: 'metrics',
    icon: 'DashboardOutlined',
    footerRender: false,
    component: './Metrics'
  },
  // {
  //   path: '/about',
  //   name: 'about',
  //   icon: 'SmileOutlined',
  //   footerRender: false,
  //   component: './Other/About'
  // },
  {
    path: '/account/center',
    footerRender: false,
    name: 'center',
    hideInMenu: true,
    component: './Other/PersonCenter'
  },
  {
    path: '*',
    component: './Other/404'
  }
];
