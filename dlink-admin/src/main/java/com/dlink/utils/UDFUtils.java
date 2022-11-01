package com.dlink.utils;

import com.dlink.model.Task;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;
import com.dlink.service.TaskService;
import com.dlink.ud.data.model.UDF;
import com.dlink.ud.util.UDFUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.catalog.FunctionLanguage;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.extra.spring.SpringUtil;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public class UDFUtils extends UDFUtil {
    private static final String FUNCTION_SQL_REGEX = "create\\s+.*function\\s+(.*)\\s+as\\s+'(.*)'(\\s+language (.*))?;";

    public static List<UDF> getUDF(String statement) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("Parse UDF class name:");
        Pattern pattern = Pattern.compile(FUNCTION_SQL_REGEX, Pattern.CASE_INSENSITIVE);
        List<String> udfSqlList = ReUtil.findAllGroup0(pattern, statement);
        List<UDF> udfList = udfSqlList.stream().map(sql -> {
            List<String> groups = ReUtil.getAllGroups(pattern, sql);
            String udfName = groups.get(1);
            String className = groups.get(2);
            Task task = SpringUtil.getBean(TaskService.class).getUDFByClassName(className);
            String code = task.getStatement();
            return UDF.builder()
                .name(udfName)
                .className(className)
                .code(code)
                .functionLanguage(FunctionLanguage.valueOf(task.getDialect().toUpperCase()))
                .build();
        }).collect(Collectors.toList());
        List<String> classNameList = udfList.stream().map(UDF::getClassName).collect(Collectors.toList());
        process.info(StringUtils.join(",", classNameList));
        process.info(CharSequenceUtil.format("A total of {} UDF have been Parsed.", classNameList.size()));
        return udfList;
    }
}
