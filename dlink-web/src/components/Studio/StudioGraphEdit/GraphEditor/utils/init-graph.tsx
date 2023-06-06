import { Cell, Dom, Edge, Graph, Model, Node, Shape } from '@antv/x6';
import loadPlugin from './plugin';

import {
  changeCurrentSelectNode,
  changeCurrentSelectNodeName,
} from '@/components/Studio/StudioGraphEdit/GraphEditor/store/modules/home';
import React from 'react';

/**
 *
 * @param container //画布容器
 * @param selectedNodes //react state selected NODE
 * @param setSelectedNodes //react setSelected
 * @param dispatch
 * @returns
 */
export const initGraph = (
  container: HTMLElement,
  selectedNodes: Node<Node.Properties>[],
  setSelectedNodes: React.Dispatch<React.SetStateAction<Node<Node.Properties>[]>>,
  dispatch: any,
) => {
  const graph = new Graph({
    container,
    //鼠标滚轮
    mousewheel: {
      enabled: true,
      zoomAtMousePosition: true,
      modifiers: 'ctrl',
      minScale: 0.5,
      maxScale: 3,
    },
    connecting: {
      connector: {
        name: 'rounded',
      },
      router: 'manhattan',
      anchor: 'center',
      connectionPoint: 'anchor',
      // 是否允许连接到画布空白位置的点
      allowBlank: false,
      //是否允许在相同的起始节点和终止之间创建多条边
      allowMulti: false,
      // 自动吸附
      snap: {
        radius: 20,
      },
      //连接的过程中创建新的边
      createEdge() {
        return new Shape.Edge({
          attrs: {
            line: {
              stroke: '#b2a2e9',
              strokeWidth: 2,
              targetMarker: {
                name: 'classic',
                size: 10,
              },
            },
          },
        });
      },

      // 在移动边的时候判断连接是否有效，如果返回 false，当鼠标放开的时候，不会连接到当前元素，否则会连接到当前元素
      validateConnection({ sourceMagnet, targetMagnet }) {
        if (!targetMagnet || !sourceMagnet) return false;

        //输入桩限制 (目标桩是输入桩的话则不能连接)
        const inputsPort = targetMagnet.getAttribute('port-group') ?? '';
        if (['inputs'].includes(inputsPort)) {
          //输出桩限制 如果
          const outputsPort = sourceMagnet.getAttribute('port-group') ?? '';
          return ['outputs'].includes(outputsPort);
        }
        return false;
      },
    },
    // 当链接桩可以被链接时，在链接桩外围渲染一个 2px 圆形框
    highlighting: {
      magnetAdsorbed: {
        name: 'stroke',
        args: {
          padding: 4,
          attrs: {
            fill: '#b2a2e9',
            stroke: '#828283',
          },
        },
      },
      embedding: {
        name: 'stroke',
        args: {
          padding: -1,
          rx: 4,
          ry: 4,
          attrs: {
            stroke: '#f6a548',
            strokeWidth: 2,
          },
        },
      },
    },
    //  将一个节点拖动到另一个节点中，使其成为另一节点的子节点
    embedding: {
      enabled: true,
      findParent({ node }) {
        const bbox = node.getBBox();
        return this.getNodes().filter((node) => {
          const data = node.getData<{ parent: boolean }>();
          if (data && data.parent) {
            const targetBBox = node.getBBox();
            return bbox.isIntersectWithRect(targetBBox);
          }
          return false;
        });
      },
    },
    background: {
      //画布背景色
      color: '#fff',
    },
    grid: {
      visible: true,
      type: 'doubleMesh',
      args: [
        {
          // 主网格线颜色
          color: '#eee',
          // 主网格线宽度
          thickness: 0.5,
        },
        {
          // 次网格线颜色
          color: '#ddd',
          // 次网格线宽度
          thickness: 1,
          // 主次网格线间隔
          factor: 4,
        },
      ],
    },
  });

  //加载相关插件
  loadPlugin(graph);

  function showPort(port: Element | SVGElement | null, show: boolean) {
    if (port instanceof SVGElement) {
      port.style.visibility = show ? 'visible' : 'hidden';
    }
  }

  // 控制连接桩显示/隐藏
  const showPorts = (ports: NodeListOf<Element>, show: boolean) => {
    ports.forEach((port) => {
      showPort(port, show);
    });
  };

  function showAllPorts(show: boolean) {
    //显示连接桩
    const ports = container.querySelectorAll('.x6-port-body');
    showPorts(ports, show);
  }

  graph.on('node:mouseenter', ({ cell }) => {
    showAllPorts(true);

    //显示删除按钮
    cell.addTools([
      {
        name: 'button-remove',
        args: {
          x: 0,
          y: 0,
          offset: { x: 0, y: 0 },
        },
      },
    ]);
  });

  graph.on('node:mouseleave', ({ cell }) => {
    showAllPorts(false);

    //移除删除工具
    cell.removeTools();
  });

  const LINE_STOKE_WIDTH = 'line/strokeWidth';
  function showEdgePorts(edge: Edge<Edge.Properties>, show: boolean) {
    const visibility = show ? 'visible' : 'hidden';
    const VISIBILITY_PATH = 'attrs/circle/style/visibility';
    edge.getSourceNode()?.setPortProp(edge.getSourcePortId()!, VISIBILITY_PATH, visibility);
    edge.getTargetNode()?.setPortProp(edge.getTargetPortId()!, VISIBILITY_PATH, visibility);
  }

  graph.on('edge:mouseenter', ({ e, view, edge, cell }) => {
    edge.attr(LINE_STOKE_WIDTH, 4);
    showEdgePorts(edge, true);

    edge.addTools([
      {
        name: 'button-remove',
        args: {
          distance: view.path.length() / 2,
        },
      },
    ]);
  });
  graph.on('edge:mouseleave', ({ e, view, edge, cell }) => {
    edge.setAttrByPath(LINE_STOKE_WIDTH, 2);
    showEdgePorts(edge, false);
    edge.removeTools();
  });

  graph.on('node:selected', ({ node }) => {
    debugger;
    console.log(node, 'selected');

    if (graph.getSelectedCells().length) {
      dispatch(changeCurrentSelectNode(node));
      dispatch(changeCurrentSelectNodeName(node.getAttrs().text.text));

      //深拷贝,数组要改变地址子组件才能监听到变化
      let selectNode = [];
      selectNode.push(node);
      setSelectedNodes([...selectNode]);
    }
  });

  //右键菜单
  graph.on('node:contextmenu', ({ cell, e }) => {
    const p = graph.clientToGraph(e.clientX, e.clientY);
  });

  graph.on('blank:contextmenu', ({ e }) => {
    const p = graph.clientToGraph(e.clientX, e.clientY);
  });

  graph.on('node:collapse', ({ node, e }: any) => {
    node.toggleCollapse();
    const collapsed = node.isCollapsed();
    const collapse = (parent: any) => {
      const cells = parent.getChildren();
      if (cells) {
        cells.forEach((cell: any) => {
          if (collapsed) {
            cell.hide();
          } else {
            cell.show();
          }

          if (cell.shape === 'package') {
            if (!cell.isCollapsed()) {
              collapse(cell);
            }
          }
        });
      }
    };

    collapse(node);
  });

  //群组大小自适应处理
  let ctrlPressed = false;
  graph.on('node:embedding', ({ e }: { e: Dom.MouseMoveEvent }) => {});

  graph.on('node:embedded', ({ node, currentParent }) => {
    ctrlPressed = false;
    //设置父节点zindex小于子节点
    currentParent?.toBack();
  });

  graph.on('node:change:size', ({ node, options }) => {
    if (options.skipParentHandler) {
      return;
    }

    const children = node.getChildren();
    if (children && children.length) {
      node.prop('originSize', node.getSize());
    }
  });

  graph.on('node:change:position', ({ node, options }) => {
    if (options.skipParentHandler || ctrlPressed) {
      return;
    }

    if (node.getChildren()?.length) {
      node.prop('originPosition', node.getPosition());
    }

    const parent = node.getParent();
    if (!parent?.isNode()) {
      return;
    }

    let originSize = parent.prop('originSize');
    if (originSize == null) {
      originSize = parent.getSize();
      parent.prop('originSize', originSize);
    }

    let originPosition = parent.prop('originPosition');
    if (originPosition == null) {
      originPosition = parent.getPosition();
      parent.prop('originPosition', originPosition);
    }

    let x = originPosition.x;
    let y = originPosition.y;
    let cornerX = originPosition.x + originSize.width;
    let cornerY = originPosition.y + originSize.height;
    let hasChange = false;
    const children = parent.getChildren();
    if (children) {
      children.forEach((child) => {
        const bbox = child.getBBox().inflate(10);
        const corner = bbox.getCorner();

        if (bbox.x < x) {
          x = bbox.x;
          hasChange = true;
        }

        if (bbox.y < y) {
          y = bbox.y;
          hasChange = true;
        }

        if (corner.x > cornerX) {
          cornerX = corner.x;
          hasChange = true;
        }

        if (corner.y > cornerY) {
          cornerY = corner.y;
          hasChange = true;
        }
      });
    }

    if (hasChange) {
      parent.prop(
        {
          position: { x, y },
          size: { width: cornerX - x, height: cornerY - y },
        },
        // Note that we also pass a flag so that we know we shouldn't
        // adjust the `originPosition` and `originSize` in our handlers.
        { skipParentHandler: true },
      );
    }
  });

  graph.on('node:resizing', ({ node }) => {
    node.setAttrs({
      image: {
        width: node.size().width,
        height: node.size().height,
      },
    });
  });

  //节点/边被取消选中时触发
  graph.on('cell:unselected', (cell: Cell, options: Model.SetOptions) => {
    selectedNodes.length = 0;
    setSelectedNodes(selectedNodes);
  });

  //节点/边被选中时触发。
  graph.on('cell:selected', (cell: Cell, options: Model.SetOptions) => {
    //节点被选中时隐藏连接桩
    const ports = container.querySelectorAll('.x6-port-body');
    showPorts(ports, false);
  });

  graph.on('cell:added', ({ cell, index, options }) => {
    //更新表格数据
    if (cell.shape === 'package') {
      cell.setZIndex(-1);
    }
    // updateGraphData(graph);
  });
  graph.on('cell:removed', ({ cell, index, options }) => {
    // updateGraphData(graph);
  });
  graph.on('cell:dblclick', ({ cell, e }) => {
    const isNode = cell.isNode();
    const name = cell.isNode() ? 'node-editor' : 'edge-editor';
    cell.removeTool(name);
    cell.addTools({
      name,
      args: {
        event: e,
        attrs: {
          backgroundColor: isNode ? '#EFF4FF' : '#FFF',
        },
      },
    });
  });
  return graph;
};
