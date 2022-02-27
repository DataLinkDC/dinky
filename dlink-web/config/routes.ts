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
    path: '/taskcenter',
    name: 'taskcenter',
    icon: 'partition',
    routes: [
      /*{
    path: '/taskcenter/task',
    name: 'task',
    icon: 'task',
    component: './Task',
  },*/

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
        component: './Cluster',
      },
      {
        path: '/registration/clusterConfiguration',
        name: 'clusterConfiguration',
        icon: 'setting',
        component: './ClusterConfiguration',
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
            icon: 'task',
            component: './AlertInstance',
          },
          {
            path: '/registration/alert/alertGroup',
            name: 'alertGroup',
            icon: 'task',
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
