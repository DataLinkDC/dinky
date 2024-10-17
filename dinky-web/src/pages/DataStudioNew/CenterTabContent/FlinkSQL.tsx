import {CenterTabType, LayoutState} from "@/pages/DataStudioNew/model";
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
import {PageContainer, ProForm, ProFormInstance} from "@ant-design/pro-components";
import {useAsyncEffect} from "ahooks";
import {getTaskDetails} from "@/pages/DataStudio/LeftContainer/Project/service";
import {SelectFlinkEnv} from "@/pages/DataStudioNew/CenterTabContent/RunToolbar/SelectFlinkEnv";
import {SelectFlinkRunMode} from "@/pages/DataStudioNew/CenterTabContent/RunToolbar/SelectFlinkRunMode";
import {mapDispatchToProps} from "@/pages/DataStudioNew/DvaFunction";
import {TaskInfo} from "@/pages/DataStudioNew/CenterTabContent/TaskInfo";
import TaskConfig from "@/pages/DataStudioNew/CenterTabContent/TaskConfig";
import {HistoryVersion} from "@/pages/DataStudioNew/CenterTabContent/HistoryVersion";

export type FlinkSqlProps = {
  showDesc: boolean;
  id: string;
  tabType: CenterTabType;
  title: string;
  params: Record<string, any>;
}

export type TaskParams = {
  taskId: number;
  key: number;
}
export  type FlinkSQLState = {
  taskId:number;
  statement: string;
  name:string;
  dialect:string
  step: number;
  flinkEnvId: number;
  versionId: number;
}

const toolbarSize = 40;
export const FlinkSQL = (props: FlinkSqlProps & any) => {
  const {showDesc, id, tempData, updateAction, updateProject} = props;
  const params = props.params as TaskParams;
  const containerRef = useRef<HTMLDivElement>(null);
  const [codeEditorWidth, setCodeEditorWidth] = useState(0)
  const [selectRightToolbar, setSelectRightToolbar] = useState<string | undefined>(undefined);
  const [loading, setLoading] = useState<boolean>(true)
  const [currentState, setCurrentState] = useState<FlinkSQLState>({
    taskId: params.taskId,
    statement: '',
    name: '',
    dialect:'',
    step: 0,
    flinkEnvId: -1,
    versionId: 0
  })

  const formRef = useRef<ProFormInstance>();

  const rightToolbarItem:TabsProps['items'] = [{
    label:'配置',
    key: 'config',
    children: <TaskConfig tempData={tempData}  />
  },{
    label:'信息',
    key: 'info',
    children: <TaskInfo params={{...currentState}}/>
  },{
    label:'历史版本',
    key: 'historyVersion',
    children: <HistoryVersion taskId={currentState.taskId}/>
  },
  ]


  useAsyncEffect(async () => {
    const taskDetail = await getTaskDetails(params.taskId)
    if (taskDetail) {
      setCurrentState(prevState => ({
        ...prevState,
        statement: taskDetail.statement,
        name: taskDetail.name,
        dialect: taskDetail.dialect,
        step: taskDetail.step,
        versionId: taskDetail.versionId,
        flinkEnvId: taskDetail.envId
      }))
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
            // type: selectRunMode,
            flinkMode: ['local'],
            flinkEnvId: currentState.flinkEnvId
          }}
          formRef={formRef}
          submitter={false}
          layout='horizontal'
          variant={"filled"}
          // disabled={current?.step === JOB_LIFE_CYCLE.PUBLISH || isLockTask} // 当该任务处于发布状态时 表单禁用 不允许修改 | when this job is publishing, the form is disabled , and it is not allowed to modify
          // onValuesChange={debounce(onValuesChange, 500)}
          syncToInitialValues
        >
          <Flex className={"run-toolbar"} wrap>
            {/* 运行工具栏*/}
            {/*todo 按钮可能会超过当前布局，解决方案：超过布局的按钮需要用更多来显示*/}
            <RunToolBarButton showDesc={showDesc} desc={"保存"} icon={<SaveOutlined/>}/>
            <RunToolBarButton showDesc={showDesc} desc={"检查"} icon={<AuditOutlined/>}/>
            <RunToolBarButton showDesc={showDesc} desc={"预览DAG"} icon={<ApartmentOutlined/>}/>
            <RunToolBarButton showDesc={showDesc} desc={"血缘"} icon={<PartitionOutlined/>}/>

            <Divider type={'vertical'} style={{height: "100%"}}/>


            <SelectFlinkEnv flinkEnv={tempData.flinkEnv} value={currentState.flinkEnvId}
                            onChange={value => setCurrentState(prevState => ({...prevState, flinkEnvId: value}))}/>

            <SelectFlinkRunMode data={tempData.flinkCluster}/>

            <Divider type={'vertical'} style={{height: "100%"}}/>


            <RunToolBarButton showDesc={showDesc} color={'green'} desc={"运行"} icon={<CaretRightOutlined/>}
                              onClick={async () => {
                                const val1 = await formRef.current?.validateFields();
                                console.log(val1)
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
        <Row style={{height: 'inherit'}}>
          <Col style={{width: codeEditorWidth - toolbarSize}}>
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
                  <Panel>
                    <PageContainer
                      title={false}
                      extra={[
                        <Button key="close" icon={<CloseOutlined/>} type={'text'}
                                onClick={() => setSelectRightToolbar(undefined)}/>,
                      ]}
                    >
                      {rightToolbarItem.find(item => item.label === selectRightToolbar)?.children}
                    </PageContainer>
                  </Panel>
                </>
              )}
            </PanelGroup>
          </Col>
          <Flex wrap vertical className={'right-toolbar'} style={{width: toolbarSize,}}>
            {rightToolbarItem.map(item=>item.label?.toString()).map((item) => (
              <div key={item} className={'right-toolbar-item ' + (selectRightToolbar === item ? 'right-toolbar-item-active' : '')}
                   onClick={() => setSelectRightToolbar(item)}>
                {item}
              </div>
            ))
            }
          </Flex>
        </Row>


      </Flex>
    </Skeleton>)
}

export default connect(
  ({DataStudio}: { DataStudio: LayoutState }) => ({
    showDesc: DataStudio.toolbar.showDesc,
    tempData: DataStudio.tempData
  }), mapDispatchToProps)(FlinkSQL);
// export default FlinkSQL;
