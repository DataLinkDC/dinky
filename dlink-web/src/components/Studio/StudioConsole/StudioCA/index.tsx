import { Tabs,Empty,Tooltip,Button } from "antd";
import { IndentedTreeGraph } from '@ant-design/charts';
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

  const config = {
    data:oneTableCAData,
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
        lineWidth: graph.findById(item.target).getModel().columnSize,
        strokeOpacity: 0.5,
      };
    },
    nodeStyle: () => {
      return {
        stroke: '#40a9ff',
      };
    },
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
        lineWidth: graph.findById(item.target).getModel().columnSize,
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
        setOneTableCAData(convertTreeData(result.datas[0]));
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
        setOneColumnCAData(convertTreeData(result.datas[0]));
      }else{
        setOneColumnCAData(null);
      }
    })
  };

  const convertTreeData=(node)=>{
    if(node){
      node.body=node.columns.toString();
      for(let i in node.children){
        node.children[i] = convertTreeData(node.children[i])
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
          单表表级血缘
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
          {oneTableCAData!=null?<IndentedTreeGraph {...config} />:<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
        </div>
      </TabPane>
      <TabPane
        tab={
          <span>
          单表字段级血缘
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
      </TabPane>
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
