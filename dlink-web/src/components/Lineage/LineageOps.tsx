import React from 'react';
import {Tooltip} from 'antd';
import {
  FullscreenOutlined,
  FullscreenExitOutlined,
  VerticalAlignBottomOutlined,
  VerticalAlignTopOutlined
} from '@ant-design/icons';

const LineageOps = ({
                      isExpand,
                      isFold,
                      onAction,
                      tableId,
                    }) => [
  isExpand ?
    {
      tooltip: '收起血缘',
      action: 'shrink',
      component: <FullscreenExitOutlined/>
    }
    :
    {
      tooltip: '展开血缘',
      action: 'expand',
      component: <FullscreenOutlined/>
    },
  isFold ?
    {
      tooltip: '展开字段',
      action: 'fold',
      component: <VerticalAlignBottomOutlined/>
    }
    :
    {
      tooltip: '收起字段',
      action: 'unfold',
      component: <VerticalAlignTopOutlined/>
    }
].map(op => {
  return {
    component: (
      <Tooltip
        title={op.tooltip}
      >
        <span onClick={() => onAction(op.action, tableId)}>
          {
            op.component
          }
        </span>
      </Tooltip>
    )
  }
});

export default LineageOps;
