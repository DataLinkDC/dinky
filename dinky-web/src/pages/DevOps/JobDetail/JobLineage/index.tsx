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

import LineageGraph from '@/components/LineageGraph';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { LineageDetailInfo } from '@/types/DevOps/data';
import { l } from '@/utils/intl';
import { connect } from '@umijs/max';
import { Card, Result } from 'antd';
import React, { useEffect } from 'react';
import 'react-lineage-dag/dist/index.css';

const JobLineage: React.FC<connect> = (props) => {
  const {
    jobDetail: { id: jobInstanceId }
  } = props;

  const [lineageData, setLineageData] = React.useState<LineageDetailInfo>({
    tables: [],
    relations: []
  });
  const queryLineageData = () => {
    queryDataByParams(API_CONSTANTS.JOB_INSTANCE_GET_LINEAGE, { id: jobInstanceId }).then((res) =>
      setLineageData(res as LineageDetailInfo)
    );
  };

  useEffect(() => {
    queryLineageData();
  }, [jobInstanceId]);

  return (
    <>
      <Card hoverable bodyStyle={{ height: '100%' }} style={{ height: parent.innerHeight - 180 }}>
        {lineageData && (lineageData.tables.length !== 0 || lineageData.relations.length !== 0) ? (
          <LineageGraph lineageData={lineageData} refreshCallBack={queryLineageData} />
        ) : (
          <Result style={{ height: '100%' }} status='warning' title={l('lineage.getError')} />
        )}
      </Card>
    </>
  );
};

export default JobLineage;
