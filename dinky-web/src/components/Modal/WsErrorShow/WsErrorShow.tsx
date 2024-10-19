import { Button, Result } from 'antd';
import { WsState } from '@/models/UseWebSocketModel';
import * as React from 'react';
import { l } from '@/utils/intl';

const WsErrorShow = (props: { state: WsState; extra?: React.ReactNode }) => {
  const { state, extra } = props;

  return (
    <Result status='error' title={l('global.ws.failed')} subTitle={state.wsUrl} extra={extra} />
  );
};

export default WsErrorShow;
