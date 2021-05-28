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
    path: '/dbase',
    name: 'dbase',
    access: 'canAdmin',
    routes: [
      {
        path: '/dbase/user-center',
        name: 'user-center',
        icon: 'user',
        routes: [
          {
            path: '/dbase/user-center/user',
            name: 'user',
            icon: 'user',
            component: './Dbase/User',
          },
          {
            path: '/dbase/user-center/role',
            name: 'role',
            icon: 'smile',
            component: './Common/Build',
          },
          {
            path: '/dbase/user-center/menu',
            name: 'menu',
            icon: 'smile',
            component: './Common/Build',
          },
          {
            path: '/dbase/user-center/client',
            name: 'client',
            icon: 'smile',
            component: './Common/Build',
          },
          {
            path: '/dbase/user-center/token',
            name: 'token',
            icon: 'smile',
            component: './Common/Build',
          },
        ],
      },
      {
        path: '/dbase/springcloud',
        name: 'springcloud',
        icon: 'cloud',
        routes: [
          {
            path: '/dbase/springcloud/nacos',
            name: 'nacos',
            component: './Common/Build',
          },
        ],
      },
      {
        path: '/dbase/database',
        name: 'database',
        icon: 'database',
        routes: [
          {
            path: '/dbase/database/manager',
            name: 'manager',
            component: './Common/Build',
          },
        ],
      },
      {
        path: '/dbase/moitor',
        name: 'moitor',
        icon: 'desktop',
        routes: [
          {
            path: '/dbase/moitor/log',
            name: 'log',
            component: './Common/Build',
          },
        ],
      },
      {
        path: '/dbase/cluster',
        name: 'cluster',
        icon: 'cluster',
        routes: [
          {
            path: '/dbase/cluster/node',
            name: 'node',
            component: './Common/Build',
          },
        ],
      },
      {
        path: '/dbase/schedule',
        name: 'schedule',
        icon: 'fieldTime',
        routes: [
          {
            path: '/dbase/schedule/job',
            name: 'job',
            component: './Common/Build',
          },
        ],
      },
    ],
  },
  {
    path: '/dbus',
    name: 'dbus',
    component: './Common/Build',
  },
  {
    path: '/dlink',
    name: 'dlink',
    access: 'canAdmin',
    routes: [
      {
        path: '/dlink/flink',
        name: 'flink',
        icon: 'crown',
        routes: [
          {
            path: '/dlink/flink/studio',
            name: 'studio',
            component: './Dlink/Studio',
          },
        ],
      },
      {
        path: '/dlink/task',
        name: 'task',
        icon: 'crown',
        routes: [
          {
            path: '/dlink/task/list',
            name: 'list',
            component: './Dlink/Task',
          },
          {
            path: '/dlink/task/create',
            name: 'create',
            component: './Common/Build',
          },
          {
            path: '/dlink/task/flinksql',
            name: 'flinksql',
            component: './Dlink/FlinkSql',
          },
        ],
      },
      {
        path: '/dlink/warehouse',
        name: 'warehouse',
        icon: 'deploymentUnit',
        routes: [
          {
            path: '/dlink/warehouse/metadata',
            name: 'metadata',
            component: './Common/Build',
          },
        ],
      },
    ],
  },
  {
    path: '/dsink',
    name: 'dsink',
    component: './Common/Build',
  },
  {
    path: '/dview',
    name: 'dview',
    component: './Common/Build',
  },
  {
    path: '/dai',
    name: 'dai',
    component: './Common/Build',
  },
  {
    path: '/dev',
    name: 'dev',
    routes: [
      {
        path: '/dev/flink',
        name: 'flink',
        icon: 'github',
        routes: [
          {
            path: 'https://ci.apache.org/projects/flink/flink-docs-release-1.13/',
            name: 'docs',
          },
        ],
      },
      {
        path: '/dev/ant',
        name: 'ant-design',
        icon: 'antDesign',
        routes: [
          {
            path: 'https://ant.design/components/icon-cn/',
            name: 'docs',
          },
          {
            path:
              'https://preview.pro.ant.design/dashboard/analysis?primaryColor=%231890ff&fixSiderbar=true&colorWeak=false&pwa=false',
            name: 'preview',
          },
        ],
      },
    ],
  },
  /*{
  path: '/admin',
  name: 'admin',
  icon: 'crown',
  access: 'canAdmin',
  component: './Admin',
  routes: [
    {
      path: '/admin/sub-page',
      name: 'sub-page',
      icon: 'smile',
      component: './Welcome',
    },
   ],
},*/
  {
    path: '/demo',
    name: 'demo',
    //access: 'canAdmin',
    routes: [
      {
        name: 'list',
        icon: 'table',
        path: '/demo/list',
        routes: [
          {
            name: 'table-list',
            icon: 'table',
            path: '/demo/list/listtablelist',
            component: './Demo/ListTableList',
          },
          {
            name: 'basic-list',
            icon: 'smile',
            path: '/demo/list/listbasiclist',
            component: './Demo/ListBasicList',
          },
        ],
      },
      {
        name: 'form',
        icon: 'smile',
        path: '/demo/form',
        routes: [
          {
            name: 'advanced-form',
            icon: 'smile',
            path: '/demo/form/formadvancedform',
            component: './FormAdvancedForm',
          },
          {
            name: '分步表单',
            icon: 'smile',
            path: '/demo/form/formstepform',
            component: './Demo/FormStepForm',
          },
        ],
      },
    ],
  },
  {
    path: '/',
    redirect: '/welcome',
  },
  {
    component: './404',
  },
];
