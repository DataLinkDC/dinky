//引入使用SockJS


import { reject } from "lodash";
import Stomp from "stompjs";
interface params {
  topicUrl: string,
  sendTopicUrl?: string,
  header?: object,
}
interface SocketRes {
  body: string,
  ack?: () => {},
  command?: string,
  headers?: object,
  nack?: () => {},
}
//请求地址
const baseUrl = "ws://192.168.1.15:8888/stomp";
//请求头
//stomp客户端 
export let stompClient: Stomp.Client;
//连接状态
let connetStatus = false;
/**
 * 初始化连接
 */

export const initSocket = (params: params) => {
  con();
  setInterval(() => {
    //根据连接状态
    if (connetStatus) {
      try {
        //发送请求
        stompClient.send(
          params.sendTopicUrl ? params.sendTopicUrl : "",
          params.header,
          JSON.stringify({ name: "wangau" })
        );
      } catch (error) {
        con();
      }
    }
  }, 10000);
};
/**
 * 连接
 */
export const con = () => {

  let socket = new WebSocket(baseUrl)

  stompClient = Stomp.over(socket);

  let header = {};
  stompClient.connect(
    header,
    () => {
      connetStatus = true;
      console.log("connectsuccess>>>>>>>>>>>>>>>>>>>");
    },
    (err: any) => {
      console.log("error");
      console.log(err);

    }
  );

};
export const subscribe = (topicurl: string) => {
  return new Promise<string>((resolve, reject) => {
    stompClient.subscribe(topicurl, (res:SocketRes) => {
      debugger
      resolve(res.body)
    })
  }).catch(err => {
    console.log(err);
    reject(err)

  })
}
/**
 * 断开
 */
export const close = () => {
  if (stompClient) {
    stompClient.disconnect(() => {
      console.log("============connect release============");
      connetStatus = false;
    });
  }
};

