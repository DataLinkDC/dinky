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

import { Circle, Group, Path } from '@antv/g';
import {
  ExtensionCategory,
  Graph,
  PathArray,
  RectCombo,
  RectComboStyleProps,
  register
} from '@antv/g6';
import { memo, useRef } from 'react';
import { Flex } from 'antd';
import { ReactNode } from '@antv/g6-extension-react';
import { Graphin } from '@antv/graphin';
import { DagreLayout, GridLayout } from '@antv/layout';
import { LineageDetailInfo } from '@/types/DevOps/data';

const collapse = (x: number, y: number, r: number) => {
  return [
    ['M', x - r, y],
    ['a', r, r, 0, 1, 0, r * 2, 0],
    ['a', r, r, 0, 1, 0, -r * 2, 0],
    ['M', x - r + 4, y],
    ['L', x + r - 4, y]
  ] as PathArray;
};

const expand = (x: number, y: number, r: number) => {
  return [
    ['M', x - r, y],
    ['a', r, r, 0, 1, 0, r * 2, 0],
    ['a', r, r, 0, 1, 0, -r * 2, 0],
    ['M', x - r + 4, y],
    ['L', x - r + 2 * r - 4, y],
    ['M', x - r + r, y - r + 4],
    ['L', x, y + r - 4]
  ] as PathArray;
};

// const data = {
//   "tables": [
//     {
//       "id": "2",
//       "name": "default_catalog.default_database.orders",
//       "columns": [
//         {
//           "name": "order_id",
//           "title": "order_id"
//         },
//         {
//           "name": "amount",
//           "title": "amount"
//         }
//       ]
//     },
//     {
//       "id": "4",
//       "name": "default_catalog.default_database.customers",
//       "columns": [
//         {
//           "name": "name",
//           "title": "name"
//         }
//       ]
//     },
//     {
//       "id": "3",
//       "name": "default_catalog.default_database.print_table",
//       "columns": [
//         {
//           "name": "order_id",
//           "title": "order_id"
//         },
//         {
//           "name": "customer_name",
//           "title": "customer_name"
//         },
//         {
//           "name": "product_name",
//           "title": "product_name"
//         },
//         {
//           "name": "amount",
//           "title": "amount"
//         },
//         {
//           "name": "price",
//           "title": "price"
//         }
//       ]
//     },
//     {
//       "id": "5",
//       "name": "default_catalog.default_database.products",
//       "columns": [
//         {
//           "name": "name",
//           "title": "name"
//         },
//         {
//           "name": "price",
//           "title": "price"
//         }
//       ]
//     }
//   ],
//   "relations": [
//     {
//       "id": "2",
//       "srcTableId": "2",
//       "tgtTableId": "3",
//       "srcTableColName": "order_id",
//       "tgtTableColName": "order_id"
//     },
//     {
//       "id": "3",
//       "srcTableId": "4",
//       "tgtTableId": "3",
//       "srcTableColName": "name",
//       "tgtTableColName": "customer_name"
//     },
//     {
//       "id": "4",
//       "srcTableId": "5",
//       "tgtTableId": "3",
//       "srcTableColName": "name",
//       "tgtTableColName": "product_name"
//     },
//     {
//       "id": "5",
//       "srcTableId": "2",
//       "tgtTableId": "3",
//       "srcTableColName": "amount",
//       "tgtTableColName": "amount"
//     },
//     {
//       "id": "6",
//       "srcTableId": "5",
//       "tgtTableId": "3",
//       "srcTableColName": "price",
//       "tgtTableColName": "price"
//     }
//   ]
// }

class RectComboWithExtraButton extends RectCombo {
  render(attributes: Required<RectComboStyleProps>, container: Group) {
    super.render(attributes, container);
    this.drawButton(attributes);
  }

