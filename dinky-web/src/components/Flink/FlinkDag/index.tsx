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

import DagDataNode from '@/components/Flink/FlinkDag/component/DagDataNode';
import DagPlanNode from '@/components/Flink/FlinkDag/component/DagPlanNode';
import {
  edgeConfig,
  graphConfig,
  layoutConfig,
  portConfig,
  zoomOptions
} from '@/components/Flink/FlinkDag/config';
import { buildDag, regConnect, updateDag } from '@/components/Flink/FlinkDag/functions';
import EllipsisMiddle from '@/components/Typography/EllipsisMiddle';
import { getDataByParamsReturnResult } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs } from '@/types/DevOps/data';
import { DagreLayout } from '@antv/layout';
import { Edge, Graph } from '@antv/x6';
import { Rectangle } from '@antv/x6-geometry';
import { Selection } from '@antv/x6-plugin-selection';
import { register } from '@antv/x6-react-shape';
import { Drawer, Select, Slider, Table, Tabs, TabsProps, Tag, Typography } from 'antd';
import { useEffect, useRef, useState } from 'react';
import './index.css';

export type DagProps = {
  job: Jobs.Job;
  onlyPlan?: boolean;
  checkPoints?: any;
};
const { Paragraph } = Typography;

const RenderCheckpoint = (id: string, checkPoints: any) => {
  const [selectPath, setSelectPath] = useState<string>('');
  const key = id + selectPath;
  const [itemChildren, setItemChildren] = useState({ [key]: [] as TabsProps['items'] });

  const checkpointArray = ((checkPoints?.history ?? []) as any[])
    .filter((x) => x.status === 'COMPLETED')
    .map((x) => {
      return { checkpointType: x.checkpoint_type, path: x.external_path, id: x.id };
    });

  useEffect(() => {
    if (!(selectPath && id)) {
      return;
    }

    if (itemChildren[key]) {
      return;
    }

    getDataByParamsReturnResult(API_CONSTANTS.READ_CHECKPOINT, {
      path: selectPath,
      operatorId: id
    }).then((res) => {
      if (!res || res.code !== 0) {
        return;
      }
      const genData = Object.keys(res.data).map((x) => {
        const datum = res.data[x];
        return {
          key: x,
          label: x,
          children: (
            <Tabs
              items={Object.keys(datum).map((y) => {
                return {
                  key: y,
                  label: y,
                  children: (
                    <Table
                      dataSource={datum[y].data}
                      columns={(datum[y].headers as string[]).map((z) => {
                        return {
                          title: z,
                          dataIndex: z,
                          key: z,
                          render: (text) => (
                            <Paragraph copyable ellipsis={{ rows: 3 }}>
                              {text}
                            </Paragraph>
                          )
                        };
                      })}
                    />
                  )
                };
              })}
              tabBarStyle={{ marginBlock: 0 }}
              tabBarGutter={10}
            />
          )
        };
      });
      setItemChildren({ ...itemChildren, [key]: genData });
    });
  }, [selectPath, id]);

  return (
    <>
      <Select
        defaultValue={selectPath}
        style={{ width: '100%' }}
        placeholder='Select a Checkpoint'
        optionFilterProp='children'
        options={checkpointArray.map((x) => {
          return {
            label: (
              <>
                <Tag color='success'>CheckPoint Id: {x.id}</Tag>
                <Tag color='processing'>
                  CheckPoint Path:<EllipsisMiddle maxCount={40}>{x.path}</EllipsisMiddle>
                </Tag>
                <Tag color='processing'>Type:{x.checkpointType}</Tag>
              </>
            ),
            value: x.path
          };
        })}
        onChange={(path) => {
          setSelectPath(path);
        }}
      />

      <Tabs items={itemChildren[key]} tabBarStyle={{ marginBlock: 0 }} tabBarGutter={10} />
    </>
  );
};

