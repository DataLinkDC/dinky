import { Col, Descriptions, DescriptionsProps, Modal, Row, Tabs, Typography } from "antd";
import { l } from "@/utils/intl";
import styles from "@/pages/DataStudio/CenterTabContent/index.less";
import { DiffEditor } from "@monaco-editor/react";
import { DIFF_EDITOR_PARAMS } from "@/pages/DataStudio/CenterTabContent/SqlTask/constants";
import { LoadCustomEditorLanguage } from "@/components/CustomEditor/languages";
import { convertCodeEditTheme } from "@/utils/function";
import React from "react";
import { TaskState } from "@/pages/DataStudio/type";

type TaskInfoProps = {
  open: boolean;
  onCancel: () => void;
  taskInfo: TaskState;
  preVersionStatement: string;
  curVersionStatement: string;
}

const {Text, Link} = Typography;

const TaskInfoModal = (props: TaskInfoProps) => {
  const renderTaskInfo = () => {
    const items: DescriptionsProps['item'] = [
      {
        key: '1',
        label: '作业id',
        children: <p>{props.taskInfo.taskId}</p>
      },
      {
        key: '2',
        label: '作业名称',
        children: <p>{props.taskInfo.name}</p>
      },
      {
        key: '3',
        label: '作业类型',
        children: <p>{props.taskInfo.dialect}</p>
      },
      {
        key: '4',
        label: '环境id',
        children: <p>{props.taskInfo.envId}</p>
      },
      {
        key: '5',
        label: '负责人',
        children: <p>{props.taskInfo.firstLevelOwner}</p>
      },
    ];

    return (
      <>
        <Descriptions title='作业信息' items={items}></Descriptions>
      </>
    );
  }

  const renderVersionCompare = () => {
    return (
      <>
        <Tabs
          items={
            [
              {
                key: '1',
                label: l('pages.datastudio.sql.sqldiff.title'),
                children: renderStatementDiff()
              }
            ]
          }
        />
      </>
    )
  };

  // Render the statement diff section
  const renderStatementDiff = () => {
    return (
      <>
        <div className={styles.diff_content}>
          <Row style={{marginBottom: '5px'}}>
            <Col span={12}>
              <Text type={'secondary'}>已上线版本</Text>
            </Col>
            <Col span={12}>
              <Text type={'secondary'}>提交审核版本</Text>
            </Col>
          </Row>
          <DiffEditor
            {...DIFF_EDITOR_PARAMS}
            language={props.taskInfo.dialect}
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
  )
}

export default TaskInfoModal;