  drawButton(attributes: Required<RectComboStyleProps>) {
    const { collapsed } = attributes;
    const [, height] = this.getKeySize(attributes);
    const btnR = 8;
    const y = -(height / 2 + btnR);
    const d = collapsed ? expand(0, y, btnR) : collapse(0, y, btnR);

    const hitArea = this.upsert(
      'hit-area',
      Circle,
      { cy: y, r: 10, fill: '#fff', cursor: 'pointer' },
      this
    );
    this.upsert('button', Path, { stroke: '#3d81f7', d, cursor: 'pointer' }, hitArea!!);
  }

  onCreate() {
    this.shapeMap['hit-area'].addEventListener('click', () => {
      const id = this.id;
      const collapsed = !this.attributes.collapsed;
      const { graph } = this.attributes.context!!;
      if (collapsed) graph.collapseElement(id);
      else graph.expandElement(id);
    });
  }
}

register(ExtensionCategory.COMBO, 'circle-combo-with-extra-button', RectComboWithExtraButton);

register(ExtensionCategory.NODE, 'react', ReactNode);
export const LineageNew = memo((props: { data: LineageDetailInfo }) => {
  const { data } = props;
  const graphRef = useRef<Graph>(null);
  // 把data.tables 的id ,name转成map
  const tablesMap = data.tables.reduce(
    (acc, item) => {
      acc[item.id] = item.name;
      return acc;
    },
    {} as Record<string, string>
  );

  return (
    <Graphin
      ref={graphRef}
      style={{ overflow: 'hidden' }}
      options={{
        autoResize: true,
        data: {
          nodes: data.tables.flatMap((item) =>
            item.columns.map((column) => ({
              id: item.id + column.name,
              combo: item.id,
              data: { name: column.name }
            }))
          ),
          edges: data.relations.map((item) => ({
            source: item.srcTableId + item.srcTableColName,
            target: item.tgtTableId + item.tgtTableColName
          })),
          combos: data.tables.map((item) => ({ id: item.id }))
        },
        combo: {
          type: 'circle-combo-with-extra-button',
          style: {
            labelText: (d) => tablesMap[d.id]
          }
        },
        node: {
          type: 'react',
          style: {
            size: [240, 20],
            component: (data) => (
              <Flex
                justify={'center'}
                align={'center'}
                style={{
                  width: '100%',
                  height: '100%',
                  background: '#fff',
                  borderRadius: 5,
                  border: '1px solid gray'
                }}
                vertical
              >
                {data.data.name}
              </Flex>
            ),
            port: true,
            ports: [{ placement: 'right' }, { placement: 'left' }]
          }
        },
        edge: {
          type: 'cubic-horizontal',
          style: {
            labelBackground: true,
            endArrow: true
          }
        },
        layout: {
          type: 'combo-combined',
          // comboPadding: 40,
          // nodeSize: 0,
          // spacing: 0,
          innerLayout: new GridLayout({ cols: 1, condense: true }),
          outerLayout: new DagreLayout({
            rankdir: 'LR',
            edgeLabelSpace: false,
            nodesep: 5,
            ranksep: 50
          })
        },
        behaviors: [
          'drag-canvas',
          'zoom-canvas',
          {
            type: 'hover-activate',
            degree: 100 // 👈🏻 Activate relations.
          }
        ],
        plugins: [
          { key: 'grid-line', type: 'grid-line', follow: false, size: 40 },
          {
            type: 'toolbar',
            position: 'right-top',
            onClick: (item) => {
              alert('item clicked:' + item);
            },
            getItems: () => {
              // G6 内置了 9 个 icon，分别是 zoom-in、zoom-out、redo、undo、edit、delete、auto-fit、export、reset
              return [
                { id: 'zoom-in', value: 'zoom-in' },
                { id: 'zoom-out', value: 'zoom-out' },
                { id: 'auto-fit', value: 'auto-fit' },
                // { id: 'export', value: 'export' },
                { id: 'reset', value: 'reset' }
              ];
            }
          }
        ],
        transforms: ['process-parallel-edges'],
        autoFit: 'center'
      }}
    />
  );
});
