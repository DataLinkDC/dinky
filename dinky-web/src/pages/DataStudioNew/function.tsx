import { RightContextMenuState } from '@/pages/DataStudioNew/data.d';
import { Dispatch, SetStateAction } from 'react';
import { ContextMenuPosition } from '@/types/Public/state';
import { DropDirection } from 'rc-dock';
import { ToolbarPosition } from '@/pages/DataStudioNew/Toolbar/data.d';

export const handleRightClick = (
  e: any,
  stateAction: Dispatch<SetStateAction<RightContextMenuState>>
) => {
  let x = e.clientX;
  let y = e.clientY;
  // 判断右键的位置是否超出屏幕 , 如果超出屏幕则设置为屏幕的最大值
  if (x + 150 > window.innerWidth) {
    x = window.innerWidth - 160; // 160 是右键菜单的宽度
  }
  if (y + 200 > window.innerHeight) {
    y = window.innerHeight - 210; // 210 是右键菜单的高度
  }
  stateAction((prevState) => {
    return {
      ...prevState,
      show: true,
      position: {
        top: y + 5,
        left: x + 10
      }
    } as RightContextMenuState;
  });

  e.preventDefault(); // 阻止浏览器默认的右键行为
};

export const InitContextMenuPosition: ContextMenuPosition = {
  left: 0,
  top: 0,
  position: 'fixed',
  cursor: 'pointer',
  width: '12vw',
  zIndex: 1000
};

// 根据工具栏位置获取停靠位置
export const getDockPositionByToolbarPosition = (position: ToolbarPosition): DropDirection => {
  switch (position) {
    case 'leftTop':
      return 'left';
    case 'leftBottom':
      return 'bottom';
    case 'right':
      return 'right';
  }
};
