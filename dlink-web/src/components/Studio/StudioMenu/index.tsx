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


import styles from "./index.less";
import {Menu, Dropdown, Tooltip, Row, Col, notification, Modal, message} from "antd";
import {
  PauseCircleTwoTone, CarryOutTwoTone, DeleteTwoTone, PlayCircleTwoTone, CameraTwoTone, SnippetsTwoTone,
  FileAddTwoTone, FolderOpenTwoTone, SafetyCertificateTwoTone, SaveTwoTone, FlagTwoTone, CodeTwoTone,
  EnvironmentOutlined, SmileOutlined, RocketTwoTone, QuestionCircleTwoTone, MessageOutlined, ClusterOutlined
  , EditTwoTone, RestTwoTone, ShrinkOutlined, ApiTwoTone, SendOutlined
} from "@ant-design/icons";
import Space from "antd/es/space";
import Divider from "antd/es/divider";
import Button from "antd/es/button/button";
import Breadcrumb from "antd/es/breadcrumb/Breadcrumb";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {CODE, postDataArray} from "@/components/Common/crud";
import {executeSql, getJobPlan, getTaskDefinition} from "@/pages/DataStudio/service";
import TaskAPI from "@/pages/API/TaskAPI";
import StudioHelp from "./StudioHelp";
import StudioGraph from "./StudioGraph";
import {
  cancelTask, developTask,
  offLineTask,
  onLineTask, recoveryTask,
  releaseTask,
  showCluster,
  showTables
} from "@/components/Studio/StudioEvent/DDL";
import React, {useCallback, useEffect, useState, useRef} from "react";
import StudioExplain from "../StudioConsole/StudioExplain";
import {
  DIALECT,
  isExecuteSql,
  isOnline,
  isRunningTask,
  isSql,
  isTask,
} from "@/components/Studio/conf";
import {
  ModalForm,
} from '@ant-design/pro-form';
import SqlExport from "@/pages/DataStudio/SqlExport";
import {Dispatch} from "@@/plugin-dva/connect";
import StudioTabs from "@/components/Studio/StudioTabs";
import {isDeletedTask, JOB_LIFE_CYCLE} from "@/components/Common/JobLifeCycle";
import DolphinPush from "@/components/Studio/StudioMenu/DolphinPush";

const menu = (
  <Menu>
    <Menu.Item>敬请期待</Menu.Item>
  </Menu>
);


