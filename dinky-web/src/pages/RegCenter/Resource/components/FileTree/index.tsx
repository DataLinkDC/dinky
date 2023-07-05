/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */


import React from "react";
import {buildTreeData} from "@/utils/function";
import {Button, Tree, Typography, Upload} from "antd";
import {UploadOutlined} from "@ant-design/icons";
import {l} from "@/utils/intl";


const {DirectoryTree} = Tree;
const {Text} = Typography;


type FileTreeProps = {
  treeData: Partial<any>[];
  onNodeClick: (info: any) => void
  onRightClick: (info: any) => void
  selectedKeys: string[],
  loadData:({ key, children }: any) => Promise<void>;
}

const FileTree: React.FC<FileTreeProps> = (props) => {

  const {treeData, selectedKeys, onNodeClick, onRightClick,loadData} = props;

  return <>
    {
      (treeData.length > 0) ?
        <DirectoryTree
          loadData={loadData}
          selectedKeys={selectedKeys}
          onSelect={(_, info) => onNodeClick(info)}
          onRightClick={info => onRightClick(info)}
          treeData={buildTreeData(treeData)}
        /> : <>
          <div style={{marginTop: '40vh', marginLeft: '1vw'}}>
            <Upload action="/api/resource/uploadFile?pid=0" directory>
              <Button icon={<UploadOutlined/>}>{l('rc.resource.upload')}</Button>
            </Upload><br/>

          </div>
          <Text className={'needWrap'} type="warning">{l('rc.resource.noResource')}</Text>
        </>

    }
  </>;
}

export default FileTree;
