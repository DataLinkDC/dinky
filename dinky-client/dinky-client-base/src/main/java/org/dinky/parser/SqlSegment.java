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

package org.dinky.parser;

import org.dinky.assertion.Asserts;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

/**
 * SqlSegment
 *
 * @since 2021/6/14 16:12
 */
public class SqlSegment {

    private static final String Crlf = new String(new byte[] {1});

    @SuppressWarnings("unused")
    private static final String FourSpace = "　　";
    /** Sql语句片段类型，大写 */
    private String type;
    /** Sql语句片段开头部分 */
    private String start;
    /** Sql语句片段中间部分 */
    private String body;
    /** Sql语句片段结束部分 */
    private String end;
    /** 用于分割中间部分的正则表达式 */
    private String bodySplitPattern;
    /** 表示片段的正则表达式 */
    private String segmentRegExp;
    /** 分割后的Body小片段 */
    private List<String> bodyPieces;

    /**
     * 构造函数
     *
     * @param segmentRegExp 表示这个Sql片段的正则表达式
     * @param bodySplitPattern 用于分割body的正则表达式
     */
    public SqlSegment(String segmentRegExp, String bodySplitPattern) {
        this("", segmentRegExp, bodySplitPattern);
    }

    public SqlSegment(String type, String segmentRegExp, String bodySplitPattern) {
        this.type = type;
        this.start = "";
        this.body = "";
        this.end = "";
        this.segmentRegExp = segmentRegExp;
        this.bodySplitPattern = bodySplitPattern;
        this.bodyPieces = new ArrayList<String>();
    }

    /** 从sql中查找符合segmentRegExp的部分，并赋值到start,body,end等三个属性中 */
    public void parse(String sql) {
        Pattern pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            start = matcher.group(1);
            body = matcher.group(2);
            end = matcher.group(3);
            if (Asserts.isNullString(type)) {
                type = start.replace("\n", " ").replaceAll("\\s+", " ").toUpperCase();
            }
            parseBody();
        }
    }

    /** 解析body部分 */
    private void parseBody() {
        Pattern p = Pattern.compile(bodySplitPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(body.trim());
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, Crlf);
        }
        m.appendTail(sb);
        String[] arr = sb.toString().split("[" + Crlf + "]");
        bodyPieces = Lists.newArrayList(arr);
    }

    /** 取得解析好的Sql片段 */
    public String getParsedSqlSegment() {
        StringBuilder sb = new StringBuilder();
        sb.append(start).append(Crlf);
        for (String piece : bodyPieces) {
            sb.append(piece).append(Crlf);
        }
        return sb.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getBodySplitPattern() {
        return bodySplitPattern;
    }

    public void setBodySplitPattern(String bodySplitPattern) {
        this.bodySplitPattern = bodySplitPattern;
    }

    public String getSegmentRegExp() {
        return segmentRegExp;
    }

    public void setSegmentRegExp(String segmentRegExp) {
        this.segmentRegExp = segmentRegExp;
    }

    public List<String> getBodyPieces() {
        return bodyPieces;
    }

    public void setBodyPieces(List<String> bodyPieces) {
        this.bodyPieces = bodyPieces;
    }
}
