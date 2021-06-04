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
  /*{
    path: '/task',
    name: 'task',
    icon: 'partition',
    component: './Task',
  },*/
  {
    path: '/cluster',
    name: 'cluster',
    icon: 'cluster',
    component: './Cluster',
  },
  {
    path: '/document',
    name: 'document',
    icon: 'container',
    component: './Document',
  },
  /*{
    path: '/dev',
    name: 'dev',
    icon: 'crown',
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
  },*/
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
  /*{
    path: '/demo',
    name: 'demo',
    icon: 'crown',
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
            component: './Demo/FormAdvancedForm',
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
  },*/
  {
    path: '/',
    redirect: '/welcome',
  },
  {
    component: './404',
  },
];
