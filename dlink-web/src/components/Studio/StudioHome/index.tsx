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


import {Typography,Divider} from 'antd';
import React from 'react';
import {connect} from 'umi';
import {StateType} from '@/pages/DataStudio/model';
import {Scrollbars} from 'react-custom-scrollbars';

const {Title, Paragraph, Text} = Typography;

const StudioHome = (props: any) => {
  const {toolHeight} = props;

  return (
    <Scrollbars style={{height: toolHeight}}>
      <Typography style={{padding:'15px'}}>
        <Title level={4}>欢迎使用 Dinky v0.6.6</Title>
        <Paragraph>
          <blockquote>实时即未来，Dinky 为 Apache Flink 而生，让 Flink SQL 纵享丝滑，并致力于实时计算平台建设。</blockquote>
        </Paragraph>
        <Title level={5}>快捷键</Title>
        <Paragraph>
          <Text keyboard>Ctrl + s</Text> 保存 <Divider type="vertical" />
          <Text keyboard>Alt + 2</Text> 校验 <Divider type="vertical" />
          <Text keyboard>Alt + 3</Text> 美化 <Divider type="vertical" />
          <Text keyboard>F2</Text> 全屏 <Divider type="vertical" />
          <Text keyboard>Esc</Text> 关闭弹框及全屏 <Divider type="vertical" />
          <Text keyboard>F1</Text> 更多快捷键
        </Paragraph>
        <Paragraph>
          <Text keyboard>Ctrl + F</Text> 搜索 <Divider type="vertical" />
          <Text keyboard>Ctrl + H</Text> 替换 <Divider type="vertical" />
          <Text keyboard>Ctrl + Z</Text> 撤销 <Divider type="vertical" />
          <Text keyboard>Ctrl + Y</Text> 重做 <Divider type="vertical" />
          <Text keyboard>Ctrl + /</Text> 注释
        </Paragraph>
        <Paragraph>
          <Text keyboard>选中 + Tab</Text> 缩进 <Divider type="vertical" />
          <Text keyboard>选中 + Shift + Tab</Text> 取消缩进 <Divider type="vertical" />
          <Text keyboard>Shift + Alt + Right</Text> 选中后续 <Divider type="vertical" />
          <Text keyboard>Shift + Alt + F</Text> 格式化
        </Paragraph>
        <Paragraph>
          <Text keyboard>Ctrl + Shift + Up/Down</Text> 编辑多行 <Divider type="vertical" />
          <Text keyboard>Shift + Alt + Up/Down</Text> 复制一行 <Divider type="vertical" />
          <Text keyboard>Ctrl + Shift + K</Text> 删除一行
        </Paragraph>
        <Paragraph>
          <Text keyboard>Ctrl + F3</Text> 匹配下一个 <Divider type="vertical" />
          <Text keyboard>Ctrl + Shift + F3</Text> 匹配上一个 <Divider type="vertical" />
          <Text keyboard>F7</Text> 前往下一个高亮 <Divider type="vertical" />
          <Text keyboard>Shift +F7</Text> 前往上一个高亮
        </Paragraph>
        <Paragraph>
          <Text keyboard>Ctrl + Shift + End</Text> 前面追加换行 <Divider type="vertical" />
          <Text keyboard>Ctrl + End</Text> 追加换行 <Divider type="vertical" />
          <Text keyboard>Alt + Up/Down</Text> 上下换位 <Divider type="vertical" />
          <Text keyboard>Ctrl + Shift + [/]</Text> 折叠/展开
        </Paragraph>
      </Typography>
    </Scrollbars>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  sql: Studio.sql,
  tabs: Studio.tabs,
  toolHeight: Studio.toolHeight,
}))(StudioHome);
