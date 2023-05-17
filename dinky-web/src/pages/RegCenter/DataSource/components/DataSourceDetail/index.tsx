/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, {useEffect, useState} from "react";
import {DataSources} from "@/types/RegCenter/data";
import {Loading} from "@/pages/Other/Loading";
import {Button} from "antd";
import {BackwardOutlined} from "@ant-design/icons";
import {DataSourceDetailBackButton} from "@/components/StyledComponents";
import {l} from "@/utils/intl";
import {history} from "@umijs/max";

type DataSourceDetailProps = {
  dataSource: Partial<DataSources.DataSource>;
  backClick: () => void;
}
const DataSourceDetail: React.FC<DataSourceDetailProps> = (props) => {

  const {dataSource, backClick} = props;
  const [loading, setLoading] = useState<boolean>(false);

  const handleBackClick = () => {
    // go back
    history.back();
    // back click callback
    backClick();
  }

  useEffect(() => {
    // todo: fetch data by dataSource.id
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
    }, 1000);
  }, []);

  return <>
    <DataSourceDetailBackButton>
      <Button icon={<BackwardOutlined/>} type="primary" onClick={handleBackClick}>{l("button.back")}</Button>
    </DataSourceDetailBackButton>

    <Loading loading={loading}/>
  </>;
};

export default DataSourceDetail;
