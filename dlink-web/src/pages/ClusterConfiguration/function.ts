import {
  FLINK_CONFIG_NAME_LIST,
  HADOOP_CONFIG_NAME_LIST,
  KUBERNETES_CONFIG_NAME_LIST
} from "@/pages/ClusterConfiguration/conf";

export function getConfig(values:any) {
  let flinkConfig = addValueToMap(values,FLINK_CONFIG_NAME_LIST());
  addListToMap(values.flinkConfigList,flinkConfig);
  if(values.type=='Yarn') {
    let hadoopConfig = addValueToMap(values, HADOOP_CONFIG_NAME_LIST());
    addListToMap(values.hadoopConfigList, hadoopConfig);
    return {
      hadoopConfigPath:values.hadoopConfigPath,
      flinkLibPath:values.flinkLibPath,
      flinkConfigPath:values.flinkConfigPath,
      hadoopConfig,
      flinkConfig,
    };
  }else if(values.type=='Kubernetes') {
    let kubernetesConfig = addValueToMap(values, KUBERNETES_CONFIG_NAME_LIST());
    addListToMap(values.kubernetesConfigList, kubernetesConfig);
    return {
      flinkLibPath:values.flinkLibPath,
      flinkConfigPath:values.flinkConfigPath,
      kubernetesConfig,
      flinkConfig,
    };
  }
}

type ConfigItem = {
  name: string,
  value: string,
};

function addListToMap(list:[ConfigItem],config:{}){
  for(let i in list){
    config[list[i].name]=list[i].value;
  }
}

function addValueToMap(values:{},keys: string []){
  let config = {};
  if(!values){
    return config;
  }
  for(let i in keys){
    config[keys[i]]=values[keys[i]];
  }
  return config;
}

export function getConfigFormValues(values:any) {
  if(!values.id){
    return {type:values.type};
  }
  let formValues = addValueToMap(values,[
    'id',
    'name',
    'alias',
    'type',
    'note',
    'enabled',
    'enabled',
  ]);
  let config = JSON.parse(values.configJson);
  let configValues = addValueToMap(config,[
    'hadoopConfigPath',
    'flinkLibPath',
    'flinkConfigPath',
  ]);
  let hadoopConfig = addValueToMap(config.hadoopConfig,HADOOP_CONFIG_NAME_LIST());
  let kubernetesConfig = addValueToMap(config.kubernetesConfig,KUBERNETES_CONFIG_NAME_LIST());
  let flinkConfig = addValueToMap(config.flinkConfig,FLINK_CONFIG_NAME_LIST());
  let hadoopConfigList = addMapToList(config.hadoopConfig,HADOOP_CONFIG_NAME_LIST());
  let kubernetesConfigList = addMapToList(config.kubernetesConfig,KUBERNETES_CONFIG_NAME_LIST());
  let flinkConfigList = addMapToList(config.flinkConfig,FLINK_CONFIG_NAME_LIST());
  return {
    ...formValues,
    ...configValues,
    ...hadoopConfig,
    ...kubernetesConfig,
    hadoopConfigList,
    kubernetesConfigList,
    ...flinkConfig,
    flinkConfigList
  }
}

function addMapToList(map:{},keys:string[]){
  let list:ConfigItem[]=[];
  for(let i in map){
    if(!keys.includes(i)){
      list.push({
        name:i,
        value:map[i],
      })
    }
  }
  return list;
}
