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

import { TaskDataType } from '@/pages/DataStudio/model';
import { postAll } from '@/services/api';
import { l } from '@/utils/intl';
import { useModel } from '@@/exports';
import { Modal, Select } from 'antd';
import TextArea from 'antd/es/input/TextArea';
import { Tab } from 'rc-tabs/lib/interface.d';
import { useEffect, useState } from 'react';
import { SseData, Topic } from '@/models/UseWebSocketModel';

export async function getPrintTables(statement: string) {
  return postAll('api/printTable/getPrintTables', { statement });
}

/*--- Clear Console ---*/
export type PrintTable = {
  tableName: string;
  fullTableName: string;
};

export const DataPage = (props: any) => {
  const { style, title } = props;
  const [consoleInfo, setConsoleInfo] = useState<string>('');
  const { subscribeTopic } = useModel('UseWebSocketModel', (model: any) => ({
    subscribeTopic: model.subscribeTopic
  }));

  useEffect(() => {
    if (title) {
      return subscribeTopic(Topic.PRINT_TABLE, [title.fullTableName], (data: SseData) => {
        if (data?.data[title.fullTableName]) {
          setConsoleInfo(
            (preConsoleInfo) => preConsoleInfo + '\n' + data.data[title.fullTableName]
          );
        }
      });
    }
  }, []);

  return <TextArea value={consoleInfo} style={{ width: style.width, height: style.height }} />;
};

export const onAdd = async (
  tabs: Tab[],
  key: string,
  data: TaskDataType | undefined,
  refresh: any
) => {
  const statement = data?.statement;

  if (!statement) return;
  const tabNames = tabs.map((tab) => tab.label);
  const result = await getPrintTables(statement);
  const tables: PrintTable[] = result.data.filter(
    (table: PrintTable) => !tabNames.includes(table.tableName)
  );

  let selectTable: PrintTable;
  Modal.confirm({
    title: l('pages.datastudio.print.table.inputTableName'),
    content: (
      <Select
        defaultValue=''
        style={{ width: '90%' }}
        onChange={(_e, t: any) => {
          selectTable = { tableName: t.label, fullTableName: t.value };
        }}
        options={tables.map((table) => ({ label: table.tableName, value: table.fullTableName }))}
      />
    ),
    onOk() {
      tabs.push({
        key: key + '/' + selectTable.tableName,
        label: selectTable.tableName,
        children: <DataPage title={selectTable} style={{ width: '100%', height: '100%' }} />
      });
      refresh();
      // onOk(selectTable);
    },
    zIndex: 1000
  });
};
