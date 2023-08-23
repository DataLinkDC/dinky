// style={{color:token.colorText}}

import { theme } from 'antd';
import React from 'react';

export type TitleProps = {
  children?: React.ReactNode;
};
const { useToken } = theme;

const Title: React.FC<TitleProps> = (props) => {
  const { token } = useToken();
  return (
    <span className={'title'} style={{ color: token.colorText }}>
      {props.children}
    </span>
  );
};

export default Title;
