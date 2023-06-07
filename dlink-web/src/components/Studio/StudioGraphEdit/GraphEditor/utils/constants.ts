// node portal id
export const X6_NODE_PORTAL_NODE_VIEW = 'X6_NODE_PORTAL_NODE_VIEW'

// style classname prefix
export const XFLOW_PREFIX_CLS = 'xflow'

/** 画布缩放最小倍数 */
export const MIN_ZOOM = 0.01

/** 画布缩放最大倍数 */
export const MAX_ZOOM = 1.5

/** node render 的默认key值 */
export const XFLOW_DEFAULT_NODE = 'XFLOW_DEFAULT_NODE'

/** node render 的默认key值 */
export const XFLOW_DEFAULT_GROUP_NODE = 'XFLOW_DEFAULT_GROUP_NODE'

/** edge render 的默认key值 */
export const XFLOW_DEFAULT_EDGE = 'XFLOW_DEFAULT_EDGE'

/** 扩展事件:事件参数 */
export interface XFlowEventRecord {
  TOGGLE_MULTI_SELECT: {
    isEnable: boolean
  }
}

/** 事件名: 扩展事件 */
export type XFlowEventName = keyof XFlowEventRecord

/** localStorage key */
export const LOCAL_STORAGE_KEY = 'XFLOW_COPY_ITEMS'

/** node height */
export const DEFAULT_NODE_HEIGHT = 50

/** node width */
export const DEFAULT_NODE_WIDTH = 150

/** default group size */
export const XFLOW_GROUP_DEFAULT_COLLAPSED_SIZE = {
  width: DEFAULT_NODE_WIDTH,
  height: DEFAULT_NODE_HEIGHT,
}
