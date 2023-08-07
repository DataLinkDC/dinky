import React, {useEffect, useState} from "react";
import {StateType, TabsPageType, VIEW} from "@/pages/DataStudio/model";
import {Button, GlobalToken, Space} from "antd";
import {connect} from "@@/exports";
import {getCurrentTab} from "@/pages/DataStudio/function";
import {getSseData} from "@/services/api";
import {API_CONSTANTS} from "@/services/constants";
import {l} from "@/utils/intl";

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
  const eventSource = getSseData("/api/sse/getJvmInfo");
  const [memDetails, setMemDetails] = useState(footContainer.memDetails);
  useEffect(() => {
    eventSource.onmessage = (event) => {
      const data = JSON.parse(event.data);
      setMemDetails(Number(data["heapUsed"] / 1024 / 1024).toFixed(0) + "/" + Number(data["total"] / 1024 / 1024).toFixed(0) + "M")
    }
    return () => {
      eventSource.close()
    }
  }, [])

  const currentTab = getCurrentTab(tabs.panes, tabs.activeKey);
  const route: ButtonRoute[] = [
    {
      text: <span
        style={{backgroundColor: token.colorBgBase}}>{memDetails}</span>,
      title: l("pages.datastudio.footer.memDetails", "", {
        max: memDetails.split("/")[1],
        used: memDetails.split("/")[0]
      }),
      isShow: () => true
    },
    {
      text: footContainer.codeType,
      title: l("pages.datastudio.footer.codeType") + footContainer.codeType,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: footContainer.lineSeparator,
      title: l("pages.datastudio.footer.lineSeparator") + footContainer.lineSeparator,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: footContainer.codeEncoding,
      title: l("pages.datastudio.footer.codeEncoding") + footContainer.codeEncoding,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: "Space: " + footContainer.space,
      title: "Space: " + footContainer.space,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: footContainer.codePosition[0] + ":" + footContainer.codePosition[1],
      title: l("pages.datastudio.footer.codePosition", "", {
        Ln: footContainer.codePosition[0],
        Col: footContainer.codePosition[1]
      }),
      isShow: (type) => TabsPageType.project === type
    },
  ]
  return (
    <div style={{
      backgroundColor: token.colorFill,
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
      </Space>
    </div>
  )
}
export default connect(({Studio}: { Studio: StateType }) => ({
  footContainer: Studio.footContainer,
  tabs: Studio.tabs
}))(FooterContainer);
