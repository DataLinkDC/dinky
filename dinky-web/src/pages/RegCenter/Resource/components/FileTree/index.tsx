/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { LoadingBtn } from '@/components/CallBackButton/LoadingBtn';
import { buildResourceTreeData } from '@/pages/RegCenter/Resource/components/FileTree/function';
import { ResourceInfo } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { Divider, Empty, Flex, Tree, Typography } from 'antd';
import React from 'react';

const { DirectoryTree } = Tree;

type FileTreeProps = {
  treeData: ResourceInfo[];
  onNodeClick: (info: any) => void;
  onRightClick: (info: any) => void;
  onSync: () => void;
  selectedKeys: string[];
};

const FileTree: React.FC<FileTreeProps> = (props) => {
  const { treeData, selectedKeys, onNodeClick, onRightClick, onSync } = props;

  return (
    <>
      <Flex justify={'space-between'} align={'center'}>
        <Typography.Text>{l('rc.resource.filelist')}</Typography.Text>
        <LoadingBtn props={{ type: 'link' }} click={onSync} title={l('rc.resource.sync')} />
      </Flex>
      <Divider style={{ margin: '8px' }} />
      {treeData.length > 0 ? (
        <DirectoryTree
          className={'treeList'}
          selectedKeys={selectedKeys}
          onSelect={(_, info) => onNodeClick(info)}
          onRightClick={(info) => onRightClick(info)}
          treeData={buildResourceTreeData(treeData)}
        />
      ) : (
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      )}
    </>
  );
};

export default FileTree;
