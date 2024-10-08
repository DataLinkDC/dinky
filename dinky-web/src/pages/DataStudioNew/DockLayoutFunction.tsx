import {BoxData} from "rc-dock/es";
import {LayoutState} from "@/pages/DataStudioNew/model";
import {ToolbarPosition, ToolbarRoute} from "@/pages/DataStudioNew/Toolbar/data.d";

export const createNewPanel = (state: LayoutState, route: ToolbarRoute) => {
  const boxData: BoxData = {
    mode: 'vertical',
    size: 600,
    children: [
      {
        tabs: [
          {
            id: route.key,
            content: route.content(),
            title: route.title,
            group: route.position
          }
        ]
      }
    ]
  }
  if (route.position == "right") {
    (state.layoutData.dockbox.children[0] as BoxData).children.push(boxData)
  } else if (route.position === 'leftTop') {
    if ((state.layoutData.dockbox.children[0] as BoxData).children) {
      (state.layoutData.dockbox.children[0] as BoxData).children = [boxData, ...(state.layoutData.dockbox.children[0] as BoxData).children]
    } else {
      (state.layoutData.dockbox.children as BoxData[]) = [boxData, ...(state.layoutData.dockbox.children as BoxData[])]
    }
  }
}

export const findToolbarPositionByTabId = (toolbar: LayoutState['toolbar'], tabId: string): ToolbarPosition | undefined => {
  if (toolbar.leftTop.allOpenTabs.includes(tabId)) {
    return 'leftTop'
  } else if (toolbar.leftBottom.allOpenTabs.includes(tabId)) {
    return 'leftBottom'
  } else if (toolbar.right.allOpenTabs.includes(tabId)) {
    return 'right'
  }
  return undefined
}
