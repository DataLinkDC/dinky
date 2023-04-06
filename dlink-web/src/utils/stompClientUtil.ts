
import Stomp, { Subscription } from "stompjs";

class StompClientUtil {
  stompClient: Stomp.Client;
  connectStatus: boolean;
  constructor() {
    const baseUrl = "ws://127.0.0.1:8888/stomp";
    const socket = new WebSocket(baseUrl);
    this.stompClient = Stomp.over(socket);
    this.connectStatus = false;
    this.connect();
  }

  connect() {
    this.stompClient.connect(
      {},
      () => {
        this.connectStatus = true;
        console.log("connect success>>>>>>>>>>>>>>>>>>>");
      },
      (err: any) => {
        console.log("error");
        console.log(err);
      }
    );
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect(() => {
        console.log("============connect release============");
        this.connectStatus = false;
      });
    }
  }
}
export default new StompClientUtil()
