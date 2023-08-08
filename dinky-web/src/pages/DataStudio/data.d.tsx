export type StudioParam = {
  useSession:boolean;
  session: string,
  useRemote?:boolean;
  clusterId?: number,
  useResult:boolean;
  maxRowNum?: number,
  statement: string,
  fragment?: boolean,
  jobName?:string,
  parallelism?: number,
  checkPoint?: number,
  savePointPath?: string,
}
