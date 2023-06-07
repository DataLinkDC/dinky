import { memo } from 'react';
import { NodeType } from './cpn-shape';
import BaseNode from './base-node';

const SqlNode = memo((nodeType: NodeType) => {
  return <BaseNode nodeType={nodeType} iconPath="icon-MySQL-icon-02" />;
});

export default SqlNode;
