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


import { useRef } from 'react';
import { off, on } from '../utils/dom';
import useMount from './useMount'

/**
 * @hook useDraggable
 * @desc 使得DOM变得可拖拽
 * @at 2020/09/22
 * @by lmh
 * */

// 拖拽的初始位置
const initPosition = { x: 0, y: 0 };
// 默认的配置，默认允许拖拽元素溢出容器
const defaultOptions = { overbound: true };
// 当前触发mouseDown的元素，由于mouseUp 绑定在window
// eventCallback 会触发到其他使用  useDraggable 的元素
let currentTarget = null;

const useDraggable = (
  container, // 容器，可以是ref.current|dom| 拖拽元素的父元素（默认）
  { onMouseDown, onMouseUp, onMouseMove }, // callback
  { overbound } = defaultOptions, // 是否支持拖拽溢出容器，默认是允许，
) => {
  const isDragging = useRef(null);
  const ref = useRef(null);

  useMount(() => {
    const mouseMove = e => {
      if (ref.current === currentTarget) {
        if (isDragging.current) {
          if (onMouseMove) {
            // ref.current | dom | 拖拽元素的父元素
            const roots =
              container?.current ?? container ?? ref.current.parentNode;

            let x = e.clientX - initPosition.x;
            let y = e.clientY - initPosition.y;

            // 是否允许 拖拽位置脱离边界
            if (!overbound) {
              if (x < 0) x = 0;
              if (y < 0) y = 0;
              const { clientWidth: pWidth, clientHeight: pHeight } = roots;
              const {
                clientWidth: cWidth,
                clientHeight: cHeight,
              } = ref.current;
              if (x + cWidth > pWidth) x = pWidth - cWidth;
              if (y + cHeight > pHeight) y = pHeight - cHeight;
            }
            onMouseMove({ x, y });
          }
        }
      }
    };
    const mouseUp = e => {
      if (ref.current === currentTarget) {
        isDragging.current = false;
        if (onMouseUp) onMouseUp(e);
      }
    };

    on(window, 'mousemove', mouseMove);
    on(window, 'mouseup', mouseUp);
    return () => {
      off(window, 'mousemove', mouseMove);
      off(window, 'mouseup', mouseUp);
    };
  });

  const props = {
    ref,
    onMouseDown: e => {
      isDragging.current = true;
      const target = e.target || e.srcElement;
      // 缓存此次触发事件的元素
      currentTarget = target;

      // offsetLeft 返回与最近定位的元素（或者body）的左边缘距离
      // clientX 鼠标事件点击的x轴位置
      initPosition.x = e.clientX - target.offsetLeft;
      initPosition.y = e.clientY - target.offsetTop;

      if (onMouseDown) onMouseDown(e);
    },
  };

  return [props, isDragging];
};

export default useDraggable;
