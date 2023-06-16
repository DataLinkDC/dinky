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


import React, {useState} from "react";
import Server from "@/pages/Metrics/Server";
import {PageContainer, ProCard} from "@ant-design/pro-components";
import Job from "./Job";
import {AreaOptions as G2plotConfig} from "@antv/g2plot/lib/plots/area/types";
import {Button, Input} from "antd";

const commonChartConfig: G2plotConfig = {
  data: [],
  autoFit: false,
  animation: false,
  height: 150,
}
export default () => {

  return <PageContainer title={false}>
    <ProCard collapsible title={'Dinky Server'} ghost hoverable bordered headerBordered>
      <Server chartConfig={commonChartConfig}/>
    </ProCard>

    <Job/>
  </PageContainer>;
}
