import {TaskState} from "@/pages/DataStudio/type";
import React, {useState} from "react";
import {handleOption} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/endpoints";
import {EditableProTable, ProColumns} from "@ant-design/pro-table";
import {Button, Tabs, Tooltip} from "antd";
import {l} from "@/utils/intl";
import {StateType} from "rmc-input-number";
import {connect} from "umi";


export const TestCaseFlinkSqlConfig = (props: { params: TaskState }) => {
  const {
    params: { taskId, statement },
    users
  } = props;
  // 测试用例相关Type定义
  // Flink Sql任务的测试用例type，一个任务拥有多个输入表
  type TestCaseList = TestCase[];
  // 每张表的测试用例type，同一张表拥有多个column相同的测试用例
  type TestCase = {
    tableName?: string;
    columns?: string[];
    rowData?: TestCaseRowData[];
  };
  // 每个测试用例
  type TestCaseRowData = {
    dinky_mock_source_row_id: React.Key;
    [key: string]: any;
  };

  // 测试用例state，用于渲染表单
  const [testCaseList, setTestCaseList] = useState<TestCaseList>([]);

  // 表单编辑相关
  // Modal是否可见
  const [modalVisible, setModalVisible] = useState(false);
  // 可编辑的数据
  const [editableKeys, setEditableRowKeys] = useState<React.Key[]>([]);
  // 待分配的最小行id
  const [rowKeyId, setRowKeyId] = useState<number>(1000);
  // 当前正在操作的行数据
  const [rowData, setRowData] = useState<readonly TestCaseRowData[]>([]);
  // 插入位置
  const [position, setPosition] = useState<'top' | 'bottom' | 'hidden'>('bottom');

  // 表单开启、关闭时重置state
  const handleOpenChange = (open: boolean) => {
    setModalVisible(open);
    setEditableRowKeys([]);
    setTestCaseList([]);
    setRowKeyId(1000);
    setRowData([]);
    setPosition('bottom');
  }

  //初始化，获取source表结构及已经保存的测试用例
  const handleFormInit = async () => {
    handleOpenChange(true);

    const res = await handleOption(
      API_CONSTANTS.LIST_TASK_TEST_CASE_BY_STATEMENT,
      l('pages.datastudio.label.execConfig.mocksource.getTestCase'),
      {
        taskId: taskId,
        statement: statement
      }
    );

    // generate row id
    const testCaseListWithId: TestCase[] = [];
    const originTestCaseList: TestCase[] = res?.data?.taskTableInputList;
    let rowDataRowKeyId: number = rowKeyId;
    originTestCaseList?.forEach((tableScheme) => {
      tableScheme?.rowData?.forEach((row) => {
        row.dinky_mock_source_row_id = rowDataRowKeyId++;
      });
      testCaseListWithId.push({
        tableName: tableScheme.tableName,
        columns: tableScheme.columns,
        rowData: tableScheme.rowData
      });
    });

    setTestCaseList(testCaseListWithId);
    setRowKeyId(rowKeyId);
  };

  // 保存测试用例
  const handleFinish = async () => {
    await handleOption(
      API_CONSTANTS.SAVE_OR_UPDATE_TEST_CASE,
      l('pages.datastudio.label.execConfig.mocksource.saveTestCase'),
      {
        taskId: taskId,
        statement: testCaseList
      }
    );
    return true;
  };

  // 基于表的行数据渲染表单
  const getProColumns = (columns: string[]) => {
    const proColumns: ProColumns<TestCaseRowData>[] = [];
    columns.forEach((columnName) => {
      proColumns.push({
        title: columnName,
        dataIndex: columnName
      });
    });
    proColumns.push({
      title: l('pages.datastudio.label.execConfig.mocksource.handleRowData'),
      valueType: 'option',
      width: 200,
      render: (text, record, _, action) => [
        <a
          key='editable'
          onClick={() => {
            action?.startEditable?.(record.dinky_mock_source_row_id);
          }}
        >{l('pages.datastudio.label.execConfig.mocksource.editeRowData')}</a>,
        <a
          key='delete'
          onClick={() => {
            setTestCaseList(
              testCaseList.map((testCase) => {
                const newRowData = testCase.rowData.filter(
                  (row) => row.dinky_mock_source_row_id !== record.dinky_mock_source_row_id
                );
                return {...testCase, rowData: newRowData};
              })
            );
          }}
        >{('pages.datastudio.label.execConfig.mocksource.deleteRowData')}</a>
      ]
    });
    return proColumns;
  };

  //表单数据发生改变时，更新state
  const handleDataChange = async (targetTableName: string, targetRowKey: any, newRowData: any) => {
    setTestCaseList((prevTestCase) =>
      prevTestCase.map((testCase) => {
        if (testCase.tableName === targetTableName) {
          const rowData = testCase.rowData ? [...testCase.rowData] : [];
          let findExistedRowData = false;

          // 更新或添加新数据
          const updatedRowData = rowData.map((item) => {
            // 存在相同rowKey时更新数据
            if (item.dinky_mock_source_row_id === targetRowKey) {
              findExistedRowData = true;
              return newRowData; // 返回新数据
            }
            return item; // 返回原数据
          });
          // 如果没有找到匹配项，则添加
          if (!findExistedRowData) {
            updatedRowData.push(newRowData);
          }
          // 返回更新后的 testCase
          return {...testCase, rowData: updatedRowData};
        }
        return testCase;
      })
    );
    //保存
    await handleFinish();
  };

  return (
    <>
      <Tooltip title={l('pages.datastudio.label.execConfig.mocksource.testcase.tips')}>
        <Button onClick={handleFormInit}>
          {l('pages.datastudio.label.execConfig.mocksource.testcase')}
        </Button>
      </Tooltip>
      <modalForm
        open={modalVisible}
        onOpenChange={handleOpenChange}
        onFinish={handleFinish}
        width={'90%'}
      >
        <Tabs defaultActiveKey='0'>
          {testCaseList.map((data, index) => {
            return (
              <Tabs.TabPane key ={index} tab = {data.tableName}>
                <EditableProTable<TestCaseRowData>
                  rowKey='dinky_mock_source_row_id'
                  scroll={{x: 960}}
                  recordCreatorProps={
                    position !== 'hidden'
                    ? {
                      position: position as 'top',
                      record: () => (
                        {
                          dinky_mock_source_row_id: rowKeyId
                        }
                      )
                    }: false
                  }
                  loading={false}
                  toollBarRender={() => [
                    <ProFormRadio.Group
                        key='render'
                        fieldProps={{
                          value: position,
                          onChange: (e) => setPosition(e.target.value)
                        }}
                        options={[
                          {
                            label: l('pages.datastudio.label.execConfig.mocksource.testcase.top'),
                            value: 'top'
                          },
                          {
                            label: l('pages.datastudio.label.execConfig.mocksource.testcase.bottom'),
                            value: 'bottom'
                          },
                          {
                            label: l('pages.datastudio.label.execConfig.mocksource.testcase.hidden'),
                            value: 'hidden'
                          }
                        ]}
                    />
                  ]}
                  columns={getProColumns(data.columns ?? [])}
                  value={data.rowData}
                  edutable={{
                    type: 'multiple',
                    editableKeys,
                    onSave: async (rowKey, rowData, row) => {
                      await handleDataChange(data.tableName ?? '', rowKey, rowData);
                    },
                    onChange: setEditableRowKeys
                  }}
                />
              </Tabs.TabPane>
            );
          })}
        </Tabs>
      </modalForm>
    </>
  );
};
