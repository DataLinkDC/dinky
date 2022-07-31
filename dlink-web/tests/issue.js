﻿/*
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


const Octokit = require('@octokit/core');

const octokit = new Octokit.Octokit({
  auth: process.env.GITHUB_TOKEN || process.env.GITHUB_AUTH,
});

const queryIssue = ({ title, id }) => {
  return octokit
    .request('GET /search/issues', {
      q: title,
      per_page: 5,
    })
    .then(({ data }) => {
      const list = data.items
        .map((item) => {
          return {
            title: item.title,
            url: item.html_url,
            id: item.id,
          };
        })
        .filter((item) => {
          return item.id !== id;
        });

      if (list.length > 0) {
        return `
> Issue Robot generation

### 以下的issue可能会帮助到你 ：

${list
  .map((item) => {
    return `* [${item.title}](${item.url})`;
  })
  .join('\n')}`;
      }
      return null;
    })
    .then(async (markdown) => {
      return markdown;
    });
};

const findIssue = async (issueId) => {
  const { data } = await octokit.request('GET /repos/{owner}/{repo}/issues/{issue_number}', {
    owner: 'ant-design',
    repo: 'ant-design-pro',
    issue_number: issueId,
  });
  return data;
};
const closeIssue = async (issueId) => {
  await octokit.request('PATCH /repos/{owner}/{repo}/issues/{issue_number}', {
    owner: 'ant-design',
    repo: 'ant-design-pro',
    issue_number: issueId,
    state: 'closed',
  });
};
const replyCommit = async (issueId, markdown) => {
  await octokit.request('POST /repos/{owner}/{repo}/issues/{issue_number}/comments', {
    owner: 'ant-design',
    repo: 'ant-design-pro',
    issue_number: issueId,
    body: markdown,
  });
};

const reply = async () => {
  const issueId = process.env.ISSUE_NUMBER;
  const issue = await findIssue(issueId);
  if (!issue.title || issue.title.length < 12) {
    replyCommit(issueId, '**请写标题！**');
    closeIssue(issueId);
    return;
  }
  const markdown = await queryIssue({
    title: issue.title,
    id: issue.id,
  });
  replyCommit(issueId, markdown);
};

reply();
