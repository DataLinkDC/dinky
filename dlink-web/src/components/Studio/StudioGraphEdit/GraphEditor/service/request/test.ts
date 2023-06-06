import xkrequest from "../api/config";
//获取算子节点参数
export function getOperatorConfigure() {
  return xkrequest.get({
    url: "/api/zdpx/operatorConfigure",
  });
}
//上传流程节点数据
export function putSqlJson(data: any) {
  return xkrequest.put({
    url: "/api/zdpx/testGraphSql",
    data,
  });
}
