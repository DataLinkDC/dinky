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

import { WEEK_LIST } from '@/pages/Home/constants';
import { G2, Heatmap, HeatmapConfig } from '@ant-design/plots';
import { useEffect, useState } from 'react';

const DevHeatmap = () => {
  const [data, setData] = useState([]);

  useEffect(() => {
    asyncFetch();
  }, []);

  const asyncFetch = () => {
    fetch('https://gw.alipayobjects.com/os/antvdemo/assets/data/github-commit.json')
      .then((response) => response.json())
      .then((json) => setData(json))
      .catch((error) => {
        console.log('fetch data failed', error);
      });
  };
  G2.registerShape('polygon', 'boundary-polygon', {
    draw(cfg, container) {
      const group = container.addGroup();
      const attrs = {
        stroke: '#fff',
        lineWidth: 1,
        fill: cfg.color,
        paht: []
      };
      const points = cfg.points;
      const path = [
        ['M', points[0].x, points[0].y],
        ['L', points[1].x, points[1].y],
        ['L', points[2].x, points[2].y],
        ['L', points[3].x, points[3].y],
        ['Z']
      ]; // @ts-ignore

      attrs.path = this.parsePath(path);
      group.addShape('path', {
        attrs
      });

      if (cfg.data.lastWeek) {
        const linePath = [
          ['M', points[2].x, points[2].y],
          ['L', points[3].x, points[3].y]
        ]; // 最后一周的多边形添加右侧边框

        group.addShape('path', {
          attrs: {
            path: this.parsePath(linePath),
            lineWidth: 4,
            stroke: '#404040'
          }
        });

        if (cfg.data.lastDay) {
          group.addShape('path', {
            attrs: {
              path: this.parsePath([
                ['M', points[1].x, points[1].y],
                ['L', points[2].x, points[2].y]
              ]),
              lineWidth: 4,
              stroke: '#404040'
            }
          });
        }
      }

      return group;
    }
  });
  const config: HeatmapConfig = {
    data,
    height: 200,
    autoFit: false,
    xField: 'week',
    yField: 'day',
    colorField: 'commits',
    reflect: 'y',
    shape: 'boundary-polygon',
    meta: {
      day: {
        type: 'cat',
        values: WEEK_LIST
      },
      week: {
        type: 'cat'
      },
      commits: {
        sync: true
      },
      date: {
        type: 'cat'
      }
    },
    yAxis: {
      grid: null
    },
    tooltip: {
      title: 'date',
      showMarkers: false
    },
    interactions: [
      {
        type: 'element-active'
      }
    ],
    xAxis: {
      position: 'top',
      tickLine: null,
      line: null,
      label: {
        offset: 12,
        style: {
          fontSize: 12,
          fill: '#666',
          textBaseline: 'top'
        },
        formatter: (val) => {
          if (val === '2') {
            return 'MAY';
          } else if (val === '6') {
            return 'JUN';
          } else if (val === '10') {
            return 'JUL';
          } else if (val === '15') {
            return 'AUG';
          } else if (val === '19') {
            return 'SEP';
          } else if (val === '24') {
            return 'OCT';
          }

          return '';
        }
      }
    }
  };

  return <Heatmap {...config} />;
};

export default DevHeatmap;
