export type Config = {
  name: string,
  lable: string,
  placeholder: string
  defaultValue?: string
}

export const HADOOP_CONFIG_LIST: Config[] = [{
  name: 'ha.zookeeper.quorum',
  lable: 'ha.zookeeper.quorum',
  placeholder: '192.168.123.1:2181,192.168.123.2:2181,192.168.123.3:2181',
}];
export const KUBERNETES_CONFIG_LIST: Config[] = [{
  name: 'kubernetes.namespace',
  lable: 'kubernetes.namespace',
  placeholder: 'dlink',
},{
  name: 'kubernetes.container.image',
  lable: 'kubernetes.container.image',
  placeholder: 'dlink',
},{
  name: 'kubernetes.rest-service.exposed.type',
  lable: 'kubernetes.rest-service.exposed.type',
  placeholder: 'NodePort',
  defaultValue: 'NodePort',
}];
export const FLINK_CONFIG_LIST: Config[] = [{
  name: 'jobmanager.memory.process.size',
  lable: 'jobmanager.memory.process.size',
  placeholder: '1600m',
}, {
  name: 'taskmanager.memory.process.size',
  lable: 'taskmanager.memory.process.size',
  placeholder: '2048m',
}, {
  name: 'taskmanager.memory.framework.heap.size',
  lable: 'taskmanager.memory.framework.heap.size',
  placeholder: '1024m',
}, {
  name: 'taskmanager.numberOfTaskSlots',
  lable: 'taskmanager.numberOfTaskSlots',
  placeholder: '4',
}, {
  name: 'parallelism.default',
  lable: 'parallelism.default',
  placeholder: '1',
}, {
  name: 'state.savepoints.dir',
  lable: 'state.savepoints.dir',
  placeholder: 'hdfs:///flink/savepoints/',
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

export function FLINK_CONFIG_NAME_LIST() {
  const list: string[] = [];
  FLINK_CONFIG_LIST.forEach(item => {
    list.push(item.name);
  });
  return list;
}
