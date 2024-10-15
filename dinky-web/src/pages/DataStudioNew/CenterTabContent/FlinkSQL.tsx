import {CenterTabType, LayoutState} from "@/pages/DataStudioNew/model";
import {Button, Col, Divider, Flex, Row, Select, Tabs, TabsProps, Tooltip} from "antd";
import "./index.less"
import React, {useEffect, useRef, useState} from "react";
import {handleInitEditorAndLanguageOnBeforeMount} from "@/components/CustomEditor/function";
import {convertCodeEditTheme} from "@/utils/function";
import {Editor} from "@monaco-editor/react";
import {Panel, PanelGroup, PanelResizeHandle} from "react-resizable-panels";
import {
  ApartmentOutlined,
  AuditOutlined,
  BugOutlined,
  CaretRightOutlined, ClearOutlined, ClusterOutlined, EnvironmentOutlined,
  FundOutlined, PartitionOutlined,
  PauseOutlined,
  RotateRightOutlined,
  SaveOutlined
} from "@ant-design/icons";
import RunToolBarButton from "@/pages/DataStudioNew/components/RunToolBarButton";
import {connect} from "@umijs/max";

export type FlinkSqlProps = {
  showDesc: boolean;
  id: string;
  tabType: CenterTabType;
  title: string;
  params: Record<string, any>;
}


const toolbarSize = 40;

export const FlinkSQL = (props: FlinkSqlProps) => {
  const  {showDesc} = props;
  const containerRef = useRef<HTMLDivElement>(null);
  const [codeEditorWidth, setCodeEditorWidth] = useState(0)
  // 数据初始化
  useEffect(() => {
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
  }, [])


  return (
    <Flex vertical style={{height: 'inherit', width: '100%'}} ref={containerRef}>
      <Row className={"run-toolbar"}>
        {/* 运行工具栏*/}
        <Col>
          <RunToolBarButton showDesc={showDesc} desc={"保存"} icon={<SaveOutlined/>} />
          <RunToolBarButton showDesc={showDesc} desc={"检查"} icon={<AuditOutlined/>} />
          <RunToolBarButton showDesc={showDesc} desc={"预览DAG"} icon={<ApartmentOutlined/>} />
          <RunToolBarButton showDesc={showDesc} desc={"血缘"} icon={<PartitionOutlined />} />

          <Divider type={'vertical'} style={{height: "100%"}}/>


          <Select
            style={{height: "100%"}}
            variant="borderless"
            size={"small"}
            showSearch
            placeholder="选择Flink Env"
            optionFilterProp="label"
            options={[
              {
                value: 'jack',
                label: 'Jack',
              }
            ]}
          />
          <Select
            style={{height: "100%"}}
            variant="borderless"
            size={"small"}
            showSearch
            placeholder="选择运行模式"
            optionFilterProp="label"
            options={[
              {
                value: 'jack',
                label: 'Jack',
              }
            ]}
          />

          <Divider type={'vertical'} style={{height: "100%"}}/>


          <RunToolBarButton showDesc={showDesc} color={'green'} desc={"运行"} icon={<CaretRightOutlined/>} />
          <RunToolBarButton showDesc={showDesc} color={'red'} desc={"预览"} icon={<BugOutlined/>} />
          <RunToolBarButton showDesc={showDesc} color={'red'} desc={"停止"} icon={<PauseOutlined/>} />

          <Divider type={'vertical'} style={{height: "100%"}}/>
          <RunToolBarButton showDesc={showDesc}  desc={"格式化"} icon={<ClearOutlined />} />
          <RunToolBarButton showDesc={showDesc}  desc={"定位"} icon={<EnvironmentOutlined />} />

          <Divider type={'vertical'} style={{height: "100%"}}/>

          <RunToolBarButton showDesc={showDesc}  desc={"发布"} icon={<FundOutlined/>} />
          <RunToolBarButton showDesc={showDesc}  desc={"运维"} icon={<RotateRightOutlined/>} />
        </Col>



      </Row>

      <Row style={{height: 'inherit'}}>
        <Col style={{width: codeEditorWidth - toolbarSize}}>
          <Editor
            beforeMount={(monaco) => handleInitEditorAndLanguageOnBeforeMount(monaco, true)}
            width={'100%'}
            height={"100%"}
            value={"12312"}
            language={"sql"}
            options={{minimap: {enabled: true, side: 'right'}}}
            // options={finalEditorOptions}
            className={'editor-develop'}
            // onMount={editorDidMountChange}
            // onChange={onChange}
            //zh-CN: 因为在 handleInitEditorAndLanguageOnBeforeMount 中已经注册了自定义语言，所以这里的作用仅仅是用来切换主题 不需要重新加载自定义语言的 token 样式 , 所以这里入参需要为空, 否则每次任意的 props 改变时(包括高度等),会出现编辑器闪烁的问题
            //en-US: because the custom language has been registered in handleInitEditorAndLanguageOnBeforeMount, so the only purpose here is to switch the theme, and there is no need to reload the token style of the custom language, so the incoming parameters here need to be empty, otherwise any props change (including height, etc.) will cause the editor to flash
            theme={convertCodeEditTheme()}
          />
        </Col>


        {/*<Col style={{backgroundColor:"red",position:'absolute',right:40,width:600,height:500,zIndex:999999999}}>*/}

        {/*</Col>*/}
        <Flex wrap vertical
              style={{width: toolbarSize, backgroundColor: '#f5f3f2', height: 'inherit',position:'absolute',right:0}}>
          <Button style={{
            height: 80,
            width: '100%',
            writingMode: 'vertical-lr',
            backgroundColor: "#0396ff",
            borderRadius: 0
          }} autoInsertSpace={false} color="default" variant="link">
            配置
          </Button>
          <Button style={{height: 80, width: '100%', writingMode: 'vertical-lr'}} color="default" variant="link">
            信息
          </Button>
        </Flex>
      </Row>


    </Flex>)
}

export default connect(
  ({DataStudio}: { DataStudio: LayoutState }) => ({
    showDesc: DataStudio.toolbar.showDesc
  }))(FlinkSQL);
// export default FlinkSQL;
