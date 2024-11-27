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
  'welcome.welcome': 'welcome to Dinky！',
  'welcome.welcome.content':
    'A next-generation real-time computing platform that is deeply customized for Apache Flink, providing agile Flink SQL and Flink Jar job development',
  'welcome.welcome.content.tip1': "This looks like it's your first time logging into Dinky",
  'welcome.welcome.content.tip2':
    "Don't worry, we only need a few simple guides to enjoy the Dinky tour!",
  'welcome.welcome.setPwd.tip': 'Set the admin password',
  'welcome.welcome.setPwd': 'Set the password',
  'welcome.welcome.skip': 'Skip this step',

  'welcome.next': 'Next',
  'welcome.prev': 'Previous',
  'welcome.submit': 'Submit',
  'welcome.finish.title': 'Init Finished！',
  'welcome.finish': 'Start your Dinky journey today！',

  'welcome.goLogin': 'Go Login！',
  'welcome.base.config.title': 'Base Config',
  'welcome.base.config.dinky.url.title': 'Dinky address：',
  'welcome.base.config.dinky.url':
    'Make sure that the external service address of dinky can be accessed in the k8s or yarn cluster, otherwise the status of the application task may not be monitored',
  'welcome.tips':
    "If you are still unsure about how to enter the parameters, don't worry, leave them as default, and you can go to the configuration center at any time to modify them",
  'welcome.base.config.taskowner.title': 'Task owner type：',
  'welcome.base.config.taskowner':
    'When [OWNER] is selected, only the job owner can operate the job, and no other user can operate the modify job,\\n When [OWNER_AND_MAINTAINER] is selected,\\n Both the job owner and the maintainer can operate the modification job, and when [ALL] is selected, everyone can operate the modification job, which is [ALL] by default.',

  'welcome.flink.config.title': 'Flink Config',
  'welcome.flink.config.jobwait.title': 'Job wait time：',
  'welcome.flink.config.jobwait':
    'The maximum wait time (seconds) to get the Job ID\\n when submitting an Application or PerJob task, and if the job is submitted slowly, you need to increase this value',
  'welcome.flink.config.useHistoryServer.title': 'Use Flink History Server：',
  'welcome.flink.config.useHistoryServer':
    'This feature will have a built-in Flink History Server in Dinky, which is used to query the history of Flink tasks, so that Flink tasks can reduce the UNKNOWN status and input the last status information of Flink tasks',
  'welcome.flink.config.historyPort.title': 'Flink History Server Port：',
  'welcome.flink.config.historyPort':
    'The built-in Flink History Server port, for example, 8082, ensures that the port is not occupied'
};
