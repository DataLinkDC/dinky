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
import {Empty, List} from "antd";

type JarListProps = {
  jarList: string[];
}

const JarList: React.FC<JarListProps> = (props) => {

  const {jarList} = props;


  const buildExpandableList = () => {
    return (
      // if enable show classes then show classes, else show empty
      jarList.length > 0 ?
        <>
          {jarList.map((item, index) => {
            return <><List.Item key={index}>{item}</List.Item></>;
          })}
        </> :
        <Empty image={Empty.PRESENTED_IMAGE_DEFAULT}/>
    );
  };

  return <>
    <List split className={"child-list"}>{buildExpandableList()}</List>;
  </>;
};

export default JarList;
