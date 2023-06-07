import { Graph, Node } from '@antv/x6';
import { register } from '@antv/x6-react-shape';
import TextNode from '@/components/Studio/StudioGraphEdit/GraphEditor/components/text-node';
import OperatorNode from '@/components/Studio/StudioGraphEdit/GraphEditor/components/operator-node';
import SqlNode from '@/components/Studio/StudioGraphEdit/GraphEditor/components/sql-node';
import { Parameter } from '@/components/Studio/StudioGraphEdit/GraphEditor/ts-define/parameter';
import { PortManager } from '@antv/x6/es/model/port';
import React from 'react';
import { GroupNode } from '@/components/Studio/StudioGraphEdit/GraphEditor/components/group-node';

enum LocalNodeType {
  MYSQL = 'mysql',
  OPERATORS = 'operators',
}

function registerTextNode() {
  //注册文本节点
  register({
    shape: 'custom-text-node',
    component: TextNode,
    width: 180,
    height: 180,
    attrs: {
      text: {
        text: 'custom-text-node',
      },
    },
    zIndex: 20,
  });
}

function registerOperatorNode(
  name: string,
  ports: Partial<PortManager.Metadata> | PortManager.PortMetadata[],
  registerCpn: React.ComponentType<{
    node: Node;
    graph: Graph;
  }>,
  portItem: PortManager.PortMetadata[],
) {
  register({
    width: 80,
    height: 50,
    attrs: {
      body: {
        style: {
          'background-color': '#c6e5ff',
          border: '1px solid #949494',
        },
      },
    },
    shape: name,
    component: registerCpn,
    ports: { ...ports, items: portItem },
  });
}

export default (
  graph: Graph,
  ports: Partial<PortManager.Metadata> | PortManager.PortMetadata[],
  operatorParameters: Parameter[],
) => {
  // 基础节点 (后期考虑根据导入Json数据注册)
  console.log(operatorParameters, 'operatorParameters');

  operatorParameters?.forEach((param) => {
    //生成ports Item
    const portItem: PortManager.PortMetadata[] = [];

    portItem.push(
      ...param.ports.inputs.map((item: { id: string }) => ({
        group: 'inputs',
        id: item.id,
        zIndex: 10,
        // markup:
      })),
    );

    portItem.push(
      ...param.ports.outputs.map((item: { id: string }) => ({
        group: 'outputs',
        id: item.id,
      })),
    );

    //保存组和节点关系
    switch (param.group.split('.')[0]) {
      case LocalNodeType.MYSQL:
        registerOperatorNode(param.name, ports, SqlNode, portItem);
        break;
      case LocalNodeType.OPERATORS:
        registerOperatorNode(param.name, ports, OperatorNode, portItem);
        break;
      default:
        break;
    }
  });

  registerTextNode();

  Graph.registerNode('package', GroupNode);
};
