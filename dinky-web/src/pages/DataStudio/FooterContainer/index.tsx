import React, {useEffect, useState} from "react";
import {StateType, TabsPageType, VIEW} from "@/pages/DataStudio/model";
import {Button, GlobalToken, Space} from "antd";
import {connect} from "@@/exports";
import {getCurrentTab} from "@/pages/DataStudio/function";
import {getSseData} from "@/services/api";
import {l} from "@/utils/intl";
import JobRunningModal from "@/pages/DataStudio/FooterContainer/JobRunningModal";

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
    const {footContainer: {
        memDetails,
        codeType,
        lineSeparator,
        codeEncoding,
        space,
        codePosition,
        jobRunningMsg,
    }, token, tabs} = props;

    const [viewJobRunning , setViewJobRunning] = useState(false);


    const eventSource = getSseData("/api/sse/getJvmInfo");
    const [memDetailInfo, setMemDetailInfo] = useState(memDetails);
    useEffect(() => {
        eventSource.onmessage = (event) => {
            const data = JSON.parse(event.data);
            setMemDetailInfo(Number(data["heapUsed"] / 1024 / 1024).toFixed(0) + "/" + Number(data["total"] / 1024 / 1024).toFixed(0) + "M")
        }
        return () => {
            eventSource.close()
        }
    }, []);


    const currentTab = getCurrentTab(tabs.panes, tabs.activeKey);
    const route: ButtonRoute[] = [
        {
            text: <span style={{backgroundColor: token.colorBgBase}}>{memDetailInfo}</span>,
            title: l("pages.datastudio.footer.memDetails", "", {
                max: memDetailInfo.split("/")[1],
                used: memDetailInfo.split("/")[0]
            }),
            isShow: () => true
        },
        {
            text: codeType,
            title: l("pages.datastudio.footer.codeType") + codeType,
            isShow: (type) => TabsPageType.project === type
        },
        {
            text: lineSeparator,
            title: l("pages.datastudio.footer.lineSeparator") + lineSeparator,
            isShow: (type) => TabsPageType.project === type
        },
        {
            text: codeEncoding,
            title: l("pages.datastudio.footer.codeEncoding") + codeEncoding,
            isShow: (type) => TabsPageType.project === type
        },
        {
            text: "Space: " + space,
            title: "Space: " + space,
            isShow: (type) => TabsPageType.project === type
        },
        {
            text: codePosition[0] + ":" + codePosition[1],
            title: l("pages.datastudio.footer.codePosition", "", {
                Ln: codePosition[0],
                Col: codePosition[1]
            }),
            isShow: (type) => TabsPageType.project === type
        },
    ];


    /**
     * render footer right info
     * @param {ButtonRoute[]} routes
     * @returns {JSX.Element[]}
     */
    const renderFooterRightInfo = (routes: ButtonRoute[]) => {
        return routes.filter(x => {
            if (x.isShow) {
                return x.isShow(currentTab?.type, currentTab?.subType, currentTab?.params);
            }
            return false
        })
            .map((item, index) => (
                <Button size={"small"} type={"text"} block style={{paddingInline: 4}} key={index}
                        onClick={item.onClick} title={item.title}>{item.text}</Button>
            ))
    }

    return <>
        <div style={{
            backgroundColor: token.colorFill,
            height: VIEW.footerHeight,
            width: "100%",
            display: "flex",
            paddingInline: 10
        }}>
            <Space style={{direction: "ltr", width: "30%%"}}>
                <Button size={"small"} type={"text"} block style={{paddingInline: 4}}>Welcome to Dinky !</Button>
            </Space>
            <Space onClick={()=>setViewJobRunning(true)} style={{direction: 'rtl', width: "30%"}}>
                {jobRunningMsg.jobName} - {jobRunningMsg.runningLog}
            </Space>
            <Space style={{direction: "rtl", width: "70%"}} size={4}
                   direction={"horizontal"}>{renderFooterRightInfo(route)}</Space>
        </div>
        <JobRunningModal value={jobRunningMsg} visible={viewJobRunning} onCancel={()=>setViewJobRunning(false)} onOk={()=>setViewJobRunning(false)} />
    </>
}
export default connect(({Studio}: { Studio: StateType }) => ({
    footContainer: Studio.footContainer,
    tabs: Studio.tabs
}))(FooterContainer);
