import { Graph } from "@antv/x6";
export default (operatorParameters: any) => {
  operatorParameters.forEach((item: any) => {
    Graph.unregisterNode(item.name);
  });
  Graph.unregisterNode("custom-text-node");
  Graph.unregisterNode("package");
};
