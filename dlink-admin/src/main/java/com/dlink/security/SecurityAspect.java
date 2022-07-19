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


package com.dlink.security;


import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.History;
import com.dlink.result.ExplainResult;
import com.dlink.result.SqlExplainResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
public class SecurityAspect {

    // 敏感信息的pattern :
    //  'password' = 'wwz@test'
    public static final String SENSITIVE = "'password'\\s+=\\s+'.+?'";

    // 敏感信息屏蔽码
    public static final String MASK = "'password'='******'";


    @AfterReturning(pointcut = "execution(* com.dlink.controller..*.*(..))", returning="returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue) {

        // mask sql for explain
        if (returnValue instanceof Result<?> && ((Result<?>) returnValue).getDatas() instanceof ExplainResult) {
            ExplainResult exp = ((ExplainResult) ((Result<?>) returnValue).getDatas());
            List<SqlExplainResult> sqlExplainResults = exp.getSqlExplainResults();
            if (CollectionUtils.isEmpty(sqlExplainResults)) {
                return;
            }
            for (SqlExplainResult explainResult : sqlExplainResults) {
                String sql = explainResult.getSql();
                explainResult.setSql(mask(sql, SENSITIVE, MASK));
            }
        }

        // mask statement for histories
        if (returnValue instanceof ProTableResult<?> && ((ProTableResult<?>) returnValue).getData() instanceof List<?>) {
            List<?> list = ((ProTableResult<?>) returnValue).getData();
            if (CollectionUtils.isEmpty(list) || !(list.get(0) instanceof History)) {
                return;
            }
            for (Object obj : list) {
                History history = (History) obj;
                String statement = history.getStatement();
                history.setStatement(mask(statement, SENSITIVE, MASK));
            }
        }

        // mask statement for history
        if (returnValue instanceof Result<?> && ((Result<?>) returnValue).getDatas() instanceof History) {
            History history = ((History) ((Result<?>) returnValue).getDatas());
            if (null != history) {
                String statement = history.getStatement();
                history.setStatement(mask(statement, SENSITIVE, MASK));
            }
        }
    }


    /**
     * 将info中的敏感信息中打码
     *
     * @param info              包含敏感信息的字符串
     * @param passwordPattern   敏感信息的regex
     * @param mask              屏蔽码
     * @return
     */
    public static String mask(String info, String passwordPattern, String mask) {
        if (null == info || null == passwordPattern || null == mask) {
            return info;
        }
        Pattern p = Pattern.compile(passwordPattern);
        Matcher m = p.matcher(info);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, mask);
        }
        m.appendTail(sb);

        return sb.toString();
    }

}
