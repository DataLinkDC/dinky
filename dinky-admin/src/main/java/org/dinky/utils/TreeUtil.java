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

package org.dinky.utils;

import org.dinky.data.dto.TreeNodeDTO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.FileUtil;

/** the tree util , is use build tree , and show tree view */
public class TreeUtil {

    /**
     * build tree node data
     *
     * @param dir {@link File}
     * @return {@link List < GitProjectTreeNodeDTO >}
     */
    public static List<TreeNodeDTO> treeNodeData(File dir, Boolean readContent) {
        List<TreeNodeDTO> nodes = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {

            TreeNodeDTO rootNode =
                    new TreeNodeDTO(
                            dir.getName(), dir.getAbsolutePath(), true, new ArrayList<>(), 0L);
            buildTree(dir, rootNode, readContent);
            nodes.add(rootNode);
        }
        return nodes;
    }

    /**
     * build tree, if readContent is true , read file content to set {@link TreeNodeDTO#content}
     *
     * @param file {@link File}
     * @param parentNode {@link TreeNodeDTO}
     * @param readContent {@link Boolean}
     */
    private static void buildTree(File file, TreeNodeDTO parentNode, Boolean readContent) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    TreeNodeDTO childNode =
                            new TreeNodeDTO(
                                    child.getName(),
                                    child.getAbsolutePath(),
                                    child.isDirectory(),
                                    new ArrayList<>(),
                                    child.length());
                    parentNode.getChildren().add(childNode);
                    buildTree(child, childNode, readContent);
                }
            }
        } else {
            try {
                if (readContent) {
                    String content = readFiles(file);
                    parentNode.setContent(content);
                }
                parentNode.setLeaf(file.isDirectory());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * read file
     *
     * @param file {@link File}
     * @return {@link String}
     * @throws IOException
     */
    public static String readFiles(File file) throws IOException {
        return FileUtil.readUtf8String(file);
    }

    /**
     * get files of dir
     *
     * @param path {@link String}
     * @return {@File}
     */
    public static File getFilesOfDir(String path) {
        return FileUtil.file(path);
    }
}
