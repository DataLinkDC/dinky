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

import {ProColumns, ProTable} from "@ant-design/pro-table";
import {Empty, List} from "antd";
import React from "react";
import {CopyTwoTone} from "@ant-design/icons";
import {BuildJarList} from "@/pages/RegCenter/GitProject/components/BuildSteps/JarShow";


type ClassListProps = {
  jarAndClassesList: Partial<BuildJarList[]>;
}


const ClassList: React.FC<ClassListProps> = (props) => {

  const {jarAndClassesList} = props;


  const columns: ProColumns<BuildJarList>[] = [
    {
      title: "Jar Name",
      dataIndex: "jarPath",
      copyable: true,
    }
  ];

  const handleCopyClick = (item: string) => {
    // 创建一个临时的 textarea 元素，并将要复制的文本放到它里面
    const textarea = document.createElement("textarea");
    textarea.value = item;

    // 将 textarea 元素添加到文档中
    document.body.appendChild(textarea);

    // 选中 textarea 中的文本
    textarea.select();

    // 执行复制操作
    document.execCommand("copy");

    // 删除临时的 textarea 元素
    document.body.removeChild(textarea);
  };


  const buildExpandableList = (classList: string[]) => {
    return (
      // if enable show classes then show classes, else show empty
        classList ?
        <>
          {classList.map((item, index) => {
            return <><List.Item key={index}>{item} <CopyTwoTone onClick={() => handleCopyClick(item)}/>
            </List.Item></>;
          })}
        </> :
        <Empty image={Empty.PRESENTED_IMAGE_DEFAULT}/>
    );
  };


  return <>
    <ProTable<BuildJarList>
      style={{height: "50vh", overflowY: "auto", msOverflowY: "hidden"}}
      columns={columns}
      toolBarRender={false}
      showHeader={false}
      dataSource={JSON.parse(JSON.stringify(jarAndClassesList)) as BuildJarList[]}
      search={false}
      rowKey="jarPath"
      pagination={false}
      // expandable={{
      //   expandRowByClick: true,
      //   expandedRowRender: record => {
      //     return <List split className={"child-list"}>{buildExpandableList(record.classList)}</List>;
      //   }
      // }}
    />
  </>;
};

export default ClassList;
