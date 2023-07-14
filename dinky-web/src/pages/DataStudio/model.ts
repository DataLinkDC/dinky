import {Reducer} from "@@/plugin-dva/types";
import React from "react";
import {DataSources} from "@/types/RegCenter/data";

/**
 * 初始化布局宽高度
 * @type {{topHeight: number, leftMargin: number, footerHeight: number, paddingInline: number, bottomHeight: number, midMargin: number, otherHeight: number, sideWidth: number, leftToolWidth: number, rightMargin: number, headerNavHeight: number, headerHeight: number, marginTop: number}}
 */
export const VIEW = {
  headerHeight: 32,
  headerNavHeight: 55,
  footerHeight: 25,
  sideWidth: 40,
  leftToolWidth: 180,
  marginTop: 84,
  topHeight: 35.6,
  bottomHeight: 100,
  rightMargin: 32,
  leftMargin: 36,
  midMargin: 41,
  otherHeight: 10,
  paddingInline: 50,
};

export type SqlMetaData = {
  statement?: string,
  metaData?: MetaData[],
};
export type MetaData = {
  table: string,
  connector: string,
  columns: Column[],
};
export type Column = {
  name: string,
  type: string,
};
type TargetKey = React.MouseEvent | React.KeyboardEvent | number;


export type EnvType = {
  id?: number,
  name?: string,
  fragment?: boolean,
};

export type TaskType = {
  id?: number,
  catalogueId?: number,
  name?: string,
  dialect?: string,
  type?: string,
  checkPoint?: number,
  savePointStrategy?: number,
  savePointPath?: string,
  parallelism?: number,
  fragment?: boolean,
  statementSet?: boolean,
  batchModel?: boolean,
  config?: [],
  clusterId?: any,
  clusterName?: string,
  clusterConfigurationId?: number,
  clusterConfigurationName?: string,
  databaseId?: number,
  databaseName?: string,
  jarId?: number,
  envId?: number,
  jobInstanceId?: number,
  note?: string,
  enabled?: boolean,
  createTime?: Date,
  updateTime?: Date,
  statement?: string,
  session: string;
  maxRowNum: number;
  jobName: string;
  useResult: boolean;
  useChangeLog: boolean;
  useAutoCancel: boolean;
  useSession: boolean;
};

export type ConsoleType = {
  result: {};
  chart: {};
}

export type TabsItemType = {
  id:string,
  label: string;
  params: string|object;
  type: "metadata";
  key: number,
  value: string;
  icon: any;
  closable: boolean;
  path: string[];
  task?: TaskType;
  console: ConsoleType;
  monaco?: any;
  isModified: boolean;
  sqlMetaData?: SqlMetaData;
  metaStore?: MetaStoreCatalogType[];
}

export type TabsType = {
  activeKey: number;
  panes: TabsItemType[];
}

export type ConnectorType = {
  tablename: string;
}

export type SessionType = {
  session?: string;
  sessionConfig?: {
    type?: string;
    clusterId?: number;
    clusterName?: string;
    address?: string;
  }
  createUser?: string;
  createTime?: string;
  connectors: ConnectorType[];
}

export type MetaStoreCatalogType = {
  name: string;
  databases: MetaStoreDataBaseType[];
}

export type MetaStoreDataBaseType = {
  name: string;
  tables: MetaStoreTableType[];
  views: string[];
  functions: string[];
  userFunctions: string[];
  modules: string[];
}

export type MetaStoreTableType = {
  name: string;
  columns: MetaStoreColumnType[];
}

export type MetaStoreColumnType = {
  name: string;
  type: string;
}

export type container = {
  selectKey: string;
  height: number | string;
  width: number | string;
  maxWidth?: number | string;
}

