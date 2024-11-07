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

import React, { useEffect } from 'react';
import {
  AsciiCode,
  HEART_TIMEOUT,
  HEART_TNERVAL,
  KeyCode,
  TerminalEvent
} from '@/pages/DataStudio/MiddleContainer/Terminal/TerminalConfig';
import { FitAddon } from '@xterm/addon-fit';
import { Terminal } from '@xterm/xterm';
import { TermProps } from '@/pages/DataStudio/MiddleContainer/Terminal/TerminalConfig';
import './xterm.css';

const TerminalContent: React.FC<TermProps> = (props) => {
  const { mode, wsUrl, backspaceAsCtrlH, fontSize, initSql, sessionId, connectAddress } = props;
  // const currentData = tabsItem.params.taskData;

  let term: Terminal;
  let ws: WebSocket;
  let lastHeartTime: Date = new Date();
  let heartInterval: NodeJS.Timeout;

  const sendWs = (data: any, type: string) => {
    ws.send(JSON.stringify({ type: type, data: data }));
  };

  const CheckWebSocket = (ws: WebSocket) => {
    if (ws) {
      return !(ws.readyState === WebSocket.CLOSING || ws.readyState === WebSocket.CLOSED);
    }
    return false;
  };

  const preprocessInput = (data: any) => {
    if (backspaceAsCtrlH) {
      if (data.charCodeAt(0) === AsciiCode.Delete) {
        data = String.fromCharCode(KeyCode.Backspace);
      }
    }
    if (data.charCodeAt(0) === AsciiCode.EndOfText) {
      data = String.fromCharCode(AsciiCode.Substitute);
    }
    if (data === '\r') {
      data = '\n';
    }
    return data;
  };

  const connectWs = (term: Terminal) => {
    const termCols = term.cols;
    const termRows = term.rows;

    const params = `mode=${mode}&cols=${termCols}&rows=${termRows}&sessionId=${sessionId}&initSql=${encodeURIComponent(initSql ?? '')}&connectAddress=${connectAddress}`;

    ws = new WebSocket(`${wsUrl}/?${params}`);

    ws.binaryType = 'arraybuffer';
    ws.onopen = function () {
      if (heartInterval) {
        clearInterval(heartInterval);
      }
      heartInterval = setInterval(() => {
        if (!CheckWebSocket(ws)) {
          clearInterval(heartInterval);
        } else {
          let currentDate = new Date();
          if (lastHeartTime.getTime() - currentDate.getTime() > HEART_TIMEOUT) {
            console.error('ws heart timeout');
          }
          sendWs('PING', TerminalEvent.TERM_HEART_EVENT);
        }
      }, HEART_TNERVAL);
    };

    ws.onclose = function (evt: CloseEvent) {
      term.writeln('\nThe connection closed: ' + evt.code + ' ' + evt.reason);
    };

    ws.onerror = function (evt: Event) {
      term.write('\nThe connection  error:' + evt);
    };

    ws.onmessage = function (e: MessageEvent) {
      if (typeof e.data === 'object') {
        const data = new Uint8Array(e.data);
        if (data.length == 1 && data[0] === KeyCode.Backspace) {
          term.write('\b \b');
        } else {
          term.write(data);
        }
      } else {
        //预留接口
        term.write(e.data.replace('\r', '\n\n'));
      }
    };
  };

  const createTerminal = () => {
    if (term) {
      return term;
    } else {
      const terminal = new Terminal({
        // rendererType: "canvas", //渲染类型
        // cols: this.cols,// 设置之后会输入多行之后覆盖现象
        // convertEol: true, //启用时，光标将设置为下一行的开头
        scrollback: 1000, //终端中的回滚量
        fontSize: fontSize, //字体大小
        // disableStdin: false, //是否应禁用输入。
        // cursorStyle: "block", //光标样式
        // cursorBlink: true, //光标闪烁
        // tabStopWidth: 4,
        theme: {
          // foreground: "black", //字体
          // background: "#000000", //背景色
          // cursor: "help" //设置光标
        }
      });
      const fitAddon = new FitAddon();
      terminal.loadAddon(fitAddon);
      terminal.open(document.getElementById('terminal-container') as HTMLElement);
      fitAddon.fit();
      terminal.focus();
      window.addEventListener('resize', () => fitAddon.fit());
      return terminal;
    }
  };

  const initAll = () => {
    if (!term) {
      term = createTerminal();
    }
    term.onData((data) => {
      if (!CheckWebSocket(ws)) {
        console.error('ws not activated');
        return;
      }
      // this.lastSendTime = new Date();
      data = preprocessInput(data);
      sendWs(data, TerminalEvent.TERM_KEY_EVENT);
    });

    term.onResize(({ cols, rows }) => {
      if (!CheckWebSocket(ws)) {
        return;
      }
      sendWs(JSON.stringify({ columns: cols, rows }), TerminalEvent.TERM_RESIZE);
    });
    connectWs(term);
  };

  useEffect(() => {
    initAll();
    return () => {
      if (CheckWebSocket(ws)) {
        sendWs('', TerminalEvent.TERM_CLOSE_EVENT);
        //延迟关闭，给与后端注销时间
        setTimeout(() => ws.close(), 2000);
      }
      clearInterval(heartInterval);
      term?.dispose();
    };
  }, []);

  return <div id='terminal-container' style={{ height: '100vh' }}></div>;
};

export default TerminalContent;
