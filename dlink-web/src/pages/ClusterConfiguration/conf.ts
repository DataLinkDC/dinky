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

import {l} from "@/utils/intl"

export type Config = {
  name: string,
  lable: string,
  placeholder: string
  defaultValue?: string
  help?: string
  showOnSubmitType?: string
  showType?: string
}

export const HADOOP_CONFIG_LIST: Config[] = [{
  name: 'ha.zookeeper.quorum',
  lable: 'ha.zookeeper.quorum',
  placeholder: '192.168.123.1:2181,192.168.123.2:2181,192.168.123.3:2181',
}];
export const KUBERNETES_CONFIG_LIST: Config[] = [{
  name: 'kubernetes.namespace',
  lable: 'kubernetes.namespace',
  placeholder: l('pages.registerCenter.clusterConfig.help.kubernets.namespace'),
  showType: 'input'
},{
  name: 'kubernetes.container.image',
  lable: 'kubernetes.container.image',
  placeholder: l('pages.registerCenter.clusterConfig.help.kubernets.image'),
  showType: 'input'

},{
  name: 'kubernetes.rest-service.exposed.type',
  lable: 'kubernetes.rest-service.exposed.type',
  placeholder: 'NodePort',
  defaultValue: '',
  showType: 'input',
  showOnSubmitType: 'Kubernetes'
},{
  name: 'kubernetes.jobmanager.cpu',
  lable: 'kubernetes.jobmanager.cpu',
  showType: 'input',
  placeholder: l('pages.registerCenter.clusterConfig.help.kubernets.jmcpu'),
}, {
  name: 'kubernetes.taskmanager.cpu',
  lable: 'kubernetes.taskmanager.cpu',
  showType: 'input',
  placeholder: l('pages.registerCenter.clusterConfig.help.kubernets.tmcpu'),
}
];
export const FLINK_CONFIG_LIST: Config[] = [
 {
  name: 'jobmanager.memory.process.size',
  lable: 'jobmanager.memory.process.size',
  placeholder: l('pages.registerCenter.clusterConfig.help.kubernets.jobManagerMemory'),
}, {
  name: 'taskmanager.memory.process.size',
  lable: 'taskmanager.memory.process.size',
  placeholder: l('pages.registerCenter.clusterConfig.help.kubernets.taskManagerMemory'),
}, {
  name: 'taskmanager.memory.framework.heap.size',
  lable: 'taskmanager.memory.framework.heap.size',
  placeholder: '',
}, {
  name: 'taskmanager.numberOfTaskSlots',
  lable: 'taskmanager.numberOfTaskSlots',
  placeholder: '',
}, {
  name: 'state.savepoints.dir',
  lable: 'state.savepoints.dir',
  placeholder: 'hdfs:///flink/savepoints/',
}, {
  name: 'state.checkpoints.dir',
  lable: 'state.checkpoints.dir',
  placeholder: 'hdfs:///flink/savepoints/',
}
];

export const APP_CONFIG_LIST: Config[] = [{
  name: 'userJarPath',
  lable: l('pages.registerCenter.clusterConfig.jar.path'),
  placeholder: 'local:///opt/example.jar',
}, {
  name: 'userJarMainAppClass',
  lable: l('pages.registerCenter.clusterConfig.jar.class'),
  placeholder: 'com.example.app',
}, {
  name: 'userJarParas',
  lable: l('pages.registerCenter.clusterConfig.jar.args'),
  placeholder: 'example: -conf test.properties',
}
];

export function HADOOP_CONFIG_NAME_LIST () {
  const list: string[] = [];
  HADOOP_CONFIG_LIST.forEach(item => {
    list.push(item.name);
  });
  return list;
}

export function KUBERNETES_CONFIG_NAME_LIST () {
  const list: string[] = [];
  KUBERNETES_CONFIG_LIST.forEach(item => {
    list.push(item.name);
  });
  return list;
}
export function APP_CONFIG_NAME_LIST () {
  const list: string[] = [];
  APP_CONFIG_LIST.forEach(item => {
    list.push(item.name);
  });
  return list;
}

export function FLINK_CONFIG_NAME_LIST() {
  const list: string[] = [];
  FLINK_CONFIG_LIST.forEach(item => {
    list.push(item.name);
  });
  return list;
}
