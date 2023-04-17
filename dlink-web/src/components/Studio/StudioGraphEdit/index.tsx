import GraphEditor from "@/components/Studio/StudioGraphEdit/GraphEditor";
import {Dispatch, DocumentStateType} from "@@/plugin-dva/connect";
import {connect} from "umi";


const FlinkGraphEditor = (props: any) => {
    const {
        height = '100%',
        width = '100%',
        language = 'sql',
        options = {
            selectOnLineNumbers: true,
            renderSideBySide: false,
            autoIndent: 'None',
        },
        monaco,
        // sqlMetaData,
    } = props;


    return (
        <>
            <GraphEditor
                ref={monaco}
                width={width}
                height={height}
                language={language}
                options={options}
                onChange={() => {
                }}
                theme="vs-dark"
                editorDidMount={() => {
                }}
                onUploadJson={(context: string) => props.saveSql(context)}
            >

            </GraphEditor>
        </>
    )
}


const mapDispatchToProps = (dispatch: Dispatch) => ({
    /*saveText:(tabs:any,tabIndex:any)=>dispatch({
      type: "Studio/saveTask",
      payload: tabs.panes[tabIndex].task,
    }),*/
    saveSql: (val: any) => dispatch({
        type: "Studio/saveSql",
        payload: val,
    }),
    saveSqlMetaData: (sqlMetaData: any, key: number) => dispatch({
        type: "Studio/saveSqlMetaData",
        payload: {
            activeKey: key,
            sqlMetaData,
            isModified: true,
        }
    }),
})

export default connect(({Document}: { Document: DocumentStateType }) => ({
    fillDocuments: Document.fillDocuments,
}), mapDispatchToProps)(FlinkGraphEditor);
