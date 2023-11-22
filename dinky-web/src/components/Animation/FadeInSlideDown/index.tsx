
import { animated, useSpring } from 'react-spring';

const FadeInSlideDown = (props: any) => {
  const { children } = props;
  const style = useSpring({
    from: { opacity: 0, transform: 'translateY(-100px)' },
    to: { opacity: 1, transform: 'translateY(0)' },
    config: { duration: 100 }
  });

  return <animated.div style={style}>{children}</animated.div>;
};

export default FadeInSlideDown;
