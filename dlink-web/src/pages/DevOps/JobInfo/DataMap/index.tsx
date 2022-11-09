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


import {Empty, Tabs} from 'antd';
import {getLineage} from "@/pages/DevOps/service";
import {useEffect, useState} from "react";
import Lineage from "@/components/Lineage";
import {l} from "@/utils/intl";

const {TabPane} = Tabs;

const DataMap = (props: any) => {

  const {job} = props;

  const [data, setData] = useState(undefined);

  const getData = () => {
    setData(undefined);
    const res = getLineage(job.instance?.id);
    res.then((result) => {
      result.datas?.tables.forEach(table => {
        table.isExpand = true;
        table.isFold = false;
      });
      setData(result.datas);
    });
  };

  useEffect(() => {
    getData();
  }, []);

  return (<>
    <Tabs defaultActiveKey="Lineage" size="small" tabPosition="top" style={{
      border: "1px solid #f0f0f0"
    }}>
      <TabPane tab={<span>{l('pages.devops.jobinfo.lineage')}</span>} key="Lineage">
        {data ? <Lineage datas={data}/> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
      </TabPane>
    </Tabs>
  </>)
};

export default DataMap;
