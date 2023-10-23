import ConsoleContent from '@/pages/DataStudio/BottomContainer/Console/ConsoleContent';
import { StateType, TabsItemType } from '@/pages/DataStudio/model';
import { connect } from '@@/exports';
import { Tabs } from 'antd';
import React, { useEffect } from 'react';

const Console: React.FC = (props: any) => {
  const {
    tabs: { panes, activeKey }
  } = props;
  useEffect(() => {}, []);

  const tabItems = panes.map((item: TabsItemType) => ({
    key: item.key,
    label: <span style={{ paddingLeft: '5px' }}>{item.label}</span>,
    children: <ConsoleContent tab={item} />
  }));

  return (
    <Tabs activeKey={activeKey} size={'small'} items={tabItems} tabBarStyle={{ display: 'none' }} />
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs,
  height: Studio.bottomContainer.height,
  console: Studio.bottomContainerContent.console
}))(Console);
