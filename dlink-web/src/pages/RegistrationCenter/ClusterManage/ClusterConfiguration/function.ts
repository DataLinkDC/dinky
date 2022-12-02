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


import {
  DOCKER_CONFIG_NAME_LIST,
  FLINK_CONFIG_NAME_LIST,
  HADOOP_CONFIG_NAME_LIST,
  KUBERNETES_CONFIG_NAME_LIST
} from "@/pages/RegistrationCenter/ClusterManage/ClusterConfiguration/conf";
import {ClusterConfigurationTableListItem} from "@/pages/RegistrationCenter/data";

export function getConfig(values: any) {
  let flinkConfig = addValueToMap(values, FLINK_CONFIG_NAME_LIST());
  addListToMap(values.flinkConfigList, flinkConfig);
  if (values.type == 'Yarn') {
    let hadoopConfig = addValueToMap(values, HADOOP_CONFIG_NAME_LIST());
    addListToMap(values.hadoopConfigList, hadoopConfig);
    return {
      hadoopConfigPath: values.hadoopConfigPath,
      flinkLibPath: values.flinkLibPath,
      flinkConfigPath: values.flinkConfigPath,
      hadoopConfig,
      flinkConfig,
    };
  } else if (values.type == 'Kubernetes') {
    let kubernetesConfig = addValueToMap(values, KUBERNETES_CONFIG_NAME_LIST());
    addListToMap(values.kubernetesConfigList, kubernetesConfig);
    let dockerConfig = addValueToMap(values, DOCKER_CONFIG_NAME_LIST());
    addListToMap(values.kubernetesConfigList, dockerConfig);
    return {
      flinkLibPath: values.flinkLibPath,
      flinkConfigPath: values.flinkConfigPath,
      kubernetesConfig,
      dockerConfig,

      flinkConfig,
    };
  } else {
    //all code paths must return a value.
    return {}
  }
}

type ConfigItem = {
  name: string,
  value: string,
};

function addListToMap(list: [ConfigItem], config: {}) {
  for (let i in list) {
    //the param maybe undefind
    if (list[i] != undefined) {
      config[list[i].name] = list[i].value;
    }
  }
}

function addValueToMap(values: {}, keys: string []) {
  let config = {};
  if (!values) {
    return config;
  }
  for (let i in keys) {
    config[keys[i]] = values[keys[i]];
  }
  return config;
}

export function getConfigFormValues(values: any) {
  if (!values.id) {
    return {type: values.type};
  }
  let formValues = addValueToMap(values, [
    'id',
    'name',
    'alias',
    'type',
    'note',
    'enabled',
    'enabled',
  ]);
  let config = JSON.parse(values.configJson);
  let configValues = addValueToMap(config, [
    'hadoopConfigPath',
    'flinkLibPath',
    'flinkConfigPath',
  ]);
  let hadoopConfig = addValueToMap(config.hadoopConfig, HADOOP_CONFIG_NAME_LIST());
  let kubernetesConfig = addValueToMap(config.kubernetesConfig, KUBERNETES_CONFIG_NAME_LIST());
  let dockerConfig = addValueToMap(config.dockerConfig, DOCKER_CONFIG_NAME_LIST());
  let flinkConfig = addValueToMap(config.flinkConfig, FLINK_CONFIG_NAME_LIST());
  let hadoopConfigList = addMapToList(config.hadoopConfig, HADOOP_CONFIG_NAME_LIST());
  let kubernetesConfigList = addMapToList(config.kubernetesConfig, KUBERNETES_CONFIG_NAME_LIST());
  let dockerConfigList = addMapToList(config.dockerConfig, DOCKER_CONFIG_NAME_LIST());
  let flinkConfigList = addMapToList(config.flinkConfig, FLINK_CONFIG_NAME_LIST());
  return {
    ...formValues,
    ...configValues,
    ...hadoopConfig,
    ...kubernetesConfig,
    ...dockerConfig,
    hadoopConfigList,
    kubernetesConfigList,
    dockerConfigList,
    ...flinkConfig,
    flinkConfigList
  }
}

function addMapToList(map: {}, keys: string[]) {
  let list: ConfigItem[] = [];
  for (let i in map) {
    if (!keys.includes(i)) {
      list.push({
        name: i,
        value: map[i],
      })
    }
  }
  return list;
}

export function getHadoopConfigPathFromClusterConfigurationsById(id: number, clusterConfigurations: ClusterConfigurationTableListItem[]) {
  for (let i in clusterConfigurations) {
    if (clusterConfigurations[i].id == id) {
      return getConfigFormValues(clusterConfigurations[i])['hadoopConfigPath']
    }
  }
  return undefined;
}
