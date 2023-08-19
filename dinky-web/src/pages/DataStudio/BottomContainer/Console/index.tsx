import CodeShow from '@/components/CustomEditor/CodeShow';
import { StateType } from '@/pages/DataStudio/model';
import { connect } from '@@/exports';
import React, { useEffect } from 'react';

const Console: React.FC = (props: any) => {
  useEffect(() => {}, []);

  return (
    <CodeShow code={props.console} height={props.height - 53} language={'kotlin'} lineNumbers={'off'} showFloatButton />
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  height: Studio.bottomContainer.height,
  console: Studio.bottomContainerContent.console
}))(Console);
