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


import {Descriptions, Tag, Typography} from 'antd';
import {Link} from 'umi';
import {Jobs} from "@/types/DevOps/data";
import {l} from '@/utils/intl';
import {ProCard} from "@ant-design/pro-components";
import {TagJobStatus} from "@/pages/DevOps/function";
import {RocketOutlined} from "@ant-design/icons";
import { parseSecondStr } from '@/utils/function';
import JobDesc from "@/pages/DevOps/JobDetail/JobOverview/components/JobDesc";
import FlinkTable from "@/pages/DevOps/JobDetail/JobOverview/components/FlinkTable";

const {Text,Paragraph } = Typography;

type JobProps = {
  jobDetail: Jobs.JobInfoDetail;
};

/**
 * Renders the JobConfigTab component.
 *
 * @param {JobProps} props - The component props containing the job detail.
 * @returns {JSX.Element} - The rendered JobConfigTab component.
 */
const JobConfigTab = (props: JobProps) => {

  const {jobDetail} = props;

  return <>
    <JobDesc jobDetail={jobDetail}/>
    <FlinkTable jobDetail={jobDetail}/>
  </>
};

export default JobConfigTab;
