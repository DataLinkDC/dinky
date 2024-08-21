/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
