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

import org.dinky.data.model.Document;
import org.dinky.data.model.FragmentVariable;
import org.dinky.data.vo.suggestion.SuggestionLabelVO;
import org.dinky.data.vo.suggestion.SuggestionVO;
import org.dinky.service.DocumentService;
import org.dinky.service.FragmentVariableService;
import org.dinky.service.SuggestionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuggestionServiceImpl implements SuggestionService {

    private final DocumentService documentService;
    private final FragmentVariableService fragmentVariableService;


    /**
     *  get suggestions for editor
     * @param enableSchemaSuggestion whether enable schema suggestion
     * @return suggestions list
     */
    @Override
    public List<SuggestionVO> getSuggestions(boolean enableSchemaSuggestion) {
        List<SuggestionVO> suggestionVOS = new ArrayList<>();
        // 1. 构建文档的建议列表
        buildDocumentSuggestions(documentService.list(), suggestionVOS);
        // 2. 全局变量的建议列表
        buildGlobalVariableSuggestions(fragmentVariableService.listEnabledAll(), suggestionVOS);
        // todo: 如果启用了schema，需要构建schema的建议列表
        // 3. schema的建议列表
        if (enableSchemaSuggestion) {
            buildSchemaSuggestions(Arrays.asList(), suggestionVOS);
        }
        // 4. 自定义关键词提示
        buildCustomSuggestions(new ArrayList<>(), suggestionVOS);

        return suggestionVOS;
    }

    /**
     * build global variable suggestions
     * @param fragmentVariableList fragment variable list
     * @param suggestionVOS suggestion list
     */
    private static void buildGlobalVariableSuggestions(
            List<FragmentVariable> fragmentVariableList, List<SuggestionVO> suggestionVOS) {
        fragmentVariableList.stream()
                .map(fragmentVariable -> {
                    SuggestionLabelVO suggestionLabelVO = SuggestionLabelVO.builder()
                            .label(fragmentVariable.getName())
                            .detail(fragmentVariable.getNote())
                            .description(fragmentVariable.getNote())
                            .build();
                    return SuggestionVO.builder()
                            .label(suggestionLabelVO)
                            .insertText(fragmentVariable.getFragmentValue())
                            .detail(fragmentVariable.getNote())
                            .build();
                })
                .forEach(suggestionVOS::add);
    }

    /**
     * build schema suggestions
     * @param buildingSchemaList schema list
     * @param suggestionVOS suggestion list
     */
    private static void buildSchemaSuggestions(List<Object> buildingSchemaList, List<SuggestionVO> suggestionVOS) {
        // todo: 构建schema的建议列表 , 包含 库名 、表名、字段名、.... , 能做到根据库名点出表名，根据表名点出字段名
    }

    /**
     * build custom suggestions
     * @param customKeyWordList custom keyword list
     * @param suggestionVOS suggestion list
     */
    private static void buildCustomSuggestions(List<Object> customKeyWordList, List<SuggestionVO> suggestionVOS) {
        // todo: 自定义关键词提示,
        //      1. 此处自定义是属于 dinky 内部自定义语法关键词提示, 如果有片段, 将片段的建议列表加入到文档中进行提示
        //      2. 可以加入 yml 语法的关键词提示 , 因为在集群配置中会有 yml 的配置文件写法 , 获取方式待定
    }

    /**
     * build document suggestions
     * @param documentList document list
     * @param suggestionVOS suggestion list
     */
    private static void buildDocumentSuggestions(List<Document> documentList, List<SuggestionVO> suggestionVOS) {
        documentList.stream()
                .map(document -> {
                    String detail =
                            document.getCategory() + " ->" + document.getType() + " -> " + document.getSubtype();
                    SuggestionLabelVO suggestionLabelVO = SuggestionLabelVO.builder()
                            .label(document.getName())
                            .detail(detail)
                            .description(document.getDescription())
                            .build();
                    return SuggestionVO.builder()
                            .label(suggestionLabelVO)
                            .insertText(document.getFillValue())
                            .kind(27)
                            .detail(detail)
                            .build();
                })
                .forEach(suggestionVOS::add);
    }

    /**
     *  by keyword get suggestions list
     * @param enableSchemaSuggestion  whether enable schema suggestion
     * @param keyWord  keyword
     * @return  suggestions list
     */
    @Override
    public List<SuggestionVO> getSuggestionsByKeyWord(boolean enableSchemaSuggestion, String keyWord) {
        return getSuggestions(enableSchemaSuggestion).stream()
                .filter(suggestionVO -> suggestionVO.getLabel().getLabel().contains(keyWord))
                .collect(Collectors.toList());
    }

    /**
     *  by sql statement get suggestions list
     * @param enableSchemaSuggestion     whether enable schema suggestion
     * @param sqlStatement  sql statement
     * @return  suggestions list
     */
    @Override
    public List<SuggestionVO> getSuggestionsBySqlStatement(
            boolean enableSchemaSuggestion, String sqlStatement, int position) {
        // todo: 根据传入的sql，获取建议列表, 需要和flink的sql解析器结合起来
        return Arrays.asList();
    }
}
