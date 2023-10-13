import { Button } from 'antd';
import { ButtonProps } from 'antd/es/button/button';
import React, { useState } from 'react';

export const LoadingBtn: React.FC<ButtonProps> = (props) => {
  const [loading, setLoading] = useState(false);

  const handleClick = async (event: React.MouseEvent<HTMLElement, MouseEvent>) => {
    if (props.onClick) {
      setLoading(true);
      await props.onClick(event);
      setLoading(false);
    }
  };

  return <Button {...props} loading={loading} onClick={(event) => handleClick(event)}></Button>;
};
