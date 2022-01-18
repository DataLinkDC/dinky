import styles from "./index.less";
import {Menu, Dropdown, Tooltip, Row, Col, Popconfirm, notification, Modal, message} from "antd";
import {
  PauseCircleTwoTone, CopyTwoTone, DeleteTwoTone, PlayCircleTwoTone, DiffTwoTone,SnippetsTwoTone,
  FileAddTwoTone, FolderOpenTwoTone, SafetyCertificateTwoTone, SaveTwoTone, FlagTwoTone,
  EnvironmentOutlined, SmileOutlined, RocketTwoTone, QuestionCircleTwoTone, MessageOutlined, ClusterOutlined
} from "@ant-design/icons";
import Space from "antd/es/space";
import Divider from "antd/es/divider";
import Button from "antd/es/button/button";
import Breadcrumb from "antd/es/breadcrumb/Breadcrumb";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import { postDataArray} from "@/components/Common/crud";
import {executeSql, getJobPlan} from "@/pages/FlinkSqlStudio/service";
import StudioHelp from "./StudioHelp";
import StudioGraph from "./StudioGraph";
import {showCluster, showTables} from "@/components/Studio/StudioEvent/DDL";
import {useCallback, useEffect, useState} from "react";
import StudioExplain from "../StudioConsole/StudioExplain";
import {DIALECT, isOnline, isSql} from "@/components/Studio/conf";
import {
  ModalForm,
} from '@ant-design/pro-form';
import SqlExport from "@/pages/FlinkSqlStudio/SqlExport";
import {Dispatch} from "@@/plugin-dva/connect";

const menu = (
  <Menu>
    <Menu.Item>敬请期待</Menu.Item>
  </Menu>
);


