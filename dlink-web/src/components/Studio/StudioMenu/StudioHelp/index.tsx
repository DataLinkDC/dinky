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


import {Typography} from "antd";
import {l} from "@/utils/intl";


const {Title, Paragraph, Link} = Typography;

const StudioMsg = () => {

  return (
    <Typography>
      <Title level={3}>欢迎大家加入 Dinky 的官方社区，共建共赢~ </Title>
      <Paragraph>
        <ul>
          <li>
            <Link href="https://github.com/DataLinkDC/dlink"
                  target="_blank">GitHub：https://github.com/DataLinkDC/dlink</Link>
          </li>
          <li>
            <Link href="https://gitee.com/DataLinkDC/Dinky" target="_blank">Gitee:
              https://gitee.com/DataLinkDC/Dinky</Link>
          </li>
          <li>
            公众号：DataLink数据中台
          </li>
          <li>
            <Link href="http://www.dlink.top" target="_blank">官网文档：http://www.dlink.top</Link>
          </li>
          <li>
            <Link href="https://space.bilibili.com/366484959/video" target="_blank">B站视频：是文末呀</Link>
          </li>
          <li>
            微信用户社区群：推荐，添加微信号 wenmo_ai 邀请进群 （申请备注 Dinky + 企业名 + 职位，不写不批）
          </li>
          <li>
            QQ用户社区群：543709668 （申请备注 Dinky + 企业名 + 职位，不写不批）
          </li>
        </ul>
      </Paragraph>
      <Title level={4}>社区守则</Title>
      <Paragraph>
        <p>1.禁止发布或讨论与本群主旨无关或不良的内容，一经发现立马被踢。</p>
        <p>2.关于 Bug 反馈与功能改进或提议请通过 issue 进行，请阅读 issue 文档要求。</p>
        <p>3.部署和使用前请先仔细阅读 Readme、公众号文章、官网文档、B站视频。</p>
        <p>4.群提问题请礼貌并且说明【版本、执行模式、操作描述、截图】。</p>
        <p>5.Issue 登记谁在使用 Dlink，可进入企业用户群提供技术支持。</p>
      </Paragraph>
    </Typography>
  );
};

export default StudioMsg;
