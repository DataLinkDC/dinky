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

import React, {useEffect, useRef, useState} from 'react'
import {Graph, Edge} from '@antv/x6'
import {register} from "@antv/x6-react-shape";
import {DagreLayout} from "@antv/layout";
import DagNode from "@/components/FlinkDag/component/DagNode";
import {
  edgeConfig,
  graphConfig,
  layoutConfig,
  portConfig
} from "@/components/FlinkDag/config";
import {buildData, regConnect, updateEdge} from "@/components/FlinkDag/functions";
import {Jobs} from "@/types/DevOps/data";
import "./index.css"

const  FlinkDag = (props:{jobDetail:Jobs.JobInfoDetail}) => {

  const container = useRef(null);
  const job = props.jobDetail.jobDataDto.job;
  const [graph,setGraph] = useState<Graph>();
  const [curentJob,setCurentJob] = useState<string>();

  const initGraph = (flinkData:any) =>{

    register({
      shape: 'data-processing-dag-node',
      width: 212, height: 48,
      component: DagNode,
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

    // Adaptive layout
    const dagreLayout = new DagreLayout(layoutConfig);
    const model = dagreLayout.layout(flinkData);
    graph.fromJSON(model);

    // Automatically zoom to fit
    const zoomOptions = {
      padding: {
        left: 50,
        right: 50,
        bottom: 100
      },
    }
    graph.zoomToFit(zoomOptions)
    graph.centerContent();
    return graph;
  }

  useEffect(() => {
    const flinkData = buildData(job);
    // Clean up old data
    if (graph){
      graph.clearCells();
    }
    setGraph(initGraph(flinkData));
  }, [curentJob]);

  useEffect(() => {
    updateEdge(job, graph);
    if (curentJob != job.jid){
      setCurentJob(job.jid)
    }
  }, [job]);


  return (
    <div style={{
      'height': '100%',
      'width': '100%',
    }} ref={container}/>
  )
};

export default FlinkDag;
