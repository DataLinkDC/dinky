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


import {useEffect, useState} from "react";
import {Alert, AutoComplete, Button, Col, Input, Row, Spin, Tooltip} from "antd";
import {showTableData} from "@/components/Studio/StudioEvent/DDL";
import styles from './index.less';
import {SearchOutlined} from "@ant-design/icons";
import Divider from "antd/es/divider";
import {ProTable} from "@ant-design/pro-table";

const TableData = (props: any) => {

  // 数据库id，数据库名称，表名称
  const {dbId, table, schema} = props;
  // 表数据
  const [tableData, setableData] = useState<{ columns: {}[], rowData: {}[] }>({columns: [], rowData: []});
  // 加载状态
  const [loading, setLoading] = useState<boolean>(false);
  // 列名和列信息数据
  const [columns, setColumns] = useState<string[]>([]);
  const [errMsg, setErrMsg] = useState<{ isErr: boolean, msg: string }>({isErr: false, msg: ""});

  // 条件查询时联想输入使用
  const [options, setOptions] = useState<{ whereOption: {}[], orderOption: {}[] }>({whereOption: [], orderOption: []});
// where输入框内容
  const [optionInput, setOptionInput] = useState<{ whereInput: string, orderInput: string }>({
    whereInput: "",
    orderInput: ""
  });
  const [page, setPage] = useState<{ page: number, pageSize: number }>({page: 0, pageSize: 10});
// const [defaultInput,setDefaultInput]
// 获取数据库数据
  const fetchData = async (whereInput: string, orderInput: string) => {

    setLoading(true);
    let temp = {rowData: [], columns: []}

    let option = {
      where: whereInput,
      order: orderInput, limitStart: "0", limitEnd: "500"
    }

    await showTableData(dbId, schema, table, option).then(result => {
      if (result.code == 1) {
        setErrMsg({isErr: true, msg: result.datas.error})
      } else {
        setErrMsg({isErr: false, msg: ""})
      }
      let data = result.datas;
      setColumns(data.columns)

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
    })
    setableData(temp);
    setLoading(false)
  };

  useEffect(() => {
    setColumns([])
    setableData({columns: [], rowData: []})
    setErrMsg({isErr: false, msg: ""})
    setOptions({whereOption: [], orderOption: []})
    setOptionInput({whereInput: "", orderInput: ""})
    setPage({page: 0, pageSize: 10})
    setLoading(false)

    fetchData("", "");


  }, [dbId, table, schema]);


// 条件查询时反馈联想信息
  const handleRecommend = (value: string) => {
    if (columns == null) {
      return []
    }
    let inputSplit: string[] = value.split(" ");
    let recommend: { value: string }[] = []
    var lastWord = inputSplit[inputSplit.length - 1]
    inputSplit.pop()
    console.log(inputSplit)
    for (let column of columns) {
      if (column.startsWith(lastWord)) {
        let msg = inputSplit.join("") + " " + column
        recommend.push({value: msg})
      }
    }
    return recommend;
  };

  const handleWhere = (value: string) => {
    let result = handleRecommend(value)
    setOptions({
      whereOption: result,
      orderOption: []
    })
  }

  const handleOrder = (value: string) => {
    let result = handleRecommend(value)
    setOptions({
      orderOption: result,
      whereOption: []
    })
  }


  return (
    <div>
      <Spin spinning={loading} delay={500}>

        <div className={styles.mrgin_top_40}>
          {errMsg.isErr ? (
            <Alert
              message="Error"
              description={errMsg.msg}
              type="error"
              showIcon
            />
          ) : <></>}
          <Row>
            <Col span={6}>
              <AutoComplete
                value={optionInput.whereInput}
                options={options.whereOption}
                style={{width: "100%"}}
                onSearch={handleWhere}
                onSelect={(value: string, option) => {
                  setOptionInput({
                    whereInput: value,
                    orderInput: optionInput.orderInput
                  })
                }}
              >
                <Input addonBefore="WHERE" placeholder="查询条件"
                       onChange={(value) => {
                         setOptionInput({
                           whereInput: value.target.value,
                           orderInput: optionInput.orderInput
                         })
                       }}
                />
              </AutoComplete>
            </Col>

            <Col span={6}>
              <AutoComplete
                value={optionInput.orderInput}
                options={options.orderOption}
                style={{width: "100%"}}
                onSearch={handleOrder}
                onSelect={(value: string, option) => {
                  setOptionInput({
                    whereInput: optionInput.whereInput,
                    orderInput: value
                  })
                }}
              >
                <Input addonBefore="ORDER BY" placeholder="排序" onChange={(value) => {
                  setOptionInput({
                    whereInput: optionInput.whereInput,
                    orderInput: value.target.value
                  })
                }}/>
              </AutoComplete>
            </Col>
            <Col span={2}>
              <Tooltip title="查询">
                <Button type="primary" shape="circle" icon={<SearchOutlined/>} size="middle" onClick={(event) => {
                  fetchData(optionInput.whereInput, optionInput.orderInput)
                }}/>
              </Tooltip>
            </Col>
          </Row>

        </div>


        <Divider orientation="left" plain>数据</Divider>

        <div>
          <ProTable
            style={{height: '95vh'}}
            columns={tableData.columns}
            dataSource={tableData.rowData}
            pagination={{
              pageSize: 10,
            }}
            scroll={{y: "80vh", x: true}}
            dateFormatter="string"
            search={false}
          />
        </div>
      </Spin>

    </div>

  )
};

export default TableData
