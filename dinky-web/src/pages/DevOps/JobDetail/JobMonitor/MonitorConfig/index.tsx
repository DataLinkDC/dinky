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


import {ModalForm, ProFormDateTimeRangePicker} from "@ant-design/pro-components";
import React from "react";
import {l} from "@/utils/intl";
import {Button, Col, Row} from "antd";
import { Radio } from 'antd';
import MonitorConfigForm from "@/pages/DevOps/JobDetail/JobMonitor/MonitorConfig/components/MonitorConfigForm";
import {Jobs} from "@/types/DevOps/data";

type GlobalFilterProps = {
  startTime: any;
  endTime: any;
  handleDateRadioChange: (e: any) => void;
  handleRangeChange: (e: any) => void;
  jobDetail: Jobs.JobInfoDetail;
}

const MonitorFilter: React.FC<GlobalFilterProps> = (props) => {
  const { startTime, endTime, handleDateRadioChange, handleRangeChange,jobDetail} = props;

  return <Row>
    <Col>
      <Radio.Group defaultValue="a" onChange={handleDateRadioChange}>
        <Radio.Button value="1h">{l('metrics.filter.1hour')}</Radio.Button>
        <Radio.Button value="24h">{l('metrics.filter.1day')}</Radio.Button>
        <Radio.Button value="168h">{l('metrics.filter.1week')}</Radio.Button>
      </Radio.Group>
    </Col>
    <Col span={19}>
      <ProFormDateTimeRangePicker
        name="datetimeRanger"
        allowClear={false}
        initialValue={[startTime, endTime]}
        fieldProps={{
          onChange: handleRangeChange,
          value: [startTime, endTime],
        }}
      />
    </Col>
    <Col>
      <MonitorConfigForm jobDetail={jobDetail}/>
    </Col>
  </Row>;
}

export default MonitorFilter;
