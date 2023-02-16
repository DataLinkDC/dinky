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

import {l} from '@/utils/intl';
import {FullscreenExitOutlined, FullscreenOutlined} from '@ant-design/icons';
import {Tooltip} from 'antd';
import React from 'react';

class FullScreen extends React.Component {
  state = {
    isFullScreen: true,
  };

  //Fullscreen event
  fullScreen = () => {
    let isFullScreen = document.webkitIsFullScreen;
    if (!isFullScreen) {
      this.requestFullScreen();
    } else {
      this.exitFullscreen();
    }
    this.setState({isFullScreen: isFullScreen});
  };
  //enter Fullscreen
  requestFullScreen = () => {
    const de = document.documentElement;
    if (de.requestFullscreen) {
      de.requestFullscreen();
    } else if (de.mozRequestFullScreen) {
      de.mozRequestFullScreen();
    } else if (de.webkitRequestFullScreen) {
      de.webkitRequestFullScreen();
    } else if (de.msRequestFullscreen) {
      de.webkitRequestFullScreen();
    }
  };
  //exit Fullscreen
  exitFullscreen = () => {
    const de = document;
    if (de.exitFullScreen) {
      de.exitFullScreen();
    } else if (de.mozExitFullScreen) {
      de.mozExitFullScreen();
    } else if (de.webkitExitFullscreen) {
      de.webkitExitFullscreen();
    } else if (de.msExitFullscreen) {
      de.msExitFullscreen();
    }
  };
  render() {
    return (<div className={'fullscreen'}>
        <Tooltip
          placement="bottom"
          title={<span>{this.state.isFullScreen ? l('global.fullScreen') : l('global.fullScreen.exit')}</span>}
        >
          {React.createElement(
            this.state.isFullScreen ? FullscreenOutlined : FullscreenExitOutlined,
            {
              onClick: this.fullScreen,
            },
          )}
        </Tooltip>
      </div>
    );
  }
}

export default FullScreen;
