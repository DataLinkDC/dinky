/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
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

  if (job && graph) {
    job.forEach((vertice) => {
      const node = graph.getCellById(vertice.id);
      if (node) {
        node.setData({ ...node.getData(), ...vertice });
      }
    });

    graph.getEdges().forEach((edge) => {
      const nodeData = edge.getSourceNode()?.getData();
      if (nodeData.status == JOB_STATUS.RUNNING) {
        edge.attr({ line: { stroke: '#3471F9' } });
        edge.attr('line/strokeDasharray', 5);
        edge.attr('line/strokeWidth', 2);
        edge.attr('line/style/animation', 'running-line 30s infinite linear');
      } else {
        edge.attr('line/strokeDasharray', 0);
        edge.attr('line/style/animation', '');
        edge.attr('line/strokeWidth', 1);
        if (nodeData.status == JOB_STATUS.FINISHED) {
          edge.attr('line/stroke', '#52c41a');
        } else if (nodeData.status == JOB_STATUS.CANCELED) {
          edge.attr('line/stroke', '#ffe7ba');
        } else if (nodeData.status == JOB_STATUS.FAILED) {
          edge.attr('line/stroke', '#ff4d4f');
        } else {
          edge.attr('line/stroke', '#bfbfbf');
        }
      }
    });
  }
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
