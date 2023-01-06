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


import {postAll} from "@/components/Common/crud";
import {message} from "antd";
import {ClusterConfigurationTableListItem} from "@/pages/RegistrationCenter/data";
import {l} from "@/utils/intl";

export async function testClusterConfigurationConnect(clusterConfiguration: ClusterConfigurationTableListItem) {
  const hide = message.loading(l('app.request.test.connection'));
  try {
    const {code,msg} = await postAll('/api/clusterConfiguration/testConnect',clusterConfiguration);
    hide();
    code==0?message.success(msg):message.error(msg);
  } catch (error) {
    hide();
    message.error(l('app.request.failed'));
  }
}
