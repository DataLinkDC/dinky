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


package com.dlink.explainer.sqlLineage;

import java.util.*;

public class TreeNode<T> implements Iterable<TreeNode<T>> {

    /**
     * 树节点
     */
    public T data;

    /**
     * 父节点，根没有父节点
     */
    public TreeNode<T> parent;

    /**
     * 子节点，叶子节点没有子节点
     */
    public List<TreeNode<T>> children;

    /**
     * 保存了当前节点及其所有子节点，方便查询
     */
    private List<TreeNode<T>> elementsIndex;

    /**
     * 构造函数
     *
     * @param data
     */
    public TreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<TreeNode<T>>();
        this.elementsIndex = new LinkedList<TreeNode<T>>();
        this.elementsIndex.add(this);
    }

    public T getData() {
        return data;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    /**
     * 判断是否为根：根没有父节点
     *
     * @return
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 判断是否为叶子节点：子节点没有子节点
     *
     * @return
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * 添加一个子节点
     *
     * @param child
     * @return
     */
    public TreeNode<T> addChild(T child) {
        TreeNode<T> childNode = new TreeNode<T>(child);
        childNode.parent = this;
        this.children.add(childNode);
        this.registerChildForSearch(childNode);
        return childNode;
    }

    public TreeNode<T> addChild(TreeNode childNode) {
        childNode.parent = this;
        this.children.add(childNode);
        this.registerChildForSearch(childNode);
        return childNode;
    }

    /**
     * 获取当前节点的层
     *
     * @return
     */
    public int getLevel() {
        if (this.isRoot()) {
            return 0;
        } else {
            return parent.getLevel() + 1;
        }
    }

    /**
     * 递归为当前节点以及当前节点的所有父节点增加新的节点
     *
     * @param node
     */
    private void registerChildForSearch(TreeNode<T> node) {
        elementsIndex.add(node);

        if (parent != null) {
            parent.registerChildForSearch(node);
        }
    }

    /**
     * 从当前节点及其所有子节点中搜索某节点
     *
     * @param cmp
     * @return
     */
    public TreeNode<T> findTreeNode(Comparable<T> cmp) {
        for (TreeNode<T> element : this.elementsIndex) {
            T elData = element.data;

            if (cmp.compareTo(elData) == 0) {
                return element;
            }
        }

        return null;
    }

    public TreeNode<T> findChildNode(Comparable<T> cmp) {
        for (TreeNode<T> element : this.getChildren()) {
            T elData = element.data;

            if (cmp.compareTo(elData) == 0) {
                return element;
            }
        }

        return null;
    }

    /**
     * 获取当前节点的迭代器
     *
     * @return
     */
    public Iterator<TreeNode<T>> iterator() {
        TreeNodeIterator<T> iterator = new TreeNodeIterator<T>(this);
        return iterator;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "[tree data null]";
    }

    /**
     * 获取所有叶子节点的数据
     *
     * @return
     */
    public Set<TreeNode<T>> getAllLeafs() {
        Set<TreeNode<T>> leafNodes = new HashSet<TreeNode<T>>();

        if (this.children.isEmpty()) {
            leafNodes.add(this);
        } else {
            for (TreeNode<T> child : this.children) {
                leafNodes.addAll(child.getAllLeafs());
            }
        }

        return leafNodes;
    }

    /**
     * 获取所有叶子节点的数据
     *
     * @return
     */
    public Set<T> getAllLeafData() {
        Set<T> leafNodes = new HashSet<T>();

        if (this.children.isEmpty()) {
            leafNodes.add(this.data);
        } else {
            for (TreeNode<T> child : this.children) {
                leafNodes.addAll(child.getAllLeafData());
            }
        }

        return leafNodes;
    }
}
