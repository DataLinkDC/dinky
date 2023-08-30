import { getCurrentData } from '@/pages/DataStudio/function';
import { StateType } from '@/pages/DataStudio/model';
import { getData, getSseData, postAll } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { connect } from '@@/exports';
import { Modal, Select, Tabs } from 'antd';
import TextArea from 'antd/es/input/TextArea';
import { ReactNode, useEffect, useState } from 'react';
import * as React from "react";

export async function getPrintTables(statement: string) {
  return postAll('api/statement/getPrintTables', { statement });
}

/*--- Clear Console ---*/
export function clearConsole() {
  return getData('api/process/clearConsole', {});
}

const DataPage = (props: any) => {
  const { height, title } = props;
  const [consoleInfo, setConsoleInfo] = useState<string>('');
  const [eventSource, setEventSource] = useState<EventSource>();
  const [tableName, setTableName] = useState<string>('');

  const onSearchName = (value: string) => {
    if (!value) return;
    eventSource?.close();
    const eventSourceNew = getSseData(API_CONSTANTS.FLINK_TABLE_DATA + '?table=' + value);

    eventSourceNew.onmessage = (event: any) => {
      setConsoleInfo((preConsoleInfo) => preConsoleInfo + '\n' + event);
    };

    setEventSource(eventSourceNew);
    setTableName(value);
  };

  useEffect(() => {
    onSearchName(title);
    return eventSource?.close();
  }, []);

  return (
    <div style={{ width: '100%' }}>
      <TextArea value={consoleInfo} />
    </div>
  );
};

const TableData = (props: any) => {
  const { statement } = props;
  const [panes, setPanes] = useState<{ label: string; key: string; children: ReactNode }[]>([]);

  function onOk(title: string) {
    const activeKey = `${panes.length + 1}`;
    const newPanes = [...panes];
    newPanes.push({
      label: title,
      children: <DataPage title={title}/>,
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
      title: 'Please select table name',
      content: (
        <Select
          defaultValue=''
          style={{ width: 120 }}
          onChange={(e) => (title = e)}
          options={tables.map((table) => ({ value: table }))}
        />
      ),
      onOk() {
        onOk(title);
      }
    });
  };

  const onEdit =  (targetKey: React.MouseEvent | React.KeyboardEvent | string, action: 'add' | 'remove') => {
    switch (action) {
      case 'add':
        addTab();
        break;
      case 'remove':
        const newPanes = panes.filter((pane) => pane.key !== targetKey);
        setPanes(newPanes);
        break;
    }
  }

  return <Tabs type='editable-card' onEdit={onEdit} items={panes}/>;
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  height: Studio.bottomContainer.height,
  statement: getCurrentData(Studio.tabs.panes, Studio.tabs.activeKey)?.statement
}))(TableData);
