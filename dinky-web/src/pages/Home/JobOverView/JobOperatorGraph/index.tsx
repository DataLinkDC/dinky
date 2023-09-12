import { JobProps } from '@/pages/DevOps/JobDetail/data';
import { API_CONSTANTS } from '@/services/endpoints';

const JobOperatorGraph = (props: JobProps) => {
  const { jobDetail } = props;
  const onLoad = () => {
    const iframe = document.getElementById('iframe-id') as HTMLIFrameElement;
    if (!iframe) return;
    const innerDoc = iframe.contentWindow?.document;

    const flinkJob = innerDoc?.querySelector('flink-root > nz-layout > nz-layout > nz-content');
    if (!flinkJob) return;
    const style = flinkJob.style;
    style.position = 'relative';
    style.overflow = 'hidden';
  };

  return (
    <div
      style={{
        border: '0px solid',
        overflow: 'hidden',
        marginLeft: '100px',
        width: '100%',
        height: '100%'
      }}
    >
      <iframe
        id='iframe-id'
        scrolling='no'
        src={`http://127.0.0.1:8000${API_CONSTANTS.FLINK_PROXY}/${jobDetail?.history?.jobManagerAddress}/#/job/running/${jobDetail?.instance?.jid}/overview`}
        onLoad={() => onLoad()}
        sandbox='allow-same-origin allow-scripts allow-popups allow-forms'
        style={{
          overflow: 'hidden',
          border: '0px none',
          width: '100%',
          height: window.innerHeight,
          marginLeft: '-260px',
          marginRight: '-220px'
        }}
      />
    </div>
  );
};

export default JobOperatorGraph;
