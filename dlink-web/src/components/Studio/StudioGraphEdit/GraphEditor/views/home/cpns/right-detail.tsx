import { memo } from 'react';
import Editor from './json-editor';
import styles from './index.less';

const RightDetail = memo(() => {
  return (
    <div className={styles['rightDetail']}>
      <div className={styles['rightDetail-header']}>节点信息</div>
      <div className={styles['rightDetail-content']}>
        <Editor />
      </div>
    </div>
  );
});

export default RightDetail;
