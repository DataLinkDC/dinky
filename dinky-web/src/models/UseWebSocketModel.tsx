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

import { useEffect, useRef, useState } from 'react';
import { ErrorMessage } from '@/utils/messages';
import { v4 as uuidv4 } from 'uuid';
import {TOKEN_KEY} from "@/services/constants";
export type SseData = {
  topic: string;
  data: Record<string, any>;
};

export enum Topic {
  JVM_INFO = 'JVM_INFO',
  PROCESS_CONSOLE = 'PROCESS_CONSOLE',
  PRINT_TABLE = 'PRINT_TABLE',
  METRICS = 'METRICS'
}

export type SubscriberData = {
  key: string;
  topic: Topic;
  params: string[];
  call: (data: SseData) => void;
};
export default () => {
  const subscriberRef = useRef<SubscriberData[]>([]);
  const wsUrl = `ws://${window.location.hostname}:${window.location.port}/api/ws/global`;

  const [ws] = useState<WebSocket>(new WebSocket(wsUrl));

  const reconnect = () => {
    if (ws?.readyState === ws?.OPEN) {
      ws?.close();
    }
    ws.onopen = () => {
      console.log('ws open');
    };
  };

  const subscribe = async () => {
    const topics: Record<string, string[]> = {};
    subscriberRef.current.forEach((sub) => {
      if (!topics[sub.topic]) {
        topics[sub.topic] = [];
      }
      if (sub.params && sub.params.length > 0) {
        topics[sub.topic] = [...topics[sub.topic], ...sub.params];
      } else {
        topics[sub.topic] = [...topics[sub.topic]];
      }
    });
    if (ws?.readyState === ws?.CLOSED) {
      reconnect();
    } else {
      const  token = JSON.parse(localStorage.getItem(TOKEN_KEY)??"{}")?.tokenValue;
      ws.send(JSON.stringify({token,topics}));
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
      ws.onmessage = (e) => {
        try {
          const data: SseData = JSON.parse(e.data);
          subscriberRef.current
            .filter((sub) => sub.topic === data.topic)
            .filter((sub) => !sub.params || sub.params.find((x) => data.data[x]))
            .forEach((sub) => sub.call(data));
        } catch (e: any) {
          ErrorMessage(e);
        }
      };
    }
  }, [ws]);

  const subscribeTopic = (topic: Topic, params: string[], onMessage: (data: SseData) => void) => {
    const sub: SubscriberData = { topic: topic, call: onMessage, params: params, key: uuidv4() };
    subscriberRef.current.push(sub);
    subscribe();
    return () => {
      //组件卸载回调方法，取消订阅此topic
      subscriberRef.current = subscriberRef.current.filter((item) => item.key !== sub.key);
      subscribe();
    };
  };

  return {
    subscribeTopic,
    reconnect
  };
};
