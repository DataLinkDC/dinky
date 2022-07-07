package com.dlink.service.security;


import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.History;
import com.dlink.result.ExplainResult;
import com.dlink.result.SqlExplainResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
public class SecurityAspect {

    // 敏感信息的pattern :
    //  'password' = 'wwz@test'
    private static final String SENSITIVE = "'password'\\s+=\\s+'.+?'";

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
                explainResult.setSql(mask(sql));
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
                history.setStatement(mask(statement));
            }
        }

        // mask statement for history
        if (returnValue instanceof Result<?> && ((Result<?>) returnValue).getDatas() instanceof History) {
            History history = ((History) ((Result<?>) returnValue).getDatas());
            if (null != history) {
                String statement = history.getStatement();
                history.setStatement(mask(statement));
            }
        }
    }


    /**
     * 将info中的敏感信息中打码
     *
     * @param info
     * @return
     */
    public static String mask(String info) {
        Pattern p = Pattern.compile(SENSITIVE);
        Matcher m = p.matcher(info);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, MASK);
        }
        m.appendTail(sb);

        return sb.toString();
    }

}