const FlinkDag = (props: DagProps) => {
  const container = useRef(null);

  const { job, onlyPlan = false, checkPoints = {} } = props;

  const [graph, setGraph] = useState<Graph>();
  const [currentJob, setCurrentJob] = useState<string>();
  const [currentSelect, setCurrentSelect] = useState<any>();
  const [open, setOpen] = useState(false);
  const [zoom, setZoom] = useState<number>(1);
  let originPosition = {
    zoom: 1
  };

  const handleClose = () => {
    setOpen(false);
    setCurrentSelect(undefined);
    graph?.zoomToFit(zoomOptions);
    graph?.centerContent();
  };

  const initListen = (graph: Graph) => {
    graph.on('node:selected', ({ cell }) => {
      if (onlyPlan) {
        return;
      }

      setOpen(true);
      setZoom((oldValue) => {
        originPosition = { zoom: oldValue };
        return 1;
      });
      graph.zoomTo(1);
      setCurrentSelect(cell);
      graph.positionPoint(Rectangle.create(cell.getBBox()).getLeftMiddle(), '10%', '50%');
    });

    graph.on('node:unselected', () => {
      setZoom(originPosition.zoom);
      handleClose();
    });
  };

  const initGraph = (flinkData: any) => {
    register({
      shape: 'data-processing-dag-node',
      width: 270,
      height: 140,
      component: onlyPlan ? DagPlanNode : DagDataNode,
      ports: portConfig
    });

    Edge.config(edgeConfig);
    Graph.registerConnector('curveConnector', regConnect, true);
    Graph.registerEdge('data-processing-curve', Edge, true);

    const graph: Graph = new Graph({
      // @ts-ignore
      container: container.current,
      ...graphConfig
    });

    graph.use(
      new Selection({
        enabled: true,
        multiple: false,
        rubberband: false,
        showNodeSelectionBox: true
      })
    );

    // Adaptive layout
    const model = new DagreLayout(layoutConfig).layout(flinkData);
    graph.fromJSON(model);

    // Automatically zoom to fit
    graph.zoomToFit(zoomOptions);
    graph.on('scale', ({ sx }) => setZoom(sx));
    graph.centerContent();
    graph?.zoomTo(zoom);
    updateDag(job?.vertices, graph);
    initListen(graph);
    return graph;
  };

  useEffect(() => {
    const flinkData = buildDag(job?.plan);
    // Clean up old data
    if (graph) {
      graph.clearCells();
    }
    setGraph(initGraph(flinkData));
    setZoom(1 / flinkData.nodes.length + 0.5);
  }, [currentJob]);

  useEffect(() => {
    updateDag(job?.vertices, graph);
    if (currentJob != job?.jid) {
      setCurrentJob(job?.jid);
    }
  }, [job]);

  useEffect(() => {
    graph?.zoomTo(zoom);
  }, [zoom]);

  return (
    <span>
      <div
        style={{
          height: 200,
          position: 'absolute',
          top: '50%',
          right: 12,
          marginTop: -100,
          zIndex: 2
        }}
      >
        <Slider
          vertical
          value={zoom}
          min={0.5}
          max={1.5}
          tooltip={{ open: false }}
          step={0.01}
          onChange={setZoom}
        />
      </div>
      <div style={{ height: '100%', width: '100%' }} ref={container} />
      <Drawer
        headerStyle={{ paddingBlock: 5 }}
        bodyStyle={{ paddingBlock: 5 }}
        open={open}
        getContainer={false}
        width={'65%'}
        mask={false}
        onClose={handleClose}
        destroyOnClose={true}
        closable={false}
      >
        {onlyPlan ? (
          <></>
        ) : (
          <Tabs
            defaultActiveKey='1'
            items={[
              {
                key: '1',
                label: 'Detail',
                children: (
                  <div style={{ whiteSpace: 'pre' }}>
                    {currentSelect?.getData().description?.replaceAll('<br/>', '\n')}
                  </div>
                )
              },
              {
                key: '2',
                label: 'CheckPointRead',
                children: RenderCheckpoint(currentSelect?.id, checkPoints)
              }
            ]}
            tabBarGutter={10}
          />
        )}
      </Drawer>
    </span>
  );
};

export default FlinkDag;
