import LineageGraph from '@/components/LineageGraph';
import { getCurrentData, mapDispatchToProps } from '@/pages/DataStudio/function';
import { StateType } from '@/pages/DataStudio/model';
import { getDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { LineageDetailInfo } from '@/types/DevOps/data';
import { l } from '@/utils/intl';
import { connect } from '@umijs/max';
import { Card, Result } from 'antd';
import React, { useEffect } from 'react';

interface StudioLineageParams {
  type: number;
  statementSet: boolean;
  dialect: string;
  databaseId: number;
  statement: string;
  envId: number;
  fragment: boolean;
  variables: any;
}

const Lineage: React.FC<connect> = (props) => {
  const {
    tabs: { panes, activeKey },
    bottomHeight
  } = props;
  const [lineageData, setLineageData] = React.useState<LineageDetailInfo>({
    tables: [],
    relations: []
  });
  const queryLineageData = () => {
    // 组装参数 statementSet type dialect databaseId
    const currentData = getCurrentData(panes, activeKey);
    if (!currentData) return;
    const { type, statementSet, dialect, databaseId, statement, envId, fragment } = currentData;
    const params: StudioLineageParams = {
      type: 1, // todo: 暂时写死 ,后续优化
      dialect: dialect,
      envId: envId,
      fragment: fragment,
      statement: statement,
      statementSet: statementSet,
      databaseId: databaseId,
      variables: {}
    };
    getDataByParams(API_CONSTANTS.STUDIO_GET_LINEAGE, params).then((res) =>
      setLineageData(res as LineageDetailInfo)
    );
  };

  useEffect(() => {
    queryLineageData();
  }, [activeKey]);

  return (
    <>
      <Card hoverable bodyStyle={{ height: bottomHeight - 50 }} style={{ height: bottomHeight }}>
        {lineageData && (lineageData.tables.length !== 0 || lineageData.relations.length !== 0) ? (
          <LineageGraph lineageData={lineageData} refreshCallBack={queryLineageData} />
        ) : (
          <Result
            style={{ height: bottomHeight - 120 }}
            status='warning'
            title={l('lineage.getError')}
          />
        )}
      </Card>
    </>
  );
};

export default connect(
  ({ Studio }: { Studio: StateType }) => ({
    tabs: Studio.tabs,
    bottomHeight: Studio.bottomContainer.height
  }),
  mapDispatchToProps
)(Lineage);
