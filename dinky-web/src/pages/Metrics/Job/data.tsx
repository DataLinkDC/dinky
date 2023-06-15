type JobMetrics = {
  taskId: number
  flinkJobId: string
  jobName: string
  verticesId: string
  metricsId: string
  url: string
}
type Task = {
  id: number
  jid: string
  name: string
  type: string
  clusterName:string
}
type Vertices={
  id:string
  name:string
  status:string
  parallelism:number
}
