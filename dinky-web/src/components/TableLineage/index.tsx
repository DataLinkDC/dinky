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

import {Slider, Tooltip, Typography, Badge, Flex, Input, Tag,} from 'antd';
import {useCallback, useEffect, useRef, useState} from 'react';
import * as G6 from '@antv/g6';
import {GNode, Group, Image, ReactNode} from '@antv/g6-extension-react';
import {getLocalTheme} from "@/utils/function";

import {THEME} from "@/types/Public/data";
import * as G6Options from "@antv/g6/src/spec";
import {ErrorMessage, SuccessMessage, WarningMessage} from "@/utils/messages";
import {Loading} from "@/pages/Other/Loading";
import './index.css'
import {LineageDetailInfo} from "@/types/DevOps/data";
import {ComboData, GraphData} from "@antv/g6/src/spec/data";
import {EdgeData, NodeData} from "@antv/g6";
import {DatabaseFilled} from "@ant-design/icons";

const STEP = 0.25;
const MAX_ZOOM = 1;
const MIN_ZOOM = 0.25;

interface TableLineageProps extends G6Options.GraphOptions {
  dataRefreshCallback?: () => void;
  refreshLoading: boolean;
};

const {Text} = Typography;

const TableLinageNode = ({data, onChange}) => {

  console.log('TableLinageNode data: ', data);
  const {id, label, fullDbName, column} = data;

  return (
    <Flex
      style={{
        width: '100%',
        height: '100%',
        // background: '#fff',
        padding: 10,
        borderRadius: 5,
        border: '1px solid gray',
      }}
      vertical
    >
      <Flex align="center" justify="space-between">
        <Text>
          <DatabaseFilled/>
          Name:
          <Tag>{label}</Tag>
        </Text>
      </Flex>
      <Text type="secondary">{id}</Text>
      <Flex align="center">
        <Text style={{flexShrink: 0}}>
          <Text type="danger">*</Text>Label:
          <Tag>{label}</Tag>
        </Text>
      </Flex>
    </Flex>
  );
};

G6.register(G6.ExtensionCategory.NODE, 'react', ReactNode);


