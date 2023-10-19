import { Badge, Tooltip, Typography } from 'antd';

import {
  LineageDetailInfo,
  LineageRelations,
  LineageTable,
  LineageTableColumn
} from '@/types/DevOps/data.d';
import { l } from '@/utils/intl';
import { SuccessNotification, WarningNotification } from '@/utils/messages';
import {
  CompassOutlined,
  ExpandAltOutlined,
  FullscreenExitOutlined,
  InsertRowAboveOutlined,
  PlusCircleOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import _ from 'lodash';
import React, { useEffect } from 'react';
import 'react-lineage-dag/dist/index.css';
import LineageDagExt from "@/components/LineageGraph/lineage-dag-ext";


interface LineageState {
  lineageData: LineageDetailInfo;
  canvas: any;
  actionMenu: any[];
  tables: ITable[];
  relations: LineageRelations[];
  columns: any[];
  operator: any[];
  centerId: string;
  showMinimap: boolean;
  refresh: boolean;
  expandField: boolean;
  collapseField: boolean;
  expandDownstream: boolean;
  collapseDownstream: boolean;
  expandUpstream: boolean;
  collapseUpstream: boolean;
}

type JobLineageProps = {
  lineageData: LineageDetailInfo;
  refreshCallBack: () => void;
};

type ITable = {
  id: string;
  name: string;
  isCollapse: boolean;
  fields: LineageTableColumn[];
};

const { Text } = Typography;

const InitLineageState: LineageState = {
  lineageData: {
    tables: [],
    relations: []
  },
  canvas: undefined,
  actionMenu: [],
  tables: [],
  relations: [],
  columns: [],
  operator: [],
  centerId: '',
  showMinimap: false,
  refresh: false,
  expandField: false,
  collapseField: false,
  expandDownstream: false,
  collapseDownstream: false,
  expandUpstream: false,
  collapseUpstream: false
};


const LineageGraph: React.FC<JobLineageProps> = (props) => {
  const { lineageData, refreshCallBack } = props;
  const [lineageState, setLineageState] = React.useState<LineageState>(InitLineageState);

  const buildLineageColumns = (data: LineageDetailInfo) => {
    return [
      {
        key: 'id',
        width: '100',
        render: (text: any, record: any, index: number) => {
          return (
            <Badge
              count={index + 1}
              size={'small'}
              color={index < 10 ? 'red' : index < 20 ? 'blue' : index < 30 ? 'gold' : 'cyan'}
            />
          );
        }
      },
      {
        key: 'name',
        primaryKey: true,
        width: '250',
        render: (text: any, record: any, index: number) => {
          return (
            <>
              <InsertRowAboveOutlined /> {text}
            </>
          );
        }
      }
    ];
  };

  const buildLineageTables = (tables: LineageTable[]) => {
    return tables.map((table: LineageTable) => ({
      id: table?.id,
      name: table?.name,
      isCollapse: false,
      fields: table?.columns
    }));
  };


  const buildLineageRelations = (relations: LineageRelations[]) => {
    return relations.map((relation: LineageRelations) => ({
      id: relation?.id,
      srcTableId: relation?.srcTableId,
      tgtTableId: relation?.tgtTableId,
      srcTableColName: relation?.srcTableColName,
      tgtTableColName: relation?.tgtTableColName
    }));
  };


  const handleExpandField = (nodeData: any, tables: ITable[]) => {
    const { isCollapse, id } = nodeData;
    lineageState.tables.filter((item) => item.id === id)
      .forEach((item) => item.isCollapse = isCollapse);

    // todo: 待实现 展开字段
    setLineageState((prevState) => ({ ...prevState, expandField: !prevState.expandField }));
    SuccessNotification(
      lineageState.expandField ? l('lineage.expandField') : l('lineage.expandField')
    );
  };

  const buildActionMenu = (data: ITable[]) => {
    return [
      {
        id: 'expandField',
        name: lineageState.expandField ? l('lineage.expandField') : l('lineage.expandField'),
        icon: (
          <Tooltip title={lineageState.expandField ? l('lineage.expandField') : l('lineage.expandField')}>
            <FullscreenExitOutlined width={300} />
          </Tooltip>
        ),
        onClick: (nodeData: any) => handleExpandField(nodeData, _.clone(data))
      },
      {
        id: 'expandDownstream',
        name: lineageState.expandDownstream
          ? l('lineage.expandDownstream')
          : l('lineage.collapseDownstream'),
        icon: (
          <Tooltip
            title={
              lineageState.expandDownstream
                ? l('lineage.expandDownstream')
                : l('lineage.collapseDownstream')
            }
          >
            <PlusCircleOutlined />
          </Tooltip>
        ),
        onClick: (nodeData: { id: string }) => {
          setLineageState((prevState) => ({
            ...prevState,
            expandDownstream: !prevState.expandDownstream
          }));
          // todo 展开下游
          WarningNotification(
            lineageState.expandDownstream
              ? l('lineage.expandDownstream')
              : l('lineage.collapseDownstream')
          );
        }
      },
      {
        id: 'expandUpstream',
        name: lineageState.expandUpstream
          ? l('lineage.expandUpstream')
          : l('lineage.collapseUpstream'),
        icon: (
          <Tooltip
            title={
              lineageState.expandUpstream
                ? l('lineage.expandUpstream')
                : l('lineage.collapseUpstream')
            }
          >
            <ExpandAltOutlined />
          </Tooltip>
        ),
        onClick: (nodeData: { id: string }) => {
          setLineageState((prevState) => ({
            ...prevState,
            expandUpstream: !prevState.expandUpstream
          }));
          // todo 展开上游
          WarningNotification(
            lineageState.expandUpstream
              ? l('lineage.expandUpstream')
              : l('lineage.collapseUpstream')
          );
        }
      }
    ];
  };

  const renderExtActionButton = () => {
    return [
      {
        key: 'minimap',
        icon: (
          <Tooltip title={lineageState.showMinimap ? l('lineage.showMap') : l('lineage.hideMap')}>
            <CompassOutlined />
          </Tooltip>
        ),
        name: lineageState.showMinimap ? l('lineage.showMap') : l('lineage.hideMap'),
        onClick: (canvas: any) => {
          setLineageState((prevState) => ({
            ...prevState,
            canvas,
            showMinimap: !prevState.showMinimap
          }));
        }
      },
      {
        key: 'refresh',
        icon: (
          <Tooltip title={l('lineage.refresh')}>
            <ReloadOutlined spin={lineageState.refresh} />
          </Tooltip>
        ),
        title: l('lineage.refresh'),
        onClick: (canvas: any) => {
          setLineageState((prevState) => ({ ...prevState, canvas, refresh: true }));
          refreshCallBack();
          setLineageState((prevState) => ({ ...prevState, canvas, refresh: false }));
        }
      }
    ];
  };

  const RenderTitle = (title: string) => {
    return (
      <Text
        title={title}
        strong
        ellipsis
        type={'secondary'}
        onClick={() => {
          lineageState.canvas.nodes.forEach((item: { redrawTitle: () => void }) => {
            item.redrawTitle();
          });
        }}
      >
        <blockquote>{title.toString().split('.')[2] ?? title}</blockquote>
      </Text>
    );
  };

  useEffect(() => {
    if (lineageData) {
      setLineageState((prevState) => ({
        ...prevState,
        lineageData: lineageData,
        tables: buildLineageTables(lineageData.tables),
        relations: buildLineageRelations(lineageData.relations)
      }));
    }
  }, [lineageData, lineageState.refresh]);

  return (
      <LineageDagExt
        tables={buildLineageTables(lineageState.lineageData.tables)}
        relations={buildLineageRelations(lineageState.lineageData.relations)}
        columns={buildLineageColumns(lineageState.lineageData)}
        operator={buildActionMenu(lineageState.tables)}
        centerId={lineageState.centerId}
        onLoaded={(canvas: any) => setLineageState((prevState) => ({ ...prevState, canvas }))}
        onChange={(data: any) =>
          setLineageState((prevState) => ({ ...prevState, centerId: data.id }))
        }
        config={{
          titleRender: (title: string) => RenderTitle(title),
          minimap: { enable: lineageState.showMinimap },
          enableHoverChain: true,
          showActionIcon: true,
          gridMode: {
            isAdsorb: true,
            theme: {
              shapeType: 'line',
              gap: 20,
              lineWidth: 1,
              lineColor: '#e8e8e8',
              circleRadiu: 5,
              circleColor: '#e8e8e8'
            }
          }
          // theme: {
          //   isMouseMoveStopPropagation: true,
          //   autoFixCanvas: {
          //     enable: true,
          //     autoMovePadding: [40, 40, 40, 40],
          //   },
          //   edge: {
          //     type: 'endpoint',
          //     shapeType: 'AdvancedBezier',
          //     hasRadius: true,
          //     isExpandWidth: true,
          //     label: '111',
          //     defaultAnimate: true,
          //     arrowPosition: 1,
          //     arrowOffset: -5
          //   },
          // }
        }}
        butterfly={{
          zoomable: true,
          draggable: true,
          movable: true,
          linkable: true
        }}
        actionMenu={renderExtActionButton()}
      />
  );
};

export default LineageGraph;
