import { memo, useEffect, useRef, useState } from "react";
import { Graph, Node } from "@antv/x6";
import { CloudSyncOutlined, FileOutlined } from "@ant-design/icons";
import { handleInitPort } from "@/utils/ports-register";
import { initGraph } from "@/utils/init-graph";
import { stencilComponentsLoader } from "@/utils/stencil-components-loader";
import { initStencil } from "@/utils/init-stencil";
import { handleInitNodes } from "@/utils/node-by-data-loader";
import registerShape from "@/utils/shape-register";
import unRegisterShape from "@/utils/shape-unregister";
import { message } from "antd";
import { useAppDispatch, useAppSelector } from "@/hooks/redux-hooks";
import { putSqlJson } from "@/service/request/test";
import { CustomMenu } from "./menu";
import { initMenu } from "@/utils/init-menu";

const LeftEditor = memo(() => {
  const [selectedNodes, setSelectedNodes] = useState<Node[]>([]);
  const [showMenuInfo, setShowMenuInfo] = useState<{
    show: boolean;
    top: number;
    left: number;
  }>({
    show: false,
    top: 0,
    left: 0,
  });

  const editorContentRef = useRef(null);
  const graphRef = useRef<Graph>();
  const stencilRef = useRef(null);

  const dispatch = useAppDispatch();

  const { flowData, operatorParameters: operatorParameters } = useAppSelector(
    (state) => ({
      flowData: state.home.flowData,
      operatorParameters: state.home.operatorParameters,
    })
  );

  const handleSave = () => {
    putSqlJson(graphRef.current?.toJSON())
      .then(() => {})
      .catch(() => {});

    messageApi.open({
      type: "success",
      content: "This is a success save",
    });
  };

  const handleDeploy = () => {
    messageApi.open({
      type: "success",
      content: "This is a success deploy",
    });
  };

  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    if (editorContentRef.current) {
      const editorContentContainer: HTMLElement = editorContentRef.current;

      //1、初始化画布
      graphRef.current = initGraph(
        editorContentContainer,
        selectedNodes,
        setSelectedNodes,
        dispatch
      );

      initMenu(graphRef.current, setShowMenuInfo);

      if (stencilRef.current) {
        const cloneRef: HTMLElement = stencilRef.current;

        //2、加载连接桩
        const ports = handleInitPort();

        //3、注册自定义节点图形
        registerShape(graphRef.current, ports, operatorParameters);

        //4、初始化stencil
        const stencil = initStencil(
          graphRef.current,
          cloneRef,
          operatorParameters
        );

        // 5、加载自定义的组件图形
        stencilComponentsLoader(graphRef.current, stencil, operatorParameters);

        //6、加载数据
        handleInitNodes(graphRef.current, flowData);
      }
    }

    return () => {
      if (graphRef.current) {
        graphRef.current.dispose();
        unRegisterShape(operatorParameters);
      }
    };
  }, [dispatch, flowData, operatorParameters]);

  return (
    <div className="leftEditor">
      {contextHolder}
      <div className="leftEditor-stencil">
        <div ref={stencilRef}></div>
      </div>
      <div className="leftEditor-editor">
        <div className="editor-header">
          <div
            className="header-save"
            onClick={() => {
              handleSave();
            }}
          >
            <span>保存</span>
            <FileOutlined />
          </div>
          <div
            className="header-save"
            onClick={() => {
              handleDeploy();
            }}
          >
            <span>部署</span>
            <CloudSyncOutlined />
          </div>
        </div>
        <div className="editor-content">
          <div ref={editorContentRef}>
            {showMenuInfo.show && (
              <CustomMenu
                top={showMenuInfo.top}
                left={showMenuInfo.left}
                graph={graphRef.current}
              />
            )}
          </div>
        </div>
      </div>
    </div>
  );
});

export default LeftEditor;
