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

package org.dinky.data.model;

import org.dinky.mybatis.model.SuperEntity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** UploadFileRecord */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_upload_file_record")
public class UploadFileRecord extends SuperEntity {

    private static final long serialVersionUID = 3769285632787490408L;

    /**
     * File type id: hadoop-conf(1)、flink-conf(2)、flink-lib(3)、user-jar(4)、dinky-jar(5), -1
     * represent no file type.
     */
    private Byte fileType = -1;

    private String fileName;
    /** Where file upload to: local(1)、hdfs(2) */
    private Byte target;

    private String fileParentPath;
    private String fileAbsolutePath;
    private Boolean isFile = true;
}
