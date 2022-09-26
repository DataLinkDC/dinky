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
        title: '沉浸式',
        Svg: require('@site/static/img/immersive.svg').default,
        description: (
            <>
                提供专业的 DataStudio 功能，支持全屏开发、自动提示与补全、语法高亮、语句美化、语法校验、
                调试预览结果、全局变量、MetaStore、字段级血缘分析、元数据查询、FlinkSQL 生成等功能
            </>
        ),
    },
    {
        title: '易用性',
        Svg: require('@site/static/img/ease_of_use.svg').default,
        description: (
            <>
                Flink 多种执行模式无感知切换，支持 Flink 多版本切换，自动化托管实时任务、恢复点、报警等，
                自定义各种配置，持久化管理的 Flink Catalog
            </>
        ),
    },
    {
        title: '增强式',
        Svg: require('@site/static/img/easy_of_deploy.svg').default,
        description: (
            <>
                兼容且增强官方 FlinkSQL 语法，如 SQL 表值聚合函数、全局变量、CDC 整库同步、执行环境、
                语句合并、共享会话等
            </>
        ),
    },
    {
        title: '一站式',
        Svg: require('@site/static/img/one_stop.svg').default,
        description: (
            <>
                提供从 FlinkSQL 开发调试到上线下线的运维监控及 SQL 的查询执行能力，使数仓建设及数据治理
                一体化
            </>
        ),
    },
    {
        title: '易扩展',
        Svg: require('@site/static/img/easy_of_extend.svg').default,
        description: (
            <>
                源码采用 SPI 插件化及各种设计模式支持用户快速扩展新功能，如连接器、数据源、报警方式、
                Flink Catalog、CDC 整库同步、自定义 FlinkSQL 语法等
            </>
        ),
    },
    {
        title: '无侵入',
        Svg: require('@site/static/img/no_invasion.svg').default,
        description: (
            <>
                Spring Boot 轻应用快速部署，不需要在任何 Flink 集群修改源码或添加额外插件，无感知连接和
                监控 Flink 集群
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
