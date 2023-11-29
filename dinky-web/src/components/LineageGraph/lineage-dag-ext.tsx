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

import LineageDag from 'react-lineage-dag';

import * as _ from 'lodash';
import * as ReactDOM from 'react-dom';
import { transformEdges, transformInitData } from 'react-lineage-dag/src/adaptor';
import LineageCanvas from 'react-lineage-dag/src/canvas/canvas';

export default class LineageDagExt extends LineageDag {
  componentDidMount() {
    let root = ReactDOM.findDOMNode(this) as HTMLElement;

    let enableHoverChain = _.get(this.props, 'config.enableHoverChain', true);
    let titleRender = _.get(this.props, 'config.titleRender');

    let canvasObj = {
      root: root,
      disLinkable: false,
      linkable: false,
      draggable: false,
      zoomable: true,
      moveable: true,
      theme: {
        edge: {
          type: 'endpoint',
          shapeType: 'AdvancedBezier',
          arrow: true,
          isExpandWidth: true,
          arrowPosition: 1,
          arrowOffset: -5
        },
        endpoint: {
          limitNum: undefined,
          expandArea: {
            left: 0,
            right: 0,
            top: 0,
            botton: 0
          }
        }
      },
      data: {
        enableHoverChain: enableHoverChain
      }
    };

    this.canvas = new LineageCanvas(canvasObj);

    let result = transformInitData({
      tables: this.props.tables,
      relations: this.props.relations,
      columns: this.props.columns,
      operator: this.props.operator,
      _titleRender: titleRender,
      _enableHoverChain: enableHoverChain,
      _emptyContent: this.props.emptyContent,
      _emptyWidth: this.props.emptyWidth
    });

    this.originEdges = result.edges;

    result = transformEdges(result.nodes, _.cloneDeep(result.edges));
    result.edges = result.edges.map((item) => {
      return {
        ...item,
        // 线条的类型: Bezier/Flow/Straight/Manhattan/AdvancedBezier/Bezier2-1/Bezier2-2/Bezier2-3/BrokenLine , 新版本可以指定 但是布局调整了
        // https://github.com/zhu-mingye/butterfly/blob/master/docs/zh-CN/edge.md#shapetype--string----%E9%80%89%E5%A1%AB
        shapeType: 'AdvancedBezier'
      };
    });

    this.canvasData = {
      nodes: result.nodes,
      edges: result.edges
    };

    setTimeout(() => {
      let tmpEdges = result.edges;
      result.edges = [];
      // this.canvas.wrapper.style.visibility = 'hidden';
      this.canvas.draw(result, () => {
        this.canvas.relayout(
          {
            edges: tmpEdges.map((item) => {
              return {
                source: item.sourceNode,
                target: item.targetNode
              };
            })
          },
          true
        );
        // this.canvas.wrapper.style.visibility = 'visible';
        this.canvas.addEdges(tmpEdges, true);

        let minimap = _.get(this, 'props.config.minimap', {});

        const minimapCfg = _.assign({}, minimap.config, {
          events: ['system.node.click', 'system.canvas.click']
        });

        if (minimap && minimap.enable) {
          this.canvas.setMinimap(true, minimapCfg);
        }

        if (_.get(this, 'props.config.gridMode')) {
          this.canvas.setGridMode(true, _.assign({}, _.get(this, 'props.config.gridMode', {})));
        }

        if (result.nodes.length !== 0) {
          this.canvas.focusCenterWithAnimate();
          this._isFirstFocus = true;
        }

        this.forceUpdate();
        this.props.onLoaded && this.props.onLoaded(this.canvas);
      });
      this.canvas.on('system.node.click', (data) => {
        let node = data.node;
        this.canvas.focus(node.id);
      });
      this.canvas.on('system.canvas.click', () => {
        this.canvas.unfocus();
      });
    }, _.get(this.props, 'config.delayDraw', 0));
  }
}