const StudioMenu = (props: any) => {

  const {tabs, current, currentPath, form, refs, dispatch, currentSession} = props;
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [exportModalVisible, handleExportModalVisible] = useState<boolean>(false);
  const [graphModalVisible, handleGraphModalVisible] = useState<boolean>(false);
  const [graphData, setGraphData] = useState();

  const onKeyDown = useCallback((e) => {
    if(e.keyCode === 83 && e.ctrlKey === true){
      e.preventDefault();
      if(current) {
        props.saveTask(current);
      }
    }
  }, [current]);

  useEffect(() => {
    document.addEventListener("keydown", onKeyDown)
    return () => {
      document.removeEventListener("keydown", onKeyDown)
    };
  }, [current]);

  const execute = () => {
    if(!isSql(current.task.dialect)&&!isOnline(current.task.type)){
      message.warn(`该任务执行模式为【${current.task.type}】，不支持 SQL 查询，请手动保存后使用右侧按钮——作业提交`);
      return;
    }
    let selectsql = null;
    if (current.monaco.current) {
      let selection = current.monaco.current.editor.getSelection();
      selectsql = current.monaco.current.editor.getModel().getValueInRange(selection);
    }
    if (selectsql == null || selectsql == '') {
      selectsql = current.value;
    }
    let useSession = !!currentSession.session;
    let param = {
      ...current.task,
      useSession: useSession,
      session: currentSession.session,
      configJson: JSON.stringify(current.task.config),
      statement: selectsql,
    };
    const key = current.key;
    const taskKey = (Math.random() * 1000) + '';
    notification.success({
      message: `新任务【${param.jobName}】正在执行`,
      description: param.statement.substring(0, 40) + '...',
      duration: null,
      key: taskKey,
      icon: <SmileOutlined style={{color: '#108ee9'}}/>,
    });
    setTimeout(() => {
      refs?.history?.current?.reload();
    }, 2000);
    const result = executeSql(param);
    result.then(res => {
      notification.close(taskKey);
      if (res.datas.success) {
        message.success('执行成功');
      } else {
        message.error('执行失败');
      }
      let newTabs = tabs;
      for (let i = 0; i < newTabs.panes.length; i++) {
        if (newTabs.panes[i].key == key) {
          newTabs.panes[i].console.result = res.datas;
          break;
        }
      }
      props.saveTabs(newTabs);
      useSession && showTables(currentSession.session, dispatch);
    })
  };

  const submit = () => {
    if (!current.task.id) {
      message.error(`草稿【${current.title}】无法被提交，请创建或选择有效作业进行提交`);
      return;
    }
    const taskKey = (Math.random() * 1000) + '';
    Modal.confirm({
      title: '异步提交作业',
      content: `确定异步提交作业【${current.task.alias}】到其配置的集群吗？请确认您的作业是否已经被保存！`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        let task = {
          id: current.task.id,
        };
        notification.success({
          message: `任务【${current.task.alias} 】正在异步提交`,
          description: current.task.statement,
          duration: null,
          key: taskKey,
          icon: <SmileOutlined style={{color: '#108ee9'}}/>,
        });
        setTimeout(() => {
          refs?.history?.current?.reload();
        }, 2000);
        const res = await postDataArray('/api/task/submit', [task.id]);
        notification.close(taskKey);
        if (res.datas[0].success) {
          message.success('异步提交成功');
        } else {
          message.success('异步提交失败');
        }
        showCluster(dispatch);
      }
    });
  };

  const onCheckSql = () => {
    handleModalVisible(true);
  };

  const onGetStreamGraph=()=>{
    let selectsql = null;
    if (current.monaco.current) {
      let selection = current.monaco.current.editor.getSelection();
      selectsql = current.monaco.current.editor.getModel().getValueInRange(selection);
    }
    if (selectsql == null || selectsql == '') {
      selectsql = current.value;
    }
    let useSession = !!currentSession.session;
    let param = {
      ...current.task,
      useSession: useSession,
      session: currentSession.session,
      configJson: JSON.stringify(current.task.config),
      statement: selectsql,
    };
    const res = getJobPlan(param);
    handleGraphModalVisible(true);
    res.then((result)=>{
      if(result.code==0){
        setGraphData(buildGraphData(result.datas));
      }else{
        setGraphData(undefined);
      }
    })
  };

  const buildGraphData=(data)=>{
    let edges = [];
    for(let i in data.nodes){
      data.nodes[i].id=data.nodes[i].id.toString();
      data.nodes[i].value={
        title:data.nodes[i].pact,
        items: [
          {
            text: getRangeText(data.nodes[i].description),
          },
          {
            text: '\r\nParallelism: ',
            value: '\r\n  '+data.nodes[i].parallelism,
          },
        ],
      };
      if(data.nodes[i].inputs){
        for(let j in data.nodes[i].inputs){
          edges.push({source: data.nodes[i].inputs[j].id.toString(),
            target: data.nodes[i].id.toString(),
            value: data.nodes[i].inputs[j].ship_strategy})
        }
      }
    }
    data.edges = edges;
    return data;
  };

  const getRangeText = (str:string) => {
    str = escape2Html(str);
    var canvas = getRangeText.canvas || (getRangeText.canvas = document.createElement("canvas"));
    var context = canvas.getContext("2d");
    context.font = "10px sans-serif";
    let result = '';
    let count = 1;
    for(let i=0,len=str.length;i<len;i++){
      result += str[i];
      let width = context.measureText(result).width;
      if(width >= 110*count) {
        result += '\r\n';
        count++;
      }
    }
    return result;
  };

  const getTextWidth = (text:string, font:string) => {
    var canvas = getTextWidth.canvas || (getTextWidth.canvas = document.createElement("canvas"));
    var context = canvas.getContext("2d");
    context.font = font;
    var metrics = context.measureText(text);
    return metrics.width;
  }

  const escape2Html = (str:string) => {
    let arrEntities={'lt':'<','gt':'>','nbsp':' ','amp':'&','quot':'"'};
    return str.replace(/&(lt|gt|nbsp|amp|quot);/ig,function(all,t){return arrEntities[t];});
  }

  const saveSqlAndSettingToTask = () => {
    props.saveTask(current);
  };

  const exportSql = () => {
    handleExportModalVisible(true);
  };

  const runMenu = (
    <Menu>
      <Menu.Item onClick={execute}>SQL 查询</Menu.Item>
      <Menu.Item onClick={submit}>提交作业</Menu.Item>
    </Menu>
  );

  const getPathItem = (paths) => {
    let itemList = [];
    for (let item of paths) {
      itemList.push(<Breadcrumb.Item>{item}</Breadcrumb.Item>)
    }
    return itemList;
  };

  const showHelp = () => {
    Modal.info({
      title: '使用帮助',
      width: 1000,
      content: (
        <StudioHelp/>
      ),
      onOk() {
      },
    });
  };
  return (
    <Row className={styles.container}>
      <Col span={24}>
        <div>
          <Space>
            <Dropdown overlay={menu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                文件
              </Button>
            </Dropdown>
            <Dropdown overlay={menu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                编辑
              </Button>
            </Dropdown>
            <Dropdown overlay={runMenu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                执行
              </Button>
            </Dropdown>
            <Dropdown overlay={menu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                帮助
              </Button>
            </Dropdown>
          </Space>
        </div>
      </Col>
      <Divider className={styles["ant-divider-horizontal-0"]}/>
      <Col span={24}>
        <Row>
          <Col span={4}>
            <Breadcrumb className={styles["dw-path"]}>
              <EnvironmentOutlined/>
              <Divider type="vertical"/>
              {getPathItem(currentPath)}
            </Breadcrumb>
          </Col>
          <Col span={12}>
            {currentSession.session &&
            (
              <Breadcrumb className={styles["dw-path"]}>
                <Divider type="vertical"/>
                <MessageOutlined/>
                <Divider type="vertical"/>
                {currentSession.session}
                <Divider type="vertical"/>
                <ClusterOutlined/>
                <Divider type="vertical"/>
                {currentSession.sessionConfig.useRemote ?
                  currentSession.sessionConfig.clusterName : '本地模式'}
              </Breadcrumb>
            )}
          </Col>
          {current?
          <Col span={8}>
            <Button
              type="text"
              icon={<FileAddTwoTone twoToneColor="#ddd"/>}
            />
            <Button
              type="text"
              icon={<FolderOpenTwoTone twoToneColor="#ddd"/>}
            />
            <Tooltip title="保存当前的 FlinkSql 及配置">
              <Button
                type="text"
                icon={<SaveTwoTone/>}
                onClick={saveSqlAndSettingToTask}
              />
            </Tooltip>
            <Tooltip title="导出当前的 Sql 及配置">
              <Button
                type="text"
                icon={<SnippetsTwoTone />}
                onClick={exportSql}
              />
            </Tooltip>
            <Divider type="vertical"/>
            <Tooltip title="检查当前的 FlinkSql">
              <Button
                type="text"
                icon={<SafetyCertificateTwoTone/>}
                onClick={onCheckSql}
              />
            </Tooltip>
            {(!current.task.dialect||current.task.dialect === DIALECT.FLINKSQL) &&(
            <Tooltip title="获取当前的 FlinkSql 的执行图">
              <Button
                type="text"
                icon={<FlagTwoTone/>}
                onClick={onGetStreamGraph}
              />
            </Tooltip>)}
            {(!current.task.dialect||current.task.dialect === DIALECT.FLINKSQL||isSql( current.task.dialect )) &&(
            <Tooltip title="执行当前的 SQL">
              <Button
                type="text"
                icon={<PlayCircleTwoTone/>}
                //loading={loadings[2]}
                onClick={execute}
              />
            </Tooltip>)}
            {(!current.task.dialect||current.task.dialect === DIALECT.FLINKSQL||isSql( current.task.dialect )) &&(<>
              <Tooltip title="提交当前的作业到集群，提交前请手动保存">
                <Button
                  type="text"
                  icon={<RocketTwoTone/>}
                  onClick={submit}
                />
              </Tooltip>
              <Popconfirm
                title="您确定要停止所有的 FlinkSql 任务吗？"
                // onConfirm={confirm}
                //onCancel={cancel}
                okText="停止"
                cancelText="取消"
              >
                <Tooltip title="停止所有的 FlinkSql 任务，暂不可用">
                  <Button
                    type="text"
                    icon={<PauseCircleTwoTone twoToneColor="#ddd"/>}
                  />
                </Tooltip>
              </Popconfirm></>)}
            <Divider type="vertical"/>
            <Button
              type="text"
              icon={<DiffTwoTone twoToneColor="#ddd"/>}
            />
            <Button
              type="text"
              icon={<CopyTwoTone twoToneColor="#ddd"/>}
            />
            <Button
              type="text"
              icon={<DeleteTwoTone twoToneColor="#ddd"/>}
            />
            <Tooltip title="查看使用帮助">
              <Button
                type="text"
                icon={<QuestionCircleTwoTone/>}
                onClick={showHelp}
              />
            </Tooltip>
          </Col>:undefined}
        </Row>
      </Col>
      <StudioExplain
        modalVisible={modalVisible}
        onClose={()=>{handleModalVisible(false)}}
      />
      <Modal
        width={1000}
        bodyStyle={{padding: '32px 40px 48px'}}
        destroyOnClose
        title="FlinkSQL 的 JobPlan"
        visible={graphModalVisible}
        onCancel={() => handleGraphModalVisible(false)}
      >
        <StudioGraph data={graphData} />
      </Modal>
      {current?
      <ModalForm
        title={`${current.task.alias} 的 ${current.task.dialect} 导出`}
        visible={exportModalVisible}
        width={1000}
        modalProps={{
          maskClosable:false,
          bodyStyle:{
            padding: '5px'
          }
        }}
        onVisibleChange={handleExportModalVisible}
        submitter={{
          submitButtonProps: {
            style: {
              display: 'none',
            },
          },
        }}
      >
        <SqlExport id={current.task.id} />
      </ModalForm>:undefined}
    </Row>
  );
};


const mapDispatchToProps = (dispatch: Dispatch)=>({
  saveTask:(current: any)=>dispatch({
    type: "Studio/saveTask",
    payload: current.task,
  }),saveTabs:(tabs: any)=>dispatch({
    type: "Studio/saveTabs",
    payload: tabs,
  }),
});

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  currentPath: Studio.currentPath,
  tabs: Studio.tabs,
  refs: Studio.refs,
  currentSession: Studio.currentSession,
}),mapDispatchToProps)(StudioMenu);
