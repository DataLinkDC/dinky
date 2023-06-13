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

import React, {useEffect, useState} from "react";
import {Area, AreaConfig} from "@ant-design/plots";
import {MetricsDataType} from "@/pages/Metrics/Server/data";


type CpuProps = {
    data: MetricsDataType[];
}
type Cpu = {
    time: Date;
    value: string | number;
}
const CPU: React.FC<CpuProps> = (props) => {

    const {data} = props;

    const dataList: Cpu[] = data.map(x => {
        return {time: x.heartTime, value: Number( x.content.jvm.cpuUsed.toFixed(2))};
    })

    useEffect(() => {

    }, []);

    const config: AreaConfig = {
        animation: false,
        data: dataList,
        height: 200,
        yField: 'value',
        xField: 'time',
        xAxis: {
            type: 'time',
            mask: 'HH:mm:ss',
        },
        yAxis:{
          min:0,
          max:100
        },
        // slider: {
        //   start: 0,
        //   end: 1,
        //   trendCfg: {
        //     isArea: true,
        //   },
        // },
    };

    return <Area {...config} />;
}

export default CPU;
