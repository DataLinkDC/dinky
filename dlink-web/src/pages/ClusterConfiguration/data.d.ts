export type ClusterConfigurationTableListItem = {
  id: number,
  name: string,
  alias: string,
  type: string,
  config: any,
  configJson: string,
  available: boolean,
  note: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};
