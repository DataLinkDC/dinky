import { Alert } from 'antd';
import React from 'react';
import Marquee from 'react-fast-marquee';

interface MetricsProps {
  tips: string | React.ReactNode;
  type: 'success' | 'info' | 'warning' | 'error';
  showIcon?: boolean;
  banner?: boolean;
  play?: boolean;
}

/**
 * The scrolling message prompt component is only used for long text, but its width may exceed the width of the container, so scrolling display is required
 * @param props
 */
export default (props: MetricsProps) => {
  const { tips, type, banner = false, showIcon = true, play = true } = props;

  const renderMarquee = () => {
    return (
      <>
        <Marquee
          style={{ alignContent: 'center' }}
          play={play}
          speed={50}
          gradient={false}
          gradientWidth={0}
        >
          {tips}
        </Marquee>
      </>
    );
  };

  return (
    <>
      <Alert
        style={{ width: '50vw' }}
        message={renderMarquee()}
        type={type}
        banner={banner}
        showIcon={showIcon}
      />
    </>
  );
};
