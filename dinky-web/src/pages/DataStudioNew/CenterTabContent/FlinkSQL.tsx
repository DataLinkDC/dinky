import {CenterTab, LayoutState} from "@/pages/DataStudioNew/model";
import {Button, Col, Divider, Flex, Row, Skeleton, TabsProps} from "antd";
import "./index.less"
import React, {useEffect, useRef, useState} from "react";
import {handleInitEditorAndLanguageOnBeforeMount} from "@/components/CustomEditor/function";
import {convertCodeEditTheme} from "@/utils/function";
import {Editor} from "@monaco-editor/react";
import {Panel, PanelGroup} from "react-resizable-panels";
import {
  ApartmentOutlined,
  AuditOutlined,
  BugOutlined,
  CaretRightOutlined,
  ClearOutlined,
  CloseOutlined,
  EnvironmentOutlined,
  FundOutlined,
  PartitionOutlined,
  PauseOutlined,
  RotateRightOutlined,
  SaveOutlined
} from "@ant-design/icons";
import RunToolBarButton from "@/pages/DataStudioNew/components/RunToolBarButton";
import {connect} from "@umijs/max";
import CusPanelResizeHandle from "@/pages/DataStudioNew/components/CusPanelResizeHandle";
import {ProForm, ProFormInstance} from "@ant-design/pro-components";
import {useAsyncEffect} from "ahooks";
import {getTaskDetails, putTask} from "@/pages/DataStudio/LeftContainer/Project/service";
import {SelectFlinkEnv} from "@/pages/DataStudioNew/CenterTabContent/RunToolbar/SelectFlinkEnv";
import {SelectFlinkRunMode} from "@/pages/DataStudioNew/CenterTabContent/RunToolbar/SelectFlinkRunMode";
import {mapDispatchToProps} from "@/pages/DataStudioNew/DvaFunction";
import {TaskInfo} from "@/pages/DataStudioNew/CenterTabContent/TaskInfo";
import TaskConfig from "@/pages/DataStudioNew/CenterTabContent/TaskConfig";
import {HistoryVersion} from "@/pages/DataStudioNew/CenterTabContent/HistoryVersion";
import {FlinkTaskRunType} from "@/pages/DataStudioNew/type";
import {TaskExtConfig} from "@/types/Studio/data";
import {JOB_LIFE_CYCLE} from "@/pages/DevOps/constants";
import {debounce} from "lodash";
import {executeSql} from "@/pages/DataStudio/HeaderContainer/service";
import {l} from "@/utils/intl";

export type FlinkSqlProps = {
  showDesc: boolean;
  tabData: CenterTab;
}

export type TaskParams = {
  taskId: number;
  key: number;
}

export  type FlinkSQLState = {
  taskId: number;
  statement: string;
  name: string;
  type: FlinkTaskRunType;
  dialect: string
  envId: number;
  versionId: number;
  savePointStrategy: number;
  savePointPath: string;
  parallelism: number;
  fragment: boolean;
  batchModel: boolean;
  clusterId?: number | null;
  clusterConfigurationId?: number | null;
  databaseId?: number;
  alertGroupId?: number;
  configJson: TaskExtConfig;
  note: string;
  step: number;
  firstLevelOwner: number;
  secondLevelOwners: number[];
  createTime: Date;
  updateTime: Date;
}

