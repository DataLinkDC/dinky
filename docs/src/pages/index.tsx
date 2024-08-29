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

import React from 'react';
import Layout from '@theme/Layout';
import './index.less';
import styles from "@site/src/pages/styles.module.css";
import clsx from "clsx";
import CarouselList from "@site/src/components/CarouselList";

// 轮播图数据
const info = [
    {
        id: 1,
        image: 'https://pic.dinky.org.cn/dinky/docs/zh-CN/home/datastudio.png',
    },
    {
        id: 2,
        image: 'https://pic.dinky.org.cn/dinky/docs/zh-CN/home/checksql.png',
    },
    {
        id: 3,
        image: 'https://pic.dinky.org.cn/dinky/docs/zh-CN/home/versiondiff.png',
    },
    {
        id: 4,
        image: 'https://pic.dinky.org.cn/dinky/docs/zh-CN/home/lineage.png',
    },
    {
        id: 5,
        image: 'https://pic.dinky.org.cn/dinky/docs/zh-CN/home/monitor.png',
    },
];
const FeatureList = [
    {
        title: '快速开发',
        Svg: require('@site/static/img/one_stop.svg').default,
        description: (
            <>
                提供 FlinkSQL Studio，通过提示补全、逻辑检查、即席查询、全局变量、元数据查询等能力提升 Flink 作业开发效率
            </>
        ),
    },
    {
        title: '开箱即用',
        Svg: require('@site/static/img/ease_of_use.svg').default,
        description: (
            <>
                屏蔽技术细节，实现 Flink 所有作业提交方式，自动托管任务监控、保存点、报警等
            </>
        ),
    },
    {
        title: '语法增强',
        Svg: require('@site/static/img/easy_of_deploy.svg').default,
        description: (
            <>
                扩展 FlinkSQL 语法，如全局变量、CDC 整库同步、打印表、执行 Jar 任务等
            </>
        ),
    },
    {
        title: '易于扩展',
        Svg: require('@site/static/img/easy_of_extend.svg').default,
        description: (
            <>
                多种设计模式支持快速扩展新功能，如数据源、报警方式、
                CDC 整库同步、自定义语法等
            </>
        ),
    },
    {
        title: '无侵入性',
        Svg: require('@site/static/img/no_invasion.svg').default,
        description: (
            <>
                Spring Boot 轻应用快速部署，不需要在 Flink 集群修改源码或添加额外插件，即装即用
            </>
        ),
    },
    {
        title: '企业推动',
        Svg: require('@site/static/img/immersive.svg').default,
        description: (
            <>
                已有百家企业在生产环境中使用，进行实时数据开发与作业托管，大量用户实践保障项目日渐成熟
            </>
        ),
    },
];

const HeaderGraph = () => {
    return (
        <div className="container">
            <div className="row row-grid align-items-center">
                <div className="col-12 col-md-5 col-lg-6 order-md-2 text-center">
                    <figure className="w-100">
                        {/*<img alt="Image placeholder" src="https://pic.dinky.org.cn/dinky/docs/zh-CN/home/datastudio.png"*/}
                        {/*     className="img-fluid mw-md-120"/>*/}
                        <CarouselList items={info} className="img-fluid mw-md-120"/>
                    </figure>
                </div>
                <div className="col-12 col-md-7 col-lg-6 order-md-1 pr-md-5">
                    <h1 className="display-4 text-center text-md-left mb-3">
                        <strong className="text-primary">Dinky<br/></strong>让Flink作业纵享丝滑
                    </h1>
                    <p className="lead text-center text-md-left text-muted">
                        为 Apache Flink 深度定制的新一代实时计算平台，提供敏捷的 Flink SQL, Flink Jar
                        作业开发、部署及监控能力，助力实时计算高效应用。
                    </p>
                    <div className="text-center text-md-left mt-5">
                        <a href="/docs/next/get_started/quick_experience"
                           className="btn btn-primary btn-icon">
                            <span className="btn-inner--text">快速开始</span>
                            {/*<span className="btn-inner--icon"><i data-feather="chevron-right"></i></span>*/}
                        </a>
                        <a href="https://github.com/DataLinkDC/Dinky"
                           className="btn btn-neutral btn-icon d-none d-lg-inline-block" target="_blank">Github</a>
                    </div>
                </div>
            </div>
        </div>
    )
}
const GithubBanner = () => {
    return (
        <div>
            <ul className="github-banner">
                <li><strong>3.0k+</strong> Github stars</li>
                <li><strong>1111</strong> Github forks</li>
                <li><strong>10k+</strong> Total downloads</li>
            </ul>
        </div>
    )
}

