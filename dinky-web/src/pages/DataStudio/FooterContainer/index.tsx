import React, {useEffect, useState} from "react";
import {StateType, TabsPageType, VIEW} from "@/pages/DataStudio/model";
import {Button, GlobalToken, Space} from "antd";
import {connect} from "@@/exports";
import {getCurrentTab} from "@/pages/DataStudio/function";
import {getSseData} from "@/services/api";
import {API_CONSTANTS} from "@/services/constants";

export type FooterContainerProps = {
  token: GlobalToken
}
type ButtonRoute = {
  text: React.ReactNode,
  title: string,
  onClick?: () => void,
  isShow?: (type?: TabsPageType, subType?: string, data?: any) => boolean;
}


const FooterContainer: React.FC<FooterContainerProps & StateType> = (props) => {
  const {footContainer, token, tabs} = props;
  const eventSource = getSseData( "/api/sse/getJvmInfo");
  const [memDetails,setMemDetails] = useState(footContainer.memDetails);
  useEffect(()=>{
    eventSource.onmessage = (event) => {
      const data = JSON.parse(event.data);
      setMemDetails(Number(data["heapUsed"]/1024/1024).toFixed(0)+"/"+Number(data["total"]/1024/1024).toFixed(0)+"M")
    }
    return ()=>{
      eventSource.close()
    }
  },[])

  const currentTab = getCurrentTab(tabs.panes, tabs.activeKey);
  const route: ButtonRoute[] = [
    {
      text: <span
        style={{backgroundColor: token.colorBgBase}}>{memDetails}</span>,
      title:"最大堆大小："+memDetails.split("/")[1]+"\n已使用：   "+memDetails.split("/")[0]+"M",
      isShow: () => true
    },
    {
      text: footContainer.codeType,
      title:"代码类型："+footContainer.codeType,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: footContainer.lineSeparator,
      title:"行分隔符："+footContainer.lineSeparator,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: footContainer.codeEncoding,
      title:"文件编码："+footContainer.codeEncoding,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: "Space: " + footContainer.space,
      title:"Space: " + footContainer.space,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: "Ln " + footContainer.codePosition[0] + ", Col " + footContainer.codePosition[1],
      title:"Ln " + footContainer.codePosition[0] + ", Col " + footContainer.codePosition[1],
      isShow: (type) => TabsPageType.project === type
    },
  ]
  return (
    <div style={{
      backgroundColor: token.colorBgLayout,
      height: VIEW.footerHeight,
      width: "100%",
      display: "flex",
      paddingInline: 10
    }}>
      <Space style={{direction: "ltr", width: "50%"}}>
        <Button size={"small"} type={"text"} block style={{paddingInline: 4}}>Welcome to Dinky !</Button>
      </Space>
      <Space style={{direction: "rtl", width: "50%"}} size={4} direction={"horizontal"}>
        {route.filter(x => {
          if (x.isShow) {
            return x.isShow(currentTab?.type, currentTab?.subType, currentTab?.params);
          }
          return false
        })
          .map((item, index) => {
            return <Button size={"small"} type={"text"} block style={{paddingInline: 4}} key={index}
                           onClick={item.onClick} title={item.title}>{item.text}</Button>
          })}
        {/*<Button size={"small"} type={"text"} block style={{paddingInline: 4}}><span*/}
        {/*  style={{backgroundColor: token.colorBgBase}}>2232/5120M</span></Button>*/}

        {/*<Button size={"small"} type={"text"} block style={{paddingInline: 4}}>FlinkSql</Button>*/}
        {/*<Button size={"small"} type={"text"} block style={{paddingInline: 4}}>LF</Button>*/}
        {/*<Button size={"small"} type={"text"} block style={{paddingInline: 4}}>UTF-8</Button>*/}
        {/*<Button size={"small"} type={"text"} block style={{paddingInline: 4}}>Space: 2</Button>*/}
        {/*<Button size={"small"} type={"text"} block style={{paddingInline: 4}}>Ln 7, Col 42</Button>*/}
      </Space>
    </div>
  )
}
export default connect(({Studio}: { Studio: StateType }) => ({
  footContainer: Studio.footContainer,
  tabs: Studio.tabs
}))(FooterContainer);
