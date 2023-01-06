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

import {Divider, Typography} from 'antd';
import React from 'react';
import {connect} from 'umi';
import {StateType} from '@/pages/DataStudio/model';
import {Scrollbars} from 'react-custom-scrollbars';
import {VERSION} from "@/components/Common/Version";
import {l} from "@/utils/intl";

const {Title, Paragraph, Text} = Typography;

const StudioHome = (props: any) => {


  const {toolHeight} = props;

  return (
    <Scrollbars style={{height: toolHeight}}>
      <Typography style={{padding: '15px'}}>
        <Title level={4}>{l('pages.datastudio.label.welcomeuse', '', {version: VERSION})}</Title>
        <Paragraph>
          <blockquote>{l('pages.datastudio.label.dinkydescribe')}</blockquote>
        </Paragraph>
        <Title level={5}>{l('shortcut.title')}</Title>
        <Paragraph>
          <Text keyboard>Ctrl + s</Text> {l('shortcut.key.save')} <Divider type="vertical"/>
          <Text keyboard>Alt + 2</Text> {l('shortcut.key.check')} <Divider type="vertical"/>
          <Text keyboard>Alt + 3</Text> {l('shortcut.key.beautify')} <Divider type="vertical"/>
          <Text keyboard>F2</Text> {l('shortcut.key.fullscreen')} <Divider type="vertical"/>
          <Text keyboard>Esc</Text> {l('shortcut.key.fullscreenClose')} <Divider type="vertical"/>
          <Text keyboard>F1</Text> {l('shortcut.key.more')}
        </Paragraph>
        <Paragraph>
          <Text keyboard>Ctrl + F</Text> {l('shortcut.key.search')} <Divider type="vertical"/>
          <Text keyboard>Ctrl + H</Text> {l('shortcut.key.replace')} <Divider type="vertical"/>
          <Text keyboard>Ctrl + Z</Text> {l('shortcut.key.revoke')} <Divider type="vertical"/>
          <Text keyboard>Ctrl + Y</Text> {l('shortcut.key.redo')} <Divider type="vertical"/>
          <Text keyboard>Ctrl + /</Text> {l('shortcut.key.notes')}
        </Paragraph>
        <Paragraph>
          <Text keyboard>{l('shortcut.key.checked')} + Tab</Text> {l('shortcut.key.indent')} <Divider type="vertical"/>
          <Text keyboard>{l('shortcut.key.checked')} + Shift + Tab</Text> {l('shortcut.key.removeIndent')} <Divider
          type="vertical"/>
          <Text keyboard>Shift + Alt + Right</Text> {l('shortcut.key.selectToEnd')} <Divider type="vertical"/>
          <Text keyboard>Shift + Alt + F</Text> {l('shortcut.key.format')}
        </Paragraph>
        <Paragraph>
          <Text keyboard>Ctrl + Shift + Up/Down</Text> {l('shortcut.key.editMultiline')} <Divider type="vertical"/>
          <Text keyboard>Shift + Alt + Up/Down</Text> {l('shortcut.key.copyRow')} <Divider type="vertical"/>
          <Text keyboard>Ctrl + Shift + K</Text> {l('shortcut.key.deleteRow')}
        </Paragraph>
        <Paragraph>
          <Text keyboard>Ctrl + F3</Text> {l('shortcut.key.matchNext')} <Divider type="vertical"/>
          <Text keyboard>Ctrl + Shift + F3</Text> {l('shortcut.key.matchPrevious')} <Divider type="vertical"/>
          <Text keyboard>F7</Text> {l('shortcut.key.goNextHighlight')} <Divider type="vertical"/>
          <Text keyboard>Shift +F7</Text> {l('shortcut.key.goPreviousHighlight')}
        </Paragraph>
        <Paragraph>
          <Text keyboard>Ctrl + Shift + End</Text> {l('shortcut.key.appendLineBefore')} <Divider type="vertical"/>
          <Text keyboard>Ctrl + End</Text> {l('shortcut.key.appendLine')} <Divider type="vertical"/>
          <Text keyboard>Alt + Up/Down</Text> {l('shortcut.key.transpositionUpAndDown')} <Divider type="vertical"/>
          <Text keyboard>Ctrl + Shift + [/]</Text> {l('shortcut.key.collapseOrExpand')}
        </Paragraph>
      </Typography>
    </Scrollbars>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  toolHeight: Studio.toolHeight,
}))(StudioHome);
