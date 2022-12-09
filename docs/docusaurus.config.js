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

// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion
const path = require('path');
const versions = require('./versions.json');
const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
    title: 'Dinky',  //网站标题 | Site title
    tagline: 'Dinky 为 Apache Flink 而生，让 Flink SQL 纵享丝滑', // 网站标语 | Tagline for your website
    url: 'http://www.dlink.top/', // 网站网址 | Your website's URL
    baseUrl: '/', // 站点的相对路径 可将其视为是主机名后的路径 | Path to your website
    staticDirectories: ['static/img', 'blog/blog_img'], // 静态文件目录 | Path to static files
    // trailingSlash: true, //此选项允许您自定义 URL/链接后是否添加结尾斜杠 | Whether to append a trailing slash to the URL when rendering URLs
    onBrokenLinks: 'ignore', // Docusaurus 在检测到无效链接时的行为 |  Docusaurus behavior when invalid links are detected    -> 类型：'ignore' | 'log' | 'warn' | 'error' | 'throw' |
    onBrokenMarkdownLinks: 'warn', // Docusaurus 在检测到无效 Markdown 链接时的行为 | Docusaurus behavior when detecting invalid markdown links  -> 类型：'ignore' | 'log' | 'warn' | 'error' | 'throw'
    onDuplicateRoutes: 'warn', // Docusaurus 在检测到重复的路由时的行为 |  Behavior of docusaurus when duplicate routes are detected  ->  类型：'ignore' | 'log' | 'warn' | 'error' | 'throw'
    favicon: 'dinky_logo.svg', // 左侧logo  | left logo
    organizationName: 'DataLinkDC', // 拥有此源的 GitHub 用户或组织。 用于部署命令。 |  The GitHub user or organization that owns this source. Command for deployment.
    projectName: 'dlink', // GitHub 源的名称。 用于部署命令。 | The name of the GitHub repository. Command for deployment.
    deploymentBranch: 'main', // GitHub Pages 的部署分支。 用于部署命令。 | The branch to deploy to GitHub Pages. Command for deployment.
    customFields: { // 自定义字段 | Custom fields
        structTitle: 'Dinky',
        structSubTitle: 'Dinky 为 Apache Flink 而生，让 Flink SQL 纵享丝滑',
        // structDescription: custom_fields.structDesc(),
        learningMore: 'https://space.bilibili.com/366484959/video'
    },
    i18n: {
        defaultLocale: 'zh',
        locales: ['zh'],
        path: "i18n",
        localeConfigs: {
            'zh': {
                label: "中文",
                direction: 'ltr',
            },
            'en': {
                label: "English",
                direction: 'ltr',
            },
        },
    },
    themes: [
        [
            require.resolve("@easyops-cn/docusaurus-search-local"),
            /** @type {import("@easyops-cn/docusaurus-search-local").PluginOptions} */
            ({
                // config url is: https://github.com/easyops-cn/docusaurus-search-local#theme-options
                hashed: true,
                indexDocs: true,
                indexPages: true,
                highlightSearchTermsOnTargetPage: false, // Highlight search terms on target page.
                explicitSearchResultPath: true,
                searchBarPosition: "right",
                language: ["zh", "en"],
                hideSearchBarWithNoSearchContext: true,
            }),
        ],
    ],
    presets: [
        [
            '@docusaurus/preset-classic',
            /** @type {import('@docusaurus/preset-classic').Options} */
            ({
                docs: {
                    lastVersion: '0.7',
                    versions: {
                        current: {
                            label: 'Next',
                            path: '/next',
                        },
                        0.6: {
                            label: '0.6',
                            path: '/0.6',
                        },
                        0.7: {
                            label: '0.7',
                            path: '/0.7',
                        },
                    },
                    sidebarPath: require.resolve('./sidebars.js'),
                    sidebarCollapsible: true,
                    // Please change this to your repo.
                    editUrl: ({locale, versionDocsDirPath, docPath}) => {
                        if (locale !== 'zh') {
                            return `https://github.com/DataLinkDC/dlink/tree/master/docs/i18n/${locale}/${docPath}`;
                        }
                        return `https://github.com/DataLinkDC/dlink/tree/master/docs/${versionDocsDirPath}/${docPath}`;
                    },
                },
                blog: {
                    showReadingTime: true, // 展示阅读时间 | show read time
                    include: ['**/*.{md,mdx}'],
                    exclude: [
                        '**/_*.{js,jsx,ts,tsx,md,mdx}',
                        '**/_*/**',
                        '**/*.test.{js,jsx,ts,tsx}',
                        '**/__tests__/**',
                    ],
                    sortPosts: "descending", // 博客主页分页的排序规则(会根据时间排序) 降序: 'descending'  升序: 'ascending' | Governs the direction of blog post sorting.
                    postsPerPage: 20, // 博客主页的前{count}篇文章数 | the blog homepage show limit count
                    readingTime: ({content, frontMatter, defaultReadingTime}) =>
                        defaultReadingTime({content, options: {wordsPerMinute: 300}}), // 阅读时间 md文件中如果不写 date: 此属性 默认是当前时间
                },
                theme: {
                    customCss: require.resolve('./src/css/custom.css'), // 自定义css文件
                },
            }),
        ],
    ],

    themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
        ({
            announcementBar: {
                id: 'announcementBar-2', // Increment on change
                content: `⭐️ &nbsp; If you like Dinky , give it a star on <a target="_blank" rel="noopener noreferrer" href="https://github.com/DataLinkDC/dlink">GitHub</a> . Domain name will be migrated soon , The new domain name is  <a target="_blank" rel="noopener noreferrer" href="http://docs.dinky.org.cn">docs.dinky.org.cn</a>`,
                backgroundColor: "#BBDFFF",
                isCloseable: false,
            },
            hideOnScroll: false, // 滚动时是否隐藏 | Whether to hide the sidebar on scroll
            docs: {
                sidebar: {
                    hideable: true,// 是否隐藏侧边栏 | Whether to hide the sidebar in mobile
                    autoCollapseCategories: true, // 点击另一个分类时自动折叠当前分类 | Click another category will automatically collapse the current category

                }
            },
            navbar: {
                title: 'Dinky',
                logo: {
                    alt: 'Dinky',
                    src: 'side_dinky.svg',
                },
                items: [
                    {
                        to: '/',
                        position: 'right',
                        label: '主页',
                        title: '主页',
                        activeBaseRegex: `^/$`,
                    },
                    {
                        type: 'docsVersionDropdown',
                        position: 'right',
                        dropdownActiveClassDisabled: true,
                    },
                    {
                        to: '/download/download',
                        position: 'right',
                        label: '下载',
                        activeBaseRegex: `/download/`,
                    },
                    {
                        label: '开发者指南',
                        to: '/docs/next/developer_guide/contribution/how_contribute',
                        position: 'right',
                        items: [
                            {
                                label: "如何参与",
                                to: "/docs/next/developer_guide/contribution/how_contribute",
                            },
                            {
                                label: "本地调试",
                                to: "/docs/next/developer_guide/local_debug",
                            },
                            {
                                label: "远程调试",
                                to: "/docs/next/developer_guide/remote_debug",
                            },
                        ],
                    },
                    /*{
                        to: '/blog',
                        position: 'right',
                        label: '博客',
                        // activeBaseRegex: `/!*!/`,
                    },*/
                    {
                        type: 'localeDropdown',
                        position: 'right',
                    },
                    {
                        href: 'https://github.com/DataLinkDC/dlink/issues/884',
                        label: 'FAQ',
                        position: 'right',
                    },
                    {
                        href: 'https://github.com/orgs/DataLinkDC/projects/1',
                        label: 'Roadmap',
                        position: 'right',
                    },
                    {
                        href: 'https://github.com/DataLinkDC/dlink',
                        label: 'GitHub',
                        className: 'header-github-link',
                        position: 'right',
                    },
                ],
            },
            footer: {
                style: 'dark',
                links: [
                    {
                        title: 'Docs',
                        items: [
                            {
                                label: '文档首页',
                                to: '/docs/next/intro',
                            },
                            {
                                label: 'Open Api',
                                to: '/docs/next/administrator_guide/studio/openapi',
                            },
                        ],
                    },
                    {
                        title: 'Community',
                        items: [
                            {
                                label: 'Discussions',
                                href: 'https://github.com/DataLinkDC/dlink/discussions',
                            },
                            {
                                label: 'Issue',
                                href: 'https://github.com/DataLinkDC/dlink/issues',
                            },
                            {
                                label: 'Pull Request',
                                href: 'https://github.com/DataLinkDC/dlink/pulls',
                            },
                        ],
                    },
                    {
                        title: 'More',
                        items: [
                            {
                                label: 'GitHub',
                                href: 'https://github.com/DataLinkDC/dlink',
                            }
                        ],
                    },
                ],
                logo: {
                    alt: 'Dinky',
                    src: 'side_dinky.svg',
                    width: 100,
                    height: 30,
                },
                copyright: `Copyright © ${new Date().getFullYear()} Dinky, Inc. DataLinkDC.`,
            },
            prism: {
                theme: lightCodeTheme,
                darkTheme: darkCodeTheme,
            },
        }),
    plugins: [
        'docusaurus-plugin-less',
        [
            '@docusaurus/plugin-content-docs',
            {
                id: 'download',
                path: 'download',
                routeBasePath: 'download',
                editUrl: ({locale, versionDocsDirPath, docPath}) => {
                    if (locale !== 'zh') {
                        return `https://github.com/DataLinkDC/dlink/tree/master/docs/i18n/${locale}/${docPath}`;
                    }
                    return `https://github.com/DataLinkDC/dlink/tree/master/docs/${versionDocsDirPath}/${docPath}`;
                },
                sidebarPath: require.resolve('./sidebars.js'),
            },
        ],
        [
            '@docusaurus/plugin-ideal-image',
            {
                quality: 70,
                max: 1030, // 最大缩放图片尺寸。
                min: 640, // 最小缩放图片尺寸。 如果原始值比这还低，会使用原图尺寸。
                steps: 2, // 在 min 和 max 之间最多生成的图片数量（包含两端点）
                disableInDev: false,
            },
        ],
    ]
};

module.exports = config;
