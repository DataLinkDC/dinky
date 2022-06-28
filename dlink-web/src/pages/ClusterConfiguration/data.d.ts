export type ClusterConfigurationTableListItem = {
  id: number,
  name: string,
  alias: string,
  type: string,
  config: any,
  configJson: string,
  isAvailable: boolean,
  note: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};
