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

import { StatusTagProps } from '@/components/JobTags/data';
import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import { l } from '@/utils/intl';
import { CameraOutlined, CloseCircleOutlined, EditOutlined } from '@ant-design/icons';
import { Tag } from 'antd';

/**
 * Renders a tag for the job life cycle based on the provided step.
 *
 * @returns {JSX.Element} - The tag representing the job life cycle.
 * @param props
 */
const JobLifeCycleTag = (props: StatusTagProps) => {
  const { status, animation = true, bordered = true } = props;

  const buildParam = () => {
    switch (status) {
      case JOB_LIFE_CYCLE.DEVELOP:
        return {
          icon: <EditOutlined />,
          color: 'default',
          text: l('global.table.lifecycle.dev')
        };
      case JOB_LIFE_CYCLE.PUBLISH:
        return {
          icon: <CameraOutlined />,
          color: 'green',
          text: l('global.table.lifecycle.publish')
        };
      default:
        return {
          icon: <CloseCircleOutlined />,
          color: 'default',
          text: status
        };
    }
  };

  const param = buildParam();
  return (
    <Tag icon={animation ? param.icon : undefined} color={param.color} bordered={bordered}>
      {param.text}
    </Tag>
  );
};

export default JobLifeCycleTag;
