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

import org.dinky.data.model.Statement;
import org.dinky.explainer.watchTable.WatchStatementExplainer;
import org.dinky.mapper.StatementMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.parser.SqlType;
import org.dinky.service.StatementService;
import org.dinky.trans.Operations;
import org.dinky.utils.SqlUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * StatementServiceImpl
 *
 * @since 2021/5/28 13:45
 */
@Service
public class StatementServiceImpl extends SuperServiceImpl<StatementMapper, Statement>
        implements StatementService {

    @Override
    public boolean insert(Statement statement) {
        return baseMapper.insert(statement) > 0;
    }

    @Override
    public List<String> getWatchTables(String statement) {
        // TODO: 2023/4/7 this function not support variable sql, because, JobManager and executor
        // couple function
        //  and status and task execute.
        final String[] statements = SqlUtil.getStatements(SqlUtil.removeNote(statement));
        return Arrays.stream(statements)
                .filter(t -> SqlType.WATCH.equals(Operations.getOperationType(t)))
                .flatMap(t -> Arrays.stream(WatchStatementExplainer.splitTableNames(t)))
                .collect(Collectors.toList());
    }
}
