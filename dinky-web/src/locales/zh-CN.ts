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

import globalHeader from './zh-CN/global';
import menu from './zh-CN/menu';
import pages from './zh-CN/pages';
import pwa from './zh-CN/pwa';
import request from './zh-CN/request';
import response from './zh-CN/response';
import shortcutKey from './zh-CN/shortcutKey';

export default {
  'navBar.lang': '语言',
  'layout.user.link.help': '帮助',
  'layout.user.link.privacy': '隐私',
  'layout.user.link.terms': '条款',
  'app.copyright.produced': 'Produced by Dinky Community',
  ...globalHeader,
  ...pages,
  ...menu,
  ...pwa,
  ...request,
  ...response,
  ...shortcutKey
};
