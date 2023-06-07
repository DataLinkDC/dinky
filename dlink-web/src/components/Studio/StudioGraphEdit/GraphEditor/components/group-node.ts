// 节点组
import { Cell, Graph, Node } from '@antv/x6';
import { XFLOW_GROUP_DEFAULT_COLLAPSED_SIZE } from '@/components/Studio/StudioGraphEdit/GraphEditor/utils/constants';

export class GroupNode extends Node {
  private collapsed: boolean = true;

  protected postprocess() {
    this.changeCollapseIcon(true);
  }

  isCollapsed() {
    return this.collapsed;
  }

  changeCollapseIcon(collapsed?: boolean) {
    if (collapsed) {
      this.attr('buttonSign', { d: 'M 1 5 9 5 M 5 1 5 9' });
      this.resize(150, 40);
    } else {
      this.attr('buttonSign', { d: 'M 2 5 8 5' });
      this.resize(200, 200);
    }
  }

  toggleVisibleInner = (cells: Cell[], visible: boolean, graph: Graph) => {
    cells.forEach((cell) => {
      const view = graph.findViewByCell(cell)?.container as HTMLElement;
      view.style.visibility = visible ? 'visible' : 'hidden';
    });
  };

  toggleCollapse(isCollapsed: boolean) {
    // TODO: maybe support recursive
    const PREVIOUS_RELATIVE_POSITION = 'previousRelativePosition';
    const PREVIOUS_SIZE = 'previousSize';

    if (isCollapsed) {
      this.prop(PREVIOUS_SIZE, this.size());
      this.size(XFLOW_GROUP_DEFAULT_COLLAPSED_SIZE);
    } else {
      this.size(this.prop(PREVIOUS_SIZE));
    }

    this.changeCollapseIcon(isCollapsed);

    const gap = 0;
    const graph = this.model?.graph as Graph;
    const children = this.getChildren()?.filter((n) => n.isNode()) as Node[];
    children?.forEach((item) => {
      const innerEdges = graph.getConnectedEdges(item).filter((edge) => {
        return children.includes(edge.getSourceNode()!) && children.includes(edge.getTargetNode()!);
      });

      if (isCollapsed) {
        this.toggleVisibleInner([item, ...innerEdges], false, graph);
        item.prop(PREVIOUS_SIZE, item.size());
        item.prop(PREVIOUS_RELATIVE_POSITION, item.position({ relative: true }));
        item.position(this.position().x + gap, this.position().y + gap);
        item.size({
          width: this.size().width - gap * 2,
          height: this.size().height - gap * 2,
        });
      } else {
        this.toggleVisibleInner([item, ...innerEdges], true, graph);
        const pos = item.prop(PREVIOUS_RELATIVE_POSITION);
        item.position(pos.x, pos.y, { relative: true });
        item.size(item.prop(PREVIOUS_SIZE));
      }
    });
    this.collapsed = !this.collapsed;
  }
}

GroupNode.config({
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
