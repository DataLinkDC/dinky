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

package org.dinky.service.impl;

import org.dinky.constant.DirConstant;
import org.dinky.dto.TreeNodeDTO;
import org.dinky.service.SystemService;
import org.dinky.utils.DirUtil;
import org.dinky.utils.TreeUtil;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * SystemServiceImpl
 *
 * @since 2022/10/15 19:17
 */
@Service
public class SystemServiceImpl implements SystemService {

    @Override
    public List<TreeNodeDTO> listLogDir() {
        File systemLogFiles = TreeUtil.getFilesOfDir(DirConstant.LOG_DIR_PATH);
        return TreeUtil.treeNodeData(systemLogFiles, false);
    }

    @Override
    public String readFile(String path) {
        return DirUtil.readFile(path);
    }
}
