/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import { useSpring, animated } from 'react-spring';

//渐变背景颜色效果：
const GradientBackground =(props:any) => {
    const {children} = props;
    const style = useSpring({
        from: { background: 'linear-gradient(45deg, #ff0000, #00ff00)' },
        to: { background: 'linear-gradient(45deg, #00ff00, #0000ff)' },
        config: { duration: 2000 },
    });

    return <animated.div style={style}>{children}</animated.div>;
};

export default GradientBackground