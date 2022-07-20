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
import styles from './DraggleLayout.less';
import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";

function DraggleLayout({
                         children, // 两列布局
                         min = 400, // 左侧最小宽度
                         max = Infinity, // 左侧最大宽度
                         containerWidth = 0, // 容器宽度
                         containerHeight = 0, // 容器高度
                         initLeftWidth = 0, // 初始左侧容器宽度
                         handler = null, // 拖拽器
                         isLeft = true, // 拖拽器
                         onWidthChange = width => width, // 左侧容器高度变化
                         dispatch,
                       }) {
  const ref = useRef(null);

  const [position, setPosition] = useState({x: initLeftWidth, y: 0});

  const [props] = useDraggable(
    ref,
    {
      onMouseMove: ({x, y}) => {
        let _x = x;
        if (_x < min) _x = min;
        if (_x > max) _x = max;
        if (onWidthChange) onWidthChange(_x);
        setPosition({x: _x, y});
        if (isLeft) {
          dispatch && dispatch({
            type: "Studio/saveToolLeftWidth",
            payload: _x,
          });
        } else {
          dispatch && dispatch({
            type: "Studio/saveToolRightWidth",
            payload: (containerWidth - _x),
          });
        }
      },
    },
    {overbound: false},
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
    <span style={{fontSize: 18, pointerEvents: 'none'}}>》</span>
  );

  return (
    <div
      ref={ref}
      className={styles.root}
      style={{width: containerWidth, height: containerHeight}}
    >
      <div className={styles.left} style={{width: position.x}}>
        {children[0]}

        <div className={styles.handler} {...props}>
          {_handler}
        </div>
      </div>
      <div
        className={styles.right}
        style={{width: containerWidth - position.x}}
      >
        {children[1]}
      </div>
    </div>
  );
}

export default connect(({Studio}: { Studio: StateType }) => ({
}))(DraggleLayout);
