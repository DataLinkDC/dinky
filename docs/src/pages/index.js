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
import clsx from 'clsx';
import Layout from '@theme/Layout';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import styles from './index.module.css';
import HomepageFeatures from '@site/src/components/HomepageFeatures';


function HomepageHeader() {
    const {siteConfig} = useDocusaurusContext();
    return (
        <header className={clsx('hero hero--primary', styles.heroBanner)}>
            <div className={clsx("container")}>
                <div className={clsx("desc", styles.box_container)}>
                    <div className={clsx("desc_left", styles.box,styles.descLeft)}>
                        <h1 className="hero__title">{siteConfig.title}</h1>
                        <p className="hero__subtitle">{siteConfig.tagline}</p>
                        <p className="hero_desc"> &nbsp;&nbsp;&nbsp;&nbsp;
                            一站式 FlinkSQL & SQL DataOps，
                            基于 Apache Flink 二次开发，无侵入，开箱即用，
                            实时即未来，批流为一体。<br/><br/>
                        </p>
                        <div className={styles.buttons}>
                            <div className={styles.buttonLink}>
                            <Link
                                className="button button--secondary button--lg"
                                to="https://github.com/DataLinkDC/dinky">
                                GitHub
                            </Link>
                            </div>
                            <div className={styles.buttonLink}>
                            <Link
                                className="button button--secondary button--lg"
                                to="https://gitee.com/mirrors/Dlink">
                                Gitee
                            </Link>
                            </div>

                            <div className={styles.buttonLink}>
                               <Link
                                   className="button button--secondary button--lg"
                                   style={{backgroundColor: '#42b983'}}
                                   to="/docs/next/get_started/quick_experience">
                                   Quick Start
                               </Link>
                            </div>

                            <div className={styles.buttonLink}>
                                <Link
                                    className="button button--secondary button--lg"
                                    style={{backgroundColor: '#07f313'}}
                                    to="http://demo.dinky.org.cn:32451/#/">
                                    Demo 环境
                                </Link>
                            </div>

                            <div className={styles.buttonLink}>
                               <iframe style={{width:"100px",height:"30px"}}
                                    src="https://ghbtns.com/github-btn.html?user=DataLinkDC&amp;repo=dinky&amp;type=star&amp;count=true&amp;size=large"
                                    title="GitHub Stars"
                                />
                            </div>
                        </div>
                    </div>
                    <div className={clsx("desc_right", styles.box,styles.descRight)}>
                        <div>
                           <img src="home.png" className="fly_svg"></img>
                        </div>
                    </div>
                </div>
            </div>
        </header>
    );
}

// structure wrapper
function Structure() {
    const {siteConfig} = useDocusaurusContext();
    return (
        <div className={clsx("structure", styles.structure,styles.box_container)}>
            <div className={clsx("image", styles.box)}><img
                src="http://pic.dinky.org.cn/dinky/docs/zh-CN/concept_architecture/architecture/dinky.png"
                alt="structure"/></div>
            <div className={clsx("text", styles.box,styles.structureText)}>
                <div className={clsx("title", styles.box,styles.structureText)}>
                    {siteConfig.customFields.structTitle}
                </div>
                <br/>
                {/*<div className="sub-title">*/}
                {/*    {siteConfig.customFields.structSubTitle}*/}
                {/*</div><br/>*/}
                <div className={clsx("description", styles.box,styles.structureDesc)}>
                    {/*{siteConfig.customFields.structDescription}*/}
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    Dinky 是一个开箱即用的一站式实时计算平台,以 Apache Flink 为基础，连接 OLAP 和数据湖等众多框架,致力于流批一体和湖仓一体的建设与实践。
                </div>
                <br/>
                <a href={siteConfig.customFields.learningMore} target="_blank" className="action-button">
                    学习更多 →
                </a>
                &nbsp;&nbsp;
                <a href={siteConfig.customFields.teaching} target="_blank" className="action-button">
                    尚硅谷教学 →
                </a>
            </div>
        </div>
    );
}


export default function Home() {
    const {siteConfig} = useDocusaurusContext();
    return (
        <Layout
            // title={`${siteConfig.title}`}
            description="为 Apache Flink 而生，让 Flink SQL 纵享丝滑 <head />">
            <HomepageHeader/>
            <Structure/>
            <main>
                <HomepageFeatures/>
            </main>
        </Layout>
    );
}
