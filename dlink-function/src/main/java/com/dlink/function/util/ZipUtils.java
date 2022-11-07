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

package com.dlink.function.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.hutool.core.compress.ZipWriter;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

/**
 * zip压缩包工具类
 *
 * @author ZackYoung
 * @since 0.6.8
 */
public class ZipUtils extends ZipWriter {

    public ZipUtils(File zipFile, Charset charset) {
        super(zipFile, charset);
    }

    public ZipUtils(OutputStream out, Charset charset) {
        super(out, charset);
    }

    public ZipUtils(ZipOutputStream out) {
        super(out);
    }

    @Override
    public ZipWriter add(String[] paths, InputStream[] ins) throws IORuntimeException {
        if (ArrayUtil.isEmpty(paths) || ArrayUtil.isEmpty(ins)) {
            throw new IllegalArgumentException("Paths or ins is empty !");
        }
        if (paths.length != ins.length) {
            throw new IllegalArgumentException("Paths length is not equals to ins length !");
        }
        long maxTime = Stream.of(paths).map(FileUtil::file).mapToLong(File::lastModified).max().getAsLong();
        for (int i = 0; i < paths.length; i++) {
            add(paths[i], ins[i], maxTime);
        }

        return this;
    }

    public ZipWriter add(String path, InputStream in, long fileTime) throws IORuntimeException {
        path = StrUtil.nullToEmpty(path);
        if (null == in) {
            // 空目录需要检查路径规范性，目录以"/"结尾
            path = StrUtil.addSuffixIfNot(path, StrUtil.SLASH);
            if (StrUtil.isBlank(path)) {
                return this;
            }
        }

        return putEntry(path, in, fileTime);
    }

    private ZipWriter putEntry(String path, InputStream in, long fileTime) throws IORuntimeException {
        try {
            ZipEntry zipEntry = new ZipEntry(path);
            zipEntry.setTime(fileTime);
            super.getOut().putNextEntry(zipEntry);
            if (null != in) {
                IoUtil.copy(in, super.getOut());
            }
            super.getOut().closeEntry();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            IoUtil.close(in);
        }

        IoUtil.flush(super.getOut());
        return this;
    }

}
