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

package com.dlink.service;

import com.dlink.common.result.Result;
import com.dlink.constant.UploadFileConstant;

import org.springframework.web.multipart.MultipartFile;

/**
 * File upload
 **/
public interface FileUploadService {

    /**
     * Upload one file, if target file exists, will delete it first
     *
     * @param file     {@link MultipartFile} instance
     * @param fileType Upload file's type, refer ${@link UploadFileConstant}
     * @return {@link com.dlink.common.result.Result}
     */
    Result upload(MultipartFile file, Byte fileType);

    /**
     * Upload multy file, if target file exists, will delete it first
     *
     * @param files    {@link MultipartFile} instance
     * @param fileType Upload file's type, refer ${@link UploadFileConstant}
     * @return {@link com.dlink.common.result.Result}
     */
    Result upload(MultipartFile[] files, Byte fileType);

    /**
     * Upload one file, if target file exists, will delete it first
     *
     * @param file     {@link MultipartFile} instance
     * @param dir      Local absolute dir
     * @param fileType Upload file's type, refer ${@link UploadFileConstant}
     * @return {@link com.dlink.common.result.Result}
     */
    Result upload(MultipartFile file, String dir, Byte fileType);

    /**
     * Upload multy file, if target file exists, will delete it first
     *
     * @param files    {@link MultipartFile} instance
     * @param dir      Local absolute dir
     * @param fileType Upload file's type, refer ${@link UploadFileConstant}
     * @return {@link com.dlink.common.result.Result}
     */
    Result upload(MultipartFile[] files, String dir, Byte fileType);

}
