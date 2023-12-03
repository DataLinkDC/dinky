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

import { LoadCustomEditorLanguage } from '@/components/CustomEditor/languages';
import {
  DIFF_EDITOR_PARAMS,
  PARAM_DIFF_TABLE_COL
} from '@/pages/DataStudio/MiddleContainer/StudioEditor/constants';
import { convertCodeEditTheme } from '@/utils/function';
import { l } from '@/utils/intl';
import { DiffEditor } from '@monaco-editor/react';
import { Col, Modal, Row, Space, Table, Tabs, Typography } from 'antd';
import React, { memo } from 'react';
import styles from './index.less';

const { Text, Link } = Typography;

export type DiffModalProps = {
  diffs: any[];
  open: boolean;
  fileName?: string;
  language?: string;
  onUse: (server: boolean) => void;
};

const DiffModal: React.FC<DiffModalProps> = (props) => {
  const { diffs, open, fileName, language = 'flinksql', onUse } = props;
  // Find the diff object with key 'statement'
  const statementDiff = diffs.find((diff) => diff.key === 'statement');
  // Filter out the diff objects with key other than 'statement'
  const paramDiff = diffs.filter((diff) => diff.key != 'statement');

  // Render the statement diff section
  const renderStatementDiff = () => {
    return (
      <>
        {statementDiff ? (
          <div className={styles.diff_content}>
            <Row style={{ marginBottom: '5px' }}>
              <Col span={12}>
                <Text type={'secondary'}>{l('pages.datastudio.sql.serverVersion')}</Text>
              </Col>
              <Col span={12}>
                <Text type={'secondary'}>{l('pages.datastudio.sql.cacheVersion')}</Text>
              </Col>
            </Row>
            <DiffEditor
              {...DIFF_EDITOR_PARAMS}
              language={language}
              // 挂载前加载语言 | Load language before mounting
              beforeMount={(monaco) => LoadCustomEditorLanguage(monaco.languages, monaco.editor)}
              original={statementDiff?.server}
              modified={statementDiff?.cache}
              theme={convertCodeEditTheme()}
            />
          </div>
        ) : (
          <Text className={styles.no_diff_content} type={'success'}>
            {l('pages.datastudio.sql.nochange')}
          </Text>
        )}
      </>
    );
  };

  // Render the parameter diff section
  const renderParamDiff = () => {
    return (
      <div className={styles.diff_content}>
        {paramDiff.length > 0 ? (
          <Table size={'small'} dataSource={paramDiff} columns={PARAM_DIFF_TABLE_COL} />
        ) : (
          <Text className={styles.no_diff_content} type={'success'}>
            {l('pages.datastudio.sql.nochange')}
          </Text>
        )}
      </div>
    );
  };

  return (
    <Modal
      title={
        <div className={styles.header}>
          {l('pages.datastudio.sql.sqlChanged')}-{fileName}
        </div>
      }
      maskClosable={false}
      onCancel={() => onUse(false)}
      open={open}
      footer={null}
      width={'75%'}
    >
      <div style={{ margin: '10px 0' }}>
        <Text>{l('pages.datastudio.sql.sqlChangedPrompt')}</Text>
      </div>
      <Tabs
        tabBarExtraContent={
          <Space>
            <Link onClick={() => onUse(true)}>{l('pages.datastudio.sql.useServer')}</Link>
            <Link onClick={() => onUse(false)}>{l('pages.datastudio.sql.useCache')}</Link>
          </Space>
        }
        items={[
          {
            key: '1',
            label: l('pages.datastudio.sql.sqldiff.title'),
            children: renderStatementDiff()
          },
          {
            key: '2',
            label: l('pages.datastudio.sql.paramdiff.title'),
            children: renderParamDiff()
          }
        ]}
      />
    </Modal>
  );
};
export default memo(DiffModal);
