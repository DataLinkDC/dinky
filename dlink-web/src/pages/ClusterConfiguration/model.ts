import type { Reducer} from "umi";

export type ClusterConfigurationType = {
  id: number,
  name: string,
  alias: string,
  type: string,
  config: any,
  available: boolean,
  note: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
}

export type ConfigurationStateType = {
  clusterConfiguration?: ClusterConfigurationType[];
}

export type ConfigurationModelType = {
  namespace: string;
  state: ConfigurationStateType;
  effects: {
  };
  reducers: {
    saveClusterConfiguration: Reducer<ConfigurationStateType>;
  };
};

const ClusterConfigurationModel : ConfigurationModelType = {
  namespace: 'Configuration',
  state: {
    clusterConfiguration: [],
  },

  effects: {

  },

  reducers: {
    saveClusterConfiguration(state, {payload}) {
      return {
        ...state,
        clusterConfiguration: [...payload],
      };
    },
  },
}

export default ClusterConfigurationModel;