function Feature({Svg, title, description}) {
    return (
        <div className={clsx('col col--4')}>
            <div className="text--center">
                <Svg className={styles.featureSvg} role="img"/>
            </div>
            <div className="text--center padding-horiz--md">
                <h3>{title}</h3>
                <p>{description}</p>
            </div>
        </div>
    );
}

export default function Home() {
    return (
        <Layout>
            <section className="slice py-7">
                {HeaderGraph()}
            </section>
            <section>
            </section>
            <section className="slice slice-lg pt-lg-6 pb-0 pb-lg-6 bg-section-secondary">
                <div className="container">
                    <div className="row mb-5 justify-content-center text-center">
                        <div className="col-lg-6">
                            <h2 className=" mt-4">什么是Dinky？</h2>
                            <div className="mt-2">
                                <p className="lead lh-180">Dinky 是一个开箱即用的一站式实时计算平台，以 Apache Flink 为基础，连接
                                    OLAP 和数据湖等众多框架,致力于流批一体和湖仓一体的建设与实践。</p>
                            </div>
                        </div>

                    </div>
                    <div>
                        <img alt="Image placeholder"
                             src="http://pic.dinky.org.cn/dinky/docs/zh-CN/concept_architecture/architecture/dinky.png"
                             className="img-fluid shadow rounded"/>
                    </div>
                </div>
            </section>

            <section className="slice slice-lg">
                <div className="container">
                    <div className="py-6">
                        <div className="row row-grid justify-content-between align-items-center">
                            <div className="col-lg-5 order-lg-2">
                                <h5 className="h3">IDE式开发</h5>
                                <p className="lead my-4">
                                    Dinky 提供一个轻量级的 IDE 式开发环境，提供一站式开发能力，从语句编写、调试、提交 到
                                    监控、发布、丝滑流畅，解决sql作业文件多，管理困难，
                                    编写困难等问题，还支持智能代码提示，Env参数，全局变量等，让开发更简单，顺滑。
                                </p>
                                <a className="text-primary ">支持Flink Sql</a><br/>
                                <a className="text-primary ">支持Flink Jar</a><br/>
                                <a className="text-primary ">CDC整库同步</a><br/>
                                <a className="text-primary text-underline--dashed">了解更多</a><br/>
                            </div>
                            <div className="col-lg-6 order-lg-1">
                                <div className="card mb-0 mr-lg-5">
                                    <div className="card-body p-2">
                                        <img alt="Image placeholder"
                                             src="https://pic.dinky.org.cn/dinky/docs/zh-CN/home/datastudio.png"
                                             className="img-fluid shadow rounded"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="py-6">
                        <div className="row row-grid justify-content-between align-items-center">
                            <div className="col-lg-5">
                                <h5 className="h3">细化运维管理</h5>
                                <p className="lead my-4">
                                    Dinky无缝支持流批一体，Yarn，K8s，Standalone，任务提交管理全方位支持，运维中心对原有Flink
                                    webui进行增强，持久化监控，个性化告警规则配置，智能重启，停止与savepoint管理等。
                                </p>
                                <a className="text-primary ">rs文件系统拓展</a><br/>
                                <a className="text-primary ">UDF 管理</a><br/>
                                <a className="text-primary ">元数据管理</a><br/>
                                <a className="text-primary text-underline--dashed">了解更多</a><br/>

                            </div>
                            <div className="col-lg-6">
                                <div className="card mb-0 ml-lg-5">
                                    <div className="card-body p-2">
                                        <img alt="Image placeholder" src="https://img2.imgtp.com/2024/05/10/ioYLk3h1.png"
                                             className="img-fluid shadow rounded"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <section className="slice slice-lg pt-lg-6 pb-0 pb-lg-6 bg-section-secondary">
                <div className="container">
                    <h1 style={{textAlign: "center"}}> 核心特性 </h1><br/>
                    <div className="row">
                        {FeatureList.map((props, idx) => (
                            <Feature key={idx} {...props} />
                        ))}
                    </div>
                </div>
            </section>

        </Layout>
    );
}
