import { PageContainer } from '@ant-design/pro-layout';
import React, { useState, useEffect } from 'react';
import { Spin } from 'antd';
import styles from './index.less';
import FlinkSqlEditor from '@/components/FlinkSqlEditor';
import * as monaco from 'monaco-editor';
import Card from 'antd/es/card';
import DropdownSubMenu from './DropdownSubMenu';
import EditorTabs from "@/components/FlinkSqlEditor/components/EditorTabs";


export default () => {
  const code: string = 'select count(1) from ';
  const value: any = {
    formulaContent: code,
  };
  const secondRightData = [
    {
      fields: [
        {
          label: 'name1',
          displayName: 'name2',
          aliasName: 'name3',
          kind: monaco.languages.CompletionItemKind.Field,
          insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          insertText: 'name:string',
          detail: '详情',
        },
      ],
    },
  ];


  const callbackTab = () => {};

  return (
    <PageContainer
      content="这是一个 FlinkSql 在线编辑器（测试版），请从这里进行开发！"
      className={styles.main}
    >
      <Card bordered={false} className={styles.card}>
      <DropdownSubMenu />
      <div
        style={{
          height: 300,
        }}
      >
        <FlinkSqlEditor value={value} secondRightData={secondRightData} />
      </div>

      </Card>
    </PageContainer>
  );
};
