/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {buildSchemaTree} from "@/pages/RegCenter/DataSource/components/DataSourceDetail/function";
import React from "react";
import {Empty, Tree} from "antd";


const {DirectoryTree} = Tree;


/**
 * props
 */
type TreeProps = {
    treeData: Partial<any>[];
    onNodeClick: (info: any) => void
    style?: React.CSSProperties
}

const JobTree: React.FC<TreeProps> = (props) => {


    const {treeData, onNodeClick, style} = props;

    return <>
        {
            (treeData.length > 0) ?
                <DirectoryTree
                    style={style}
                    className={'treeList'}
                    onSelect={(_, info) => onNodeClick(info)}
                    treeData={buildSchemaTree(treeData)}
                /> : <Empty className={'code-content-empty'} description={"暂无作业,请点击左上角新建目录"}/>
        }
    </>;
};

export default JobTree;