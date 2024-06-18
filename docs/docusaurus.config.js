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
    url: 'https://www.dinky.org.cn/', // 网站网址 | Your website's URL
    baseUrl: '/', // 站点的相对路径 可将其视为是主机名后的路径 | Path to your website
    staticDirectories: ['static/img', 'blog/blog_img'], // 静态文件目录 | Path to static files
    // trailingSlash: true, //此选项允许您自定义 URL/链接后是否添加结尾斜杠 | Whether to append a trailing slash to the URL when rendering URLs
    onBrokenLinks: 'ignore', // Docusaurus 在检测到无效链接时的行为 |  Docusaurus behavior when invalid links are detected    -> 类型：'ignore' | 'log' | 'warn' | 'error' | 'throw' |
    onBrokenMarkdownLinks: 'warn', // Docusaurus 在检测到无效 Markdown 链接时的行为 | Docusaurus behavior when detecting invalid markdown links  -> 类型：'ignore' | 'log' | 'warn' | 'error' | 'throw'
    onDuplicateRoutes: 'warn', // Docusaurus 在检测到重复的路由时的行为 |  Behavior of docusaurus when duplicate routes are detected  ->  类型：'ignore' | 'log' | 'warn' | 'error' | 'throw'
    favicon: 'dinky_logo.svg', // 左侧logo  | left logo
    organizationName: 'DataLinkDC', // 拥有此源的 GitHub 用户或组织。 用于部署命令。 |  The GitHub user or organization that owns this source. Command for deployment.
    projectName: 'dinky', // GitHub 源的名称。 用于部署命令。 | The name of the GitHub repository. Command for deployment.
    deploymentBranch: 'main', // GitHub Pages 的部署分支。 用于部署命令。 | The branch to deploy to GitHub Pages. Command for deployment.
    customFields: { // 自定义字段 | Custom fields
        structTitle: 'Dinky',
        structSubTitle: 'Dinky 为 Apache Flink 而生，让 Flink SQL 纵享丝滑',
        // structDescription: custom_fields.structDesc(),
        learningMore: 'https://space.bilibili.com/366484959/video',
        teaching: 'https://www.bilibili.com/video/BV1SX4y1Y7CB/?spm_id_from=333.337.search-card.all.click&vd_source=e806cc3d8e01f5e39a97787aca3fa3ae'
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
                    includeCurrentVersion: true,
                    lastVersion: '1.0',
                    versions: {
                        current: {
                            label: 'Dev',
                            path: '/next',
                            banner: 'unreleased',
                        },
                        '1.0': {
                            label: '1.0',
                            path: '/1.0',
                        },
                        0.7: {
                            label: '0.7',
                            path: '/0.7',
                            banner: 'unmaintained',
                        },
                        0.6: {
                            label: '0.6',
                            path: '/0.6',
                            banner: 'unmaintained',
                        },
                    },
                    sidebarPath: require.resolve('./sidebars.js'),
                    sidebarCollapsible: true,
                    // Please change this to your repo.
                    editUrl: ({locale, versionDocsDirPath, docPath}) => {
                        if (locale !== 'zh') {
                            return `https://github.com/DataLinkDC/dinky/tree/master/docs/i18n/${locale}/${docPath}`;
                        }
                        return `https://github.com/DataLinkDC/dinky/tree/master/docs/${versionDocsDirPath}/${docPath}`;
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
            // announcementBar: {
            //     id: 'announcementBar-2', // Increment on change
            //     content: `⭐️ &nbsp; If you like Dinky , give it a star on <a target="_blank" rel="noopener noreferrer" href="https://github.com/DataLinkDC/dinky">GitHub</a> . Domain name will be migrated soon , The new domain name is  <a target="_blank" rel="noopener noreferrer" href="https://www.dinky.org.cn">www.dinky.org.cn</a>`,
            //     backgroundColor: "#BBDFFF",
            //     isCloseable: false,
            // },
            hideOnScroll: false, // 滚动时是否隐藏 | Whether to hide the sidebar on scroll
            docs: {
                sidebar: {
                    hideable: true,// 是否隐藏侧边栏 | Whether to hide the sidebar in mobile
                    autoCollapseCategories: false, // 点击另一个分类时自动折叠当前分类 | Click another category will automatically collapse the current category

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
                        position: 'left',
                        label: 'home',
                        activeBaseRegex: `^/$`,
                    },
                    {
                        type: 'docsVersionDropdown',
                        position: 'left',
                        dropdownActiveClassDisabled: true,
                    },
                    {
                        to: '/download/download',
                        position: 'right',
                        label: 'download',
                        activeBaseRegex: `/download/`,
                    },
                    {
                        label: 'developer_guide',
                        to: '/docs/next/developer_guide/contribution/how_contribute',
                        position: 'right',
                        items: [
                            {
                                label: "how_contribute",
                                to: "/docs/next/developer_guide/contribution/how_contribute",
                            },
                            {
                                label: "local_debug",
                                to: "/docs/next/developer_guide/local_debug",
                            },
                        ],
                    },
                    // {
                    //     to: '/blog',
                    //     position: 'right',
                    //     label: 'bolg',
                    //     // activeBaseRegex: `/*/`,
                    // },
                    {
                        type: 'localeDropdown',
                        position: 'right',
                    },
                    {
                        href: 'https://github.com/DataLinkDC/dinky/issues/884',
                        label: 'faq',
                        position: 'right',
                    },
                    {
                        href: 'https://github.com/orgs/DataLinkDC/projects/1',
                        label: 'roadmap',
                        position: 'right',
                    },
                    {
                        href: 'https://github.com/DataLinkDC/dinky',
                        className: 'header-github-link',
                        position: 'right',
                        alt: 'Gitlab repository',
                    },
                ],
            },
            footer: {
                style: 'dark',
                links: [
                    {
                        title: 'docs',
                        items: [
                            {
                                label: 'doc_home',
                                to: '/docs/next/get_started/overview',
                            },
                            {
                                label: 'openapi',
                                to: '/docs/next/openapi/openapi_overview',
                            },
                        ],
                    },
                    {
                        title: 'community',
                        items: [
                            {
                                label: 'issue',
                                href: 'https://github.com/DataLinkDC/dinky/issues',
                            },
                            {
                                label: 'pr',
                                href: 'https://github.com/DataLinkDC/dinky/pulls',
                            },
                        ],
                    },
                    {
                        title: 'more',
                        items: [
                            {
                                label: 'github',
                                href: 'https://github.com/DataLinkDC/dinky',
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
                copyright: `Copyright © ${new Date().getFullYear()} Dinky, Inc. DataLinkDC.<br/>
                    <a href="https://beian.miit.gov.cn" target="_blank">鲁ICP备20001630号-3</a>`,
            },
            prism: {
                theme: lightCodeTheme,
                darkTheme: darkCodeTheme,
            },
        }),
    plugins: [
        './src/plugin/wwads-plugin.ts',
        'docusaurus-plugin-less',
        [
            '@docusaurus/plugin-content-docs',
            {
                id: 'download',
                path: 'download',
                routeBasePath: 'download',
                editUrl: ({locale, versionDocsDirPath, docPath}) => {
                    if (locale !== 'zh') {
                        return `https://github.com/DataLinkDC/dinky/tree/master/docs/i18n/${locale}/${docPath}`;
                    }
                    return `https://github.com/DataLinkDC/dinky/tree/master/docs/${versionDocsDirPath}/${docPath}`;
                },
                sidebarPath: require.resolve('./sidebars.js'),
            },
        ],
        [
            '@docusaurus/plugin-ideal-image',
            {
                quality: 70,
                max: 1030, // 最大缩放图片尺寸。
                options: {
                    // 图片加载失败时显示的图片
                    // 默认的图片加载方式
                    // 默认的图片加载方式
                    loadType: 'default',
                    // 图片加载失败时显示的图片
                    errorImg: 'Cannot load image',
                    // 图片加载失败时显示的图片
                    errorType: 'default',
                    // 图片加载失败时显示的图片
                    errorImgType: 'default',
                    // 图片加载失败时显示的图片
                },
                min: 640, // 最小缩放图片尺寸。 如果原始值比这还低，会使用原图尺寸。
                steps: 2, // 在 min 和 max 之间最多生成的图片数量（包含两端点）
                disableInDev: false,
            },
        ]
    ],
    scripts: [
        // 统计 pv
        {src: 'https://hm.baidu.com/hm.js?7f2b5e6f354b8ae1cdec43ba108936f7', async: true},
        {src: 'https://cdn.wwads.cn/js/makemoney.js', async: true},
    ]
};

module.exports = config;
