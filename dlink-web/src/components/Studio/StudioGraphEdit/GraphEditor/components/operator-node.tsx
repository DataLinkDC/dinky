import React, { memo, FC } from 'react';

import { NodeType } from './cpn-shape';
import BaseNode from './base-node';

const OperatorNode: FC<NodeType> = memo(({ node }: NodeType) => {
  return <BaseNode nt={node} iconPath="icon-operator" />;
});

export default OperatorNode;
