import { WaterMark } from '@ant-design/pro-components';
import React from 'react';

interface WaterMarkExtProps {
  children: React.ReactNode;
  hiddenWaterMark?: boolean;
  waterMarkContent?: string[] | string;
  contentColor?: string;
}

const WaterMarkExt = (props: WaterMarkExtProps) => {
  const { children, hiddenWaterMark, waterMarkContent = [] || '', contentColor } = props;

  return (
    <>
      {hiddenWaterMark ? (
        <>{children}</>
      ) : (
        <>
          <WaterMark
            fontColor={contentColor}
            fontStyle={'oblique'}
            fontSize={18}
            content={waterMarkContent}
          >
            {children}
          </WaterMark>
        </>
      )}
    </>
  );
};

export default WaterMarkExt;
