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

import React, {useState, useEffect} from 'react';
import {StatisticCard} from '@ant-design/pro-components';
import RcResizeObserver from 'rc-resize-observer';
import {
    AlertGroupIcon,
    AlertInstanceIcon, ClusterConfigIcon,
    ClusterInstanceIcon,
    DatabaseIcon, GitIcon,
    GlobalVarIcon
} from "@/components/Icons/HomeIcon";
import {DatabaseTwoTone} from "@ant-design/icons";
import {imgStyle} from "@/pages/Home/constants";
import CountFormatter from "@/components/CountFormatter";
import {l} from "@/utils/intl";



const ResourceView = () => {

  const [responsive, setResponsive] = useState(false);

  return (
    <RcResizeObserver
      key="resize-observer"
      onResize={(offset) => {
        setResponsive(offset.width < 296);
      }}
    >
      <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
        <StatisticCard
          statistic={{
            title: l('home.develop.re.ci'),
            value: 2176,
            icon:<ClusterInstanceIcon style={imgStyle}/>,
            formatter: (value)=> <CountFormatter value={Number(value)}/>
          }}
        />
        <StatisticCard
          statistic={{
            title: l('home.develop.re.cc'),
            value: 475,
            icon: <ClusterConfigIcon style={imgStyle}/>,
            formatter: (value)=> <CountFormatter value={Number(value)}/>
          }}
        />
      </StatisticCard.Group>
      <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
        <StatisticCard
          statistic={{
            title: l('home.develop.re.ds'),
            value: 87,
            icon: <DatabaseIcon style={imgStyle}/>,
              formatter: (value)=> <CountFormatter value={Number(value)}/>
          }}
        />
        <StatisticCard
          statistic={{
            title: l('home.develop.re.gv'),
            value: 1754,
            icon: <GlobalVarIcon style={imgStyle}/>,
              formatter: (value)=> <CountFormatter value={Number(value)}/>
          }}
        />
      </StatisticCard.Group>
      <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
        <StatisticCard
          statistic={{
            title: l('home.develop.re.ai'),
            value: 87,
            icon: <AlertInstanceIcon style={imgStyle}/>,
              formatter: (value)=> <CountFormatter value={Number(value)}/>
          }}
        />
        <StatisticCard
          statistic={{
            title: l('home.develop.re.ag'),
            value: 1754,
            icon: <AlertGroupIcon style={imgStyle}/>,
              formatter: (value)=> <CountFormatter value={Number(value)}/>
          }}
        />
      </StatisticCard.Group>
    <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
        <StatisticCard
            statistic={{
                title: l('home.develop.re.git'),
                value: 220,
                icon: <GitIcon style={imgStyle}/>,
                formatter: (value)=> <CountFormatter value={Number(value)}/>
            }}
        />

    </StatisticCard.Group>
    </RcResizeObserver>
  );
};

export default ResourceView
