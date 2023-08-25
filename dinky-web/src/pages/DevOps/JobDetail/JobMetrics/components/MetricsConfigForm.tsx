import { JobMetricsItem } from '@/pages/DevOps/JobDetail/data';
import MonitorConfigTab from '@/pages/DevOps/JobDetail/JobMetrics/components/MetricsConfigTab/MetricsConfigTab';
import { putMetricsLayout } from '@/pages/DevOps/JobDetail/JobMetrics/service';
import { DevopsType } from '@/pages/DevOps/JobDetail/model';
import { l } from '@/utils/intl';
import { ModalForm } from '@ant-design/pro-components';
import { Button, Tabs } from 'antd';
import { connect } from 'umi';

const MetricsConfigForm = (props: any) => {
  const { dispatch, jobDetail, metricsTarget, layoutName } = props;

  const saveJobMetrics = async () => {
    let params: JobMetricsItem[] = [];
    for (let value of Object.values(metricsTarget)) {
      // @ts-ignore
      params.push(...value);
    }
    await putMetricsLayout(layoutName, params);
    dispatch({
      type: 'Devops/queryMetricsTarget',
      payload: { layoutName: layoutName }
    });
    return true;
  };

  const onValueChange = (vertice: any, keys: string[]) => {
    const data = keys.map((key) => ({
      taskId: jobDetail.history.taskId,
      vertices: vertice.id,
      metrics: key,
      title: vertice.name,
      layoutName: layoutName,
      showType: 'Chart',
      showSize: '25%'
    }));

    dispatch({
      type: 'Devops/updateMetricsTarget',
      payload: { verticeId: vertice.id, data: data }
    });
  };

  const itemTabs = jobDetail?.jobHistory?.job?.vertices?.map((item: any) => {
    return {
      key: item.id,
      label: item.name,
      children: <MonitorConfigTab vertice={item} onValueChange={onValueChange} />
    };
  });

  return (
    <ModalForm
      width={1000}
      layout={'horizontal'}
      title={l('devops.jobinfo.metrics.configMetrics')}
      trigger={<Button type='primary'>{l('devops.jobinfo.metrics.configMetrics')}</Button>}
      onFinish={async () => await saveJobMetrics()}
    >
      <Tabs items={itemTabs} />
    </ModalForm>
  );
};

export default connect(({ Devops }: { Devops: DevopsType }) => ({
  jobDetail: Devops.jobInfoDetail,
  metricsTarget: Devops.metrics.jobMetricsTarget,
  layoutName: Devops.metrics.layoutName
}))(MetricsConfigForm);
