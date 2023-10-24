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

import { getCurrentTab } from '@/pages/DataStudio/function';
import { StateType, TabsPageSubType } from '@/pages/DataStudio/model';
import ExecuteConfigCommonSql from '@/pages/DataStudio/RightContainer/ExecuteConfig/CommonSql';
import ExecuteConfigFlinkSql from '@/pages/DataStudio/RightContainer/ExecuteConfig/FlinkSql';
import { connect } from 'umi';

const ExecuteConfig = (props: any) => {
  const {
    tabs: { panes, activeKey }
  } = props;
  const current = getCurrentTab(panes, activeKey);
  {
    if (current?.subType === TabsPageSubType.flinkSql) {
      return <ExecuteConfigFlinkSql />;
    } else {
      return <ExecuteConfigCommonSql />;
    }
  }
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs
}))(ExecuteConfig);
