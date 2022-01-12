import { Tabs,Empty,Tooltip,Button } from "antd";
import { FlowAnalysisGraph } from '@ant-design/charts';
import {SearchOutlined} from "@ant-design/icons";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import { getCAByStatement} from "@/pages/FlinkSqlStudio/service";
import {useState} from "react";

const { TabPane } = Tabs;

const StudioCA = (props:any) => {
  const {current} = props;
  const [oneTableCAData,setOneTableCAData] = useState<any>(null);
  const [oneColumnCAData,setOneColumnCAData] = useState<any>(null);

  const nodeStateStyles = {
    hover: {
      stroke: '#1890ff',
      lineWidth: 2,
    },
    selected: {
      stroke: '#f00',
      lineWidth: 3,
    },
  };

  const buildGraphData=(data,graphData)=>{
    if(!graphData.nodes){
      graphData.nodes = [];
    }
    if(!graphData.edges){
      graphData.edges = [];
    }
    for(let i in data){
      let nodesItem = {
        id:data[i].id,
        value:{
          title:data[i].name,
          items: [
            {
              text: data[i].columns,
            },
          ],
        }
      }
      graphData.nodes.push(nodesItem);
      if(data[i].children){
        for(let j in data[i].children){
          graphData.edges.push({source: data[i].children[j].id,
            target: data[i].id,
            value: ''});
          buildGraphData(data[i].children,graphData);
        }
      }
    }
  };

  const config = {
    data: oneTableCAData,
    height:350,
    nodeCfg: {
      size: [160, 65],
      items: {
        autoEllipsis: false,
        padding: [10],
        containerStyle: {
          // fill: '#fff',
          width:'100px',
        },
        style: (cfg, group, type) => {
          const styles = {
            value: {
              // fill: '#000',
            },
            text: {
              // fill: '#222',
              width:'100px',
            },
          };
          return styles[type];
        },
      },
      nodeStateStyles: {
        hover: {
          // stroke: '#1890ff',
          lineWidth: 2,
        },
      },
      style: {
        // fill: '#40a9ff',
        // stroke: '#1890ff',
      },
    },
    edgeCfg: {
      type: 'polyline',
      label: {
        style: {
          // fill: '#666',
          fontSize: 12,
          fillOpacity: 1,
        },
      },
      endArrow: {
        // fill: '#333',
      },
      edgeStateStyles: {
        hover: {
          // stroke: '#1890ff',
          lineWidth: 2,
        },
      },
    },
    markerCfg: (cfg) => {
      const { edges } = oneTableCAData;
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

  const columnConfig = {
    data:oneColumnCAData,
    behaviors: ['drag-canvas', 'zoom-canvas', 'drag-node'],
    bodyStyle: {
      fill: '#aaa',
    },
    nodeStateStyles,
    onReady: (graph) => {
      graph.on('node:mouseenter', (evt) => {
        const item = evt.item;
        graph.setItemState(item, 'hover', true);
      });
      graph.on('node:mouseleave', (evt) => {
        const item = evt.item;
        graph.setItemState(item, 'hover', false);
      });
    },
    edgeStyle: (item, graph) => {
      /**
       * graph.findById(item.target).getModel()
       * item.source: 获取 source 数据
       * item.target: 获取 target 数据
       */
      // console.log(graph.findById(item.target).getModel());
      return {
        stroke: '#40a9ff',
        // lineWidth: graph.findById(item.target).getModel().columnSize,
        lineWidth: 1,
        strokeOpacity: 0.5,
      };
    },
    nodeStyle: () => {
      return {
        stroke: '#40a9ff',
      };
    },
  };

  const getOneTableCA=()=>{
    const res = getCAByStatement({
      statement:current.value,
      type: 1,
    });
    res.then((result)=>{
      if(result.code==0){
        let graphData = {};
        buildGraphData(result.datas,graphData);
        setOneTableCAData(graphData);
      }else{
        setOneTableCAData(null);
      }
    })
  };

  const getOneColumnCA=()=>{
    const res = getCAByStatement({
      statement:current.value,
      type: 2,
    });
    res.then((result)=>{
      if(result.code==0){
        setOneColumnCAData(buildGraphData(result.datas[0]));
      }else{
        setOneColumnCAData(null);
      }
    })
  };

  const fullTreeData=(node)=>{
    if(node){
      node.body=node.columns.toString();
      for(let i in node.children){
        node.children[i] = fullTreeData(node.children[i])
      }
      return node;
    }
    return null;
  };

  return (
    <Tabs defaultActiveKey="OneTableCA" size="small"  tabPosition="left" >
      <TabPane
        tab={
          <span>
          单任务表级血缘
        </span>
        }
        key="OneTableCA"
      >
        <div>
          <div style={{float: "left"}}>
            <Tooltip title="重新计算血缘">
              <Button
                type="text"
                icon={<SearchOutlined />}
                onClick={getOneTableCA}
              />
            </Tooltip>
          </div>
          {oneTableCAData!=null?<FlowAnalysisGraph {...config} />:<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
        </div>
      </TabPane>
      {/*<TabPane
        tab={
          <span>
          单任务字段级血缘
        </span>
        }
        key="OneColumnCA"
      >
        <div>
          <div style={{float: "left"}}>
            <Tooltip title="重新计算血缘">
              <Button
                type="text"
                icon={<SearchOutlined />}
                onClick={getOneColumnCA}
              />
            </Tooltip>
          </div>
          {oneColumnCAData!=null?<IndentedTreeGraph {...columnConfig} />:<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
        </div>
      </TabPane>*/}
      {/*<TabPane
        tab={
          <span>
          全局表级血缘
        </span>
        }
        key="AllTableCA"
      >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>
      <TabPane
        tab={
          <span>
          全局字段级血缘
        </span>
        }
        key="AllColumnCA"
      >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>*/}
    </Tabs>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioCA);
