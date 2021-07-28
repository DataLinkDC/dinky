export type DataBaseItem = {
  id: number,
  name: string,
  alias: string,
  groupName: string,
  type: string,
  url: string,
  username: string,
  password: string,
  note: string,
  dbVersion: string,
  status: boolean,
  healthTime: Date,
  heartbeatTime: Date,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};


export type DataBaseFormProps = {
  name: string,
  alias: string,
  groupName: string,
  type: string,
  ip: string,
  port: number,
  url: string,
  username: string,
  password: string,
  note: string,
  dbVersion: string,
  enabled: boolean,
}
