import {BoxData} from "rc-dock/es";
import {LayoutState} from "@/pages/DataStudioNew/model";
import {ToolbarPosition, ToolbarRoute} from "@/pages/DataStudioNew/Toolbar/data.d";
import {PanelData} from "rc-dock/lib/DockData";
import {LayoutData} from "rc-dock";

export const createNewPanel = (layoutData: LayoutData, route: ToolbarRoute):LayoutData => {
  // todo 这里有布局混乱导致算法崩溃风险
  const boxData: BoxData = {
    mode: 'vertical',
    size: 600,
    children: [
      {
        tabs: [
          {
            id: route.key,
            content: <></>,
            title: route.title,
            group: route.position
          }
        ]
      }
    ]
  }

  const dockbox = layoutData.dockbox;
  if (dockbox.mode === "horizontal") {
    if (route.position == "right") {
      (dockbox.children as BoxData[]).push(boxData)
    } else if (route.position === 'leftTop') {
      dockbox.children = [boxData, ...dockbox.children]
    } else if (route.position === 'leftBottom') {
      return  {
        ...layoutData,
        dockbox: {
          mode: 'vertical',
          children: [{
            mode: 'horizontal',
            children: [...dockbox.children]
          }, boxData]
        }
      }
    }

  } else if (dockbox.mode === "vertical") {
    if (dockbox.children.length === 0) {
      dockbox.children.push(boxData)
    } else {
      if (route.position === 'leftBottom') {
        dockbox.children.push(boxData)
      } else {
        for (let i = 0; i < dockbox.children.length; i++) {
          if ((dockbox.children[i] as PanelData).group !== 'leftBottom') {
            if (route.position === 'leftTop') {
              if ('tabs' in dockbox.children[i]) {
                // panel
                dockbox.children[i] = {
                  mode: 'horizontal',
                  children: [boxData, dockbox.children[i] as PanelData]
                }
              } else {
                // box
                (dockbox.children[i] as BoxData).children = [boxData, ...(dockbox.children[i] as BoxData).children]
              }
            } else if (route.position === 'right') {
              if ('tabs' in dockbox.children[i]) {
                // panel
                dockbox.children[i] = {
                  mode: 'horizontal',
                  children: [dockbox.children[i] as PanelData, boxData]
                }
              } else {
                // box
                (dockbox.children[i] as BoxData).children.push(boxData)
              }
            }
            break
          }
        }
      }
    }

  }
  return  layoutData

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
