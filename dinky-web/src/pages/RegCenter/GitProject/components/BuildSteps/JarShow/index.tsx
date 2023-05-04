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
import JarList from "@/pages/RegCenter/GitProject/components/BuildSteps/JarShow/JarList";
import ClassList from "@/pages/RegCenter/GitProject/components/BuildSteps/JarShow/ClassList";


export type BuildJarList = {
  jarPath: string;
  classList: string[];
}

const jarData: string[] = [
  "a.jar",
  "2.jar",
  "a32.jar",
  "a432.jar",
  "a3jkahj.jar",
];


const jarAndClassesData: BuildJarList[] = [
  {
    jarPath: "55sajsa.jar",
    classList: [
      "org.dinky.controller.StudioController",
      "org.dinky.controller.StudioController",
      "org.dinky.controller.StudioController",
      "org.dinky.controller.StudioController",
    ],
  },
  {
    jarPath: "45456.jar",
    classList: [
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
    ],
  },
  {
    jarPath: "gsdfsaad.jar",
    classList: [
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
    ],
  },
  {
    jarPath: "rwrtewqrwqewq.jar",
    classList: [
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
    ],
  },
  {
    jarPath: "tgerwtwere.jar",
    classList: [
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
    ],
  },
  {
    jarPath: "dsdffe.jar",
    classList: [
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
    ],
  },
  {
    jarPath: "sasasa.jar",
    classList: [
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
      "org.dinky.controller.udf.UDFController",
    ],
  },

];


type JarShowProps = {
  step: number
  jarClassesData?: BuildJarList[]
  jarListData?: string[]
}

const JarShow: React.FC<JarShowProps> = (props) => {

  const {step,jarListData,jarClassesData} = props;

  return <>
    {
      step === 4 ?
        <JarList jarList={jarData}/> :
        step === 5 ? <ClassList jarList={jarAndClassesData}/> : <Empty image={Empty.PRESENTED_IMAGE_DEFAULT}/>
    }
  </>;
};

export default JarShow;
