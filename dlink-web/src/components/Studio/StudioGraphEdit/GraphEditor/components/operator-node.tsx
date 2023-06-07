import { memo } from 'react';

import BaseNode from './base-node';
import { NodeType } from './cpn-shape';

const OperatorNode = memo((nodeType: NodeType) => {
  return <BaseNode nodeType={nodeType} iconPath="icon-operator" />;
});

export default OperatorNode;
