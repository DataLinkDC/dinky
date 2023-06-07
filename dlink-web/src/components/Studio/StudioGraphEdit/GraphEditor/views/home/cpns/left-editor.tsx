import { memo, useEffect, useRef, useState } from 'react';
import { Graph, Node } from '@antv/x6';
import { handleInitPort } from '@/components/Studio/StudioGraphEdit/GraphEditor/utils/ports-register';
import { initGraph } from '@/components/Studio/StudioGraphEdit/GraphEditor/utils/init-graph';
import { stencilComponentsLoader } from '@/components/Studio/StudioGraphEdit/GraphEditor/utils/stencil-components-loader';
import { initStencil } from '@/components/Studio/StudioGraphEdit/GraphEditor/utils/init-stencil';
import { handleInitNodes } from '@/components/Studio/StudioGraphEdit/GraphEditor/utils/node-by-data-loader';
import registerShape from '@/components/Studio/StudioGraphEdit/GraphEditor/utils/shape-register';
import unRegisterShape from '@/components/Studio/StudioGraphEdit/GraphEditor/utils/shape-unregister';
import {
  useAppDispatch,
  useAppSelector,
} from '@/components/Studio/StudioGraphEdit/GraphEditor/hooks/redux-hooks';
import { CustomMenu } from './menu';
import { initMenu } from '@/components/Studio/StudioGraphEdit/GraphEditor/utils/init-menu';
import styles from './index.less';

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

  const { flowData, operatorParameters: operatorParameters } = useAppSelector((state) => ({
    flowData: state.home.flowData,
    operatorParameters: state.home.operatorParameters,
  }));
  useEffect(() => {
    if (editorContentRef.current) {
      const editorContentContainer: HTMLElement = editorContentRef.current;

      //1、初始化画布
      graphRef.current = initGraph(
        editorContentContainer,
        selectedNodes,
        setSelectedNodes,
        dispatch,
      );

      initMenu(graphRef.current, setShowMenuInfo);

      if (stencilRef.current) {
        const cloneRef: HTMLElement = stencilRef.current;

        //2、加载连接桩
        const ports = handleInitPort();

        //3、注册自定义节点图形
        registerShape(graphRef.current, ports, operatorParameters);

        //4、初始化stencil
        const stencil = initStencil(graphRef.current, cloneRef, operatorParameters);

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
    <div className={styles['leftEditor']}>
      <div className={styles['leftEditor-stencil']}>
        <div ref={stencilRef}></div>
      </div>
      <div className={styles['leftEditor-editor']}>
        <div className={styles['editor-content']}>
          <div ref={editorContentRef} className={styles['x6-graph']}>
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
