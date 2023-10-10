import LineageGraph from '@/components/LineageGraph';
import { DevopsType } from '@/pages/DevOps/JobDetail/model';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { LineageDetailInfo } from '@/types/DevOps/data';
import { l } from '@/utils/intl';
import { connect } from '@umijs/max';
import { Card, Result } from 'antd';
import React, { useEffect } from 'react';
import 'react-lineage-dag/dist/index.css';

const JobLineage: React.FC<connect> = (props) => {
  const {
    jobDetail: { id: jobInstanceId }
  } = props;

  const [lineageData, setLineageData] = React.useState<LineageDetailInfo>({
    tables: [],
    relations: []
  });
  const queryLineageData = () => {
    queryDataByParams(API_CONSTANTS.JOB_INSTANCE_GET_LINEAGE, { id: jobInstanceId }).then((res) =>
      setLineageData(res as LineageDetailInfo)
    );
  };

  useEffect(() => {
    queryLineageData();
  }, [jobInstanceId]);

  return (
    <>
      <Card hoverable bodyStyle={{ height: '100%' }} style={{ height: parent.innerHeight - 180 }}>
        {lineageData && (lineageData.tables.length !== 0 || lineageData.relations.length !== 0) ? (
          <LineageGraph lineageData={lineageData} refreshCallBack={queryLineageData} />
        ) : (
          <Result style={{ height: '100%' }} status='warning' title={l('lineage.getError')} />
        )}
      </Card>
    </>
  );
};

export default connect(({ Devops }: { Devops: DevopsType }) => ({
  jobDetail: Devops.jobInfoDetail
}))(JobLineage);
