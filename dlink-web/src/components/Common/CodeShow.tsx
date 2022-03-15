import MonacoEditor from "react-monaco-editor";
import * as _monaco from "monaco-editor";

export type CodeShowFormProps = {
  height?: string;
  width?: string;
  language: string;
  theme?: string;
  options?: any;
  code: string;
};

const CodeShow = (props: CodeShowFormProps) => {

  const {
    height = '100%',
    width = '100%',
    language = 'sql',
    theme = 'vs',
    options = {
      selectOnLineNumbers: true,
      renderSideBySide: false,
      autoIndent:'None',
      readOnly:true ,
    },
    code,
  } = props;


  return (<>
    <MonacoEditor
      width={width}
      height={height}
      language={language}
      value={code}
      options={options}
      theme={theme}
    />
  </>)
};

export default CodeShow;
