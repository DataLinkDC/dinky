import { JobMetricsItem } from '@/pages/DevOps/JobDetail/data';
import { DevopsType } from '@/pages/DevOps/JobDetail/model';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { connect, useRequest } from '@@/exports';
import { Transfer } from 'antd';

const MetricsConfigTab = (props: any) => {
  const { vertice, onValueChange, jobDetail, metricsTarget } = props;
  const jobManagerUrl = jobDetail.cluster?.jobManagerHost;
  const jobId = jobDetail.jobHistory.job?.jid;

  const { data } = useRequest({
    url: API_CONSTANTS.GET_JOB_MERTICE_ITEMS,
    params: { address: jobManagerUrl, jobId: jobId, verticeId: vertice.id }
  });

  const targetKeys = (data: []) => data.map((i: JobMetricsItem) => i.metrics);

  return (
    <>
      <Transfer
        showSearch={true}
        dataSource={data ?? []}
        titles={[l('devops.jobinfo.metrics.metricsItems'), l('devops.jobinfo.metrics.selected')]}
        targetKeys={targetKeys(metricsTarget[vertice.id] ?? [])}
        onChange={(tgk) => onValueChange(vertice, tgk)}
        rowKey={(item) => item.id}
        render={(item) => item.id}
        listStyle={{ width: '42vh', height: '50vh' }}
        oneWay
      />
    </>
  );
};

export default connect(({ Devops }: { Devops: DevopsType }) => ({
  jobDetail: Devops.jobInfoDetail,
  metricsTarget: Devops.metrics.jobMetricsTarget,
  layoutName: Devops.metrics.layoutName
}))(MetricsConfigTab);
