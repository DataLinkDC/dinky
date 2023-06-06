import { memo } from 'react';
import RightDetail from './cpns/right-detail';
import LeftEditor from './cpns/left-editor';
import styles from './index.less';
const GraphEditor = memo((props) => {
  //获取数据
  return (
    <div className={styles['graph-container']}>
      <LeftEditor />
      <RightDetail />
    </div>
  );
});

export default GraphEditor;
