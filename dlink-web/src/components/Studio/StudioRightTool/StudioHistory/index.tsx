import React, {useRef, useState} from "react";
import {MinusSquareOutlined} from '@ant-design/icons';
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Col, Drawer, Modal, Row, Tooltip} from 'antd';
import ProDescriptions from '@ant-design/pro-descriptions';
import {handleOption, queryData} from "@/components/Common/crud";
import {Scrollbars} from "react-custom-scrollbars";
import {TaskHistoryTableListItem} from "@/components/Studio/StudioRightTool/StudioHistory/data";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import ReactDiffViewer, {DiffMethod} from "react-diff-viewer";
import Prism from "prismjs";
import "prismjs/components/prism-apex";
import "prismjs/components/prism-sql";
import moment from "moment";


const url = '/api/task/version';


const StudioHistory = (props: any) => {
  const {current, toolHeight} = props;
  const [row, setRow] = useState<TaskHistoryTableListItem>();
  const [versionDiffRow, setVersionDiffRow] = useState<TaskHistoryTableListItem>();
  const actionRef = useRef<ActionType>();

  const [versionDiffVisible, setVersionDiffVisible] = useState<boolean>(false);

  if (current.key) {
    actionRef.current?.reloadAndRest?.();
  }

  const cancelHandle = () => {
    setVersionDiffVisible(false);
  }

  // TODO: 关键词搜索参考 https://codesandbox.io/s/diff-viewer-r2fdt , 语言设置会报错,语言设置在 `highlightSyntax` 代码(参考: https://codesandbox.io/s/vzgxh?file=/src/App.js:16808-16974)

  const VersionDiffProps = () => {
    // const [searchValue, setSearchValue] = React.useState("");
    // const highlightSyntax = str => {
    //   let text = str;
    //   if (searchValue) {
    //     text = reactStringReplace(str, searchValue, match => (
    //       <span style={{backgroundColor: "green"}}>{match}</span>
    //     ));
    //   }
    //   return <>{text}</>
    const highlightSyntax = str => {
      return  str ? (
        <pre
          style={{ display: "inline" }}
          dangerouslySetInnerHTML={{
            __html: Prism.highlight(str, Prism.languages.sql, "sql")
          }}
        />
      ) : (
        <></>
      );

    };
    // const onSearchChange = ({target: {...value}}) => setSearchValue(value);


    const versionDiffStyles = {
      variables: {
        dark: {
          diffViewerBackground: "#5d7b9f",
          diffViewerTitleBackground: "#244ae1",
          highlightBackground: "#0775f3",
          emptyLineBackground: "rgba(161,163,166,0.94)",
          codeFoldContentColor: "rgba(123,61,154,0.83)",
          codeFoldGutterBackground: "#6F767E",
          codeFoldBackground: "#E2E4E5",
        },
        light: {
          codeFoldGutterBackground: "#6F767E",
          codeFoldBackground: "#E2E4E5",
          emptyLineBackground: "#6897BB",

        }
      }
    };



    let leftTitle = "Version：【" + versionDiffRow?.versionId + "】   创建时间: 【" + (moment(versionDiffRow?.createTime).format('YYYY-MM-DD HH:mm:ss')) + "】";
    let rightTitle = "Version：【当前编辑版本】 创建时间: 【" + (moment(current?.task?.createTime).format('YYYY-MM-DD HH:mm:ss')) + "】 最后更新时间: 【" + (moment(current?.task?.updateTime).format('YYYY-MM-DD HH:mm:ss')) + "】"


    return (
      <>
        {/*<Search id="searchBar" title={"Search："} onChange={onSearchChange} style={{width: "300px"}}/><br/>*/}
        <ReactDiffViewer
          oldValue={versionDiffRow?.statement}
          newValue={current?.task?.statement}
          compareMethod={DiffMethod.CHARS}
          leftTitle={leftTitle}
          rightTitle={rightTitle}
          splitView={true}
          useDarkTheme={false}
          styles={versionDiffStyles}
          renderContent={highlightSyntax}
        />
      </>
    );
  };


  const versionDiffForm = () => {
    return (
      <>
        <Modal title="Version Diff" visible={versionDiffVisible} destroyOnClose={true} width={"85%"}
               bodyStyle={{height: "700px"}}
               onCancel={() => {
                 cancelHandle();
               }}
               footer={[
                 <Button key="back" onClick={() => {
                   cancelHandle();
                 }}>
                   关闭
                 </Button>,
               ]}>
          <Scrollbars style={{height: "100%"}}>
            <React.StrictMode>
              <VersionDiffProps/>
            </React.StrictMode>
          </Scrollbars>
        </Modal>
      </>
    )
  }

  const columns: ProColumns<TaskHistoryTableListItem>[] = [
    // {
    //   title: 'id',
    //   dataIndex: 'id',
    //   hideInForm: false,
    //   hideInSearch: false,
    // },
    {
      title: '版本ID',
      dataIndex: 'versionId',
      sorter: true,
      hideInForm: true,
      hideInSearch: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '操作',
      valueType: 'option',
      align: "center",
      render: (text, record, index) => (
        <>
          <Button type="link" onClick={() => onRollBackVersion(record)}>回滚</Button>
          <Button type="link" onClick={() => {
            setVersionDiffRow(record)
            setVersionDiffVisible(true)
          }}>版本对比</Button>
        </>

      )
    },
  ];


  const onRollBackVersion = (row: TaskHistoryTableListItem) => {
    Modal.confirm({
      title: '回滚Flink SQL版本',
      content: `确定回滚Flink SQL版本至【${row.versionId}】吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const TaskHistoryRollbackItem = {
          id: current.key, versionId: row.versionId
        }
        await handleOption('api/task/rollbackTask', "回滚Flink SQL版本", TaskHistoryRollbackItem);
        actionRef.current?.reloadAndRest?.();
      }
    });
  };

  return (
    <>
      <Row>
        <Col span={24}>
          <div style={{float: "right"}}>
            <Tooltip title="最小化">
              <Button
                type="text"
                icon={<MinusSquareOutlined/>}
              />
            </Tooltip>
          </div>
        </Col>
      </Row>
      <Scrollbars style={{height: (toolHeight - 32)}}>
        <ProTable<TaskHistoryTableListItem>
          actionRef={actionRef}
          rowKey="id"
          request={(params, sorter, filter) => queryData(url, {taskId: current.key, ...params, sorter, filter})}
          columns={columns}
          search={false}
        />
        <Drawer
          width={600}
          visible={!!row}
          onClose={() => {
            setRow(undefined);
          }}
          closable={false}
        >
          {row?.versionId && (
            <ProDescriptions<TaskHistoryTableListItem>
              column={2}
              title={row?.versionId}
              request={async () => ({
                data: row || {},
              })}
              params={{
                id: row?.versionId,
              }}
              columns={columns}
            />
          )}
        </Drawer>
      </Scrollbars>
      {versionDiffForm()}
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  toolHeight: Studio.toolHeight,
}))(StudioHistory);
