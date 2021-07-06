
export type SessionItem = {
  session: string,
  sessionConfig: {
    type:string,
    useRemote:boolean,
    clusterId:number,
  },
  createUser: string,
  createTime: string,
};
