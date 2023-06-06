import { memo } from "react";
import Editor from "./json-editor";

const RightDetail = memo(() => {
  return (
    <div className="rightDetail">
      <div className="rightDetail-header">节点信息</div>
      <div className="rightDetail-content">
        <Editor />
      </div>
    </div>
  );
});

export default RightDetail;
