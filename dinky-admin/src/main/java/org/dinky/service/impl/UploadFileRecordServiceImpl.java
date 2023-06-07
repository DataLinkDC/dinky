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

import org.dinky.data.constant.UploadFileConstant;
import org.dinky.data.model.UploadFileRecord;
import org.dinky.mapper.UploadFileRecordMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.UploadFileRecordService;
import org.dinky.utils.FilePathUtil;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

/** UploadFileRecordServiceImpl */
@Service
public class UploadFileRecordServiceImpl
        extends SuperServiceImpl<UploadFileRecordMapper, UploadFileRecord>
        implements UploadFileRecordService {

    @Override
    public boolean saveOrUpdateFile(
            String fileName, String parentPath, String absolutePath, Byte fileType, Byte target) {
        UploadFileRecord updateWrapper = new UploadFileRecord();
        updateWrapper.setFileType(fileType);
        updateWrapper.setTarget(target);
        updateWrapper.setFileAbsolutePath(absolutePath);

        UploadFileRecord entity = new UploadFileRecord();
        entity.setFileType(fileType);
        entity.setTarget(target);
        entity.setName(UploadFileConstant.getDirName(fileType));
        entity.setIsFile(true);
        entity.setFileName(fileName);
        entity.setFileParentPath(FilePathUtil.removeFileSeparator(parentPath));
        entity.setFileAbsolutePath(absolutePath);

        return saveOrUpdate(entity, new UpdateWrapper<>(updateWrapper));
    }

    @Override
    public boolean saveOrUpdateDir(String parentPath, Byte fileType, Byte target) {
        UploadFileRecord updateWrapper = new UploadFileRecord();
        updateWrapper.setFileType(fileType);
        updateWrapper.setTarget(target);
        updateWrapper.setIsFile(false);

        UploadFileRecord entity = new UploadFileRecord();
        entity.setFileType(fileType);
        entity.setTarget(target);
        entity.setName(UploadFileConstant.getDirName(fileType));
        entity.setIsFile(false);
        entity.setFileParentPath(FilePathUtil.removeFileSeparator(parentPath));

        return saveOrUpdate(entity, new UpdateWrapper<>(updateWrapper));
    }
}
