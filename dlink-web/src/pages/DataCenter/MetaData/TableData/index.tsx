import {useEffect, useState} from "react";
import {Alert, AutoComplete, Button, Col, Input, Row, Spin, Table, Tooltip} from "antd";
import {showTableData} from "@/components/Studio/StudioEvent/DDL";
import styles from './index.less';
import { SearchOutlined} from "@ant-design/icons";
import Divider from "antd/es/divider";

const TableData = (props: any) => {

  // 数据库id，数据库名称，表名称
  const {dbId, table, schema,rows} = props;
  // 表数据
  const [tableData, setableData] = useState<{ columns: {}[],rowData: {}[] }>({columns: [], rowData: []});
  // 加载状态
  const [loading, setLoading] = useState<boolean>(false);
  // 列名和列信息数据
  const [columns, setColumns] = useState<string[]>([]);
  const [errMsg, setErrMsg] = useState<{isErr:boolean,msg:string}>({isErr:false,msg:""});
  //使用多选框进行自由列选择，屏蔽不需要看见的列，目前实现有些问题，暂时屏蔽
  // 是否展开字段选择
  // const [showFilter, setShowFilter] = useState(false);
  // 是否全选，全选状态相关
  // const [state, setState] = useState<{ checkedList: [], indeterminate: boolean, checkAll: boolean }>({checkedList: [], indeterminate: true, checkAll: false});
  // 条件查询时联想输入使用
  const [options, setOptions] = useState<{whereOption: {}[],orderOption:{}[] }>({whereOption:[],orderOption:[]});
// where输入框内容
const [optionInput, setOptionInput] = useState<{whereInput:string,orderInput:string}>({whereInput:"",orderInput:""});
const [page, setPage] = useState<{ page:number,pageSize:number }>({page:0 ,pageSize:10});

// 获取数据库数据
const fetchData = async () => {

  setLoading(true);
  let temp= {rowData: [], columns: []}
  let limitStart = page.page * page.pageSize;
  console.log(page.page+"------"+page.pageSize)
  let limitEnd = limitStart + page.pageSize;
  console.log(limitStart+"------"+limitEnd)

  let option = {where:optionInput.whereInput,
    order:optionInput.orderInput,
    limitStart:limitStart,
    limitEnd:limitEnd}

  await showTableData(dbId, schema, table, option).then(result => {
    if (result.code == 1){
      setErrMsg({isErr:true,msg:result.datas.error})
    }else {
      setErrMsg({isErr:false,msg:""})
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
  fetchData();
}, [dbId, table, schema,page]);

//使用多选框进行自由列选择，屏蔽不需要看见的列，目前实现有些问题，暂时屏蔽
// // 单项选择监听
// const onChange = checkedList => {
//   setState({
//     checkedList,
//     indeterminate:
//       !!checkedList.length && checkedList.length < columns.length,
//     checkAll: checkedList.length === columns.length
//   });
// };
//
// //全选按钮监听
// const onCheckAllChange = e => {
//   setState({
//     checkedList: e.target.checked ? columns : [],
//     indeterminate: false,
//     checkAll: e.target.checked
//   });
// };


// 条件查询时反馈联想信息
const handleRecommend = (value: string) => {
  if (columns==null){
    return []
  }
  let inputSplit: string[] = value.split(" ");
  let recommend: { value: string }[] = []
  var lastWord = inputSplit[inputSplit.length - 1]
  inputSplit.pop()
  console.log(inputSplit)
  for (let column of columns) {
    if (column.startsWith(lastWord)) {
      let msg = inputSplit.join("") + " "+column
      recommend.push({value: msg })
    }
  }
  return recommend;
};

const handleWhere = (value:string) =>{
  let result = handleRecommend(value)
  setOptions({
    whereOption:result,
    orderOption:[]
  })
}

const handleOrder = (value:string) =>{
  let result = handleRecommend(value)
  setOptions({
    orderOption:result,
    whereOption:[]
  })
}



return (
  <div>
    <Spin spinning={loading} delay={500}>

      <div className={styles.mrgin_top_40}>
        {errMsg.isErr?(
          <Alert
            message="Error"
            description={errMsg.msg}
            type="error"
            showIcon
          />
        ):<></>}
        <Row>
          <Col span={12}>
            <AutoComplete
              options={options.whereOption}
              style={{width: "100%"}}
              onSearch={handleWhere}
              onSelect={(value:string, option)=>{
                setOptionInput({whereInput:value,
                  orderInput:optionInput.orderInput})
              }}
            >
              <Input addonBefore="WHERE" placeholder="查询条件" onChange={(value)=>{
                setOptionInput({whereInput:value.target.value,
                  orderInput:optionInput.orderInput})
              }}/>
            </AutoComplete>

          </Col>
          <Col span={12}>
            <AutoComplete
              options={options.orderOption}
              style={{width: "100%"}}
              onSearch={handleOrder}
              onSelect={(value:string, option)=>{
                setOptionInput({whereInput:optionInput.whereInput,
                  orderInput:value})
              }}
            >
              <Input addonBefore="ORDER BY" placeholder="排序" onChange={(value)=>{
                setOptionInput({whereInput:optionInput.whereInput,
                  orderInput:value.target.value})
              }}/>
            </AutoComplete>
          </Col>
        </Row>
        {/*//使用多选框进行自由列选择，屏蔽不需要看见的列，目前实现有些问题，暂时屏蔽*/}
        {/*{showFilter ? (*/}
        {/*  <div>*/}
        {/*    <Checkbox*/}
        {/*      indeterminate={state.indeterminate}*/}
        {/*      onChange={onCheckAllChange}*/}
        {/*      checked={state.checkAll}*/}
        {/*    >*/}
        {/*      全选*/}
        {/*    </Checkbox>*/}
        {/*    <br/>*/}
        {/*    <CheckboxGroup*/}
        {/*      options={columns}*/}
        {/*      value={state.checkedList}*/}
        {/*      onChange={onChange}*/}
        {/*    />*/}
        {/*  </div>*/}
        {/*) : (<></>)}*/}
        <Row className={styles.margin_10}>
          <Col span={23}/>
          {/*<Col span={1}>*/}
          {/*  <Tooltip title="字段过滤">*/}
          {/*    <Button type="primary" shape="circle" icon={<FilterOutlined/>} size="middle" onClick={(event) => {*/}
          {/*      setShowFilter(!showFilter)*/}
          {/*    }}/>*/}
          {/*  </Tooltip>*/}
          {/*</Col>*/}
          <Col span={1}>
            <Tooltip title="查询">
              <Button type="primary" shape="circle" icon={<SearchOutlined/>} size="middle" onClick={(event)=>{
                fetchData()
              }}/>
            </Tooltip>
          </Col>
        </Row>
      </div>


      <Divider orientation="left" plain>数据</Divider>

      <div>
        <Table style={{height: '95vh'}}
               columns={tableData.columns}
               dataSource={tableData.rowData}
               pagination={{pageSize:page.pageSize,total:rows,
                 onChange: (pageNum, pageSize) => {
                   console.log(pageNum+"================"+pageSize)
                   setPage({page:pageNum-1,pageSize:pageSize});
                 }}}
               scroll={{y: "80vh", x: true}}
        />
      </div>
    </Spin>

  </div>

)
};

export default TableData
