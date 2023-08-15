import React, {Component, useEffect, useState} from "react";
import {connect} from "@@/exports";
import {StateType} from "@/pages/DataStudio/model";
import {getData, getSseData, postAll} from "@/services/api";
import {API_CONSTANTS} from "@/services/constants";
import TextArea from "antd/es/input/TextArea";
import {Modal, Select, Tabs} from "antd";
import TabPane = Tabs.TabPane;

export async function getWatchTables(statement: string) {
  return postAll('api/statement/getWatchTables', {statement});
}

/*--- Clear Console ---*/
export function clearConsole() {
  return getData('api/process/clearConsole', {});
}

const DataPage = (props: any) => {
  const {height, title} = props;
  const [consoleInfo, setConsoleInfo] = useState<string>("");
  const [eventSource, setEventSource] = useState<EventSource>();
  const [tableName, setTableName] = useState<string>("");


  const onSearchName = (value: string) => {
    eventSource?.close();
    const eventSourceNew = getSseData(API_CONSTANTS.FLINK_TABLE_DATA + "?table=" + value);

    eventSourceNew.onmessage = (event: any) => {
      setConsoleInfo(preConsoleInfo => preConsoleInfo + "\n" + event);
    }

    setEventSource(eventSourceNew);
    setTableName(value)
  };

  useEffect(() => {
    eventSource?.close();
  }, []);

  return (<div style={{width: '100%'}}>
    <TextArea value={consoleInfo}/>
  </div>)
}

const TableData = (props: any) => {

  const {statement} = props;
  const [panes, setPanes] = useState<{ title: string, key: string, content: Component }[]>([]);

  const addTab = async () => {
    let title: string

    const result = await getWatchTables(statement);
    let tables: [string] = result.datas
    Modal.confirm({
      title: 'Please select table name',
      content: <Select defaultValue="" style={{width: 120}} onChange={e => title = e}>
        {tables.map(t => (
          <Option value={t}>{t}</Option>
        ))
        }
      </Select>,
      onOk() {
        const activeKey = `${panes!.length + 1}`;
        const newPanes = [...panes!];
        newPanes.push({
          title: title,
          content: <DataPage/>,
          key: activeKey
        });
        setPanes(newPanes);
      }
    });
  };

  return (<>
    <Tabs type="editable-card" onEdit={(targetKey, action) => {
      if (action === 'add') {
        addTab();
      } else if (action === 'remove') {
        const newPanes = panes!.filter((pane) => pane.key !== targetKey);
        setPanes(newPanes);
      }
    }}>
      {panes!.map((pane) => (
        <TabPane tab={pane.title} key={pane.key}>
          {pane.content}
        </TabPane>
      ))}
    </Tabs>
  </>);
}

export default connect(({Studio}: { Studio: StateType }) => ({
  height: Studio.bottomContainer.height,
  statement: Studio.tabs.panes.find(pane => Studio.tabs.activeKey === pane.key)?.sqlMetaData?.statement
}))(TableData);
