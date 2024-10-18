import {API_CONSTANTS} from "@/services/endpoints";
import React, {useRef, useState} from "react";
import {TaskVersionListItem} from "@/types/Studio/data";
import {l} from "@/utils/intl";
import moment from 'moment';
import {Button, Card, Modal, Tag} from "antd";
import {RocketOutlined, SyncOutlined} from "@ant-design/icons";
import {DiffEditor, Monaco, MonacoDiffEditor} from "@monaco-editor/react";
import {convertCodeEditTheme} from "@/utils/function";
import {handleOption, handleRemoveById} from "@/services/BusinessCrud";
import VersionList from "@/components/VersionList";
import {useRequest} from "@umijs/max";

export const HistoryVersion = (props: { taskId: number,statement:string,updateTime:Date}) => {
  const {
    taskId,statement,updateTime
  } = props;

  const {data, refresh, loading} = useRequest({
    url: API_CONSTANTS.GET_JOB_VERSION,
    params: {taskId: taskId}
  });

  const [versionDiffVisible, setVersionDiffVisible] = useState<boolean>(false);
  const [versionDiffRow, setVersionDiffRow] = useState<TaskVersionListItem>();


  const editorRef = useRef<MonacoDiffEditor>(null);

  function handleEditorDidMount(editor: MonacoDiffEditor) {
    // @ts-ignore
    editorRef.current = editor;
  }

  const VersionDiffForm = () => {
    let leftTitle = l('pages.datastudio.label.version.leftTitle', '', {
      versionId: versionDiffRow?.versionId,
      createTime: moment(versionDiffRow?.createTime).format('YYYY-MM-DD HH:mm:ss')
    });

    let rightTitle = l('pages.datastudio.label.version.rightTitle', '', {
      updateTime: moment(updateTime).format('YYYY-MM-DD HH:mm:ss')
    });
    let originalValue = versionDiffRow?.statement;

    return (
      <Modal
        title={l('pages.datastudio.label.version.diff')}
        open={versionDiffVisible}
        destroyOnClose={true}
        width={'85%'}
        styles={{body: {height: '70vh'}}}
        onCancel={() =>{
          editorRef.current?.dispose()
          setVersionDiffVisible(false)
        } }
        footer={[
          <Button key='back' onClick={() => setVersionDiffVisible(false)}>
            {l('button.close')}
          </Button>
        ]}
      >
        <div style={{display: 'flex', flexDirection: 'row', justifyContent: 'space-between'}}>
          <Tag color='green' style={{height: '20px'}}>
            <RocketOutlined/> {leftTitle}
          </Tag>
          <Tag color='blue' style={{ height: '20px' }}>
            <SyncOutlined spin /> {rightTitle}
          </Tag>
        </div>
        <br/>
        <DiffEditor

          height={'95%'}
          options={{
            readOnly: true,
            selectOnLineNumbers: true,
            lineDecorationsWidth: 20,
            mouseWheelZoom: true,
            automaticLayout: true,
            scrollBeyondLastLine:false
          }}
          language={'sql'}
          original={originalValue}
          modified={statement}
          onMount={handleEditorDidMount}
          theme={convertCodeEditTheme()}
        />
      </Modal>
    );
  };

  const onRollBackVersion = async (row: TaskVersionListItem) => {
    Modal.confirm({
      title: l('pages.datastudio.label.version.rollback.flinksql'),
      content: l('pages.datastudio.label.version.rollback.flinksqlConfirm', '', {
        versionId: row.versionId
      }),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        const TaskVersionRollbackItem = {
          taskId: row.taskId,
          versionId: row.versionId
        };
        await handleOption(
          'api/task/rollbackTask',
          l('pages.datastudio.label.version.rollback.flinksql'),
          TaskVersionRollbackItem
        );
      }
    });
  };

  const deleteVersion = async (item: TaskVersionListItem) => {
    await handleRemoveById(API_CONSTANTS.GET_JOB_VERSION, item.id);
    await refresh()
  };

  return (
    <Card>
      <VersionList
        loading={loading}
        data={data}
        onDeleteListen={deleteVersion}
        onRollBackListen={onRollBackVersion}
        onSelectListen={(item) => {
          setVersionDiffRow(item);
          setVersionDiffVisible(true);
        }}
      />
      {VersionDiffForm()}
    </Card>
  );
};
