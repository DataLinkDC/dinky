import {FLINK_CONFIG_NAME_LIST, HADOOP_CONFIG_NAME_LIST} from "@/pages/ClusterConfiguration/conf";

export function getConfig(values:any) {
  let hadoopConfig = addValueToMap(values,HADOOP_CONFIG_NAME_LIST());
  addListToMap(values.hadoopConfigList,hadoopConfig);
  let flinkConfig = addValueToMap(values,FLINK_CONFIG_NAME_LIST());
  addListToMap(values.flinkConfigList,flinkConfig);
  return {
    hadoopConfigPath:values.hadoopConfigPath,
    flinkLibPath:values.flinkLibPath,
    flinkConfigPath:values.flinkConfigPath,
    hadoopConfig:hadoopConfig,
    flinkConfig:flinkConfig,
  };
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
  let flinkConfig = addValueToMap(config.flinkConfig,FLINK_CONFIG_NAME_LIST());
  let hadoopConfigList = addMapToList(config.hadoopConfig,HADOOP_CONFIG_NAME_LIST());
  let flinkConfigList = addMapToList(config.flinkConfig,FLINK_CONFIG_NAME_LIST());
  return {
    ...formValues,
    ...configValues,
    ...hadoopConfig,
    hadoopConfigList:hadoopConfigList,
    ...flinkConfig,
    flinkConfigList:flinkConfigList
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
