import { Graph } from '@antv/x6';
import { Clipboard } from '@antv/x6-plugin-clipboard';
import { Selection } from '@antv/x6-plugin-selection';
import { Keyboard } from '@antv/x6-plugin-keyboard';
import { Snapline } from '@antv/x6-plugin-snapline';
import { History } from '@antv/x6-plugin-history';
import { Scroller } from '@antv/x6-plugin-scroller';
import { Transform } from '@antv/x6-plugin-transform';
import { Export } from '@antv/x6-plugin-export';

//复制粘贴
export const clipboard = (graph: Graph) => {
  graph.use(
    new Selection({
      enabled: true,
      multiple: true,
      rubberband: true,
      movable: true,
      showNodeSelectionBox: true,
      pointerEvents: 'none',
      showEdgeSelectionBox: true,
    }),
  );

  graph.use(
    new Clipboard({
      enabled: true,
    }),
  );
};

//对齐
export const snapLine = (graph: Graph) => {
  graph.use(
    new Snapline({
      enabled: true,
    }),
  );
};

export const keyboard = (graph: Graph) => {
  graph.use(
    new Keyboard({
      enabled: true,
      global: true,
    }),
  );

  graph.bindKey(['meta+c', 'ctrl+c'], () => {
    const cells = graph.getSelectedCells();
    if (cells.length) {
      graph.copy(cells);
    }
    return false;
  });

  graph.bindKey(['meta+x', 'ctrl+x'], () => {
    const cells = graph.getSelectedCells();
    if (cells.length) {
      graph.cut(cells);
    }
    return false;
  });

  graph.bindKey(['meta+v', 'ctrl+v'], () => {
    if (!graph.isClipboardEmpty()) {
      const cells = graph.paste({ offset: 32 });
      graph.cleanSelection();
      graph.select(cells);
    }
    return false;
  });

  //delete
  graph.bindKey(['backspace', 'delete'], () => {
    const cells = graph.getSelectedCells();
    if (cells.length) {
      graph.removeCells(cells);
    }
  });
};

export const history = (graph: Graph) => {
  graph.use(
    new History({
      enabled: true,
    }),
  );
  //undo redo
  graph.bindKey(['meta+z', 'ctrl+z'], () => {
    if (graph.canUndo()) {
      graph.undo();
    }
    return false;
  });

  graph.bindKey(['meta+y', 'ctrl+y'], () => {
    graph.redo();
    return false;
  });
};
export const scroller = (graph: Graph) => {
  graph.use(
    new Scroller({
      enabled: true,
    }),
  );
  graph.centerContent();
};

export const transform = (graph: Graph) => {
  graph.use(
    new Transform({
      resizing: {
        enabled: true,
      },
    }),
  );
  graph.centerContent();
};

export const exportGraph = (graph: Graph) => {
  graph.use(new Export());
};

export default function loadPlugin(graph: Graph) {
  exportGraph(graph);
  clipboard(graph);
  snapLine(graph);
  keyboard(graph);
  history(graph);
}
