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

package org.dinky.mybatis.util;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProTableUtilTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void ignoreUndefinedSortFieldWhenPaging() throws Exception {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(mapper.readTree("{\"sorter\":{\"undefined\":null}}"), wrapper);

        String sql = String.valueOf(wrapper.getSqlSegment());
        Assert.assertFalse(sql.toLowerCase().contains("undefined"));
        Assert.assertFalse(sql.toLowerCase().contains("order by"));
    }

    @Test
    public void keepValidColumnSort() throws Exception {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(mapper.readTree("{\"sorter\":{\"createTime\":\"descend\"}}"), wrapper);

        String sql = wrapper.getSqlSegment();
        Assert.assertTrue(sql.contains("create_time"));
        Assert.assertTrue(sql.toLowerCase().contains("desc"));
    }

    @Test
    public void keepValidColumnSortAscend() throws Exception {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(mapper.readTree("{\"sorter\":{\"name\":\"ascend\"}}"), wrapper);

        String sql = wrapper.getSqlSegment();
        Assert.assertTrue(sql.contains("a.name"));
        Assert.assertTrue(sql.toLowerCase().contains("asc"));
    }

    @Test
    public void ignoreMalformedSortField() throws Exception {
        String unusualField = "createTime,status";
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(mapper.readTree("{\"sorter\":{\"" + unusualField + "\":\"descend\"}}"), wrapper);

        String sql = String.valueOf(wrapper.getSqlSegment());
        Assert.assertFalse(sql.contains(unusualField));
        Assert.assertFalse(sql.toLowerCase().contains("order by"));
    }

    @Test
    public void ignoreUnexpectedSortDirection() throws Exception {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(mapper.readTree("{\"sorter\":{\"createTime\":\"desc nulls last\"}}"), wrapper);

        String sql = String.valueOf(wrapper.getSqlSegment());
        Assert.assertFalse(sql.toLowerCase().contains("nulls last"));
        Assert.assertFalse(sql.toLowerCase().contains("order by"));
    }

    @Test
    public void keepValidColumnFilter() throws Exception {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(mapper.readTree("{\"filter\":{\"enabled\":[1,0]}}"), wrapper);

        String sql = wrapper.getSqlSegment();
        Assert.assertTrue(sql.contains("a.enabled"));
        Assert.assertTrue(sql.contains("#{ew.paramNameValuePairs."));
        Assert.assertEquals(2, wrapper.getParamNameValuePairs().size());
        Assert.assertTrue(wrapper.getParamNameValuePairs().containsValue(1.0d));
        Assert.assertTrue(wrapper.getParamNameValuePairs().containsValue(0.0d));
    }

    @Test
    public void keepValidStringFilter() throws Exception {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(mapper.readTree("{\"filter\":{\"type\":[\"FUN_UDF\"]}}"), wrapper);

        String sql = wrapper.getSqlSegment();
        Assert.assertTrue(sql.contains("a.type"));
        Assert.assertTrue(sql.contains("#{ew.paramNameValuePairs."));
        Assert.assertFalse(sql.contains("FUN_UDF"));
        Assert.assertTrue(wrapper.getParamNameValuePairs().containsValue("FUN_UDF"));
    }

    @Test
    public void ignoreMalformedFilterField() throws Exception {
        String unusualField = "enabled or name";
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(mapper.readTree("{\"filter\":{\"" + unusualField + "\":[1]}}"), wrapper);

        String sql = String.valueOf(wrapper.getSqlSegment());
        Assert.assertFalse(sql.contains(unusualField));
        Assert.assertTrue(wrapper.getParamNameValuePairs() == null
                || wrapper.getParamNameValuePairs().isEmpty());
    }
}
