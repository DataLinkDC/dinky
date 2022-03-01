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
    path: '/flinksqlstudio',
    name: 'flinksqlstudio',
    icon: 'consoleSql',
    footerRender: false,
    component: './FlinkSqlStudio',
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
      },
    ],
  },
  {
    path: '/',
    redirect: '/flinksqlstudio',
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
