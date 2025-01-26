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

import { Col, Descriptions, DescriptionsProps, Modal, Row, Tabs, Typography } from 'antd';
import { l } from '@/utils/intl';
import styles from '@/pages/DataStudio/CenterTabContent/index.less';
import { DiffEditor } from '@monaco-editor/react';
import { DIFF_EDITOR_PARAMS } from '@/pages/DataStudio/CenterTabContent/SqlTask/constants';
import { LoadCustomEditorLanguage } from '@/components/CustomEditor/languages';
import { convertCodeEditTheme } from '@/utils/function';
import React from 'react';
import { TaskState } from '@/pages/DataStudio/type';

type TaskInfoProps = {
  open: boolean;
  onCancel: () => void;
  taskInfo: TaskState | undefined;
  preVersionStatement: string;
  curVersionStatement: string;
};

const { Text, Link } = Typography;
const TaskInfoModal = (props: TaskInfoProps) => {
  const renderTaskInfo = () => {
    const items: DescriptionsProps['item'] = [
      {
        key: '1',
        label: l('pages.datastudio.label.jobInfo.id'),
        children: <p>{props.taskInfo?.taskId}</p>
      },
      {
        key: '2',
        label: l('pages.datastudio.label.jobInfo.name'),
        children: <p>{props.taskInfo?.name}</p>
      },
      {
        key: '3',
        label: l('pages.datastudio.label.jobInfo.dialect'),
        children: <p>{props.taskInfo?.dialect}</p>
      },
      {
        key: '4',
        label: l('pages.datastudio.label.jobConfig.flinksql.env'),
        children: <p>{props.taskInfo?.envId}</p>
      },
      {
        key: '5',
        label: l('pages.datastudio.label.jobInfo.firstLevelOwner'),
        children: <p>{props.taskInfo?.firstLevelOwner}</p>
      }
    ];

    return (
      <>
        <Descriptions title={l('devops.jobinfo.config.JobBaseInfo')} items={items}></Descriptions>
      </>
    );
  };

  const renderVersionCompare = () => {
    return (
      <>
        <Tabs
          items={[
            {
              key: '1',
              label: l('pages.datastudio.sql.sqldiff.title'),
              children: renderStatementDiff()
            }
          ]}
        />
      </>
    );
  };

  // Render the statement diff section
  const renderStatementDiff = () => {
    return (
      <>
        <div className={styles.diff_content}>
          <Row style={{ marginBottom: '5px' }}>
            <Col span={12}>
              <Text type={'secondary'}>{l('approval.previousTaskVersion')}</Text>
            </Col>
            <Col span={12}>
              <Text type={'secondary'}>{l('approval.currentTaskVersion')}</Text>
            </Col>
          </Row>
          <DiffEditor
            {...DIFF_EDITOR_PARAMS}
            language={props.taskInfo?.dialect}
            // 挂载前加载语言 | Load language before mounting
            beforeMount={(monaco) => LoadCustomEditorLanguage(monaco.languages, monaco.editor)}
            original={props.preVersionStatement}
            modified={props.curVersionStatement}
            theme={convertCodeEditTheme()}
          />
        </div>
      </>
    );
  };

  return (
    <>
      <Modal open={props.open} onCancel={props.onCancel} width={'75%'}>
        {renderTaskInfo()}
        {renderVersionCompare()}
      </Modal>
    </>
  );
};

export default TaskInfoModal;
