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


import {Alert, Col, Divider, notification, Row, Spin, Table, Tooltip} from 'antd';
import React, {useEffect, useRef, useState} from "react";
import MonacoEditor from "react-monaco-editor";
import {CaretRightOutlined, InfoCircleOutlined} from "@ant-design/icons";
import {execDatabaseSql} from "@/components/Studio/StudioEvent/DDL";
import {ProColumns} from "@ant-design/pro-table/lib/typing";
import * as _monaco from "monaco-editor";
import {l} from "@/utils/intl";
import {CompletionItemInsertTextRule, CompletionItemKind} from "monaco-editor";
import {getClickhouseKeyWord, getDorisKeyWord, getMysqlKeyWord} from "@/pages/DataCenter/MetaData/Console/function";

let provider = {
  dispose: () => {
  },
};

interface ISuggestions {
  label: string;
  kind: string;
  insertText: string;
  detail?: string;
  insertTextRules: CompletionItemInsertTextRule
}

const Console = (props: any) => {
    const editorInstance: any = useRef<any>();
    const monacoInstance: any = useRef();
    const {dbId, schemeList, database} = props;
    // 表数据
    const [tableData, setableData] = useState<{ columns: ProColumns[], rowData: {}[], msg: string }>({
      columns: [],
      rowData: [],
      msg: ""
    });
    // 加载状态
    const [loading, setLoading] = useState<boolean>(false);
    const [errMsg, setErrMsg] = useState<{ isErr: boolean, msg: string }>({isErr: false, msg: ""});

    //自定义sql提示保存的数组
    let metas: [{ name: string, kind: CompletionItemKind }] = [];
    let currentDatabase = {type: "mysql"};

    useEffect(() => {
      for (let db of database) {
        if (db.id == dbId) {
          currentDatabase = {type: db.type}
          console.log(currentDatabase)
        }
      }
      loadDatabaseMeta()
    }, [schemeList, database, dbId]);


    const editorDidMountHandle = (editor: any, monaco: any) => {
      monacoInstance.current = monaco;
      editorInstance.current = editor;
      reloadCompletion();
    };

    const loadDatabaseMeta = () => {
      //根据数据库不同加载对应数据库的关键字
      let keyWords: string[];
      console.log(currentDatabase)
      switch (currentDatabase.type.toLowerCase()) {
        case "doris":
          keyWords = getDorisKeyWord()
          break
        case "mysql":
          keyWords = getMysqlKeyWord()
          break
        case "clickhouse":
          keyWords = getClickhouseKeyWord()
          break
        default:
          keyWords = getMysqlKeyWord()
      }
      //由于自定义sql提示会覆盖原本的sql提示功能，所以需要重新填写
      for (let key of keyWords) {
        metas.push({name: key, kind: _monaco.languages.CompletionItemKind.Keyword})
      }
      //加载数据库所有的schema与table添加进sql提示里面，目前还无法做到列级别的提示
      for (let database of schemeList) {
        metas.push({name: database.name, kind: _monaco.languages.CompletionItemKind.Method})
        for (let table of database.tables) {
          metas.push({name: table.name, kind: _monaco.languages.CompletionItemKind.Variable})
          metas.push({name: `${database.name}.${table.name}`, kind: _monaco.languages.CompletionItemKind.Field})
        }
      }
    }

    const reloadCompletion = () => {
      provider.dispose();
      provider = monacoInstance.current.languages.registerCompletionItemProvider(currentDatabase.type.toLowerCase(), {
        provideCompletionItems() {
          return {
            suggestions: buildSuggestions(),
          };
        },
      });
    };

    const buildSuggestions = () => {
      let suggestions: ISuggestions[] = [];
      if (metas) {
        for (let item of metas) {
          suggestions.push(
            {
              label: item.name,
              kind: item.kind,
              insertText: item.name,
              insertTextRules: _monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            }
          )
        }
      }
      return suggestions;
    };


    const getSelectionVal = () => {
      const selection = editorInstance.current.getSelection() // 获取光标选中的值
      const {startLineNumber, endLineNumber, startColumn, endColumn} = selection
      const model = editorInstance.current.getModel()

      return model.getValueInRange({
        startLineNumber,
        startColumn,
        endLineNumber,
        endColumn,
      })
    }

    const execSql = async () => {

      let selectSql = getSelectionVal();

      if (selectSql == "" || selectSql == undefined) {
        notification.warn({
          message: l('pages.metadata.help.noSqlSelect'),
          description: l('pages.metadata.help.selectSql'),
          icon: <InfoCircleOutlined/>,
        });
        return;
      }

      setLoading(true);
      let temp: { columns: ProColumns[], rowData: {}[], msg: string } = {rowData: [], columns: [], msg: ""}

      await execDatabaseSql(dbId, selectSql).then(result => {
        if (result.code == 1) {
          setErrMsg({isErr: true, msg: result.datas.error})
        } else {
          setErrMsg({isErr: false, msg: ""})
        }
        let data = result.datas;

        for (const columnsKey in data.columns) {
          temp.columns.push({
            title: data.columns[columnsKey],
            dataIndex: data.columns[columnsKey],
            key: data.columns[columnsKey],
            ellipsis: true
          })
        }

        for (const row of result.datas.rowData) {
          row.key = row.id
          temp.rowData.push(row)
        }

        temp.msg = `Execution Time: ${data.time / 1000} s , Row count: ${data.total}`
        if (data.total >= 500) {
          temp.msg = `${temp.msg} , row count more than 500, only show top 500`
        }
      })
      setableData(temp);
      setLoading(false)
    };

    return (<>
      <div>
        <Row>
          <Col>
            <div style={{
              position: "relative", width: "25px",
              height: "30vh",
              background: "#f0f0f0"
            }}>
              <Tooltip title={l('pages.metadata.help.exec')}>
                <CaretRightOutlined
                  style={{
                    color: '#4096ff',
                    fontSize: "x-large",
                    textAlign: "center",
                    position: "absolute",
                    bottom: "10px",
                  }}
                  width={"50px"}
                  onClick={execSql}
                  disabled={loading}
                  shape={"circle"}
                />
              </Tooltip>
            </div>
          </Col>
          <Col span={23}>
            <MonacoEditor
              width={"100%"}
              height={"30vh"}
              language={currentDatabase.type.toLowerCase()}
              theme="vs-dark"
              editorDidMount={editorDidMountHandle}
              options={{
                automaticLayout: true,
              }}
            />
          </Col>
        </Row>
      </div>
      <Divider orientation="left"></Divider>
      {errMsg.isErr ? (
        <Alert
          message="Error"
          description={errMsg.msg}
          type="error"
          showIcon
        />
      ) : tableData.columns.length > 0 ? (
        <Alert message={tableData.msg} type="success" showIcon/>
      ) : <></>
      }
      <br/>
      <div>
        <Spin spinning={loading} delay={500}>
          <Table
            columns={tableData.columns}
            dataSource={tableData.rowData}
            pagination={{
              defaultPageSize: 10,
              showSizeChanger: true,
            }}
            scroll={{x: true}}
          />
        </Spin>
      </div>

    </>)
  }
;

export default Console;
