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


import { Empty } from "antd";
import {FlowAnalysisGraph} from '@ant-design/charts';
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import React, {useState} from "react";

const StudioGraph = (props:any) => {
  const {data,current,currentSession} = props;

  const config = {
    data,
    height:350,
    nodeCfg: {
      size: [160, 65],
      items: {
        autoEllipsis: false,
        padding: [10],
        containerStyle: {
          fill: '#fff',
          width:'100px',
        },
        style: (cfg, group, type) => {
          const styles = {
            value: {
              fill: '#000',
            },
            text: {
              fill: '#222',
              width:'100px',
            },
          };
          return styles[type];
        },
      },
      nodeStateStyles: {
        hover: {
          stroke: '#1890ff',
          lineWidth: 2,
        },
      },
      style: {
        fill: '#40a9ff',
        stroke: '#1890ff',
      },
    },
    edgeCfg: {
      type: 'polyline',
      label: {
        style: {
          fill: '#666',
          fontSize: 12,
          fillOpacity: 1,
        },
      },
      endArrow: {
        fill: '#333',
      },
      edgeStateStyles: {
        hover: {
          stroke: '#1890ff',
          lineWidth: 2,
        },
      },
    },
    markerCfg: (cfg) => {
      const { edges } = data;
      return {
        position: 'right',
        show: edges.find((item) => item.source === cfg.id),
        collapsed: !edges.find((item) => item.source === cfg.id),
      };
    },
    behaviors: ['drag-canvas', 'zoom-canvas', 'drag-node'],
    /*layout: {
      rankdir: 'TB',
      ranksepFunc: () => 20,
    },*/
  };

  return (
    <>{data? <FlowAnalysisGraph {...config} /> :<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  currentSession: Studio.currentSession,
}))(StudioGraph);
