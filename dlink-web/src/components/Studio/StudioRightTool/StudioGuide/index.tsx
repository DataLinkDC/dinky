import {Typography,Divider} from 'antd';
import React from 'react';
import {connect} from 'umi';
import {StateType} from '@/pages/FlinkSqlStudio/model';
import {Scrollbars} from 'react-custom-scrollbars';
import { history } from 'umi';

const {Title, Paragraph,Link, Text} = Typography;

const StudioGuide = (props: any) => {
  const {toolHeight} = props;

  return (
    <Scrollbars style={{height: toolHeight}}>
      <Typography style={{padding:'15px'}}>
        <Title level={5}>快捷引导</Title>
        <Paragraph>
          <ul>
            <li>
              <Link onClick={()=>{history.push('/registration/cluster/clusterInstance')}}>注册集群实例</Link>
            </li>
            <li>
              <Link onClick={()=>{history.push('/registration/cluster/clusterConfiguration')}}>注册集群配置</Link>
            </li>
            <li>
              <Link onClick={()=>{history.push('/registration/jar')}}>注册 Jar</Link>
            </li>
            <li>
              <Link onClick={()=>{history.push('/registration/database')}}>注册数据源</Link>
            </li>
            <li>
              <Link onClick={()=>{history.push('/registration/document')}}>注册文档</Link>
            </li>
            <li>
              <Link onClick={()=>{history.push('/settings')}}>修改系统配置</Link>
            </li>
            <li>
              <Link href="http://www.dlink.top/" target="_blank" >官网文档</Link>
            </li>
            <li>
              <Link href="https://github.com/DataLinkDC/dlink" target="_blank" >Github</Link>
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
