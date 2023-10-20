import LineageDag from 'react-lineage-dag';

import * as ReactDOM from "react-dom";
import * as _ from "lodash";
import LineageCanvas from "react-lineage-dag/src/canvas/canvas";
import {transformInitData, transformEdges} from 'react-lineage-dag/src/adaptor';


export default class LineageDagExt extends LineageDag {

  componentDidMount() {
    let root = ReactDOM.findDOMNode(this) as HTMLElement;

    let enableHoverChain = _.get(this.props, 'config.enableHoverChain', true);
    let titleRender = _.get(this.props, 'config.titleRender');

    let canvasObj = {
      root: root,
      disLinkable: false,
      linkable: false,
      draggable: false,
      zoomable: true,
      moveable: true,
      theme: {
        edge: {
          type: 'endpoint',
          shapeType: 'AdvancedBezier',
          arrow: true,
          isExpandWidth: true,
          arrowPosition: 1,
          arrowOffset: -5
        },
        endpoint: {
          limitNum: undefined,
          expandArea: {
            left: 0,
            right: 0,
            top: 0,
            botton: 0
          }
        }
      },
      data: {
        enableHoverChain: enableHoverChain
      }
    };

    this.canvas = new LineageCanvas(canvasObj);

    let result = transformInitData({
      tables: this.props.tables,
      relations: this.props.relations,
      columns: this.props.columns,
      operator: this.props.operator,
      _titleRender: titleRender,
      _enableHoverChain: enableHoverChain,
      _emptyContent: this.props.emptyContent,
      _emptyWidth: this.props.emptyWidth
    });

    this.originEdges = result.edges;

    result = transformEdges(result.nodes, _.cloneDeep(result.edges));
    result.edges = result.edges.map((item) => {
      return {
        ...item,
        shapeType: 'AdvancedBezier',
      }
    });

    this.canvasData = {
      nodes: result.nodes,
      edges: result.edges
    };

    setTimeout(() => {
      let tmpEdges = result.edges;
      result.edges = [];
      // this.canvas.wrapper.style.visibility = 'hidden';
      this.canvas.draw(result, () => {
        this.canvas.relayout({
          edges: tmpEdges.map((item) => {
            return {
              source: item.sourceNode,
              target: item.targetNode
            }
          })
        }, true);
        // this.canvas.wrapper.style.visibility = 'visible';
        debugger;
        this.canvas.addEdges(tmpEdges, true);

        let minimap = _.get(this, 'props.config.minimap', {});

        const minimapCfg = _.assign({}, minimap.config, {
          events: [
            'system.node.click',
            'system.canvas.click'
          ]
        });

        if (minimap && minimap.enable) {
          this.canvas.setMinimap(true, minimapCfg);
        }

        if (_.get(this, 'props.config.gridMode')) {
          this.canvas.setGridMode(true, _.assign({}, _.get(this, 'props.config.gridMode', {})))
        }

        if (result.nodes.length !== 0) {
          this.canvas.focusCenterWithAnimate();
          this._isFirstFocus = true;
        }

        this.forceUpdate();
        this.props.onLoaded && this.props.onLoaded(this.canvas);
      });
      this.canvas.on('system.node.click', (data) => {
        let node = data.node;
        this.canvas.focus(node.id);
      });
      this.canvas.on('system.canvas.click', () => {
        this.canvas.unfocus();
      });
    }, _.get(this.props, 'config.delayDraw', 0));

  }
}
