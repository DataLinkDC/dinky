import {Reducer} from "@@/plugin-dva/types";
import React from "react";
import {DataSources} from "@/types/RegCenter/data";
import {QueryParams} from "@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/data";
import {l} from "@/utils/intl";

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
  midMargin: 50,
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
};

export type ConsoleType = {
  // eslint-disable-next-line @typescript-eslint/ban-types
  result: {};
  // eslint-disable-next-line @typescript-eslint/ban-types
  chart: {};
}
export type MetadataParams = {
  queryParams: QueryParams;
  // eslint-disable-next-line @typescript-eslint/ban-types
  tableInfo: {};
}
export enum TabsPageType {None="",metadata="metadata",flinkSql="flinkSql"}
export type TabsItemType = {
  id:string,
  label: string;
  breadcrumbLabel: string;
  params: string|object|MetadataParams;
  type: TabsPageType;
  key: string,
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
  activeKey: string;
  activeBreadcrumbTitle:string;
  panes: TabsItemType[];
}

export type ConnectorType = {
  tablename: string;
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

export type Container = {
  selectKey: string;
  selectSubKey: {[c:string]:string};
  height: number | string;
  width: number | string;
  maxWidth?: number | string;
}
export type BottomContainerContent = {
  console:string
}
export type StateType = {
  isFullScreen: boolean;
  toolContentHeight: number;
  centerContentHeight: number;
  leftContainer: Container;
  rightContainer: Container;
  bottomContainer: Container;
  database: {
    dbData: DataSources.DataSource[];
    selectDatabaseId: number | null;
    expandKeys: [];
    selectKey: [];
  };
  tabs: TabsType;
  bottomContainerContent:BottomContainerContent
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
    updateSelectBottomSubKey: Reducer<StateType>;
    updateBottomHeight: Reducer<StateType>;
    saveDataBase: Reducer<StateType>;
    updateTabsActiveKey: Reducer<StateType>;
    closeTab: Reducer<StateType>;
    addTab: Reducer<StateType>;
    closeAllTabs: Reducer<StateType>;
    closeOtherTabs: Reducer<StateType>;
    updateSelectDatabaseId: Reducer<StateType>;
    updateDatabaseExpandKey: Reducer<StateType>;
    updateDatabaseSelectKey: Reducer<StateType>;
    updateBottomConsole: Reducer<StateType>;
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
      selectSubKey: {},
      height: "100%",
      width: 260,
    },
    rightContainer: {
      selectKey: '',
      selectSubKey: {},
      height: "100%",
      width: 260,
    },
    bottomContainer: {
      selectKey: 'menu.datastudio.console',
      selectSubKey: {},
      height: 180,
      width: "100%",
    },
    database: {
      dbData : [],
      selectDatabaseId: null,
      expandKeys: [],
      selectKey: [],
    },
    tabs: {
      activeBreadcrumbTitle:"",
      activeKey: "0",
      panes: [],
    },
    bottomContainerContent:{
      console:""
    }
  },
  effects: {},
  reducers: {
    /**
     * 更新工具栏高度
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: any, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: Container, leftContainer: Container, bottomContainer: Container}}
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
     * @returns {{centerContentHeight: any, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: Container, leftContainer: Container, bottomContainer: Container}}
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
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: Container, leftContainer: {selectKey: any, width: number | string, height: number | string, maxWidth?: number | string}, bottomContainer: Container}}
     */
    updateSelectLeftKey(state, {payload}) {
      return {
        ...state,
        leftContainer: {
          ...state.leftContainer,
          selectKey: payload,
          label: payload.trim()===""?"":l(payload),
        }
      };
    },
    /**
     * 更新左侧宽度
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: Container, leftContainer: {selectKey: string, width: any, height: number | string, maxWidth?: number | string}, bottomContainer: Container}}
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
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: {selectKey: any, width: number | string, height: number | string, maxWidth?: number | string}, leftContainer: Container, bottomContainer: Container}}
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
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: {selectKey: string, width: any, height: number | string, maxWidth?: number | string}, leftContainer: Container, bottomContainer: Container}}
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
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: Container, leftContainer: Container, bottomContainer: {selectKey: any, width: number | string, height: number | string, maxWidth?: number | string}}}
     */
    updateSelectBottomKey(state, {payload}) {
      let centerContentHeight=0;
      let toolContentHeight=0;
      if (payload === '') {
        centerContentHeight =(state.centerContentHeight as number)  + (state.bottomContainer.height as number);
        toolContentHeight =(state.toolContentHeight as number)  + (state.bottomContainer.height as number);
        console.log(2)
      }else if (state.bottomContainer.selectKey!=='' && payload!==state.bottomContainer.selectKey){
        centerContentHeight =(state.centerContentHeight as number);
        toolContentHeight =(state.toolContentHeight as number);
      }else {
        centerContentHeight =(state.centerContentHeight as number)  - (state.bottomContainer.height as number);
        toolContentHeight =(state.toolContentHeight as number)  - (state.bottomContainer.height as number);
        console.log(3)
      }

      return {
        ...state,
        centerContentHeight:centerContentHeight,
        toolContentHeight:toolContentHeight,
        bottomContainer: {
          ...state.bottomContainer,
          selectKey: payload,
        }
      };
    },
    updateSelectBottomSubKey(state, {payload}) {
      return {
        ...state,
        bottomContainer: {
          ...state.bottomContainer,
          selectSubKey: {
            ...state.bottomContainer.selectSubKey,
            [state.bottomContainer.selectKey]: payload
          },
        }
      };
    },
    /**
     * 更新底部高度
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: Container, leftContainer: Container, bottomContainer: {selectKey: string, width: number | string, height: any, maxWidth?: number | string}}}
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
     * 更新数据库列表
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: TabsType, isFullScreen: boolean, rightContainer: Container, leftContainer: Container, bottomContainer: Container}}
     */
    saveDataBase(state, {payload}) {
      return {
        ...state,
        database: {...state.database ,dbData: payload},
      };
    },
    /**
     * 更新tabs activeKey
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSourceDataSources.DataSource[], tabs: {panes: TabsItemType[], activeKey: any}, isFullScreen: boolean, rightContainer: Container, leftContainer: Container, bottomContainer: Container}}
     */
    updateTabsActiveKey(state, {payload}) {
      const itemTypes = state.tabs.panes.filter(x=>x.key===payload);
      if (itemTypes.length===1){
        const itemType = itemTypes[0];
        return {
          ...state,
          tabs: {
            ...state.tabs,
            activeKey: payload,
            activeBreadcrumbTitle:[itemType.type,itemType.breadcrumbLabel,itemType.label].join("/")
          },
        };
      }
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
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: {panes: TabsItemType[], activeKey: number}, isFullScreen: boolean, rightContainer: Container, leftContainer: Container, bottomContainer: Container}}
     */
    closeTab(state, {payload }) {
      const needCloseKey=(payload as  TargetKey).toString();
      const {tabs:{panes,activeKey}}= state;
      if (needCloseKey===activeKey){
        for (const [index,pane] of panes.entries()) {
          if (pane.key === needCloseKey) {
            const item =index+1>=panes.length?index+1>1&&index+1 === panes.length?panes[index-1]:panes[0]: panes[index+1];
            return {
              ...state,
              tabs: {
                panes:panes.filter(pane => pane.key !== needCloseKey),
                activeKey: item.key,
                activeBreadcrumbTitle:panes.length<2?"":[item.type,item.breadcrumbLabel,item.label].join("/"),
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
          activeKey: activeKey,
          activeBreadcrumbTitle: state.tabs.activeBreadcrumbTitle,
        },
      };
    },
    /**
     * 添加tab 如果存在则不添加
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], tabs: {panes: TabsItemType[], activeKey: number}, isFullScreen: boolean, rightContainer: Container, leftContainer: Container, bottomContainer: Container}}
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
        node.key=state.tabs.panes.length === 0 ? "0" :(parseInt(state.tabs.panes[state.tabs.panes.length-1].key)+1 ).toString();
        return {
          ...state,
          tabs: {
            panes:[...state.tabs.panes,node],
            activeBreadcrumbTitle:[node.type,node.breadcrumbLabel,node.label].join("/"),
            activeKey: node.key,
          },
      }
    },

    /**
     * 关闭所有tab
     */
    closeAllTabs(state) {
        return {
            ...state,
            tabs: {
              panes:[],
              activeKey: "",
              activeBreadcrumbTitle:"",
            },
        };
    },
    /**
     * 关闭其他tab
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: {dbData: DataSources.DataSource[], selectDatabaseId: number | null, expandKeys: [], selectKey: []}, tabs: {panes: TabsItemType[], activeBreadcrumbTitle: string, activeKey: any}, isFullScreen: boolean, rightContainer: Container, leftContainer: Container, bottomContainer: Container}}
     */
    closeOtherTabs(state, {payload}) {
      // 从 pans 中找到需要关闭的 tab
      const tabsItem = state.tabs.panes.find(pane => pane.key === payload.key);
      return {
            ...state,
            tabs: {
              panes: tabsItem ? [tabsItem] : [],
              activeKey: tabsItem?.key || "",
              activeBreadcrumbTitle: tabsItem?.breadcrumbLabel || "",
            },
        };
    },

    /**
     * 更新选中数据库id
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: DataSources.DataSource[], selectDatabaseId: any, tabs: TabsType, isFullScreen: boolean, rightContainer: Container, leftContainer: Container, bottomContainer: Container}}
     */
    updateSelectDatabaseId(state, {payload}) {
        return {
            ...state,
           database: {...state.database ,selectDatabaseId : payload},
        }
    },

    /**
     * 更新数据库展开key
     * @param state
     * @param payload
     * @returns {any}
     */
    updateDatabaseExpandKey(state, {payload}) {
      return {
        ...state,
        database: {...state.database , expandKeys: payload},
      }
    },
    /**
     * 更新数据库选中key
     * @param {StateType} state
     * @param {any} payload
     * @returns {{centerContentHeight: number, toolContentHeight: number, database: {dbData: DataSources.DataSource[], selectKey: [], selectKeys: any, selectDatabaseId: number | null, expandKeys: []}, tabs: TabsType, isFullScreen: boolean, rightContainer: Container, leftContainer: Container, bottomContainer: Container}}
     */
    updateDatabaseSelectKey(state, {payload}) {
      return {
            ...state,
            database: {...state.database , selectKeys: payload},
        }
    },
    updateBottomConsole(state, {payload}) {
      return {
        ...state,
        bottomContainerContent: {...state.bottomContainerContent , console: payload},
      }
    }

  }
}
export default Model;