export type StateType = {
  isFullScreen: boolean;
  toolContentHeight: number;
  centerContentHeight: number;
  leftContainer: container;
  rightContainer: container;
  bottomContainer: container;
  database: DataSources.DataSource[];
  selectDatabaseId: number;
  tabs: TabsType;
};
export type ModelType = {
  namespace: string;
  state: StateType;
  effects: {};
  reducers: {
    updateToolContentHeight: Reducer<StateType>;
    updateCenterContentHeight: Reducer<StateType>;
    updateSelectLeftKey: Reducer<StateType>;
    updateLeftWidth: Reducer<StateType>;
    updateSelectRightKey: Reducer<StateType>;
    updateRightWidth: Reducer<StateType>;
    updateSelectBottomKey: Reducer<StateType>;
    updateBottomHeight: Reducer<StateType>;
    saveDataBase: Reducer<StateType>;
    updateTabsActiveKey: Reducer<StateType>;
    closeTab: Reducer<StateType>;
    addTab: Reducer<StateType>;
    updateSelectDatabaseId: Reducer<StateType>;
  };
};
const Model: ModelType = {

  namespace: 'Studio',
  state: {
    isFullScreen: false,
    toolContentHeight:0,
    centerContentHeight:0,
    leftContainer: {
      selectKey: 'menu.datastudio.project',
      height: "100%",
      width: 500,
    },
    rightContainer: {
      selectKey: 'menu.datastudio.jobConfig',
      height: "100%",
      width: 500,
    },
    bottomContainer: {
      selectKey: 'menu.datastudio.console',
      height: 400,
      width: "100%",
    },
    database: [],
    selectDatabaseId: 0,
    tabs: {
      activeKey: 0,
      panes: [],
    },
  },
  effects: {},
  reducers: {
    /**
     * 更新工具栏高度
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: any, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: container, leftContainer: container, bottomContainer: container}}
     */
    updateToolContentHeight(state, {payload}) {
      return {
        ...state,
        toolContentHeight: payload
      };
    },
    /**
     * 更新中间内容高度
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: any, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: container, leftContainer: container, bottomContainer: container}}
     */
    updateCenterContentHeight(state, {payload}) {
      return {
        ...state,
        centerContentHeight: payload
      };
    },
    /**
     * 更新左侧选中key
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: container, leftContainer: {selectKey: any, width: number | string, height: number | string, maxWidth?: number | string}, bottomContainer: container}}
     */
    updateSelectLeftKey(state, {payload}) {
      return {
        ...state,
        leftContainer: {
          ...state.leftContainer,
          selectKey: payload,
        }
      };
    },
    /**
     * 更新左侧宽度
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: container, leftContainer: {selectKey: string, width: any, height: number | string, maxWidth?: number | string}, bottomContainer: container}}
     */
    updateLeftWidth(state, {payload}) {
      return {
        ...state,
        leftContainer: {
          ...state.leftContainer,
          width: payload,
        }
      };
    },
    /**
     * 更新右侧选中key
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: {selectKey: any, width: number | string, height: number | string, maxWidth?: number | string}, leftContainer: container, bottomContainer: container}}
     */
    updateSelectRightKey(state, {payload}) {
      return {
        ...state,
        rightContainer: {
          ...state.rightContainer,
          selectKey: payload,
        }
      };
    },
    /**
     * 更新右侧选中key
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: {selectKey: string, width: any, height: number | string, maxWidth?: number | string}, leftContainer: container, bottomContainer: container}}
     */
    updateRightWidth(state, {payload}) {
      return {
        ...state,
        rightContainer: {
          ...state.rightContainer,
          width: payload,
        }
      };
    },
    /**
     * 更新底部选中key
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: container, leftContainer: container, bottomContainer: {selectKey: any, width: number | string, height: number | string, maxWidth?: number | string}}}
     */
    updateSelectBottomKey(state, {payload}) {
      if (payload === '') {
        state.centerContentHeight =(state.centerContentHeight as number)  + (state.bottomContainer.height as number);
        state.toolContentHeight =(state.toolContentHeight as number)  + (state.bottomContainer.height as number);
      }else {
        state.centerContentHeight =(state.centerContentHeight as number)  - (state.bottomContainer.height as number);
        state.toolContentHeight =(state.toolContentHeight as number)  - (state.bottomContainer.height as number);
      }
      return {
        ...state,
        bottomContainer: {
          ...state.bottomContainer,
          selectKey: payload,
        }
      };
    },
    /**
     * 更新底部高度
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: container, leftContainer: container, bottomContainer: {selectKey: string, width: number | string, height: any, maxWidth?: number | string}}}
     */
    updateBottomHeight(state, {payload}) {
      return {
        ...state,
        bottomContainer: {
          ...state.bottomContainer,
          height: payload,
        }
      };
    },
    /**
     * 更新数据库
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: container, leftContainer: container, bottomContainer: container}}
     */
    saveDataBase(state, {payload}) {
      return {
        ...state,
        database: [...payload],
      };
    },
    /**
     * 更新tabs activeKey
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSourceDataSources.DataSource[], tabs: {panes: TabsItemType[], activeKey: any}, isFullScreen: boolean, rightContainer: container, leftContainer: container, bottomContainer: container}}
     */
    updateTabsActiveKey(state, {payload}) {
      return {
        ...state,
        tabs: {
          ...state.tabs,
          activeKey: payload
        },
      };
    } ,
    /**
     *  关闭tab
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: {panes: TabsItemType[], activeKey: number}, isFullScreen: boolean, rightContainer: container, leftContainer: container, bottomContainer: container}}
     */
    closeTab(state, {payload }) {
      const needCloseKey=payload as  TargetKey;
      const {tabs:{panes,activeKey}}= state;
      if (needCloseKey===activeKey){
        for (const [index,pane] of panes.entries()) {
          if (pane.key === needCloseKey) {
            const newActiveKey =index+1>=panes.length?index+1>1&&index+1==panes.length?panes[index-1].key:1: panes[index+1].key;
            return {
              ...state,
              tabs: {
                panes:panes.filter(pane => pane.key !== needCloseKey),
                activeKey: newActiveKey
              },
            };
          }
        }
      }
      const newPanes = panes.filter(pane => pane.key !== needCloseKey)
      return {
        ...state,
        tabs: {
          panes:newPanes,
          activeKey: activeKey
        },
      };
    },
    /**
     * 添加tab 如果存在则不添加
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: {panes: TabsItemType[], activeKey: number}, isFullScreen: boolean, rightContainer: container, leftContainer: container, bottomContainer: container}}
     */
    addTab(state, {payload }) {
      const node=payload as  TabsItemType;
      for (const item of state.tabs.panes) {
        if (item.id === node.id) {
          return {
            ...state,
            tabs:{
              ...state.tabs,
              activeKey:item.key
            }
          };
        }
      }
        node.key=state.tabs.panes.length===0?0:state.tabs.panes[state.tabs.panes.length-1].key+1 ;
        return {
          ...state,
          tabs: {
            panes:[...state.tabs.panes,node],
            activeKey: node.key
          },
      }
    },
    /**
     * 更新选中数据库id
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], selectDatabaseId: any, tabs: TabsType, isFullScreen: boolean, rightContainer: container, leftContainer: container, bottomContainer: container}}
     */
    updateSelectDatabaseId(state, {payload}) {
        return {
            ...state,
            selectDatabaseId: payload,
        }
    }




  }
}
export default Model;
