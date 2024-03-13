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

import CodeShow from '@/components/CustomEditor/CodeShow';
import { getCurrentData } from '@/pages/DataStudio/function';
import { explainSql } from '@/pages/DataStudio/HeaderContainer/service';
import { StateType } from '@/pages/DataStudio/model';
import { l } from '@/utils/intl';
import { ConsoleSqlOutlined } from '@ant-design/icons';
import ProList from '@ant-design/pro-list';
import { Drawer, Space, Tag, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { connect } from 'umi';

const { Paragraph, Text } = Typography;

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

export type ExplainProps = {
  data: Partial<ExplainItem>;
};
const Explain: React.FC<ExplainProps> = (props: any) => {
  const [explainData, setExplainData] = useState([]);
  const [result, setResult] = useState(<Text>{l('pages.datastudio.explain.validate')}</Text>);
  const [showModal, setShowModal] = useState(false);
  const [explainInfo, setExplainInfo] = useState('');
  const {
    tabs: { panes, activeKey }
  } = props;
  const current = getCurrentData(panes, activeKey);

  useEffect(() => {
    // let selectsql = null;
    // if (current.monaco.current) {
    //   let selection = current.monaco.current.editor.getSelection();
    //   selectsql = current.monaco.current.editor.getModel().getValueInRange(selection);
    // }
    // if (selectsql == null || selectsql == '') {
    //   selectsql = current.value;
    // }
    let param = {
      ...current,
      configJson: current?.config,
      taskId: current?.id
    };
    setResult(<Text>{l('pages.datastudio.explain.validate')}</Text>);
    setExplainData([]);
    const result = explainSql(
      l('pages.datastudio.editor.checking', '', { jobName: current?.name }),
      param
    );
    result.then((res) => {
      const errorExplainData: [] = [];
      let errorCount: number = 0;
      if (!res.data) {
        setResult(<Text type='danger'>{res.msg}</Text>);
        return;
      } else {
        for (let i in res.data) {
          if (!res.data[i].explainTrue || !res.data[i].parseTrue) {
            // @ts-ignore
            errorExplainData.push(res.data[i]);
            errorCount++;
          }
        }
      }
      if (errorCount == 0) {
        setExplainData(res.data);
        setResult(<Text type='success'>{l('pages.datastudio.explain.validate.allright')}</Text>);
      } else {
        setExplainData(errorExplainData);
        setResult(
          <Text type='danger'>
            {l('pages.datastudio.explain.validate.error', '', {
              errorCount: errorCount
            })}
          </Text>
        );
      }
    });
  }, []);
  const showPlanDrawer = (info: string) => {
    setShowModal(true);
    setExplainInfo(info);
  };
  const renderContent = () => {
    return (
      <>
        <ProList<ExplainItem>
          toolBarRender={false}
          search={{
            filterType: 'light'
          }}
          rowKey='id'
          dataSource={explainData}
          pagination={{
            pageSize: 5
          }}
          showActions='hover'
          metas={{
            avatar: {
              dataIndex: 'index',
              search: false
            },
            title: {
              dataIndex: 'type',
              title: 'type',
              render: (_, row) => {
                return (
                  <Space size={0}>
                    <Tag color='blue' key={row.type}>
                      <ConsoleSqlOutlined /> {row.type}
                    </Tag>
                  </Space>
                );
              }
            },
            description: {
              search: false,
              render: (_, row) => {
                return (
                  <>
                    {row.sql ? (
                      <>
                        <Paragraph ellipsis={{ rows: 2, expandable: true, symbol: 'more' }}>
                          {row.sql}
                        </Paragraph>
                      </>
                    ) : null}
                    {row.error ? (
                      <Paragraph>
                        <CodeShow code={row.error} language='java' height='500px' />
                      </Paragraph>
                    ) : null}
                    {row.explain ? (
                      <a
                        onClick={() => showPlanDrawer(row.explain)}
                        style={{ float: 'right' }}
                        type={'link'}
                      >
                        Show Plan
                      </a>
                    ) : (
                      <></>
                    )}
                  </>
                );
              }
            },
            subTitle: {
              render: (_, row) => {
                return (
                  <Space size={0}>
                    {row.parseTrue ? (
                      <Tag color='#44b549'>
                        {l('pages.datastudio.explain.validate.grammar.right')}
                      </Tag>
                    ) : (
                      <Tag color='#ff4d4f'>
                        {l('pages.datastudio.explain.validate.grammar.error')}
                      </Tag>
                    )}
                    {row.explainTrue ? (
                      <Tag color='#108ee9'>
                        {l('pages.datastudio.explain.validate.logic.right')}
                      </Tag>
                    ) : (
                      <Tag color='#ff4d4f'>
                        {l('pages.datastudio.explain.validate.logic.error')}
                      </Tag>
                    )}
                    {row.explainTime}
                  </Space>
                );
              },
              search: false
            }
          }}
          options={{
            search: false,
            setting: false
          }}
        />
      </>
    );
  };

  return (
    <>
      <Paragraph>
        <blockquote>{result}</blockquote>
      </Paragraph>
      {renderContent()}
      <Drawer
        width={'50%'}
        title='Show Plan'
        placement='right'
        onClose={() => setShowModal(false)}
        open={showModal}
      >
        <CodeShow
          style={{ alignItems: 'inherit' }}
          code={explainInfo}
          language='java'
          height='92vh'
        />
      </Drawer>
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs
}))(Explain);
