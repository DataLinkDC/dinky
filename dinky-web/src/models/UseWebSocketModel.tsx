import {useEffect, useRef, useState} from "react";
import {SseData, SubscriberData} from "@/models/Sse";
import {ErrorMessage} from "@/utils/messages";


export default () => {
  const subscriberRef = useRef<SubscriberData[]>([]);
  const wsUrl = `ws://${window.location.hostname}:${window.location.port}/api/ws/global`;

  const [ws] = useState<WebSocket>(new WebSocket(wsUrl));

  const reconnect = () => {
    if (ws?.readyState === ws?.OPEN) {
      ws?.close();
    }
    ws.onopen = () => {
      console.log("ws open");
    }
  };

  const subscribe = async () => {
    const topics: string[] = [];
    subscriberRef.current.forEach((sub) => topics.push(...sub.topic));
    const param = {topics: topics};
    if (ws?.readyState === ws?.CLOSED) {
      reconnect()
    } else {
      ws?.send(JSON.stringify(param));
    }
  };

  useEffect(() => {
    setInterval(() => {
      if (ws?.readyState === ws?.CLOSED) {
        reconnect();
      }
    }, 5000);
  }, []);

  useEffect(() => {
    if (ws) {
      // ws.onopen = () => setTimeout(() => subscribe(), 1000);
      ws.onmessage = (e) => {
        try {
          const data: SseData = JSON.parse(e.data);
          subscriberRef.current
            .filter((sub) => sub.topic.includes(data.topic))
            .forEach((sub) => sub.call(data));
        } catch (e: any) {
          ErrorMessage(e);
        }
      };
    }
  }, [ws]);

  const subscribeTopic = (topic: string[], onMessage: (data: SseData) => void) => {
    const sub: SubscriberData = {topic: topic, call: onMessage};
    if (!subscriberRef.current.flatMap(x=>x.topic).includes(sub.topic[0])){
      subscriberRef.current = [...subscriberRef.current, sub];
      subscribe();
    }
    return () => {
      //组件卸载回调方法，取消订阅此topic
      subscriberRef.current = subscriberRef.current.filter((item) => item !== sub);
      subscribe();
    };
  };

  return {
    subscribeTopic,
    reconnect
  };
};
