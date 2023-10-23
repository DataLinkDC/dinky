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

package org.dinky.data.enums;

/** 业务操作类型 */
public enum BusinessType {
    /** 其它 */
    OTHER,

    /** 新增 */
    INSERT,

    /** 新增或修改 */
    INSERT_OR_UPDATE,

    /** 修改 */
    UPDATE,

    /** 提交 */
    SUBMIT,

    /** 执行 */
    EXECUTE,

    /** 触发 */
    TRIGGER,

    /** query */
    QUERY,

    /** test */
    TEST,

    /** 删除 */
    DELETE,

    /** 授权 */
    GRANT,

    /** 导出 */
    EXPORT,

    /** 导入 */
    IMPORT,

    /** 上传 */
    UPLOAD,

    /** 下载 */
    DOWNLOAD,

    /** remote op */
    REMOTE_OPERATION,
}
