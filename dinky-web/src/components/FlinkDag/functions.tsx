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

import { JOB_STATUS } from '@/pages/DevOps/constants';
import { Jobs } from '@/types/DevOps/data';
import { Graph, Path } from '@antv/x6';

export const buildDag = (job: Jobs.JobPlan) => {
  const edges: any = [];
  const nodes: any = [];

  if (!job) return { nodes: nodes, edges: edges };

  job.nodes.forEach((node) => {
    nodes.push({
      id: node.id,
      shape: 'data-processing-dag-node',
      ports: [
        {
          id: `${node.id}-in`,
          group: 'in'
        },
        {
          id: `${node.id}-out`,
          group: 'out'
        }
      ],
      data: node
    });

    node.inputs?.forEach((plan_node) => {
      edges.push({
        label: plan_node.ship_strategy,
        id: `${node.id}-${plan_node.num}`,
        shape: 'data-processing-curve',
        zIndex: -1,
        source: {
          cell: `${plan_node.id}`,
          port: `${plan_node.id}-out`
        },
        target: {
          cell: `${node.id}`,
          port: `${node.id}-in`
        },
        data: node
      });
    });
  });

  return { nodes: nodes, edges: edges };
};

export const updateDag = (job: Jobs.JobVertices[], graph?: Graph) => {
  if (!job || !graph) return;

  job.forEach((vertices) => {
    const node = graph.getCellById(vertices.id);
    if (node) {
      node.setData({ ...node.getData(), ...vertices });
    }
  });

  graph.getEdges().forEach((edge) => {
    const nodeData = edge.getSourceNode()?.getData();
    let stroke = '#bfbfbf';
    let strokeDasharray = 0;
    let strokeWidth = 1;
    let animation = '';

    switch (nodeData.status) {
      case JOB_STATUS.RUNNING: {
        stroke = '#3471F9';
        strokeDasharray = 5;
        strokeWidth = 2;
        animation = 'running-line 30s infinite linear';
        break;
      }
      case JOB_STATUS.FINISHED:
        stroke = '#52c41a';
        break;
      case JOB_STATUS.CANCELED:
        stroke = '#ffe7ba';
        break;
      case JOB_STATUS.FAILED:
        stroke = '#ff4d4f';
        break;
    }

    edge.attr({ line: { strokeDasharray: strokeDasharray } });
    edge.attr({ line: { strokeWidth: strokeWidth } });
    edge.attr({ line: { animation: animation } });
    edge.attr({ line: { stroke: stroke } });
  });
};

export const regConnect = (sourcePoint: any, targetPoint: any) => {
  const hgap = Math.abs(targetPoint.x - sourcePoint.x);
  const path = new Path();
  path.appendSegment(Path.createSegment('M', sourcePoint.x - 4, sourcePoint.y));
  path.appendSegment(Path.createSegment('L', sourcePoint.x + 12, sourcePoint.y));
  // 水平三阶贝塞尔曲线
  path.appendSegment(
    Path.createSegment(
      'C',
      sourcePoint.x < targetPoint.x ? sourcePoint.x + hgap / 2 : sourcePoint.x - hgap / 2,
      sourcePoint.y,
      sourcePoint.x < targetPoint.x ? targetPoint.x - hgap / 2 : targetPoint.x + hgap / 2,
      targetPoint.y,
      targetPoint.x - 6,
      targetPoint.y
    )
  );
  path.appendSegment(Path.createSegment('L', targetPoint.x + 2, targetPoint.y));

  return path.serialize();
};
