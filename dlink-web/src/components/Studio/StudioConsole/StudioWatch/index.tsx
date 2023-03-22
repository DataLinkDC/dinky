import {useEffect, useState} from "react";
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import {message} from "antd";
import {getConsoleInfo} from "@/pages/SettingCenter/ProcessList/service";

const StudioWatch = (props: any) => {
  // const {current, height, isActive} = props;
  // const {consoleInfo, setConsoleInfo} = useState<string>("");
  //
  // useEffect(() => {
  //   refreshConsoleInfo()
  //   const socket = new SockJS('ws://localhost:8000/stomp');
  //   const stompClient = Stomp.over(socket);
  //   stompClient.connect({}, () => {
  //     stompClient.subscribe('/topic/table/`default_catalog`.`default_database`.`print_Orders`', (message) => {
  //
  //     });
  //   }, (error) => {
  //     message.error('Failed to connect to WebSocket: ' + error);
  //   });
  // }, [isActive]);
  //
  // const refreshConsoleInfo = () => {
  //   if (isActive) {
  //     const res = getConsoleInfo();
  //     res.then((result) => {
  //       result.datas && setConsoleInfo(result.datas);
  //     });
  //   }
  // }
}
