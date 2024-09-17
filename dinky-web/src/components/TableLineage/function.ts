import {LineageDetailInfo} from "@/types/DevOps/data";
import {EdgeData, NodeData} from "@antv/g6";
import {ComboData, GraphData} from "@antv/g6/src/spec/data";


/**
 * {  "nodes": [    { "id": "node1", "combo": "combo-1", "style": { "x": 100, "y": 100 } },    { "id": "node2", "style": { "x": 200, "y": 200 } }  ],  "edges": [{ "source": "node1", "target": "node2" }],  "combos": [{ "id": "combo-1", "style": { "x": 100, "y": 100 } }] }
 * @param data
 */
const lineageDataTransformToGraphData = (data: LineageDetailInfo): GraphData => {
  const nodes: NodeData[] = []; // 节点
  const edges: EdgeData[] = []; // 边
  const combos: ComboData[] = []; // 分组

  data.tables.forEach(item => {
    item.columns.forEach(column => {
      nodes.push({
        id: item.name + '.' + column.name,
        label: column.name,
        fullDbName: item.name,
        column: column,
      });
    });
    // nodes.push({
    //   id: item.name,
    //   label: item.name,
    //   data: item.name,
    //   column: item.columns,
    //   // style: {
    //   //   fill: '#e70606',
    //   //   stroke: '#1060d9',
    //   //   lineWidth: 1,
    //   //   port: true,
    //   //   ports: [{placement: 'left'}, {placement: 'right'}]
    //   // }
    // });
    // combos.push({
    //   id: item.id,
    //   type: 'table',
    //   label: item.name,
    //   style: {fill: '#e70606', stroke: '#1060d9', lineWidth: 1}
    // });
  })
  data.relations.forEach(item => {
    // 拿出 表的 name 和 字段的 name
    const source = data.tables.findLast(table => table.id === item.srcTableId);
    const target = data.tables.findLast(table => table.id === item.tgtTableId);
    if (!source || !target) {
      return;
    }
    edges.push({
      id: item.id,
      source: source.name + '.' + item.srcTableColName,
      sourceNode: item.srcTableColName,
      target: target.name + '.' + item.tgtTableColName,
      targetNode: item.tgtTableColName,
      label: item.relationship,
      style: {stroke: '#1060d9', lineWidth: 1}
    });
  })


  return {nodes: nodes, edges: edges, combos: combos};
}


export {lineageDataTransformToGraphData};
