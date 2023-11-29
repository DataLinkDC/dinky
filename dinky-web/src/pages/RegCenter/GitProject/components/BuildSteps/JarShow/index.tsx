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

import JarList from '@/pages/RegCenter/GitProject/components/BuildSteps/JarShow/JarList';
import { GitProject } from '@/types/RegCenter/data';
import React, { useEffect } from 'react';

/** props
 */
type JarShowProps = {
  value: Partial<GitProject>;
  data: any;
};

const JarShow: React.FC<JarShowProps> = (props) => {
  const { data, value } = props;

  /**
   * state
   */
  const [resultData, setResultData] = React.useState<any[]>([]);

  /**
   * Effect
   */
  useEffect(() => {
    setResultData(data);
  }, [data, value]);

  /**
   * render
   */
  return (
    <div style={{ height: '50vh', overflowY: 'auto' }}>
      <JarList projectId={value.id} jarAndClassesList={resultData} />
    </div>
  );
};

export default JarShow;
