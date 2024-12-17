import {Col, Descriptions, DescriptionsProps, Modal, Row, Table, Tabs, Typography} from "antd";
import {l} from "@/utils/intl";
import styles from "@/pages/DataStudio/CenterTabContent/index.less";
import {DiffEditor} from "@monaco-editor/react";
import {DIFF_EDITOR_PARAMS, PARAM_DIFF_TABLE_COL} from "@/pages/DataStudio/CenterTabContent/SqlTask/constants";
import {LoadCustomEditorLanguage} from "@/components/CustomEditor/languages";
import {convertCodeEditTheme} from "@/utils/function";
import React from "react";

type TaskInfoProps = {
  open: boolean;
  onCancel: () => void;
}

const { Text, Link } = Typography;

const TaskInfoModal = (props: TaskInfoProps) => {
  const renderTaskInfo = () => {
    const items: DescriptionsProps['item'] = [
      {
        key: '1',
        label: '作业id',
        children: <p>4</p>
      },
      {
        key: '4',
        label: '作业类型',
        children: <p>Flinksql</p>
      },
      {
        key: '2',
        label: '作业描述',
        children: <p>这是一个测试上线的作业</p>
      },
      {
        key: '3',
        label: '负责人',
        children: <p>Mactavish Cui</p>
      }
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
              },
              {
                key: '',
                label: l('pages.datastudio.sql.paramdiff.title'),
                children: renderParamDiff()
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
            language={'flinksql'}
            // 挂载前加载语言 | Load language before mounting
            beforeMount={(monaco) => LoadCustomEditorLanguage(monaco.languages, monaco.editor)}
            original={'select * from \n tableA'}
            modified={'select * from \n tableB'}
            theme={convertCodeEditTheme()}
          />
        </div>
      </>
    );
  };

  // Render the parameter diff section
  const renderParamDiff = () => {
    return (
      <div className={styles.diff_content}>
        {/*<Table size={'small'} dataSource={paramDiff} columns={PARAM_DIFF_TABLE_COL} />*/}
        <Table size={'small'} columns={PARAM_DIFF_TABLE_COL}/>
      </div>
    );
  };

  return (
    <>
      <Modal open = {props.open} onCancel={props.onCancel} width={'75%'}>
        {renderTaskInfo()}
        {renderVersionCompare()}
      </Modal>
    </>
  )
}

export default TaskInfoModal;
