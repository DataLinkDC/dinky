
import { reject } from "lodash";
import Stomp, { Subscription } from "stompjs";

interface SocketRes {
  body: string,
  ack?: () => {},
  command?: string,
  headers?: object,
  nack?: () => {},
}

export let stompClient: Stomp.Client;

let connetStatus = false;

class MyStompClient {
  mqClient: Stomp.Client;
  connetStatus: boolean;
  subObj: Subscription;
  constructor() {
    const baseUrl = "ws://127.0.0.1:8888/stomp";
    let socket = new WebSocket(baseUrl);
    this.mqClient = Stomp.over(socket);
    this.connetStatus = false;
    this.subObj = { id: "", unsubscribe: () => { } };
    this.con();

  }
  con() {
    this.mqClient.connect(
      {},
      () => {
        this.connetStatus = true;
        console.log("connectsuccess>>>>>>>>>>>>>>>>>>>");
      },
      (err: any) => {
        console.log("error");
        console.log(err);
      }
    );
  }
  close() {
    if (this.mqClient) {
      this.mqClient.disconnect(() => {
        console.log("============connect release============");
        this.connetStatus = false;
      });
    }
  }
  subscribe(topicurl: string) {
    return new Promise<string>((resolve, reject) => {
      this.subObj = this.mqClient.subscribe(topicurl, (res: SocketRes) => {
        resolve(res.body)
      })
    }).catch(err => {
      console.log(err);
      reject(err)
    })
  }
  unsubscribe() {
    this.subObj.unsubscribe()
    this.subObj = { id: "", unsubscribe: () => { } };
  }
}
export default new MyStompClient()
