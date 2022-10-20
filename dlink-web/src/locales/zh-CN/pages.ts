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

export default {
  'pages.layouts.userLayout.title': 'Dinky 实时计算平台',
  'pages.login.accountLogin.tab': '账户密码登录',
  'pages.login.accountLogin.errorMessage': '错误的用户名和密码（admin/admin)',
  'pages.login.failure': '登录失败，请重试！',
  'pages.login.success': '登录成功！',
  'pages.login.chooseTenant': '请选择租户',
  'pages.login.username.placeholder': '用户名: admin',
  'pages.login.username.required': '用户名是必填项！',
  'pages.login.password.placeholder': '密码: admin',
  'pages.login.password.required': '密码是必填项！',
  'pages.login.phoneLogin.tab': '手机号登录',
  'pages.login.phoneLogin.errorMessage': '验证码错误',
  'pages.login.phoneNumber.placeholder': '请输入手机号！',
  'pages.login.phoneNumber.required': '手机号是必填项！',
  'pages.login.phoneNumber.invalid': '不合法的手机号！',
  'pages.login.captcha.placeholder': '请输入验证码！',
  'pages.login.captcha.required': '验证码是必填项！',
  'pages.login.phoneLogin.getVerificationCode': '获取验证码',
  'pages.getCaptchaSecondText': '秒后重新获取',
  'pages.login.rememberMe': '自动登录',
  'pages.login.forgotPassword': '忘记密码 ?',
  'pages.login.submit': '登录',
  'pages.login.loginWith': '其他登录方式 :',
  'pages.login.registerAccount': '注册账户',
  'pages.welcome.Community': '官方社区',
  'pages.welcome.upgrade': '更新日志',
  'pages.welcome.QQ': 'QQ官方社区群',
  'pages.welcome.QQcode': '543709668',
  'pages.welcome.link': '欢迎加入',
  'pages.welcome.star': '欢迎 Star ',
  'pages.welcome.advancedLayout': 'Github',
  'pages.welcome.alertMessage': '实时计算平台 Dinky 即将发布，目前为体验版，版本号为 {version}。',
  'pages.admin.subPage.title': ' 这个页面只有 admin 权限才能查看',
  'pages.admin.subPage.alertMessage': 'umi ui 现已发布，欢迎使用 npm run ui 启动体验。',
  'pages.searchTable.createForm.newRule': '新建规则',
  'pages.searchTable.updateForm.ruleConfig': '规则配置',
  'pages.searchTable.updateForm.basicConfig': '基本信息',
  'pages.searchTable.updateForm.ruleName.nameLabel': '规则名称',
  'pages.searchTable.updateForm.ruleName.nameRules': '请输入规则名称！',
  'pages.searchTable.updateForm.ruleDesc.descLabel': '规则描述',
  'pages.searchTable.updateForm.ruleDesc.descPlaceholder': '请输入至少五个字符',
  'pages.searchTable.updateForm.ruleDesc.descRules': '请输入至少五个字符的规则描述！',
  'pages.searchTable.updateForm.ruleProps.title': '配置规则属性',
  'pages.searchTable.updateForm.object': '监控对象',
  'pages.searchTable.updateForm.ruleProps.templateLabel': '规则模板',
  'pages.searchTable.updateForm.ruleProps.typeLabel': '规则类型',
  'pages.searchTable.updateForm.schedulingPeriod.title': '设定调度周期',
  'pages.searchTable.updateForm.schedulingPeriod.timeLabel': '开始时间',
  'pages.searchTable.updateForm.schedulingPeriod.timeRules': '请选择开始时间！',
  'pages.searchTable.titleDesc': '描述',
  'pages.searchTable.ruleName': '规则名称为必填项',
  'pages.searchTable.titleCallNo': '服务调用次数',
  'pages.searchTable.titleStatus': '状态',
  'pages.searchTable.nameStatus.default': '关闭',
  'pages.searchTable.nameStatus.running': '运行中',
  'pages.searchTable.nameStatus.online': '已上线',
  'pages.searchTable.nameStatus.abnormal': '异常',
  'pages.searchTable.titleUpdatedAt': '上次调度时间',
  'pages.searchTable.exception': '请输入异常原因！',
  'pages.searchTable.titleOption': '操作',
  'pages.searchTable.config': '配置',
  'pages.searchTable.subscribeAlert': '订阅警报',
  'pages.searchTable.title': '查询表格',
  'pages.searchTable.new': '新建',
  'pages.searchTable.chosen': '已选择',
  'pages.searchTable.item': '项',
  'pages.searchTable.totalServiceCalls': '服务调用次数总计',
  'pages.searchTable.tenThousand': '万',
  'pages.searchTable.batchDeletion': '批量删除',
  'pages.searchTable.batchApproval': '批量审批',

  // ------------------------------------------------------------------------------

  'pages.operate': '操作', // all table list of operation columns is use this item

  'pages.devops.jobstatus.CREATED': '已创建',
  'pages.devops.jobstatus.INITIALIZING': '初始化',
  'pages.devops.jobstatus.RUNNING': '运行中',
  'pages.devops.jobstatus.FINISHED': '已完成',
  'pages.devops.jobstatus.FAILING': '异常中',
  'pages.devops.jobstatus.FAILED': '已异常',
  'pages.devops.jobstatus.SUSPENDED': '已暂停',
  'pages.devops.jobstatus.CANCELLING': '停止中',
  'pages.devops.jobstatus.CANCELED': '停止',
  'pages.devops.jobstatus.RESTARTING': '重启中',
  'pages.devops.jobstatus.UNKNOWN': '未知',

  'pages.devops.LastUpdateTime': '最后更新时间',

  'pages.settings.UserManagement': '用户管理',
  'pages.settings.Flink': 'Flink 设置',
  'pages.settings.FlinkURL': '提交 Jar 文件路径到 FlinkSQL',

  'pages.settings.FlinkSQLJarMainParameter': '提交FlinkSQL的Jar的主类入参',
  'pages.settings.FlinkSQLJarMainClass': '提交FlinkSQL的Jar主类',
  'pages.settings.FlinkRestAPI': '使用 Rest API',
  'pages.settings.FlinkURLSplit': 'FlinkSQL 语句分隔符',
  'pages.settings.FlinkSQLLogic': '使用逻辑计划计算血缘关系',
  'pages.settings.FlinkJobID': '获取作业 ID 的最长等待时间（秒）',
  'pages.settings.FlinkNoSetting': '未设置',
  'pages.settings.FlinkNoUseSetting': 'Flink任务开启后，通过 JobManager 的 RestAPI 进行 Savepoint、Stop等操作',
  'pages.settings.FlinkLogic': 'Flink 任务的字段血缘分析计算是否基于逻辑计划 , 仅在 1.14 版本中支持',

  'pages.settings.FlinkUpdate': '修改',
  'pages.settings.FlinkSave': '保存',
  'pages.settings.FlinkCancel': '返回',
  'pages.settings.FlinkUse': '启用',
  'pages.settings.FlinkNotUse': '禁用',

  'pages.user.UserManger': '用户管理',
  'pages.user.UserName': '用户名',//用户名
  'pages.user.UserJobNumber': '工号',//工号
  'pages.user.UserPhoneNumber': '手机号',//手机号
  'pages.user.UserNickName': '昵称',//昵称
  'pages.user.UserIsUse': '是否启用',//是否启用
  'pages.user.UserUpdateTime': '最近更新时间',//最近更新时间
  'pages.user.UserCreateTime': '创建时间',//创建时间


  'pages.user.UserDeleteUser': '删除用户',
  'pages.user.UserCreateUser': '添加用户',
  'pages.user.UserUpdateUser': '修改用户',
  'pages.user.AssignRole': '分配角色',
  'pages.user.delete': '删除用户',
  'pages.user.deleteConfirm': '您确定要删除此用户吗？',
  'pages.user.enable': '启用用户',
  'pages.user.enableConfirm': '您确定要启用此用户吗？',
  'pages.user.disable': '禁用用户',
  'pages.user.disableConfirm': '您确定要禁用此用户吗？',
  'pages.user.UserEnterUserName': '请输入用户名',
  'pages.user.UserEnterUniqueUserName': '请输入唯一的用户名',
  'pages.user.UserEnterJobNumber': '请输入工号',
  'pages.user.UserEnterNickName': '请输入昵称',
  'pages.user.UserEnterPhoneNumber': '请输入手机号',
  'pages.user.UserOldPassword': '旧密码',
  'pages.user.UserNewPassword': '新密码',
  'pages.user.UserRepeatNewPassword': '重复新密码',
  'pages.user.UserEnterOldPassword': '请输入旧密码',
  'pages.user.UserEnterNewPassword': '请输入新密码',
  'pages.user.UserEnterRepeatNewPassword': '请重复输入新密码',
  'pages.user.UserNewPasswordNotMatch': '两次输入的新密码不一致',
  'pages.user.disableTotalOf': '被禁用的用户共 ',
  'pages.user.selectDisable': ' 个',


  'pages.tenant.TenantManager': '租户管理',
  'pages.tenant.TenantCode': '租户编码',
  'pages.tenant.Note': '备注/描述',
  'pages.tenant.CreateTime': '创建时间',
  'pages.tenant.UpdateTime': '最后更新时间',
  'pages.tenant.AssignUser': '分配用户',
  'pages.tenant.create': '创建租户',
  'pages.tenant.update': '修改租户',
  'pages.tenant.EnterTenantCode': '请输入租户编码!',
  'pages.tenant.EnterTenantNote': '请输入租户备注/描述信息!',
  'pages.tenant.delete': '删除租户',
  'pages.tenant.deleteConfirm': '您确定要删除此租户吗？',
  'pages.tenant.enable': '启用租户',
  'pages.tenant.enableConfirm': '您确定要启用此租户吗？',
  'pages.tenant.disable': '禁用租户',
  'pages.tenant.disableConfirm': '您确定要禁用此租户吗？',


  'pages.nameSpace.NameSpaceManagement': '命名空间管理',
  'pages.nameSpace.NameSpaceCode': '命名空间编码',
  'pages.nameSpace.belongTenant': '所属租户',
  'pages.nameSpace.enable': '是否启用',
  'pages.nameSpace.note': '备注/描述',
  'pages.nameSpace.createTime': '创建时间',
  'pages.nameSpace.updateTime': '最后更新时间',
  'pages.nameSpace.deleteNameSpace': '删除命名空间',
  'pages.nameSpace.deleteNameSpaceConfirm': '您确定要删除此命名空间吗？',
  'pages.nameSpace.enableNameSpace': '启用命名空间',
  'pages.nameSpace.enableNameSpaceConfirm': '您确定要启用此命名空间吗？',
  'pages.nameSpace.disableNameSpace': '禁用命名空间',
  'pages.nameSpace.disableNameSpaceConfirm': '您确定要禁用此命名空间吗？',
  'pages.nameSpace.create': '创建命名空间',
  'pages.nameSpace.update': '修改命名空间',
  'pages.nameSpace.EnterNameSpaceCode': '请输入命名空间编码!',
  'pages.nameSpace.EnterNameSpaceNote': '请输入命名空间的备注/描述信息!',
  'pages.nameSpace.disableTotalOf': '被禁用的命名空间共 ',
  'pages.nameSpace.selectDisable': '个',

  'pages.role.roleManagement': '角色管理',
  'pages.role.roleCode': '角色编码',
  'pages.role.roleName': '角色名称',
  'pages.role.namespaceIds': '命名空间',
  'pages.role.note': '备注/描述',
  'pages.role.belongTenant': '所属租户',
  'pages.role.createTime': '创建时间',
  'pages.role.updateTime': '最后更新时间',
  'pages.role.create': '创建角色',
  'pages.role.update': '修改角色',
  'pages.role.EnterRoleCode': '请输入角色编码!',
  'pages.role.EnterRoleName': '请输入角色名称!',
  'pages.role.selectNameSpace': '请选择命名空间!',
  'pages.role.EnterNote': '请输入角色的备注/描述信息!',
  'pages.role.delete': '删除角色',
  'pages.role.deleteConfirm': '您确定要删除此角色吗？',
  'pages.role.enable': '启用角色',
  'pages.role.enableConfirm': '您确定要启用此角色吗？',
  'pages.role.disable': '禁用角色',
  'pages.role.disableConfirm': '您确定要禁用此角色吗？',


  'pages.regist.openAPI': 'OpenAPI 文档',
  'pages.regist.BusinessComponent': '业务组件文档',

};
