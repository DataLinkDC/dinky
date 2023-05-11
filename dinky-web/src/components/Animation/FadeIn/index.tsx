/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {useSpring, animated} from "react-spring";

const FadeIn = (props: any) => {
  const {children} = props;
  const fadeIn = useSpring({
    opacity: 1,
    from: {opacity: 0},
    config: {
      duration: 1000,
      delay: 1000,
    },
  });

  return <animated.div style={fadeIn}>{children}</animated.div>;
};

export default FadeIn;
