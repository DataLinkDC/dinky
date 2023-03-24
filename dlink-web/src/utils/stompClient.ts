
import { reject } from "lodash";
import Stomp, { Subscription } from "stompjs";
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

export let stompClient: Stomp.Client;

let connetStatus = false;

class MyStompClient {
  mqClient: Stomp.Client;
  connetStatus: boolean;
  subObj: Subscription;
  constructor() {
    const baseUrl = "ws://192.168.1.15:8888/stomp";
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
        debugger
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
