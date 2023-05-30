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

import {DataSources} from '@/types/RegCenter/data';
import {Empty} from 'antd';
import React from 'react';
import TableInfo from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SchemaDesc/TableInfo';
import ColumnInfo from './ColumnInfo/Index';
import {l} from '@/utils/intl';
import {Height80VHDiv} from '@/components/StyledComponents';

type SchemaDescProps = {
  tableInfo: Partial<DataSources.Table>;
  tableColumns: Partial<DataSources.Column[]>;
}

const SchemaDesc: React.FC<SchemaDescProps> = (props) => {
  const {tableInfo, tableColumns} = props;

  return <Height80VHDiv>
    {!(Object.keys(tableInfo).length === 0 && tableColumns.length === 0) ?
      <>
        <TableInfo tableInfo={tableInfo}/>
        <ColumnInfo columnInfo={tableColumns}/>
      </>
      : <Empty className={'code-content-empty'} description={l('rc.ds.detail.tips')}/>
    }
  </Height80VHDiv>;
};

export default SchemaDesc;
