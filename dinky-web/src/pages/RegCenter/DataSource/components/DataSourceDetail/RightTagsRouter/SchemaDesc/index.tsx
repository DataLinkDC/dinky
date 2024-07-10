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

import TableInfo from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SchemaDesc/TableInfo';
import { DataSources } from '@/types/RegCenter/data';
import React from 'react';
import PaimonDesc from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SchemaDesc/PaimonDesc/PaimonDesc';
import { DATA_SOURCE_TYPE } from '@/pages/RegCenter/DataSource/components/constants';
import GeneralJdbcDesc from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SchemaDesc/GeneralJdbcDesc/GeneralJdbcDesc';

const SchemaDesc: React.FC<DataSources.SchemaDescProps> = (props) => {
  const { tableInfo, queryParams } = props;

  const getBottomItems = () => {
    if (tableInfo?.driverType == DATA_SOURCE_TYPE.PAIMON) {
      return <PaimonDesc tableInfo={tableInfo} queryParams={queryParams} />;
    } else {
      return <GeneralJdbcDesc tableInfo={tableInfo} />;
    }
  };

  return (
    <>
      {tableInfo && <TableInfo tableInfo={tableInfo} />}
      {getBottomItems()}
    </>
  );
};

export default SchemaDesc;
