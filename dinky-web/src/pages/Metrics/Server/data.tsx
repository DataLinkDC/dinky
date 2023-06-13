export type MetricsDataType = {
  content: {
    jvm: JVMType,
    cpu: CPUType,
    mem: MemoryType,
  },
  metricsTotal: number,
  model: string,
  heartTime: Date,
}
type JVMType = {
  total: number,
  max: number,
  free: number,
  cpuUsed: number,
  nonHeapMax: number,
  nonHeapUsed: number,
  heapMax: number,
  heapUsed: number,
  threadPeakCount: number,
  threadCount: number,
  version: string,
  home: string,
}

type CPUType = {
  cpuNum: number,
  sys: number,
  wait: number,
  free: number,
}

type MemoryType = {
  total: number,
  used: number,
  free: number,
}
