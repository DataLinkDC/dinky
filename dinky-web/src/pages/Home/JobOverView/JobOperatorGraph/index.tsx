import { JobProps } from '@/pages/DevOps/JobDetail/data';
import { API_CONSTANTS } from '@/services/endpoints';

const JobOperatorGraph = (props: JobProps) => {
  const { jobDetail } = props;

  const url = `${window.location.origin}/api/flink_web/proxy?_authority=${jobDetail?.history?.jobManagerAddress}&_jid=${jobDetail?.instance?.jid}`

  const onLoad = () => {
    const iframe = document.getElementById('iframe-id') as HTMLIFrameElement;
    if (!iframe) return;
    const innerDoc = iframe.contentWindow?.document;
    if (!innerDoc) return;
    innerDoc.body.style.visibility = 'hidden';
    const flinkJob = innerDoc?.querySelector('flink-root > nz-layout > nz-layout');
    if (!flinkJob) return;

    // @ts-ignore
      const style = flinkJob.style;
      style.visibility = 'visible';
      style.top = '0';
      style.left = '0';
      style.zIndex = 999;
      style.marginLeft = '0px';
      style.marginTop = '0px';
      // style.overflow = 'hidden';
      style.position = 'absolute';
      style.width = iframe.offsetWidth + 'px';
      style.height = iframe.offsetHeight + 'px';
  };

  return (
    <div
      style={{
        border: '0px solid',
        overflow: 'hidden',
        width: '100%',
        height: '100%'
      }}
    >
      <iframe
        id='iframe-id'
        scrolling='no'
        src={url}
        onLoad={() => onLoad()}
        sandbox='allow-same-origin allow-scripts allow-popups allow-forms'
        style={{
          overflow: 'hidden',
          border: '0px none',
          width: '100%',
          height: window.innerHeight
          // marginLeft: '-260px',
          // marginRight: '-220px'
        }}
      />
    </div>
  );
};

export default JobOperatorGraph;
