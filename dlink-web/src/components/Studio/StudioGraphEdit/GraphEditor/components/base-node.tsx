import { Tooltip } from 'antd';
import styles from './index.less';
import CpnShape, { NodeType } from './cpn-shape';

const BaseNode = (props: { nodeType: NodeType; iconPath: string }) => {
  const {
    nodeType: { node },
    iconPath,
  } = props;
  return (
    <div className={styles['custom-calcu-node']}>
      {node && (
        <Tooltip title={node.shape}>
          <div className={styles['custom-calcu-node-label']}>{node.shape}</div>
        </Tooltip>
      )}
      <div className={styles['custom-calcu-node-svg']}>
        <CpnShape iconPath={iconPath} />
      </div>
    </div>
  );
};

export default BaseNode;
