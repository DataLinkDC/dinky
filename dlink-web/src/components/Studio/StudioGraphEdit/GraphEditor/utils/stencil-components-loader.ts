import { Stencil } from '@antv/x6-plugin-stencil';
import { Graph, Node } from '@antv/x6';
import { Parameter } from '@/components/Studio/StudioGraphEdit/GraphEditor/ts-define/parameter';

/**
 * //注册stencil中的组件
 * @description 加载左侧自定义图形（用来拖拽）
 * @param graph
 * @param stencil
 * @param operatorParameters
 */

export const stencilComponentsLoader = (
  graph: Graph,
  stencil: Stencil,
  operatorParameters: Parameter[],
) => {
  const registeredStenCpn: { cpn: Node<Node.Properties>; cpnName: string }[] = [];
  const groupsName: { [key: string]: string[] } = {};
  //根据算子参数注册stencil组件
  operatorParameters?.forEach((param: Parameter) => {
    const node = graph.createNode({
      shape: param.name,
      width: 70,
      height: 50,
      attrs: {
        body: {
          rx: 7,
          ry: 6,
        },
        text: {
          text: param.name,
          fontSize: 2,
        },
      },
    });

    registeredStenCpn.push({ cpn: node, cpnName: param.name });
    //保存组和节点关系
    const groupParamName = param.group.split('.')[0];
    if (!(groupParamName in groupsName)) {
      groupsName[groupParamName] = [];
    }
    groupsName[groupParamName].push(param.name);
  });

  //文本节点
  const textAreaNode = graph.createNode({
    shape: 'custom-text-node',
  });
  stencil.load([textAreaNode], 'textArea');

  //组节点
  const groupNode = graph.createNode({
    shape: 'package',
    attrs: {
      text: {
        text: 'Group Name',
      },
    },
    data: {},
  });
  stencil.load([groupNode], 'groupNode');

  //算子节点
  Object.entries(groupsName).forEach(([group, groupNames]) => {
    // 每个分组需要加载的组件
    const groupRegisteredCpn: Node<Node.Properties>[] = registeredStenCpn
      .filter(({ cpnName }) => groupNames.includes(cpnName))
      .map(({ cpn }) => cpn);

    stencil.load(groupRegisteredCpn, group);
  });
};
