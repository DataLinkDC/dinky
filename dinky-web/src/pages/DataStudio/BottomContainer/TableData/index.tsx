import { getCurrentData } from '@/pages/DataStudio/function';
import { StateType } from '@/pages/DataStudio/model';
import { getData, getSseData, postAll } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import { Modal, Select, Tabs } from 'antd';
import TextArea from 'antd/es/input/TextArea';
import { Tab } from 'rc-tabs/lib/interface.d';
import * as React from 'react';
import { useEffect, useState } from 'react';

export async function getPrintTables(statement: string) {
  return postAll('api/task/getPrintTables', { statement });
}

/*--- Clear Console ---*/
export function clearConsole() {
  return getData('api/process/clearConsole', {});
}

const DataPage = (props: any) => {
  const { style, title } = props;
  const [consoleInfo, setConsoleInfo] = useState<string>('');
  const [eventSource, setEventSource] = useState<EventSource>();
  const [tableName, setTableName] = useState<string>('');

  const onSearchName = (value: string) => {
    if (!value) return;
    eventSource?.close();
    const eventSourceNew = getSseData(API_CONSTANTS.FLINK_TABLE_DATA + '?table=' + value);
    eventSourceNew.onerror = (event: any) => {
      console.log('EventSource failed:', event);
    };

    eventSourceNew.addEventListener('pt_data', (event: any) => {
      setConsoleInfo((preConsoleInfo) => preConsoleInfo + '\n' + event.data);
    });

    setEventSource(eventSourceNew);
    setTableName(value);
  };

  useEffect(() => {
    onSearchName(title);
    return eventSource?.close();
  }, []);

  return <TextArea value={consoleInfo} style={{ width: style.width, height: style.height }} />;
};

const TableData = (props: any) => {
  const { statement, height } = props;
  const [panes, setPanes] = useState<Tab[]>([]);

  function onOk(title: string) {
    const activeKey = `${panes.length + 1}`;
    const newPanes = [...panes];
    newPanes.push({
      label: title,
      children: <DataPage title={title} style={{ width: '100%', height: height - 63 }} />,
      key: activeKey
    });
    setPanes(newPanes);
  }

  const addTab = async () => {
    if (!statement) return;
    const result = await getPrintTables(statement);
    const tables: [string] = result.datas;

    let title: string;
    Modal.confirm({
      title: l('pages.datastudio.print.table.inputTableName'),
      content: (
        <Select
          defaultValue=''
          style={{ width: '90%' }}
          onChange={(e) => (title = e)}
          options={tables.map((table) => ({ value: table }))}
        />
      ),
      onOk() {
        onOk(title);
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
