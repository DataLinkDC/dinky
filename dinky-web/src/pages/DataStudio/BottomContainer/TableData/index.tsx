import { getCurrentData } from '@/pages/DataStudio/function';
import { StateType } from '@/pages/DataStudio/model';
import { getData, postAll } from '@/services/api';
import { l } from '@/utils/intl';
import {connect, useModel} from '@@/exports';
import { Modal, Select, Tabs } from 'antd';
import TextArea from 'antd/es/input/TextArea';
import { Tab } from 'rc-tabs/lib/interface.d';
import * as React from 'react';
import { useEffect, useState } from 'react';
import {SSE_TOPIC} from "@/pages/DevOps/constants";
import {SseData} from "@/models/Sse";

export async function getPrintTables(statement: string) {
  return postAll('api/printTable/getPrintTables', { statement });
}

/*--- Clear Console ---*/
export function clearConsole() {
  return getData('api/process/clearConsole', {});
}
export type PrintTable = {
  tableName:string,
  fullTableName:string
}

const DataPage = (props: any) => {
  const { style, title } = props;
  const [consoleInfo, setConsoleInfo] = useState<string>('');
  const {subscribeTopic} = useModel('Sse',(model:any)=>({subscribeTopic:model.subscribeTopic}))
  const [tableName, setTableName] = useState<string>('');

  useEffect(() => {
    if (title){
      setTableName(title.tableName);
      const topic = `${SSE_TOPIC.PRINT_TABLE}/${title.fullTableName}`
      return subscribeTopic([topic],(data:SseData)=>{
        setConsoleInfo((preConsoleInfo) => preConsoleInfo + '\n' + data.data);
      });
    }
  }, []);

  return <TextArea value={consoleInfo} style={{ width: style.width, height: style.height }} />;
};

const TableData = (props: any) => {
  const { statement, height } = props;
  const [panes, setPanes] = useState<Tab[]>([]);

  function onOk(title: PrintTable) {
    const activeKey = `${panes.length + 1}`;
    const newPanes = [...panes];
    newPanes.push({
      label: title.tableName,
      children: <DataPage title={title} style={{ width: '100%', height: height - 63 }} />,
      key: activeKey
    });
    setPanes(newPanes);
  }

  const addTab = async () => {
    if (!statement) return;
    const result = await getPrintTables(statement);
    const tables: PrintTable[] = result.datas;

    let selectTable:PrintTable;
    Modal.confirm({
      title: l('pages.datastudio.print.table.inputTableName'),
      content: (
        <Select
          defaultValue=''
          style={{ width: '90%' }}
          onChange={(e,t:any) => {
            selectTable = {tableName:t.label,fullTableName:t.value}
          }}
          options={tables.map((table) => ({ label:table.tableName, value: table.fullTableName }))}
        />
      ),
      onOk() {
        onOk(selectTable);
      }
    });
  };

  const onEdit = (
    targetKey: React.MouseEvent | React.KeyboardEvent | string,
    action: 'add' | 'remove'
  ) => {
    switch (action) {
      case 'add':
        addTab();
        break;
      case 'remove':
        const newPanes = panes.filter((pane) => pane.key !== targetKey);
        setPanes(newPanes);
        break;
    }
  };

  return <Tabs type='editable-card' onEdit={onEdit} items={panes} />;
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  height: Studio.bottomContainer.height,
  statement: getCurrentData(Studio.tabs.panes, Studio.tabs.activeKey)?.statement
}))(TableData);
