import { CircleBtn } from '@/components/CallBackButton/CircleBtn';
import Title from '@/components/Front/Title';
import ContentScroll from '@/components/Scroll/ContentScroll';
import MovableSidebar from '@/components/Sidebar/MovableSidebar';
import { StateType, STUDIO_MODEL, VIEW } from '@/pages/DataStudio/model';
import { LeftBottomMoreTabs, LeftBottomSide } from '@/pages/DataStudio/route';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import { PlusOutlined } from '@ant-design/icons';
import { ConfigProvider, Space, Tabs } from 'antd';
import React from 'react';

export type BottomContainerProps = {
  size: number;
};
const BottomContainer: React.FC<BottomContainerProps> = (props: any) => {
  const { dispatch, size, bottomContainer } = props;
  const width = document.documentElement.clientWidth - VIEW.sideWidth * 2;

  /**
   * 侧边栏最小化
   */
  const handleMinimize = () => {
    dispatch({
      type: STUDIO_MODEL.updateSelectBottomKey,
      payload: ''
    });
  };

  /**
   * 更新底部高度
   * @param {number} height
   */
  const updateBottomHeight = (height: number) => {
    dispatch({
      type: STUDIO_MODEL.updateBottomHeight,
      payload: height
    });
  };

  /**
   * 更新中间内容高度
   * @param {number} height
   */
  const updateCenterContentHeight = (height: number) => {
    dispatch({
      type: STUDIO_MODEL.updateCenterContentHeight,
      payload: height
    });
  };

  const updateSelectBottomSubKey = (key: string) => {
    dispatch({
      type: STUDIO_MODEL.updateSelectBottomSubKey,
      payload: key
    });
  };

  /**
   * 更新工具栏内容高度
   * @param {number} height
   */
  const updateToolContentHeight = (height: number) => {
    dispatch({
      type: STUDIO_MODEL.updateToolContentHeight,
      payload: height
    });
  };
  const getSubTabs = () => {
    // @ts-ignore
    return Object.values(
      Object.keys(LeftBottomMoreTabs).map((x) =>
        LeftBottomMoreTabs[x].map((y) => {
          return { ...y, key: x + '/' + y.key };
        })
      )
    ).flatMap((x) => x);
  };

  /**
   * 拖动回调
   * @param event
   * @param direction
   * @param {{offsetHeight: any}} elementRef
   */
  const resizeCallback = (
    event: any,
    direction: any,
    elementRef: {
      offsetHeight: any;
    }
  ) => {
    updateBottomHeight(elementRef.offsetHeight);
    const centerContentHeight =
      document.documentElement.clientHeight -
      VIEW.headerHeight -
      VIEW.headerNavHeight -
      VIEW.footerHeight -
      VIEW.otherHeight -
      bottomContainer.height;
    updateCenterContentHeight(centerContentHeight);
    updateToolContentHeight(centerContentHeight - VIEW.leftMargin);
  };

  const renderTabPane = () => {
    // @ts-ignore
    const leftBottomMoreTab = LeftBottomMoreTabs[bottomContainer.selectKey];
    if (leftBottomMoreTab) {
      const items = leftBottomMoreTab.map((item: any) => {
        return {
          key: bottomContainer.selectKey + '/' + item.key,
          label: (
            <span>
              {item.icon}
              {item.label}
            </span>
          )
        };
      });
      return (
        <Tabs
          style={{ height: '32px', display: '-webkit-box' }}
          items={items}
          type='card'
          onChange={(key: string) => {
            updateSelectBottomSubKey(key.split('/')[1]);
          }}
          activeKey={
            bottomContainer.selectKey +
            '/' +
            bottomContainer.selectSubKey[bottomContainer.selectKey]
          }
          size='small'
          tabPosition='top'
        />
      );
    }
    return <></>;
  };
  const renderItems = () => {
    return [
      ...LeftBottomSide.map((x) => {
        return { ...x, key: x.key + '/' };
      }),
      ...getSubTabs()
    ].map((item) => {
      return {
        ...item,
        children: (
          <ContentScroll height={props.bottomContainer.height - VIEW.leftMargin}>
            {item.children}
          </ContentScroll>
        )
      };
    });
  };

  return (
    <MovableSidebar
      title={
        <ConfigProvider
          theme={{
            components: {
              Tabs: {
                horizontalMargin: '0',
                cardPaddingSM: '6px',
                horizontalItemPadding: '0'
              }
            }
          }}
        >
          <Space>
            <Title>{l(bottomContainer.selectKey)}</Title>
            {renderTabPane()}
          </Space>
        </ConfigProvider>
      }
      visible={bottomContainer.selectKey}
      style={{
        zIndex: 999,
        height: bottomContainer.height,
        marginTop: 0,
        backgroundColor: '#fff',
        position: 'fixed',
        bottom: VIEW.footerHeight
      }}
      defaultSize={{ width: '100%', height: bottomContainer.height }}
      minHeight={VIEW.midMargin}
      maxHeight={size.contentHeight - 40}
      onResize={(
        event: any,
        direction: any,
        elementRef: {
          offsetHeight: any;
        }
      ) => resizeCallback(event, direction, elementRef)}
      btnGroup={[<CircleBtn key={'max'} icon={<PlusOutlined />} />]}
      enable={{ top: true }}
      handlerMinimize={handleMinimize}
      maxWidth={width}
    >
      <Tabs
        activeKey={
          bottomContainer.selectKey +
          '/' +
          (bottomContainer.selectSubKey[bottomContainer.selectKey]
            ? bottomContainer.selectSubKey[bottomContainer.selectKey]
            : '')
        }
        items={renderItems()}
        tabBarStyle={{ display: 'none' }}
      />
    </MovableSidebar>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  leftContainer: Studio.leftContainer,
  bottomContainer: Studio.bottomContainer,
  centerContentHeight: Studio.centerContentHeight
}))(BottomContainer);
