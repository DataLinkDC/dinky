import React, { memo, FC } from 'react';
import { NodeType } from './cpn-shape';
import BaseNode from './base-node';

const SqlNode: FC<NodeType> = memo(({ node }: NodeType) => {
  return <BaseNode nt={node} iconPath="icon-MySQL-icon-02" />;
});

export default SqlNode;
