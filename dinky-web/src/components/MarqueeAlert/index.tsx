/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
