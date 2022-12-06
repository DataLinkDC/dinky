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
    routes: [
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