const TableLineage = (props: TableLineageProps) => {
  const graphRef = useRef(null);
  const [zoom, setZoom] = useState(MIN_ZOOM);
  const [toolbarId, setTollbarId] = useState({
    id: '',
    count: 0,// 由于点击每次并不会重新执行，所以需要一个变量来判断是否重新执行
  });

  /**
   * * 初始化图
   */
  const initGraph = (): G6.Graph => {
    if (!graphRef.current) return;

    const graph = new G6.Graph({
      container: graphRef.current,
      width: innerWidth - 20,
      autoFit: 'center',
      height: innerHeight - 70,
      // background: '#babcbd',
      zoom: zoom, // 缩放比例
      zoomRange: [0.25, 1], // 缩放范围
      plugins: [...pluginsConfig], // 插件
      // todo: 'lasso-select' 不能和 'drag-canvas' 一起使用, 会有冲突
      behaviors: ['drag-canvas', 'zoom-canvas', 'drag-element', 'focus-element', 'collapse-expand',
        { type: 'click-select', multiple: false, trigger: ['shift'] },
        {
        type: 'hover-activate',
        enable: (event) => event.targetType === 'node',
        degree: 1, // 👈🏻 Activate relations.
        state: 'highlight',
        inactiveState: 'dim',
        onHover: (event) => {
          event.view.setCursor('pointer');
        },
        onHoverEnd: (event) => {
          event.view.setCursor('default');
        },
      },], // 交互行为
      layout: {
        type: 'antv-dagre',
        rankdir: 'LR',
        nodesep: 100,
        ranksep: 70,
        controlPoints: true,
      },
      combo: {
        type: 'rect',
        style: {
          padding: 20,

        },
      },
      node: {
        type: 'react',
        style: {
          size: [240, 100],
          component: (data) => <TableLinageNode data={data}/>,
          // fill: '#e70606',
          // stroke: '#1060d9',
          // lineWidth: 1,
          labelPlacement: 'center',
          port: true,
          ports: [{placement: 'left'}, {placement: 'right'}],
        },
        state: {
          highlight: {
            fill: '#D580FF',
            halo: true,
            lineWidth: 0,
          },
          dim: {
            fill: '#99ADD1',
          },
        },
      },
      edge: {
        type: 'cubic-horizontal',
        state: {
          highlight: {
            stroke: '#D580FF',
          },
        },
        style: {
          labelPlacement: 'center',
          lineDash: [10, 10],
          endArrow: true,
          endArrowType: 'triangleRect',
          // halo: true,
          badge: true,
          // loop: true,
          curveOffset: 25,
          curvePosition: 0.2,
          stroke: '#7e3feb',
          lineWidth: 4,
          labelText: (d) => d.relationship,
          labelBackground: true,
          // labelBackgroundFill: '#0a0a0a',
          labelBackgroundOpacity: 1,
          labelBackgroundLineWidth: 2,
          labelBackgroundStroke: '#7e3feb',
          labelPadding: [15, 10],
          labelBackgroundRadius: 4,
          labelWordWrap: true,
          labelMaxWidth: '40%',

        },
      },
      theme: getLocalTheme() === THEME.light ? THEME.light : THEME.darkShort,
      ...props,
    });
    return graph;
  };

  // 调用了 initGraph() 初始化 graphInstance 必须在 useEffect 中执行
  const [graphInstance, setGraphInstance] = useState<G6.Graph>(initGraph());


  /** 对应插件的点击事件
   *          { id: 'zoom-in', value: 'zoom-in' },
   *           { id: 'zoom-out', value: 'zoom-out' },
   *           { id: 'redo', value: 'redo' }, // 暂不实现
   *           { id: 'undo', value: 'undo' }, // 暂不实现
   *           { id: 'delete', value: 'delete' }, // 暂不实现
   *           { id: 'auto-fit', value: 'auto-fit' },
   *           { id: 'reset', value: 'reset' },
   * @param value 点击的插件id
   * @param target 插件的dom元素
   */
  useEffect(() => {
    if (!graphInstance) return;
    switch (toolbarId.id) {
      case 'zoom-in': // 放大
        // 如果大于 1 或者小于 0 则不再放大了
        if (zoom >= MAX_ZOOM) {
          WarningMessage("已经放大到最大比例了, 不能再放大了!", 2);
          new Promise((resolve) => setZoom(prevState => MAX_ZOOM)).then(() => resolve());
          graphInstance?.zoomTo?.(zoom, true);
        } else {
          new Promise((resolve) => setZoom(prevState => prevState + STEP)).then(() => resolve());
          graphInstance?.zoomTo?.(zoom + STEP, true);
        }
        break;
      case 'zoom-out': // 缩小
        // 如果小于 0.25 则不再缩小了, 否则继续缩小
        if (zoom <= MIN_ZOOM) {
          WarningMessage("已经缩小到最小比例了, 不能再缩小了!", 2);
          new Promise((resolve) => setZoom(prevState => MIN_ZOOM)).then(() => resolve());
          graphInstance?.zoomTo?.(MIN_ZOOM, true);
        } else {
          new Promise((resolve) => setZoom(prevState => prevState - STEP)).then(() => resolve());
          graphInstance?.zoomTo?.(zoom - STEP, true);
        }
        break;
      case 'auto-fit': // 自动适配
        SuccessMessage('根据当前比例自动居中...', 2);
        graphInstance?.render?.();
        graphInstance?.fitView?.({
          direction: 'both',
          when: 'always'
        }, true)
        break;
      case 'reset': // 重置  虽然名字是 reset, 但当前场景 写为刷新
        if (!props.dataRefreshCallback) {
          ErrorMessage('当前没有设置刷新数据的回调函数, 无法刷新数据!', 3);
          return;
        } else {
          SuccessMessage('正在刷新数据...', 2);
          graphInstance?.clear?.();
          props.dataRefreshCallback();
        }
        break;
    }
  }, [toolbarId])

// [ 'tooltip', 'minimap', 'edge-tooltip', 'drag-node', 'brush-select', 'collapse-expand', 'edge-filter','timebar'],
  const pluginsConfig = [
    {key: 'grid-line', type: 'grid-line', follow: false},
    // 提示插件
    {
      key: 'tooltip',
      type: 'tooltip',
      trigger: 'hover',
      enable: 'always',
      position: 'top-left',
      enterable: false,
    },
    // 缩略图插件
    {
      key: 'minimap',
      type: 'minimap',
      //  'left' | 'right' | 'top' | 'bottom' | 'left-top' | 'left-bottom' |
      //  'right-top' | 'right-bottom' | 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right' | 'center' Default: 'right-bottom'
      position: 'bottom-left',
      size: [200, 150],
    },
    // 工具栏插件
    {
      key: 'toolbar',
      type: 'toolbar',
      // 'left-top' | 'left-bottom' | 'right-top' | 'right-bottom' | 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right' Default: 'top-left'
      position: 'left-top',
      onClick: (value, target) => {
        // 如果当次 点击的插件id 和 上次点击的插件id 相同，则初始化 count 置为0
        if (value === toolbarId.id && toolbarId.count > 0) {
          setTollbarId(prevState => ({id: value, count: 0}));
        }
        setTollbarId(prevState => ({id: value, count: prevState.count + 1}));
      },
      className: 'table-lineage-toolbar',
      // 渲染菜单项
      getItems: () => {
        return [
          {id: 'zoom-in', value: 'zoom-in'}, // 放大
          {id: 'zoom-out', value: 'zoom-out'}, // 缩小
          {id: 'auto-fit', value: 'auto-fit'}, // 自适应
          {id: 'reset', value: 'reset'}, // 刷新
          // { id: 'redo', value: 'redo' },
          // { id: 'undo', value: 'undo' },
          // { id: 'delete', value: 'delete' },
          // { id: 'edit', value: 'edit' },
          // { id: 'export', value: 'export' },
        ];
      }
    },
    {
      key: 'grid-line',
      type: 'grid-line',
      size: 20, // 单个网格的大小
      border: true, // 是否显示边框
      borderLineWidth: 1, // 边框宽度
      // borderStroke: '#e70606', // 边框颜色
      // borderStyle: 'rgba(0, 255, 136, 0.4)', // 边框样式
      follow: true, // 是否跟随视口移动
      lineWidth: 1, // 网格线宽度
      stroke: '#e70606', // 网格线颜色
    }
  ]


  useEffect(() => {
    const graph = initGraph();
    setGraphInstance(graph);
    graph?.render?.();
    graphRef.current = graph;
    setZoom(1)
    return () => {
      setGraphInstance(undefined);
      graph?.clear?.();
      graph?.destroy?.();
    };
  }, [props, getLocalTheme()]);


  useEffect(() => {
    // 注册双击事件, 双击画图时 将视图居中
    if (!graphInstance) return;
    graphInstance?.once?.(G6.GraphEvent.AFTER_RENDER, () => {
      graphInstance?.fitView?.({
        direction: 'both',
        when: 'always'
      }, true);
    });
    console.log('register dblclick event');
    graphInstance?.once?.('canvas:dblclick', () => {
      console.debug('canvas dblclick , to fitView and fitCenter');
      graphInstance?.fitView?.({
        direction: 'both',
        when: 'always'
      }, true)
    });
  }, [graphInstance]);


  useEffect(() => {
    if (graphInstance) {
      graphInstance?.fitView?.({
        direction: 'both',
        when: 'always'
      })
      graphInstance?.zoomTo?.(zoom, true);
      console.debug('current zoom: ', zoom);
    }
  }, [zoom, graphInstance !== undefined]);

  return <>
    <div
      style={{
        height: '70vh',
        position: 'absolute',
        top: '5vh',
        bottom: '5vh',
        right: '5vw',
        zIndex: 2
      }}
    >
      <Slider
        vertical included={true}
        style={{
          backgroundColor: 'rgba(159,153,111,0.73)',
          borderRadius: 5,
          width: 12,
          height: '100%',
          alignItems: 'center'
        }}
        value={zoom}
        min={0}
        max={1}
        tooltip={{open: true, placement: 'left', formatter: (value) => `${value * 100}%`}}
        // dots={true}
        marks={{0.25: '25%', 0.5: '50%', 0.75: '75%', 1: '100%'}}
        step={STEP}
        onChange={setZoom}
      />
    </div>
    {
      props.refreshLoading ? <Loading loading={props.refreshLoading}/> :
        <div id={'table-lineage'} style={{height: '100%', width: '100%'}} ref={graphRef}/>
    }

  </>;
};

export default TableLineage;
