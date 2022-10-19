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
      }, {
        path: '/registration/document',
        name: 'document',
        icon: 'container',
        component: './Document',
      },{
        path: '/registration/fragment',
        name: 'fragment',
        icon: "cloud",
        component: './FragmentVariable',
      }
    ],
  },
  {
    name: 'authenticationcenter',
    icon: 'SafetyCertificateOutlined',
    path: '/authenticationcenter',
    routes: [
      {
        path: '/authenticationcenter/usermanager',
        name: 'usermanager',
        icon: 'UserOutlined',
        component: './AuthenticationCenter/UserManager',
      },
      {
        path: '/authenticationcenter/rolemanager',
        name: 'rolemanager',
        icon: 'TeamOutlined',
        component: './AuthenticationCenter/RoleManager',
      },
      {
        path: '/authenticationcenter/namespacemanager',
        name: 'namespacemanager',
        icon: 'BulbOutlined',
        component: './AuthenticationCenter/NamespaceManager',
      },
      {
        path: '/authenticationcenter/tenantmanager',
        name: 'tenantmanager',
        icon: 'SecurityScanOutlined',
        component: './AuthenticationCenter/TenantManager',
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
    path: '/settingcenter',
    routes: [
      {
        path: '/settingcenter/flinksettings',
        name: 'flinkConfig',
        icon: 'setting',
        component: './SettingCenter/FlinkSettings',
      },
      {
        path: '/settingcenter/udfTemplate',
        name: 'udfTemplate',
        icon: 'setting',
        component: './SettingCenter/UDFTemplate',
      },
      {
        path: '/settingcenter/systeminfo',
        name: 'systemInfo',
        icon: 'desktop',
        component: './SettingCenter/SystemInfo',
      },
      {
        path: '/settingcenter/processList',
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
