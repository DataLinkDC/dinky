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
    path: '/welcome',
    name: 'home',
    icon: 'home',
    component: './Welcome',
  },
  {
    path: '/flinksqlstudio',
    name: 'flinksqlstudio',
    icon: 'consoleSql',
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
      {
        path: '/taskcenter/jar',
        name: 'jar',
        icon: 'code-sandbox',
        component: './Jar',
      },
    ],
  },
  {
    path: '/clusters',
    name: 'clusters',
    icon: 'cluster',
    routes: [
      {
        path: '/clusters/cluster',
        name: 'cluster',
        icon: 'cluster',
        component: './Cluster',
      },
      {
        path: '/clusters/clusterConfiguration',
        name: 'clusterConfiguration',
        icon: 'setting',
        component: './ClusterConfiguration',
      },
    ],
  },
  {
    path: '/database',
    name: 'database',
    icon: 'database',
    component: './DataBase',
  },
  {
    path: '/document',
    name: 'document',
    icon: 'container',
    component: './Document',
  },
  {
    path: '/',
    redirect: '/welcome',
  },
  {
    name: 'settings',
    icon: 'setting',
    path: '/settings',
    component: './Settings',
  },
  {
    component: './404',
  },
];
