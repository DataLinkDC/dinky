import { Empty } from "antd";
import {FlowAnalysisGraph} from '@ant-design/charts';
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import React, {useState} from "react";

const StudioGraph = (props:any) => {
  const {data,current,currentSession} = props;

  const config = {
    data,
    nodeCfg: {
      size: [140, 65],
      /*anchorPoints: [
        [0.5, 1],
        [0.5, 0],
      ],*/
      items: {
        padding: [6, 0, 0],
        containerStyle: {
          fill: '#fff',
          width:'100px',
          display: 'inline-block',
          overflow:'hidden',
          textOverflow:'ellipsis',
          whiteSpace:'nowrap',
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
              width:'100px',
              display: 'inline-block',
              overflow:'hidden',
          textOverflow:'ellipsis',
          whiteSpace:'nowrap',
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
        radius: [2, 2, 2, 2],
      },
    },
    edgeCfg: {
      type: 'polyline',
      label: {
        style: {
          fill: '#aaa',
          fontSize: 12,
          fillOpacity: 1,
        },
      },
      endArrow: {
        fill: '#ddd',
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
