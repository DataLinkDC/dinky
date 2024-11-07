/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { BoxData } from 'rc-dock/es';
import { DataStudioState } from '@/pages/DataStudioNew/model';
import { ToolbarPosition, ToolbarRoute } from '@/pages/DataStudioNew/Toolbar/data.d';
import { PanelData, TabData } from 'rc-dock/es/DockData';
import { DockLayout, LayoutData } from 'rc-dock';
import { Filter } from 'rc-dock/es/Algorithm';

export const activeTab = (
  dockLayout: DockLayout,
  layoutData: LayoutData,
  sourceTabData: TabData,
  targetId: string
) => {
  const tabPanel = find(layoutData, sourceTabData.id!!) as PanelData;
  const targetTabPanel = find(layoutData, targetId) as PanelData;
  if (!tabPanel && targetTabPanel) {
    // 新增tab
    targetTabPanel.tabs = [sourceTabData, ...targetTabPanel.tabs];
    targetTabPanel.activeId = sourceTabData.id;
  } else {
    // 切换tab
    if (tabPanel.activeId === sourceTabData.id) {
      dockLayout.loadLayout(layoutData);
      return;
    }
    if (tabPanel.tabs.length === 0) {
      tabPanel.tabs = [sourceTabData];
    } else {
      if (tabPanel.tabs.some((tab) => tab.id === sourceTabData.id)) {
        tabPanel.activeId = sourceTabData.id;
      }
    }
  }

  dockLayout.loadLayout(layoutData);
};

export const createNewPanel = (
  layoutData: LayoutData,
  route: ToolbarRoute,
  size?: number
): LayoutData => {
  // todo 这里有布局混乱导致算法崩溃风险
  const panelData: PanelData = {
    group: route.position,
    size,
    tabs: [
      {
        id: route.key,
        content: <></>,
        title: route.title,
        group: route.position
      }
    ]
  };
  const boxData: BoxData = {
    mode: 'vertical',
    size: size ?? 1000,
    children: [panelData]
  };

  const dockbox = layoutData.dockbox;
  if (dockbox.mode === 'horizontal') {
    if (route.position == 'right') {
      (dockbox.children as BoxData[]).push(boxData);
    } else if (route.position === 'leftTop') {
      dockbox.children = [boxData, ...dockbox.children];
    } else if (route.position === 'leftBottom') {
      return {
        ...layoutData,
        dockbox: {
          mode: 'vertical',
          children: [
            {
              mode: 'horizontal',
              children: [...dockbox.children]
            },
            boxData
          ]
        }
      };
    } else if (route.position === 'centerContent') {
      if (dockbox.children.length === 0) {
        dockbox.children = [...dockbox.children, boxData];
      } else {
        if ((dockbox.children[0] as PanelData).group === 'leftTop') {
          dockbox.children = [dockbox.children[0], boxData, ...dockbox.children.slice(1)];
        } else if ((dockbox.children[0] as PanelData).group === 'right') {
          dockbox.children = [boxData, ...dockbox.children];
        }
      }
    }
  } else if (dockbox.mode === 'vertical') {
    if (dockbox.children.length === 0) {
      dockbox.children.push(boxData);
    } else {
      if (route.position === 'leftBottom') {
        dockbox.children.push(boxData);
      } else {
        for (let i = 0; i < dockbox.children.length; i++) {
          if ((dockbox.children[i] as PanelData).group !== 'leftBottom') {
            if (route.position === 'leftTop') {
              if ('tabs' in dockbox.children[i]) {
                // panel
                dockbox.children[i] = {
                  mode: 'horizontal',
                  children: [boxData, dockbox.children[i] as PanelData]
                };
              } else {
                // box
                (dockbox.children[i] as BoxData).children = [
                  boxData,
                  ...(dockbox.children[i] as BoxData).children
                ];
              }
            } else if (route.position === 'right') {
              if ('tabs' in dockbox.children[i]) {
                // panel
                dockbox.children[i] = {
                  mode: 'horizontal',
                  children: [dockbox.children[i] as PanelData, boxData]
                };
              } else {
                // box
                (dockbox.children[i] as BoxData).children.push(boxData);
              }
            } else if (route.position === 'centerContent') {
              if ('tabs' in dockbox.children[i]) {
                // panel
                if ((dockbox.children[i] as PanelData).group === 'leftTop') {
                  dockbox.children[i] = [dockbox.children[i], panelData];
                } else if ((dockbox.children[i] as PanelData).group === 'right') {
                  dockbox.children[i] = [panelData, dockbox.children[i]];
                }
                dockbox.children[i] = {
                  mode: 'horizontal',
                  children: [...(dockbox.children[i] as PanelData[])]
                };
              } else {
                if ((dockbox.children[i].children[0] as PanelData).group === 'leftTop') {
                  (dockbox.children[i] as BoxData).children = [
                    dockbox.children[i].children[0],
                    panelData,
                    ...dockbox.children[i].children.slice(1)
                  ];
                }
              }
            }
            break;
          }
        }
      }
    }
  }
  return layoutData;
};

export const findToolbarPositionByTabId = (
  toolbar: DataStudioState['toolbar'],
  tabId: string
): ToolbarPosition | undefined => {
  if (toolbar.leftTop.allOpenTabs.includes(tabId)) {
    return 'leftTop';
  } else if (toolbar.leftBottom.allOpenTabs.includes(tabId)) {
    return 'leftBottom';
  } else if (toolbar.right.allOpenTabs.includes(tabId)) {
    return 'right';
  }
  return undefined;
};

export function find(
  layout: LayoutData,
  id: string,
  filter: Filter = Filter.AnyTabPanel
): PanelData | TabData | BoxData | undefined {
  let result: PanelData | TabData | BoxData | undefined;

  if (filter & Filter.Docked) {
    result = findInBox(layout.dockbox, id, filter);
  }
  if (result) return result;

  if (filter & Filter.Floated) {
    result = findInBox(layout.floatbox, id, filter);
  }
  if (result) return result;

  if (filter & Filter.Windowed) {
    result = findInBox(layout.windowbox, id, filter);
  }
  if (result) return result;

  if (filter & Filter.Max) {
    result = findInBox(layout.maxbox, id, filter);
  }

  return result;
}

function findInBox(
  box: BoxData | undefined,
  id: string,
  filter: Filter
): PanelData | TabData | BoxData | undefined {
  let result: PanelData | TabData | BoxData | undefined;
  if (filter | Filter.Box && box?.id === id) {
    return box;
  }
  if (!box?.children) {
    return undefined;
  }
  for (let child of box.children) {
    if ('children' in child) {
      if ((result = findInBox(child, id, filter))) {
        break;
      }
    } else if ('tabs' in child) {
      if ((result = findInPanel(child, id, filter))) {
        break;
      }
    }
  }
  return result;
}

function findInPanel(
  panel: PanelData,
  id: string,
  filter: Filter
): PanelData | TabData | undefined {
  if (panel.id === id && filter & Filter.Panel) {
    return panel;
  }
  if (filter & Filter.Tab) {
    for (let tab of panel.tabs) {
      if (tab.id === id) {
        return panel;
      }
    }
  }
  return undefined;
}
