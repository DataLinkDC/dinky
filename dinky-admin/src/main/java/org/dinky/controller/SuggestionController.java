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

package org.dinky.controller;

import org.dinky.data.dto.SuggestionDTO;
import org.dinky.data.result.Result;
import org.dinky.data.vo.suggestion.SuggestionVO;
import org.dinky.service.SuggestionService;

import java.util.Set;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "Suggestion Controller")
@RequestMapping("/api/suggestion")
@SaCheckLogin
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService suggestionService;

    @PostMapping("/queryAllSuggestions")
    @ApiOperation(value = "QueryAllSuggestions", notes = "queryAllSuggestions")
    @ApiImplicitParam(name = "suggestionDTO", value = "suggestionDTO", required = true, dataType = "SuggestionDTO")
    public Result<Set<SuggestionVO>> queryAllSuggestions(@RequestBody SuggestionDTO suggestionDTO) {
        return Result.succeed(suggestionService.getSuggestions(suggestionDTO.isEnableSchemaSuggestion()));
    }

    @PostMapping("/queryAllSuggestionsByKeyWord")
    @ApiOperation(value = "QueryAllSuggestionsByKeyWord", notes = "queryAllSuggestionsByKeyWord")
    @ApiImplicitParam(name = "suggestionDTO", value = "suggestionDTO", required = true, dataType = "SuggestionDTO")
    public Result<Set<SuggestionVO>> queryAllSuggestionsByKeyWord(@RequestBody SuggestionDTO suggestionDTO) {
        return Result.succeed(suggestionService.getSuggestionsByKeyWord(
                suggestionDTO.isEnableSchemaSuggestion(), suggestionDTO.getKeyWord()));
    }

    @PostMapping("/queryAllSuggestionsBySqlStatement")
    @ApiOperation(value = "QueryAllSuggestionsBySqlStatement", notes = "queryAllSuggestionsBySqlStatement")
    @ApiImplicitParam(name = "suggestionDTO", value = "suggestionDTO", required = true, dataType = "SuggestionDTO")
    public Result<Set<SuggestionVO>> queryAllSuggestionsBySqlStatement(@RequestBody SuggestionDTO suggestionDTO) {
        return Result.succeed(suggestionService.getSuggestionsBySqlStatement(
                suggestionDTO.isEnableSchemaSuggestion(),
                suggestionDTO.getSqlStatement(),
                suggestionDTO.getPosition()));
    }
}
