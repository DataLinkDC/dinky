import { Empty } from "antd";
import {FlowAnalysisGraph} from '@ant-design/charts';
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import React, {useState} from "react";

const StudioGraph = (props:any) => {
  const {graphData,current,currentSession} = props;

  const config = {
    data:graphData,
    nodeCfg: {
      size: [140, 25],
      items: {
        padding: 6,
        containerStyle: {
          fill: '#fff',
        },
        style: (cfg, group, type) => {
          const styles = {
            icon: {
              width: 12,
              height: 12,
            },
            value: {
              fill: '#f00',
            },
            text: {
              fill: '#aaa',
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
      title: {
        containerStyle: {
          fill: 'transparent',
        },
        style: {
          fill: '#000',
          fontSize: 12,
        },
      },
      style: {
        fill: '#E6EAF1',
        stroke: '#B2BED5',
        radius: [2, 2, 2, 2],
      },
    },
    edgeCfg: {
      label: {
        style: {
          fill: '#aaa',
          fontSize: 12,
          fillOpacity: 1,
        },
      },
      style: (edge) => {
        const stroke = edge.target === '0' ? '#c86bdd' : '#5ae859';
        return {
          stroke,
          lineWidth: 1,
          strokeOpacity: 0.5,
        };
      },
      edgeStateStyles: {
        hover: {
          lineWidth: 2,
          strokeOpacity: 1,
        },
      },
    },
    markerCfg: (cfg) => {
      const {edges} = graphData;
      return {
        position: 'right',
        show: edges.find((item) => item.source == cfg.id),
        collapsed: !edges.find((item) => item.source == cfg.id),
      };
    },
    behaviors: ['drag-canvas', 'zoom-canvas', 'drag-node'],
  };




  /*const buildGraphEdges=(nodes)=>{
    let edges = [];
    for(let i in nodes){
      if(nodes[i].predecessors){
        for(let j in nodes[i].predecessors){
          edges.push({source: nodes[i].predecessors[j].id.toString(),
            target: nodes[i].id.toString(),
            value: nodes[i].predecessors[j].ship_strategy})
        }
      }
    }
    return edges;
  };*/

  return (
    <>{graphData? <FlowAnalysisGraph {...config} /> :<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  currentSession: Studio.currentSession,
}))(StudioGraph);
