import { Graph, Node } from '@antv/x6';
import { register } from '@antv/x6-react-shape';
import TextNode from '@/components/Studio/StudioGraphEdit/GraphEditor/components/text-node';
import OperatorNode from '@/components/Studio/StudioGraphEdit/GraphEditor/components/operator-node';
import SqlNode from '@/components/Studio/StudioGraphEdit/GraphEditor/components/sql-node';
import { Parameter } from '@/components/Studio/StudioGraphEdit/GraphEditor/ts-define/parameter';
import { PortManager } from '@antv/x6/es/model/port';
import React from 'react';

enum NodeType {
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
  portItem: { group: string; id: string }[],
) {
  register({
    width: 40,
    height: 25,
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
    const portItem: { group: string; id: string }[] = [];

    portItem.push(
      ...param.ports.inputs.map((item: { id: string }) => ({
        group: 'inputs',
        id: item.id,
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
      case NodeType.MYSQL:
        registerOperatorNode(param.name, ports, SqlNode, portItem);
        break;
      case NodeType.OPERATORS:
        registerOperatorNode(param.name, ports, OperatorNode, portItem);
        break;
      default:
        break;
    }
  });

  registerTextNode();

  // 节点组
  class NodeGroup extends Node {
    private collapsed: boolean = true;

    postprocess() {
      this.toggleCollapse(true);
    }

    isCollapsed() {
      return this.collapsed;
    }

    toggleCollapse(collapsed?: boolean) {
      const target = collapsed == null ? !this.collapsed : collapsed;
      if (target) {
        this.attr('buttonSign', { d: 'M 1 5 9 5 M 5 1 5 9' });
        this.resize(200, 40);
      } else {
        this.attr('buttonSign', { d: 'M 2 5 8 5' });
        this.resize(300, 300);
      }
      this.collapsed = target;
    }
  }

  NodeGroup.config({
    shape: 'rect',
    markup: [
      {
        tagName: 'rect',
        selector: 'body',
      },
      {
        tagName: 'image',
        selector: 'image',
      },
      {
        tagName: 'text',
        selector: 'text',
      },
      {
        tagName: 'g',
        selector: 'buttonGroup',
        children: [
          {
            tagName: 'rect',
            selector: 'button',
            attrs: {
              'pointer-events': 'visiblePainted',
            },
          },
          {
            tagName: 'path',
            selector: 'buttonSign',
            attrs: {
              fill: 'none',
              'pointer-events': 'none',
            },
          },
        ],
      },
    ],
    attrs: {
      body: {
        refWidth: '100%',
        refHeight: '100%',
        strokeWidth: 1,
        fill: '#fde4d7',
        stroke: '#818181',
        rx: 4,
        ry: 4,
      },
      image: {
        'xlink:href':
          'https://gw.alipayobjects.com/mdn/rms_0b51a4/afts/img/A*X4e0TrDsEiIAAAAAAAAAAAAAARQnAQ',
        width: 16,
        height: 16,
        x: 8,
        y: 12,
      },
      text: {
        fontSize: 12,
        fill: 'rgba(0,0,0,0.85)',
        refX: 30,
        refY: 15,
      },
      buttonGroup: {
        refX: '100%',
        refX2: -25,
        refY: 13,
      },
      button: {
        height: 14,
        width: 16,
        rx: 2,
        ry: 2,
        fill: '#f5f5f5',
        stroke: '#ccc',
        cursor: 'pointer',
        event: 'node:collapse',
      },
      buttonSign: {
        refX: 3,
        refY: 2,
        stroke: '#808080',
      },
    },
  });

  Graph.registerNode('package', NodeGroup);
};
