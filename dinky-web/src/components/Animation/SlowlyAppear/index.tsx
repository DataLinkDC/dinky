
import { animated, useSpring } from 'react-spring';

// 缓慢出现
const SlowlyAppear = (props: any) => {
  const { children } = props;
  const slowlyAppearProps = useSpring({
    opacity: 1,
    from: { opacity: 0 },
    config: { duration: 100 }
  });

  return <animated.h1 style={slowlyAppearProps}>{children}</animated.h1>;
};

export default SlowlyAppear;
