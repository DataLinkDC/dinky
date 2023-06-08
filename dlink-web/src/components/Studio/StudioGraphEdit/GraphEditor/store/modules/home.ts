import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { Parameter } from '@/components/Studio/StudioGraphEdit/GraphEditor/ts-define/parameter';
import { Graph } from '@antv/x6';
import { getOperatorConfigure } from '../../service/request/test';
import { message } from 'antd';

export const initFlowDataAction = createAsyncThunk('fetchData', (payload, store) => {
  getOperatorConfigure().then((res: any) => {
    if (res.status !== 200) {
      message.error('请求节点参数失败!');
      return;
    }
    if (res?.data?.datas) {
      store.dispatch(initOperatorParameters(res.data.datas));
    }
  });
  // import(
  //   '@/components/Studio/StudioGraphEdit/GraphEditor/assets/json-data/operatorParameters.json'
  // ).then((res: any) => {
  //   if (res?.datas) {
  //     store.dispatch(initOperatorParameters(res.datas));
  //   }
  // });
});

const homeSlice = createSlice({
  name: 'home',
  initialState: {
    flowData: {},
    parameters: {},
    //初始化算子参数，注册算子组件
    operatorParameters: [] as Parameter[],
    currentSelectNode: {},
    //当前选中的算子节点名称
    currentSelectNodeName: '',
    //当前选中的节点参数数据
    currentSelectNodeParamsData: [],
    //保存graph在其他组件中调用
    graph: Graph,
  },
  reducers: {
    initFlowDataInfo(state, { payload }) {
      state.flowData = payload;
    },
    changeParameters(state, { payload }) {
      let parameters = payload?.store?.data?.data?.parameters;
      state.parameters = parameters.length > 0 ? parameters[0] : {};
    },
    //初始化算子参数stencil
    initOperatorParameters(state, { payload }) {
      state.operatorParameters = payload;
    },
    //保存当前选中的节点信息
    changeCurrentSelectNode(state, { payload }) {
      state.currentSelectNode = payload;
    },
    //保存当前选中的节点名称
    changeCurrentSelectNodeName(state, { payload }) {
      state.currentSelectNodeName = payload;
    },
    //设置当前节点属性数据
    changeCurrentSelectNodeParamsData(state, { payload }) {
      state.currentSelectNode = payload;
    },
    changeGraph(state, { payload }) {
      state.graph = payload;
    },
  },
  extraReducers: {},
});

export const {
  initFlowDataInfo,
  changeParameters,
  initOperatorParameters,
  changeCurrentSelectNode,
  changeCurrentSelectNodeName,
  changeCurrentSelectNodeParamsData,
  changeGraph,
} = homeSlice.actions;

export default homeSlice.reducer;
