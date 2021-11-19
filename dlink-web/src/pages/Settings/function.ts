import {postAll, getData} from "@/components/Common/crud";
import {message} from "antd";

export function loadSettings(dispatch: any) {
  const res = getData('api/sysConfig/getAll');
  res.then((result) => {
    result.datas && dispatch && dispatch({
      type: "Settings/saveSettings",
      payload: result.datas,
    });
  });
}

export function saveSettings(values:{},dispatch: any) {
  const res = postAll("api/sysConfig/updateSysConfigByJson",values);
  res.then((result) => {
    message.success(`修改配置成功！`);
    dispatch && dispatch({
      type: "Settings/saveSettings",
      payload: values,
    });
  });
}
