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

import CountFormatter from '@/components/CountFormatter';
import {
  AlertGroupIcon,
  AlertInstanceIcon,
  ClusterConfigIcon,
  ClusterInstanceIcon,
  DatabaseIcon,
  GitIcon,
  GlobalVarIcon
} from '@/components/Icons/HomeIcon';
import { imgStyle } from '@/pages/Home/constants';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { ResourceOverView } from '@/types/Home/data';
import { l } from '@/utils/intl';
import { StatisticCard } from '@ant-design/pro-components';
import RcResizeObserver from 'rc-resize-observer';
import { useEffect, useState } from 'react';

const ResourceView = () => {
  const [responsive, setResponsive] = useState(false);

  const [data, setData] = useState<ResourceOverView>();

  useEffect(() => {
    queryDataByParams(API_CONSTANTS.GET_RESOURCE_OVERVIEW).then((res) =>
      setData(res as ResourceOverView)
    );
  }, []);

  return (
    <RcResizeObserver
      key='resize-observer'
      onResize={(offset) => {
        setResponsive(offset.width < 296);
      }}
    >
      <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
        <StatisticCard
          statistic={{
            title: l('home.develop.re.ci'),
            value: data?.flinkClusterCount || 0,
            icon: <ClusterInstanceIcon style={imgStyle} />,
            formatter: (value) => <CountFormatter value={Number(value)} />
          }}
        />
        <StatisticCard
          statistic={{
            title: l('home.develop.re.cc'),
            value: data?.flinkConfigCount || 0,
            icon: <ClusterConfigIcon style={imgStyle} />,
            formatter: (value) => <CountFormatter value={Number(value)} />
          }}
        />
      </StatisticCard.Group>
      <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
        <StatisticCard
          statistic={{
            title: l('home.develop.re.ds'),
            value: data?.dbSourceCount || 0,
            icon: <DatabaseIcon style={imgStyle} />,
            formatter: (value) => <CountFormatter value={Number(value)} />
          }}
        />
        <StatisticCard
          statistic={{
            title: l('home.develop.re.gv'),
            value: data?.globalVarCount || 0,
            icon: <GlobalVarIcon style={imgStyle} />,
            formatter: (value) => <CountFormatter value={Number(value)} />
          }}
        />
      </StatisticCard.Group>
      <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
        <StatisticCard
          statistic={{
            title: l('home.develop.re.ai'),
            value: data?.alertInstanceCount || 0,
            icon: <AlertInstanceIcon style={imgStyle} />,
            formatter: (value) => <CountFormatter value={Number(value)} />
          }}
        />
        <StatisticCard
          statistic={{
            title: l('home.develop.re.ag'),
            value: data?.alertGroupCount || 0,
            icon: <AlertGroupIcon style={imgStyle} />,
            formatter: (value) => <CountFormatter value={Number(value)} />
          }}
        />
      </StatisticCard.Group>
      <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
        <StatisticCard
          statistic={{
            title: l('home.develop.re.git'),
            value: data?.gitProjectCount || 0,
            icon: <GitIcon style={imgStyle} />,
            formatter: (value) => <CountFormatter value={Number(value)} />
          }}
        />
      </StatisticCard.Group>
    </RcResizeObserver>
  );
};

export default ResourceView;
