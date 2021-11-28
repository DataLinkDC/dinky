package com.dlink.db.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.CaseFormat;

import java.util.*;

/**
 * ProTableUtil
 *
 * @author wenmo
 * @since 2021/5/25
 **/
public class ProTableUtil {

    /**
     * @Author wenmo
     * @Description 自动装载表格分页排序参数
     * @Date 2021/5/18
     * @Param [para, wrapper, camelToUnderscore, isDelete]
     **/
    public static void autoQuery(JsonNode para, QueryWrapper<?> wrapper, boolean camelToUnderscore, boolean isDelete) {
        buildDelete(wrapper,camelToUnderscore,isDelete);
        JsonNode sortField = para.get("sorter");
        if(sortField!=null) {
            Iterator<Map.Entry<String, JsonNode>> fields = sortField.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                buildSort(entry.getKey(), entry.getValue().asText(), wrapper, camelToUnderscore);
            }
        }
        JsonNode filter = para.get("filter");
        if(filter!=null) {
            Iterator<Map.Entry<String, JsonNode>> fields2 = filter.fields();
            while (fields2.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields2.next();
                buildFilter(entry.getKey(), entry.getValue(), wrapper, camelToUnderscore);
            }
        }
    }

    private static void buildDelete( QueryWrapper<?> wrapper, boolean camelToUnderscore, boolean isDelete){
        if (isDelete) {
            if (camelToUnderscore) {
                wrapper.eq(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "is_delete"), 0);
            } else {
                wrapper.eq("is_delete", 0);
            }
        }
    }
    
    private static void buildSort(String sortField,String sortValue,QueryWrapper<?> wrapper, boolean camelToUnderscore){
        if (sortField != null && sortValue != null) {
            if (camelToUnderscore) {
                sortField = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sortField);
            }
            if (sortValue.equals("descend")) {
                if(!sortField.contains(".")) {
                    wrapper.orderByDesc("a." + sortField);
                }
            } else {
                if(!sortField.contains(".")) {
                    wrapper.orderByAsc("a." + sortField);
                }
            }
        }
    }

    private static void buildFilter(String searchField,JsonNode searchValue,QueryWrapper<?> wrapper, boolean camelToUnderscore){
        if (searchField != null && !searchField.equals("") && searchValue != null) {
            if (camelToUnderscore) {
                searchField = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, searchField);
            }
            final String field = searchField;
            List<String> searchValues = new ArrayList<>();
            String type ="String";
            if (searchValue.isArray()){
                for (final JsonNode objNode : searchValue){
                    if(objNode.getNodeType()==JsonNodeType.NUMBER){
                        type ="Number";
                    }
                    searchValues.add(objNode.asText());
                }
            }
            if(searchValues.size()>0) {
                if ("Number".equals(type)) {
                    wrapper.and(qw -> {
                        for (int i = 0; i < searchValues.size(); i++) {
                            Double itemField = Double.parseDouble(searchValues.get(i));
                            if (i > 0) {
                                qw.or();
                            }
                            qw.eq("a." + field, itemField);
                        }
                    });
                } else {
                    wrapper.and(qw -> {
                        for (int i = 0; i < searchValues.size(); i++) {
                            String itemField = searchValues.get(i);
                            if (i > 0) {
                                qw.or();
                            }
                            qw.eq("a." + field, itemField);
                        }
                    });
                }
            }
        }
    }
    /**
     * @return void
     * @Author wenmo
     * @Description 自动装载表单查询参数
     * @Date 2021/5/18
     * @Param [wrapper, para, blackarr, writearr, camelToUnderscore]
     **/
    public static void autoSetFromPara(QueryWrapper<?> wrapper, JsonNode para, String[] blackarr, String[] writearr, boolean camelToUnderscore) {
        List<String> blacklist = Arrays.asList(blackarr);
        List<String> writelist = Arrays.asList(writearr);
        if (para.isObject())
        {
            Iterator<Map.Entry<String, JsonNode>> it = para.fields();
            while (it.hasNext())
            {
                Map.Entry<String, JsonNode> entry = it.next();
                String mapKey = entry.getKey();
                if (blacklist.indexOf(mapKey) == -1 || writelist.indexOf(mapKey) > -1) {
                    if(entry.getValue().getNodeType()== JsonNodeType.NUMBER) {
                        Double mapValue = entry.getValue().asDouble();
                        if (mapValue != null) {
                            if (camelToUnderscore) {
                                wrapper.eq(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, mapKey), mapValue);
                            } else {
                                wrapper.eq(mapKey, mapValue);
                            }
                        }
                    }else{
                        String mapValue = entry.getValue().asText();
                        if (mapValue != null&&!"".equals(mapValue)) {
                            if (camelToUnderscore) {
                                wrapper.eq(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, mapKey), mapValue);
                            } else {
                                wrapper.eq(mapKey, mapValue);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @return void
     * @Author wenmo
     * @Description 默认表单黑白名单
     * @Date 2021/5/18
     * @Param [wrapper, para]
     **/
    public static void autoSetFromParaDefault(QueryWrapper<?> wrapper, JsonNode para) {
        final String[] blackarr = {"current", "pageSize", "sorter", "filter"};
        final String[] writearr = {};
        autoSetFromPara(wrapper, para, blackarr, writearr, true);
    }

    /**
     * @return void
     * @Author wenmo
     * @Description 默认表格参数
     * @Date 2021/5/18
     * @Param [para, wrapper]
     **/
    public static void autoQueryDefalut(JsonNode para, QueryWrapper<?> wrapper) {
        autoQuery(para, wrapper, true, false);
    }

    public static void autoQueryDefalut(JsonNode para, QueryWrapper<?> wrapper,boolean isDelete) {
        autoQuery(para, wrapper, true, isDelete);
    }

    /**
     * @return void
     * @Author wenmo
     * @Description protable默认调用方法
     * @Date 2021/5/18
     * @Param [para, wrapper]
     **/
    public static void autoQueryAndSetFormParaDefalut(JsonNode para, QueryWrapper<?> wrapper) {
        autoSetFromParaDefault(wrapper, para);
        autoQueryDefalut(para, wrapper);
    }

    public static void autoQueryAndSetFormParaCustom(JsonNode para, QueryWrapper<?> wrapper) {
        autoSetFromParaDefault(wrapper, para);
        autoQuery(para, wrapper, true, false);
    }
}
