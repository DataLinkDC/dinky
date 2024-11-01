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

import { Circle, Path } from '@antv/g';
import { CircleCombo, ExtensionCategory, Graph, register } from '@antv/g6';
import { useEffect, useRef } from 'react';
import { Graph as G6Graph } from '@antv/g6';
import { useAsyncEffect } from 'ahooks';
const collapse = (x, y, r) => {
  return [
    ['M', x - r, y],
    ['a', r, r, 0, 1, 0, r * 2, 0],
    ['a', r, r, 0, 1, 0, -r * 2, 0],
    ['M', x - r + 4, y],
    ['L', x + r - 4, y]
  ];
};

const expand = (x, y, r) => {
  return [
    ['M', x - r, y],
    ['a', r, r, 0, 1, 0, r * 2, 0],
    ['a', r, r, 0, 1, 0, -r * 2, 0],
    ['M', x - r + 4, y],
    ['L', x - r + 2 * r - 4, y],
    ['M', x - r + r, y - r + 4],
    ['L', x, y + r - 4]
  ];
};

class CircleComboWithExtraButton extends CircleCombo {
  render(attributes, container) {
    super.render(attributes, container);
    this.drawButton(attributes);
  }

  drawButton(attributes) {
    const { collapsed } = attributes;
    const [, height] = this.getKeySize(attributes);
    const btnR = 8;
    const y = height / 2 + btnR;
    const d = collapsed ? expand(0, y, btnR) : collapse(0, y, btnR);

    const hitArea = this.upsert(
      'hit-area',
      Circle,
      { cy: y, r: 10, fill: '#fff', cursor: 'pointer' },
      this
    );
    this.upsert('button', Path, { stroke: '#3d81f7', d, cursor: 'pointer' }, hitArea);
  }

  onCreate() {
    this.shapeMap['hit-area'].addEventListener('click', () => {
      const id = this.id;
      const collapsed = !this.attributes.collapsed;
      const { graph } = this.attributes.context;
      if (collapsed) graph.collapseElement(id);
      else graph.expandElement(id);
    });
  }
}

export const LineageNew = () => {
  const graphRef = useRef<G6Graph>();
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    register(ExtensionCategory.COMBO, 'circle-combo-with-extra-button', CircleComboWithExtraButton);

    const graph = new G6Graph({
      container: containerRef.current,
      data: {
        nodes: [
          { id: 'node-0', combo: 'combo-0', style: { x: 100, y: 100 } },
          { id: 'node-1', combo: 'combo-0', style: { x: 150, y: 100 } },
          { id: 'node-2', style: { x: 250, y: 100 } }
        ],
        edges: [{ source: 'node-1', target: 'node-2' }],
        combos: [{ id: 'combo-0' }]
      },
      combo: {
        type: 'circle-combo-with-extra-button'
      },
      behaviors: ['drag-element']
    });

    graphRef.current = graph;

    graph.render().then();
    return () => {
      const graph = graphRef.current;
      if (graph) {
        graph.destroy();
        graphRef.current = undefined;
      }
    };
  }, []);

  return <div ref={containerRef} style={{ width: '100%', height: '100%' }} />;
};
