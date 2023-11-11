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

package org.dinky.data.vo.suggestion;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuggestionVO implements Serializable {

    private static final long serialVersionUID = 4631906403475634212L;

    private Object key;

    // label
    private SuggestionLabelVO label;
    // kind
    /**
     * 此字段值可选为:
     *  <p>
     *         Method = 0, // 方法
     *         Function = 1, // 函数
     *         Constructor = 2, // 构造函数
     *         Field = 3, // 字段
     *         Variable = 4, // 变量
     *         Class = 5, // 类
     *         Struct = 6,  // 结构体
     *         Interface = 7, // 接口
     *         Module = 8, // 模块
     *         Property = 9,  // 属性
     *         Event = 10,  // 事件
     *         Operator = 11, // 操作符
     *         Unit = 12, // 单元
     *         Value = 13, // 值
     *         Constant = 14,  // 常量
     *         Enum = 15,  // 枚举
     *         EnumMember = 16,  // 枚举成员
     *         Keyword = 17,    // 关键字
     *         Text = 18,  // 文本
     *         Color = 19,  // 颜色
     *         File = 20,   // 文件
     *         Reference =  21, // 引用
     *         Customcolor = 22, // 自定义颜色
     *         Folder = 23, // 文件夹
     *         TypeParameter = 24, // 类型参数
     *         User = 25, // 用户
     *         Issue = 26, // 问题
     *         Snippet = 27 // 代码片段
     */
    private int kind;
    // insertText
    private String insertText;
    // detail
    private String detail;
}
