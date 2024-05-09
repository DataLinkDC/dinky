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
import styles from './styles.module.css';

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

export default function HomepageFeatures() {
    return (
        <section className={styles.features}>
            <div className="container">
                <h1 style={{textAlign: "center"}}> Dinky Core Features </h1><br/>
                <div className="row">
                    {FeatureList.map((props, idx) => (
                        <Feature key={idx} {...props} />
                    ))}
                </div>
            </div>
        </section>
    );
}
