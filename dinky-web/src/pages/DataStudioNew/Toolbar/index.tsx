import { Flex, Row, Typography } from 'antd';
import React from 'react';
import './index.less';
import { ToolbarProp } from '@/pages/DataStudioNew/Toolbar/data.d';

export default (props: ToolbarProp) => {
  const { showDesc, showActiveTab, route, onClick, toolbarSelect } = props;
  return (
    <Flex wrap gap={4} justify={'center'} className={'toolbar-side'}>
      {route.map((item) => {
        // 设置显示样式
        let className = 'toolbar-icon-container';
        if (toolbarSelect?.currentSelect === item.key) {
          className += ' toolbar-icon-container-select';
        } else if (toolbarSelect?.allTabs?.has(item.key) && showActiveTab) {
          className += ' toolbar-icon-container-open';
        }
        return (
          <Row
            className={className}
            align={'middle'}
            justify={'center'}
            key={item.key}
            onClick={() => {
              onClick(item);
            }}
          >
            <span
              style={{
                width: '100%',
                textAlign: 'center'
              }}
            >
              {React.cloneElement(item.icon, { className: 'toolbar-icon' })}
            </span>
            {showDesc && <span className={'toolbar-desc'}>{item.title}</span>}
          </Row>
        );
      })}
    </Flex>
  );
};
