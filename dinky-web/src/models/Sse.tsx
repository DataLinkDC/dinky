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

import { postAll } from '@/services/api';
import { ErrorMessage } from '@/utils/messages';
import { useEffect, useRef, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';

const session_invalid_label = 'SESSION_INVALID';
export type SseData = {
  topic: string;
  data: any;
};
export type SubscriberData = {
  topic: string[];
  call: (data: SseData) => void;
};

export default () => {
  const uuidRef = useRef<string>(uuidv4());
  const subscriberRef = useRef<SubscriberData[]>([]);
  const [eventSource, setEventSource] = useState<EventSource>();

  const subscribe = async () => {
    const topics: string[] = [];
    subscriberRef.current.forEach((sub) => topics.push(...sub.topic));
    const para = { sessionKey: uuidRef.current, topics: topics };
    await postAll('api/sse/subscribeTopic', para)
      .then((res: any) => {
        if (res.data.length === 1 && res.data[0] === session_invalid_label) {
          reconnectSse();
        }
      })
      .catch((e) => ErrorMessage(e));
  };
  const reconnectSse = () => {
    uuidRef.current = uuidv4();
    const sseUrl = '/api/sse/connect?sessionKey=' + uuidRef.current;
    eventSource?.close();
    setEventSource(new EventSource(sseUrl));
  };

  useEffect(() => {
    reconnectSse();
  }, []);

  useEffect(() => {
    if (eventSource) {
      eventSource.onopen = () => setTimeout(() => subscribe(), 1000);
      eventSource.onmessage = (e) => {
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
  }, [eventSource]);

  const subscribeTopic = (topic: string[], onMessage: (data: SseData) => void) => {
    const sub: SubscriberData = { topic: topic, call: onMessage };
    subscriberRef.current = [...subscriberRef.current, sub];
    subscribe();
    return () => {
      //组件卸载回调方法，取消订阅此topic
      subscriberRef.current = subscriberRef.current.filter((item) => item !== sub);
      subscribe();
    };
  };

  return {
    subscribeTopic,
    reconnectSse
  };
};
