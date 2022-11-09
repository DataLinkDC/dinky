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


import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {Modal, Space, Tag, Typography,} from 'antd';
import {ConsoleSqlOutlined} from "@ant-design/icons";
import ProList from '@ant-design/pro-list';
import {explainSql} from "@/pages/DataStudio/service";
import {useEffect, useState} from "react";
import CodeShow from "@/components/Common/CodeShow";
import {l} from "@/utils/intl";

const {Paragraph, Text} = Typography;

type ExplainItem = {
  index: number;
  type: string;
  sql: string;
  parse: string;
  explain: string;
  error: string;
  parseTrue: string;
  explainTrue: string;
  explainTime: number;
};

export type StudioExplainProps = {
  onCancel: (flag?: boolean) => void;
  modalVisible: boolean;
  data: Partial<ExplainItem>;
}
const StudioExplain = (props: any) => {

  const [explainData, setExplainData] = useState([]);
  const [result, setResult] = useState(<Text>正在校验中...</Text>);
  const {
    onClose,
    modalVisible,
    current,
    currentSession,
  } = props;

  useEffect(() => {
    if (!modalVisible) {
      return;
    }
    let selectsql = null;
    if (current.monaco.current) {
      let selection = current.monaco.current.editor.getSelection();
      selectsql = current.monaco.current.editor.getModel().getValueInRange(selection);
    }
    if (selectsql == null || selectsql == '') {
      selectsql = current.value;
    }
    let useSession = !!currentSession.session;
    let param = {
      ...current.task,
      useSession: useSession,
      session: currentSession.session,
      configJson: JSON.stringify(current.task.config),
      statement: selectsql,
    };
    setResult(<Text>正在校验中...</Text>);
    setExplainData([]);
    const result = explainSql(param);
    result.then(res => {
      const errorExplainData: [] = [];
      let errorCount: number = 0;
      for (let i in res.datas) {
        if (!res.datas[i].explainTrue || !res.datas[i].parseTrue) {
          errorExplainData.push(res.datas[i]);
          errorCount++;
        }
      }
      if (errorCount == 0) {
        setExplainData(res.datas);
        setResult(<Text type="success">全部正确</Text>);
      } else {
        setExplainData(errorExplainData);
        setResult(<Text type="danger">存在错误，共计{errorCount}个</Text>);
      }
    })
  }, [modalVisible]);

  const renderContent = () => {
    return (
      <>
        <ProList<ExplainItem>
          toolBarRender={false}
          search={{
            filterType: 'light',
          }}
          rowKey="id"
          dataSource={explainData}
          pagination={{
            pageSize: 5,
          }}
          showActions="hover"
          metas={{
            avatar: {
              dataIndex: 'index',
              search: false,
            },
            title: {
              dataIndex: 'type',
              title: 'type',
              render: (_, row) => {
                return (
                  <Space size={0}>
                    <Tag color="blue" key={row.type}>
                      <ConsoleSqlOutlined/> {row.type}
                    </Tag>
                  </Space>
                );
              },
            },
            description: {
              search: false,
              render: (_, row) => {
                return (
                  <>
                    {row.sql ?
                      (<Paragraph ellipsis={{rows: 2, expandable: true, symbol: 'more'}}>
                        {row.sql}
                      </Paragraph>) : null
                    }
                    {row.error ?
                      (<Paragraph>
                        <CodeShow code={row.error} language='java'
                                  height='500px' theme="vs-dark"/>
                      </Paragraph>) : null
                    }
                  </>
                )
              }
            },
            subTitle: {
              render: (_, row) => {
                return (
                  <Space size={0}>
                    {row.parseTrue ?
                      (<Tag color="#44b549">语法正确</Tag>) :
                      (<Tag color="#ff4d4f">语法有误</Tag>)}
                    {row.explainTrue ?
                      (<Tag color="#108ee9">逻辑正确</Tag>) :
                      (<Tag color="#ff4d4f">逻辑有误</Tag>)}
                    {row.explainTime}
                  </Space>
                );
              },
              search: false,
            },
          }}
          options={{
            search: false,
            setting: false
          }}
        />
      </>)
  };

  return (
    <Modal
      width={'100%'}
      destroyOnClose
      centered
      title="FlinkSql 语法和逻辑检查"
      visible={modalVisible}
      footer={false}
      onCancel={onClose}
    >
      <Paragraph>
        <blockquote>{result}</blockquote>
      </Paragraph>
      {renderContent()}
    </Modal>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  currentSession: Studio.currentSession,
}))(StudioExplain);
