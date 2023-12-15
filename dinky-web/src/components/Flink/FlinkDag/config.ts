/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { DagreLayoutOptions } from '@antv/layout/lib/layout/types';
import { Platform } from '@antv/x6';
import { Options } from '@antv/x6/lib/graph/options';
import Connecting = Options.Connecting;
import Manual = Options.Manual;

export const edgeConfig = {
  markup: [
    {
      tagName: 'path',
      selector: 'wrap',
      attrs: {
        fill: 'none',
        cursor: 'pointer',
        stroke: 'transparent',
        strokeLinecap: 'round'
      }
    },
    {
      tagName: 'path',
      selector: 'line',
      attrs: {
        fill: 'none',
        pointerEvents: 'none'
      }
    }
  ],
  connector: { name: 'curveConnector' },
  attrs: {
    wrap: {
      connection: true,
      strokeWidth: 1,
      strokeLinejoin: 'round'
    },
    line: {
      connection: true,
      stroke: '#A2B1C3',
      strokeWidth: 1,
      targetMarker: {
        name: 'classic',
        size: 6
      }
    },
    text: {
      fontSize: 12
    },
    rect: {
      // fill: 'transparent',
    }
  }
};

export const portConfig = {
  groups: {
    in: {
      position: 'left',
      attrs: {
        circle: {
          r: 4,
          magnet: true,
          stroke: 'transparent',
          strokeWidth: 1,
          fill: 'transparent'
        }
      }
    },

    out: {
      position: {
        name: 'right',
        args: {
          dx: -32
        }
      },

      attrs: {
        circle: {
          r: 4,
          magnet: true,
          stroke: 'transparent',
          strokeWidth: 1,
          fill: 'transparent'
        }
      }
    }
  }
};

export const graphConnectConfig: Partial<Connecting> = {
  snap: true,
  allowBlank: false,
  allowLoop: false,
  highlight: true,
  sourceAnchor: {
    name: 'left',
    args: {
      dx: Platform.IS_SAFARI ? 4 : 8
    }
  },
  targetAnchor: {
    name: 'right',
    args: {
      dx: Platform.IS_SAFARI ? 4 : -8
    }
  },
  // Connection pile verification
  validateConnection({ sourceMagnet, targetMagnet }) {
    // Connections can only be created from output link stubs
    if (!sourceMagnet || sourceMagnet.getAttribute('port-group') === 'in') {
      return false;
    }
    // You can only connect to input link stubs
    return !(!targetMagnet || targetMagnet.getAttribute('port-group') !== 'in');
  }
};

export const graphConfig: Partial<Manual> = {
  // The canvas can be moved using the mouse
  panning: {
    enabled: true,
    eventTypes: ['leftMouseDown', 'mouseWheel']
  },
  // Moving nodes beyond the canvas is prohibited
  translating: {
    restrict: true
  },
  // Prohibit moving nodes
  interacting: {
    nodeMovable: false
  },
  // Control scaling via CTRL
  mousewheel: {
    enabled: true,
    modifiers: 'ctrl',
    factor: 1.1,
    maxScale: 1.5,
    minScale: 0.5
  },
  connecting: graphConnectConfig
};

export const layoutConfig: DagreLayoutOptions = {
  type: 'dagre',
  rankdir: 'LR',
  align: 'UL',
  ranksep: 120,
  nodesep: 40,
  controlPoints: true
};

export const zoomOptions = {
  padding: {
    left: 50,
    right: 50,
    top: 50,
    bottom: 50
  }
};
