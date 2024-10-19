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
import { TOKEN_KEY } from '@/services/constants';

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

export type WsState = {
  wsOnReady: boolean;
  wsUrl: string;
};

export default () => {
  const subscriberRef = useRef<SubscriberData[]>([]);

  const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
  const wsUrl = `${protocol}://${window.location.hostname}:${window.location.port}/api/ws/global`;
  const [wsState, setWsState] = useState<WsState>({ wsOnReady: true, wsUrl });

  const ws = useRef<WebSocket>();

  const reconnect = () => {
    if (ws.current && ws.current.readyState === WebSocket.CLOSED) {
      ws.current.close();
    }
    ws.current = new WebSocket(wsUrl);
    ws.current.onopen = () => {
      setWsState({ wsOnReady: true, wsUrl });
      receiveMessage();
      subscribe();
    };
    ws.current.onerror = () => setWsState({ wsOnReady: false, wsUrl });
    ws.current.onclose = () => setWsState({ wsOnReady: false, wsUrl });
  };

  const subscribe = () => {
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
    if (!ws.current || ws.current.readyState === WebSocket.CLOSED) {
      reconnect();
    } else if (ws.current.readyState === WebSocket.OPEN) {
      const token = JSON.parse(localStorage.getItem(TOKEN_KEY) ?? '{}')?.tokenValue;
      ws.current.send(JSON.stringify({ token, topics }));
    } else {
      //TODO 这里要做些什么
    }
  };

  const receiveMessage = () => {
    if (ws.current) {
      ws.current.onmessage = (e) => {
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
  };

  useEffect(() => {
    receiveMessage();
    setInterval(() => {
      if (!ws.current || ws.current.readyState != WebSocket.OPEN) {
        reconnect();
      }
    }, 2000);
  }, []);

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
    reconnect,
    wsState
  };
};
