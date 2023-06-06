import { Graph } from "@antv/x6";
/**
 * //根据json数据绘节点及边
 * @param graph
 * @param data
 */
export const handleInitNodes = (graph: Graph, data: any) => {
  //目前写死，后续优化
  graph.fromJSON(data);

};
