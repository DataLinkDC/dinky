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

package com.dlink.service.impl;

import com.dlink.constant.DirConstant;
import com.dlink.model.FileNode;
import com.dlink.service.SystemService;
import com.dlink.utils.DirUtil;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * SystemServiceImpl
 *
 * @author wenmo
 * @since 2022/10/15 19:17
 */
@Service
public class SystemServiceImpl implements SystemService {

    @Override
    public List<FileNode> listDirByPath(String path) {
        return DirUtil.listDirByPath(path);
    }

    @Override
    public List<FileNode> listLogDir() {
        return DirUtil.listDirByPath(DirConstant.LOG_DIR_PATH);
    }

    @Override
    public String readFile(String path) {
        return DirUtil.readFile(path);
    }
}
