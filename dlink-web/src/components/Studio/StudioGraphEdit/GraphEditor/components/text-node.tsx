import React, { memo, FC } from 'react';

import { Input } from 'antd';
import { NodeType } from './cpn-shape';

const { TextArea } = Input;

const TextNode: FC<NodeType> = memo(({ node }: NodeType) => {
  const onChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    node.setData({ parameters: e.target.value });
  };

  return (
    <>
      <TextArea
        style={{ marginBottom: 24 }}
        onChange={onChange}
        placeholder="input..."
        autoSize={{ minRows: 3, maxRows: 100 }}
      />
    </>
  );
});

export default TextNode;
