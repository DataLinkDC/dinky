import React, {useState} from "react";
import {Button, Space} from "antd";
import CodeShow from "@/components/CustomEditor/CodeShow";
import useThemeValue from "@/hooks/useThemeValue";
import {Editor} from "@monaco-editor/react";
import {jsonToSql} from "@/pages/DataStudio/BottomContainer/Tools/JsonToSql/service";
import {convertCodeEditTheme} from "@/utils/function";
import {connect} from "@@/exports";
import {StateType} from "@/pages/DataStudio/model";

const padding = 10;

const JsonToSql: React.FC = (props:connect) => {
    const {height} = props;
    const maxHeight=height-36-(padding*2)-32-12;
    const themeValue = useThemeValue();
    const border = `1px solid ${themeValue.borderColor}`;
    const [jsonData, setJsonData] = useState("");
    const [sqlData, setSqlData] = useState("");
    return (
        <div style={{padding: padding}}>
            <Space>
                <Button
                    children={"Convert"}
                    onClick={() => {
                        jsonToSql({"data": jsonData}).then(setSqlData)
                    }}
                />
            </Space>

            <div style={{display: "flex", paddingBlockStart: padding}}>
                <div style={{width: "50%", border}}>
                    <Editor height={maxHeight} value={jsonData} language={"json"} theme={convertCodeEditTheme()} onChange={(x) => {
                        setJsonData(x ?? "");
                    }}/>
                </div>
                <div style={{width: "50%"}}>
                    <CodeShow height={maxHeight} code={sqlData} language={"json"} style={{border}}
                              options={{minimap: true}}/>
                </div>
            </div>
        </div>
    )
}
export default connect(({ Studio }: { Studio: StateType }) => ({
    height: Studio.bottomContainer.height
}))(JsonToSql);
