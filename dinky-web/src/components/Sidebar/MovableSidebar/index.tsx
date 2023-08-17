import { CircleBtn } from '@/components/CallBackButton/CircleBtn';
import useThemeValue from '@/hooks/useThemeValue';
import { MinusOutlined } from '@ant-design/icons';
import { Space, TabPaneProps, theme } from 'antd';
import { Enable, Resizable, ResizeCallback, Size } from 're-resizable';
import React, { useState } from 'react';

export type MovableSidebarProps = {
  title?: React.ReactNode;
  enable?: Enable;
  minWidth?: string | number;
  minHeight?: string | number;
  maxWidth?: string | number;
  maxHeight?: string | number;
  contentHeight?: number;
  defaultSize?: Size;
  visible?: boolean;
  children?: React.ReactNode;
  handlerMinimize?: () => void;
  handlerMaxsize?: () => void;
  btnGroup?: React.ReactNode[];
  onResize?: ResizeCallback;
  style?: React.CSSProperties;
  tagList?: TabPaneProps[];
};
const { useToken } = theme;
const MovableSidebar: React.FC<MovableSidebarProps> = (props) => {
  const { token } = useToken();
  const themeValue = useThemeValue();

  const {
    style,
    visible,
    onResize,
    defaultSize,
    minWidth,
    maxWidth,
    minHeight,
    maxHeight,
    enable,
    children,
    title,
    contentHeight,
    handlerMinimize,
    handlerMaxsize,
    tagList,
  } = props;
  const [showBtn, setShowBtn] = useState(false);

  return (
    <div
      onMouseEnter={() => setShowBtn(true)}
      onMouseLeave={() => setShowBtn(false)}
    >
      <Resizable
        className={'container'}
        style={{
          ...style,
          display: visible ? 'block' : 'none',
          borderRadius: 5,
        }}
        onResize={onResize}
        defaultSize={defaultSize}
        minWidth={minWidth}
        maxWidth={maxWidth}
        minHeight={minHeight}
        maxHeight={maxHeight}
        enable={enable}
      >
        {/*<PageContainer*/}
        {/*    title={title}*/}
        {/*    tabProps={{*/}
        {/*        type: "editable-card",*/}
        {/*        hideAdd: true,*/}
        {/*        size: "small",*/}
        {/*        animated: true,*/}
        {/*        tabBarStyle: {margin: 0, padding: 0},*/}
        {/*    }}*/}
        {/*    tabList={tagList}*/}
        {/*    extra={[*/}
        {/*      <Button title={l('global.mini')} key={"minimize"} icon={<MinusOutlined/>} block type={"text"} shape={"circle"}*/}
        {/*              onClick={props.handlerMinimize}/>*/}
        {/*        // <MinusSquareOutlined size={15}  title={l('global.mini')} key={"minimize"} onClick={handlerMinimize}/>,*/}
        {/*        // <MinusOutlined size={35}  title={l('global.mini')} key={"minimize"} onClick={handlerMinimize}/>,*/}
        {/*        // <PlusSquareOutlined size={15} title={l('global.max')} key={"maximize"} onClick={handlerMaxsize}/>*/}
        {/*    ]}*/}
        {/*    fixedHeader*/}
        {/*>*/}
        {/*    <div style={{height: contentHeight,marginBlock:-5,marginInline:-10}}>{children}</div>*/}
        {/*</PageContainer>*/}
        <>
          <div
            style={{
              backgroundColor: token.colorBgBase,
              borderBlockColor: themeValue.borderColor,
            }}
            className={'container-header'}
          >
            <div>{title}</div>
            <div className={showBtn ? 'show' : 'hide'}>
              <Space size={5}>
                {props.btnGroup}
                <CircleBtn
                  onClick={props.handlerMinimize}
                  icon={<MinusOutlined />}
                />
              </Space>
            </div>
          </div>
          <div
            style={{
              height: contentHeight,
              backgroundColor: token.colorBgBase,
              overflow:"auto"
            }}
          >
            {children}
          </div>
        </>
      </Resizable>
    </div>
  );
};
export default MovableSidebar;