const StudioMenu = (props: any) => {

  const {isFullScreen, tabs, current, currentPath, form, width, height, refs, dispatch, currentSession} = props;
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [exportModalVisible, handleExportModalVisible] = useState<boolean>(false);
  const [graphModalVisible, handleGraphModalVisible] = useState<boolean>(false);
  const [dolphinModalVisible, handleDolphinModalVisible] = useState<boolean>(false);
  // const [editModalVisible, handleEditModalVisible] = useState<boolean>(false);
  const [graphData, setGraphData] = useState();
  const [dolphinData, setDolphinData] = useState();

  const onKeyDown = useCallback((e) => {
    if (e.keyCode === 83 && (e.ctrlKey === true || e.metaKey)) {
      e.preventDefault();
      if (current) {
        props.saveTask(current);
      }
    }
    if (e.keyCode === 113) {
      e.preventDefault();
      if (current) {
        // handleEditModalVisible(true);
        props.changeFullScreen(true);
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
    if (!isSql(current.task.dialect) && !isOnline(current.task.type)) {
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
      taskId: current.task.id,
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
        res.datas?.jobInstanceId && props.changeTaskJobInstance(current.task.id, res.datas?.jobInstanceId);
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
          res.datas[0].jobInstanceId && props.changeTaskJobInstance(current.task.id, res.datas[0].jobInstanceId);
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

  const onGetStreamGraph = () => {
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
    res.then((result) => {
      if (result.code == CODE.SUCCESS) {
        setGraphData(buildGraphData(result.datas));
      } else {
        message.error(`获取作业执行计划失败，原因：\n${result.msg}`);
        setGraphData(undefined);
      }
    })
  };

  //获取当前task关联的海豚数据
  const viewDolphinCon = () => {
    const res = getTaskDefinition(current.task.id);
    res.then((result) => {
      if (result.code == CODE.SUCCESS) {
        setDolphinData(result.datas);
      } else {
        message.error(`获取海豚数据失败，原因：\n${result.msg}`);
        setDolphinData(undefined);
      }
      handleDolphinModalVisible(true);
    })
  };

  const buildGraphData = (data) => {
    let edges = [];
    for (let i in data.nodes) {
      data.nodes[i].id = data.nodes[i].id.toString();
      data.nodes[i].value = {
        title: data.nodes[i].pact,
        items: [
          {
            text: getRangeText(data.nodes[i].description),
          },
          {
            text: '\r\nParallelism: ',
            value: '\r\n  ' + data.nodes[i].parallelism,
          },
        ],
      };
      if (data.nodes[i].inputs) {
        for (let j in data.nodes[i].inputs) {
          edges.push({
            source: data.nodes[i].inputs[j].id.toString(),
            target: data.nodes[i].id.toString(),
            value: data.nodes[i].inputs[j].ship_strategy
          })
        }
      }
    }
    data.edges = edges;
    return data;
  };

  const getRangeText = (str: string) => {
    str = escape2Html(str);
    var canvas = getRangeText.canvas || (getRangeText.canvas = document.createElement("canvas"));
    var context = canvas.getContext("2d");
    context.font = "10px sans-serif";
    let result = '';
    let count = 1;
    for (let i = 0, len = str.length; i < len; i++) {
      result += str[i];
      let width = context.measureText(result).width;
      if (width >= 110 * count) {
        result += '\r\n';
        count++;
      }
    }
    return result;
  };

  const getTextWidth = (text: string, font: string) => {
    var canvas = getTextWidth.canvas || (getTextWidth.canvas = document.createElement("canvas"));
    var context = canvas.getContext("2d");
    context.font = font;
    var metrics = context.measureText(text);
    return metrics.width;
  }

  const escape2Html = (str: string) => {
    let arrEntities = {'lt': '<', 'gt': '>', 'nbsp': ' ', 'amp': '&', 'quot': '"'};
    return str.replace(/&(lt|gt|nbsp|amp|quot);/ig, function (all, t) {
      return arrEntities[t];
    });
  }

  const toFullScreen = () => {
    if (current) {
      props.changeFullScreen(true);
    }
  };

  const saveSqlAndSettingToTask = () => {
    props.saveTask(current);
  };

  const exportSql = () => {
    handleExportModalVisible(true);
  };

  const toReleaseTask = () => {
    Modal.confirm({
      title: '发布作业',
      content: `确定发布作业【${current.task.alias}】吗？请确认您的作业是否已经被保存！`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = releaseTask(current.task.id);
        res.then((result) => {
          if (result.code == CODE.SUCCESS) {
            props.changeTaskStep(current.task.id, JOB_LIFE_CYCLE.RELEASE);
            message.success(`发布作业【${current.task.alias}】成功`);
          } else {
            message.error(`发布作业【${current.task.alias}】失败，原因：\n${result.msg}`);
          }
        });
      }
    });
  };

  const toDevelopTask = () => {
    Modal.confirm({
      title: '维护作业',
      content: `确定维护作业【${current.task.alias}】吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = developTask(current.task.id);
        res.then((result) => {
          result.datas && props.changeTaskStep(current.task.id, JOB_LIFE_CYCLE.DEVELOP);
          if (result.code == CODE.SUCCESS) {
            message.success(`维护作业【${current.task.alias}】成功`);
          }
        });
      }
    });
  };

  const toOnLineTask = () => {
    Modal.confirm({
      title: '上线作业',
      content: `确定上线作业【${current.task.alias}】吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = onLineTask(current.task.id);
        res.then((result) => {
          if (result.code === CODE.SUCCESS) {
            props.changeTaskStep(current.task.id, JOB_LIFE_CYCLE.ONLINE);
            result.datas?.jobInstanceId && props.changeTaskJobInstance(current.task.id, result.datas?.jobInstanceId);
            message.success(`上线作业【${current.task.alias}】成功`);
          } else {
            message.error(`上线作业【${current.task.alias}】失败，原因：\n${result.msg}`);
          }
        });
      }
    });
  };

  const handleCancelTask = (type: string) => {
    Modal.confirm({
      title: '停止作业',
      content: `确定停止作业【${current.task.alias}】吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = offLineTask(current.task.id, type);
        res.then((result) => {
          if (result.code === CODE.SUCCESS) {
            if (current.task.step === JOB_LIFE_CYCLE.ONLINE) {
              props.changeTaskStep(current.task.id, JOB_LIFE_CYCLE.RELEASE);
            }
            props.changeTaskJobInstance(current.task.id, 0);
            message.success(`停止作业【${current.task.alias}】成功`);
          } else {
            message.error(`停止作业【${current.task.alias}】失败，原因：\n${result.msg}`);
          }
        });
      }
    });
  };

  const toOffLineTask = (type: string) => {
    Modal.confirm({
      title: '下线作业',
      content: `确定下线作业【${current.task.alias}】吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = offLineTask(current.task.id, type);
        res.then((result) => {
          if (result.code === CODE.SUCCESS) {
            props.changeTaskStep(current.task.id, JOB_LIFE_CYCLE.RELEASE);
            props.changeTaskJobInstance(current.task.id, 0);
            message.success(`下线作业【${current.task.alias}】成功`);
          } else {
            message.error(`下线作业【${current.task.alias}】失败，原因：\n${result.msg}`);
          }
        });
      }
    });
  };

  const toCancelTask = () => {
    Modal.confirm({
      title: '注销作业',
      content: `确定注销作业【${current.task.alias}】吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = cancelTask(current.task.id);
        res.then((result) => {
          if (result.code === CODE.SUCCESS) {
            props.changeTaskStep(current.task.id, JOB_LIFE_CYCLE.CANCEL);
            message.success(`注销作业【${current.task.alias}】成功`);
          } else {
            message.error(`注销作业【${current.task.alias}】失败，原因：\n${result.msg}`);
          }
        });
      }
    });
  };

  const toRecoveryTask = () => {
    Modal.confirm({
      title: '恢复作业',
      content: `确定恢复作业【${current.task.alias}】吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = recoveryTask(current.task.id);
        res.then((result) => {
          result.datas && props.changeTaskStep(current.task.id, JOB_LIFE_CYCLE.DEVELOP);
          if (result.code == CODE.SUCCESS) {
            message.success(`恢复作业【${current.task.alias}】成功`);
          }
        });
      }
    });
  };

  const isShowGetStreamGraphBtn = () => {
    return (!current.task.dialect || current.task.dialect === DIALECT.FLINKSQL);
  };

  const isShowExecuteBtn = () => {
    return !isDeletedTask(current.task.step) && isExecuteSql(current.task.dialect) && !isRunningTask(current.task.jobInstanceId);
  };

  const isShowSubmitBtn = () => {
    return !isDeletedTask(current.task.step) && isTask(current.task.dialect) && !isRunningTask(current.task.jobInstanceId);
  };

  const isShowCancelTaskBtn = () => {
    return !isDeletedTask(current.task.step) && isTask(current.task.dialect) && isRunningTask(current.task.jobInstanceId);
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
      itemList.push(<Breadcrumb.Item key={item}>{item}</Breadcrumb.Item>)
    }
    return itemList;
  };

  const showAPI = () => {
    Modal.info({
      title: current.task.alias + ' API 手册',
      width: 1000,
      content: (
        <TaskAPI task={current.task}/>
      ),
      onOk() {
      },
    });
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
      {/*<Col span={24}>
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
      </Col>*/}
      <Divider className={styles["ant-divider-horizontal-0"]}/>
      <Col span={24}>
        <Row>
          <Col span={16}>
            <Breadcrumb className={styles["dw-path"]}>
              <EnvironmentOutlined style={{lineHeight: '32px'}}/>
              <Divider type="vertical" style={{height: 'unset'}}/>
              {getPathItem(currentPath)}
            </Breadcrumb>
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
          {current?.task ?
            <Col span={8}>
              <Tooltip title="全屏开发">
                <Button
                  type="text"
                  icon={<CodeTwoTone/>}
                  onClick={toFullScreen}
                />
              </Tooltip>
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
                  icon={<SnippetsTwoTone/>}
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
              {isShowGetStreamGraphBtn() && (
                <Tooltip title="获取当前的 FlinkSql 的执行图">
                  <Button
                    type="text"
                    icon={<FlagTwoTone/>}
                    onClick={onGetStreamGraph}
                  />
                </Tooltip>)}
              {isShowExecuteBtn() && (
                <Tooltip title="执行当前的 SQL">
                  <Button
                    type="text"
                    icon={<PlayCircleTwoTone/>}
                    //loading={loadings[2]}
                    onClick={execute}
                  />
                </Tooltip>)}
              {isShowSubmitBtn() && (<>
                <Tooltip title="提交当前的作业到集群，提交前请手动保存">
                  <Button
                    type="text"
                    icon={<RocketTwoTone/>}
                    onClick={submit}
                  />
                </Tooltip>
              </>)}
              {isShowSubmitBtn() && (<>
                <Tooltip title="推送到海豚调度">
                  <Button
                    type="text" style={{color: '#248FFF'}}
                    icon={<SendOutlined/>}
                    onClick={viewDolphinCon}
                  />
                </Tooltip>
              </>)}
              {isShowCancelTaskBtn() &&
                <Tooltip title="停止">
                  <Button
                    type="text"
                    icon={<PauseCircleTwoTone/>}
                    onClick={() => handleCancelTask('canceljob')}
                  />
                </Tooltip>
              }
              <Divider type="vertical"/>
              {current.task.step == JOB_LIFE_CYCLE.DEVELOP ?
                <Tooltip title="发布，发布后将无法修改">
                  <Button
                    type="text"
                    icon={<CameraTwoTone/>}
                    onClick={toReleaseTask}
                  />
                </Tooltip> : undefined
              }{current.task.step == JOB_LIFE_CYCLE.RELEASE ?
              <><Tooltip title="维护，点击进入编辑状态">
                <Button
                  type="text"
                  icon={<EditTwoTone/>}
                  onClick={toDevelopTask}
                />
              </Tooltip>
                <Tooltip title="上线，上线后自动恢复、告警等将生效">
                  <Button
                    type="text"
                    icon={<CarryOutTwoTone/>}
                    onClick={toOnLineTask}
                  />
                </Tooltip></> : undefined
            }{current.task.step == JOB_LIFE_CYCLE.ONLINE ?
              <Tooltip title="下线，将进入最新发布状态">
                <Button
                  type="text"
                  icon={<PauseCircleTwoTone/>}
                  onClick={() => toOffLineTask('cancel')}
                />
              </Tooltip> : undefined
            }{(current.task.step != JOB_LIFE_CYCLE.ONLINE && current.task.step != JOB_LIFE_CYCLE.CANCEL) ?
              <Tooltip title="注销，将进入回收站">
                <Button
                  type="text"
                  icon={<DeleteTwoTone/>}
                  onClick={toCancelTask}
                />
              </Tooltip> : undefined
            }{current.task.step == JOB_LIFE_CYCLE.CANCEL ?
              <Tooltip title="恢复，将进入维护模式">
                <Button
                  type="text"
                  icon={<RestTwoTone/>}
                  onClick={toRecoveryTask}
                />
              </Tooltip> : undefined
            }
              <Tooltip title="查看 API">
                <Button
                  type="text"
                  icon={<ApiTwoTone/>}
                  onClick={showAPI}
                />
              </Tooltip>
              <Tooltip title="查看使用帮助">
                <Button
                  type="text"
                  icon={<QuestionCircleTwoTone/>}
                  onClick={showHelp}
                />
              </Tooltip>
            </Col> : <Col span={8}><Tooltip title="查看使用帮助">
              <Button
                type="text"
                icon={<QuestionCircleTwoTone/>}
                onClick={showHelp}
              />
            </Tooltip></Col>}
        </Row>
      </Col>
      <StudioExplain
        modalVisible={modalVisible}
        onClose={() => {
          handleModalVisible(false)
        }}
      />
      <Modal
        width={1000}
        bodyStyle={{padding: '32px 40px 48px'}}
        destroyOnClose
        title="FlinkSQL 的 JobPlan"
        visible={graphModalVisible}
        onCancel={() => handleGraphModalVisible(false)}
      >
        <StudioGraph data={graphData}/>
      </Modal>
      <Modal
        width={700}
        bodyStyle={{padding: '32px 40px 48px'}}
        destroyOnClose
        title="推送到海豚调度"
        visible={dolphinModalVisible}
        onCancel={() => handleDolphinModalVisible(false)}
        footer={[]}
      >
        <DolphinPush data={dolphinData} taskCur={current} handleDolphinModalVisible={handleDolphinModalVisible}/>
      </Modal>
      {current?.task ?
        <ModalForm
          title={`${current.task.alias} 的 ${current.task.dialect} 导出`}
          visible={exportModalVisible}
          width={1000}
          modalProps={{
            maskClosable: false,
            bodyStyle: {
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
          <SqlExport id={current.task.id}/>
        </ModalForm> : undefined}
      {current && isFullScreen ? <Modal
        width={width}
        bodyStyle={{padding: 0}}
        style={{top: 0, padding: 0, margin: 0, maxWidth: '100vw'}}
        destroyOnClose
        maskClosable={false}
        closable={true}
        closeIcon={
          <Tooltip title="退出全屏">
            <Button
              icon={<ShrinkOutlined/>}
              type="primary"
              style={{position: "fixed", right: "0"}}>
              退出
            </Button>
          </Tooltip>}
        visible={isFullScreen}
        footer={null}
        onCancel={() => {
          props.changeFullScreen(false);
        }}>
        <StudioTabs width={width} height={height}/>
      </Modal> : undefined}
    </Row>
  );
};


const mapDispatchToProps = (dispatch: Dispatch) => ({
  saveTask: (current: any) => dispatch({
    type: "Studio/saveTask",
    payload: current.task,
  }), saveTabs: (tabs: any) => dispatch({
    type: "Studio/saveTabs",
    payload: tabs,
  }), changeFullScreen: (isFull: boolean) => dispatch({
    type: "Studio/changeFullScreen",
    payload: isFull,
  }), changeTaskStep: (id: number, step: number) => dispatch({
    type: "Studio/changeTaskStep",
    payload: {
      id, step
    },
  }), changeTaskJobInstance: (id: number, jobInstanceId: number) => dispatch({
    type: "Studio/changeTaskJobInstance",
    payload: {
      id, jobInstanceId
    },
  }),
});

export default connect(({Studio}: { Studio: StateType }) => ({
  isFullScreen: Studio.isFullScreen,
  current: Studio.current,
  currentPath: Studio.currentPath,
  tabs: Studio.tabs,
  refs: Studio.refs,
  currentSession: Studio.currentSession,
}), mapDispatchToProps)(StudioMenu);
