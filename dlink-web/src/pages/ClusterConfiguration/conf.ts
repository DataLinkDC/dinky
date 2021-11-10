export type Config = {
  name: string,
  lable: string,
  placeholder: string
}

export function HADOOP_CONFIG_NAME_LIST () {
  let list: string[] = [];
  for (let i in HADOOP_CONFIG_LIST) {
    list.push(HADOOP_CONFIG_LIST[i].name);
  }
  return list;
}

export function FLINK_CONFIG_NAME_LIST() {
  let list: string[] = [];
  for (let i in FLINK_CONFIG_LIST) {
    list.push(FLINK_CONFIG_LIST[i].name);
  }
  return list;
}

export const HADOOP_CONFIG_LIST: Config[] = [{
  name: 'ha.zookeeper.quorum',
  lable: 'ha.zookeeper.quorum',
  placeholder: '值如 192.168.123.1:2181,192.168.123.2:2181,192.168.123.3:2181',
}];
export const FLINK_CONFIG_LIST: Config[] = [{
  name: 'jobmanager.memory.process.size',
  lable: 'jobmanager.memory.process.size',
  placeholder: '值如 1600m',
}, {
  name: 'taskmanager.memory.flink.size',
  lable: 'taskmanager.memory.flink.size',
  placeholder: '值如 2048m',
}, {
  name: 'taskmanager.memory.framework.heap.size',
  lable: 'taskmanager.memory.framework.heap.size',
  placeholder: '值如 1024m',
}, {
  name: 'taskmanager.numberOfTaskSlots',
  lable: 'taskmanager.numberOfTaskSlots',
  placeholder: '值如 4',
}, {
  name: 'parallelism.default',
  lable: 'parallelism.default',
  placeholder: '值如 4',
}
];
