import {Space, Switch} from "antd";
import {SWITCH_OPTIONS} from "@/services/constants";
import React from "react";

type EnableSwitchProps = {
    record: any;
    onChange: () => void;
}
export const EnableSwitch = (props: EnableSwitchProps) => {

    const {record,onChange} = props;

    return <>
        <Space>
            <Switch
                {...SWITCH_OPTIONS()}
                checked={record.enabled}
                onChange={() => onChange()}/>
        </Space>
    </>
}