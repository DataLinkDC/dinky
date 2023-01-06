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


import React, {useRef, useState} from 'react';
import useDraggable from '../../hooks/useDraggable';
import styles from './DraggleVerticalLayout.less';
import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";

function DraggleVerticalLayout({
   children, // 两行布局
   min = 100, // 顶部最小高度
   max = Infinity, // 底部最大高度
   containerWidth = 0, // 容器宽度
   containerHeight = 0, // 容器高度
   initTopHeight = 0, // 初始顶部容器高度
   handler = null, // 拖拽器
   onHeightChange = height => height, // 左侧容器高度变化
   toolHeight,
   dispatch,
 }) {
  const ref = useRef(null);

  const [position, setPosition] = useState({ x: 0, y: toolHeight });

  const [props] = useDraggable(
    ref,
    {
      onMouseMove: ({ x, y }) => {
        let _y = y;
        if (_y < min) _y = min;
        if (_y > max) _y = max;
        if (onHeightChange) onHeightChange(_y);
        setPosition({ x, y:_y });
        dispatch&&dispatch({
          type: "Studio/saveToolHeight",
          payload: _y,
        });
      },
    },
    { overbound: false },
  );
  const _handler = handler ? (
    React.cloneElement(handler, {
      ...handler.props,
      style: {
        ...handler.props.style,
        pointerEvents: 'none',
      },
    })
  ) : (
    <span style={{ fontSize: 18, pointerEvents: 'none' }}>》</span>
  );

  return (
    <div
      ref={ref}
      className={styles.root}
      style={{ width: containerWidth, height: containerHeight }}
    >
      <div className={styles.top} style={{ height: position.y }}>
        {children[0]}

        <div className={styles.handler} {...props}>
          {_handler}
        </div>
      </div>
      <div
        className={styles.bottom}
        style={{ height: containerHeight - position.y }}
      >
        {children[1]}
      </div>
    </div>
  );
}

export default connect(({Studio}: { Studio: StateType }) => ({
  toolHeight: Studio.toolHeight,
}))(DraggleVerticalLayout);
