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

export type TermProps = {
  mode: string;
  wsUrl: string;
  fontSize: number;
  backspaceAsCtrlH: boolean;
  initSql?: string;
  sessionId?: string;
  connectAddress: string;
};
export const setTermConfig = (config: TermProps) => {
  localStorage.setItem('terminal-config', JSON.stringify(config));
};

export const getTermConfig = () => {
  const initializeConfig = () => {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    return {
      mode: 'MODE_EMBEDDED',
      wsUrl: `${protocol}//${window.location.host}/api/ws/sql-gateway`,
      fontSize: 14,
      backspaceAsCtrlH: true
    };
  };

  const configKey = 'terminal-config';
  const storedConfig = localStorage.getItem(configKey);

  if (storedConfig) {
    try {
      return JSON.parse(storedConfig);
    } catch (error) {
      console.error('Failed to parse stored config:', error);
      return initializeConfig();
    }
  } else {
    return initializeConfig();
  }
};

export const HEART_TIMEOUT = 30 * 1000;
export const HEART_TNERVAL = 30 * 1000;

export const enum TerminalEvent {
  TERM_KEY_EVENT = 'TERM_KEY_EVENT',
  TERM_HEART_EVENT = 'TERM_HEART_EVENT',
  TERM_CLOSE_EVENT = 'TERM_CLOSE_EVENT',
  TERM_RESIZE = 'TERM_RESIZE'
}

export const enum KeyCode {
  Backspace = 8,
  Tab = 9,
  Enter = 13,
  Shift = 16,
  Ctrl = 17,
  Alt = 18,
  PauseBreak = 19,
  CapsLock = 20,
  Escape = 27,
  Space = 32,
  PageUp = 33,
  PageDown = 34,
  End = 35,
  Home = 36,
  LeftArrow = 37,
  UpArrow = 38,
  RightArrow = 39,
  DownArrow = 40,
  Insert = 45,
  Delete = 46,
  Key0 = 48,
  Key1 = 49,
  Key2 = 50,
  Key3 = 51,
  Key4 = 52,
  Key5 = 53,
  Key6 = 54,
  Key7 = 55,
  Key8 = 56,
  Key9 = 57,
  A = 65,
  B = 66,
  C = 67,
  D = 68,
  E = 69,
  F = 70,
  G = 71,
  H = 72,
  I = 73,
  J = 74,
  K = 75,
  L = 76,
  M = 77,
  N = 78,
  O = 79,
  P = 80,
  Q = 81,
  R = 82,
  S = 83,
  T = 84,
  U = 85,
  V = 86,
  W = 87,
  X = 88,
  Y = 89,
  Z = 90,
  LeftWindowKey = 91,
  RightWindowKey = 92,
  SelectKey = 93,
  Numpad0 = 96,
  Numpad1 = 97,
  Numpad2 = 98,
  Numpad3 = 99,
  Numpad4 = 100,
  Numpad5 = 101,
  Numpad6 = 102,
  Numpad7 = 103,
  Numpad8 = 104,
  Numpad9 = 105,
  Multiply = 106,
  Add = 107,
  Subtract = 109,
  DecimalPoint = 110,
  Divide = 111,
  F1 = 112,
  F2 = 113,
  F3 = 114,
  F4 = 115,
  F5 = 116,
  F6 = 117,
  F7 = 118,
  F8 = 119,
  F9 = 120,
  F10 = 121,
  F11 = 122,
  F12 = 123,
  NumLock = 144,
  ScrollLock = 145,
  SemiColon = 186,
  EqualSign = 187,
  Comma = 188,
  Dash = 189,
  Period = 190,
  ForwardSlash = 191,
  GraveAccent = 192,
  OpenBracket = 219,
  BackSlash = 220,
  CloseBracket = 221,
  SingleQuote = 222
}

export const enum AsciiCode {
  Null = 0,
  StartOfHeader = 1,
  StartOfText = 2,
  EndOfText = 3,
  EndOfTransmission = 4,
  Enquiry = 5,
  Acknowledge = 6,
  Bell = 7,
  Backspace = 8,
  HorizontalTab = 9,
  LineFeed = 10,
  VerticalTab = 11,
  FormFeed = 12,
  CarriageReturn = 13,
  ShiftOut = 14,
  ShiftIn = 15,
  DataLinkEscape = 16,
  DeviceControl1 = 17,
  DeviceControl2 = 18,
  DeviceControl3 = 19,
  DeviceControl4 = 20,
  NegativeAcknowledge = 21,
  SynchronousIdle = 22,
  EndOfTransmissionBlock = 23,
  Cancel = 24,
  EndOfMedium = 25,
  Substitute = 26,
  Escape = 27,
  FileSeparator = 28,
  GroupSeparator = 29,
  RecordSeparator = 30,
  UnitSeparator = 31,
  Space = 32,
  ExclamationMark = 33,
  DoubleQuote = 34,
  Hash = 35,
  Dollar = 36,
  Percent = 37,
  Ampersand = 38,
  SingleQuote = 39,
  LeftParenthesis = 40,
  RightParenthesis = 41,
  Asterisk = 42,
  Plus = 43,
  Comma = 44,
  Minus = 45,
  Period = 46,
  Slash = 47,
  Zero = 48,
  One = 49,
  Two = 50,
  Three = 51,
  Four = 52,
  Five = 53,
  Six = 54,
  Seven = 55,
  Eight = 56,
  Nine = 57,
  Colon = 58,
  Semicolon = 59,
  LessThan = 60,
  Equals = 61,
  GreaterThan = 62,
  QuestionMark = 63,
  At = 64,
  A = 65,
  B = 66,
  C = 67,
  D = 68,
  E = 69,
  F = 70,
  G = 71,
  H = 72,
  I = 73,
  J = 74,
  K = 75,
  L = 76,
  M = 77,
  N = 78,
  O = 79,
  P = 80,
  Q = 81,
  R = 82,
  S = 83,
  T = 84,
  U = 85,
  V = 86,
  W = 87,
  X = 88,
  Y = 89,
  Z = 90,
  LeftSquareBracket = 91,
  Backslash = 92,
  RightSquareBracket = 93,
  Caret = 94,
  Underscore = 95,
  Backtick = 96,
  a = 97,
  b = 98,
  c = 99,
  d = 100,
  e = 101,
  f = 102,
  g = 103,
  h = 104,
  i = 105,
  j = 106,
  k = 107,
  l = 108,
  m = 109,
  n = 110,
  o = 111,
  p = 112,
  q = 113,
  r = 114,
  s = 115,
  t = 116,
  u = 117,
  v = 118,
  w = 119,
  x = 120,
  y = 121,
  z = 122,
  LeftCurlyBracket = 123,
  Pipe = 124,
  RightCurlyBracket = 125,
  Tilde = 126,
  Delete = 127
}