const toolbarSize = 40;
export const FlinkSQL = (props: FlinkSqlProps & any) => {
  const {showDesc, tempData, updateAction, updateProject, updateCenterTab} = props;
  const {params, title} = props.tabData as CenterTab;
  const containerRef = useRef<HTMLDivElement>(null);
  const [codeEditorWidth, setCodeEditorWidth] = useState(0);

  const [selectRightToolbar, setSelectRightToolbar] = useState<string | null>(null);

  const [loading, setLoading] = useState<boolean>(true);
  const [currentState, setCurrentState] = useState<FlinkSQLState>({
    alertGroupId: -1,
    batchModel: false,
    configJson: {
      udfRefer: [],
      customConfig: {}
    },
    databaseId: 0,
    firstLevelOwner: 0,
    fragment: false,
    note: "",
    parallelism: 0,
    savePointPath: "",
    savePointStrategy: 0,
    secondLevelOwners: [],
    type: "local",
    taskId: params.taskId,
    statement: '',
    name: '',
    dialect: '',
    step: 0,
    envId: -1,
    versionId: 0,
    createTime: new Date(),
    updateTime: new Date()
  });

  const formRef = useRef<ProFormInstance>();


  useAsyncEffect(async () => {
    const taskDetail = await getTaskDetails(params.taskId)
    if (taskDetail) {
      // @ts-ignore
      setCurrentState({...taskDetail, taskId: params.taskId})
    }
    setLoading(false)
  }, [])
  // 数据初始化
  useEffect(() => {
    if (!containerRef.current) {
      return () => {
      }
    }
    // 监控布局宽度高度变化，重新计算树的高度
    const element = containerRef.current!!;
    const observer = new ResizeObserver((entries) => {
      if (entries?.length === 1) {
        // 这里节点理应为一个，减去的高度是为搜索栏的高度
        setCodeEditorWidth(entries[0].contentRect.width);
      }
    });
    observer.observe(element);
    return () => {
      observer.unobserve(element)
    };
  }, [loading])

  const getFlinkMode = () => {
    if (currentState.type === 'local') {
      return ['local']
    }
    if (currentState.type === 'standalone' || currentState.type === 'kubernetes-session' || currentState.type === 'yarn-session') {
      return [currentState.type, currentState.clusterId]
    }
  }

  const onValuesChange = (changedValues: any, allValues: FlinkSQLState) => {
    updateCenterTab({...props.tabData, isUpdate: true})
    if ('flinkMode' in allValues) {
      const mode = (allValues['flinkMode'] as [string, number])[0]
      if (mode === 'local') {
        allValues.clusterId = null
        allValues.clusterConfigurationId = null
      } else if (mode === 'standalone' || mode === 'kubernetes-session' || mode === 'yarn-session') {
        allValues.clusterId = (allValues['flinkMode'] as [string, number])[1]
        allValues.clusterConfigurationId = null
      } else {
        const id = (allValues['flinkMode'] as [string, number])[1]
        allValues.clusterId = null
        allValues.clusterConfigurationId = id
      }
    }
    setCurrentState({...currentState, ...allValues})
  }

  const rightToolbarItem: TabsProps['items'] = [{
    label: '配置',
    key: 'config',
    children: <TaskConfig tempData={tempData} data={currentState} onValuesChange={debounce(onValuesChange, 500)}/>
  }, {
    label: '信息',
    key: 'info',
    children: <TaskInfo params={{...currentState}}/>
  }, {
    label: '历史版本',
    key: 'historyVersion',
    children: <HistoryVersion taskId={currentState.taskId} statement={currentState.statement}
                              updateTime={currentState.updateTime}/>
  },
  ]


  const handleSave = async () => {
    await putTask(currentState)
    updateCenterTab({...props.tabData, isUpdate: false})
  }


  return (
    <Skeleton loading={loading} active
              title={false}
              paragraph={{
                rows: 5,
                width: '100%'
              }}>
      <Flex vertical style={{height: 'inherit', width: '100%'}} ref={containerRef}>
        <ProForm
          size={'middle'}
          initialValues={{
            flinkMode: getFlinkMode(),
            envId: currentState.envId
          }}
          formRef={formRef}
          submitter={false}
          layout='horizontal'
          variant={"filled"}
          // disabled={currentState?.step === JOB_LIFE_CYCLE.PUBLISH || isLockTask} // 当该任务处于发布状态时 表单禁用 不允许修改 | when this job is publishing, the form is disabled , and it is not allowed to modify
          disabled={currentState?.step === JOB_LIFE_CYCLE.PUBLISH} // 当该任务处于发布状态时 表单禁用 不允许修改 | when this job is publishing, the form is disabled , and it is not allowed to modify
          onValuesChange={debounce(onValuesChange, 500)}
          syncToInitialValues
        >
          <Flex className={"run-toolbar"}>
            {/* 运行工具栏*/}
            {/*todo 按钮可能会超过当前布局，解决方案：超过布局的按钮需要用更多来显示*/}
            <RunToolBarButton showDesc={showDesc} desc={"保存"} icon={<SaveOutlined/>} onClick={handleSave}/>
            <RunToolBarButton showDesc={showDesc} desc={"检查"} icon={<AuditOutlined/>}/>
            <RunToolBarButton showDesc={showDesc} desc={"预览DAG"} icon={<ApartmentOutlined/>}/>
            <RunToolBarButton showDesc={showDesc} desc={"血缘"} icon={<PartitionOutlined/>}/>

            <Divider type={'vertical'} style={{height: "100%"}}/>


            <SelectFlinkEnv flinkEnv={tempData.flinkEnv} value={currentState.envId}
                            onChange={value => setCurrentState(prevState => ({...prevState, envId: value}))}/>

            <SelectFlinkRunMode data={tempData.flinkCluster}/>

            <Divider type={'vertical'} style={{height: "100%"}}/>


            <RunToolBarButton showDesc={showDesc} color={'green'} desc={"运行"} icon={<CaretRightOutlined/>}
                              onClick={async () => {
                                await handleSave()
                                await executeSql(
                                  l('pages.datastudio.editor.submitting', '', {jobName: title}),
                                  params.taskId
                                );
                                updateAction({
                                  actionType: 'run',
                                  params: {
                                    taskId: params.taskId,
                                    envId: currentState.envId
                                  }
                                })
                              }}/>
            <RunToolBarButton showDesc={showDesc} color={'red'} desc={"预览"} icon={<BugOutlined/>}/>
            <RunToolBarButton showDesc={showDesc} color={'red'} desc={"停止"} icon={<PauseOutlined/>}/>

            <Divider type={'vertical'} style={{height: "100%"}}/>
            <RunToolBarButton showDesc={showDesc} desc={"格式化"} icon={<ClearOutlined/>}/>
            <RunToolBarButton showDesc={showDesc} desc={"定位"} icon={<EnvironmentOutlined/>} onClick={async () => {
              updateProject({selectedKeys: [params.key]})
            }}/>

            <Divider type={'vertical'} style={{height: "100%"}}/>

            <RunToolBarButton showDesc={showDesc} desc={"发布"} icon={<FundOutlined/>}/>
            <RunToolBarButton showDesc={showDesc} desc={"运维"} icon={<RotateRightOutlined/>}/>


          </Flex>
        </ProForm>
        <Flex flex={1} style={{height: 0}}>
          <Row style={{width: "100%", height: '100%'}}>
            <Col style={{width: codeEditorWidth - toolbarSize, height: '100%'}}>
              <PanelGroup direction={"horizontal"}>
                <Panel>
                  <Editor
                    beforeMount={(monaco) => handleInitEditorAndLanguageOnBeforeMount(monaco, true)}
                    width={'100%'}
                    height={"100%"}
                    value={currentState.statement}
                    language={"sql"}
                    options={{minimap: {enabled: true, side: 'right'}, scrollBeyondLastLine: false}}
                    // options={finalEditorOptions}
                    className={'editor-develop'}
                    // onMount={editorDidMountChange}
                    // onChange={onChange}
                    //zh-CN: 因为在 handleInitEditorAndLanguageOnBeforeMount 中已经注册了自定义语言，所以这里的作用仅仅是用来切换主题 不需要重新加载自定义语言的 token 样式 , 所以这里入参需要为空, 否则每次任意的 props 改变时(包括高度等),会出现编辑器闪烁的问题
                    //en-US: because the custom language has been registered in handleInitEditorAndLanguageOnBeforeMount, so the only purpose here is to switch the theme, and there is no need to reload the token style of the custom language, so the incoming parameters here need to be empty, otherwise any props change (including height, etc.) will cause the editor to flash
                    theme={convertCodeEditTheme()}
                  />
                </Panel>
                {selectRightToolbar && (
                  <>
                    <CusPanelResizeHandle/>
                    <Panel className={'right-toolbar-container'} style={{overflowY: 'auto'}}>
                      <Flex gap={5} vertical>
                        <Flex justify={"right"}>
                          <Button key="close" icon={<CloseOutlined/>} type={'text'}
                                  onClick={() => setSelectRightToolbar(null)}/>
                        </Flex>

                        {rightToolbarItem.find(item => item.label === selectRightToolbar)?.children}
                      </Flex>

                    </Panel>
                  </>
                )}
              </PanelGroup>
            </Col>
            <Flex wrap vertical className={'right-toolbar'} style={{width: toolbarSize,}}>
              {rightToolbarItem.map(item => item.label?.toString()).map((item) => (
                <div key={item}
                     className={'right-toolbar-item ' + (selectRightToolbar === item ? 'right-toolbar-item-active' : '')}
                     onClick={() => setSelectRightToolbar(item)}>
                  {item}
                </div>
              ))
              }
            </Flex>
          </Row>
        </Flex>


      </Flex>
    </Skeleton>)
}

export default connect(
  ({DataStudio}: { DataStudio: LayoutState }) => ({
    showDesc: DataStudio.toolbar.showDesc,
    tempData: DataStudio.tempData
  }), mapDispatchToProps)(FlinkSQL);
