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


import {Typography} from 'antd';
import React from 'react';
import {StateType} from '@/pages/DataStudio/model';
import {Scrollbars} from 'react-custom-scrollbars';
import {connect, history} from 'umi';
import {l} from "@/utils/intl";

const {Title, Paragraph, Link, Text} = Typography;

const StudioGuide = (props: any) => {

  const {toolHeight} = props;

  return (
    <Scrollbars style={{height: toolHeight}}>
      <Typography style={{padding: '15px'}}>
        <Title level={5}>{l('pages.datastudio.label.quickguide')}</Title>
        <Paragraph>
          <ul>
            <li>
              <Link onClick={() => {
                history.push('/registration/cluster/clusterInstance')
              }}>{l('pages.datastudio.label.registcluster')}</Link>
            </li>
            <li>
              <Link onClick={() => {
                history.push('/registration/cluster/clusterConfiguration')
              }}>{l('pages.datastudio.label.registclusterconfig')}</Link>
            </li>
            <li>
              <Link onClick={() => {
                history.push('/registration/jar')
              }}>{l('pages.datastudio.label.registjar')}</Link>
            </li>
            <li>
              <Link onClick={() => {
                history.push('/registration/database')
              }}>{l('pages.datastudio.label.registdatasource')}</Link>
            </li>
            <li>
              <Link onClick={() => {
                history.push('/datacenter/metadata')
              }}>{l('pages.datastudio.label.metadata')}</Link>
            </li>
            <li>
              <Link onClick={() => {
                history.push('/registration/alert/alertInstance')
              }}>{l('pages.datastudio.label.alarmInstance')}</Link>
            </li>
            <li>
              <Link onClick={() => {
                history.push('/registration/alert/alertGroup')
              }}>{l('pages.datastudio.label.alarmGroup')}</Link>
            </li>
            <li>
              <Link onClick={() => {
                history.push('/registration/fragment')
              }}>{l('pages.datastudio.label.val')}</Link>
            </li>
            <li>
              <Link onClick={() => {
                history.push('/registration/document')
              }}>{l('pages.datastudio.label.registdocument')}</Link>
            </li>
            <li>
              <Link onClick={() => {
                history.push('/settingCenter/flinkSettings')
              }}>{l('pages.datastudio.label.configsystemconfig')}</Link>
            </li>
            <li>
              <Link onClick={() => {
                history.push('/settingCenter/udfTemplate')
              }}>{l('pages.datastudio.label.udfTemplate')}</Link>
            </li>
            <li>
              <Link onClick={() => {
                history.push('/settingCenter/systemInfo')
              }}>{l('pages.datastudio.label.systemInfo')}</Link>
            </li>
            <li>
              <Link href="http://www.dlink.top/"
                    target="_blank">{l('pages.datastudio.label.officialdocumentation')}</Link>
            </li>
            <li>
              <Link href="https://github.com/DataLinkDC/dlink" target="_blank">Github</Link>
            </li>
          </ul>
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
}))(StudioGuide);
