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

import DagDataNode from '@/components/FlinkDag/component/DagDataNode';
import DagPlanNode from '@/components/FlinkDag/component/DagPlanNode';
import {
  edgeConfig,
  graphConfig,
  layoutConfig,
  portConfig,
  zoomOptions
} from '@/components/FlinkDag/config';
import { buildDag, regConnect, updateDag } from '@/components/FlinkDag/functions';
import { Jobs } from '@/types/DevOps/data';
import { DagreLayout } from '@antv/layout';
import { Edge, Graph } from '@antv/x6';
import { Selection } from '@antv/x6-plugin-selection';
import { register } from '@antv/x6-react-shape';
import { Drawer } from 'antd';
import { useEffect, useRef, useState } from 'react';
import './index.css';

export type DagProps = {
  job: Jobs.Job;
  onlyPlan?: boolean;
};

const FlinkDag = (props: DagProps) => {
  const container = useRef(null);

  const { job, onlyPlan = false } = props;

  const [graph, setGraph] = useState<Graph>();
  const [currentJob, setCurrentJob] = useState<string>();
  const [currentSelect, setCurrentSelect] = useState<any>();
  const [open, setOpen] = useState(false);

  const handleClose = () => {
    setOpen(false);
    setCurrentSelect(undefined);
    graph?.zoomToFit(zoomOptions);
    graph?.centerContent();
  };

  const initListen = (graph: Graph) => {
    graph.on('node:selected', ({ cell }) => {
      setOpen(true);
      setCurrentSelect(cell);
      graph.positionCell(cell, 'center');
    });

    graph.on('node:unselected', ({ cell }) => handleClose());
  };

  const initGraph = (flinkData: any) => {
    register({
      shape: 'data-processing-dag-node',
      width: 250,
      height: 140,
      component: onlyPlan ? DagPlanNode : DagDataNode,
      ports: portConfig
    });

    Edge.config(edgeConfig);
    Graph.registerConnector('curveConnector', regConnect, true);
    Graph.registerEdge('data-processing-curve', Edge, true);

    const graph: Graph = new Graph({
      // @ts-ignore
      container: container.current,
      ...graphConfig
    });

    graph.use(
      new Selection({
        enabled: true,
        multiple: false,
        rubberband: false,
        showNodeSelectionBox: true
      })
    );

    // Adaptive layout
    const model = new DagreLayout(layoutConfig).layout(flinkData);
    graph.fromJSON(model);

    // Automatically zoom to fit
    graph.zoomToFit(zoomOptions);
    graph.centerContent();
    updateDag(job?.vertices, graph);
    initListen(graph);
    return graph;
  };

  useEffect(() => {
    const flinkData = buildDag(job?.plan);
    // Clean up old data
    if (graph) {
      graph.clearCells();
    }
    setGraph(initGraph(flinkData));
  }, [currentJob]);

  useEffect(() => {
    updateDag(job?.vertices, graph);
    if (currentJob != job?.jid) {
      setCurrentJob(job?.jid);
    }
  }, [job]);

  return (
    <>
      <div style={{ height: '100%', width: '100%' }} ref={container} />
      <Drawer
        title={currentSelect?.data?.id}
        open={open}
        getContainer={false}
        width={'35%'}
        mask={false}
        onClose={() => handleClose()}
        destroyOnClose={true}
      >
        <p>{currentSelect?.getData().description}</p>
      </Drawer>
    </>
  );
};

export default FlinkDag;
